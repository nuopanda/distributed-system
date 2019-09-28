package cs6650_project3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The class Client.
 */
public class Client {

  private static Query server_stub;
  private static int defaultTimeOutInSec = 5;
  private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd.hh.MM.ss.ms");

  /**
   * Execute with Timeout.
   */
  private static Object executeWithTimeout(Callable<Object> task) {
    ExecutorService executor = Executors.newCachedThreadPool();
    Future<Object> future = executor.submit(task);
    try {
      return future.get(defaultTimeOutInSec, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      System.out.println("No response from client. Continue for the next query.");
    } catch (Exception e) {
      // System.err.println("Non-timeout exception found.");
      //e.printStackTrace();
    } finally {
      future.cancel(true);
    }
    return null;
  }

  /**
   * Gets current timestamp.
   */
  public static void getTime() {
    long sendTime = System.currentTimeMillis();
    Date sendDate = new Date(sendTime);
    System.out.println(sdf.format(sendDate));
  }

  /**
   * Put key and val into the map.
   */
  private static void put(String key, String val, Integer serverNo) {
    Callable<Object> task = new Callable<Object>() {
      public Object call() throws RemoteException {
        getTime();
        System.out.println("Sent to FirstServer: put " + key + ", val " + val);
        return server_stub.putGlobal(key, val, serverNo);
      }
    };
    if (executeWithTimeout(task) != null) {
      getTime();
      System.out.println("put(" + key + ", " + val + ") completed.");
    }else{
      getTime();
      System.out.println("put(" + key + ", " + val + ") cannot complete.");
    }
  }

  /**
   * Get key from the map.
   */
  private static void get(String key) {
    Callable<Object> task = new Callable<Object>() {
      public Object call() throws RemoteException {
        getTime();
        System.out.println("Sent to FirstServer: get " + key);
        return server_stub.get(key);
      }
    };
    String val = (String) executeWithTimeout(task);
    if (val != null) {
      getTime();
      System.out.println("get(" + key + ") returned " + val + ".");
    }
  }

  /**
   * Delete key from the map.
   */
  private static void delete(String key, Integer serverNo) {
    Callable<Object> task = new Callable<Object>() {
      public Object call() throws RemoteException {
        getTime();
        System.out.println("Sent to FirstServer: delete " + key);
        return server_stub.deleteGlobal(key, serverNo);
      }
    };
    String result = (String) executeWithTimeout(task);
    if (result != null) {
      getTime();
      System.out.println(result);
    }
  }

  /**
   * Send invalid Query.
   */
  private static void invalidQuery(String line) {
    Callable<Object> task = new Callable<Object>() {
      public Object call() throws RemoteException {
        getTime();
        System.out.println("Sent to FirstServer: " + line);
        return server_stub.invalidQuery(line);
      }
    };
    if (executeWithTimeout(task) != null) {
      getTime();
      System.out.println(line + " is an invalid query.");
    }
  }


  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws MalformedURLException the malformed url exception
   * @throws RemoteException the remote exception
   * @throws NotBoundException the not bound exception
   */
  public static void main(String[] args)
      throws MalformedURLException, RemoteException, NotBoundException {
    if (args.length != 1) {
      System.err.println(
          "Instruction: input Server No. (1-5)");
      System.exit(1);
    }
    Integer serverNo = Integer.valueOf(args[0]);
    String serverAddr = null;
    switch (serverNo) {
      case 1:
        serverAddr = "//localhost:2010/Server";
        break;
      case 2:
        serverAddr = "//localhost:2020/Server";
        break;
      case 3:
        serverAddr = "//localhost:2030/Server";
        break;
      case 4:
        serverAddr = "//localhost:2040/Server";
        break;
      case 5:
        serverAddr = "//localhost:2050/Server";
        break;
      default:
        System.err.println(
            "Instruction: input Server No. (1-5)");
        System.exit(1);
    }
    server_stub = (Query) Naming.lookup(serverAddr);
    System.out.println("Successfully connected to " + serverAddr);
    System.out.println("Executing the 15 default commands...");
    put("one", "1", serverNo);
    put("two", "2", serverNo);
    put("three", "3", serverNo);
    put("four", "4", serverNo);
    put("five", "5", serverNo);
    get("one");
    get("two");
    get("three");
    get("four");
    get("five");
    delete("one", serverNo);
    delete("two", serverNo);
    delete("three", serverNo);
    delete("four", serverNo);
    delete("five", serverNo);

    System.out.println("Waiting for user input...");
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      try {
        String line = reader.readLine();
        String[] tokens = line.split(" ");
        for (String s : tokens) {
          if (s == null || s.isEmpty()) {
            System.err.println("Invalid command; found a null or empty token.");
            invalidQuery(line);
            continue;
          }
        }
        if (tokens[0].equals("put")) {
          if (tokens.length != 3) {
            System.err.println("Invalid user input for PUT command: " + line);
            invalidQuery(line);
            continue;
          }
          put(tokens[1], tokens[2], serverNo);
        } else if (tokens[0].equals("get")) {
          if (tokens.length != 2) {
            System.err.println("Invalid user input for GET command: " + line);
            invalidQuery(line);
            continue;
          }
          get(tokens[1]);
        } else if (tokens[0].equals("delete")) {
          if (tokens.length != 2) {
            System.err.println("Invalid user input for DELETE command: " + line);
            invalidQuery(line);
            continue;
          }
          delete(tokens[1], serverNo);
        } else {
          System.err.println("Invalid function: " + line);
          invalidQuery(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
