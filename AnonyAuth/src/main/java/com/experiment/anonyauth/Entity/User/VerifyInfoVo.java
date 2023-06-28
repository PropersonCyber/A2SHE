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

    public String getMsg() {
        return msg;
    }

    public void setPhi_2(String phi_2) {
        this.phi_2 = phi_2;
    }

    public void setPhi_1(String phi_1) {
        this.phi_1 = phi_1;
    }

    public String getC() {
        return c;
    }

    public String getCredAggRandom() {
        return credAggRandom;
    }

    public void setAttributes(String[][] attributes) {
        this.attributes = attributes;
    }

    public String[][] getAttributes() {
        return attributes;
    }

    public void setIvk(String[][] ivk) {
        this.ivk = ivk;
    }

    public String getPhi_1() {
        return phi_1;
    }

    public String getPhi_2() {
        return phi_2;
    }

    public String getPhi_3() {
        return phi_3;
    }

    public String getS_alpha() {
        return S_alpha;
    }

    public String getS_n() {
        return S_n;
    }

    public String getS_beta() {
        return S_beta;
    }

    public String getS_gamma() {
        return S_gamma;
    }

    public String getS_o() {
        return S_o;
    }

    public String getS_u() {
        return S_u;
    }

    public String[][] getIvk() {
        return ivk;
    }

    public void setC(String c) {
        this.c = c;
    }

    public void setCredAggRandom(String credAggRandom) {
        this.credAggRandom = credAggRandom;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setPhi_3(String phi_3) {
        this.phi_3 = phi_3;
    }

    public void setS_alpha(String s_alpha) {
        S_alpha = s_alpha;
    }

    public void setS_beta(String s_beta) {
        S_beta = s_beta;
    }

    public void setS_gamma(String s_gamma) {
        S_gamma = s_gamma;
    }

    public void setS_n(String s_n) {
        S_n = s_n;
    }

    public void setS_o(String s_o) {
        S_o = s_o;
    }

    public void setS_u(String s_u) {
        S_u = s_u;
    }
}
