package xiaozhi.common.ai.response;

import java.util.List;
import lombok.Data;

/**
 * dify GET
 * /messages/{message_id}/suggested
 * 获取下一轮建议问题列表 的返回结果
 * @author: lei.gui
 * @date: 2024/8/17
 */
@Data
public class MessageSuggestedResponse {
    private String result;

    private List<String> data;
}
