package com.example.myfiles;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class HttpAspect {
//    private final static Logger logger = LoggerFactory.getLogger(HttpAspect.class);
//
//    @Pointcut("execution(public * com.example.myfiles.FileController.deleteFile(..))")
//    public void check(){}
//
//    @Before("check()")
//    public void doBefore(JoinPoint joinPoint) throws Exception{
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//        /*if(!request.getRemoteAddr().equals("36.23.97.169")){
//            logger.warn(request.getRemoteAddr());
//            throw new Exception("noDeletePermission");
//        }*/
//        //logger.info(request.getRemoteAddr());
//    }
}
