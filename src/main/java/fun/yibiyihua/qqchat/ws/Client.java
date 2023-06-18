package fun.yibiyihua.qqchat.ws;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import fun.yibiyihua.qqchat.constants.InfoProperties;
import fun.yibiyihua.qqchat.constants.MessageType;
import fun.yibiyihua.qqchat.entitiy.QqMessage;
import fun.yibiyihua.qqchat.entitiy.Params;
import fun.yibiyihua.qqchat.entitiy.Request;
import fun.yibiyihua.qqchat.api.OpenAIChatApi;
import fun.yibiyihua.qqchat.thread.ReConnectTask;
import redis.clients.jedis.Jedis;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

/**
 * @author: yibiyihua
 * @date: Created in 2023/5/24 17:40
 * @description: 客户端
 * @modified By:
 * @version: 1.0
 */

@ClientEndpoint
public class Client {
    private Session session;
    private static Client INSTANCE;
    private static final Jedis jedis = new Jedis(InfoProperties.REDIS_HOST, InfoProperties.REDIS_PORT);
    //重连状态，默认false未在重连，true重连中
    private volatile static boolean connecting = false;

    static {
        jedis.auth(InfoProperties.REDIS_PASSWORD);
    }

    private Client(String url) throws DeploymentException, IOException {
        session = ContainerProvider.getWebSocketContainer().connectToServer(this, URI.create(url));
    }

    public synchronized static boolean connect(String url) {
        try {
            INSTANCE = new Client(url);
            connecting = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("连接失败");
            return false;
        }
    }

    /**
     * @author: yibiyihua
     * @date: 2023/5/26 13:22
     * @param: []
     * @return: void
     * @description: 重连
     * @version: v1.0
     */
    public synchronized static void reConnect() {
        if (!connecting) {
            connecting = true;
            if (INSTANCE != null) {
                INSTANCE.session = null;
                INSTANCE = null;
            }
            //断线重连
            ReConnectTask.execute();
        }
    }

    @OnMessage
    public void onMessage(String json) {
        //获取消息对象
        QqMessage message = JSONUtil.toBean(json, QqMessage.class);
        String mesStr = message.getMessage();
        if (mesStr == null || mesStr.trim().isEmpty() || !"message".equals(message.getPost_type())) {
            return;
        }
        //创建请求
        Request<Params> paramsRequest = new Request<>();
        //设置请求类型
        paramsRequest.setAction("send_msg");
        //创建返回数据对象
        Params params = new Params();
        String messageType = message.getMessage_type();
        String chat;
        if (MessageType.PRIVATE.equals(messageType)) {//私聊
            params.setUser_id(message.getUser_id());
        } else if (MessageType.GROUP.equals(messageType)) {//群聊@bot
            if(!mesStr.contains("[CQ:at,qq="+ InfoProperties.QQ_BOT+"]")) {
                return;
            }
            //截取cq码中文本信息
            int index = mesStr.indexOf("]");
            if (index != -1) {
                mesStr = mesStr.substring(index + 1).trim();
                //拦截空提问
                if ("".equals(mesStr)) {
                    return;
                }
            }
            params.setGroup_id(message.getGroup_id());
        }
        params.setMessage_type(messageType);
        chat = OpenAIChatApi.chat(mesStr, message.getUser_id(),messageType);
        //拼接cq码
        params.setMessage("[CQ:reply,id=" + message.getMessage_id() + "] " + chat);
        //将返回参数放入请求
        paramsRequest.setParams(params);
        //发送信息
        sendMessage(JSONUtil.toJsonStr(paramsRequest));
    }

    /**
     * @author: yibiyihua
     * @date: 2023/5/25 9:55
     * @param: [json]
     * @return: void
     * @description: 发送消息
     * @version: v1.0
     */
    public static void sendMessage(String json) {
        Client.INSTANCE.session.getAsyncRemote().sendText(json);
    }

    @OnOpen
    public void onOpen(Session session) {
        //清除问答文件
        FileUtil.clean(InfoProperties.ANSWER_PATH);
        //清空redis缓存
        jedis.flushDB();
        System.out.println("连接成功");
    }

    @OnClose
    public void onClose(Session session) {
        //清除问答文件
        FileUtil.clean(InfoProperties.ANSWER_PATH);
        //清空redis缓存
        jedis.flushDB();
        //重连
        reConnect();
        System.out.println("连接关闭");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        //清除问答文件
        FileUtil.clean(InfoProperties.ANSWER_PATH);
        //清空redis缓存
        jedis.flushDB();
        //重连
        reConnect();
        System.out.println("连接异常：");
        throwable.printStackTrace();
    }
}