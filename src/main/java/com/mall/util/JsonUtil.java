package com.mall.util;

import com.google.common.collect.Lists;
import com.mall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
public class JsonUtil {
    public static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //所有字段都转换成JSON
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        //取消默认的将Date转为时间戳的设置
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        //取消空Bean报错的设置
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        //设置标准时间格式
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));
        //忽略json字符串中存在但是java对象字段不存在的情况
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static <T> String stringify(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper
                    .writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Stringify obj to string error", e);
            return null;
        }
    }

    public static <T> T parse(String jsonStr, Class<T> clazz){
        if (StringUtils.isEmpty(jsonStr)||clazz==null){
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) jsonStr : objectMapper.readValue(jsonStr, clazz);
        } catch (IOException e) {
            log.warn("Parse string to obj error", e);
            return null;
        }
    }

    public static <T> T parse(String jsonStr, TypeReference<T> typeReference){
        if (StringUtils.isEmpty(jsonStr)||typeReference==null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class) ?
                    (T) jsonStr:
                    objectMapper.readValue(jsonStr, typeReference));
        } catch (IOException e) {
            log.warn("Parse string to obj error", e);
            return null;
        }
    }

    public static <T> T parse(String jsonStr, Class<?> collectionClass, Class<?>... elementClasses) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        try {
            return objectMapper.readValue(jsonStr, javaType);
        } catch (IOException e) {
            log.warn("Parse string to obj error", e);
            return null;
        }
    }

    public static void main(String[] args) {
        User u1 = new User();
        u1.setUsername("xiaoming");
        u1.setPhone("13812345678");
        User u2 = new User();
        u2.setUsername("damao");
        u2.setPhone("13887654321");
        List<User> userList = Lists.newArrayList();
        userList.add(u1);
        userList.add(u2);
        String s = stringify(userList);
        List<User> userList1=parse(s, new TypeReference<List<User>>() {
        });
        User user = userList1.get(0);
        System.out.println("user.getN = " + user.getUsername());

    }
}
