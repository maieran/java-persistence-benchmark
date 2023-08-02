package de.uniba.dsg.wss.service;

/**
 * Can be thrown whenever some sort of issue prevents a data transaction involving Redis from
 * successful completion.
 *
 * @author Johannes Manner
 * @author Andre Maier
 */
public class MsTransactionException extends RuntimeException {

  private static final long serialVersionUID = 5089174648043655677L;

  public MsTransactionException(String message) {
    super(message);
  }
}
