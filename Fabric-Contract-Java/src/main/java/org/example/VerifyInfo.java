package org.example;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

/**
 * @author Administrator
 * @date2023/6/9 0009 9:45
 */
@DataType
@Data
@Accessors(chain = true)
//验证通过之后需要上传到区块链中的数据
public class VerifyInfo {

    //随机化后的聚合凭证
    @Property
    String CredAggRandom;

    //用户进出公共场所出示的凭证证明Pi
    @Property
    String Phi_1;
    @Property
    String Phi_2;
    @Property
    String Phi_3;
    @Property
    String S_n;
    @Property
    String S_alpha;
    @Property
    String S_u;
    @Property
    String S_o;
    @Property
    String S_beta;
    @Property
    String S_gamma;
    //Hash value
    @Property
    String C;

    //用户进出共场所信息msg
    @Property
    UserMsg msg;
}
