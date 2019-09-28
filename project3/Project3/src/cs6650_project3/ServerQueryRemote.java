package cs6650_project3;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


public class ServerQueryRemote extends UnicastRemoteObject implements ServerQuery {

  private static final long serialVersionUID = 1L;
  private Random rand = new Random();
  private String putInfo[] = new String[2];
  private static String deleteInfo = null;
  private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();

  /**
   * Instantiates a new Query remote.
   *
   * @throws RemoteException the remote exception
   */
  protected ServerQueryRemote() throws RemoteException {
    super();
  }

  /**
   * put check.
   *
   * return boolean.
   */
  public boolean putCheck(String key, String value) {
    // use random to mimic a situation in which server is not ready
    Integer randNum = rand.nextInt(10);
    // mock not ready
    if (randNum != 5) {
      putInfo[0] = key;
      putInfo[1] = value;
      System.out.println("put key: " + key + " , value: " + value + " is prepared");
      return true;
    } else {
      System.out.println("isn't prepared");
      return false;
    }
  }

  @Override
  public String put() {
    map.put(putInfo[0], putInfo[1]);
    System.out.println("key: " + putInfo[0] + " ,value: " + putInfo[1] + " added");
    putInfo = new String[2];
    return "added";
  }

  @Override
  public String putAbort() {
    putInfo = new String[2];
    System.out.println("put operation is aborted");
    return "aborted";
  }

  @Override
  public String get(String key) {
    // mock a timeout
    try {
      Thread.sleep(rand.nextInt(6000));
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String result = map.containsKey(key) ? map.get(key) : "not found the key";
    return result;
  }

  @Override
  public boolean deleteCheck(String key) {
    if (map.containsKey(key)) {
      deleteInfo = key;
      System.out.println("delete key: " + key + " is prepared");
      return true;
    } else {
      System.out.println("isn't prepared");
      return false;
    }
  }


  @Override
  public String delete() {
    map.remove(deleteInfo);
    System.out.println("key: " + deleteInfo + " is deleted");
    putInfo = null;
    return "deleted";
  }

  @Override
  public String deleteAbort() {
    putInfo = null;
    System.out.println("deletion operation is aborted");
    return "aborted";
  }


}