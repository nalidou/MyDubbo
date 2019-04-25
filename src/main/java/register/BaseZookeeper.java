package register;


import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class BaseZookeeper implements Watcher{

    public BaseZookeeper(){}

    public BaseZookeeper(String host){
        this.connectZookeeper(host);
    }

    private ZooKeeper zookeeper;

    //超时时间
    private static final int SESSION_TIME_OUT = 2000;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void process(WatchedEvent event) {
        if (event.getState() == KeeperState.SyncConnected) {
            //System.out.println("Watch received event");
            countDownLatch.countDown();
        }
    }

    //连接zookeeper
    protected void connectZookeeper(String host){
        try {
            zookeeper = new ZooKeeper(host, SESSION_TIME_OUT, this);
            countDownLatch.await();
            //System.out.println("zookeeper connection success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //创建节点
    protected String createNode(String path, String data){
        try {
            //永久节点
            String result = this.zookeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            //临时节点（会话关闭就删除了，调用close后就自动删除了）
            //String result = this.zookeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("createNode: " + result);
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    //创建多级节点
    //String path = "/dubbo/server/com.wzy.server.OrderServer";
    protected boolean createMultNode(String path){

        String[] paths = path.split("/");
        String realPath = "/";
        for (int i=1; i<paths.length; i++) {
            realPath += paths[i];
            String result = createNode(realPath, "");

            if (result == null) {
                return false;
            }
            realPath += "/";
        }
        return true;
    }

    //获取路径下所有子节点
    protected List<String> getChildren(String path){
        try {
            List<String> children = zookeeper.getChildren(path, false);
            return children;
        } catch (Exception e) {
            //当路径已经是根节点（没有子节点）时，就会抛异常
            return null;
        }

    }

    //获取节点上面的数据
    protected String getData(String path) throws KeeperException, InterruptedException{
        byte[] data = zookeeper.getData(path, false, null);
        if (data == null) {
            return "";
        }
        return new String(data);
    }

    //设置节点信息
    protected Stat setData(String path, String data){
        try {
            getData(path);
            Stat stat = zookeeper.setData(path, data.getBytes(), -1);
            return stat;
        } catch (Exception e) {
            //String result = createNode(path,"");
            return null;
        }

    }

    //删除节点
    protected boolean deleteNode(String path){
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        try {
            zookeeper.delete(path, -1);
        } catch (InterruptedException e) {
            return false;
        } catch (KeeperException e) {
            return false;
        }
        return true;
    }

    //获取创建时间
    protected String getCTime(String path) throws KeeperException, InterruptedException{
        Stat stat = zookeeper.exists(path, false);
        return String.valueOf(stat.getCtime());
    }

    //获取某个路径下孩子的数量
    protected Integer getChildrenNum(String path) throws KeeperException, InterruptedException{
        int childenNum = zookeeper.getChildren(path, false).size();
        return childenNum;
    }

    //监听节点是否被删除
    protected void watchIsDel(final String path) throws Exception{
        zookeeper.exists(path, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                Event.EventType type = watchedEvent.getType();
                if (Event.EventType.NodeDeleted.equals(type)) {
                    System.out.println("结点 " + path + "被删除了");
                }
            }
        });
    }

    //关闭连接
    public void closeConnection() {
        if (zookeeper != null) {
            try {
                zookeeper.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
