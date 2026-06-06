package com.movie.comment.aspect;

import com.movie.comment.common.ActionType;
import java.lang.annotation.*;

/**
 * 标记 Controller 方法，AOP 切面自动记录访问日志。
 *
 * <pre>
 * &#64;LogAction(ActionType.VIEW_MOVIE_DETAIL)              // 无 targetId
 * &#64;LogAction(value = ActionType.POST_REVIEW, targetParamName = "movieId")  // 从参数提取 targetId
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogAction {
    /** 动作类型 */
    ActionType value();

    /** 目标 ID 对应的参数名（空串表示无 targetId） */
    String targetParamName() default "";
}
