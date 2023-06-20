package org.example.Util;

import com.alibaba.fastjson.JSON;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;
import org.example.Param.SysParam;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author PerpersonCyber
 * @date2023/6/20 0020 9:11
 */
public class ElementOperation {
    private static final String Zr="Zr";
    private static final String G1="G1";
    private static final String G2="G2";
    private static final String GT="GT";

    @Data
    private static class ElementStorage{
        public String element;
        public String field;
    }

    //根据element得到对应的字符串,用于存储和传输
    public static String getElementString(Element element){
        Field field = element.getField();
        ElementStorage elementStorage = new ElementStorage();

        //判断元素的field
        if(field.equals(SysParam.G1))
            elementStorage.field=G1;
        else if(field.equals(SysParam.G2))
            elementStorage.field=G2;
        else if(field.equals(SysParam.Zr))
            elementStorage.field=Zr;
        else if(field.equals(SysParam.GT))
            elementStorage.field=GT;
        else
            throw new RuntimeException("元素异常,不是G1,G2,Zr,GT上的元素!");
        elementStorage.element= Arrays.toString(element.toBytes());
        return JSON.toJSONString(elementStorage);
    }

    //根据储存element的字符串和对应的field生成元素
    public static Element getElementFromString(String elementString,Field field){
        if(elementString==null||"".equals(elementString))
            return null;
        String[] stringList = elementString.substring(1, elementString.length() - 1).split(", ");
        byte[] byteList=new byte[stringList.length];
        for(int i=0;i<stringList.length;i++){
            byteList[i]=Byte.parseByte(stringList[i]);
        }

        return field.newElementFromBytes(byteList);
    }

    //根据储存element的字符串生成元素
    public static Element getElementFromString(String elementString){
        if(elementString==null||"".equals(elementString))
            return null;
        ElementStorage elementStorage = JSON.parseObject(elementString, ElementStorage.class);
        String elementStr = elementStorage.element;
        String fieldType=elementStorage.field;
        Field field=null;
        switch (fieldType) {
            case G1:
                field = SysParam.G1;
                break;
            case G2:
                field = SysParam.G2;
                break;
            case Zr:
                field = SysParam.Zr;
                break;
            case GT:
                field = SysParam.GT;
                break;
            default:
                throw new RuntimeException("数据异常,元素类型不明确");
        }
        return getElementFromString(elementStr,field);
    }

    //将字符串转为zr上的element
    public static Element StringConvertZrElement(String str){
        return SysParam.Zr.newElementFromBytes(str.getBytes());
    }

    //Hash函数  将一干元素转化为一个zr群上的元素
    public static Element HashToZr(Element... elements){
        byte[][] bytes=new byte[elements.length][];
        int sumLen=0;
        for (int i=0;i<elements.length;i++){
            bytes[i]=elements[i].toBytes();
            sumLen+=bytes[i].length;
        }
        byte[] resBytes=new byte[sumLen];
        int len=0;
        for(int i=0;i<elements.length;i++){
            System.arraycopy(bytes[i],0,resBytes,len,bytes[i].length);
            len+=bytes[i].length;
        }

        // 创建MessageDigest实例
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Unsupported hash algorithm");
        }

        // 计算哈希值
        byte[] hashValue = md.digest(resBytes);

        return SysParam.Zr.newElementFromHash(hashValue,0,hashValue.length);
    }


    //Hash函数  将若干元素转换成为一个G1群上的元素
    public static Element HashToG1(Element... elements){
        byte[][] bytes=new byte[elements.length][];
        int sumLen=0;
        for (int i=0;i<elements.length;i++){
            bytes[i]=elements[i].toBytes();
            sumLen+=bytes[i].length;
        }
        byte[] resBytes=new byte[sumLen];
        int len=0;
        for(int i=0;i<elements.length;i++){
            System.arraycopy(bytes[i],0,resBytes,len,bytes[i].length);
            len+=bytes[i].length;
        }

        // 创建MessageDigest实例
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Unsupported hash algorithm");
        }

        // 计算哈希值
        byte[] hashValue = md.digest(resBytes);

        return SysParam.G1.newElementFromHash(hashValue,0,hashValue.length);
    }

}
