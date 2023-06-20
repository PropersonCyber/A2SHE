package org.example;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import org.example.Util.ElementOperation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Administrator
 * @date2023/6/14 0014 10:33
 */
@Data
public class RegistrationAuth {
    private static final String fileName="register.properties";

    final static int NUMBER = 3;

    //注册中心的签名私钥和验证公钥
    static Element[] sk = new Element[NUMBER];

    public static Element[] vk = new Element[NUMBER];

    //注册中心的追踪私钥和追踪公钥
    static Element tsk;

    public static Element tpk;

    //注册中心的初始化
    static{
        Properties properties = new Properties();
        InputStream input = null;


        try {
            // 加载properties文件
            input = SysEntry.class.getClassLoader().getResourceAsStream(fileName);
            properties.load(input);

            //配置文件读取，RA的公私钥对
            for(int i=0;i<NUMBER;i++){
                String skStr = properties.getProperty("sk_" + i);
                sk[i]= ElementOperation.getElementFromString(skStr).getImmutable();
            }
            for(int i=0;i<NUMBER;i++){
                String vkStr = properties.getProperty("vk_" + i);
                vk[i]=ElementOperation.getElementFromString(vkStr).getImmutable();
            }

            //读取RA的追踪公私钥对
            String tskStr=properties.getProperty("tsk");
            tsk=ElementOperation.getElementFromString(tskStr).getImmutable();

            String tpkStr = properties.getProperty("tpk");
            tpk=ElementOperation.getElementFromString(tpkStr).getImmutable();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
