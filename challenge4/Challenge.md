# Challenge 4

This week's challenge is to make a client-server chatroom. A bit like IRC, but with one user. Unless
you get onto the Threading at the end, in which case you might have multiple users :-)

There should be two programs that you can run:

- a Server program, which receives messages and prints them on it's local display
- a Client program, which waits for the user to type a message, and then sends it to the Server

## Suggested Methods

In both cases, you will need to use sockets. This is the way clients and servers communicate
reliably (as opposed to datagrams, which are "unreliable" as messages are not guaranteed to be
delivered), typically over TCP.

- The server will need a ServerSocket (which opens a socket on your local machine).
- The client will need a Socket (which opens a socket on a 'remote' machine).
- For the purposes of testing, your 'remote' machine will probably be your same local machine, in
  which case the hostname is localhost.
- You will need to pick a port number. Anything over 1024 will probably not clash, but be aware
  another program may be using it. You could use a command line argument (remember String[] args in
  main?) to change the port number when launching the server.

To do anything with this client-server set up, you will need to do some communication.

- A ServerSocket cannot do anything until a client connects. Look at .accept() for this.
- Both Socket and ServerSocket have a .getInputStream() and .getOutputStream(). Work out how to read
  and write to these.
- Get your client sending messages, and have the server listen and print these out.

## Possible Extensions
- Want to extend this so multiple clients can connect and post messages? Your server needs to support multiple connections, and run code for both connections simultaneously! To do this you need multithreading!

  - To do threading in Java, you need a class that either extends Thread, or implements Runnable.
  - Then each time you accept a connection, create a new version of the Thread, and .run() it.
-     Send Bare Bones instructions to the server, and have the server interpret them.
-  Write an applet or Swing interface for your client.
-  Have your server perform certain tasks (not "Knock Knock" though ;-) )
-  Implement the IRC standard: http://blog.initprogram.com/2010/10/14/a-quick-basic-primer-on-the-irc-protocol/

