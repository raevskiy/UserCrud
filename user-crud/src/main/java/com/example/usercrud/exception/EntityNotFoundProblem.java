package com.example.usercrud.exception;

import lombok.Getter;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

@Getter
public class EntityNotFoundProblem extends AbstractThrowableProblem {

    public EntityNotFoundProblem(String entity, String field, String value) {
        super(
                null,
                "Not Found",
                Status.NOT_FOUND,
                String.format("Entity %s where %s = %s not found", entity, field, value));
    }
}
