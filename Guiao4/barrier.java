import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Barrier {
    private int c = 0;
    private int n;
    Lock l = new ReentrantLock();
    Condition cond = l.newCondition();

    Barrier(int n) { this.n = n; }

    void await() throws InterruptedException{
        l.lock();
        try{
            c+=1;
            if(c < n)
                while(c < n)
                    cond.await();
            else 
                cond.signalAll();

        } finally {l.unlock();}
    }
}