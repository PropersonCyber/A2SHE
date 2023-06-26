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
    private Element Sb;

    public static UserProof fromVo(UserProofVo userProofVo){
        UserProof userProof = new UserProof();
        ElementStringConverter.stringToElement(userProofVo, userProof);
        return userProof;
    }

    public static UserProofVo toVo(UserProof UserProof){
        UserProofVo UserProofVo = new UserProofVo();
        ElementStringConverter.elementToString(UserProof,UserProofVo);
        return UserProofVo;
    }
}
