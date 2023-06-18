package fun.yibiyihua.qqchat.entitiy.openai;

import lombok.Data;

/**
 * @author: yibiyihua
 * @date: Created in 2023/6/20 10:24
 * @description:
 * @modified By:
 * @version: 1.0
 */
@Data
public class Message {
    private String role;
    private String content;
}
