package gzn.netty.advance.protocol;

import gzn.netty.common.config.Config;
import gzn.netty.common.message.RpcRequestMessage;
import gzn.netty.common.protocol.*;

public class TestJson {
    public static void main(String[] args) throws Exception {

        RpcRequestMessage message = new RpcRequestMessage(
                SequenceIdGenerator.nextId(),
                "gzn.netty.rpc.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"张三"}
        );
        Serializer.Algorithm algorithm = Config.getSerializerAlgorithm();
        byte[] bytes = algorithm.serialize(message);
        RpcRequestMessage requestMessage = algorithm.deserialize(RpcRequestMessage.class, bytes);
        System.out.println(requestMessage);


    }
}
