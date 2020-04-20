package org.corbin.xml.stream.web;

import org.corbin.simple.utils.xml.XmlUtil;
import org.corbin.simple.utils.xml.example.JavaBeanExample;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
public class xmlFileController {

    @RequestMapping("/java-xml")
    public String test() {
        JavaBeanExample example = new JavaBeanExample();
        example.setId(34);
        example.setName("苏小小");

        String result = XmlUtil.beanToXml(example, StandardCharsets.UTF_8);
        System.out.println(result);
        //<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        //<javaBeanExample>
        //    <id>34</id>
        //    <realName>苏小小</realName>
        //</javaBeanExample>
        return result;
        //浏览器显示 34 苏小小


    }
}
