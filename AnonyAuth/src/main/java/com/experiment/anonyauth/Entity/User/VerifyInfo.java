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
@NoArgsConstructor
@AllArgsConstructor
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
        verifyInfoVo.setMsg(verifyInfo.msg);
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
}
