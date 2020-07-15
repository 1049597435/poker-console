package com.tzx.websocket.ddz.util;

import com.alibaba.fastjson.JSONArray;
import com.tzx.websocket.ddz.pojo.Card;
import com.tzx.websocket.ddz.pojo.ChannelBean;
import com.tzx.websocket.ddz.pojo.MessageBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.websocket.ddz.util
 * @ClassName: MyUtil
 * @Description:
 * @Author: 唐志翔
 * @Date: 2020/6/30 0030 18:16
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public class MyUtil {

    /**手牌初始化*/
    private static final List<String> cardList = new ArrayList<String>(){
        {
            add(Card.A.code());
            add(Card.A.code());
            add(Card.A.code());
            add(Card.A.code());
            add(Card.TWO.code());
            add(Card.TWO.code());
            add(Card.TWO.code());
            add(Card.TWO.code());
            add(Card.THREE.code());
            add(Card.THREE.code());
            add(Card.THREE.code());
            add(Card.THREE.code());
            add(Card.FOUR.code());
            add(Card.FOUR.code());
            add(Card.FOUR.code());
            add(Card.FOUR.code());
            add(Card.FIVE.code());
            add(Card.FIVE.code());
            add(Card.FIVE.code());
            add(Card.FIVE.code());
            add(Card.SIX.code());
            add(Card.SIX.code());
            add(Card.SIX.code());
            add(Card.SIX.code());
            add(Card.SEVEN.code());
            add(Card.SEVEN.code());
            add(Card.SEVEN.code());
            add(Card.SEVEN.code());
            add(Card.EIGHT.code());
            add(Card.EIGHT.code());
            add(Card.EIGHT.code());
            add(Card.EIGHT.code());
            add(Card.NINE.code());
            add(Card.NINE.code());
            add(Card.NINE.code());
            add(Card.NINE.code());
            add(Card.TEN.code());
            add(Card.TEN.code());
            add(Card.TEN.code());
            add(Card.TEN.code());
            add(Card.J.code());
            add(Card.J.code());
            add(Card.J.code());
            add(Card.J.code());
            add(Card.Q.code());
            add(Card.Q.code());
            add(Card.Q.code());
            add(Card.Q.code());
            add(Card.K.code());
            add(Card.K.code());
            add(Card.K.code());
            add(Card.K.code());
            add(Card.MIN_KING.code());
            add(Card.MAX_KING.code());
        }
    };

    /**
     * null值校验
     * @param s
     * @return
     */
    public static boolean isEmpty(Object s){
        if(s instanceof String){
            return s == null || "".equals(s);
        }else{
            return s == null;
        }
    }

    /**
     * 不为NULL值
     * @param s
     * @return
     */
    public static boolean isNotEmpty(Object s){
        return !isEmpty(s);
    }

    /**
     * 发送信息
     * @param channel
     * @param msg
     */
    public static void send(Channel channel, MessageBean msg){
        try{
            final ByteBuf text = channel.alloc().buffer(8);
            text.writeBytes(msg.isJson().getBytes());
            //客户端直接发送请求数据到服务端
            channel.writeAndFlush(text);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 服务器群发消息
     * @param msg
     * @param channelList
     */
    public static void sendAll(String msg, List<ChannelBean> channelList){
        sendAll(new MessageBean(CmdUtil.NORMAL_RESPONSE,msg),channelList);
    }

    /**
     * 服务器群发消息
     * @param msg
     * @param channelList
     */
    public static void sendAll(MessageBean msg, List<ChannelBean> channelList){
        if(channelList.size() == 0){
            System.out.println("还没有连接的用户");
            return;
        }
        for (ChannelBean bean : channelList) {
            final ByteBuf text = bean.getChannel().alloc().buffer(8);
            text.writeBytes(msg.isJson().getBytes());
            //客户端直接发送请求数据到服务端
            bean.getChannel().writeAndFlush(text);
            System.out.println(LoginUtil.isLogin(bean.getChannel())+bean.getName()+"发送成功!");
        }
    }

    /**
     * 重置登录用户的索引
     * @param channelList
     * @param index
     */
    public static void channelRemove(List<ChannelBean> channelList , Integer index){
        for (int i = index; i < channelList.size(); i++) {
            LoginUtil.markAsLogin(channelList.get(i).getChannel(),i);
        }
    }

    /**
     * 是否全部准备好了
     * @param channelList
     * @return
     */
    public static boolean isReadyAll(List<ChannelBean> channelList){
        if(channelList.size() != 3){
            return false;
        }
        boolean result = true;
        for (ChannelBean channelBean : channelList) {
            if(!channelBean.isReady()){
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * 随机先手叫分
     * @return
     */
    public static int randomBid(){
        return new Random().nextInt(3);
    }

    /**洗牌*/
    public static List<String> randomHand(){
        List<String> result = cardList;
        Collections.shuffle(result);
        Collections.shuffle(result);
        Collections.shuffle(result);
        return result;
    }

    /**
     * 叫分校验
     * @param text
     * @return
     */
    public static boolean landVerify(String text,Integer landNum){
        if(!Pattern.matches("^[0-3]$", text)){
            return false;
        }
        if("0".equals(text)){
            return true;
        }
        return Integer.parseInt(text) > landNum;
    }

    /**
     * 首出牌校验
     * @return
     */
    public static int handVerify(List<String> pool,List<String> hands){
        int result = -1;
        if(!containVerify(pool,hands)){
            return result;
        }
        int len = hands.size();
        handSort(hands);
        if(len == 1){
            result = Type.SINGLE.getCode();
        }else if(len == 2){
            if(hands.get(0).equals(hands.get(1))){
                result = Type.DOUBLE.getCode();
            }else if(computeNum(hands,Card.MIN_KING.code()) == 1 && computeNum(hands,Card.MAX_KING.code()) == 1){
                result = Type.ROCKET.getCode();
            }
        }else if(len == 3){
            if(computeNum(hands,hands.get(0)) == len){
                result = Type.THREE.getCode();
            }
        }else if(len == 4){
            if(computeNum(hands,hands.get(0)) == len){
                result = Type.BOMB.getCode();
            }else{
                Set<String> set = new HashSet<>(hands);
                int num = computeNum(hands, set.iterator().next());
                if(set.size() == 2 && (num == 1 || num == 3)){
                    result = Type.THREE_ONE.getCode();
                }
            }
        }else if(len == 5){
            Set<String> set = new HashSet<>(hands);
            if(set.size() == len && straightVerify(hands)){
                result = Type.STRAIGHT.getCode();
            }else if(set.size() == 2){
                int num = computeNum(hands, set.iterator().next());
                if(num == 2 || num == 3){
                    result = Type.THREE_TOW.getCode();
                }
            }
        }else{
            Set<String> set = new HashSet<>(hands);
            if(set.size() == len && straightVerify(hands)){
                result = Type.STRAIGHT.getCode();
            }
            if(result == -1 && (len%2) == 0 && (set.size()*2) == len){
                boolean isDoubles = true;
                for (String s : set) {
                    if(computeNum(hands,s) != 2){
                        isDoubles = false;
                        break;
                    }
                }
                if (isDoubles){
                    List<String> arry = new ArrayList<>(set);
                    handSort(arry);
                    if(straightVerify(arry)){
                        result = Type.DOUBLES.getCode();
                    }
                }
            }
            if(result == -1 && (len == 6 || len == 8) && set.size() == 3){
                if(len == 6){
                    boolean fourOne = false;
                    for (String s : set) {
                        if(computeNum(hands,s) == 4){
                            fourOne = true;
                            break;
                        }
                    }
                    if(fourOne){
                        result = Type.FOUR_ONE.getCode();
                    }
                }else if(len == 8){
                    boolean fourTow1 = false;
                    boolean fourTow2 = false;
                    for (String s : set) {
                        int num = computeNum(hands, s);
                        if(num == 4){
                            fourTow1 = true;
                        }else if(num == 2){
                            fourTow2 = true;
                        }
                    }
                    if(fourTow1 && fourTow2){
                        result = Type.FOUR_TOW.getCode();
                    }
                }
            }
            if(result == -1 && (len%3) == 0 && (set.size()*3) == len){
                boolean isThrees = true;
                for (String s : set) {
                    if(computeNum(hands,s) != 3){
                        isThrees = false;
                        break;
                    }
                }
                if (isThrees){
                    List<String> arry = new ArrayList<>(set);
                    handSort(arry);
                    if(straightVerify(arry)){
                        result = Type.THREES.getCode();
                    }
                }
            }
            if(result == -1 && (len%4 == 0) ){
                int index = len / 4;
                int t = 0;
                List<String> arry = new ArrayList<>();
                for (String s : set) {
                    if(computeNum(hands,s) == 3){
                        t++;
                        arry.add(s);
                    }
                }
                if(index == t){
                    handSort(arry);
                    if(straightVerify(arry)){
                        result = Type.THREES_ONE.getCode();
                    }
                }
            }
            if(result == -1 && (len%5 == 0) && (set.size() == (len / 5 * 2)) ){
                int index = len / 5;
                int t = 0;
                int w = 0;
                List<String> arry = new ArrayList<>();
                for (String s : set) {
                    int num = computeNum(hands, s);
                    if(num == 3){
                        t++;
                        arry.add(s);
                    }else if(num == 2){
                        w++;
                    }
                }
                if(index == t && index == w){
                    handSort(arry);
                    if(straightVerify(arry)){
                        result = Type.THREES_TOW.getCode();
                    }
                }
            }
        }
        return result;
    }

    /**
     * 顺子校验
     * @param hands
     * @return
     */
    private static boolean straightVerify(List<String> hands){
        int start = Card.isCard(hands.get(0)).index();
        int end = Card.isCard(hands.get(hands.size()-1)).index();
        int si = start - end + 1;
        return start<= Card.A.index() && end >= Card.THREE.index() && si == hands.size();
    }

    /**输入校验*/
    private static boolean strVerify(List<String> str){
        if(str.size() == 0){
            return false;
        }
        for (String s : str) {
            if(Card.isCard(s) == null){
                return false;
            }
        }
        return true;
    }

    /**
     * 验证list1是否全部包含list2
     * @param list1
     * @param list2
     * @return
     */
    public static boolean containVerify(List<String> list1,List<String> list2){
        if(!strVerify(list2)){
            return false;
        }
        boolean result = true;
        Set<String> set = new HashSet<>(list2);
        for (String s : set){
            int i = computeNum(list1, s);
            int j = computeNum(list2, s);
            if(j>i){
                result =  false;
                break;
            }
        }
        return result;
    }


    /**
     * 压牌校验
     */
    public static int gameVerify(List<String> hands,List<String> oldHands,Integer type,List<String> pool){
        int result = -1;
        int handType = handVerify(pool, hands);
        if(handType == -1){
            return result;
        }
        if(type == handType){
            if(Type.SINGLE.isType(type) || Type.DOUBLE.isType(type) || Type.THREE.isType(type) || Type.BOMB.isType(type)) {
                if (Card.isCard(hands.get(0)).index() > Card.isCard(oldHands.get(0)).index()) {
                    result = handType;
                }
            }else if(Type.THREE_ONE.isType(type) || Type.THREE_TOW.isType(type)){
                Set<String> set = new HashSet<>(hands);
                Set<String> oldSet = new HashSet<>(oldHands);
                Card card = null;
                Card oldCard = null;
                for (String s : set){
                    if(computeNum(hands,s) == 3){
                        card = Card.isCard(s);
                    }
                }
                for (String s : oldSet){
                    if(computeNum(oldHands,s) == 3){
                        oldCard = Card.isCard(s);
                    }
                }
                if(card.index() > oldCard.index()){
                    result = handType;
                }
            }else if(Type.THREES_ONE.isType(type) || Type.THREES_TOW.isType(type)){
                Set<String> set = new HashSet<>(hands);
                Set<String> oldSet = new HashSet<>(oldHands);
                List<String> list1 = new ArrayList<>();
                List<String> list2 = new ArrayList<>();
                for (String s : set){
                    if(computeNum(hands,s) == 3){
                        list1.add(s);
                    }
                }
                for (String s : oldSet){
                    if(computeNum(oldHands,s) == 3){
                        list2.add(s);
                    }
                }
                handSort(list1);
                handSort(list2);
                if(list1.size() == list2.size() && Card.isCard(list1.get(0)).index() > Card.isCard(list2.get(0)).index()){
                    result = handType;
                }
            }else if(Type.FOUR_ONE.isType(type) || Type.FOUR_TOW.isType(type)){
                Set<String> set = new HashSet<>(hands);
                Set<String> oldSet = new HashSet<>(oldHands);
                Card card = null;
                Card oldCard = null;
                for (String s : set){
                    if(computeNum(hands,s) == 4){
                        card = Card.isCard(s);
                    }
                }
                for (String s : oldSet){
                    if(computeNum(oldHands,s) == 4){
                        oldCard = Card.isCard(s);
                    }
                }
                if(card.index() > oldCard.index()){
                    result = handType;
                }
            }else if(Type.STRAIGHT.isType(type) || Type.DOUBLES.isType(type) || Type.THREES.isType(type)){
                if(oldHands.size() == hands.size() && Card.isCard(hands.get(0)).index() > Card.isCard(oldHands.get(0)).index()){
                    result = handType;
                }
            }
        }else if(typeGrade(type) < typeGrade(handType)){
            result = handType;
        }
        return result;
    }

    private static int typeGrade(int type){
        int result = 0;
        if(type == Type.BOMB.getCode()){
            result = 1;
        }else if(type == Type.ROCKET.getCode()){
            result = 2;
        }
        return result;
    }

    /**
     * 下一个玩家索引
     * @param index
     * @return
     */
    public static int lastIndex(Integer index){
        if(index == 2){
            return 0;
        }else{
            return index + 1;
        }
    }

    /**
     * 获取叫分可选项
     * @param last
     * @return
     */
    public static String landPack(Integer last){
        List<Integer> list = new ArrayList<>();
        list.add(0);
        last++;
        for (int i = last; i <= 3; i++) {
            list.add(i);
        }
        return JSONArray.toJSONString(list);
    }

    /**
     * 玩家手牌减扣
     * @param hand  手牌
     * @param face  出牌
     */
    public static void cardFaceSub(List<String> hand,List<String> face){
        for (String s : face) {
            Iterator<String> i = hand.iterator();
            hands:while (i.hasNext()){
                String next = i.next();
                if(next.equals(s)){
                    i.remove();
                    break hands;
                }
            }
        }
    }

    /**
     * 游戏结束
     * @param channelBeanList
     */
    public static void gameOver(List<ChannelBean> channelBeanList,String mes){
        //暴露手牌
        for (ChannelBean channelBean : channelBeanList) {
            mes += channelBean.getName()+"的手牌为:"+JSONArray.toJSONString(channelBean.getHand())+"\n";
            //重置游戏状态
            channelBean.setReady(false);
        }
       sendAll(new MessageBean(CmdUtil.READY,mes),channelBeanList);
    }

    /**
     * 手牌排序
     * @param hand
     */
    public static void handSort(List<String> hand){
        Collections.sort(hand, (s1,s2) -> {
               if(s1.equals(s2)){
                   return 0;
               }else{
                   Card card1 = Card.isCard(s1);
                   Card card2 = Card.isCard(s2);
                   return card1.index() < card2.index() ? 1 : -1;
               }
        });
    }

    public static String hanSize(List<ChannelBean> channelBeanList){
        String result = ",";
        for (ChannelBean channelBean : channelBeanList) {
            result += channelBean.getName()+"("+channelBean.getHand().size()+")"+",";
        }
        return result;
    }

    /**
     * 计算数组中包含该元素的个数
     * @param hands
     * @param val
     * @return
     */
    private static int computeNum(List<String> hands,String val){
        return Collections.frequency(hands, val);
    }
}
