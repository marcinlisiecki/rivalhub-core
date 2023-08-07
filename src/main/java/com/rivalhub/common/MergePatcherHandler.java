package com.rivalhub.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.rivalhub.common.dto.ErrorMessageDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MergePatcherHandler {

    @ExceptionHandler({
            JsonPatchException.class,
            JsonProcessingException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageDto handlerResourceNotFound(Exception e) {
        return new ErrorMessageDto(ErrorMessages.SERVER_ERROR);
    }
}
