package org.example;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

/**
 * @author Administrator
 * @date2023/6/9 0009 10:14
 */
@DataType
@Data
@Accessors(chain = true)
public class UserMsg {

    @Property
    String PubID;

    @Property
    String TimeStamp;

    @Property
    String AuxInfo;
}
