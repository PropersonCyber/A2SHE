package com.experiment.anonyauth.Entity.User;

import com.experiment.anonyauth.Tool.ElementStringConverter;
import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

/**
 * @author PerpersonCyber
 * @date2023/6/26 0026 15:40
 */
@Data
public class UserInfo {
    private Long id;
    private Element rToken;
    private Element nodeValue;
    private Element rv;
    private Element fr;
    private Element tToken;

    public static UserInfo fromVo(UserInfoVo userInfoVo){
        UserInfo userInfo = new UserInfo();
        ElementStringConverter.stringToElement(userInfoVo,userInfo);
        userInfo.setId(userInfo.getId());
        return userInfo;
    }

    public static UserInfoVo toVo(UserInfo userInfo){
        UserInfoVo userInfoVo = new UserInfoVo();
        ElementStringConverter.elementToString(userInfo,userInfoVo);
        userInfoVo.setId(userInfo.getId());
        return userInfoVo;
    }
}
