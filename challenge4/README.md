# Challenge 4
The [challenge](Challenge.md) for this week was to create a client-server chatroom. My implementation allows for multiple clients and is even end-to-end encrypted!

## Methodology
To do this I had to use quite a lot of threads and as we all know once there's threads, we need a way of synchronising them. Fortunately, in Java (most likely due to the JVM) locks are incredibly easy to make. All you need to do is use the synchronised keyword on function calls and the JVM will ensure you don't both try to read from the same value or anything stupid like that. In practice this meant I had a state class that kept a list of sent messages that were then discarded once all the connections had acted upon the messages.

To send information between the client and the server I used the following packet scheme. The first 4 bytes received were for the length of the data being sent. This is necessary as the TCP/IP protocol is stream based and as such cannot guarantee that a single write call to the socket will result in a single packet being sent over the network. The next byte was used to find what type of message was sent and finally the rest of the data. I decided that since this was just a small project there was no need to over-generalise as such both the client and server share the exact same codebase to create and read the messages.

Finally, the end-to-end encryption is done through the use of the `/password` command. When this command is called the password is stored by the client and a hashed version of it (using a salt provided by the server so that each client uses the same salt) to the server. This ensures that the server has no way of decrypting the messages sent as the password is needed for that. But also ensures that clients are not flooded with messages they are unable to decrypt, Since the server will only send the message to other clients that have given the server the same hash password. As for the actual encryption itself I decided to use a [Caesar cipher](https://en.wikipedia.org/wiki/Caesar_cipher) as it is often used and as such I imagine it must be quite cryptographically secure.

## Overview
- ClientHandler which is started in a separate thread every time a client connects.
- The State which holds previous messages as well as currently connected clients (a word of warning this is only updated once we try to send something down the socket).
- The Client and Server which are the two main ways you can start your program. They currently connect to localhost on port 3001 but this could easily be changed to anything you like.
- A BCrypt hasher made by Damien Miller which can be found [here](https://gist.github.com/coderberry/651613).
