package com.yelanyanyu.zqsspringmvc.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.net.URL;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class XmlParser {
    /**
     * 得到springConfig.xml文件中的base-package
     *
     * @return
     */
    public static String getBasePackage(String xmlFileName) {
        InputStream is = XmlParser.class.getClassLoader().getResourceAsStream(xmlFileName);
        System.out.println(is);
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(is);
            Element rootElement = document.getRootElement();
            Attribute attribute = rootElement.element("component-scan").attribute("base-package");
            return attribute.getText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
