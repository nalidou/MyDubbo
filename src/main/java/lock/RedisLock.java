package lock;

import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Redis实现分布式锁
 */
public class RedisLock implements Lock {

    ThreadLocal<Jedis> jedis = new ThreadLocal<Jedis>();

    private static String LOCK_NAME = "LOCK";
    private static String REQUEST_ID = null;

    public RedisLock (String requestId) {
        RedisLock.REQUEST_ID = requestId;
        if (jedis.get() == null) {
            jedis.set(new Jedis("localhost"));
        }
    }
    public void lock() {
        if (tryLock()) {
            //jedis.set(LOCK_NAME, REQUEST_ID);
            //jedis.expire(LOCK_NAME, 1000);//设置过期时间

            //问题：上面两句代码不存在原子性操作，所以用下面一句代码替换掉
            jedis.get().set(LOCK_NAME, REQUEST_ID, "NX", "PX", 1000);
        }
    }

    public boolean tryLock() {
        while (true) {
            //key不存在返回1，不存在则返回0
            Long lock = jedis.get().setnx(LOCK_NAME, REQUEST_ID);
            if (lock == 1) {
                return true;
            }
        }
    }


    public void unlock() {
        //问题：保证不了原子性
        //String value = jedis.get(LOCK_NAME);
        //if (REQUEST_ID.equals(value)) {
        //    jedis.del(LOCK_NAME);
        //}

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        jedis.get().eval(script, Collections.singletonList(LOCK_NAME), Collections.singletonList(REQUEST_ID));
        jedis.get().close();
        jedis.remove();

    }

    public Condition newCondition() {
        return null;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void lockInterruptibly() throws InterruptedException {

    }
}
