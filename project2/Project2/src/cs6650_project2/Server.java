package cs6650_project2;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;

/**
 * The class Server.
 */
public class Server {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    try {
      LocateRegistry.createRegistry(2020);
      Query stub = new QueryRemote();
      String address = "//localhost:2020/Server";
      Naming.rebind(address, stub);
      System.out.println("Server is ready. Address: " + address);
    } catch (Exception e) {
      System.err.println("Server exception: " + e.toString());
      e.printStackTrace();
    }
  }

}

