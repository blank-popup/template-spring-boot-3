package com.example.template.setting.logging;

import com.example.template.setting.mybatis.ProviderCurrentAhaUserDetails;
import com.example.template.setting.mybatis.ProviderUser;
import com.example.template.setting.body.WrapperHttpServletRequest;
import com.example.template.setting.security.AhaUserDetails;
import com.example.template.util.UtilSql;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSession;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class AspectLoggingRepository {
    private final SqlSession sqlSession;
    private ProviderUser<AhaUserDetails, Long> providerUser = ProviderCurrentAhaUserDetails.INSTANCE;

    @Pointcut("execution(* com.example.template..Repository*.*(..))")
    public void pointcutRepository() {}

    @Before("pointcutRepository()")
    public void beforeRepository(JoinPoint joinPoint) {
        if (isToLog(joinPoint) == false) {
            return;
        }
        log.info("======= Starting request : {} =======", joinPoint.getSignature().toShortString());
    }

    @AfterReturning(pointcut = "pointcutRepository()", returning = "returnValue")
    public void afterReturningRepository(JoinPoint joinPoint, Object returnValue) {
        if (isToLog(joinPoint) == false) {
            return;
        }
        logParameterRepository(joinPoint);
        log.debug(
                "\n=== {}({}) Repository-{} ====\n"
                        + "returnValue : {}\n"
                        + "==================================================\n",
                providerUser.getUser().getUsername(), providerUser.getId(), joinPoint.getSignature().toShortString(),
                returnValue
        );
    }

    @AfterThrowing(pointcut = "pointcutRepository()", throwing = "e")
    public void afterThrowingRepository(JoinPoint joinPoint, Exception e) {
        if (isToLog(joinPoint) == false) {
            return;
        }
        logParameterRepository(joinPoint);
        log.warn(
                "\n=== {}({}) Repository-{} ====\n"
                        + "Error Message : {}\n"
                        + "==================================================\n",
                providerUser.getUser().getUsername(), providerUser.getId(), joinPoint.getSignature().toShortString(),
                e.getMessage()
        );
    }

    private boolean isToLog(JoinPoint joinPoint) {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String uri = httpServletRequest.getRequestURI();
        boolean isDuringSignin = uri.contains("/auth/signin");
        if (isDuringSignin == true) {
            return true;
        }
        else {
            String nameRepository = joinPoint.getSignature().toShortString();
            boolean isAccessSelectUserDetailsByUsername = nameRepository.contains("RepositorySecurity.selectUserDetailsByUsername");
            return isAccessSelectUserDetailsByUsername == false;
        }
    }

    private String getRequestBody() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        WrapperHttpServletRequest cachingRequest = (WrapperHttpServletRequest) request;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] body = cachingRequest.getContentAsByteArray();
            return objectMapper.readTree(body).toString();
        } catch (IOException e) {
            return null;
        }
    }

    private String getParameter(JoinPoint joinPoint) {
        List<String> parameters = new ArrayList<>();
        for (int ii = 0; ii < joinPoint.getArgs().length; ++ii) {
            parameters.add(joinPoint.getArgs()[ii].toString());
        }
        if (parameters.isEmpty() == true) {
            return null;
        }
        else {
            return String.join(", ", parameters);
        }
    }

    private String getQuery(JoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        Class<?>[] interfaces = targetClass.getInterfaces();
        for (int ii = 0; ii < interfaces.length; ++ii) {
            if (interfaces[ii].isAnnotationPresent(Mapper.class) == true) {
                String query = UtilSql.showSql(sqlSession, interfaces[ii], joinPoint.getSignature().getName(), joinPoint.getArgs());
                return query;
            }
        }

        return null;
    }

    private void logParameterRepository(JoinPoint joinPoint) {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String requestBody = getRequestBody();
        String parameter = getParameter(joinPoint);
        String query = getQuery(joinPoint);
        log.debug(
                "\n=== {}({}) {} Repository-{} ====\n"
                        + "{} {}\n"
                        + "Query String : {}\n"
                        + "Request Body : {}\n"
                        + "Parameter : {}\n"
                        + "Query : {}\n"
                        + "==================================================\n",
                providerUser.getUser().getUsername(), providerUser.getId(), httpServletRequest.getRemoteAddr(), joinPoint.getSignature().toShortString(),
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(),
                httpServletRequest.getQueryString(),
                requestBody,
                parameter,
                query
        );
    }

//    @Pointcut("execution(* com.example.template..Controller*.*(..)) || execution(* com.example.template..ProviderJwt.getInformationOfJwt(..))")
////    @Pointcut("execution(* com.example.template..Controller*.*(..)) || execution(* com.example.template..Service*.*(..)) || execution(* com.example.template..ProviderJwt.getInformationOfJwt(..))")
////    @Pointcut("within(com.example.template..*)")
//    public void pointcutController() {}
//
//    @Before("pointcutController()")
//    public void beforeController(JoinPoint joinPoint) {
//        log.info("======= Starting request : {} =======", joinPoint.getSignature().toShortString());
//    }
//
//    @AfterReturning(pointcut = "pointcutController()", returning = "returnValue")
//    public void afterReturningController(JoinPoint joinPoint, Object returnValue) {
//        logParameterController(joinPoint);
//        log.info("==== [{}] : returnValue : {}", joinPoint.getSignature().toShortString(), returnValue == null ? "null" : returnValue.toString());
//        log.info("======= Ended request : {} =======", joinPoint.getSignature().toShortString());
//    }
//
//    @AfterThrowing(pointcut = "pointcutController()", throwing = "e")
//    public void afterThrowingController(JoinPoint joinPoint, Exception e) {
//        logParameterController(joinPoint);
//        log.error("==== [{}] : Error Message : {}", joinPoint.getSignature().toShortString(), e.getMessage());
//        log.error("======= Error occurred in request : {} =======", joinPoint.getSignature().toShortString());
//    }
//
//    private void logParameterController(JoinPoint joinPoint) {
//        for (int ii = 0; ii < joinPoint.getArgs().length; ++ii) {
//            log.info("==== [{}] : parameter[{}] : {}", joinPoint.getSignature().toShortString(), ii, joinPoint.getArgs()[ii].toString());
//        }
//    }
}
