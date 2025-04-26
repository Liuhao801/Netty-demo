package gzn.netty.rpc.server.handler;

import gzn.netty.common.message.RpcRequestMessage;
import gzn.netty.common.message.RpcResponseMessage;
import gzn.netty.common.protocol.SequenceIdGenerator;
import gzn.netty.rpc.server.service.HelloService;
import gzn.netty.rpc.server.service.ServicesFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) throws Exception {
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(message.getSequenceId());
        try {
            // 获取真正的实现对象
            HelloService service = (HelloService)
                    ServicesFactory.getService(Class.forName(message.getInterfaceName()));
            // 获取要调用的方法
            Class<?>[] parameterTypes = Arrays.stream(message.getParameterTypes())
                    .map(name -> {
                        try {
                            return Class.forName(name);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(Class<?>[]::new);

            Method method = service.getClass().getMethod(message.getMethodName(), parameterTypes);
            // 调用方法
            Object invoke = method.invoke(service, message.getParameterValue());
            // 调用成功
            response.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getCause().getMessage();
            // 调用异常
            response.setExceptionValue("远程调用出错:" +msg);
        }
        // 返回结果
        ctx.writeAndFlush(response);
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage message = new RpcRequestMessage(
                SequenceIdGenerator.nextId(),
                "gzn.netty.rpc.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"张三"}
        );
        HelloService service = (HelloService) ServicesFactory.getService(Class.forName(message.getInterfaceName()));
        Class<?>[] parameterTypes = Arrays.stream(message.getParameterTypes())
                .map(name -> {
                    try {
                        return Class.forName(name);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(Class<?>[]::new);

        Method method = service.getClass().getMethod(message.getMethodName(), parameterTypes);
        Object invoke = method.invoke(service, message.getParameterValue());
        System.out.println(invoke);
    }
}
