package cs6650_project2;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The class QueryRemote.
 */
public class QueryRemote extends UnicastRemoteObject implements Query {

  private static final long serialVersionUID = 1L;
  private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd.hh.MM.ss.ms");
  private Random rand = new Random();

  private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();

  /**
   * Instantiates a new Query remote.
   *
   * @throws RemoteException the remote exception
   */
  protected QueryRemote() throws RemoteException {
    super();
  }

  /**
   * Gets time.
   */
  public static void getTime() {
    long sendTime = System.currentTimeMillis();
    Date sendDate = new Date(sendTime);
    System.out.println(sdf.format(sendDate));
  }

  private String getClientAddress() throws AccessException {
    String hostName = null;
    try {
      hostName = RemoteServer.getClientHost();
    } catch (ServerNotActiveException e) {
      // if no remote host is currently executing this method,
      // then is localhost, and the access should be granted.
    }
    if (hostName == null) {
      throw new AccessException("Can not get remote host address.");
    }
    return hostName;
  }

  @Override
  public String put(String key, String value) throws RemoteException {
    try {
      Thread.sleep(rand.nextInt(6000));
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    getTime();
    System.out.println("Received from client " + getClientAddress()+ ": put key: " + key + ", value: " + value);
    map.put(key, value);
    getTime();
    System.out.println("Sent to client: key: " + key + ", value: " + value + " has been added");
    return "added";
  }

  @Override
  public String get(String key) throws RemoteException {
    try {
      Thread.sleep(rand.nextInt(6000));
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    getTime();
    System.out.println("Received from client " + getClientAddress()+ ": get key: " + key);
    String result = map.containsKey(key) ? map.get(key) : "not found the key";
    getTime();
    System.out.println("Sent to client: key: " + key + ", value: " + result);
    return result;
  }

  @Override
  public String delete(String key) throws RemoteException {
    try {
      Thread.sleep(rand.nextInt(6000));
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    getClientAddress();
    getTime();
    System.out.println("Received from client " + getClientAddress()+ ": delete key: " + key);
    String result;
    if (map.containsKey(key)){
      map.remove(key);
      result =  "deleted key: " + key;
    }else{
      result = "key: " + key + " is not found";
    }
    getTime();
    System.out.println("Sent to client: " + result);
    return result;
  }

  @Override
  public String invalidQuery(String line) throws RemoteException {
    try {
      Thread.sleep(rand.nextInt(6000));
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    getClientAddress();
    getTime();
    System.out.println("received malformed request of length " + line.length() + " from " + getClientAddress());
    getTime();
    System.out.println("Sent to client: " + line + " is invalid ");
    return "invalid";
  }
}