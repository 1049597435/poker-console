package com.tzx.websocket.ddz.pojo;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.websocket.ddz.pojo
 * @ClassName: 用户表
 * @Description:
 * @Author: 唐志翔
 * @Date: 2020/7/2 0002 16:14
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ChannelBean {
    /**通信管道*/
    private Channel channel;
    /**用户名称*/
    private String name;
    /**是否准备好*/
    private boolean ready = false;
    /**手牌*/
    private List<String> hand = new CopyOnWriteArrayList<>();
    /**是否地主*/
    private boolean landlord = false;

    public boolean isLandlord() {
        return landlord;
    }

    public void setLandlord(boolean landlord) {
        this.landlord = landlord;
    }

    public List<String> getHand() {
        return hand;
    }

    public void setHand(List<String> hand) {
        this.hand = hand;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public ChannelBean(Channel channel) {
        this.channel = channel;
    }

    public ChannelBean(Channel channel, String name) {
        this.channel = channel;
        this.name = name;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getName() {
        return "<"+name+">";
    }

    public void setName(String name) {
        this.name = name;
    }
}
