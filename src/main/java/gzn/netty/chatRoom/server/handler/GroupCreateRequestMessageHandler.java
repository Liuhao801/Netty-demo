package gzn.netty.chatRoom.server.handler;

import gzn.netty.common.message.GroupCreateRequestMessage;
import gzn.netty.common.message.GroupCreateResponseMessage;
import gzn.netty.chatRoom.server.session.Group;
import gzn.netty.chatRoom.server.session.GroupSession;
import gzn.netty.chatRoom.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

@Slf4j
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        log.debug("msg: {}", msg);
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        // 群管理器
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if(group == null){
            // 发生成功消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true,groupName + "创建成功"));
            // 发送拉群消息
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            for (Channel channel : channels) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true,"您已被拉入" + groupName));
            }
        }else{
            ctx.writeAndFlush(new GroupCreateResponseMessage(false,groupName + "已经存在"));
        }
    }
}
