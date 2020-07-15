package com.tzx.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx.http
 * @ClassName: HttpServerHandler
 * @Description:
 * @Author: 唐志翔
 * @Date: 2020/6/10 0010 15:09
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private HttpRequest request;

    private AsciiString contentType = HttpHeaderValues.TEXT_PLAIN;

//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
//        if (msg instanceof HttpRequest) {
//            request = (HttpRequest) msg;
//            request.method();
//            String uri = request.uri();
//            System.out.println("Uri:" + uri);
//        }
//        if (msg instanceof HttpContent) {
//
//            HttpContent content = (HttpContent) msg;
//            ByteBuf buf = content.content();
//            System.out.println(buf.toString(io.netty.util.CharsetUtil.UTF_8));
//
//            ByteBuf byteBuf = Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8);
//            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
//            response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
//            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
//
//            ctx.writeAndFlush(response);
//
//        }
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        System.out.println("class:" + msg.getClass().getName());
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer("test".getBytes())); // 2

        HttpHeaders heads = response.headers();
        heads.add(HttpHeaderNames.CONTENT_TYPE, contentType + "; charset=UTF-8");
        heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes()); // 3
        heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        ctx.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete");
        super.channelReadComplete(ctx);
        ctx.flush(); // 4
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught");
        if(null != cause) cause.printStackTrace();
        if(null != ctx) ctx.close();
    }
}
