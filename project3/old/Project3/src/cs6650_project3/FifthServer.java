package cs6650_project3;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * The class Fifth server.
 */
public class FifthServer {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    try {
      LocateRegistry.createRegistry(2050);
      Query stub = new QueryRemote();
      String address = "//localhost:2050/Server";
      Naming.rebind(address, stub);
      System.out.println("FifthServer is ready. Address: " + address);
    } catch (Exception e) {
      System.err.println("FifthServer exception: " + e.toString());
      e.printStackTrace();
    }
  }
}
