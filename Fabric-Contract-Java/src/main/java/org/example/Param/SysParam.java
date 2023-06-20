package org.example.Param;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import lombok.Data;
import org.example.SysEntry;
import org.example.Util.ElementOperation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Data
/**
 * @author Administrator
 * @date2023/6/14 0014 9:22
 * 系统中所需要的公开系统参数
 */
public class SysParam {

    public static Pairing pairing;

    //G1群上的生成元
    public static Element g1;

    public static Element g_1;

    public static Element g_2;

    //G2群上的生成元
    public static Element g2;

    //当前时间纪元
    public static String epoch;

    public static Field Zr;
    public static Field G1;
    public static Field G2;
    public static Field GT;

    //系统参数初始化阶段
    static{
        pairing = PairingFactory.getPairing("f.properties");
        Zr=pairing.getZr();
        G1=pairing.getG1();
        G2=pairing.getG2();
        GT=pairing.getGT();

        Properties properties = new Properties();
        InputStream input = null;

        try {
            // 加载properties文件
            input = SysEntry.class.getClassLoader().getResourceAsStream("params.properties");
            properties.load(input);

            // 读取属性值
            String g1String = properties.getProperty("g1");
            String g_1String = properties.getProperty("g_1");
            String g_2String = properties.getProperty("g_2");
            String g2String = properties.getProperty("g2");

            g1= ElementOperation.getElementFromString(g1String).getImmutable();
            g_1= ElementOperation.getElementFromString(g_1String).getImmutable();
            g_2= ElementOperation.getElementFromString(g_2String).getImmutable();
            g2= ElementOperation.getElementFromString(g2String).getImmutable();

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
