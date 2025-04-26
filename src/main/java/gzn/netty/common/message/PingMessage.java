package gzn.netty.common.message;

public class PingMessage extends Message {
    @Override
    public int getMessageType() {
        return PingMessage;
    }
}
