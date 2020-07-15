package com.tzx.websocket.ddz.client.handler;

import com.tzx.websocket.ddz.util.CmdUtil;
import com.tzx.websocket.ddz.pojo.MessageBean;
import com.tzx.websocket.ddz.util.MyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.websocket.ddz.client.handler
 * @ClassName: TimeClientHandler
 * @Description:
 * @Author: 唐志翔
 * @Date: 2020/6/30 0030 14:14
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    /**消息缓存*/
    private static String cache = null;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        try {
            byte[] result = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(result);
            String cmd = new String(result);
//            System.out.println("Debug----"+cmd);
            int ind = 0;
            while ((ind = cmd.indexOf("}")) > -1){
                String mes = cmd.substring(0,ind+1);
                if(cache != null){
                    mes = cache + mes;
                    cache = null;
                }
                MessageBean message = MessageBean.isMessageBean(mes);
                channelHandle(ctx,message);
                cmd = cmd.substring(ind+1);
            }
            if(cmd.length() > 0){
                cache = cmd;
            }
        } finally {
            byteBuf.release();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("连接成功开始登录...");
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入姓名来登录!");
        String s = scanner.nextLine();
        MessageBean messageBean = new MessageBean(CmdUtil.LOGIN_REQUEST,s);
        final ByteBuf text = ctx.alloc().buffer(8);
        text.writeBytes(messageBean.isJson().getBytes());
        ctx.writeAndFlush(text);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
        System.out.println("TimeClientHandler:exceptionCaught");
    }

    /**
     * 消息处理
     * @param ctx
     * @param bean
     */
    private void channelHandle(ChannelHandlerContext ctx,MessageBean bean){
        if(bean.isCmdUtil(CmdUtil.LOGIN_RESPONSE)){
            System.out.println("登录响应:"+bean.getText());
        }else if (bean.isCmdUtil(CmdUtil.NORMAL_RESPONSE)){
            System.out.println("游戏提示:"+bean.getText());
        }else if(bean.isCmdUtil(CmdUtil.READY)){
            System.out.println(bean.getText());
            Scanner scanner = new Scanner(System.in);
            System.out.println("准备好了就回车开始哦~");
            String msg = scanner.nextLine();
            MyUtil.send(ctx.channel(), new MessageBean(CmdUtil.READY,msg));
        }else if(bean.isCmdUtil(CmdUtil.LAND)){
            System.out.println(bean.getText()+",请叫分");
            Scanner scanner = new Scanner(System.in);
            String msg = scanner.nextLine();
            while (!MyUtil.landVerify(msg,bean.getLandNum())){
                System.out.println(bean.getText()+",请正确输入选项 Thanks");
                msg = scanner.nextLine();
            }
            MyUtil.send(ctx.channel(), new MessageBean(CmdUtil.LAND,msg));
        }else if(bean.isCmdUtil(CmdUtil.FIRST)){
            System.out.println(bean.getText()+",请出牌:");
            Scanner scanner = new Scanner(System.in);
            String msg = scanner.nextLine().toUpperCase();
            List<String> hands = Arrays.asList(msg.split(""));
            int type = -1;
            while ((type = MyUtil.handVerify(bean.getHand(),hands)) < 0){
                System.out.println(bean.getText()+",请规范出牌 Thanks");
                msg = scanner.nextLine().toUpperCase();
                hands = Arrays.asList(msg.split(""));
            }
            MyUtil.send(ctx.channel(),new MessageBean(CmdUtil.FIRST,"",hands,type));
        }else if(bean.isCmdUtil(CmdUtil.GAME)){
            System.out.println(bean.getText()+"(回车为不要),请压牌:");
            Scanner scanner = new Scanner(System.in);
            String msg = scanner.nextLine().toUpperCase();
            if("".equals(msg)){
                MyUtil.send(ctx.channel(),new MessageBean(CmdUtil.GAME,msg));
                return;
            }
            List<String> hands = Arrays.asList(msg.split(""));
            boolean zero = true;
            int type;
            while ((type = MyUtil.gameVerify(hands,bean.getCardFace(),bean.getType(),bean.getHand())) < 0){
                System.out.println(bean.getText()+",请规范出牌且出的牌要大过上家的牌哦~");
                msg = scanner.nextLine().toUpperCase();
                if("".equals(msg)){
                    MyUtil.send(ctx.channel(),new MessageBean(CmdUtil.GAME,msg));
                    zero = false;
                    break;
                }
                hands = Arrays.asList(msg.split(""));
            }
            if(zero){
                MyUtil.send(ctx.channel(),new MessageBean(CmdUtil.FIRST,"",hands,type));
            }
        }
    }
}
