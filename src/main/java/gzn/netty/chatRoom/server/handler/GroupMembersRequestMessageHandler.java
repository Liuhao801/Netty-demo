package gzn.netty.chatRoom.server.handler;

import gzn.netty.chatRoom.message.GroupMembersRequestMessage;
import gzn.netty.chatRoom.message.GroupMembersResponseMessage;
import gzn.netty.chatRoom.server.session.GroupSession;
import gzn.netty.chatRoom.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@ChannelHandler.Sharable
public class GroupMembersRequestMessageHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        log.debug("msg: {}", msg);
        String groupName = msg.getGroupName();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        if (groupSession.isExistGroup(groupName)) {
            Set<String> members = groupSession.getMembers(groupName);
            ctx.writeAndFlush(new GroupMembersResponseMessage(groupName, members));
        } else {
            ctx.writeAndFlush(new GroupMembersResponseMessage(false, groupName + "群不存在"));
        }

    }
}
