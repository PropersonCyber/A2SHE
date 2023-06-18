package org.example;

import lombok.Data;

/**
 * @author Administrator
 * @date2023/6/9 0009 11:13
 */
@Data
/**
*@author Yangpeng
*@Description
*@Date 2023/6/11 0011 10:36
 *
 * 属性配置实体应该要有初始化的值，application-test-network-org1.properties中对应的
*/
public class HyperLedgerFabricProperties {

    String mspId;

    String networkConnectionConfigPath;

    String certificatePath;

    String privateKeyPath;

    String tlsCertPath;

    String channel;
}
