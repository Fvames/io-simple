package dev.fvames.rpc.consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @version 2019/7/3 16:50
 */

public class RpcProxyHandler extends ChannelInboundHandlerAdapter {

	private Object response;

	public Object getResponse() {
		return this.response;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		this.response = msg;
	}
}
