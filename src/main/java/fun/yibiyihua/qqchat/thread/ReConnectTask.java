package fun.yibiyihua.qqchat.thread;

import fun.yibiyihua.qqchat.constants.InfoProperties;
import fun.yibiyihua.qqchat.ws.Client;

/**
 * @author: yibiyihua
 * @date: Created in 2023/5/26 13:23
 * @description: 重连线程
 * @modified By:
 * @version: 1.0
 */
public class ReConnectTask implements Runnable {
    @Override
    public void run() {
        //重连死循环，直至连接成功
        while (true) {
            if (Client.connect(InfoProperties.WEBSOCKET_URL)) {
                break;
            } else {
                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @author: yibiyihua
     * @date: 2023/5/26 14:31
     * @param: []
     * @return: void
     * @description: 重连方法
     * @version: v1.0
     */
    public static void execute() {
        new Thread(new ReConnectTask()).start();
    }
}
