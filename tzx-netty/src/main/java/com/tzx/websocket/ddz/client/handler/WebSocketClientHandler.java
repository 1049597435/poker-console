package com.tzx.websocket.ddz.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.websocket.ddz.client.handler
 * @ClassName: WebSocketClientHandler
 * @Description: 处理器
 * @Author: 唐志翔
 * @Date: 2020/6/30 0030 10:03
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public class WebSocketClientHandler extends SimpleChannelInboundHandler<String> {

    private final WebSocketClientHandshaker webSocketClientHandshaker;

    public WebSocketClientHandler(WebSocketClientHandshaker webSocketClientHandshaker) {
        this.webSocketClientHandshaker = webSocketClientHandshaker;
    }

    /**
     * 当客户端主动链接服务端的链接后，调用此方法
     *
     * @param channelHandlerContext ChannelHandlerContext
     */
    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        System.out.println("\n\t⌜⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓\n" +
                "\t├ [Mock 建立连接]\n" +
                "\t⌞⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓");

        Channel channel = channelHandlerContext.channel();
        // 握手
        webSocketClientHandshaker.handshake(channel);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String data) {
        System.out.println("接收到客户端的响应为:{}"+ data);
        //自定义处理消息
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("\n\t⌜⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓\n" +
                "\t├ [exception]: {}\n" +
                "\t⌞⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓"+cause.getMessage());
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        System.out.println("与服务器端断开连接");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.flush();
    }
}
