package com.experiment.anonyauth.Tool;

import com.experiment.anonyauth.Entity.User.UserProof;
import com.experiment.anonyauth.Param.PublicParam;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 15:36
 */
public class CommonOperation {
    public static Boolean isUserHaveSecretKey(Element ivk, UserProof userProof){
        Field Zr = PublicParam.Zr;
        Field G2 = PublicParam.G2;
        Element g1 = PublicParam.g1;
        Element g2 = PublicParam.g2;

        Element Sb = userProof.getSb();
        Element uvk = userProof.getUvk();
        Element hashedR = userProof.getHashedR();

        Element Rl = g1.duplicate().powZn(Sb).getImmutable();
        Element Rr = uvk.duplicate().powZn(hashedR).negate();
        Element R1 = Rl.duplicate().mul(Rr).getImmutable();
        Element hashedElement1 = ElementOperation.Hash(ivk, uvk, R1);
        return hashedElement1.isEqual(hashedR);
    }
}
