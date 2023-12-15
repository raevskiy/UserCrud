package com.example.usercrud.exception;

import com.example.usercrud.exception.response.SimpleMessageResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler implements ProblemHandling {

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class})
    @ResponseBody
    public SimpleMessageResponse handleOptimisticLockingException(HttpServletResponse response, ObjectOptimisticLockingFailureException e) {
        log.warn("Optimistic locking was triggered", e);

        response.setContentType("application/json");
        response.setStatus(HttpStatus.CONFLICT.value());

        return new SimpleMessageResponse("The resource is updated on the server side. Get its latest version and retry your request.");
    }

}
