package com.ling.lingaicodegeneration.core.builder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Vue 项目构建器
 * 负责执行 npm install 和 npm run build，将 Vue 项目构建为静态文件
 */
@Slf4j
@Component
public class VueProjectBuilder {

    static final int SNIPPET_MAX_CHARS = 4_000;

    /**
     * 异步构建项目（不阻塞主流程）
     * 使用 Java 21 虚拟线程在单独的线程中执行构建，避免阻塞主流程
     */
    public void buildProjectAsync(String projectPath) {
        Thread.ofVirtual().name("vue-builder-" + System.currentTimeMillis()).start(() -> {
            try {
                buildProject(projectPath);
            } catch (Exception e) {
                log.error("异步构建 Vue 项目时发生异常: {}", e.getMessage(), e);
            }
        });
    }

    /**
     * 构建 Vue 项目，保留旧布尔接口以兼容现有调用方。
     */
    public boolean buildProject(String projectPath) {
        return buildProjectWithResult(projectPath).passed();
    }

    /**
     * 构建 Vue 项目，并返回稳定的构建结果。
     */
    public VueBuildResult buildProjectWithResult(String projectPath) {
        Instant startedAt = Instant.now();
        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();
        File projectDir = new File(projectPath);
        if (!projectDir.exists() || !projectDir.isDirectory()) {
            String message = "项目目录不存在: " + projectPath;
            log.error(message);
            error.append(message);
            return failed(startedAt, output, error);
        }
        File packageJson = new File(projectDir, "package.json");
        if (!packageJson.exists()) {
            String message = "package.json 文件不存在: " + packageJson.getAbsolutePath();
            log.error(message);
            error.append(message);
            return failed(startedAt, output, error);
        }
        log.info("开始构建 Vue 项目: {}", projectPath);
        CommandResult installResult = executeNpmInstall(projectDir);
        appendCommandResult(output, error, installResult);
        if (!installResult.success()) {
            log.error("npm install 执行失败");
            return failed(startedAt, output, error);
        }
        CommandResult buildResult = executeNpmBuild(projectDir);
        appendCommandResult(output, error, buildResult);
        if (!buildResult.success()) {
            log.error("npm run build 执行失败");
            return failed(startedAt, output, error);
        }
        File distDir = new File(projectDir, "dist");
        if (!distDir.exists()) {
            String message = "构建完成但 dist 目录未生成: " + distDir.getAbsolutePath();
            log.error(message);
            error.append(message);
            return failed(startedAt, output, error);
        }
        log.info("Vue 项目构建成功, dist 目录: {}", distDir.getAbsolutePath());
        return VueBuildResult.passed(elapsedMillis(startedAt), truncate(output.toString()), truncate(error.toString()));
    }

    /**
     * 执行 npm install 命令
     */
    private CommandResult executeNpmInstall(File projectDir) {
        log.info("执行 npm install...");
        return executeCommand(projectDir, npmCommand("install"), 300); // 5分钟超时
    }

    /**
     * 执行 npm run build 命令
     */
    private CommandResult executeNpmBuild(File projectDir) {
        log.info("执行 npm run build...");
        return executeCommand(projectDir, npmCommand("run", "build"), 180); // 3分钟超时
    }

    private List<String> npmCommand(String... args) {
        String executable = isWindows() ? "npm.cmd" : "npm";
        return java.util.stream.Stream.concat(java.util.stream.Stream.of(executable), java.util.stream.Stream.of(args))
                .toList();
    }

    /**
     * 检测是否是 Windows 系统
     */
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    /**
     * 执行命令
     */
    private CommandResult executeCommand(File workingDir, List<String> command, int timeoutSeconds) {
        try {
            log.info("在目录 {} 中执行命令: {}", workingDir.getAbsolutePath(), String.join(" ", command));
            Process process = new ProcessBuilder(command)
                    .directory(workingDir)
                    .start();
            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            ByteArrayOutputStream stderr = new ByteArrayOutputStream();
            Thread stdoutReader = copyAsync(process.getInputStream(), stdout);
            Thread stderrReader = copyAsync(process.getErrorStream(), stderr);
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                log.error("命令执行超时 ({}秒), 强制终止进程", timeoutSeconds);
                process.destroyForcibly();
                joinReader(stdoutReader);
                joinReader(stderrReader);
                return new CommandResult(false, -1, stdout.toString(StandardCharsets.UTF_8),
                        appendMessage(stderr.toString(StandardCharsets.UTF_8), "命令执行超时: " + String.join(" ", command)));
            }
            joinReader(stdoutReader);
            joinReader(stderrReader);
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                log.info("命令执行成功: {}", String.join(" ", command));
                return new CommandResult(true, exitCode, stdout.toString(StandardCharsets.UTF_8),
                        stderr.toString(StandardCharsets.UTF_8));
            }
            log.error("命令执行失败, 退出码: {}", exitCode);
            return new CommandResult(false, exitCode, stdout.toString(StandardCharsets.UTF_8),
                    appendMessage(stderr.toString(StandardCharsets.UTF_8), "命令退出码: " + exitCode));
        } catch (Exception e) {
            log.error("执行命令失败: {}, 错误信息: {}", String.join(" ", command), e.getMessage());
            return new CommandResult(false, -1, "", e.getMessage());
        }
    }

    private Thread copyAsync(InputStream inputStream, ByteArrayOutputStream outputStream) {
        Thread thread = Thread.ofVirtual().start(() -> {
            try (inputStream; outputStream) {
                inputStream.transferTo(outputStream);
            } catch (IOException e) {
                log.warn("读取构建命令输出失败: {}", e.getMessage());
            }
        });
        return thread;
    }

    private void joinReader(Thread reader) throws InterruptedException {
        reader.join(TimeUnit.SECONDS.toMillis(2));
    }

    private void appendCommandResult(StringBuilder output, StringBuilder error, CommandResult result) {
        appendSection(output, result.output());
        appendSection(error, result.error());
    }

    private void appendSection(StringBuilder builder, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (!builder.isEmpty()) {
            builder.append(System.lineSeparator());
        }
        builder.append(value.strip());
    }

    private VueBuildResult failed(Instant startedAt, StringBuilder output, StringBuilder error) {
        return VueBuildResult.failed(elapsedMillis(startedAt), truncate(output.toString()), truncate(error.toString()));
    }

    private long elapsedMillis(Instant startedAt) {
        return Math.max(0, Duration.between(startedAt, Instant.now()).toMillis());
    }

    static String truncate(String value) {
        if (value == null || value.length() <= SNIPPET_MAX_CHARS) {
            return value == null ? "" : value;
        }
        return value.substring(0, SNIPPET_MAX_CHARS) + "...[truncated]";
    }

    private String appendMessage(String value, String message) {
        if (value == null || value.isBlank()) {
            return message;
        }
        return value.strip() + System.lineSeparator() + message;
    }

    private record CommandResult(boolean success, int exitCode, String output, String error) {
    }
}
