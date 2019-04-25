package register;

import framework.URL;

import java.util.List;
import java.util.Random;

/**
 * zk 的注册工具
 */
public class ZkRegister extends BaseZookeeper {

    private static String ZK_HOST = "127.0.0.1:2181";
    private final String SERVER_ADDRESS = "/dubbo/server";
    private final String ROOT_ADDRESS = "/dubbo";

    public ZkRegister(){
        super(ZK_HOST);
    }

    public void setZkHost(String host){ZkRegister.ZK_HOST = host;}

    /**
     * 注册服务
     * @param serverInterface
     * @param url
     * @return
     */
    public boolean regist(Class serverInterface, URL url){
        if (null != getChildren(ROOT_ADDRESS)){
            deleteNodeRF(ROOT_ADDRESS);
        }
        return addAddressToNode(SERVER_ADDRESS + "/" + serverInterface.getName(), new String[]{url.getAddress()});
    }

    /**
     * 从地址列表里随机获取一个地址
     * @param serverInterface
     * @return
     */
    public String getURLRandom(Class serverInterface){
        List<String> urls = getChildren(SERVER_ADDRESS + "/" + serverInterface.getName());
        return urls.get(new Random().nextInt(urls.size()));
    }

    /**
     * 向节点添加服务地址
     * @param nodePath
     * @param address
     * @return
     * String path = "/dubbo/server/com.wzy.server.OrderServer";
     * String[] ip = new String[]{"192.168.37.1","192.168.37.2","192.168.37.3"};
     */
    public boolean addAddressToNode (String nodePath, String[] address) {
        if (!nodePath.startsWith("/")) {
            nodePath = "/" + nodePath;
        }

        if (null == getChildren(nodePath)){
            createMultNode(nodePath);
        }
        for (int i=0; i<address.length; i++) {
            String newPath = nodePath + "/" + address[i];
            String result = createNode(newPath,"");
            if (null == result) {
                return false;
            }
        }
        return true;
    }

    public boolean deleteNodeRF (String rootPath) {
        return deleteNodeRF(rootPath, rootPath);
    }
    /**
     * 删除节点及其子目录
     * @param rootPath
     * @return
     */
    private boolean deleteNodeRF (String rootPath, String parentPath) {
        if (!rootPath.startsWith("/")) {
            rootPath = "/" + rootPath;
        }
        List<String> childs = getChildren(rootPath);
        if (childs.size() > 0) {
            //递归
            for (String child : childs) {
                deleteNodeRF(rootPath + "/" + child, rootPath);
            }
        } else {
            System.out.println("delete: " + rootPath + " " + deleteNode(rootPath));
        }
        System.out.println("delete: " + parentPath + " " + deleteNode(parentPath));

        return true;
    }
}
