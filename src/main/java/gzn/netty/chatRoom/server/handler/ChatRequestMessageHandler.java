package gzn.netty.chatRoom.server.handler;

import gzn.netty.common.message.ChatRequestMessage;
import gzn.netty.common.message.ChatResponseMessage;
import gzn.netty.chatRoom.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        log.debug("msg: {}", msg);
        // 获取目标对象的 channel
        Channel channel = SessionFactory.getSession().getChannel(msg.getTo());

        if (channel != null) {
            // 在线
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(),msg.getContent()));
        }else{
            // 不在线
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方用户不存在或者不在线"));
        }
    }
}
