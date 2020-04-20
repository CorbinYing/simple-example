package org.corbin.simple.utils.xml.example;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

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


}
