package gzn.netty.chatRoom.server.handler;

import gzn.netty.chatRoom.message.GroupChatRequestMessage;
import gzn.netty.chatRoom.message.GroupChatResponseMessage;
import gzn.netty.chatRoom.server.session.GroupSession;
import gzn.netty.chatRoom.server.session.GroupSessionFactory;
import gzn.netty.chatRoom.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        log.debug("msg: {}", msg);
        String groupName = msg.getGroupName();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        if (! groupSession.isExistGroup(groupName)) {
            ctx.writeAndFlush(new GroupChatResponseMessage(false,"群聊不存在"));
        }else{
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            for (Channel channel : channels) {
                channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(),msg.getContent(),msg.getGroupName()));
            }
        }
    }
}
