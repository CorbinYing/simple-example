package org.corbin.simple.utils.xml.example;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class Product {

    //表示为属性，而非元素,id的值以属性值呈现
    //@XmlAttribute 与 @XmlElement相互排斥
    @XmlAttribute(name = "productId")
    private String id;

    @XmlElement(name = "productName")
    private String name;
    //使用xml list 将列表中的所有字段显示在同一个标签中，空格隔开
    //不使用则分多个相同标签分别显示
    @XmlList
    private List<String> tags;


}
