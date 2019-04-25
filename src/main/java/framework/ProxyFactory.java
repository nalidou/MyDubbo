package framework;

import protocol.http.HttpClient;
import provider.api.MyService;
import register.MapRegister;
import register.ZkRegister;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/***
 * 动态代理类
 */
public class ProxyFactory {

    public static <T> T getProxy(final Class interfaceClass){

        Object object = Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class[]{interfaceClass},
                new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                HttpClient httpClient = new HttpClient();
                Invocation invocation = new Invocation(interfaceClass.getName(),
                        method.getName(), method.getParameterTypes(), args);

                URL url = getUrl(interfaceClass);
                String result = httpClient.send(url.getHostname(), url.getPort(), invocation);

                return result;
            }
        });
        return (T) object;
    }

    private static URL getUrl(final Class interfaceClass){
        ZkRegister zk = new ZkRegister();
        String url = zk.getURLRandom(interfaceClass);
        zk.closeConnection();
        return new URL(url.split(":")[0], Integer.parseInt(url.split(":")[1]));
    }

}
