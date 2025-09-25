package xiaozhi.common.ai.response;

import java.io.Serializable;
import lombok.Data;

@Data
public class Metadata implements Serializable {
    private Usage usage;
}
