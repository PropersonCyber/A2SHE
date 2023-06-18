package org.example;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

/**
 * @author Administrator
 * @date2023/6/14 0014 10:33
 */
@Data
public class RegistrationAuth {
    final int NUMBER = 3;
    //注册中心的签名私钥和验证公钥
    Element[] sk = new Element[NUMBER];

    Element[] vk = new Element[NUMBER];

    //注册中心的追踪私钥和追踪公钥
    Element tsk;

    Element tpk;
}
