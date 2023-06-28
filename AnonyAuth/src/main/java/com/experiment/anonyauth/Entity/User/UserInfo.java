package com.experiment.anonyauth.Entity.User;

import com.experiment.anonyauth.Tool.ElementStringConverter;
import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 15:40
 */
@Data
public class UserInfo {
    private Long id;
    private Element rToken;
    private Element nodeValue;
    private Element rv;
    private Element fr;
    private Element tToken;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Element getrToken() {
        return rToken;
    }

    public void setrToken(Element rToken) {
        this.rToken = rToken;
    }

    public Element getRv() {
        return rv;
    }

    public void setRv(Element rv) {
        this.rv = rv;
    }

    public Element getFr() {
        return fr;
    }

    public void setFr(Element fr) {
        this.fr = fr;
    }

    public Element getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(Element nodeValue) {
        this.nodeValue = nodeValue;
    }

    public Element gettToken() {
        return tToken;
    }

    public void settToken(Element tToken) {
        this.tToken = tToken;
    }
}
