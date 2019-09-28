import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * The class Udp client.
 */
public class UDPClient {

  private DatagramSocket socket = null;
  private int count = 0;
  private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd.hh.MM.ss.ms");
  private final static String QUERY = "Enter PUT [key] [value], GET [key], DELETE [key] or enter BYE to quit: ";
  private final static String QUERY2 = "Enter PUT [key] [value] to populate the database or enter BYE to quit: ";
  private static final Pattern QUIT = Pattern.compile("bye", Pattern.CASE_INSENSITIVE);
  private Schedular schedular = new Schedular();
  private DefaultData defaultData = new DefaultData();
  private byte[] buff = null;
  private Random rand = new Random();
  private Set<Integer> set = new HashSet<>();
  private String fromUser = null;
  private String fromServer = null;

  /**
   * Instantiates a new Udp client.
   *
   * @param address the address
   * @param port the port
   * @throws Exception the exception
   */
  public UDPClient(InetAddress address, int port) throws Exception {
    try {
      socket = new DatagramSocket();
      Scanner sc = new Scanner(System.in);
      String start = "connecting";
      // sending "connecting" to server
      buff = start.getBytes();
      DatagramPacket connectRequest =
          new DatagramPacket(buff, buff.length, address, port);
      socket.send(connectRequest);
      System.out.println("Sent to server: " + start);
      long startTime = System.currentTimeMillis();
      Date startDate = new Date(startTime);
      System.out.println(sdf.format(startDate));
      while (true) {
        Thread.sleep(2000);
        socket.setSoTimeout(4000);
        byte[] buffer = new byte[512];
        try {
          DatagramPacket response = new DatagramPacket(buffer, buffer.length);
          socket.receive(response);
        } catch (SocketTimeoutException e) {

        } catch (ConnectException e) {
          System.err.println("disconnect" + e.getMessage());
        } catch (SocketException e) {
          System.err.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
          System.err.println("IOException: " + e.getMessage());
        }
        fromServer = data(buffer).toString();
        // print out response in log
        if (fromServer != null && fromServer.length() != 0) {
          if (fromServer.equals("BYE")) {
            System.out.println("From Server:" + address + " : " + port + " : " + fromServer);
            long receiveTime = System.currentTimeMillis();
            Date receiveDate = new Date(receiveTime);
            System.out.println(sdf.format(receiveDate));
            System.out.println("Closing connection");
            break;
          }
          long receiveTime = System.currentTimeMillis();
          Date receiveDate = new Date(receiveTime);
          if (fromServer.equals(QUERY) || fromServer.equals(QUERY2)) {
            System.out.println("From Server:" + address + " : " + port + " : " + fromServer);
            System.out.println(sdf.format(receiveDate));
            fromServer = null;
          } else {
            String[] answer = fromServer.split(" ");
            Integer answerID = Integer.valueOf(answer[1]);
            // check if the time span is within limit
            if (schedular.checkValidPeriod(answerID, receiveTime)) {
              System.out.println("From Server:" + address + " : " + port + " : " + fromServer);
              System.out.println(sdf.format(receiveDate));
            } else {
              if (fromUser != null) {
                System.out.println("There is no response.");
                System.out.println(QUERY);
              }
            }
            schedular.delete(answerID);
            fromServer = null;
          }
        } else {
          if (fromUser != null) {
            System.out.println("There is no response.");
            System.out.println(QUERY);
          }
        }

        socket.setSoTimeout(2000);
        fromUser = null;
        if (count < 15) {
          defaultRequest(count, address, port);
          count++;
          continue;
        }
        fromUser = sc.nextLine();
        if (fromUser != null) {
          System.out.println("Client: " + fromUser);
          if (QUIT.matcher(fromUser).matches()) {
            buff = fromUser.getBytes();
            DatagramPacket request =
                new DatagramPacket(buff, buff.length, address, port);
            socket.send(request);
            System.out.println("Sent to server: " + fromUser);
            long sendTime = System.currentTimeMillis();
            Date sendDate = new Date(sendTime);
            System.out.println(sdf.format(sendDate));
          } else {
            Integer ID = getIdNum();
            String query = fromUser + " " + ID + " " + fromUser.length();
            buff = query.getBytes();
            DatagramPacket input =
                new DatagramPacket(buff, buff.length, address, port);
            try {
              socket.send(input);
            } catch (Exception e) {
              System.err.println("Exception: " + e.getMessage());
              break;
            }
            System.out.println("Sent to server: " + fromUser + " ID: " + ID);
            long sendTime = System.currentTimeMillis();
            Date sendDate = new Date(sendTime);
            System.out.println(sdf.format(sendDate));
            schedular.add(ID, sendTime);
          }
        }
      }
    } catch (
        SocketException e) {
      System.err.println("Socket: " + e.getMessage());
    } catch (
        IOException e) {
      System.err.println("Client error: " + e.getMessage());
    } catch (
        Exception e) {
      e.printStackTrace();
      System.err.println("Exception error: " + e.getMessage());
    } finally {
      socket.close();
    }

  }

  /**
   * Gets id num.
   *
   * @return the id num
   */
  public Integer getIdNum() {
    Integer key = rand.nextInt(Integer.MAX_VALUE);
    while (set.contains(key)) {
      key = rand.nextInt(Integer.MAX_VALUE);
    }
    set.add(key);
    return key;
  }

  /**
   * Default request.
   *
   * @param count the count
   * @param address the address
   * @param port the port
   */
  public void defaultRequest(int count, InetAddress address, int port) {
    System.out.println("get input automatically");
    fromUser = defaultData.getDefaultData(count);
    Integer ID = getIdNum();
    String query = fromUser + " " + ID + " " + fromUser.length();
    buff = query.getBytes();
    DatagramPacket input =
        new DatagramPacket(buff, buff.length, address, port);
    try {
      socket.send(input);
    } catch (Exception e) {

    }
    System.out.println("Sent to server: " + fromUser + " ID: " + ID);
    long sendTime = System.currentTimeMillis();
    Date sendDate = new Date(sendTime);
    System.out.println(sdf.format(sendDate));
    schedular.add(ID, sendTime);
    if (count == 15) {
      System.out.println("ending automatically process");
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
// args give message contents and server hostname

    if (args.length != 2) {
      System.out.println(
          "Instruction: java tmp.UDPClient <Host name/IP address> <Port number>");
      System.exit(1);
    }
    UDPClient client = new UDPClient(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
  }
}
