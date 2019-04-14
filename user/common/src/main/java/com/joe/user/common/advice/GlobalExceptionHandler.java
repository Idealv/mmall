package com.joe.user.common.advice;

import com.joe.user.common.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public ServerResponse handleException(ConstraintViolationException e){
        String errorMsg = formatMsg(e.getMessage());

        return ServerResponse.createByErrorMessage(errorMsg);
    }

    private String formatMsg(String sourceStr) {
        if (sourceStr.contains(",")) {
            String[] errorMsgs = sourceStr.split(",");
            for (int i = 0; i <errorMsgs.length ; i++) {
                errorMsgs[i] = errorMsgs[i].substring(errorMsgs[i].indexOf(".") + 1);
            }
            return StringUtils.join(errorMsgs, ",");
        } else {
             return sourceStr.substring(sourceStr.indexOf("."));
        }
    }

}
