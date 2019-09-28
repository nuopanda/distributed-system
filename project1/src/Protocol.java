import java.util.Random;
import java.util.regex.Pattern;

/**
 * The class Protocol.
 */
public class Protocol {

  private ServerStatus status;
  private Database database;
  private static final Pattern QUIT = Pattern.compile("bye", Pattern.CASE_INSENSITIVE);
  private static final Pattern PUT = Pattern.compile("put", Pattern.CASE_INSENSITIVE);
  private static final Pattern DELETE = Pattern.compile("delete", Pattern.CASE_INSENSITIVE);
  private static final Pattern GET = Pattern.compile("get", Pattern.CASE_INSENSITIVE);
  private static final String message = " !!received unsolicited response acknowledging unknown PUT/GET/DELETE with an invalid KEY";
  private Random rand = new Random();

  /**
   * Instantiates a new Protocol.
   *
   * @param database the database
   */
  public Protocol(Database database) {
    this.status = ServerStatus.WAITING;
    this.database = database;
  }

  /**
   * Process input string.
   *
   * @param input the input
   * @return the string
   */
  public String processInput(String input) {
    StringBuilder output = new StringBuilder();
    switch (this.status) {
      case WAITING:
        output.append("Enter PUT [key] [value] to populate the database or enter BYE to quit: ");
        this.status = ServerStatus.GET_OPERATION;
        break;
      case GET_OPERATION:
        if (QUIT.matcher(input).matches()) {
          output.append("BYE");
          break;
        }
        // mock a delay of server side
        try {
          //Thread.sleep(30000);
          Thread.sleep(rand.nextInt(6000));
        } catch (Exception e) {

        }
        // split the input and process
        String[] inputs = input.split(" ");
        // Use regex to check inputs and check the length of inputs
        if (PUT.matcher(inputs[0]).matches() && inputs.length == 5) {
          database.put(inputs[1], inputs[2]);
          output.append(
              "ID: " + inputs[3] + " key: " + inputs[1] + " value: " + inputs[2] + " are added.");
        } else if (DELETE.matcher(inputs[0]).matches() && inputs.length == 4) {
          // call database's method
          String result = database.delete(inputs[1]);
          if (result.equals("DELETED")) {
            output.append("ID: " + inputs[2] + " key: " + inputs[1] + " is deleted.");
          }else{
            output.append("ID: " + inputs[inputs.length - 2]);
            output.append(message);
          }
        } else if (GET.matcher(inputs[0]).matches() && inputs.length == 4) {
          String result = database.get(inputs[1]);
          if (!result.equals("NOTFOUND")) {
            output.append("ID: " + inputs[2] + " key: " + inputs[1] + " value is " + result + ".");
          }else{
            output.append("ID: " + inputs[inputs.length - 2]);
            output.append(message);
          }
        } else {
          output.append("ID: " + inputs[inputs.length - 2]);
          output.append(message);
        }
        this.status = ServerStatus.SENT_RESULT;
      case SENT_RESULT:
        output.append(" Enter PUT [key] [value], GET [key], DELETE [key] or enter BYE to quit: ");
        this.status = ServerStatus.GET_OPERATION;
        break;
      default:
        output.append("...");
    }
    return output.toString();
  }
}