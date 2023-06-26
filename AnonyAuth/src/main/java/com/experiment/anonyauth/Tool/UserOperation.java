package com.experiment.anonyauth.Tool;

import com.experiment.anonyauth.Entity.User.UserProof;
import com.experiment.anonyauth.Entity.User.VerifyInfo;
import com.experiment.anonyauth.Param.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 16:10
 */
public class UserOperation {
    public static UserProof createUserProof(Element ivk){
        Element g1 = PublicParam.g1;
        Element g2 = PublicParam.g2;

        Element uvk = UserParam.uvk;
        Element usk = UserParam.usk;

        Element f = PublicParam.Zr.newRandomElement().getImmutable();
        Element R = g1.duplicate().powZn(f).getImmutable();
        Element hashedR = ElementOperation.Hash(ivk, uvk, R);
        Element Sb = f.add(hashedR.duplicate().mul(usk)).getImmutable();

        UserProof userProof = new UserProof();
        userProof.setUvk(uvk);
        userProof.setSb(Sb);
        userProof.setHashedR(hashedR);
        return userProof;
    }
    /**
     * 验证发行方的发布的凭证的有效性
     * @param credential
     * @param ivkList
     * @return
     */
    public static Boolean SingleCredVerify(Credential credential, Element[] ivkList){
        Pairing pairing = PublicParam.pairing;
        Element g2 = PublicParam.g2;

        Element uvk = UserParam.uvk;

        Element left =pairing.pairing(credential.getCredential(), g2);
        String[] attributes = credential.getAttributes();
        Element[] attribute = new Element[attributes.length];
        Element[] exp = new Element[attributes.length];
        for (int i = 0; i < attributes.length; i++) {
            byte[] attributeByte = attributes[i].getBytes();
            attribute[i] = PublicParam.Zr.newElementFromBytes(attributeByte);
            exp[i] = ivkList[i+1].duplicate().powZn(attribute[i]).getImmutable();
        }

        Element signaturePrefix = exp[0];
        for (int i = 1; i < attributes.length; i++) {
            signaturePrefix = signaturePrefix.duplicate().mul(exp[i]);
        }

        Element signature = ivkList[0].duplicate().mul(signaturePrefix).mul(ivkList[ivkList.length - 1]);

        Element right =pairing.pairing(uvk, signature);

        return left.isEqual(right);
    }

    /**
     * 将所有的凭证聚合为一个凭证
     * @param credentials
     * @return
     */
    public static AggregationCredential CredAgg(Credential... credentials) {
        AggregationCredential aggregationCredential=new AggregationCredential();
        //聚合凭证
        Element aggCred = credentials[0].getCredential();
        for (int i = 1; i < credentials.length; i++) {
            aggCred = aggCred.duplicate().mul(credentials[i].getCredential());
        }
        aggregationCredential.setAggCredential(aggCred);

        //属性和issuerId
        String[][] attributes=new String[credentials.length][];
        Element[][] ivkList=new Element[credentials.length][];
        for(int i=0;i<credentials.length;i++){
            attributes[i]=credentials[i].getAttributes();
            ivkList[i]=credentials[i].getIvkList();
        }

        aggregationCredential.setAttributes(attributes);
        aggregationCredential.setIvkList(ivkList);
        return aggregationCredential;

    }

