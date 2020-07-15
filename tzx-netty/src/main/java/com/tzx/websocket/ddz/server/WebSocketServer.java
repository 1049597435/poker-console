package com.tzx.websocket.ddz.server;

import com.tzx.websocket.ddz.pojo.ChannelBean;
import com.tzx.websocket.ddz.pojo.Game;
import com.tzx.websocket.ddz.server.handler.TimeServerHandler;
import com.tzx.websocket.ddz.server.handler.WebSocketServerHandler;
import com.tzx.websocket.ddz.util.MyUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.websocket.ddz.server
 * @ClassName: WebSocketServer
 * @Description: 长连接服务器
 * @Author: 唐志翔
 * @Date: 2020/6/30 0030 09:28
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public class WebSocketServer {
    public void run1(int port) throws Exception{
        //创建两组线程，监听连接和工作
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            //Netty用于启动Nio服务端的启动类
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    //注册NioServerSocketChannel
                    .channel(NioServerSocketChannel.class)
                    //注册处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //用于Http请求的编码或者解码
                            pipeline.addLast("http-codec", new HttpServerCodec());
                            //把Http消息组成完整地HTTP消息
                            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                            //向客户端发送HTML5文件
                            pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                            //实际处理的Handler
                            pipeline.addLast("handler", new WebSocketServerHandler());
                        }
                    });
            Channel ch = b.bind(port).sync().channel();
            System.out.println("Web socket server started at port " + port + '.');
            System.out.println("Open your browser and navigate to http://localhost:" + port + '/');
            ch.closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void run(int port) throws Exception {
        // (1)
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // (2)
            ServerBootstrap b = new ServerBootstrap();
            //用户池
            List<ChannelBean> channelList = new CopyOnWriteArrayList<>();
            //游戏池
            Game game = new Game();
            b.group(bossGroup, workerGroup)
                    // (3)
                    .channel(NioServerSocketChannel.class)
                    // (4)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TimeServerHandler(channelList,game));
                        }
                    })
                    // (5)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // (6)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // (7)
            ChannelFuture f = b.bind(port).sync();
            f.addListener( future -> {
                System.out.println("启动成功!");
//                new Thread(() -> {
//                    Scanner scanner = new Scanner(System.in);
//                    while (true){
//                        System.out.println("输入需要群发的信息");
//                        String line = scanner.nextLine();
//                        MyUtil.sendAll(line,channelList);
//                    }
//                }).start();
            });
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8089;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new WebSocketServer().run(port);
    }
}
