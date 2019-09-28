package cs6650_project2;

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
  public String put(String key, String value) throws RemoteException;

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
  public String delete(String key) throws RemoteException;

  /**
   * Invalid query string.
   *
   * @param line the line
   * @return the string
   * @throws RemoteException the remote exception
   */
  public String invalidQuery(String line) throws RemoteException;
}