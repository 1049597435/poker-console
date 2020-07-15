package com.tzx.websocket.ddz.util;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.websocket.ddz.util
 * @ClassName: LoginUtil
 * @Description:
 * @Author: 唐志翔
 * @Date: 2020/6/30 0030 18:09
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public class LoginUtil {
    public static void markAsLogin(Channel channel,Integer index){
        channel.attr(Attributes.LOGIN).set(index);
    }

    public static Integer isLogin(Channel channel){
        Attribute<Integer> attr = channel.attr(Attributes.LOGIN);
        return attr.get();
    }
}
