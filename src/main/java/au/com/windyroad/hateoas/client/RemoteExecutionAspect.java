package au.com.windyroad.hateoas.client;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class RemoteExecutionAspect {

//    @Around("execution(* au.com.windyroad.servicegateway.model.IAdminRootController.*(..))")
    // @target(org.springframework.transaction.annotation.Transactional)
    public Object doConcurrentOperation(ProceedingJoinPoint pjp)
            throws Throwable {
        return pjp.proceed();
    }
}
