package com.experiment.anonyauth.Tool;

import com.experiment.anonyauth.Entity.User.VerifyInfo;
import com.experiment.anonyauth.Param.AggregationCredential;
import com.experiment.anonyauth.Param.PublicParam;
import com.experiment.anonyauth.Param.RAParam;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

/**
 * @author PerpersonCyber
 * @date2023/6/28 0028 9:37
 */
public class VerifierOperation {
    public static Boolean AggregationCredVerify(Element uvk, AggregationCredential aggregationCredential, Element[]... ivkList) {
        Element g2 = PublicParam.g2;
        Element credential = aggregationCredential.getAggCredential();
        String[][] attributes = aggregationCredential.getAttributes();

        Element left = PublicParam.pairing.pairing(credential, g2);

        Element[][] attributesElement = new Element[attributes.length][];
        for (int i = 0; i < attributes.length; i++) {
            attributesElement[i] = new Element[attributes[i].length];
            for (int j = 0; j < attributes[i].length; j++) {
                byte[] attribute = attributes[i][j].getBytes();
                attributesElement[i][j] = PublicParam.pairing.getZr().newElementFromBytes(attribute, 0);
            }
        }

        Element[] rigs = new Element[attributes.length];

        for (int i = 0; i < attributes.length; i++) {
            Element rig = ivkList[i][1].duplicate().powZn(attributesElement[i][0]);
            for (int j = 1; j < attributes[i].length; j++)
                rig = rig.duplicate().mul(ivkList[i][j + 1].duplicate().powZn(attributesElement[i][j]));
            rig = ivkList[i][0].duplicate().mul(rig).getImmutable();
            rig = rig.duplicate().mul(ivkList[i][ivkList[i].length - 1]);
            rigs[i] = rig;
        }

        Element aggregationRig = rigs[0];
        for (int i = 1; i < rigs.length; i++)
            aggregationRig = aggregationRig.duplicate().mul(rigs[i]).getImmutable();

        Element right = PublicParam.pairing.pairing(uvk, aggregationRig);

        return right.isEqual(left);
    }

