package cs6650_project3;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ServerQuery extends Remote {

  /**
   * Get string.
   *
   * @param key the key
   * @return the string
   * @throws RemoteException the remote exception
   */
  public String get(String key) throws RemoteException;

  /**
   * Put check boolean.
   *
   * @param key the key
   * @param value the value
   * @return the boolean
   * @throws RemoteException the remote exception
   */
  public boolean putCheck(String key, String value) throws RemoteException;

  /**
   * Put operation.
   *
   * @return the string
   * @throws RemoteException the remote exception
   */
  public String put() throws RemoteException;

  /**
   * Put abort.
   *
   * @return the string
   * @throws RemoteException the remote exception
   */
  public String putAbort() throws RemoteException;

  /**
   * Delete check.
   *
   * @param key the key
   * @return the boolean
   * @throws RemoteException the remote exception
   */
  public boolean deleteCheck(String key) throws RemoteException;

  /**
   * Delete operation.
   *
   * @return the string
   * @throws RemoteException the remote exception
   */
  public String delete() throws RemoteException;

  /**
   * Delete abort.
   *
   * @return the string
   * @throws RemoteException the remote exception
   */
  public String deleteAbort() throws RemoteException;
}