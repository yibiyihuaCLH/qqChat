package fun.yibiyihua.qqchat.entitiy.openai;

import lombok.Data;

/**
 * @author: yibiyihua
 * @date: Created in 2023/6/20 10:34
 * @description:
 * @modified By:
 * @version: 1.0
 */
@Data
public class Error {
    private String message;
    private String type;
    private String param;
    private String code;
}
