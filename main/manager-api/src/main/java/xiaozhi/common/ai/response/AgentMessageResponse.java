package xiaozhi.common.ai.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AgentMessageResponse extends CompletionMessageResponse {

    /**
     * LLM 返回文本块事件
     */
    private String event;

    @JSONField(name = "conversation_id")
    private String conversationId;
}
