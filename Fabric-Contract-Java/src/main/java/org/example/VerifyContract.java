package org.example;

import com.alibaba.fastjson.JSON;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import org.apache.commons.lang3.StringUtils;
import org.example.Param.SysParam;
import org.example.Util.ElementOperation;
import org.example.Util.StringUtil;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

/**
 * @author Administrator
 * @date2023/6/9 0009 9:50
 */
@Contract(
        name = "VerifyContract",
        info = @Info(
                title = "Verify Contract",
                description = "The Hyperledger Fabric Verify Contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "f.carr@example.com",
                        name = "F Carr",
                        url = "https://hyperledger.example.com")
        )
)
public class VerifyContract implements ContractInterface {

    //系统初始化阶段
    SysParam sysParam;
    RegistrationAuth registrationAuth;

    //初始化区块链账本
    @Transaction
    public void InitLedger(final Context ctx) {
//        ChaincodeStub stub = ctx.getStub();
//        UserMsg userMsg = new UserMsg().setPubID("ox0000001").setTimeStamp("20230609").setAuxInfo("UserMsg");
//
//        VerifyInfo verifyInfo = new VerifyInfo().setCredAggRandom("test")
//                .setMsg(userMsg)
//                .setPhi_1("Test Phi_1")
//                .setPhi_2("Test Phi_2")
//                .setPhi_3("Test Phi_3")
//                .setS_n("Test")
//                .setS_alpha("Test")
//                .setS_u("Test")
//                .setS_o("Test")
//                .setS_beta("Test")
//                .setS_gamma("Test")
//                .setC("Hash_C");
//
//        stub.putStringState(verifyInfo.Phi_1.toString(), JSON.toJSONString(verifyInfo));
    }


