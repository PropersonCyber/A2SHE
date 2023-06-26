package com.experiment.anonyauth.Param;

import com.experiment.anonyauth.AnonyAuthApplication;
import com.experiment.anonyauth.Tool.ElementOperation;
import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 16:18
 */
@Data
public class RAParam {
    private static final String fileName="register.properties";
    private static final int size=4;

    public static Element[] rvk;
    public static Element rtvk;

    public static Element[] rsk;
    public static Element rtsk;


    static {

        Properties properties = new Properties();
        InputStream input = null;

        rvk=new Element[size];
        rsk=new Element[size];

        try {
            // 加载properties文件
            input = AnonyAuthApplication.class.getClassLoader().getResourceAsStream(fileName);
            properties.load(input);

            //rsk读取属性值
            for(int i=0;i<size;i++){
                String rskStr = properties.getProperty("rsk_" + i);
                rsk[i]= ElementOperation.getElementFromString(rskStr).getImmutable();
            }

            //rvk读取属性值
            for(int i=0;i<size;i++){
                String rvkStr = properties.getProperty("rvk_" + i);
                rvk[i]=ElementOperation.getElementFromString(rvkStr).getImmutable();
            }

            //rtsk获取属性值
            String rtskStr=properties.getProperty("rtsk");
            rtsk=ElementOperation.getElementFromString(rtskStr).getImmutable();

            String rtvkStr = properties.getProperty("rtvk");
            rtvk=ElementOperation.getElementFromString(rtvkStr).getImmutable();

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
