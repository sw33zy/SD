import java.util.concurrent.locks.ReentrantLock;

class Bank2 {

  private static class Account {
    private int balance;
    Account(int balance) { this.balance = balance; }
    int balance() { return balance; }
    boolean deposit(int value) {
      balance += value;
      return true;
    }
  }

  // Our single account, for now
  private Account savings = new Account(0);

  //Bank mutex
  ReentrantLock l = new ReentrantLock();

  // Account balance
  public int balance() {
    l.lock();
    try{
        return savings.balance();
    } finally {l.unlock();}
  }

  // Deposit
  boolean deposit(int value) {
    l.lock();
    try{
        return savings.deposit(value);
    } finally {l.unlock();}
  }
}


class Deposits2 implements Runnable {
    private Bank2 bank;

    Deposits2 (Bank2 bank) {
        this.bank = bank;
    }

    public void run() {
        final long I = 1000;
        final int V = 100;

        for(int j = 0; j < I; j++) {
            bank.deposit(V);
        }
    }
}

class Ex3 {

    public static void main(String[] args) {
        final int  N = 10;
        Bank2 bank = new Bank2();
        Thread threads[] = new Thread[N];
        Deposits2 deposit = new Deposits2(bank);

        for(int i = 0; i < N; i++) {
            threads[i] = new Thread(deposit);
        }
        for(int i = 0; i < N; i++) {
            threads[i].start();
        }

        for(int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Account balance: " + bank.balance());
    }
}