    //查询链上数据
    @Transaction
    public VerifyInfo queryVerifyInfo(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String VerifyInfoState = stub.getStringState(key);
        if (StringUtils.isBlank(VerifyInfoState)) {
            String errorMessage = String.format("VerifyInfo Phi_1 %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage);
        }

        return JSON.parseObject(VerifyInfoState, VerifyInfo.class);
    }

    //验证通过之后，在区块链上创建一条数据
    @Transaction
    public VerifyInfo createVerifyInfo(final Context ctx, VerifyInfo verifyInfo) {
//        ChaincodeStub stub = ctx.getStub();
//        String verifyInfoState = stub.getStringState(verifyInfo.Phi_1);
//
//        if (StringUtils.isNotBlank(verifyInfoState)) {
//            String errorMessage = String.format("VerifyInfo Phi_1 %s already exists", verifyInfo.Phi_1);
//            System.out.println(errorMessage);
//            throw new ChaincodeException(errorMessage);
//        }
//
//        String json = JSON.toJSONString(verifyInfo);
//        stub.putStringState(verifyInfo.Phi_1, json);
//        stub.setEvent("createVerifyInfoEvent", org.apache.commons.codec.binary.StringUtils.getBytesUtf8(json));
        return verifyInfo;
    }

    //验证用户出示的凭证，通过验证之后将认证信息上传到区块链中
    @Transaction
    public boolean Verify_tx(final Context ctx, VerifyInfo verifyInfo) {
        //调用验证算法
        /**
         *@author Yangpeng
         *@Description
         *对用户出示的凭证证明信息进行验证，验证通过之后将信息上传（即在区块上创建信息）
         */
        if (Verify(verifyInfo)) {
            createVerifyInfo(ctx, verifyInfo);
            return true;
        }

        return false;
    }

    //验证算法
    /*
     * 导入相关的密码学包，根据实际验证算法的场景重写验证算法
     * */
    public boolean Verify(VerifyInfo verifyInfo) {
        //计算D1'的值
        //D1=Pa
        //引入参数
        Pairing pairing = SysParam.pairing;
        Element g1 = SysParam.g1;
        Element g_1 = SysParam.g_1;
        Element g_2 = SysParam.g_2;
        Element g2 = SysParam.g2;




        Element tpk = RegistrationAuth.tpk;
        Element[] vk = RegistrationAuth.vk;

        Element[][] ivk=verifyInfo.getIvk();
        String message=verifyInfo.getMsg();

        String epoch=SysParam.epoch;
        Element epoch_t = ElementOperation.StringConvertZrElement(epoch).getImmutable();

        Element phi_1= verifyInfo.getPhi_1().getImmutable();
        Element phi_2 = verifyInfo.getPhi_2().getImmutable();
        Element phi_3 = verifyInfo.getPhi_3().getImmutable();
        Element credAggRandom = verifyInfo.getCredAggRandom().getImmutable();
        Element c = verifyInfo.getC().getImmutable();
        Element c_negate = c.duplicate().negate();
        Element S_n = verifyInfo.getS_n().getImmutable();
        Element S_alpha = verifyInfo.getS_alpha().getImmutable();
        Element S_u = verifyInfo.getS_u().getImmutable();
        Element S_o = verifyInfo.getS_o().getImmutable();
        Element S_beta = verifyInfo.getS_beta().getImmutable();
        Element S_gamma = verifyInfo.getS_gamma().getImmutable();

        String[][] attr= verifyInfo.getAttributes();

        //计算D_1
        Element[][] attribute = new Element[attr.length][];

        for (int i = 0; i < attr.length; i++) {
            attribute[i] = new Element[attr[i].length];
            for (int j = 0; j < attr[i].length; j++) {
                //把第i,j个属性值转换成bytes，并映射到Zr群上
                byte[] att = attr[i][j].getBytes();
                attribute[i][j] = pairing.getZr().newElementFromBytes(att, 0);
            }
        }

        Element rig0 = ivk[0][1].duplicate().powZn(attribute[0][0]);

        for (int i = 1; i < attr[0].length; i++) {
            rig0 = rig0.duplicate().mul(ivk[0][i + 1].duplicate().powZn(attribute[0][i]));//不知道ivk[1][]签名的属性个数，计算ivk1,i^a1,i
        }

        Element rigg = ivk[0][0].duplicate().mul(rig0).getImmutable();//计算ivk1,0*ivk1,i^a1,i

        //根据m发行方数量以及每个发行方签发的属性数量计算乘积
        //访问策略中包含m个发行方的数量
        for(int i=1;i<ivk.length;i++){
            Element r1 = ivk[i][1].duplicate().powZn(attribute[i][0]);
            for(int j=1;j<attr[i].length;j++){
                r1 = r1.duplicate().mul(ivk[i][j + 1].duplicate().powZn(attribute[i][j]));//计算出j下的ivkj,j^aj,j;
            }
            Element r2 = ivk[i][0].duplicate().mul(r1).getImmutable();
            rigg = rigg.duplicate().mul(r2).getImmutable();//将j个凭证值连乘
        }


        //计算D_1
        Element D1_r1 = pairing.pairing(phi_3.duplicate().powZn(S_u), rigg).getImmutable();
        Element D1_r2 = pairing.pairing(credAggRandom.duplicate().powZn(c_negate), g2).getImmutable();
        Element D_1 = D1_r1.duplicate().mul(D1_r2).getImmutable();

       //预计算值
        //e(g_2,g2 )
        Element E_g_2_g2 = pairing.pairing(g_2, g2).getImmutable();
        //e(tpk,vk_3)
        Element E_tpk_vk3 = pairing.pairing(tpk, vk[2]).getImmutable();
        //e(g1,g2)
        Element E_g1_g2=pairing.pairing(g1, g2).getImmutable();
        //e(tpk,g2)
        Element E_tpk_g2=pairing.pairing(tpk, g2).getImmutable();
        //e(g_1,g2)
        Element E_g_1_g2=pairing.pairing(g_1, g2).getImmutable();


//        //H(vk1,vk2,vk3,t)拼接的字符串
//        String vk_t= StringUtil.ElementArrayToString(vk)+epoch;
        //将hash值映射到G1群上
        Element H_vk_t=ElementOperation.HashToG1(vk[0],vk[1],vk[2],epoch_t);

        //e(H(vk,t),vk1)
        Element E_H_vk1=pairing.pairing(H_vk_t,vk[0]).getImmutable();
        //e(H(vk,t),vk2)
        Element E_H_vk2=pairing.pairing(H_vk_t,vk[1]).getImmutable();
        //e(H(vk,t),vk3)
        Element E_H_vk3=pairing.pairing(H_vk_t,vk[2]).getImmutable();
        //e(H(vk,t),g2)
        Element E_H_g2=pairing.pairing(H_vk_t, g2).getImmutable();
        //e(H(vk,t),vk1)*e(H(vk,t),vk3)^t
        Element E_H_vk13=pairing.pairing(H_vk_t,vk[0].duplicate().mul(vk[2].duplicate().powZn(epoch_t))).getImmutable();

        //计算D_2
        Element D2_r1 = E_g_2_g2.duplicate().powZn(S_n);
        Element D2_r2 = pairing.pairing(phi_1.duplicate().powZn(S_alpha.duplicate().negate()), g2).getImmutable();
        Element D2_r3 =E_tpk_vk3.duplicate().powZn(S_o).getImmutable();
        Element D2_r4 = E_g1_g2.duplicate().powZn(S_u).getImmutable();
        Element D2_r5 = E_tpk_g2.duplicate().powZn(S_beta).getImmutable();
        Element D2_r6_1 = pairing.pairing(phi_1, vk[2]).getImmutable();

        //Element D2_r6 = (D2_r6_1.duplicate().powZn(c_negate).mul(g_1_E_g2.duplicate().powZn(c))).getImmutable();

        Element D2_r6 = (D2_r6_1.duplicate().div(E_g_1_g2)).powZn(c_negate).getImmutable();
        Element D_2 = D2_r1.duplicate().mul(D2_r2).mul(D2_r3).mul(D2_r4).mul(D2_r5).mul(D2_r6).getImmutable();

        //计算D_3
        Element D3_r1 = E_H_vk2.duplicate().powZn(S_n).getImmutable();
        Element D3_r2 = E_H_g2.duplicate().powZn(S_gamma).getImmutable();
        Element D3_r3_1 = pairing.pairing(phi_2, g2).getImmutable();
        Element D3_r3 = (D3_r3_1.duplicate().div(E_H_vk13)).powZn(c_negate).getImmutable();
        Element D_3 = D3_r1.duplicate().mul(D3_r2).mul(D3_r3).getImmutable();

        //计算D_4
        Element D4_r1 = g1.duplicate().powZn(S_o).getImmutable();
        Element D4_r2 = phi_3.duplicate().powZn(c_negate).getImmutable();
        Element D_4 = D4_r1.duplicate().mul(D4_r2).getImmutable();

        //将消息转成byte数组元素
        Element mes = ElementOperation.StringConvertZrElement(message);

        //Element cc = ElementOperation.Hash(phi_1,D_1);
        Element cc = ElementOperation.HashToZr(phi_1, phi_2, phi_3, credAggRandom, D_1, D_2, D_3, D_4, mes);
        return cc.isEqual(c);
    }

    //预计算参数
    public Element[] PreCompute(String Epoch_t) {
        //e(g_2,g2 )
        Element E_g_2_g2 = SysParam.pairing.pairing(SysParam.g_2, SysParam.g2).getImmutable();
        //e(tpk,vk_3)
        Element E_tpk_vk3 = SysParam.pairing.pairing(RegistrationAuth.tpk, RegistrationAuth.vk[2]).getImmutable();
        //e(g1,g2)
        Element E_g1_g2=SysParam.pairing.pairing(SysParam.g1, SysParam.g2).getImmutable();
        //e(tpk,g2)
        Element E_tpk_g2=SysParam.pairing.pairing(RegistrationAuth.tpk, SysParam.g2).getImmutable();
        //e(g_1,g2)
        Element E_g_1_g2=SysParam.pairing.pairing(SysParam.g_1, SysParam.g2).getImmutable();

        //时间纪元转换成Element元素
        Element Epoch= StringUtil.StringToElement(SysParam.pairing,Epoch_t);
        //H(vk1,vk2,vk3,t)拼接的字符串
        String vk_t= StringUtil.ElementArrayToString(RegistrationAuth.vk)+Epoch;
        //将hash值映射到G1群上
        Element H_vk_t=SysParam.pairing.getG1().newElementFromHash(vk_t.getBytes(),0,vk_t.getBytes().length);

        //e(H(vk,t),vk1)
        Element E_H_vk1=SysParam.pairing.pairing(H_vk_t,RegistrationAuth.vk[0]).getImmutable();
        //e(H(vk,t),vk2)
        Element E_H_vk2=SysParam.pairing.pairing(H_vk_t,RegistrationAuth.vk[1]).getImmutable();
        //e(H(vk,t),vk3)
        Element E_H_vk3=SysParam.pairing.pairing(H_vk_t,RegistrationAuth.vk[2]).getImmutable();
        //e(H(vk,t),g2)
        Element E_H_g2=SysParam.pairing.pairing(H_vk_t, SysParam.g2).getImmutable();

        return new Element[]{E_g_2_g2,E_tpk_vk3,E_g1_g2,E_tpk_g2,E_g_1_g2,E_H_g2,E_H_vk1,E_H_vk2,E_H_vk3,H_vk_t};

    }
}
