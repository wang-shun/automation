package com.gome.test.mock.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JaxbUtil<T> {

    /** 
     * java bean->XML 
     */
    public String toXmlString(T t) {

        StringWriter writer = new StringWriter();
        try {
            org.exolab.castor.xml.Marshaller marshaller = new org.exolab.castor.xml.Marshaller(writer);
            marshaller.setEncoding("UTF-8");
            marshaller.marshal(t);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return writer.toString();
    }

    public String beanToXml(T t) {
        String xml = "";
        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(t.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8");
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
            StringWriter stringWriter = new StringWriter();
            jaxbMarshaller.marshal(t, stringWriter);
            xml += stringWriter.toString();

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return xml;
    }

    @SuppressWarnings("unchecked")
    public T xmlToBean(String xml, T t) {
        T b = null;
        try {
            JAXBContext context = JAXBContext.newInstance(t.getClass());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            b = (T) unmarshaller.unmarshal(new StringReader(xml));

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return b;
    }

}
