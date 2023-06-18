package org.example;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.GatewayException;

import java.util.Map;

/**
 * @author Administrator
 * @date2023/6/9 0009 14:37
 */
@Slf4j
@AllArgsConstructor
public class VerifyInfoContract {
    final Gateway gateway;

    final Contract contract;

    final HyperLedgerFabricProperties hyperLedgerFabricProperties;

    //查询区块链上的数据
    public Map<String, Object> queryVerifyInfoByKey(String key) throws GatewayException {

        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] verifyInfos = contract.evaluateTransaction("queryVerifyInfo", key);

        result.put("payload", StringUtils.newStringUtf8(verifyInfos));
        result.put("status", "ok");

        return result;
    }

    public Map<String, Object> createVerifyInfo(VerifyInfo verifyInfo) throws Exception {

        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] bytes = contract.submitTransaction("createVerifyInfo",
                verifyInfo.getAttributes(),
                verifyInfo.getHash_C(),
                verifyInfo.getPhi_1(),
                verifyInfo.getPhi_2(),
                verifyInfo.getPhi_3(),
                verifyInfo.getW_f(),
                verifyInfo.getW_beta(),
                verifyInfo.getW_o(),
                verifyInfo.getW_ou(),
                verifyInfo.getW_k(),
                verifyInfo.getW_u(),
                String.valueOf(verifyInfo.getMsg()),
                verifyInfo.getCredAgg()
                );

        result.put("payload", StringUtils.newStringUtf8(bytes));
        result.put("status", "ok");
        return result;
    }

    //调用链上验证函数
    public Map<String,Object> Verify_tx(VerifyInfo verifyInfo) throws Exception {
        Map<String,Object> result=Maps.newConcurrentMap();

        byte[] bytes=contract.evaluateTransaction("Verify_tx",
                verifyInfo.getAttributes(),
                verifyInfo.getHash_C(),
                verifyInfo.getPhi_1(),
                verifyInfo.getPhi_2(),
                verifyInfo.getPhi_3(),
                verifyInfo.getW_f(),
                verifyInfo.getW_beta(),
                verifyInfo.getW_o(),
                verifyInfo.getW_ou(),
                verifyInfo.getW_k(),
                verifyInfo.getW_u(),
                String.valueOf(verifyInfo.getMsg()),
                verifyInfo.getCredAgg()
                );

        result.put("payload", StringUtils.newStringUtf8(bytes));
        //如果通过验证，则将数据上传到区块链中
        if(StringUtils.newStringUtf8(bytes)=="true")
            return createVerifyInfo(verifyInfo);
        else{
            result.put("status", "error");
            return result;
        }

    }

}
