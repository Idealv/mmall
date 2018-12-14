package com.mall.util;

import com.mall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;
import java.text.SimpleDateFormat;

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

    public static void main(String[] args) {
        User user = new User();
        user.setUsername("xiaoming");
        user.setPhone("13812345678");
        String userStr = stringify(user);
        User u = parse(userStr, User.class);
        System.out.println(u.getUsername());
    }
}
