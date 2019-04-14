package com.joe.category.utils;

import com.joe.category.enums.OperationEnum;
import com.joe.category.enums.ResultEnum;

public class MessageBuilder {
    private String entity;

    public MessageBuilder(String entity) {
        this.entity = entity;
    }

    public String buildMsg(OperationEnum operation, ResultEnum result) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.entity);
        buffer.append(operation.getOperation());
        buffer.append(result.getResult());
        return buffer.toString();
    }

    public String searchSuccess(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.entity);
        buffer.append(OperationEnum.SEARCH);
        buffer.append(ResultEnum.SUCCESS);
        return buffer.toString();
    }

    public String searchFailure(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.entity);
        buffer.append(OperationEnum.SEARCH);
        buffer.append(ResultEnum.FAILURE);
        return buffer.toString();
    }

    public String saveSuccess(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.entity);
        buffer.append(OperationEnum.SAVE);
        buffer.append(ResultEnum.SUCCESS);
        return buffer.toString();
    }

    public String saveFailure(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.entity);
        buffer.append(OperationEnum.SAVE);
        buffer.append(ResultEnum.FAILURE);
        return buffer.toString();
    }

    public String updateSuccess(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.entity);
        buffer.append(OperationEnum.UPDATE);
        buffer.append(ResultEnum.SUCCESS);
        return buffer.toString();
    }

    public String updateFailure(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.entity);
        buffer.append(OperationEnum.UPDATE);
        buffer.append(ResultEnum.FAILURE);
        return buffer.toString();
    }

    public String deleteSuccess(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.entity);
        buffer.append(OperationEnum.DELETE);
        buffer.append(ResultEnum.SUCCESS);
        return buffer.toString();
    }

    public String deleteFailure(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.entity);
        buffer.append(OperationEnum.DELETE);
        buffer.append(ResultEnum.FAILURE);
        return buffer.toString();
    }


}
