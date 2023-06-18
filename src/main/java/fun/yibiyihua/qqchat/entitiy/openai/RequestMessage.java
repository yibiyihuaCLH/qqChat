package fun.yibiyihua.qqchat.entitiy.openai;

import lombok.Data;

import java.util.List;

/**
 * @author: yibiyihua
 * @date: Created in 2023/6/20 21:21
 * @description: openai ai请求信息
 * @modified By:
 * @version: 1.0
 */
@Data
public class RequestMessage {
    private String model;
    private List<Message> messages;
    private String temperature;
}
