package fun.yibiyihua.qqchat.entitiy;

import lombok.Data;

/**
 * @author: yibiyihua
 * @date: Created in 2023/5/25 9:23
 * @description: 发送的消息
 * @modified By:
 * @version: 1.0
 */
@Data
public class Params {
    //消息类型, 支持 private、group , 分别对应私聊、群组, 如不传入, 则根据传入的 *_id 参数判断
    private String message_type;
    //对方 QQ 号 ( 消息类型为 private 时需要 )
    private String user_id;
    //群号 ( 消息类型为 group 时需要 )
    private String group_id;
    //要发送的内容
    private String message;
    //消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
    private Boolean auto_escape = false;//默认为false
}
