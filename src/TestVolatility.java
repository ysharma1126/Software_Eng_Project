import java.util.HashMap;

class MutableBoolean {
  public Boolean can_start;
  public MutableBoolean(Boolean can_start)
  {
    this.can_start = can_start;
  }
}

public class TestVolatility {

  private static volatile HashMap<String, Boolean> location_to_click_status = new HashMap<String, Boolean>();
  private static volatile MutableBoolean can_start = new MutableBoolean(false);
  
  public static void main(String[] args)
  {

    location_to_click_status.put("Ans", false);
    Object lock = new Object();
    System.out.println("1 " + location_to_click_status.get("Ans"));
    TestThread1 test_thread = new TestThread1(location_to_click_status, lock, can_start);
    Thread t = new Thread(test_thread);
    t.start();
//    try {
//      t.join();
//    } catch (InterruptedException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
    
    while (!can_start.can_start)
    {
      /*
       * Spin
       */
    }
    synchronized(lock)
    {
      System.out.println("1 " + location_to_click_status.get("Ans"));
    } 

  }
  
}