package com.experiment.anonyauth.Entity.User;

import com.experiment.anonyauth.Tool.ElementOperation;
import com.experiment.anonyauth.Tool.ElementStringConverter;
import it.unisa.dia.gas.jpbc.Element;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 15:46
 */
@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class VerifyInfo {
    private Element phi_1;
    private Element phi_2;
    private Element phi_3;
    private Element credAggRandom;
    private Element c;
    private Element S_n;
    private Element S_alpha;
    private Element S_u;
    private Element S_o;
    private Element S_beta;
    private Element S_gamma;
    private String[][] attributes;
    private Element[][] ivk;
    private String msg;

    public Element getPhi_1() {
        return phi_1;
    }

    public void setPhi_1(Element phi_1) {
        this.phi_1 = phi_1;
    }

    public Element getPhi_2() {
        return phi_2;
    }

    public void setPhi_2(Element phi_2) {
        this.phi_2 = phi_2;
    }

    public Element getPhi_3() {
        return phi_3;
    }

    public Element getCredAggRandom() {
        return credAggRandom;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Element[][] getIvk() {
        return ivk;
    }

    public String[][] getAttributes() {
        return attributes;
    }

    public void setAttributes(String[][] attributes) {
        this.attributes = attributes;
    }

    public void setIvk(Element[][] ivk) {
        this.ivk = ivk;
    }

    public VerifyInfo(){

    }
    public VerifyInfo(Element phi_1,
                      Element phi_2,
                      Element phi_3,
                      Element credAggRandom,
                      Element c,
                      Element S_n,
                      Element S_alpha,
                      Element S_u,
                      Element S_o,
                      Element S_beta,
                      Element S_gamma,
                      String[][] attributes,
                      Element[][] ivk,
                      String msg) {
        this.phi_1=phi_1;
        this.phi_2=phi_2;
        this.phi_3=phi_3;
        this.credAggRandom=credAggRandom;
        this.c=c;
        this.S_n=S_n;
        this.S_alpha=S_alpha;
        this.S_u=S_u;
        this.S_o=S_o;
        this.S_beta=S_beta;
        this.S_gamma=S_gamma;
        this.attributes=attributes;
        this.ivk=ivk;
        this.msg=msg;
    }
}
