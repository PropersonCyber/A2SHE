package com.experiment.anonyauth.Entity.User;

import lombok.Data;

@Data
/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 15:41
 */
public class UserInfoVo {
    private Long id;
    private String rToken;
    private String nodeValue;
    private String rv;
    private String fr;
    private String tToken;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFr() {
        return fr;
    }

    public void setFr(String fr) {
        this.fr = fr;
    }
}
