package cs6650_project3;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * The class Second server.
 */
public class SecondServer {

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
      System.out.println("SecondServer is ready. Address: " + address);
    } catch (Exception e) {
      System.err.println("SecondServer exception: " + e.toString());
      e.printStackTrace();
    }
  }
}