    public static VerifyInfo Show(AggregationCredential credential, String message) {
        Element credagg=credential.getAggCredential();
        Element[][] ivk=credential.getIvkList();
        String[][] attr=credential.getAttributes();

        //引入参数
        Pairing pairing = PublicParam.pairing;
        Field Zr = PublicParam.Zr;
        Element g1 = PublicParam.g1;
        Element g_1=PublicParam.g_1;
        Element g_2 = PublicParam.g_2;
        Element g2 = PublicParam.g2;

        Element rToken = UserParam.rToken;
        Element tToken = UserParam.tToken;
        Element usk = UserParam.usk;
        Element alpha_l=UserParam.alpha_l;
        Element nodeValue =UserParam.nodeValue;
        Element Rv=UserParam.Rv;
        Element epoch_t = ElementOperation.StringConvertZrElement(PublicParam.epoch).getImmutable();


        Element rtvk = RAParam.rtvk;
        Element[] rvk = RAParam.rvk;


        //预计算值
        //e(g_2,g2 )
        Element E_g_2_g2 = pairing.pairing(g_2, g2).getImmutable();
        //e(tpk,vk_3)
        Element E_tpk_vk3 = pairing.pairing(rtvk, rvk[2]).getImmutable();
        //e(g1,g2)
        Element E_g1_g2=pairing.pairing(g1, g2).getImmutable();
        //e(tpk,g2)
        Element E_tpk_g2=pairing.pairing(rtvk, g2).getImmutable();
        //e(g_1,g2)
        Element E_g_1_g2=pairing.pairing(g_1, g2).getImmutable();
        //H(vk1,vk2,vk3,t)拼接的字符串
        //将hash值映射到G1群上
        Element H_vk_t=ElementOperation.HashToG1(rvk[0],rvk[1],rvk[2],epoch_t);
        //e(H(vk,t),vk1)
        Element E_H_vk1=pairing.pairing(H_vk_t,rvk[0]).getImmutable();
        //e(H(vk,t),vk2)
        Element E_H_vk2=pairing.pairing(H_vk_t,rvk[1]).getImmutable();
        //e(H(vk,t),vk3)
        Element E_H_vk3=pairing.pairing(H_vk_t,rvk[2]).getImmutable();
        //e(H(vk,t),g2)
        Element E_H_g2=pairing.pairing(H_vk_t, g2).getImmutable();
        //e(H(vk,t),vk1)*e(H(vk,t),vk3)^t
        Element E_H_vk13=pairing.pairing(H_vk_t,rvk[0].duplicate().mul(rvk[2].duplicate().powZn(epoch_t))).getImmutable();

        //计算参数
        Element gamma_1 = Zr.newRandomElement().getImmutable();
        Element gamma_2= Zr.newRandomElement().getImmutable();
        Element phi_1 = tToken.duplicate().mul(rtvk.duplicate().powZn(gamma_1)).getImmutable();
        Element phi_2 = rToken.duplicate().mul(H_vk_t.duplicate().powZn(gamma_2)).getImmutable();
        Element phi_3 = g1.duplicate().powZn(gamma_1).getImmutable();
        Element beta = gamma_1.duplicate().mul(alpha_l).getImmutable();
        Element credagg_ran = credagg.duplicate().powZn(gamma_1).getImmutable();

        Element r_n = Zr.newRandomElement().getImmutable();
        Element r_alpha = Zr.newRandomElement().getImmutable();
        Element r_u = Zr.newRandomElement().getImmutable();
        Element r_o = Zr.newRandomElement().getImmutable();
        Element r_beta = Zr.newRandomElement().getImmutable();
        Element r_gamma = Zr.newRandomElement().getImmutable();


        //计算D1
        Element[][] attribute = new Element[attr.length][];

        for (int i = 0; i < attr.length; i++) {
            attribute[i]=new Element[attr[i].length];
            for (int j = 0; j < attr[i].length; j++) {
                byte[] att = attr[i][j].getBytes();
                attribute[i][j] = Zr.newElementFromBytes(att, 0);
            }
        }

        Element rig0 = ivk[0][1].duplicate().powZn(attribute[0][0]);

        for (int i = 1; i < attr[0].length; i++) {
            rig0 = rig0.duplicate().mul(ivk[0][i+1].duplicate().powZn(attribute[0][i]));//不知道ivk[1][]签名的属性个数，计算ivk1,i^a1,i
        }

        Element rigr = ivk[0][0].duplicate().mul(rig0).getImmutable();//计算ivk1,0*ivk1,i^a1,i
        Element rigg = rigr.duplicate().mul(ivk[0][ivk[0].length - 1]);//计算j=1下的ivk1,0*ivk1,i^a1,i*ivk1,ivk1.length-1

        for (int i = 1; i < attr.length; i++) {
            Element r1 = ivk[i][1].duplicate().powZn(attribute[i][0]);
            for (int j = 1; j < attr[i].length; j++) {
                r1 = r1.duplicate().mul(ivk[i][j+1].duplicate().powZn(attribute[i][j]));//计算出j下的ivkj,j^aj,j;
            }
            Element r2 = ivk[i][0].duplicate().mul(r1).getImmutable();
            Element r3 = r2.duplicate().mul(ivk[i][ivk[i].length - 1]);
            rigg = rigg.duplicate().mul(r3).getImmutable();//将j个凭证值连乘
        }

        Element leftElement = phi_3.duplicate().powZn(r_u).getImmutable();

        Element D1 = pairing.pairing(leftElement, rigg);

        //计算D2
        Element D2_r1 = pairing.pairing(phi_1.duplicate().powZn(r_alpha.duplicate().negate()), g2).getImmutable();
        Element D2_r2 = E_g_2_g2.duplicate().powZn(r_n).getImmutable();
        Element D2_r3 = E_g1_g2.duplicate().powZn(r_u).getImmutable();
        Element D2_r4 = E_tpk_vk3.duplicate().powZn(r_o).getImmutable();
        Element D2_r5 = E_tpk_g2.duplicate().powZn(r_beta).getImmutable();
        Element D2 = D2_r1.duplicate().mul(D2_r2).mul(D2_r3).mul(D2_r4).mul(D2_r5).getImmutable();

        //计算D3
        Element D3_r1 = E_H_vk2.duplicate().powZn(r_n).getImmutable();
        Element D3_r2 = E_H_g2.duplicate().powZn(r_gamma).getImmutable();
        Element D3 = D3_r1.duplicate().mul(D3_r2).getImmutable();

        //计算D4
        Element D4 = g1.duplicate().powZn(r_o).getImmutable();

        Element msg = ElementOperation.StringConvertZrElement(message);

        Element c = ElementOperation.Hash(phi_1,phi_2,phi_3,credagg_ran,D1,D2,D3,D4,msg);

        Element S_u = r_u.duplicate().add(c.duplicate().mul(usk)).getImmutable();
        Element S_n = r_n.duplicate().add(c.duplicate().mul(nodeValue)).getImmutable();
        Element S_alpha = r_alpha.duplicate().add(c.duplicate().mul(alpha_l)).getImmutable();
        Element S_o = r_o.duplicate().add(c.duplicate().mul(gamma_1)).getImmutable();
        Element S_beta = r_beta.duplicate().add(c.duplicate().mul(beta)).getImmutable();
        Element S_gamma = r_gamma.duplicate().add(c.duplicate().mul(gamma_2)).getImmutable();
        return new VerifyInfo(phi_1,phi_2,phi_3,credagg_ran,c,S_n,S_alpha,S_u,S_o,S_beta,S_gamma,attr,credential.getIvkList(), message);
    }
}
