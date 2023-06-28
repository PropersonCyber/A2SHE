package com.experiment.anonyauth;

import com.experiment.anonyauth.Entity.User.UserProof;
import com.experiment.anonyauth.Entity.User.VerifyInfo;
import com.experiment.anonyauth.Entity.User.VerifyInfoVo;
import com.experiment.anonyauth.Param.*;
import com.experiment.anonyauth.Tool.*;
import it.unisa.dia.gas.jpbc.Element;
import org.apache.catalina.User;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AnonyAuthApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void RASetupTest(){
        System.out.println(RAParam.rsk);
        System.out.println(RAParam.rvk);
        System.out.println(RAParam.tsk);
        System.out.println(RAParam.tpk);
    }

    @Test
    void ProcessTest(){
        //当前时间纪元
        String epoch=String.valueOf(System.currentTimeMillis());
        PublicParam.epoch=epoch;

        //初始化用户
        UserParam.usk=PublicParam.Zr.newRandomElement().getImmutable();
        UserParam.uvk=PublicParam.g1.duplicate().powZn(UserParam.usk).getImmutable();
        UserParam.alpha_l=PublicParam.Zr.newRandomElement().getImmutable();
        UserParam.nodeValue=PublicParam.Zr.newRandomElement().getImmutable();

        //获取身份令牌token
        UserParam.tToken=RAOperation.createUserTraceToken(UserOperation.createUserProof(RAParam.rvk),UserParam.nodeValue,UserParam.alpha_l).getImmutable();
        UserParam.rToken=RAOperation.createUserRevokeToken(UserParam.nodeValue,UserParam.alpha_l).getImmutable();

        System.out.println("UserParam Init");


        String[] attrs= Issuer1Param.attribute;
        Element[] iskList=Issuer1Param.isk;
        Element[] ivkList= Issuer1Param.ivk;

        //判断发行方的公私钥对有效
        boolean equal=PublicParam.g2.powZn(iskList[0]).isEqual(ivkList[0]);

        //issuer1
        UserProof userProof = UserOperation.createUserProof(ivkList);
        Boolean userHaveSecretKey = CommonOperation.isUserHaveSecretKey(ivkList[0], userProof);

        Credential credential1 = IssuerOperation.getCredential(UserParam.uvk, 2, ivkList,iskList, attrs);
        Boolean aBoolean1 = UserOperation.SingleCredVerify(credential1, ivkList);


        String message = "OKKKKKK";

        AggregationCredential aggregationCredential= UserOperation.CredAgg(credential1);

        VerifyInfo verifyInfo=UserOperation.Show(aggregationCredential,message);

        VerifyInfoVo verifyInfoVo=TransferOperation.toVo(verifyInfo);
        VerifyInfo verifyInfo1=TransferOperation.fromVo(verifyInfoVo);

        if(verifyInfo1.equals(verifyInfo))
            System.out.println("proof trans ok");
        Element traceToken1 = RAOperation.trace(verifyInfo);

        if(traceToken1.isEqual(UserParam.tToken))
            System.out.println("trace Ok");

        Boolean verify = VerifierOperation.Verify(verifyInfo);
        System.out.println(verify);


    }

}
