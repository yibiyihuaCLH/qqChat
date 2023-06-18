package fun.yibiyihua.qqchat.api;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import fun.yibiyihua.qqchat.enums.MessageType;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.Jedis;

import java.net.InetSocketAddress;
import java.net.Proxy;
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
    private static final ResourceBundle inf = ResourceBundle.getBundle("inf");
    private static final Jedis jedis = new Jedis(inf.getString("redis.host"), Integer.parseInt(inf.getString("redis.port")));

    static {
        jedis.auth(inf.getString("redis.password"));
    }

    /**
     * @author: yibiyihua
     * @date: 2023/5/25 16:04
     * @param: [question, userId, messageType]
     * @return: java.lang.String
     * @description: openai接口
     * @version: v1.0
     */
    public static String chat(String question, String userId, MessageType messageType) {
        //群聊，无上下文指令
        if (MessageType.GROUP.equals(messageType)) {
            List<Map<String, Object>> dataList = new ArrayList<>();
            //获取问题答案
            return askQuestion(dataList, question);
        }
        //历史记录文件路径
        String filePath = inf.getString("answer.path") + userId + ".json";
        //私聊，添加上下文逻辑
        if ("关闭上下文".equals(question)) {
            jedis.del(userId);
            //删除历史记录json文件
            FileUtil.del(filePath);
            return "上下文已关闭。";
        }
        if ("开启上下文".equals(question)) {
            jedis.set(userId, "1");
            //设置超时时长一小时（管理员除外）
            if (!inf.getString("qq.adm").equals(userId)) {
                jedis.expire(userId, 60 * 60);
            }
            //创建历史记录json文件
            FileUtil.writeString("[]", filePath, "UTF-8");
            return "上下文已开启。";
        }
        String message;
        if (jedis.get(userId) == null) {//上下文未开启
            //删除历史记录json文件
            FileUtil.del(filePath);
            List<Map<String, Object>> dataList = new ArrayList<>();
            //获取问题答案
            message = askQuestion(dataList, question);
        } else {//上下文已开启
            //获取历史对话记录
            List<Map<String, Object>> dataList = hisDataList(filePath);
            //携带历史和问题一同提问
            message = askQuestion(dataList,question);
            //将问题答案存入历史记录
            HashMap<String, Object> data = new HashMap<>();
            data.put("role", "assistant");
            data.put("content", message);
            dataList.add(data);
            // 转换为JSON字符串
            String wJsonString = JSONUtil.toJsonStr(dataList);
            // 将JSON字符串写入到文件
            FileUtil.writeString(wJsonString, filePath, "UTF-8");
        }
        //返回答案
        return message;
    }

    /**
     * @author: yibiyihua
     * @date: 2023/5/25 16:04
     * @param: [paramMap, dataList, question]
     * @return: java.lang.String
     * @description: 提问openai
     * @version: v1.0
     */
    public String askQuestion(List<Map<String, Object>> dataList, String question) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("model", "gpt-3.5-turbo");
        //添加问题
        HashMap<String, Object> data = new HashMap<>();
        data.put("role", "user");
        data.put("content", question);
        dataList.add(data);
        //提问
        paramMap.put("messages", dataList);
        JSONObject message;
        //发送请求
        HttpRequest request = HttpRequest.post(inf.getString("openai.url"))
                .header("Authorization", "Bearer " + inf.getString("openai.key"))
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(paramMap));
        //设置代理
        if ("true".equals(inf.getString("proxy"))) {
            request.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(inf.getString("proxy.host"), Integer.parseInt(inf.getString("proxy.port")))));
        }
        //获取返回信息
        String body = request.execute().body();
        JSONObject jsonObject = JSONUtil.parseObj(body);
        //返回error信息
        if (jsonObject.get("error") != null) {
            JSONObject error = JSONUtil.parseObj(jsonObject.get("error"));
            String code = String.valueOf(error.get("code"));
            if ("context_length_exceeded".equals(code)) {
                //“超出上下文长度”，删除历史记录第一个元素，再提问
                dataList.remove(0);
                return askQuestion(dataList,question);
            }else if ("invalid_api_key".equals(code)) {
                return "key过期，请更新";
            } else {
                //其他报错暂时直接返回报错信息
                return error.get("message").toString();
            }
        }
        //未报错，处理返回信息
        JSONArray choices = jsonObject.getJSONArray("choices");
        JSONObject result = choices.get(0, JSONObject.class, Boolean.TRUE);
        message = result.getJSONObject("message");
        //返回答案
        return message.getStr("content");
    }

    /**
     * @author: yibiyihua
     * @date: 2023/5/25 16:03
     * @param: [jsonString, dataList]
     * @return: void
     * @description: 将jsonList转换添加至List
     * @version: v1.0
     */
    private static List<Map<String, Object>> hisDataList(String filePath) {
        //读取历史记录
        String jsonString = ResourceUtil.readUtf8Str(filePath);
        //转化为List
        List<Map<String, Object>> dataList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);
        for (Object obj : jsonArray) {
            Map<String, Object> map = new HashMap<>();
            if (obj != null) {
                Map<String, Object> tempMap = (Map<String, Object>) obj;
                for (String key : tempMap.keySet()) {
                    Object value = tempMap.get(key);
                    if (value != null) {
                        String strValue = value.toString();
                        map.put(key, strValue);
                    }
                }
            }
            dataList.add(map);
        }
        return dataList;
    }
}