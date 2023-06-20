package org.example;

import it.unisa.dia.gas.jpbc.Element;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.example.Util.ElementOperation;
import org.example.Util.ElementStringConverter;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
/**
 * @author Administrator
 * @date2023/6/9 0009 9:45
 */
@DataType
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
//验证通过之后需要上传到区块链中的数据
public class VerifyInfo {

    //随机化后的聚合凭证
    @Property
    Element CredAggRandom;

    //用户进出公共场所出示的凭证证明Pi
    @Property
    Element Phi_1;
    @Property
    Element Phi_2;
    @Property
    Element Phi_3;
    @Property
    Element S_n;
    @Property
    Element S_alpha;
    @Property
    Element S_u;
    @Property
    Element S_o;
    @Property
    Element S_beta;
    @Property
    Element S_gamma;
    //Hash value
    @Property
    Element C;

    @Property
    String[][] attributes;

    @Property
    Element[][] ivk;

    //用户进出共场所信息msg
    @Property
    String msg;


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
        ElementStringConverter.stringToElement(verifyInfoVo,verifyInfo);
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
                ivkStr[i][j]= ElementOperation.getElementString(ivkList[i][j]);
        }
        verifyInfoVo.setIvk(ivkStr);
        ElementStringConverter.elementToString(verifyInfo, verifyInfoVo);
        return verifyInfoVo;
    }

}
