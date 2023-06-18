package org.example;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import lombok.Data;

@Data
/**
 * @author Administrator
 * @date2023/6/14 0014 9:22
 * 系统中所需要的公开系统参数
 */
public class SysParam {
    public Pairing pairing;

    //G1群上的生成元
    public Element g1;

    public Element g_1;

    public Element g_2;

    //G2群上的生成元
    public Element g2;


    //系统参数生成
    public void setup(String properties){
        this.pairing=PairingFactory.getPairing(properties);
        this.g1=pairing.getG1().newRandomElement().getImmutable();
        this.g_1=pairing.getG1().newRandomElement().getImmutable();
        this.g_2=pairing.getG1().newRandomElement().getImmutable();
        this.g2=pairing.getG2().newRandomElement().getImmutable();
    }



}
