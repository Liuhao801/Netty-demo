package gzn.netty.common.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupChatResponseMessage extends AbstractResponseMessage {
    private String from;
    private String content;
    private String groupName;

    public GroupChatResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    public GroupChatResponseMessage(String from, String content, String groupName) {
        super(true,"发送成功");
        this.from = from;
        this.content = content;
        this.groupName= groupName;
    }
    @Override
    public int getMessageType() {
        return GroupChatResponseMessage;
    }
}