    public static Boolean Verify(VerifyInfo verifyInfo) {

        //引入参数
        Element g2 = PublicParam.g2;
        Element g_2 = PublicParam.g_2;
        Element g_1 = PublicParam.g_1;
        Element g1 = PublicParam.g1;

        Element tpk = RAParam.tpk;
        Element[] rvk = RAParam.rvk;
        Element[][] ivk = verifyInfo.getIvk();
        String message = verifyInfo.getMsg();

        String epoch = PublicParam.epoch;
        Element epoch_t = ElementOperation.StringConvertZrElement(epoch).getImmutable();

        Element phi_1 = verifyInfo.getPhi_1().getImmutable();
        Element phi_2 = verifyInfo.getPhi_2().getImmutable();
        Element phi_3 = verifyInfo.getPhi_3().getImmutable();
        Element credAgg = verifyInfo.getCredAggRandom().getImmutable();
        Element c = verifyInfo.getC().getImmutable();
        Element c_negate = c.duplicate().negate();

        Element S_u = verifyInfo.getS_u().getImmutable();
        Element S_n = verifyInfo.getS_n().getImmutable();
        Element S_alpha = verifyInfo.getS_alpha().getImmutable();
        Element S_o = verifyInfo.getS_o().getImmutable();
        Element S_beta = verifyInfo.getS_beta().getImmutable();
        Element S_gamma = verifyInfo.getS_gamma().getImmutable();

        String[][] attr = verifyInfo.getAttributes();

        //计算D_1
        Element[][] attribute = new Element[attr.length][];

        for (int i = 0; i < attr.length; i++) {
            attribute[i] = new Element[attr[i].length];
            for (int j = 0; j < attr[i].length; j++) {
                byte[] att = attr[i][j].getBytes();
                attribute[i][j] = PublicParam.pairing.getZr().newElementFromBytes(att, 0);
            }
        }

        Element rig0 = ivk[0][1].duplicate().powZn(attribute[0][0]);

        for (int i = 1; i < attr[0].length; i++) {
            rig0 = rig0.duplicate().mul(ivk[0][i + 1].duplicate().powZn(attribute[0][i]));//不知道ivk[1][]签名的属性个数，计算ivk1,i^a1,i
        }

        Element rigr = ivk[0][0].duplicate().mul(rig0).getImmutable();//计算ivk1,0*ivk1,i^a1,i
        Element rigg = rigr.duplicate().mul(ivk[0][ivk[0].length - 1]);//计算j=1下的ivk1,0*ivk1,i^a1,i*ivk1,ivk1.length-1

        for (int i = 1; i < ivk.length; i++) {
            Element r1 = ivk[i][1].duplicate().powZn(attribute[i][0]);
            for (int j = 1; j < attr[i].length; j++) {
                r1 = r1.duplicate().mul(ivk[i][j + 1].duplicate().powZn(attribute[i][j]));//计算出j下的ivkj,j^aj,j;
            }
            Element r2 = ivk[i][0].duplicate().mul(r1).getImmutable();
            Element r3 = r2.duplicate().mul(ivk[i][ivk[i].length - 1]);
            rigg = rigg.duplicate().mul(r3).getImmutable();//将j个凭证值连乘
        }


        //预计算值
        //e(g_2,g2 )
        Element E_g_2_g2 = PublicParam.pairing.pairing(g_2, g2).getImmutable();
        //e(tpk,vk_3)
        Element E_tpk_vk3 = PublicParam.pairing.pairing(tpk, rvk[2]).getImmutable();
        //e(g1,g2)
        Element E_g1_g2=PublicParam.pairing.pairing(g1, g2).getImmutable();
        //e(tpk,g2)
        Element E_tpk_g2=PublicParam.pairing.pairing(tpk, g2).getImmutable();
        //e(g_1,g2)
        Element E_g_1_g2=PublicParam.pairing.pairing(g_1, g2).getImmutable();
        //H(vk1,vk2,vk3,t)拼接的字符串
        //将hash值映射到G1群上
        Element H_vk_t=ElementOperation.HashToG1(rvk[0],epoch_t);
        //e(H(vk,t),vk2)
        Element E_H_vk2=PublicParam.pairing.pairing(H_vk_t,rvk[1]).getImmutable();
        //e(H(vk,t),g2)
        Element E_H_g2=PublicParam.pairing.pairing(H_vk_t, g2).getImmutable();
        //e(H(vk,t),vk1)*e(H(vk,t),vk3)^t
        Element E_H_vk13=PublicParam.pairing.pairing(H_vk_t,rvk[0].duplicate().mul(rvk[2].duplicate().powZn(epoch_t))).getImmutable();




        //计算D_1
        Element D1_r1 = PublicParam.pairing.pairing(phi_3.duplicate().powZn(S_u), rigg).getImmutable();
        Element D1_r2 = PublicParam.pairing.pairing(credAgg.duplicate().powZn(c_negate), g2).getImmutable();
        Element D_1 = D1_r1.duplicate().mul(D1_r2).getImmutable();


        //计算D_2
        Element D2_r1 = E_g_2_g2.duplicate().powZn(S_n).getImmutable();
        Element D2_r2 = PublicParam.pairing.pairing(phi_1.duplicate().powZn(S_alpha.duplicate().negate()), g2).getImmutable();
        Element D2_r3 = E_tpk_vk3.duplicate().powZn(S_o).getImmutable();
        Element D2_r4 = E_g1_g2.duplicate().powZn(S_u).getImmutable();
        Element D2_r5 = E_tpk_g2.duplicate().powZn(S_beta).getImmutable();
        Element D2_r6_1 = PublicParam.pairing.pairing(phi_1, rvk[2]).getImmutable();
        Element D2_r6 = (D2_r6_1.duplicate().div(E_g_1_g2)).duplicate().powZn(c_negate).getImmutable();
        Element D_2 = D2_r1.duplicate().mul(D2_r2).mul(D2_r3).mul(D2_r4).mul(D2_r5).mul(D2_r6).getImmutable();

        //计算D_3
        Element D3_r1 = E_H_vk2.duplicate().powZn(S_n).getImmutable();
        Element D3_r2 = E_H_g2.duplicate().powZn(S_gamma).getImmutable();
        Element D3_r3_1 = PublicParam.pairing.pairing(phi_2, g2).getImmutable();
        Element D3_r3 = (D3_r3_1.duplicate().div(E_H_vk13)).duplicate().powZn(c_negate).getImmutable();
        Element D_3 = D3_r1.duplicate().mul(D3_r2).mul(D3_r3).getImmutable();

        //计算D_4
        Element D4_r1 = g1.duplicate().powZn(S_o).getImmutable();
        Element D4_r2 = phi_3.duplicate().powZn(c_negate).getImmutable();
        Element D_4 = D4_r1.duplicate().mul(D4_r2).getImmutable();

        //将消息转成byte数组元素
        Element mes = ElementOperation.StringConvertZrElement(message);

        //Element cc = ElementOperation.Hash(phi_1,D_1);
        Element cc = ElementOperation.Hash(phi_1, phi_2, phi_3, credAgg, D_1, D_2, D_3, D_4, mes);
        return cc.isEqual(c);
    }
}
