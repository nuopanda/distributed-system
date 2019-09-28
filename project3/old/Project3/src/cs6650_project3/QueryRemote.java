package cs6650_project3;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The class QueryRemote.
 */
public class QueryRemote extends UnicastRemoteObject implements Query {

  private static final long serialVersionUID = 1L;
  private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd.hh.MM.ss.ms");
  private Random rand = new Random();
  private static Query serverStub;
  private static String[] serverAddr;
  private static int defaultTimeOutInSec = 5;
  private String putInfo[] = new String[2];
  private static String deleteInfo = null;
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

  private static Object executeWithTimeout(Callable<Object> task) {
    ExecutorService executor = Executors.newCachedThreadPool();
    Future<Object> future = executor.submit(task);
    try {
      return future.get(defaultTimeOutInSec, TimeUnit.SECONDS);
      // move to the operation
    } catch (TimeoutException e) {
      System.out.println("time out. Didn't receive ready confirmation");
    } catch (Exception e) {
      //System.out.println("didn't receive ready confirmation");
      //e.printStackTrace();
    } finally {
      future.cancel(true);
    }
    return false;
  }

  private static boolean putFirstPhaseCommit(String serverAddr, String key,
      String value) {
    Callable<Object> task = new Callable<Object>() {
      public Object call() throws RemoteException, NotBoundException, MalformedURLException {
        serverStub = (Query) Naming.lookup(serverAddr);
        //getTime();
        //System.out.println("Successfully connected to " + serverAddr);
        getTime();
        System.out.println("Sent to " + serverAddr + " : OK to commit? ");
        return serverStub.putCheck(key, value);
      }
    };
    boolean res = (boolean) executeWithTimeout(task);
    if (res) {
      getTime();
      System.out.println("received ready confirmation from " + serverAddr);
      return true;
    } else {
      getTime();
      System.out.println("received NOT ready confirmation from " + serverAddr);
      return false;
    }
  }

  /**
   * put check.
   *
   * return boolean.
   */
  public boolean putCheck(String key, String value) throws RemoteException {
    // use random to mimic a situation in which server is not ready
    Integer randNum = rand.nextInt(5);
    // mock not ready
    if (randNum != 4) {
      putInfo[0] = key;
      putInfo[1] = value;
      System.out.println("put key: " + key + " , value: " + value + " is prepared");
      return true;
    } else {
      System.out.println("isn't prepared");
      return false;
    }
  }

