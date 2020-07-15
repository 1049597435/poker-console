package com.tzx.websocket.ddz.pojo;

import com.alibaba.fastjson.JSONObject;
import com.tzx.websocket.ddz.util.CmdUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.websocket.ddz.pojo
 * @ClassName: Message
 * @Description: 消息实体
 * @Author: 唐志翔
 * @Date: 2020/6/30 0030 17:45
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public class MessageBean {

    /**指令*/
    private Integer cmd;
    /**消息文本*/
    private String text;
    /**牌堆顶*/
    private List<String> cardFace = new ArrayList<>();
    /**牌堆顶类型*/
    private Integer type;
    /**最低叫分*/
    private Integer landNum;
    /**玩家手牌*/
    private List<String> hand = new ArrayList<>();

    public MessageBean() {
    }

    public MessageBean(String text) {
        this.text = text;
        this.cmd = CmdUtil.NORMAL_RESPONSE.getCmd();
    }

    public MessageBean(CmdUtil cmd, String text) {
        this.cmd = cmd.getCmd();
        this.text = text;
    }

    public MessageBean(CmdUtil cmd, String text,List<String> hand) {
        this.cmd = cmd.getCmd();
        this.text = text;
        this.hand = hand;
    }

    public MessageBean(CmdUtil cmd, String text,Integer landNum) {
        this.cmd = cmd.getCmd();
        this.text = text;
        this.landNum = landNum;
    }

    public MessageBean(CmdUtil cmd, String text, List<String> cardFace,Integer type) {
        this.cmd = cmd.getCmd();
        this.text = text;
        this.cardFace = cardFace;
        this.type = type;
    }
    public MessageBean(CmdUtil cmd, String text, List<String> cardFace,Integer type,List<String> hand) {
        this.cmd = cmd.getCmd();
        this.text = text;
        this.cardFace = cardFace;
        this.type = type;
        this.hand = hand;
    }

    public List<String> getHand() {
        return hand;
    }

    public void setHand(List<String> hand) {
        this.hand = hand;
    }

    public Integer getLandNum() {
        return landNum;
    }

    public void setLandNum(Integer landNum) {
        this.landNum = landNum;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<String> getCardFace() {
        return cardFace;
    }

    public void setCardFace(List<String> cardFace) {
        this.cardFace = cardFace;
    }

    public Integer getCmd() {
        return cmd;
    }

    public void setCmd(Integer cmd) {
        this.cmd = cmd;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String isJson(){
        return JSONObject.toJSONString(this);
    }

    public static MessageBean isMessageBean(String json){
        return JSONObject.parseObject(json,MessageBean.class);
    }

    public boolean isCmdUtil(CmdUtil cmdUtil){
        return cmdUtil.getCmd().equals(this.cmd);
    }
}
