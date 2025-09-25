package xiaozhi.common.ai.response;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class WorkflowData {
    /**
     * workflow 执行 ID
     */
    private String id;
    /**
     * 关联 Workflow ID
     */
    @JSONField(name = "workflow_id")
    private String workflowId;
    /**
     * 执行状态, running / succeeded / failed / stopped
     */
    private String status;
    /**
     * outputs (json) Optional 输出内容
     */
    private JSONObject outputs;
    /**
     *  (string) Optional 错误原因
     */
    private String error;
    /**
     * (float) Optional 耗时(s)
     */
    @JSONField(name = "elapsed_time")
    private String elapsedTime;

    /**
     * (int) Optional 总使用 tokens
     */
    @JSONField(name = "total_tokens")
    private Integer totalTokens;

    /**
     * 总步数（冗余），默认 0
     */
    @JSONField(name = "total_tokens")
    private Integer totalSteps;

    /**
     * 开始时间
     */
    @JSONField(name = "created_at")
    private Long createdAt;

    /**
     * 结束时间
     */
    @JSONField(name = "finished_at")
    private Long finishedAt;

}

