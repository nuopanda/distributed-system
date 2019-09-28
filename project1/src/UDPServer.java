
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The class Udp server.
 */
public class UDPServer {

  InetAddress inetAddress;
  private Database database = new Database();
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd.hh.MM.ss.ms");
  private DatagramSocket socket;
  private Protocol protocol = new Protocol(database);
  private String outputLine;
  private static final String message = "received unsolicited response acknowledging unknown PUT/GET/DELETE with an invalid KEY";
  private String query = null;

  /**
   * Instantiates a new Udp server.
   *
   * @param port the port
   */
  public UDPServer(int port) {
    // connect to client
    try {
      socket = new DatagramSocket(port);
      inetAddress = InetAddress.getLocalHost();
      System.out.println("Server IP address: " + inetAddress.getHostAddress());
      System.out.println("Server host name: " + inetAddress.getHostName());
      byte[] startBuffer = new byte[512];
      // receive "connecting " from client
      DatagramPacket start = new DatagramPacket(startBuffer, startBuffer.length);
      socket.receive(start);
      InetAddress clientAddress = start.getAddress();
      int clientPort = start.getPort();
      String startConfirmation = data(startBuffer).toString();
      System.out
          .println("From Client:" + clientAddress + " : " + clientPort + " : " + startConfirmation);
      long receiveStartTime = System.currentTimeMillis();
      Date receiveStartDate = new Date(receiveStartTime);
      System.out.println(sdf.format(receiveStartDate));
      outputLine = protocol.processInput(null);
      while (true) {
        if (outputLine != null) {
          byte[] returnBuffer = outputLine.getBytes();
          // deal with malformed request
          if (outputLine.equals(message)) {
            String[] input = query.split(" ");
            Integer fromUserLen = Integer.valueOf(input[input.length - 1]);
            System.out.println(
                "received malformed request of length " + fromUserLen + " from " + clientAddress
                    + " : " + clientPort);
          }
          System.out.println("Sending back to client:" + outputLine);
          DatagramPacket response = new DatagramPacket(returnBuffer, returnBuffer.length,
              clientAddress, clientPort);
          socket.send(response);
          long sendTime = System.currentTimeMillis();
          Date sendDate = new Date(sendTime);
          System.out.println(sdf.format(sendDate));
        }
        if (outputLine.equals("BYE")) {
          System.out.println("Closing connection");
          long sendTime = System.currentTimeMillis();
          Date sendDate = new Date(sendTime);
          System.out.println(sdf.format(sendDate));
          break;
        }
        // read from client
        byte[] buffer = new byte[512];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        socket.receive(request);
        query = data(buffer).toString();
        System.out.println("From Client:" + clientAddress + " : " + clientPort + " : " + query);
        long receiveTime = System.currentTimeMillis();
        Date receiveDate = new Date(receiveTime);
        System.out.println(sdf.format(receiveDate));
        outputLine = protocol.processInput(query);
      }
    } catch (SocketException e) {
      System.out.println("Socket: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("IO: " + e.getMessage());
    } finally {
      socket.close();
    }
  }


  /**
   * Data string builder.
   *
   * @param buffer the buffer
   * @return the string builder
   */
  public static StringBuilder data(byte[] buffer) {
    if (buffer == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    int i = 0;
    while (buffer[i] != 0) {
      sb.append((char) buffer[i]);
      i++;
    }
    return sb;
  }

  /**
   * Main.
   *
   * @param args the args
   * @throws Exception the exception
   */
  public static void main(String args[]) throws Exception {
    if (args.length != 1 || !checkInput(args[0])) {
      System.out.println("Instruction: input the java UDPServer <Port Number>");
      System.exit(1);
    }
    int port = Integer.valueOf(args[0]);
    UDPServer server = new UDPServer(port);

  }

  /**
   * Check input boolean.
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
