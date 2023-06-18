package org.example;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 * @date2023/6/9 0009 11:09
 */
@Data
@Accessors(chain = true)
public class VerifyInfo {

    String CredAgg;

    String Phi_1;

    String Phi_2;

    String Phi_3;

    String W_k;

    String W_f;

    String W_u;

    String W_ou;

    String W_o;

    String W_beta;

    String Hash_C;

    String Attributes;

    UserMsg msg;
}
