package com.tzx.websocket.ddz.client;

import com.tzx.websocket.ddz.client.handler.TimeClientHandler;
import com.tzx.websocket.ddz.client.handler.WebSocketClientHandler;
import com.tzx.websocket.ddz.pojo.MessageBean;
import com.tzx.websocket.ddz.util.CmdUtil;
import com.tzx.websocket.ddz.util.MyUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.websocket.ddz.client
 * @ClassName: WebSocketClient
 * @Description:
 * @Author: 唐志翔
 * @Date: 2020/6/30 0030 09:41
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public class WebSocketClient {

    private void run(String url){
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            URI uri = new URI(url);
            Bootstrap bootstrap = new Bootstrap();
            WebSocketClientHandler webSocketClientHandler = new WebSocketClientHandler(
                    WebSocketClientHandshakerFactory.newHandshaker(uri
                            , WebSocketVersion.V13
                            , null
                            , false
                            , new DefaultHttpHeaders()));
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).
                    handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 将请求与应答消息编码或者解码为HTTP消息
                            pipeline.addLast(new HttpClientCodec());
                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());
                            // 客户端Handler
                            pipeline.addLast("handler", webSocketClientHandler);
                        }
                    });
            ChannelFuture future = bootstrap.connect(uri.getHost(), uri.getPort()).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException | URISyntaxException e) {
            System.out.println("socket连接异常:{}"+e);
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void run1(String host,int port) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // (1)
            Bootstrap b = new Bootstrap();
            // (2)
            b.group(workerGroup);
            // (3)
            b.channel(NioSocketChannel.class);
            // (4)
            b.option(ChannelOption.TCP_NODELAY, true);
            b.option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(1024));
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeClientHandler());
                }
            });
            // Start the client.
            // (5)
            ChannelFuture future = b.connect(host, port).sync();
            future.addListener( f -> {
                Channel channel = ((ChannelFuture) f).channel();
                new Thread(() -> {
//                    Scanner scanner = new Scanner(System.in);
//                    while (true) {
//                        System.out.println("输入需要发送的信息~");
//                        String msg = scanner.nextLine();
//                        MyUtil.send(channel, msg);
//                    }
//                    Scanner scanner = new Scanner(System.in);
//                    System.out.println("准备好了就回车开始哦~");
//                    String msg = scanner.nextLine();
//                    MyUtil.send(channel, new MessageBean(CmdUtil.READY,msg));
                }).start();
            });
            // Wait until the connection is closed.
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        String uri = "ws://192.168.1.136:8080/websocket";
        WebSocketClient webSocketClient = new WebSocketClient();
        try {
            webSocketClient.run1("192.168.1.136",8089);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
