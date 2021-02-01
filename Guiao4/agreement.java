import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import static java.lang.Math.max;

public class Agreement {
    private static class Instance{
        int result = Integer.MIN_VALUE;
        int c = 0;
    }

    private int n ;
    private Instance agmnt = new Instance();
    Lock l = new ReentrantLock();
    Condition cond = l.newCondition();

    Agreement(int n) {this.n = n;}

    int propose(int choice) throws InterruptedException{
        l.lock();
        try{
            Instance agmnt = this.agmnt;
            agmnt.c +=1;
            agmnt.result = max(agmnt.result, choice);
            if(agmnt.c < n)
                while(this.agmnt==agmnt)
                    cond.await();
            else{
                cond.signalAll();
                this.agmnt= new Instance();
            }
            return agmnt.result;
        } finally {l.unlock();}
    }
}
