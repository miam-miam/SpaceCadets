enum MessageId {
  INIT, // Sent when client first connects, (contains )
  LIST_USER, // Sent to new client when they first connect (It contains a hashmap from user_id to username)
  ADD_USER, // Sent to all clients when new client connects
  MESSAGE, // Sent to server when client sends a message.
  MESSAGE_RECEIVED; // Sent to client when a different client sends a message.

  public static final MessageId[] values = values();
}
