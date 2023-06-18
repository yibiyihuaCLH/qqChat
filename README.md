# qqChat
主要详解src/main/resources/下的inf.properties配置文件
#机器人qq号码，自己注册个小号
qq.bot=
#管理员qq
qq.adm=
#path（默认也行，上下文缓存文件，随便找个文件目录就行）
answer.path=/opt/qqBot/openAIJson/
#jianting端口，跟go-chttp相同，websocket
websocket.url=ws://127.0.0.1:9999
#openai（openai请求端口，不用动）
openai.url=https://api.openai.com/v1/chat/completions
#自己填写key
openai.key=
#redis（上网查，安装就行，默认本机，端口也是默认，密码不设置就不用填写）
redis.host=127.0.0.1
redis.port=6379
#redis.password=
#proxy（代理软件端口，如果不用就改为false即可）
proxy=true
proxy.host=127.0.0.1
proxy.port=10809
