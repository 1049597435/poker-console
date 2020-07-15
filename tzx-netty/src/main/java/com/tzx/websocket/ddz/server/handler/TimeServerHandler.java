package com.tzx.websocket.ddz.server.handler;

import com.alibaba.fastjson.JSONArray;
import com.tzx.websocket.ddz.pojo.ChannelBean;
import com.tzx.websocket.ddz.pojo.Game;
import com.tzx.websocket.ddz.util.CmdUtil;
import com.tzx.websocket.ddz.pojo.MessageBean;
import com.tzx.websocket.ddz.util.LoginUtil;
import com.tzx.websocket.ddz.util.MyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.*;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.websocket.ddz.server.handler
 * @ClassName: TimeServerHandler
 * @Description:
 * @Author: 唐志翔
 * @Date: 2020/6/30 0030 14:09
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter  {

    private List<ChannelBean> channelList;

    private Game game;

    public TimeServerHandler(List<ChannelBean> channelList,Game game) {
        this.channelList = channelList;
        this.game = game;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)
        final ByteBuf byteBuf = ctx.alloc().buffer(8);
        MessageBean req = new MessageBean(CmdUtil.READY,"欢迎登录");
        byteBuf.writeBytes(req.isJson().getBytes());
        ctx.writeAndFlush(byteBuf,ctx.channel().newPromise());
        System.out.println("TimeServerHandler，有新连接");
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        cause.printStackTrace();
        Integer index = LoginUtil.isLogin(ctx.channel());
        ChannelBean bean = channelList.get(index);
        System.out.println(bean.getName()+"已经失去连接");
        Iterator<ChannelBean> it = channelList.iterator();
        while(it.hasNext()){
            ChannelBean str = it.next();
            if(bean.getName().equals(str.getName())){
                it.remove();
            }
        }
        MyUtil.channelRemove(channelList,index);
        MyUtil.gameOver(channelList,bean.getName()+"已经失去连接\n");
        game.setStart(false);
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Discard the received data silently.
        ByteBuf byteBuf = (ByteBuf) msg;
        String resStr = null;
        try {
            byte[] result = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(result);
            String text = new String(result);
            MessageBean res = MessageBean.isMessageBean(text);
            resStr = messageHandle(res,ctx.channel());
        } finally {
            byteBuf.release();
        }
        if(MyUtil.isNotEmpty(resStr)){
            final ByteBuf byteBuf2 = ctx.alloc().buffer(8);
            byteBuf2.writeBytes(byteBuf);
            ctx.channel().writeAndFlush(resStr.getBytes(),ctx.channel().newPromise());
        }
    }

    /**
     * 消息处理
     * @param bean
     * @param channel
     * @return
     */
    private String messageHandle(MessageBean bean,Channel channel){
        String result = null;
        Integer index = LoginUtil.isLogin(channel);
        if(bean.isCmdUtil(CmdUtil.LOGIN_REQUEST)){
            //执行登录绑定操作
            if(channelList.size() < 3){
                channelList.add(new ChannelBean(channel,bean.getText()));
                LoginUtil.markAsLogin(channel,channelList.size()-1);
                result = new MessageBean(CmdUtil.READY,"登录成功!").isJson();
                System.out.println(bean.getText()+"登录成功!");
            }else{
                result = new MessageBean(CmdUtil.LOGIN_RESPONSE,"由于技术原因有限,只支持三人游玩,谢谢合作!").isJson();
            }
        }else if(bean.isCmdUtil(CmdUtil.NORMAL_REQUEST)){
            //判断进行登录操作
            if(MyUtil.isEmpty(index)){
                //未登录则发送登录请求
                result = new MessageBean(CmdUtil.LOGIN_REQUEST,"请输入您的名字哦~").isJson();
                System.out.println("收到未登录用户发送的消息:"+bean.getText());
            }else{
                //登录用户的信息
                System.out.println(channelList.get(index).getName()+":" + bean.getText());
            }
        }else if(bean.isCmdUtil(CmdUtil.READY)){
            //准备开始
            channelList.get(index).setReady(true);
            if(MyUtil.isReadyAll(channelList)){
                gameStart("游戏开始啦");
            }else{
                String ss = "";
                if(channelList.size() == 3){
                    ss = "还有玩家未准备";
                }else{
                    ss = "还缺少" +(3 - channelList.size())+"名玩家";
                }
                ss += ",请稍候游戏开始。";
                MyUtil.send(channelList.get(index).getChannel(),new MessageBean(CmdUtil.NORMAL_RESPONSE,ss));
            }
        }else if(bean.isCmdUtil(CmdUtil.LAND)){
            //抢地主环节
            int val = Integer.parseInt(bean.getText());
            int lastIndex = MyUtil.lastIndex(index);
            MyUtil.sendAll(channelList.get(index).getName()+"叫分"+val+"分",channelList);
            if(val == 0){
                if(lastIndex == game.getIndex()){
                    if(game.getLand() > 0){
                        landlordIsFix(game.getLandIndex());
                    }else{
                        gameStart("重新开始游戏!");
                    }
                }else{
                    MyUtil.send(channelList.get(lastIndex).getChannel(),new MessageBean(CmdUtil.LAND,"您的可选项为"+MyUtil.landPack(game.getLand())+"0为不叫",game.getLand()));
                }
            }else if(val == 3 || lastIndex == game.getIndex()){
                landlordIsFix(index);
            }else {
                game.setLandIndex(index);
                game.setLand(val);
                MyUtil.send(channelList.get(lastIndex).getChannel(),new MessageBean(CmdUtil.LAND,"您的可选项为"+MyUtil.landPack(val)+"0为不叫,请叫分",val));
            }
        }else if(bean.isCmdUtil(CmdUtil.FIRST)){
            //首出 或者 压上牌了
            game.setIndex(index);
            game.setCardFace(bean.getCardFace());
            game.setType(bean.getType());
            //玩家手牌减扣
            MyUtil.cardFaceSub(channelList.get(index).getHand(),bean.getCardFace());
            //判断是否最后一次出牌 是 则结束游戏
            if(channelList.get(index).getHand().size() == 0){
                //牌出完  结束游戏
                String mes = channelList.get(index).getName()+"出牌:"+JSONArray.toJSONString(game.getCardFace())+",手牌已经出完了~,"+(channelList.get(index).isLandlord()?"地主胜利":"平民胜利")+"\n";
                MyUtil.gameOver(channelList,mes);
                game.setStart(false);
            }else{
                //下家
                int lastIndex = MyUtil.lastIndex(index);
                //通报出牌
                MyUtil.sendAll(channelList.get(index).getName()+"出牌:"+JSONArray.toJSONString(game.getCardFace())+MyUtil.hanSize(channelList)+channelList.get(lastIndex).getName()+"请准备",channelList);
                //让下家开始压牌
                String msg = "您的牌为"+JSONArray.toJSONString(channelList.get(lastIndex).getHand());
                MyUtil.send(channelList.get(lastIndex).getChannel(),new MessageBean(CmdUtil.GAME,msg,game.getCardFace(),game.getType(),channelList.get(lastIndex).getHand()));
            }
        }else if(bean.isCmdUtil(CmdUtil.GAME)){
            //要不起
            //下家
            int nextIndex =  MyUtil.lastIndex(index);
            String allMsg = "";
            MessageBean msg = null;
            if(nextIndex == game.getIndex()){
                allMsg = channelList.get(nextIndex).getName()+"开始新的回合~";
                msg = new MessageBean(CmdUtil.FIRST,"您的牌为"+JSONArray.toJSONString(channelList.get(nextIndex).getHand()),channelList.get(nextIndex).getHand());
            }else{
                allMsg = channelList.get(index).getName()+"PASS"+","+channelList.get(nextIndex).getName()+"请准备";
                msg = new MessageBean(CmdUtil.GAME,"您的牌为"+JSONArray.toJSONString(channelList.get(nextIndex).getHand()),game.getCardFace(),game.getType(),channelList.get(nextIndex).getHand());
            }
            //通报出牌
            MyUtil.sendAll(allMsg,channelList);
            //让下家开始压牌
            MyUtil.send(channelList.get(nextIndex).getChannel(),msg);
        }
        return result;
    }

    /**
     * 开始游戏
     */
    private void gameStart(String msg){
        //开始游戏
        game.setStart(true);
        MyUtil.sendAll(msg,channelList);
        //洗牌
        List<String> hand = MyUtil.randomHand();
        //发牌[
        for (int i = 0; i < channelList.size(); i++) {
            int start = (i * 17);
            int end = start + 17;
            List<String> list = new ArrayList<>();
            for (int j = start; j < end; j++) {
                list.add(hand.get(j));
            }
            MyUtil.handSort(list);
            channelList.get(i).setHand(list);
            MyUtil.send(channelList.get(i).getChannel(),new MessageBean("你的手牌收好啦:"+ JSONArray.toJSONString(list)));
        }
        //随机先手叫分
        int randomBid = MyUtil.randomBid();
        game.setIndex(randomBid);
        //三张地主牌
        List<String> cf = new ArrayList<>();
        cf.add(hand.get(53));
        cf.add(hand.get(52));
        cf.add(hand.get(51));
        game.setCardFace(cf);
        //设置最低分
        game.setLand(0);
        MyUtil.send(channelList.get(randomBid).getChannel(),new MessageBean(CmdUtil.LAND,"您的可选项为[0,1,2,3]0为不叫",game.getLand()));
    }

    /**
     * 地主确定 开始出牌
     * @param index
     */
    private void landlordIsFix(Integer index){
        //发放地主牌 并通知所有选手地主是谁
        String hs = JSONArray.toJSONString(game.getCardFace());
        channelList.get(index).getHand().addAll(game.getCardFace());
        MyUtil.handSort(channelList.get(index).getHand());
        channelList.get(index).setLandlord(true);
        MyUtil.sendAll("恭喜"+ channelList.get(index).getName()+"获得地主，地主牌为:"+hs,channelList);
        MyUtil.send(channelList.get(index).getChannel(),new MessageBean(CmdUtil.FIRST,"您的牌为:"+JSONArray.toJSONString(channelList.get(index).getHand()),channelList.get(index).getHand()));
    }
}