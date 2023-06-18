package org.example;

import com.alibaba.fastjson.JSON;
import it.unisa.dia.gas.jpbc.Element;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.Random;

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
    SysParam sysParam;

    //初始化区块链账本
    @Transaction
    public void InitLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        UserMsg userMsg = new UserMsg().setPubID("ox0000001").setTimeStamp("20230609").setAuxInfo("UserMsg");

        VerifyInfo verifyInfo = new VerifyInfo().setCredAggRandom("test")
                .setMsg(userMsg)
                .setPhi_1("Test Phi_1")
                .setPhi_2("Test Phi_2")
                .setPhi_3("Test Phi_3")
                .setS_n("Test")
                .setS_alpha("Test")
                .setS_u("Test")
                .setS_o("Test")
                .setS_beta("Test")
                .setS_gamma("Test")
                .setC("Hash_C");

        stub.putStringState(verifyInfo.Phi_1, JSON.toJSONString(verifyInfo));
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
        ChaincodeStub stub = ctx.getStub();
        String verifyInfoState = stub.getStringState(verifyInfo.Phi_1);

        if (StringUtils.isNotBlank(verifyInfoState)) {
            String errorMessage = String.format("VerifyInfo Phi_1 %s already exists", verifyInfo.Phi_1);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage);
        }

        String json = JSON.toJSONString(verifyInfo);
        stub.putStringState(verifyInfo.Phi_1, json);
        stub.setEvent("createVerifyInfoEvent", org.apache.commons.codec.binary.StringUtils.getBytesUtf8(json));
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


        return true;
    }

    //预计算参数
    public Element[] PreCompute(SysParam sysParam, RegistrationAuth RA, String Epoch_t) {
        //e(g_2,g2 )
        Element E_g_2_g2 = sysParam.pairing.pairing(sysParam.g_2, sysParam.g2).getImmutable();
        //e(tpk,vk_3)
        Element E_tpk_vk3 = sysParam.pairing.pairing(RA.tpk, RA.vk[2]).getImmutable();
        //e(g1,g2)
        Element E_g1_g2=sysParam.pairing.pairing(sysParam.g1, sysParam.g2).getImmutable();
        //e(tpk,g2)
        Element E_tpk_g2=sysParam.pairing.pairing(RA.tpk, sysParam.g2).getImmutable();
        //e(g_1,g2)
        Element E_g_1_g2=sysParam.pairing.pairing(sysParam.g_1, sysParam.g2).getImmutable();

        //时间纪元转换成Element元素
        Element Epoch=Util.StringToElement(sysParam.pairing,Epoch_t);
        //H(vk1,vk2,vk3,t)拼接的字符串
        String vk_t=Util.ElementArrayToString(RA.getVk())+Epoch;
        //将hash值映射到G1群上
        Element H_vk_t=sysParam.pairing.getG1().newElementFromHash(vk_t.getBytes(),0,vk_t.getBytes().length);

        //e(H(vk,t),vk1)
        Element E_H_vk1=sysParam.pairing.pairing(H_vk_t,RA.getVk()[0]).getImmutable();
        //e(H(vk,t),vk2)
        Element E_H_vk2=sysParam.pairing.pairing(H_vk_t,RA.getVk()[1]).getImmutable();
        //e(H(vk,t),vk3)
        Element E_H_vk3=sysParam.pairing.pairing(H_vk_t,RA.getVk()[2]).getImmutable();
        //e(H(vk,t),g2)
        Element E_H_g2=sysParam.pairing.pairing(H_vk_t, sysParam.g2).getImmutable();

        Element[] preRes={E_g_2_g2,E_tpk_vk3,E_g1_g2,E_tpk_g2,E_g_1_g2,E_H_g2,E_H_vk1,E_H_vk2,E_H_vk3,H_vk_t};

        return preRes;

    }
}
