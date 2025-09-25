package xiaozhi.common.ai.response;

import com.alibaba.fastjson2.annotation.JSONField;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * @author xiuc001
 */
@Data
public class CompletionMessageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -3265916189369241244L;

    @JSONField(name = "message_id")
    private String messageId;
    private String model;
    private String answer;
    private Metadata metadata;
    private String event;
    @JSONField(name = "created_at")
    private Long createdAt;
    @JSONField(name = "task_id")
    private String taskId;
    private String id;
}
