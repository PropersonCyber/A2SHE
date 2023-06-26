package com.experiment.anonyauth.Param;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

@Data
/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 16:09
 */
public class AggregationCredential {
    private Element aggCredential;
    private String[][] attributes;
    private Element[][] ivkList;
}
