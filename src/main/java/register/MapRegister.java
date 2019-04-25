package register;

import framework.URL;

import java.util.*;

@Deprecated
public class MapRegister {

    private static Map<String, List<URL>> REGISTER = new HashMap<String, List<URL>>();

    public static void regist(String interfaceName, URL url){

        List<URL> list = REGISTER.get(interfaceName);
        if (list == null || list.isEmpty()) {
            list = Arrays.asList(url);
        } else {
            list.add(url);
        }
        REGISTER.put(interfaceName, list);

        System.out.println("注册中心： " + REGISTER.keySet());
    }

    /***
     * 随机返回一个注册地址
     * @param interfaceName
     * @return
     */
    public static URL random(String interfaceName) {

        List<URL> list = REGISTER.get(interfaceName);
        return list.get(new Random().nextInt(list.size()));
    }

}
