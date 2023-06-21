package com.example.fabricjava;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 * @date2023/6/19 0019 9:37
 */
@Data
@Accessors(chain = true)
public class VerifyInfo {
    //随机化后的聚合凭证
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

    String attributes;

    String ivk;

    String msg;

}
