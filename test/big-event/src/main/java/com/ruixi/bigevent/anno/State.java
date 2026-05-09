package com.ruixi.bigevent.anno;

import com.ruixi.bigevent.validation.StateValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(
        validatedBy = {StateValidation.class}//指定校验规则的类
)
@Target({ElementType.FIELD})//注解的作用域
@Retention(RetentionPolicy.RUNTIME)//注解的保留时间
public @interface State {
    //校验失败的错误信息
    String message() default "{state的参数只能是已发布或草稿}";
    //匹配的组
    Class<?>[] groups() default {};
    //负载，获取State注解的附加信息
    Class<? extends Payload>[] payload() default {};
}
