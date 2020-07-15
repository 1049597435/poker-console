package com.tzx.websocket.ddz.util;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.websocket.ddz.util
 * @ClassName: 指令工具
 * @Description:
 * @Author: 唐志翔
 * @Date: 2020/6/30 0030 16:34
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum  CmdUtil {
    /**开始游戏*/
    READY(1),
    /**发送消息全部*/
    MESSAGE_ALL(2),
    /**指定人发送消息*/
    MESSAGE(3),
    /**抢地主*/
    LAND(4),
    /**要不起*/
    GAME(5),
    /**出牌/压牌*/
    FIRST(6),
    LOGIN_REQUEST(99),
    LOGIN_RESPONSE(98),
    NORMAL_REQUEST(97),
    NORMAL_RESPONSE(96);

    CmdUtil(Integer cmd) {
        this.cmd = cmd;
    }

    private Integer cmd;

    public Integer getCmd() {
        return cmd;
    }
}
