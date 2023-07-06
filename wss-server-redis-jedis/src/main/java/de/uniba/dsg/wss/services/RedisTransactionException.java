package de.uniba.dsg.wss.services;

public class RedisTransactionException extends RuntimeException {

  private static final long serialVersionUID = 5089174648043655677L;

  public RedisTransactionException(String message) {
    super(message);
  }
}
