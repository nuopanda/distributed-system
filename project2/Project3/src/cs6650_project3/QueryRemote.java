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
  private static Query[] serverStub;
  private static String[] serverAddr;
  private static int defaultTimeOutInSec = 5;
  private static final String message = "ready";
  private static final String message2 = "not ready";
  private static String putInfo[];
  private static String deleteInfo;
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
      System.out.println("didn't receive ready confirmation");
    } catch (Exception e) {
      System.out.println("didn't receive ready confirmation");
      e.printStackTrace();
    } finally {
      future.cancel(true);
    }
    return false;
  }

  private static boolean firstPhaseCommit(String serverAddr, Integer num, String key,
      String value) {
    Callable<Object> task = new Callable<Object>() {
      public Object call() throws RemoteException {
        getTime();
        System.out.println("Sent to " + serverAddr + " : OK to commit? ");
        return serverStub[num].test(key, value);
      }
    };
    if ((boolean) executeWithTimeout(task)) {
      getTime();
      System.out.println("received ready confirmation from " + serverAddr);
      return true;
    } else {
      getTime();
      System.out.println("received NOT ready confirmation from " + serverAddr);
      return false;
    }
  }

  public boolean test(String key, String value) throws RemoteException {
    putInfo[0] = key;
    putInfo[0] = value;
    System.out.println("prepared");
    return true;
  }


  private static boolean secondPhaseCommit(String serverAddr, Integer num, boolean commit) {
    Callable<Object> task = new Callable<Object>() {
      public Object call() throws RemoteException {
        getTime();
        if (commit) {
          System.out.println("Sent to Server: commit");
          return serverStub[num].put();
        } else {
          System.out.println("Sent to Server: abort");
          return serverStub[num].putAbort();
        }
      }
    };
    if (executeWithTimeout(task).equals("added")) {
      getTime();
      System.out.println(serverAddr + "added");
      return true;
    } else {
      getTime();
      return false;
    }
  }

  public String put() {
    map.put(putInfo[0], putInfo[1]);
    putInfo[0] = null;
    putInfo[0] = null;
    return "added";
  }

  public String putAbort() {
    putInfo[0] = null;
    putInfo[1] = null;
    return "aborted";
  }

  @Override
  public String putGlobal(String key, String value, Integer serverNo) throws RemoteException {
    serverAddr = new String[5];
    serverStub = new Query[4];
//    try {
////      Thread.sleep(rand.nextInt(6000));
////    } catch (InterruptedException e) {
////      // TODO Auto-generated catch block
////      e.printStackTrace();
////    }
    getTime();
    System.out.println(
        "Received from client " + getClientAddress() + ": put key: " + key + ", value: " + value);
    boolean testResult[] = new boolean[4];
    for (int i = 0; i < 5; i++) {
      if (i + 1 == serverNo) {
        continue;
      }
      serverAddr[i] = "//localhost:20" + serverNo + "0/Server";
      try {
        serverStub[i] = (Query) Naming.lookup(serverAddr[i]);
        getTime();
        System.out.println("Successfully connected to " + serverAddr[i]);
        testResult[i] = firstPhaseCommit(serverAddr[i], i, key, value);
      } catch (NotBoundException e) {
        System.err.println("Can't connect to " + serverAddr[i]);
        e.printStackTrace();
      } catch (MalformedURLException e) {
        System.err.println("Can't connect to " + serverAddr[i]);
        e.printStackTrace();
      }
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
      result[i] = secondPhaseCommit(serverAddr[i], i, commit);
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
    try {
      Thread.sleep(rand.nextInt(6000));
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    getTime();
    System.out.println("Received from client " + getClientAddress() + ": get key: " + key);
    String result = map.containsKey(key) ? map.get(key) : "not found the key";
    getTime();
    System.out.println("Sent to client: key: " + key + ", value: " + result);
    return result;
  }

  @Override
  public String deleteGlobal(String key, String serverNo)  throws RemoteException {
    serverAddr = new String[5];
    serverStub = new Query[4];
    try {
      Thread.sleep(rand.nextInt(6000));
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    getClientAddress();
    getTime();
    System.out.println("Received from client " + getClientAddress() + ": delete key: " + key);
    String result;
    if (map.containsKey(key)) {
      map.remove(key);
      result = "deleted key: " + key;
    } else {
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
    System.out.println(
        "received malformed request of length " + line.length() + " from " + getClientAddress());
    getTime();
    System.out.println("Sent to client: " + line + " is invalid ");
    return "invalid";
  }
}