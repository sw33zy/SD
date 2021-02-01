import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Bank {

    private static class Account {

      private int balance;
      ReentrantLock l = new ReentrantLock();
      Account(int balance) { 
          this.balance = balance;
          this.l = new ReentrantLock();
      }
      int balance() { 
        l.lock();
        try{  
            return balance; 
        } finally {l.unlock();}
      }
      boolean deposit(int value) {
        l.lock();
        try{
            balance += value;
        } finally {l.unlock();}
        return true;
      }
      boolean withdraw(int value) {
        l.lock();
        try{  
            if (value > balance)
                return false;
            balance -= value;
            return true;
        } finally {l.unlock();}
      }
    }
  
    // Bank slots and vector of accounts
    private int slots;
    private Account[] av; 

    //Bank mutex
  
    public Bank(int n)
    {
      slots=n;
      av=new Account[slots];
      for (int i=0; i<slots; i++) av[i]=new Account(0);
    }
  
    // Account balance
    public int balance(int id) {
      if (id < 0 || id >= slots)
        return 0;
      /*l.lock();
      try{  */
      return av[id].balance();
      //} finally {l.unlock();}
    }
  
    // Deposit
    boolean deposit(int id, int value) {
      if (id < 0 || id >= slots)
        return false;
      //l.lock();
      //try{
      return av[id].deposit(value);
      //} finally{l.unlock();}
    }
  
    // Withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
      if (id < 0 || id >= slots)
        return false;
      //l.lock();
      //try{
      return av[id].withdraw(value);
      //} finally {l.unlock();}
    }

    //Transfer
    public boolean transfer(int from, int to, int value){
        if(from < 0 || to < 0 || from >=slots || to >=slots)
            return false;
        if(from < to) {
            av[from].l.lock();
            av[to].l.lock();
        }
        else {
            av[to].l.lock();
            av[from].l.lock();
        }
        try{
            if(av[from].balance < value)
                return false; //Insufficient funds
            av[from].withdraw(value);
            av[to].deposit(value);
            return true;
        } finally {av[from].l.unlock(); av[to].l.unlock();}
    }

    //Total
    public int totalBalance(){
        int sum = 0;
        //l.lock();
        for(int i = 0; i<slots; i++){
            av[i].l.lock();
        }
        for(int i = 0; i<slots; i++){
            sum+=av[i].balance;
            av[i].l.unlock();
        }
        //l.unlock();
        return sum;
    }
  }