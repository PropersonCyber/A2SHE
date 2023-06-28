package com.experiment.anonyauth.Tool;

import com.experiment.anonyauth.Entity.User.*;
import it.unisa.dia.gas.jpbc.Element;

/**
 * @author PerpersonCyber
 * @date2023/6/27 0027 10:15
 */
public class TransferOperation {

    public static UserInfo fromVo(UserInfoVo userInfoVo) {
        UserInfo userInfo = new UserInfo();
        ElementStringConverter.stringToElement(userInfoVo, userInfo);
        userInfo.setId(userInfo.getId());
        return userInfo;
    }

    public static UserInfoVo toVo(UserInfo userInfo) {
        UserInfoVo userInfoVo = new UserInfoVo();
        ElementStringConverter.elementToString(userInfo, userInfoVo);
        userInfoVo.setId(userInfo.getId());
        return userInfoVo;
    }


    public static VerifyInfo fromVo(VerifyInfoVo verifyInfoVo){
        VerifyInfo verifyInfo = new VerifyInfo();
        verifyInfo.setAttributes(verifyInfoVo.getAttributes());
        verifyInfo.setMsg(verifyInfoVo.getMsg());
        String[][] ivkStr = verifyInfoVo.getIvk();
        Element[][] ivkList= new Element[ivkStr.length][];
        for(int i=0;i<ivkList.length;i++){
            ivkList[i]=new Element[ivkStr[i].length];
            for(int j=0;j<ivkList[i].length;j++)
                ivkList[i][j]= ElementOperation.getElementFromString(ivkStr[i][j]);
        }
        verifyInfo.setIvk(ivkList);
        ElementStringConverter.stringToElement(verifyInfoVo, verifyInfo);
        return verifyInfo;
    }

    public static VerifyInfoVo toVo(VerifyInfo verifyInfo){
        VerifyInfoVo verifyInfoVo = new VerifyInfoVo();
        verifyInfoVo.setMsg(verifyInfo.getMsg());
        verifyInfoVo.setAttributes(verifyInfo.getAttributes());
        Element[][] ivkList = verifyInfo.getIvk();
        String[][] ivkStr= new String[ivkList.length][];
        for(int i=0;i<ivkStr.length;i++){
            ivkStr[i]=new String[ivkList[i].length];
            for(int j=0;j<ivkStr[i].length;j++)
                ivkStr[i][j]=ElementOperation.getElementString(ivkList[i][j]);
        }
        verifyInfoVo.setIvk(ivkStr);
        ElementStringConverter.elementToString(verifyInfo, verifyInfoVo);
        return verifyInfoVo;
    }


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
