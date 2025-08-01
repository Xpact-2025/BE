package com.itstime.xpact.global.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class LogAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController com.itstime.xpact..*)")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object loggingAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        if (args.length > 0 && args[0] != null) {
            System.out.printf("[실행 API Controller] - %s.%s(%s)%n",
                    className, methodName, args.length > 0 ? args[0].toString() : "");
        } else {
            System.out.printf("[실행 API Controller] - %s.%s()%n",
                    className, methodName);
        }

        Object result = joinPoint.proceed();
        stopWatch.stop();
        long time = stopWatch.getLastTaskTimeMillis();

        System.out.printf("[API 종료] - %s.%s() - %d ms \n", className, methodName, time);
        return result;
    }
}
