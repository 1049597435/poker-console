package com.tzx.websocket.ddz.util;

import io.netty.util.AttributeKey;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.websocket.ddz.util
 * @ClassName: Attributes
 * @Description:
 * @Author: 唐志翔
 * @Date: 2020/6/30 0030 18:12
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface Attributes {
    AttributeKey<Integer> LOGIN = AttributeKey.newInstance("login");
}
