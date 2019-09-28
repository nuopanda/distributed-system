package cs6650_project3;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;

/**
 * The class FirstServer.
 */
public class FirstServer {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    try {
      LocateRegistry.createRegistry(2010);
      Query stub = new QueryRemote();
      String address = "//localhost:2010/Server";
      Naming.rebind(address, stub);
      System.out.println("FirstServer is ready. Address: " + address);
    } catch (Exception e) {
      System.err.println("FirstServer exception: " + e.toString());
      e.printStackTrace();
    }
  }

}

