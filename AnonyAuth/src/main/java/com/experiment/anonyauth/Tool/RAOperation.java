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
    /**
    *@author Yangpeng
    *@Description
    *创建用户的追踪密钥
    */
    public static Element createUserTraceToken(UserProof userProof, Element nodeValue, Element alpha_r){
        Element[] rvk = RAParam.rvk;
        Element[] rsk = RAParam.rsk;
        Element rvk1= rvk[0];
        Element rsk3 = rsk[2];

        Boolean isUserValid = CommonOperation.isUserHaveSecretKey(rvk1,userProof);
        if (isUserValid) {
            Element uvk = userProof.getUvk();
            Element expPrefix= rsk3.add(alpha_r).getImmutable();
            Element exp=expPrefix.duplicate().invert().getImmutable();
            Element basePrefix=PublicParam.g_2.duplicate().powZn(nodeValue).duplicate().mul(uvk).getImmutable();
            Element base=PublicParam.g_1.duplicate().mul(basePrefix).getImmutable();
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

        Element epoch_t = ElementOperation.StringConvertZrElement(epoch);
        Element H_vk_t=ElementOperation.HashToG1(RAParam.rvk[0],epoch_t);


        Element exp1 = rsk[1].duplicate().mul(nodeValue).getImmutable();
        Element exp2 = rsk[2].duplicate().mul(epoch_t).getImmutable();
//        Element exp3 = rsk[3].duplicate().mul(Rv).getImmutable();
        Element exp3 = rsk[0].duplicate().add(exp1).getImmutable();
        Element exp = exp3.duplicate().add(exp2).getImmutable();
        return H_vk_t.duplicate().powZn(exp).getImmutable();
    }

    public static Element trace(VerifyInfo verifyInfo) {
        Element phi_1 = verifyInfo.getPhi_1();
        Element phi_3 = verifyInfo.getPhi_3();
        Element tsk = RAParam.tsk;
        Element trace_token = phi_1.duplicate().div(phi_3.duplicate().powZn(tsk)).getImmutable();
        return trace_token;
    }
}
