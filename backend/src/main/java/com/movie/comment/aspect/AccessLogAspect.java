package com.movie.comment.aspect;

import com.movie.comment.common.ActionType;
import com.movie.comment.entity.AccessLog;
import com.movie.comment.mapper.AccessLogMapper;
import com.movie.comment.security.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP 切面：自动记录标记了 {@link LogAction} 的方法调用。
 * 仅记录已登录用户的操作。
 */
@Aspect
@Component
public class AccessLogAspect {

    private final AccessLogMapper accessLogMapper;

    public AccessLogAspect(AccessLogMapper accessLogMapper) {
        this.accessLogMapper = accessLogMapper;
    }

    @Around("@annotation(logAction)")
    public Object around(ProceedingJoinPoint joinPoint, LogAction logAction) throws Throwable {
        // 仅记录已登录用户
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return joinPoint.proceed();
        }

        // SEARCH_MOVIE：只有带 keyword 搜索时才记
        ActionType action = logAction.value();
        if (action == ActionType.SEARCH_MOVIE) {
            if (!hasNonBlankKeyword(joinPoint)) {
                return joinPoint.proceed();
            }
        }

        // 先执行业务方法（业务失败抛异常则不进日志）
        Object result = joinPoint.proceed();

        // 提取 targetId
        Long targetId = extractTargetId(joinPoint, logAction.targetParamName());

        // 记录日志
        AccessLog log = new AccessLog();
        log.setUserId(userId);
        log.setUsername(UserContext.getUsername());
        log.setAction(action.getValue());
        log.setTargetId(targetId);

        HttpServletRequest request = getRequest();
        if (request != null) {
            log.setIp(request.getRemoteAddr());
            log.setUserAgent(request.getHeader("User-Agent"));
        }

        accessLogMapper.insert(log);
        return result;
    }

    /** 检查 listMovies 的 keyword 参数是否非空 */
    private boolean hasNonBlankKeyword(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < paramNames.length; i++) {
            if ("keyword".equals(paramNames[i]) && args[i] instanceof String kw) {
                return kw != null && !kw.isBlank();
            }
        }
        return false;
    }

    /** 从方法参数中按名称提取 Long 类型的 targetId */
    private Long extractTargetId(ProceedingJoinPoint joinPoint, String targetParamName) {
        if (targetParamName == null || targetParamName.isBlank()) {
            return null;
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < paramNames.length; i++) {
            if (targetParamName.equals(paramNames[i]) && args[i] instanceof Long val) {
                return val;
            }
        }
        return null;
    }

    private HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attrs.getRequest();
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
