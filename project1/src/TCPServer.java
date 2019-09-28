import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The class Tcp server.
 */
public class TCPServer {


  InetAddress inetAddress;
  ServerSocket serverSocket;
  private Database database;
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd.hh.MM.ss.ms");
  private final static String message = "received unsolicited response acknowledging unknown PUT/GET/DELETE with an invalid KEY";

  /**
   * Instantiates a new Tcp server.
   *
   * @param port the port
   * @param database the database
   */
  public TCPServer(Integer port, Database database) {
    System.out.println("Waiting...");
    try {
      inetAddress = InetAddress.getLocalHost();
      System.out.println("Server IP address: " + inetAddress.getHostAddress());
      System.out.println("Server host name: " + inetAddress.getHostName());
      serverSocket = new ServerSocket(port, 50, inetAddress);
      // also can use localhost as IP address, in that case, client should enter "127.0.0.1 32000" as command line argument
      //serverSocket = new ServerSocket(port);
    } catch (Exception e) {

    }
    // use try with resource, will close the enclosed resources automatically
    try (
        Socket clientSocket = serverSocket.accept();
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream()));
    ) {
      this.database = database;
      // get the ip address of the client
      String clientAddress = clientSocket.getInetAddress().getHostAddress();
      String inputLine = null, outputLine = null;
      System.out.println("Connected from : " + clientAddress + ":" + clientSocket.getPort());
      Protocol protocol = new Protocol(database);
      outputLine = protocol.processInput(null);
      while (true) {
        if (outputLine != null) {
          // sending response for malformed request
          if (outputLine.equals(message)) {
            String[] input = inputLine.split(" ");
            Integer fromUserLen = Integer.valueOf(input[input.length - 1]);
            String ID = input[input.length - 2];
            System.out.println(
                "received malformed request of length " + fromUserLen + " from " + clientAddress
                    + " : " + clientSocket.getPort());
          }
          out.println(outputLine);
          System.out.println("Sending back to client:" + outputLine);
          long sendTime = System.currentTimeMillis();
          Date sendDate = new Date(sendTime);
          System.out.println(sdf.format(sendDate));
          outputLine = null;
        }
        // receiving request from client
        if ((inputLine = in.readLine()) != null) {
          System.out.println("From Client: " + clientAddress + " " + inputLine);
          long receiveTime = System.currentTimeMillis();
          Date receiveDate = new Date(receiveTime);
          System.out.println(sdf.format(receiveDate));
          outputLine = protocol.processInput(inputLine);
          // closing the connection
          if (outputLine.equals("BYE")) {
            System.out.println("Closing connection");
            out.println(outputLine);
            long endTime = System.currentTimeMillis();
            Date endDate = new Date(endTime);
            System.out.println(sdf.format(endDate));
            break;
          }
        }
      }
    } catch (SocketTimeoutException e) {
      System.err.println("SocketTimeoutException: " + e.getMessage());
    } catch (SocketException e) {
      System.err.println("SocketException: " + e.getMessage());
    } catch (UnknownHostException e) {
      System.err.println("UnknownHostException: " + e.getMessage());
    } catch (IOException e) {
      System.err.println("IOException: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Exception: " + e.getMessage());
    }
  }

  /**
   * Main.
   *
   * @param args the args
   * @throws Exception the exception
   */
  public static void main(String args[]) throws Exception {
    Database database = new Database();
    // check of length of arguments
    if (args.length != 1 || !checkInput(args[0])) {
      System.out.println(
          "Instruction: java tmp.TCPServer <Port number>");
      System.exit(1);
    }
    TCPServer server = new TCPServer(Integer.parseInt(args[0]), database);
  }

  /**
   * Check if input are digits.
   *
   * @param input the input
   * @return the boolean
   */
  public static boolean checkInput(String input) {
    int len = input.length();
    for (int i = 0; i < len; i++) {
      if (!Character.isDigit(input.charAt(i))) {
        return false;
      }
    }
    return true;
  }
}
