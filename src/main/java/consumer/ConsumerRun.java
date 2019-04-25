package consumer;

import framework.Invocation;
import framework.ProxyFactory;
import protocol.http.HttpClient;
import provider.api.MyService;

public class ConsumerRun {
    public static void main(String[] args) {

        MyService myService = ProxyFactory.getProxy(MyService.class);
        System.out.println(myService.getName("aaa"));


    }
}
