package xiaozhi.common.ai.response;

import com.alibaba.fastjson2.annotation.JSONField;
import java.io.Serializable;
import lombok.Data;

@Data
public class WorkflowResponse implements Serializable {
    /**
     * workflow 执行 ID
     */
    @JSONField(name = "task_id")
    private String taskId;
    /**
     * 任务 ID，用于请求跟踪和下方的停止响应接口
     */
    @JSONField(name = "workflow_run_id")
    private String workflowRunId;

    /**
     * 详细内容
     */
    private WorkflowData data;
}
