package org.corbin.simple.utils.xml.example;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author xiesu
 * JAXB 转换对象必须属于JAXBElement类型，或者使用@XmlRootElement注解
 * <p>
 * order类生成的xml格式如下
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * <order>
 * <id>1</id>
 * <orderPrice>100.9</orderPrice>
 * <products>
 * <singleProduct productId="p1">
 * <productName>p1产品</productName>
 * </singleProduct>
 * <singleProduct productId="p2">
 * <productName>p2产品</productName>
 * </singleProduct>
 * </products>
 * <extraAttr>
 * <businessName>大灰狼有限公司</businessName>
 * <customerName>喜羊羊</customerName>
 * </extraAttr>
 * </order>
 */
@Getter
@Setter
@ToString
//可自定义xml的根节点名，不写默认与小写字母开头的类名相同
@XmlRootElement(name = "order")
//使用 @XmlElement()注解时需要使用此注解
@XmlAccessorType(XmlAccessType.FIELD)
public class Order {

    private Integer id;

    @XmlElement(name = "orderPrice")
    private Double price;

    //定义循环嵌套的singleProduct的外层标签名
    @XmlElementWrapper(name = "products")
    //定义嵌套对象的别名,不定义默认属性名为标签名
    @XmlElement(name = "singleProduct")
    //也可以使用@XmlAnyAttrbute注解  List<Object> List<? extends Product> 动态放入具体商品等
    private List<Product> productList;

    //@XmlElementRef注解 可以利用继承关系，显示ExtraAttr的子类的属性字段
    private ExtraAttr extraAttr;

    /**
     * 使用JAXB必须拥有无参构造方法，如果覆盖，需要显示指定
     */
    public Order() {
    }


}
