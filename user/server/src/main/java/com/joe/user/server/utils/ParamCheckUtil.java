package com.joe.user.server.utils;

import com.joe.user.common.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ParamCheckUtil {
    public static ServerResponse<Object> checkParam(BindingResult result){
        if (result.hasErrors()){
            List<ObjectError> errorList = result.getAllErrors();
            Map<String, String> errorMap = new HashMap<>();
            errorList.stream().forEach(e ->
                    errorMap.put(((FieldError) e).getField(),
                            e.getDefaultMessage())
            );
            log.error(errorMap.toString());
            ServerResponse.createByError(errorMap);
        }
        return ServerResponse.createBySuccess();
    }
}
