package com.ling.lingaicodegeneration.exception;

import cn.hutool.json.JSONUtil;
import com.ling.lingaicodegeneration.common.BaseResponse;
import com.ling.lingaicodegeneration.common.ResultUtils;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Map;

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        // If this is an SSE request, write error in SSE format instead of JSON
        if (handleSseError(e.getCode(), e.getMessage())) {
            return null;
        }
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        if (handleSseError(ErrorCode.SYSTEM_ERROR.getCode(), "System error")) {
            return null;
        }
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }

    /**
     * Detect if the current request is an SSE request and write the error in SSE format.
     * Returns true if handled as SSE (caller should return null), false for regular HTTP.
     *
     * Why this is needed:
     * When an exception (e.g. rate limit) fires inside an SSE endpoint BEFORE the stream
     * starts, Spring cannot return a normal JSON error — the Content-Type is already
     * text/event-stream. Without this, the browser's EventSource gets a silent failure.
     * We manually write a "business-error" SSE event so the frontend can show a proper message.
     *
     * Uses a custom "business-error" event type (not the built-in "error") to avoid
     * conflicts with the browser's default EventSource error handling.
     */
    private boolean handleSseError(int errorCode, String errorMessage) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }

        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        if (response == null) {
            return false;
        }

        // Identify SSE requests by Accept header or URL path
        String accept = request.getHeader("Accept");
        String url = request.getRequestURI();
        boolean isSseRequest = (accept != null && accept.contains("text/event-stream"))
                || url.contains("/chat/gen/code");

        if (!isSseRequest) {
            return false;
        }

        try {
            // Set SSE response headers
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Connection", "keep-alive");

            // Build structured error payload
            Map<String, Object> errorData = Map.of(
                    "error", true,
                    "code", errorCode,
                    "message", errorMessage
            );
            String errorJson = JSONUtil.toJsonStr(errorData);

            // Write business-error event (custom type avoids browser's built-in error event)
            response.getWriter().write("event: business-error\ndata: " + errorJson + "\n\n");
            response.getWriter().flush();

            // Write done event to close the stream cleanly on the client side
            response.getWriter().write("event: done\ndata: {}\n\n");
            response.getWriter().flush();

            return true;
        } catch (IOException ioException) {
            log.error("Failed to write SSE error response", ioException);
            return true; // Still return true — it was an SSE request
        }
    }
}