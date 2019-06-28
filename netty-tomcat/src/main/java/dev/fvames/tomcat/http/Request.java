package dev.fvames.tomcat.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * @version 2019/6/28 17:06
 */

public class Request {

    private ChannelHandlerContext ctx;
    private HttpRequest request;

    public Request(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public String getUrl() {
        return request.uri();
    }

    public String getMethodName() {
        return request.method().name();
    }

    public Map<String, List<String>> getParamters() {

        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());

        return decoder.parameters();
    }

    public String getParamter(String name) {

        Map<String, List<String>> params = getParamters();
        List<String> param = params.get(name);

        return null == param ? null : param.get(0);
    }
}
