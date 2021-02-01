import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Barrierv2 {
    private int c = 0;
    private int epoch = 0;
    private int n;
    Lock l = new ReentrantLock();
    Condition cond = l.newCondition();

    Barrierv2(int n) { this.n = n;}

    void await() throws InterruptedException{
        l.lock();
        try{
            int e = epoch;
            c+=1;
            if(c < n)
                while(epoch==e)
                    cond.await();
            else {
                cond.signalAll();
                c = 0;
                epoch += 1;
            }
        } finally {l.unlock();}
    }
}

class Main {
    public static void main(String[] args) {
        Barrierv2 b = new Barrierv2(3);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("Vou fazer await");
                b.await();
            } catch (Exception e) {}
            System.out.println("await retornou");
            try {
                Thread.sleep(2000);
                System.out.println("Vou fazer await");
                b.await();
            } catch (Exception e) {}
            System.out.println("await retornou");
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                System.out.println("Vou fazer await");
                b.await();
            } catch (Exception e) {}
            System.out.println("await retornou");
            try {
                Thread.sleep(1000);
                System.out.println("Vou fazer await");
                b.await();
            } catch (Exception e) {}
            System.out.println("await retornou");
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                System.out.println("Vou fazer await");
                b.await();
            } catch (Exception e) {}
            System.out.println("await retornou");
            try {
                Thread.sleep(3000);
                System.out.println("Vou fazer await");
                b.await();
            } catch (Exception e) {}
            System.out.println("await retornou");
        }).start();
    }
}
