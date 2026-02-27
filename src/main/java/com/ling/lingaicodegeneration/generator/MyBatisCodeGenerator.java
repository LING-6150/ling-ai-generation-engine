package com.ling.lingaicodegeneration.generator;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.spring.boot.MybatisFlexProperties;
import com.zaxxer.hikari.HikariDataSource;

import java.util.HashMap;
import java.util.Map;

public class MyBatisCodeGenerator {

    // 需要生成的表名
    private static final String[] TABLE_NAMES = {"chat_history"};

    public static void main(String[] args) {
        // 从配置文件中获取数据库连接信息
        Map<String, Object> dataSourceConfig = new HashMap<>();
        dataSourceConfig.put("url", "jdbc:mysql://localhost:3306/ling_ai_code_generation");
        dataSourceConfig.put("username", "root");
        dataSourceConfig.put("password", "LingMysql123!");

        // 配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dataSourceConfig.get("url").toString());
        dataSource.setUsername(dataSourceConfig.get("username").toString());
        dataSource.setPassword(dataSourceConfig.get("password").toString());

        // 创建配置内容
        GlobalConfig globalConfig = createGlobalConfig();

        // 通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        // 生成代码
        generator.generate();
    }

    public static GlobalConfig createGlobalConfig() {
        // 创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        // 设置根包，建议先生成到一个临时目录下，生成代码后，再移动到项目目录下
        globalConfig.getPackageConfig()
                .setBasePackage("com.ling.lingaicodegeneration.genresult");

        // 设置表前缀和只生成哪些表，未配置 setGenerateTable 时，生成所有表
        globalConfig.getStrategyConfig()
                .setGenerateTable(TABLE_NAMES)
                // 设置逻辑删除的默认字段名称
                .setLogicDeleteColumn("isDelete");

        // 设置生成 entity 并启用 Lombok
        globalConfig.enableEntity()
                .setWithLombok(true)
                .setJdkVersion(21);

        // 设置生成 mapper
        globalConfig.enableMapper();
        globalConfig.enableMapperXml();

        // 设置生成 service
        globalConfig.enableService();
        globalConfig.enableServiceImpl();

        // 设置生成 controller
        globalConfig.enableController();

        // 设置生成时间和字符串为空，避免多余代码改动
        globalConfig.getJavadocConfig()
                .setAuthor("")
                .setSince("");
        return globalConfig;
    }
}
