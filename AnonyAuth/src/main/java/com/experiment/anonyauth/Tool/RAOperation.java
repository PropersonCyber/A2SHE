package com.experiment.anonyauth.Tool;

import com.experiment.anonyauth.Entity.User.UserProof;
import com.experiment.anonyauth.Entity.User.VerifyInfo;
import com.experiment.anonyauth.Param.PublicParam;
import com.experiment.anonyauth.Param.RAParam;
import it.unisa.dia.gas.jpbc.Element;

/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 17:12
 */
public class RAOperation {
    public static Element createUserTraceToken(UserProof userProof, Element nodeValue, Element fr){
        Element[] rvk = RAParam.rvk;
        Element[] rsk = RAParam.rsk;
        Element rvk1= rvk[0];
        Element rsk3 = rsk[3];

        Boolean isUserValid = CommonOperation.isUserHaveSecretKey(rvk1, userProof);
        if (isUserValid) {
            Element g_1 = PublicParam.g_1;
            Element g_2 = PublicParam.g_2;
            Element uvk = userProof.getUvk();

            Element expPrefix= rsk3.add(fr).getImmutable();
            Element exp=expPrefix.duplicate().invert().getImmutable();
            Element basePrefix=g_2.duplicate().powZn(nodeValue).duplicate().mul(uvk).getImmutable();
            Element base=g_1.duplicate().mul(basePrefix).getImmutable();
            Element tToken=base.duplicate().powZn(exp).getImmutable();
            return tToken;
        }
        else {
            return null;
        }
    }

    /**
     * 生成撤销令牌
     * @param nodeValue 节点对应的特殊值,系统分配
     * @param Rv 负责溯源的元素
     * @return 撤销令牌
     */
    public static Element createUserRevokeToken(Element nodeValue, Element Rv){
        String epoch=PublicParam.epoch;
        Element g1 = PublicParam.g1;

        Element[] rsk = RAParam.rsk;

        Element epochElement = ElementOperation.StringConvertZrElement(epoch);
        Element exp1 = rsk[1].duplicate().mul(nodeValue).getImmutable();
        Element exp2 = rsk[2].duplicate().mul(epochElement).getImmutable();
        Element exp3 = rsk[3].duplicate().mul(Rv).getImmutable();
        Element exp4 = rsk[0].duplicate().add(exp1).getImmutable();
        Element exp5 = exp4.duplicate().add(exp2).getImmutable();
        Element exp = exp5.duplicate().add(exp3).getImmutable();
        return g1.duplicate().powZn(exp).getImmutable();
    }

    public static Element trace(VerifyInfo VerifyInfo) {
        Element phi_1 = VerifyInfo.getPhi_1();
        Element phi_3 = VerifyInfo.getPhi_3();
        Element rtsk = RAParam.rtsk;
        Element trace_token = phi_1.duplicate().div(phi_3.duplicate().powZn(rtsk)).getImmutable();
        return trace_token;
    }
}
