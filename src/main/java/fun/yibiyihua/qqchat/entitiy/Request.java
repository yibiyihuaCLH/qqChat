package fun.yibiyihua.qqchat.entitiy;

import lombok.Data;

/**
 * @author: yibiyihua
 * @date: Created in 2023/5/25 9:21
 * @description: 请求
 * @modified By:
 * @version: 1.0
 */
@Data
public class Request<T> {
    //终结点名称, 例如 'send_group_msg'
    private String action;
    //返回参数
    private T params;
    //'回声', 如果指定了 echo 字段, 那么响应包也会同时包含一个 echo 字段, 它们会有相同的值(没用上，不懂)
    private String echo;

}
