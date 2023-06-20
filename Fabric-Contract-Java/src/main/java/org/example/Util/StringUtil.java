package org.example.Util;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.Base64;

/**
 * @author Administrator
 * @date2023/6/14 0014 10:51
 */
public class StringUtil {
    //将输入的String中的每个元素转变成Element中映射到Zr上的点元素
    public static Element StringToElement(Pairing pairing, String str) {
        byte[] att = str.getBytes();
        Element attribute = pairing.getZr().newElementFromBytes(att, 0);

        return attribute;
    }

    //将输入的String一维数组中的每个元素转变成Element中映射到Zr上的点元素
    public static Element[] StringToElementOne(Pairing pairing, String[] str) {
        Element[] attribute = new Element[str.length];
        for (int i = 0; i < str.length; i++) {
            byte[] att = str[i].getBytes();
            attribute[i] = pairing.getZr().newElementFromBytes(att, 0);
        }
        return attribute;
    }

    //将输入的String二维数组中的每个元素转变成Element中映射到Zr上的点元素
    public static Element[][] StringToElementTwo(Pairing pairing, String[][] str) {

        Element[][] attribute = new Element[str.length][str[1].length];

        for (int j = 1; j < str.length; j++) {
            for (int i = 1; i < str[j].length; i++) {
                byte[] att = str[j][i].getBytes();
                attribute[j][i] = pairing.getZr().newElementFromBytes(att, 0);
            }
        }
        return attribute;
    }

    //将Element元素转换成String类型
    public static String ElementToString(Element element){
        byte[] bytes=element.toBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
    //将Element[]元素转换成String类型
    public static String ElementArrayToString(Element[] elements){
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<elements.length;i++){
            byte[] tempByte=elements[i].toBytes();
            builder.append(Base64.getEncoder().encodeToString(tempByte));
        }
        return builder.toString();
    }
}
