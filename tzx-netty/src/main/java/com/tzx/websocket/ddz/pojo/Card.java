package com.tzx.websocket.ddz.pojo;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.websocket.ddz.pojo
 * @ClassName: Card
 * @Description:
 * @Author: 唐志翔
 * @Date: 2020/7/9 0009 09:55
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum Card {
    A("A",11),
    TWO("2",12),
    THREE("3",0),
    FOUR("4",1),
    FIVE("5",2),
    SIX("6",3),
    SEVEN("7",4),
    EIGHT("8",5),
    NINE("9",6),
    TEN("0",7),
    J("J",8),
    Q("Q",9),
    K("K",10),
    MIN_KING("@",13),
    MAX_KING("$",14);


    private String code;
    private int index;

    private Card(String code,int index) {
        this.code = code;
        this.index = index;
    }

    public String code() {
        return code;
    }

    public int index() {
        return index;
    }

    public static Card isCard(String code){
        for (Card value : values()) {
            if (value.code().equals(code)){
                return value;
            }
        }
        return null;
    }
}
