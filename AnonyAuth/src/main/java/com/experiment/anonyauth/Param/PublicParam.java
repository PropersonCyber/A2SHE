package com.experiment.anonyauth.Param;

import com.experiment.anonyauth.AnonyAuthApplication;
import com.experiment.anonyauth.Tool.ElementOperation;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 15:28
 */
public class PublicParam {
    public static String epoch;

    public static Element g1;
    public static Element g_1;
    public static Element g_2;
    public static Element g2;

    public static Pairing pairing;

    public static Field Zr;
    public static Field G1;
    public static Field G2;
    public static Field GT;

    static {
        pairing = PairingFactory.getPairing("f.properties");
        Zr=pairing.getZr();
        G1=pairing.getG1();
        G2=pairing.getG2();
        GT=pairing.getGT();

        Properties properties = new Properties();
        InputStream input = null;

        try {
            // 加载properties文件
            input = AnonyAuthApplication.class.getClassLoader().getResourceAsStream("params.properties");
            properties.load(input);

            // 读取属性值
            String g1s = properties.getProperty("g1");
            String g_1s = properties.getProperty("g_1");
            String g_2s = properties.getProperty("g_2");
            String g2s = properties.getProperty("g2");

            g1= ElementOperation.getElementFromString(g1s).getImmutable();
            g_1= ElementOperation.getElementFromString(g_1s).getImmutable();
            g_2= ElementOperation.getElementFromString(g_2s).getImmutable();
            g2= ElementOperation.getElementFromString(g2s).getImmutable();

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
