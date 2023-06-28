package com.experiment.anonyauth.Entity.User;

import com.experiment.anonyauth.Tool.ElementStringConverter;
import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 15:36
 */
@Data
public class UserProof {
    private Element uvk;
    private Element HashedR;
    private Element S_theta;
    private Element A;

    public Element getS_theta() {
        return S_theta;
    }

    public Element getUvk() {
        return uvk;
    }

    public Element getHashedR() {
        return HashedR;
    }

    public void setS_theta(Element s_theta) {
        S_theta = s_theta;
    }

    public void setHashedR(Element hashedR) {
        HashedR = hashedR;
    }

    public void setUvk(Element uvk) {
        this.uvk = uvk;
    }
}
