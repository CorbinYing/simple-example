package org.corbin.simple.utils.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
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
     *
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

    /**
     * bean 转xml输出流
     *
     * @param elementBean
     * @param outputStream
     * @param charset
     * @param <T>
     */
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

    /**
     * xml转bean
     *
     * @param xmlData
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T xmlToBean(String xmlData, Class<T> clazz) {
        return xmlToBean(new StringReader(xmlData), clazz);
    }

    /**
     * xml转bean
     * 不提供关闭reader方法,由调用者关闭
     *
     * @param reader
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T xmlToBean(Reader reader, Class<T> clazz) {
        T domain = null;
        try {
            // 获取JAXB的上下文环境，需要传入具体的 Java bean -> 这里使用Student
            JAXBContext context = JAXBContext.newInstance(clazz);
            // 创建 UnMarshaller 实例
            Unmarshaller unmarshaller = context.createUnmarshaller();
            //将XML数据序列化 -> 该方法的返回值为Object基类，需要强转类型
            domain = (T) unmarshaller.unmarshal(reader);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return domain;
    }
}
