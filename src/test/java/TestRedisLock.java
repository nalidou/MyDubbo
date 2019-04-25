public class TestRedisLock {
    public static Integer num = 1;
    public static void main(String[] args) {

        for (int i = 0; i < 20; i++) {
            new Thread(new Task(), "thread " + i).start();
        }

    }

    static class Task implements Runnable{

        public void run() {
            sub();
            System.out.println(Thread.currentThread().getName() + " " +num);
        }

        public void sub(){
            if (num != 0) {
                num--;
            }

        }
    }
}
