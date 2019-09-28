package cs6650_project3;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * The class Third server.
 */
public class ThirdServer {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    try {
      LocateRegistry.createRegistry(2030);
      Query stub = new QueryRemote();
      String address = "//localhost:2030/Server";
      Naming.rebind(address, stub);
      System.out.println("ThirdServer is ready. Address: " + address);
    } catch (Exception e) {
      System.err.println("ThirdServer exception: " + e.toString());
      e.printStackTrace();
    }
  }
}
