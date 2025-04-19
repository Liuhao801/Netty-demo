package gzn.netty.chatRoom.message;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@ToString(callSuper = true)
public class GroupMembersResponseMessage extends AbstractResponseMessage {

    private String groupName;
    private Set<String> members;

    public GroupMembersResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    public GroupMembersResponseMessage(String groupName,Set<String> members) {
        super(true,"发送成功");
        this.groupName = groupName;
        this.members = members;
    }

    @Override
    public int getMessageType() {
        return GroupMembersResponseMessage;
    }
}
