package com.tzx.websocket.ddz.util;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.websocket.ddz.util
 * @ClassName: Type
 * @Description:
 * @Author: 唐志翔
 * @Date: 2020/7/9 0009 18:03
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum Type {
    /**出单*/
    SINGLE(0),
    /**出对*/
    DOUBLE(1),
    /**三张不带*/
    THREE(2),
    /**三带一*/
    THREE_ONE(3),
    /**三带对*/
    THREE_TOW(4),
    /**炸弹*/
    BOMB(5),
    /**火箭*/
    ROCKET(6),
    /**连对*/
    DOUBLES(7),
    /**飞机不带*/
    THREES(8),
    /**飞机带单*/
    THREES_ONE(9),
    /**飞机带对*/
    THREES_TOW(10),
    /**四带两个单*/
    FOUR_ONE(11),
    /**四代两个对*/
    FOUR_TOW(12),
    /**顺子*/
    STRAIGHT(13);

    private int code;

    private Type(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public boolean isType(int type){
        return this.code == type;
    }
}
