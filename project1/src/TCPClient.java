import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * The class Tcp client.
 */
public class TCPClient {

  private Socket socket = null;
  private PrintWriter out = null;
  private BufferedReader in = null;
  private BufferedReader stdIn = null;
  private Random rand = new Random();
  private Set<Integer> set = new HashSet<>();
  private int count = 0;
  private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd.hh.MM.ss.ms");
  private final static Pattern QUIT = Pattern.compile("bye", Pattern.CASE_INSENSITIVE);
  private final static String QUERY = "Enter PUT [key] [value], GET [key], DELETE [key] or enter BYE to quit: ";
  private final static String QUERY2 = "Enter PUT [key] [value] to populate the database or enter BYE to quit: ";
  private Schedular schedular = new Schedular();
  private DefaultData defaultData = new DefaultData();
  private String fromServer = null, fromUser = null;


  /**
   * Instantiates a new Tcp client.
   *
   * @param address the address
   * @param port the port
   */
  public TCPClient(InetAddress address, int port) {
    try {
      socket = new Socket();
      socket.connect(new InetSocketAddress(address, port), 5000);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      stdIn = new BufferedReader(new InputStreamReader(System.in));
      System.out
          .println("Connected to Server: " + socket.getInetAddress() + " :" + socket.getPort());
      while (true) {
        String fromServer = null, fromUser = null;
//        Thread.sleep(2000);
        socket.setSoTimeout(4000);
        // read from server
        try {
          fromServer = in.readLine();
        } catch (SocketTimeoutException e) {
          //System.err.println("time out");
        } catch (ConnectException e) {
          System.err.println("disconnect" + e.getMessage());
        } catch (SocketException e) {
          System.err.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
          System.err.println("IOException: " + e.getMessage());
        }
        if (fromServer != null) {
          long receiveTime = System.currentTimeMillis();
          Date receiveDate = new Date(receiveTime);
          // closing connection
          if (fromServer.equals("BYE")) {
            System.out.println("Server: " + fromServer);
            System.out.println(sdf.format(receiveDate));
            System.out.println("Closing connection");
            break;
          }
          if (fromServer.equals(QUERY) || fromServer.equals(QUERY2)) {
            System.out.println("Server: " + fromServer + "\n" + sdf.format(receiveDate));
            //fromServer = null;
          } else {
            String[] answer = fromServer.split(" ");
            Integer answerID = Integer.valueOf(answer[1]);
            // check if response is within time limit
            if (schedular.checkValidPeriod(answerID, receiveTime)) {
              System.out.println("Server: " + fromServer + "\n" + sdf.format(receiveDate));
            } else {
              if (fromUser != null) {
                System.out.println("There is no response.");
                System.out.println(QUERY);
              }
              schedular.delete(answerID);
            }
            //fromServer = null;
          }
        } else {
          if (fromUser != null) {
            System.out.println("There is no response.");
            System.out.println(QUERY);
          }
        }
        socket.setSoTimeout(2000);
        fromUser = null;
        // do the automatically client's request five PUT, GET, DELETE
        if (count < 15) {
          defaultRequest(count);
          if (out.checkError()) {
            throw new Exception("server is disconnected");
          }
          count++;
          continue;
        }
        // read from console
        if (stdIn.ready() && (fromUser = stdIn.readLine()) != null) {
          System.out.println("Client: " + fromUser);
          // send quit request to server
          if (QUIT.matcher(fromUser).matches()) {
            out.println(QUIT);
            System.out.println("Sent to server: " + fromUser);
            long sendTime = System.currentTimeMillis();
            Date sendDate = new Date(sendTime);
            System.out.println(sdf.format(sendDate));
          } else {
            // give request a unique ID number
            Integer ID = getIdNum();
            // add ID number and length of request to the sending request
            String query = fromUser + " " + ID + " " + fromUser.length();
            out.println(query);
            System.out.println("Sent to server: " + fromUser + " ID: " + ID);
            long sendTime = System.currentTimeMillis();
            Date sendDate = new Date(sendTime);
            System.out.println(sdf.format(sendDate));
            schedular.add(ID, sendTime);
          }
          if (out.checkError()) {
            throw new Exception("server is disconnected");
          }
        }
      }
    } catch (
        SocketTimeoutException e) {
      System.err.println("SocketTimeoutException: " + e.getMessage());
    } catch (
        SocketException e) {
      System.err.println("SocketException: " + e.getMessage());
    } catch (
        UnknownHostException e) {
      System.err.println("UnknownHostException: " + e.getMessage());
    } catch (
        IOException e) {
      System.err.println("IOException: " + e.getMessage());
    } catch (
        Exception e) {
      System.err.println("Exception: " + e.getMessage());
    } finally {
      try {
        socket.close();
        out.close();
        in.close();
        stdIn.close();
        System.out.println("closed");
      } catch (IOException e) {
        System.err.println("IOException: " + e.getMessage());
      }
    }
  }

  /**
   * Gets id num.
   *
   * @return the id num
   */
  public Integer getIdNum() {
    // generate unique ID number
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
   */
  public void defaultRequest(int count) {
    System.out.println("get input automatically");
    fromUser = defaultData.getDefaultData(count);
    Integer ID = getIdNum();
    String query = fromUser + " " + ID + " " + fromUser.length();
    out.println(query);
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
   * Main.
   *
   * @param args the args
   * @throws Exception the exception
   */
  // Commandline arguments:
  // server IP address (refer to Server IP address from Machine1)
  // port number(same as port number from Machine1)
  public static void main(String args[]) throws Exception {
    if (args.length != 2) {
      System.err.println(
          "Instruction: java tmp.TCPClient <HostName/IP address> <Port number>");
      System.exit(1);
    }
    TCPClient client = new TCPClient(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
  }
}
