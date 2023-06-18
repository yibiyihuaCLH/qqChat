package fun.yibiyihua.qqchat.entitiy.openai;

import lombok.Data;

import java.util.List;

/**
 * @author: yibiyihua
 * @date: Created in 2023/6/20 10:09
 * @description: openai api返回信息
 * @modified By:
 * @version: 1.0
 */
@Data
public class ResponseMessage {
    private String id;
    private String object;
    private String created;
    private String model;
    private Usage usage;
    private List<Choice> choices;
    private Error error;
}
