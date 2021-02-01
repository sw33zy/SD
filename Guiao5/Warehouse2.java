import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Warehouse2 {
  private Lock l = new ReentrantLock();
  private Map<String, Product> m =  new HashMap<String, Product>();

  private class Product { 
    int q = 0; 
    Condition c = l.newCondition();
  }

  //uma vez que é private, locks são desnecessários neste caso
  //use under lock
  private Product get(String s) {
    Product p = m.get(s);
    if (p != null) return p;
    p = new Product();
    m.put(s, p);
    return p;
  }

  public void supply(String s, int q) {
    l.lock();
    try{
      Product p = get(s);
      p.q += q;
      p.c.signalAll();
    } finally {l.unlock();}
  }

  public void consume(String[] a) throws InterruptedException{
    l.lock();
    try{
      for (int i = 0; i < a.length;){
        Product p = get(a[i]);
        i++;
        if(p.q==0){
          p.c.await();
          i=0;
        }
      }
      for (String s : a){
          get(s).q--;
      }
    } finally {l.unlock();}
  }
}