/**
 * The class Default data.
 * client default requests.
 */
public class DefaultData {

  private String[] defaultData;

  /**
   * Instantiates a new Default data.
   */
  public DefaultData() {
    defaultData = new String[15];
    defaultData[0] = "put one 1";
    defaultData[1] = "put two 2";
    defaultData[2] = "put three 3";
    defaultData[3] = "put four 4";
    defaultData[4] = "put five 5";
    defaultData[5] = "get one";
    defaultData[6] = "get two";
    defaultData[7] = "get three";
    defaultData[8] = "get four";
    defaultData[9] = "get five";
    defaultData[10] = "delete one";
    defaultData[11] = "delete two";
    defaultData[12] = "delete three";
    defaultData[13] = "delete four";
    defaultData[14] = "delete five";

  }

  /**
   * Gets default data.
   *
   * @param count the count
   * @return the default data
   */
  public String getDefaultData(int count) {
    return this.defaultData[count];
  }


}
