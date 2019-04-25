import register.ZkRegister;

import java.util.ArrayList;
import java.util.List;

public class TestZK {

    public static void main(String[] args) {
        //BaseZookeeper zk = null;

        // zk = new BaseZookeeper("192.168.37.136:2181");

        String path = "/dubbo/server/com.wzy.server.OrderServer";
        String path2 = "/dubbo5/server/com.wzy.server.OrderServer";
        String path3 = "/dubbo4/server/com.wzy.server.OrderServer/192.168.37.1";
        String[] ip = new String[]{"192.168.37.1","192.168.37.2","192.168.37.3"};
        ZkRegister zk = new ZkRegister();

        //System.out.println(zk.getChildren(path3));

        //System.out.println(zk.deleteNode("/dubbo4/server/com.wzy.server.OrderServer/192.168.37.1"));
        //System.out.println(zk.addAddressToNode(path2, ip));
        System.out.println(zk.deleteNodeRF("/dubbo4"));


        //System.out.println(zk.createMultNode(path));

        //System.out.println(zk.getChildren(path2));
        //zk.createNode(path,"192.168.37.1");
        //System.out.println(zk.getChildren("/"));

        //zk.createNode("/dubbo/a/cc","");
        //System.out.println(zk.getData("/dubbo"));//dubbo服务注册地址
        //System.out.println(zk.setData("/dubbo",""));




    }
}
