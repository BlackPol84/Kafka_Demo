package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.KafkaMetricProducer;

import java.util.Arrays;

@Async
@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class MetricAspect {

    @Value("${spring.metric.execution.time}")
    private long workingTime;

    @Value("${spring.kafka.topic.metric_trace}")
    private String metricTopic;

    private final KafkaMetricProducer producer;

    @Around("@annotation(ru.t1.java.demo.aop.Metric)")
    public Object measureWorkingTime(ProceedingJoinPoint pJoinPoint) {
        log.info("Вызов метода: {}", pJoinPoint.getSignature().toShortString());
        long beforeTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = pJoinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("Ошибка при выполнении метода: {}", throwable.getMessage(), throwable);
        }

        long executionTime = System.currentTimeMillis() - beforeTime;

        if(executionTime > workingTime) {
            String methodName = pJoinPoint.getSignature().getName();
            String methodParams = pJoinPoint.getArgs() != null ? Arrays.toString(pJoinPoint.getArgs()) : "[]";
            String message = String.format("Method %s executed in %d ms with params: %s",
                    methodName, executionTime, methodParams);

            producer.sendTo(metricTopic, message);
        }

        return result;
    }
}
