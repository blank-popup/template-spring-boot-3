package com.example.template.setting.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class AspectLog {
//    @Pointcut("execution(* com.example.template..Controller*.*(..))")
    @Pointcut("within(com.example.template..*)")
    public void controller() {}

    @Before("controller()")
    public void beforeRequest(JoinPoint joinPoint) {
        log.info("##### Start request : {}", joinPoint.getSignature().toShortString());
        log.info("Parameter : {}", joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "controller()", returning = "returnValue")
    public void afterReturningLogging(JoinPoint joinPoint, Object returnValue) {
        if (returnValue == null) {
            log.info("returnValue : NULL");
        }
        else {
            log.info("returnValue : {}", returnValue.toString());
        }
        log.info("##### End request : {}", joinPoint.getSignature().toShortString());
    }

    @AfterThrowing(pointcut = "controller()", throwing = "e")
    public void afterThrowingLogging(JoinPoint joinPoint, Exception e) {
        log.error("##### Occured error in request : {}", joinPoint.getSignature().toShortString());
        log.error("##### Error Message : {}", e.getMessage());
    }
}
