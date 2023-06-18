package fun.yibiyihua.qqchat.constants;

import java.util.ResourceBundle;

/**
 * @author: yibiyihua
 * @date: Created in 2023/6/20 20:52
 * @description: inf.properties
 * @modified By:
 * @version: 1.0
 */
public class InfoProperties {
    //inf.properties文件对象
    public static final ResourceBundle inf = ResourceBundle.getBundle("info");
    //机器人qq号码
    public static final String QQ_BOT = inf.getString("qq.bot");
    //管理员qq号码
    public static final String QQ_ADMIN = inf.getString("qq.adm");
    //历史记录保存路径
    public static final String ANSWER_PATH = inf.getString("answer.path");
    //通信地址
    public static final String WEBSOCKET_URL = inf.getString("websocket.url");
    //openai请求地址
    public static final String OPENAI_URL = inf.getString("openai.url");
    //openai密钥
    public static final String OPENAI_KEY = inf.getString("openai.key");
    //redis地址
    public static final String REDIS_HOST = inf.getString("redis.host");
    //redis端口
    public static final int REDIS_PORT = Integer.parseInt(inf.getString("redis.port"));
    //redis密码
    public static final String REDIS_PASSWORD = inf.getString("redis.password");
    //是否开启代理
    public static final boolean PROXY = Boolean.parseBoolean(inf.getString("proxy"));
    //代理主机地址
    public static final String PROXY_HOST = inf.getString("proxy.host");
    //代理端口
    public static final int PROXY_PORT = Integer.parseInt(inf.getString("proxy.port"));
    public static final String SYSTEM_CONTENT = inf.getString("system.content");
}
