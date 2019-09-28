package cs6650_project3;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * The class Fourth server.
 */
public class FourthServer {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    try {
      LocateRegistry.createRegistry(2040);
      Query stub = new QueryRemote();
      String address = "//localhost:2040/Server";
      Naming.rebind(address, stub);
      System.out.println("FourthServer is ready. Address: " + address);
    } catch (Exception e) {
      System.err.println("FourthServer exception: " + e.toString());
      e.printStackTrace();
    }
  }
}
