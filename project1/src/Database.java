import java.util.HashMap;
import java.util.Map;

/**
 * The class Database.
 * Store the key-value pairs
 */
public class Database {

  private Map<String, String> map;

  /**
   * Instantiates a new Database.
   */
  public Database() {
    this.map = new HashMap<>();
  }

  /**
   * Put.
   *
   * @param key the key
   * @param value the value
   */
  public void put(String key, String value) {
    map.put(key, value);
  }

  /**
   * Get string.
   *
   * @param key the key
   * @return the string
   */
  public String get(String key) {
    if (map.containsKey(key)) {
      return map.get(key) + "";
    }
    return "NOTFOUND";
  }

  /**
   * Delete string.
   *
   * @param key the key
   * @return the string
   */
  public String delete(String key) {
    if (map.containsKey(key)) {
      map.remove(key);
      return "DELETED";
    }
    return "NOTFOUND";
  }

  /**
   * Gets size.
   *
   * @return the size
   */
  public int getSize() {
    return map.size();
  }
}
