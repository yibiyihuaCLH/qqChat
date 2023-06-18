package fun.yibiyihua.qqchat.entitiy.openai;

import lombok.Data;

/**
 * @author: yibiyihua
 * @date: Created in 2023/6/20 10:19
 * @description:
 * @modified By:
 * @version: 1.0
 */
@Data
public class Choice {
    private Message message;
    private String finish_reason;
    private String index;
}
