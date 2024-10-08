package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class ExceptionAspect {

    @Value("${spring.kafka.topic.error_trace}")
    private String metricTopic;

    private final KafkaTemplate<String, String> exceptionTemplate;

    @AfterThrowing(pointcut = "@annotation(LogException)", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        System.err.println("ASPECT EXCEPTION ANNOTATION: Logging exception: {}"
                + joinPoint.getSignature().getName());

        String methodName = joinPoint.getSignature().getName();
        String methodParams = joinPoint.getArgs() != null ? Arrays.toString(joinPoint.getArgs()) : "[]";
        String stackTraceString = Arrays.toString(ex.getStackTrace());
        String message = String.format("Method %s executed with params: %s. " +
                        "Exception: %s. Stack trace: %s",
                methodName, methodParams, ex.getMessage(), stackTraceString);

        exceptionTemplate.send(metricTopic, message);
    }
}
