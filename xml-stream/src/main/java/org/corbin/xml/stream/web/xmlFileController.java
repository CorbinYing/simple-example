package org.corbin.xml.stream.web;

import com.google.common.collect.Lists;
import org.corbin.simple.utils.xml.XmlUtil;
import org.corbin.simple.utils.xml.example.ExtraAttr;
import org.corbin.simple.utils.xml.example.Order;
import org.corbin.simple.utils.xml.example.Product;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiesu
 */
@RestController
public class xmlFileController {

    @RequestMapping("/java-xml")
    public String test() {
        ExtraAttr extraAttr = new ExtraAttr();
        extraAttr.setBusinessName("大灰狼有限公司");
        extraAttr.setCustomerName("喜羊羊");

        Product p1 = new Product();
        p1.setId("p1");
        p1.setName("p1产品");

        Product p2 = new Product();
        p2.setId("p2");
        p2.setName("p2产品");
        p2.setTags(Arrays.asList("进口", "优选"));

        List<Product> products = Lists.newArrayList(p1, p2);


        Order order = new Order();
        order.setId(1);
        order.setPrice(100.9d);
        order.setExtraAttr(extraAttr);
        order.setProductList(products);


        String result = XmlUtil.beanToXml(order, StandardCharsets.UTF_8);
        System.out.println(result);

        return result;

    }

    @RequestMapping("xml-java")
    public void xmlToBean() {
        String xmldata = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<order>\n" +
                "    <id>1</id>\n" +
                "    <orderPrice>100.9</orderPrice>\n" +
                "    <products>\n" +
                "        <singleProduct productId=\"p1\">\n" +
                "            <productName>p1产品</productName>\n" +
                "        </singleProduct>\n" +
                "        <singleProduct productId=\"p2\">\n" +
                "            <productName>p2产品</productName>\n" +
                "            <tags>进口 优选</tags>\n" +
                "        </singleProduct>\n" +
                "    </products>\n" +
                "    <extraAttr>\n" +
                "        <businessName>大灰狼有限公司</businessName>\n" +
                "        <customerName>喜羊羊</customerName>\n" +
                "    </extraAttr>\n" +
                "</order>";

        Order order = XmlUtil.xmlToBean(xmldata, Order.class);
        System.out.println(order.toString());

    }
}
