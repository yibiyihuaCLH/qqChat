package fun.yibiyihua.qqchat.entitiy;

import fun.yibiyihua.qqchat.enums.MessageType;
import lombok.Data;

/**
 * @author: yibiyihua
 * @date: Created in 2023/5/25 9:07
 * @description: 接收的消息
 * @modified By:
 * @version: 1.0
 */
@Data
public class Message {
    /*上报类型
    message	消息, 例如, 群聊消息
    request	请求, 例如, 好友申请
    notice	通知, 例如, 群成员增加
    meta_event	元事件, 例如, go-cqhttp 心跳包
    */
    private String post_type;
    /*消息类型
    private 私聊
    group 群聊
    */
    private MessageType message_type;
    //事件发生的时间戳
    private Long time;
    //收到事件的机器人 QQ 号
    private String self_id;
    /*消息子类型, 如果是好友则是 friend, 如果是群临时会话则是 group, 如果是在群中自身发送则是 group_self
    friend	好友
    normal	群聊
    anonymous	匿名
    group_self	群中自身发送
    group	群临时会话
    notice	系统提示
    */
    private String sub_type;
    //消息内容
    private String message;
    //原始消息内容
    private String raw_message;
    //字体
    private Integer font;
    //发送人信息
    private Sender sender;
    //消息ID
    private String message_id;
    //发送者 QQ 号
    private String user_id;
    //接收者 QQ 号
    private String target_id;
    //群号
    private String group_id;
    //??
    private Integer message_seq;
    //匿名消息
    private Object anonymous;

}
