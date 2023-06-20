package org.example;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import org.hyperledger.fabric.contract.annotation.Property;

/**
 * @author PerpersonCyber
 * @date2023/6/20 0020 9:59
 */
@Data
public class VerifyInfoVo {
    String CredAggRandom;
    String Phi_1;
    String Phi_2;
    String Phi_3;
    String S_n;
    String S_alpha;
    String S_u;
    String S_o;
    String S_beta;
    String S_gamma;
    String C;
    String[][] attributes;
    String[][] ivk;
    String msg;
}
