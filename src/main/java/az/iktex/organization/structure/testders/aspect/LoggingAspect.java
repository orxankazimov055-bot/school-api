package az.iktex.organization.structure.testders.aspect;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* az.iktex.organization.structure.testders.service..*(..))")
    public Object logServiceMethods(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {

        String methodName =
                joinPoint.getSignature().toShortString();

        String traceId = MDC.get("traceId");

        String username = MDC.get("username");

        if (username == null) {
            username = "anonymous";
        }

        long startTime = System.currentTimeMillis();

        log.info(
                "START | traceId={} | username={} | method={}",
                traceId,
                username,
                methodName
        );

        try {

            Object result = joinPoint.proceed();

            long duration =
                    System.currentTimeMillis() - startTime;

            log.info(
                    "SUCCESS | traceId={} | username={} | method={} | duration={}ms",
                    traceId,
                    username,
                    methodName,
                    duration
            );

            return result;

        } catch (Throwable ex) {

            long duration =
                    System.currentTimeMillis() - startTime;

            log.error(
                    "ERROR | traceId={} | username={} | method={} | duration={}ms | error={}",
                    traceId,
                    username,
                    methodName,
                    duration,
                    ex.getClass().getSimpleName()
            );

            throw ex;
        }
    }
}