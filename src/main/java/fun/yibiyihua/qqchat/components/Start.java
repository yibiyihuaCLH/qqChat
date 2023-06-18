package fun.yibiyihua.qqchat.components;

import fun.yibiyihua.qqchat.ws.Client;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

/**
 * @author: yibiyihua
 * @date: Created in 2023/5/24 17:52
 * @description: 项目启动
 * @modified By:
 * @version: 1.0
 */
@Component
public class Start implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        //连接客户端
        if (!Client.connect(ResourceBundle.getBundle("inf").getString("websocket.url"))) {
            //未连接成功，重连
            Client.reConnect();
        }
    }
}