  private static boolean putSecondPhaseCommit(String serverAddr, boolean commit) {
    Callable<Object> task = new Callable<Object>() {
      public Object call() throws RemoteException, NotBoundException, MalformedURLException {
        serverStub = (Query) Naming.lookup(serverAddr);
        getTime();
        //System.out.println("Successfully connected to " + serverAddr);
        if (commit) {
          System.out.println("Sent to Server: commit");
          return serverStub.put();
        } else {
          System.out.println("Sent to Server: abort");
          return serverStub.putAbort();
        }
      }
    };
    if (executeWithTimeout(task).equals("added")) {
      getTime();
      System.out.println(serverAddr + " added");
      return true;
    } else {
      getTime();
      System.out.println(serverAddr + " didn't perform add operation");
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
  public String putGlobal(String key, String value, Integer serverNo) throws RemoteException {
    serverAddr = new String[5];
    serverStub = null;
    System.out.println("--------------------------------------------------------");
    getTime();
    System.out.println(
        "Received from client " + getClientAddress() + ": put key: " + key + ", value: " + value);
    boolean testResult[] = new boolean[4];
    int idx = 0;
    for (int i = 1; i <= 5; i++) {
      if (i == serverNo) {
        //testResult[i] = true;
        continue;
      }
      serverAddr[idx] = "//localhost:20" + i + "0/Server";
      //System.out.println(serverAddr[idx]);
      testResult[idx] = putFirstPhaseCommit(serverAddr[idx], key, value);
      idx++;
    }
    boolean commit = true;
    for (boolean result : testResult) {
      if (!result) {
        commit = false;
        break;
      }
    }
    boolean result[] = new boolean[4];
    for (int i = 0; i < 4; i++) {
      result[i] = putSecondPhaseCommit(serverAddr[i], commit);
    }
    for (boolean res : result) {
      if (!res) {
        return null;
      }
    }
    map.put(key, value);
    getTime();
    System.out.println("Sent to client: key: " + key + ", value: " + value + " has been added");
    return "added";
  }


  @Override
  public String get(String key) throws RemoteException {
    // mock a timeout
    try {
      Thread.sleep(rand.nextInt(6000));
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("--------------------------------------------------------");
    getTime();
    System.out.println("Received from client " + getClientAddress() + ": get key: " + key);
    String result = map.containsKey(key) ? map.get(key) : "not found the key";
    getTime();
    System.out.println("Sent to client: key: " + key + ", value: " + result);
    return result;
  }

  @Override
  public boolean deleteCheck(String key) throws RemoteException {
    if (map.containsKey(key)) {
      deleteInfo = key;
      System.out.println("delete key: " + key + " is prepared");
      return true;
    } else {
      System.out.println("isn't prepared");
      return false;
    }
  }

  private static boolean deleteFirstPhaseCommit(String serverAddr, String key) {
    Callable<Object> task = new Callable<Object>() {
      public Object call() throws RemoteException, NotBoundException, MalformedURLException {
        serverStub = (Query) Naming.lookup(serverAddr);
        getTime();
        //System.out.println("Successfully connected to " + serverAddr);
        System.out.println("Sent to " + serverAddr + " : OK to commit? ");
        return serverStub.deleteCheck(key);
      }
    };
    boolean res = (boolean) executeWithTimeout(task);
    if (res) {
      getTime();
      System.out.println("received ready confirmation from " + serverAddr);
      return true;
    } else {
      getTime();
      System.out.println("received NOT ready confirmation from " + serverAddr);
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


  private static boolean deleteSecondPhaseCommit(String serverAddr, boolean commit) {
    Callable<Object> task = new Callable<Object>() {
      public Object call() throws RemoteException, NotBoundException, MalformedURLException {
        serverStub = (Query) Naming.lookup(serverAddr);
        getTime();
        //System.out.println("Successfully connected to " + serverAddr);
        if (commit) {
          System.out.println("Sent to Server: commit");
          return serverStub.delete();
        } else {
          System.out.println("Sent to Server: abort");
          return serverStub.deleteAbort();
        }
      }
    };
    if (executeWithTimeout(task).equals("deleted")) {
      getTime();
      System.out.println(serverAddr + " deleted");
      return true;
    } else {
      getTime();
      return false;
    }
  }


  @Override
  public String deleteGlobal(String key, Integer serverNo) throws RemoteException {
    serverAddr = new String[5];
    serverStub = null;
    System.out.println("--------------------------------------------------------");
    getTime();
    System.out.println("Received from client " + getClientAddress() + ": delete key: " + key);
    boolean testResult[] = new boolean[4];
    int idx = 0;
    for (int i = 1; i <= 5; i++) {
      if (i == serverNo) {
        //testResult[i] = true;
        continue;
      }
      serverAddr[idx] = "//localhost:20" + i + "0/Server";
      System.out.println(serverAddr[idx]);
      testResult[idx] = deleteFirstPhaseCommit(serverAddr[idx], key);
      idx++;
    }
    boolean commit = true;
    if (!map.containsKey(key)) commit = false;
    for (boolean result : testResult) {
      if (!result) {
        commit = false;
        break;
      }
    }
    boolean result[] = new boolean[4];
    for (int i = 0; i < 4; i++) {
      result[i] = deleteSecondPhaseCommit(serverAddr[i], commit);
    }
    for (boolean res : result) {
      if (!res) {
        return null;
      }
    }
    map.remove(key);
    getTime();
    System.out.println("Sent to client: key: " + key + " has been deleted");
    return "deleted";
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
    System.out.println(
        "received malformed request of length " + line.length() + " from " + getClientAddress());
    getTime();
    System.out.println("Sent to client: " + line + " is invalid ");
    return "invalid";
  }
}