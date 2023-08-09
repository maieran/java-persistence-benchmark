package de.uniba.dsg.wss.services;

/**
 * Can be thrown whenever some sort of issue prevents a data transaction involving Redis from
 * successful completion.
 *
 * @author Johannes Manner
 * @author Andre Maier
 */
public class RedisTransactionException extends RuntimeException {

  private static final long serialVersionUID = 5089174648043655677L;

  public RedisTransactionException(String message) {
    super(message);
  }
}
