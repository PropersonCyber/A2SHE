package com.experiment.anonyauth.Param;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

@Data
/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 16:09
 */
public class AggregationCredential {
    private Element aggCredential;
    private String[][] attributes;
    private Element[][] ivkList;

    public void setAttributes(String[][] attributes) {
        this.attributes = attributes;
    }

    public String[][] getAttributes() {
        return attributes;
    }

    public void setIvkList(Element[][] ivkList) {
        this.ivkList = ivkList;
    }

    public Element getAggCredential() {
        return aggCredential;
    }

    public void setAggCredential(Element aggCredential) {
        this.aggCredential = aggCredential;
    }

    public Element[][] getIvkList() {
        return ivkList;
    }
}
