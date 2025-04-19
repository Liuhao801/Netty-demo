package gzn.netty.chatRoom.server.handler;

import gzn.netty.chatRoom.message.LoginRequestMessage;
import gzn.netty.chatRoom.message.LoginResponseMessage;
import gzn.netty.chatRoom.server.service.UserServiceFactory;
import gzn.netty.chatRoom.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        log.debug("msg: {}", msg);
        String username = msg.getUsername();
        String password = msg.getPassword();

        boolean login = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage message = null;
        if (login) {
            SessionFactory.getSession().bind(ctx.channel(),username);
            message = new LoginResponseMessage(true, "登录成功");
        }else{
            message = new LoginResponseMessage(false, "用户名或密码错误！");
        }
        ctx.writeAndFlush(message);
    }
}
