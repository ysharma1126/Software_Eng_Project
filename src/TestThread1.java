import java.util.HashMap;

public class TestThread1 implements Runnable {

  private volatile HashMap<String, Boolean> location_to_click_status = new HashMap<String, Boolean>();
  private Object lock;
  private volatile MutableBoolean can_start;
  
  public TestThread1(HashMap<String, Boolean> location_to_click_status, Object lock, MutableBoolean can_start)
  {
    this.location_to_click_status = location_to_click_status;
    this.lock = lock;
    this.can_start = can_start;
  }
  

  public void run()
  {
    synchronized(this.lock)
    {
      can_start.can_start = true;
      location_to_click_status.put("Ans", true);
      System.out.println(location_to_click_status.get("Ans"));
    }
  }
  
}
