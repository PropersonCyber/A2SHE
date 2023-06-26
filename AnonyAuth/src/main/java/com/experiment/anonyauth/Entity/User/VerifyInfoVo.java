package com.experiment.anonyauth.Entity.User;

import lombok.Data;

@Data
/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 15:47
 */
public class VerifyInfoVo {
    private String phi_1;
    private String phi_2;
    private String phi_3;
    private String credAggRandom;
    private String c;
    private String S_n;
    private String S_alpha;
    private String S_u;
    private String S_o;
    private String S_beta;
    private String S_gamma;
    private String[][] attributes;
    private String[][] ivk;
    private String msg;
}
