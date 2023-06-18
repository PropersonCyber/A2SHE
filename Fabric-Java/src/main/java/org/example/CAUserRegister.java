package org.example;

import lombok.Data;

import java.util.List;

/**
 * @author Administrator
 * @date2023/6/9 0009 13:54
 */
@Data
public class CAUserRegister {
    String id;

    String secret;

    List<CAUserAttribute> attrs;
}
