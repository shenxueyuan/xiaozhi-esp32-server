package xiaozhi.common.ai.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionMessageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /* 包含多个键值对，用于输入各种由应用定义的变量值 */
    private Map<String, Object> inputs;
    /* 响应返回的模式，支持streaming（流式）和blocking（阻塞式） */
    @JsonProperty("response_mode")
    private String responseMode;
    /* 用户标识符，用于定义终端用户的身份以进行检索和统计 */
    private String user;
    /* 文件列表，适用于结合文本理解和回答问题时输入文件（图像），仅当模型支持视觉能力时可用 */
    private List<FileInfo> files;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        /* 支持的类型：目前仅支持image */
        private String type;
        /* 传输方法，remote_url表示图像URL，local_file表示文件上传 */
        private String transfer_method;
        /* 图像URL（当传输方法为remote_url时） */
        private String url;
        /* 上传文件ID，必须事先通过文件上传API获得（当传输方法为local_file时） */
        private String upload_file_id;
    }
}