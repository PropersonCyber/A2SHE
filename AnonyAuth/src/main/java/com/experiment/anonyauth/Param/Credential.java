package com.experiment.anonyauth.Param;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

@Data
/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 16:07
 */
public class Credential {
    private Element credential;
    private String[] attributes;
    private Element[] ivkList;

    public void setCredential(Element credential) {
        this.credential = credential;
    }

    public void setAttributes(String[] attributes) {
        this.attributes = attributes;
    }

    public void setIvkList(Element[] ivkList) {
        this.ivkList = ivkList;
    }

    public Element getCredential() {
        return credential;
    }

    public Element[] getIvkList() {
        return ivkList;
    }

    public String[] getAttributes() {
        return attributes;
    }
}
