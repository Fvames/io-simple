package dev.fvames.tomcat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.UnsupportedEncodingException;

/**
 * @version 2019/6/28 17:07
 */

public class Response {

    private ChannelHandlerContext ctx;

    public Response(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void write(String out) {

        if (null == out || "" == out) {
            return;
        }

        try {

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(out.getBytes("UTF-8")));

            response.headers().set("Content-Type", "text/html;");
            ctx.write(response);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {

            ctx.flush();
            ctx.close();
        }
    }

}
