package com.experiment.anonyauth.Param;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 16:17
 */
@Data
public class UserParam {
    public static Element uvk;
    public static Element usk;

    //撤销token及其生成参数
    public static Element rToken;
    //节点对应的特殊值,系统分配
    public static Element nodeValue;
    public static Element Rv;


    //追踪token,随机参数
    public static Element alpha_l;
    public static Element tToken;
}
