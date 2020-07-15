package com.tzx.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ServerChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.http
 * @ClassName: HttpServerInitializer
 * @Description: 责任链
 * @Author: 唐志翔
 * @Date: 2020/6/10 0010 15:04
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public class HttpServerInitializer extends ChannelInitializer<ServerChannel> {
    @Override
    protected void initChannel(ServerChannel serverChannel) throws Exception {
       serverChannel.pipeline()
        //处理http消息的编解码
        .addLast("httpServerCodec", new HttpServerCodec())
        //上面等同下面两个
//        .addLast("httpResponseEndcoder", new HttpResponseEncoder())
//        .addLast("HttpRequestDecoder", new HttpRequestDecoder())
        //添加自定义的ChannelHandler
       .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
       .addLast("httpServerHandler", new HttpServerHandler());
    }
}
