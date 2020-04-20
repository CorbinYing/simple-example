package org.corbin.simple.utils.xml.example;


import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author xiesu
 * JAXB 转换对象必须属于JAXBElement类型，或者使用@XmlRootElement注解
 */
@Getter
@Setter
@XmlRootElement
//使用 @XmlElement(name = "realName")注解时需要使用此注解
@XmlAccessorType(XmlAccessType.FIELD)
public class JavaBeanExample implements Serializable {
    private Integer id;
    @XmlElement(name = "realName")
    private String name;

    /**
     * 使用JAXB必须拥有无参构造方法，如果覆盖，需要显示指定
     */
    public JavaBeanExample() {
    }
}
