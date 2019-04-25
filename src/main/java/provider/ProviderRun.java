package provider;

import framework.URL;
import protocol.http.HttpServer;
import provider.api.MyService;
import provider.impl.MyServiceImpl;
import register.MapRegister;
import register.ZkRegister;

public class ProviderRun {

    private static final String HOST_NAME = "localhost";
    private static final Integer PORT = 8080;

    public static void main(String[] args) {

        //注册服务
        URL url = new URL(HOST_NAME, PORT);
        ZkRegister zk = new ZkRegister();
        zk.regist(MyService.class, url);//接口注册到zk上
        //zk.closeConnection();

        //MapRegister.regist(MyService.class.getName(), url);
        LocalMap.put(MyService.class.getName(), MyServiceImpl.class);//为当前服务器添加实现类

        //暴露服务
        HttpServer httpServer = new HttpServer();
        httpServer.start(HOST_NAME, PORT);
    }
}
