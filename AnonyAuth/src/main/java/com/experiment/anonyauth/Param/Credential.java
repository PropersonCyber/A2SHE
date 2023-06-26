package com.experiment.anonyauth.Param;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

@Data
/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 16:07
 */
public class Credential {
    private Element credential;
    private String[] attributes;
    private Element[] ivkList;
}
