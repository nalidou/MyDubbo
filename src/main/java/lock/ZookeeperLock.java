package lock;

import org.apache.zookeeper.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Zookeeper实现分布式锁
 */
public class ZookeeperLock implements Lock {

    private ThreadLocal<ZooKeeper> zk = new ThreadLocal<ZooKeeper>();
    private String host = "localhost:2181";

    private final String LOCK_NAME = "/LOCK";
    private ThreadLocal<String> CURRENT_NODE = new ThreadLocal<String>();

    private void init() {
        if (zk.get() == null) {
            synchronized (ZookeeperLock.class) {
                if (zk.get() == null) {
                    try {
                        zk.set( new ZooKeeper(host, 2000, new Watcher() {
                            public void process(WatchedEvent watchedEvent) {
                                // do nothing..
                            }
                        }));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public void lock() {
        init();
        if (tryLock()) {
            System.out.println("get lock success");
        }
    }

    public boolean tryLock() {
        String node = LOCK_NAME + "/zk_";
        try {
            //创建临时顺序节点  /LOCK/zk_1
            CURRENT_NODE.set(zk.get().create(node, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL));
            //zk_1,zk_2
            List<String> list = zk.get().getChildren(LOCK_NAME, false);
            Collections.sort(list);
            System.out.println(list);
            String minNode = list.get(0);

            if ((LOCK_NAME + "/" + minNode).equals(CURRENT_NODE.get())) {
                return true;
            } else {
                //等待锁
                Integer currentIndex = list.indexOf(CURRENT_NODE.get().substring(CURRENT_NODE.get().lastIndexOf("/") + 1));
                String preNodeName = list.get(currentIndex - 1);

                //监听前一个节点删除事件
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                zk.get().exists(LOCK_NAME + "/" + preNodeName, new Watcher() {
                    public void process(WatchedEvent watchedEvent) {
                        if (Event.EventType.NodeDeleted.equals(watchedEvent.getType())) {
                            countDownLatch.countDown();
                            System.out.println(Thread.currentThread().getName() + "唤醒锁..");
                        }
                    }
                });

                System.out.println(Thread.currentThread().getName() + "等待锁..");
                countDownLatch.await();//在变成0之前会一直阻塞

            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void unlock() {
        try {
            zk.get().delete(CURRENT_NODE.get(), -1);
            CURRENT_NODE.remove();
            zk.get().close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void lockInterruptibly() throws InterruptedException {

    }

    public Condition newCondition() {
        return null;
    }
}
