package cs6650_project3;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface Query.
 */
public interface Query extends Remote {

  /**
   * Put string.
   *
   * @param key the key
   * @param value the value
   * @return the string
   * @throws RemoteException the remote exception
   */
  public String putGlobal(String key, String value, Integer serverNo) throws RemoteException;

  /**
   * Get string.
   *
   * @param key the key
   * @return the string
   * @throws RemoteException the remote exception
   */
  public String get(String key) throws RemoteException;

  /**
   * Delete string.
   *
   * @param key the key
   * @return the string
   * @throws RemoteException the remote exception
   */
  public String deleteGlobal(String key, Integer serverNo) throws RemoteException;

  /**
   * Invalid query string.
   *
   * @param line the line
   * @return the string
   * @throws RemoteException the remote exception
   */
  public String invalidQuery(String line) throws RemoteException;

  public boolean test(String key, String value) throws RemoteException;

  public String put() throws RemoteException;

  public String putAbort() throws RemoteException;
}