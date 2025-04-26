package gzn.netty.common.message;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author yihang
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class RpcResponseMessage extends Message {
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private String exceptionValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}
