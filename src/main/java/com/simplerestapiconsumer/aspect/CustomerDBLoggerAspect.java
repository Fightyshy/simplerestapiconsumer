package com.simplerestapiconsumer.aspect;

import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simplerestapiconsumer.SimplerestapiconsumerApplication;
import com.simplerestapiconsumer.util.TokenParser;

@Aspect
@Component
public class CustomerDBLoggerAspect {
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(SimplerestapiconsumerApplication.class);
	
	@Autowired
	private TokenParser tokenParser;
	
	@Pointcut("target(com.simplerestapiconsumer.controller.CustomerConsumerController)")
	public void customerPointcut() {
		
	}
	//https://medium.com/@KosteRico/spring-aop-in-2021-level-up-your-logging-8d1498242ba2
	@Before("customerPointcut()")
	public void logEndpointSelected(JoinPoint joinPoint) {
		MethodSignature sig = (MethodSignature) joinPoint.getSignature();
		String token = (String) joinPoint.getArgs()[0];
		log.info(tokenParser.getUsernameFromToken(token)+" is executing Customer endpoint "+sig.getMethod().getName());
	}
	
	@After("customerPointcut()")
	public void logEndpoints(JoinPoint joinPoint) {
		
		//Method sig
		MethodSignature sig = (MethodSignature) joinPoint.getSignature();
		String token = (String) joinPoint.getArgs()[0];
		//Added javax.xml.bind dependency https://stackoverflow.com/questions/55606519/getting-exception-java-lang-noclassdeffounderror-could-not-initialize-class-jav
		log.info(tokenParser.getUsernameFromToken(token)+" executed Customer endpoint "+sig.getMethod().getName()+" with no issues at "+ LocalDateTime.now());
		
		
	}
}
