package com.just.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;

@RestController
public class VulController {
    @PostMapping("/vul")
    public String vul(HttpServletRequest request) throws Exception {
        String data = request.getParameter("data");
        if (data == null) {
            return "I need data";
        }
        byte[] decode = Base64.getDecoder().decode(data);
        ByteArrayInputStream bais = new ByteArrayInputStream(decode);
        ObjectInputStream ois = new ObjectInputStream(bais);
        ois.readObject();
        return "vul";
    }

    @RequestMapping("/")
    public String index() {
        return "<h1>Jackson vul environment</h1>post请求data参数到/vul接口";
    }
}
