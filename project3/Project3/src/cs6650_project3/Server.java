package cs6650_project3;


import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;


public class Server {

  private static String addr = "//localhost:";

  public static void main(String[] args) {

    if (args.length != 1) {
      System.err.println(
          "Instruction: input Server No. (1-5)");
      System.exit(1);
    }
    int portNum = Integer.valueOf(args[0]);
    String serverAddr = null;
    String serverQueryAddr = null;
    Integer port = null;
    switch (portNum) {
      case 1:
        port = 2010;
        serverAddr = addr + "2010/Server";
        serverQueryAddr = addr + "2010/ServerQuery";
        break;
      case 2:
        port = 2020;
        serverAddr = addr + "2020/Server";
        serverQueryAddr = addr + "2020/ServerQuery";
        break;
      case 3:
        port = 2030;
        serverAddr = addr + "2030/Server";
        serverQueryAddr = addr + "2030/ServerQuery";
        break;
      case 4:
        port = 2040;
        serverAddr = addr + "2040/Server";
        serverQueryAddr = addr + "2040/ServerQuery";
        break;
      case 5:
        port = 2050;
        serverAddr = addr + "2050/Server";
        serverQueryAddr = addr + "2050/ServerQuery";
        break;
      default:
        System.err.println(
            "Instruction: input Server No. (1-5)");
        System.exit(1);
    }
    try {
      LocateRegistry.createRegistry(port);
      Query stub = new QueryRemote();
      ServerQuery serverStub = new ServerQueryRemote();
      Naming.rebind(serverAddr, stub);
      Naming.rebind(serverQueryAddr, serverStub);
      System.out.println("Server " + port + " is ready. Address: " + serverAddr);
    } catch (Exception e) {
      System.err.println("Server " + port + " exception: " + e.toString());
      e.printStackTrace();
    }
  }
}
