package com.tzx.websocket.ddz.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.websocket.ddz.pojo
 * @ClassName: Game
 * @Description: 游戏池
 * @Author: 唐志翔
 * @Date: 2020/7/3 0003 15:05
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public class Game {
    /**是否已经开始游戏*/
    private boolean start = false;
    /**发起索引*/
    private Integer index;
    /**牌面*/
    private List<String> cardFace = new ArrayList<>();
    /**牌面类型*/
    private Integer type;
    /**局中最高分*/
    private Integer land;
    /**剧中最高分所属人*/
    private Integer landIndex;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getLandIndex() {
        return landIndex;
    }

    public void setLandIndex(Integer landIndex) {
        this.landIndex = landIndex;
    }

    public Integer getLand() {
        return land;
    }

    public void setLand(Integer land) {
        this.land = land;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public List<String> getCardFace() {
        return cardFace;
    }

    public void setCardFace(List<String> cardFace) {
        this.cardFace = cardFace;
    }
}
