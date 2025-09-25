package xiaozhi.common.ai.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessageResponse extends CompletionMessageResponse {
    @JSONField(name = "conversation_id")
    private String conversationId;
}
