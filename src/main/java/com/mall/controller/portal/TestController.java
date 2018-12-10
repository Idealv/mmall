package com.mall.controller.portal;

import com.google.common.collect.Maps;
import com.mall.common.ServerResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/test")
public class TestController {
    @RequestMapping("remote.do")
    @ResponseBody
    public ServerResponse test(){
        Map<String, String> map = Maps.newHashMap();
        map.put("test", "value");
        return ServerResponse.createBySuccess(map);
    }
}
