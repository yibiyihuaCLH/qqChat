package fun.yibiyihua.qqchat.entitiy.openai;

import lombok.Data;

/**
 * @author: yibiyihua
 * @date: Created in 2023/6/20 10:17
 * @description:
 * @modified By:
 * @version: 1.0
 */
@Data
public class Usage {
    private String prompt_tokens;
    private String completion_tokens;
    private String total_tokens;
}
