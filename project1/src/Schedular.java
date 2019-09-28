import java.util.HashMap;
import java.util.Map;

/**
 * The class Schedular.
 * Store the request ID and its corresponding sending timestamp
 */
public class Schedular {
  // the timestamp difference (time limit) can be set as needed
  private long DIFF = 5000;
  private Map<Integer, Long> map;

  /**
   * Instantiates a new Schedular.
   */
  public Schedular() {
    this.map = new HashMap<>();
  }

  /**
   * Add.
   *
   * @param ID the id
   * @param timeStamp the time stamp
   */
  public void add(Integer ID, Long timeStamp) {
    map.put(ID, timeStamp);
  }

  /**
   * Check valid period.
   *
   * @param ID the id
   * @param timeStamp the time stamp
   * @return the boolean
   */
  public boolean checkValidPeriod(Integer ID, Long timeStamp) {
    if (timeStamp - map.get(ID) < DIFF) {
      return true;
    }
    return false;
  }

  /**
   * Delete.
   *
   * @param ID the id
   */
  public void delete(Integer ID) {
    map.remove(ID);
  }
}
