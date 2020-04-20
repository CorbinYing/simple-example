package org.corbin.simple.utils.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

/**
 * 配合jABX 使用，转换为对象
 * {@link javax.xml.bind.annotation.XmlElement}
 */
public final class XmlUtil {


    /**
     * java Bean转xml
     * java bean 需要使用jaxb标准的注解表示
     * bean示例{@link }
     * @param elementBean
     * @param charset
     * @param <T>
     * @return
     */
    public static <T> String beanToXml(T elementBean, Charset charset) {
        String result = null;
        try {
            // 获取JAXB的上下文环境，需要传入具体的 Java bean
            Class<?> clazz = elementBean.getClass();
            JAXBContext context = JAXBContext.newInstance(clazz);
            // 创建 Marshaller 实例
            Marshaller marshaller = context.createMarshaller();
            // 设置转换参数 -> 这里举例是告诉序列化器是否格式化输出
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, charset.name());

            StringWriter writer = new StringWriter();
            marshaller.marshal(elementBean, writer);
            result = writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> void beanToXml(T elementBean, OutputStream outputStream, Charset charset) {
        try {
            Class<?> clazz = elementBean.getClass();
            // 获取JAXB的上下文环境，需要传入具体的 Java bean
            JAXBContext context = JAXBContext.newInstance(clazz);
            // 创建 Marshaller 实例
            Marshaller marshaller = context.createMarshaller();
            // 设置转换参数 -> 这里举例是告诉序列化器是否格式化输出
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, charset.name());
            marshaller.marshal(elementBean, outputStream);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


}
