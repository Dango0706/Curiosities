package me.tuanzi.curiosities.util;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import org.slf4j.Logger;

/**
 * 调试日志工具类
 * 提供基于配置的调试日志输出功能
 * 只有在开发模式启用时才会输出调试日志，提供性能友好的调试机制
 */
public class DebugLogger {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 私有构造函数，防止实例化
     */
    private DebugLogger() {
        throw new UnsupportedOperationException("DebugLogger is a utility class and cannot be instantiated");
    }

    /**
     * 输出调试日志
     * 只有在开发模式启用时才会输出日志，性能友好
     *
     * @param message 日志消息
     */
    public static void debugLog(String message) {
        // 性能优化：先检查配置，避免不必要的字符串操作
        if (isDebugEnabled()) {
            LOGGER.info("[DEBUG] {}", message);
        }
    }

    /**
     * 输出格式化调试日志
     * 只有在开发模式启用时才会输出日志，性能友好
     * 支持SLF4J风格的占位符格式化
     *
     * @param message 日志消息模板，支持{}占位符
     * @param args    格式化参数
     */
    public static void debugLog(String message, Object... args) {
        // 性能优化：先检查配置，避免不必要的字符串格式化
        if (isDebugEnabled()) {
            LOGGER.info("[DEBUG] " + message, args);
        }
    }

    /**
     * 输出带异常的调试日志
     * 只有在开发模式启用时才会输出日志，性能友好
     *
     * @param message   日志消息
     * @param throwable 异常对象
     */
    public static void debugLog(String message, Throwable throwable) {
        if (isDebugEnabled()) {
            LOGGER.info("[DEBUG] {}", message, throwable);
        }
    }

    /**
     * 输出格式化的带异常调试日志
     * 只有在开发模式启用时才会输出日志，性能友好
     *
     * @param message   日志消息模板，支持{}占位符
     * @param throwable 异常对象
     * @param args      格式化参数
     */
    public static void debugLog(String message, Throwable throwable, Object... args) {
        if (isDebugEnabled()) {
            // 手动格式化消息，因为SLF4J的格式化不支持同时传递异常和参数
            String formattedMessage = formatMessage(message, args);
            LOGGER.info("[DEBUG] {}", formattedMessage, throwable);
        }
    }

    /**
     * 检查是否启用了调试模式
     * 包含安全检查，避免配置未加载时的异常
     *
     * @return 是否启用调试模式
     */
    private static boolean isDebugEnabled() {
        try {
            // 检查配置是否已加载
            if (!ModConfigManager.isConfigLoaded()) {
                return false;
            }
            // 返回开发模式配置值
            return ModConfigManager.DEVELOPMENT_MODE_ENABLED.get();
        } catch (Exception e) {
            // 如果获取配置时发生任何异常，默认不启用调试
            return false;
        }
    }

    /**
     * 手动格式化消息
     * 简单的{}占位符替换实现
     *
     * @param message 消息模板
     * @param args    参数数组
     * @return 格式化后的消息
     */
    private static String formatMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }

        String result = message;
        for (Object arg : args) {
            int index = result.indexOf("{}");
            if (index != -1) {
                String argStr = arg != null ? arg.toString() : "null";
                result = result.substring(0, index) + argStr + result.substring(index + 2);
            } else {
                break; // 没有更多占位符
            }
        }
        return result;
    }

    /**
     * 检查调试模式是否启用（公共方法）
     * 可用于在调用方进行性能优化检查
     *
     * @return 是否启用调试模式
     */
    public static boolean isDebugModeEnabled() {
        return isDebugEnabled();
    }

    /**
     * 输出调试信息日志（INFO级别）
     * 用于重要的调试信息
     *
     * @param message 日志消息
     * @param args    格式化参数
     */
    public static void debugInfo(String message, Object... args) {
        if (isDebugEnabled()) {
            LOGGER.info("[DEBUG-INFO] " + message, args);
        }
    }

    /**
     * 输出调试详细日志（DEBUG级别）
     * 用于详细的调试信息
     *
     * @param message 日志消息
     * @param args    格式化参数
     */
    public static void debugDetail(String message, Object... args) {
        if (isDebugEnabled()) {
            LOGGER.debug("[DEBUG-DETAIL] " + message, args);
        }
    }

    /**
     * 输出调试跟踪日志（TRACE级别）
     * 用于非常详细的跟踪信息
     *
     * @param message 日志消息
     * @param args    格式化参数
     */
    public static void debugTrace(String message, Object... args) {
        if (isDebugEnabled()) {
            LOGGER.trace("[DEBUG-TRACE] " + message, args);
        }
    }

    /**
     * 输出调试警告日志（WARN级别）
     * 用于警告级别的调试信息
     *
     * @param message 日志消息
     * @param args    格式化参数
     */
    public static void debugWarn(String message, Object... args) {
        if (isDebugEnabled()) {
            LOGGER.warn("[DEBUG-WARN] " + message, args);
        }
    }

    /**
     * 输出调试错误日志（ERROR级别）
     * 用于错误级别的调试信息
     *
     * @param message 日志消息
     * @param args    格式化参数
     */
    public static void debugError(String message, Object... args) {
        if (isDebugEnabled()) {
            LOGGER.error("[DEBUG-ERROR] " + message, args);
        }
    }

    /**
     * 输出调试错误日志（ERROR级别）带异常
     * 用于错误级别的调试信息
     *
     * @param message   日志消息
     * @param throwable 异常对象
     * @param args      格式化参数
     */
    public static void debugError(String message, Throwable throwable, Object... args) {
        if (isDebugEnabled()) {
            String formattedMessage = formatMessage(message, args);
            LOGGER.error("[DEBUG-ERROR] {}", formattedMessage, throwable);
        }
    }

    /**
     * 输出普通信息日志（INFO级别）
     * 用于重要的系统信息，不受开发模式控制
     *
     * @param message 日志消息
     * @param args    格式化参数
     */
    public static void info(String message, Object... args) {
        LOGGER.info(message, args);
    }

    /**
     * 输出警告日志（WARN级别）
     * 用于警告信息，不受开发模式控制
     *
     * @param message 日志消息
     * @param args    格式化参数
     */
    public static void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }

    /**
     * 输出错误日志（ERROR级别）
     * 用于错误信息，不受开发模式控制
     *
     * @param message 日志消息
     * @param args    格式化参数
     */
    public static void error(String message, Object... args) {
        LOGGER.error(message, args);
    }

    /**
     * 输出错误日志（ERROR级别）带异常
     * 用于错误信息，不受开发模式控制
     *
     * @param message   日志消息
     * @param throwable 异常对象
     */
    public static void error(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
    }
}
