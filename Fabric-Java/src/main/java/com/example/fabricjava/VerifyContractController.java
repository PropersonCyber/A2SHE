package com.example.fabricjava;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.GatewayException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Administrator
 * @date2023/6/19 0019 9:04
 */
@RestController
@RequestMapping("/verifycontract")
@Slf4j
@AllArgsConstructor
public class VerifyContractController {

    final Gateway gateway;

    final Contract contract;

    final HyperLedgerFabricProperties hyperLedgerFabricProperties;

    @GetMapping("/{key}")
    public Map<String, Object> queryVerifyInfoByKey(@PathVariable String key) throws GatewayException {

        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] resInfo = contract.evaluateTransaction("queryVerifyInfo", key);

        result.put("payload", StringUtils.newStringUtf8(resInfo));
        result.put("status", "ok");

        return result;
    }

    @GetMapping("/all")
    public Map<String, Object> queryAllVerifyInfo() throws GatewayException {

        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] resInfo = contract.evaluateTransaction("queryAllVerifyInfo");

        result.put("payload", StringUtils.newStringUtf8(resInfo));
        result.put("status", "ok");
        return result;
    }

    @PutMapping("/verify")
    public Map<String,Object> Verify(@RequestBody VerifyInfo verifyInfo) throws  Exception{
        Map<String,Object> result=Maps.newConcurrentMap();

        //查看执行时间
        long startTime=System.currentTimeMillis();
        byte[] bytes = contract.submitTransaction("Verify", verifyInfo.getCredAggRandom(),
                verifyInfo.getPhi_1(),
                verifyInfo.getPhi_2(),
                verifyInfo.getPhi_3(),
                verifyInfo.getS_n(),
                verifyInfo.getS_alpha(),
                verifyInfo.getS_u(),
                verifyInfo.getS_o(),
                verifyInfo.getS_beta(),
                verifyInfo.getS_gamma(),
                verifyInfo.getC(),
                verifyInfo.getAttributes(),
                verifyInfo.getMsg());
        long endTime=System.currentTimeMillis();
        long elapsedTime=endTime-startTime;
        System.out.println("Verify 执行时间："+elapsedTime+"ms");
        result.put("payload", StringUtils.newStringUtf8(bytes));
        result.put("status", "ok");
        return result;
    }
}
