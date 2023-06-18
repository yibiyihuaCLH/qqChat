package fun.yibiyihua.qqchat.api;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import fun.yibiyihua.qqchat.constants.ErrorCode;
import fun.yibiyihua.qqchat.constants.InfoProperties;
import fun.yibiyihua.qqchat.constants.MessageType;
import fun.yibiyihua.qqchat.constants.Role;
import fun.yibiyihua.qqchat.entitiy.openai.*;
import fun.yibiyihua.qqchat.entitiy.openai.Error;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.Jedis;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author yibiyihua
 * ClassName:OpenAIAPI.java
 * date:2023-03-03 16:49
 * Description:
 */
@UtilityClass
public class OpenAIChatApi {
    //api地址
    private static final Jedis jedis = new Jedis(InfoProperties.REDIS_HOST, InfoProperties.REDIS_PORT);

    static {
        jedis.auth(InfoProperties.REDIS_PASSWORD);
    }

    /**
     * @author: yibiyihua
     * @date: 2023/5/25 16:04
     * @param: [question, userId, messageType]
     * @return: java.lang.String
     * @description: 聊天
     * @version: v1.0
     */
    public static String chat(String question, String userId, String messageType) {
        //设置系统人格
        Message initData = new Message();
        initData.setRole(Role.SYSTEM);
        initData.setContent(InfoProperties.SYSTEM_CONTENT);
        ArrayList<Message> newDataList = new ArrayList<>();
        newDataList.add(initData);
        /*群聊，无上下文指令*/
        if (MessageType.GROUP.equals(messageType)) {
            //获取问题答案
            return askQuestion(newDataList, question);
        }
        //历史记录文件路径
        String filePath = InfoProperties.ANSWER_PATH + userId + ".json";
        /*私聊，添加上下文逻辑*/
        if ("关闭上下文".equals(question)) {
            jedis.del(userId);
            //删除历史记录json文件
            FileUtil.del(filePath);
            return "上下文已关闭。";
        }
        if ("开启上下文".equals(question)) {
            jedis.set(userId, "1");
            //设置超时时长一小时（管理员除外）
            if (!InfoProperties.QQ_ADMIN.equals(userId)) {
                jedis.expire(userId, 60 * 60);
            }
            //创建历史记录json文件
            FileUtil.writeString(JSONUtil.toJsonStr(newDataList), filePath, StandardCharsets.UTF_8);
            return "上下文已开启。";
        }
        String message;
        if (jedis.get(userId) == null) {//上下文未开启
            //删除历史记录json文件
            FileUtil.del(filePath);
            //获取问题答案
            message = askQuestion(newDataList, question);
        } else {//上下文已开启
            //获取历史对话记录
            String jsonString = ResourceUtil.readUtf8Str(filePath);
            List<Message> dataList = JSONUtil.toList(jsonString, Message.class);
            //携带历史和问题一同提问
            message = askQuestion(dataList,question);
            //将问题答案存入历史记录
            Message newAnswer = new Message();
            newAnswer.setRole(Role.ASSISTANT);
            newAnswer.setContent(message);
            dataList.add(newAnswer);
            // 将历史记录串写入到文件
            FileUtil.writeString(JSONUtil.toJsonStr(dataList), filePath, StandardCharsets.UTF_8);
        }
        //返回答案
        return message;
    }

    /**
     * @author: yibiyihua
     * @date: 2023/5/25 16:04
     * @param: [paramMap, dataList, question]
     * @return: java.lang.String
     * @description: 调用openai
     * @version: v1.0
     */
    public String askQuestion(List<Message> dataList, String question) {
        //添加新信息
        Message newQuestion = new Message();
        newQuestion.setRole(Role.USER);
        newQuestion.setContent(question);
        dataList.add(newQuestion);
        //设置请求参数
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setModel("gpt-3.5-turbo");
        requestMessage.setMessages(dataList);
        //配置请求
        HttpRequest request = HttpRequest.post(InfoProperties.OPENAI_URL)
                .header("Authorization", "Bearer " + InfoProperties.OPENAI_KEY)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(requestMessage));
        //（设置代理）
        if (InfoProperties.PROXY) {
            request.setProxy(new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(InfoProperties.PROXY_HOST,
                            InfoProperties.PROXY_PORT))
            );
        }
        //发送请求，获取返回信息
        String body = request.execute().body();
        ResponseMessage responseMessage = JSONUtil.toBean(body, ResponseMessage.class);
        //返回error信息
        Error error = responseMessage.getError();
        if (responseMessage.getError() != null) {
            String code = error.getCode();
            if (ErrorCode.CONTEXT_LENGTH_EXCEEDED.equals(code)) {
                //“超出上下文长度”，删除历史记录第一组对话，再提问
                dataList.remove(1);
                dataList.remove(2);
                return askQuestion(dataList,question);
            }else if (ErrorCode.INVALID_API_KEY.equals(code)) {
                return "密钥过期，请更新";
            } else {
                //其他报错暂时直接返回报错信息
                return error.getMessage();
            }
        }
        //未报错，处理返回信息
        List<Choice> choices = responseMessage.getChoices();
        Message message = choices.get(0).getMessage();
        //返回答案
        return message.getContent();
    }
}