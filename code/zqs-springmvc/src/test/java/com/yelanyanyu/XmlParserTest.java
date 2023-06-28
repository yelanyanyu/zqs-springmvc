package com.yelanyanyu;

import com.yelanyanyu.zqsspringmvc.xml.XmlParser;
import org.junit.Test;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class XmlParserTest {
    @Test
    public void Test01() {
        String file = XmlParserTest.class.getClassLoader().getResource("springConfig.xml").getPath();
        System.out.println(file);
        String basePackage = XmlParser.getBasePackage("springConfig.xml");
        System.out.println(basePackage);
    }
}
