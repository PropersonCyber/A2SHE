package org.example;

import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import java.util.Properties;

/**
 * @author Administrator
 * @date2023/6/9 0009 13:49
 */
public class FabricCAConfiguration {
    public HFCAClient hfcaClient() throws Exception {

        Properties properties = new Properties();
        properties.setProperty("pemFile" , "D:\\Code\\Fabric\\src\\main\\resources\\ca-cert.pem");
        //链码本地IP地址
        HFCAClient hfcaClient = HFCAClient.createNewInstance("org1-int-ca", "https://192.168.0.105:7056", properties);

        hfcaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        return hfcaClient;
    }
}
