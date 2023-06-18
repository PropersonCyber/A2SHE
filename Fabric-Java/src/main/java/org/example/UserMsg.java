package org.example;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 * @date2023/6/9 0009 11:11
 */
@Data
@Accessors(chain = true)
public class UserMsg {

    String PubID;

    String TimeStamp;

    String AuxInfo;
}
