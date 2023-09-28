package de.uniba.dsg.wss.services;

public class AerospikeTransactionException extends RuntimeException {

  private static final long serialVersionUID = 5089174648043655677L;

  public AerospikeTransactionException(String message) {
    super(message);
  }
}
