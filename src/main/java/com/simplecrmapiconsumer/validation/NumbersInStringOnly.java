package com.simplecrmapiconsumer.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = NumbersInStringOnlyConstraintValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface NumbersInStringOnly {
	public String message() default "Only numbers are allowed";
	
	public Class<?>[] groups() default{};
	
	public Class<? extends Payload>[] payload() default {};
}
