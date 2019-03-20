package jennifer.server;

/**
* This is a simple HTTP server that receives requests
* and sends responses to multiple clients.
*
*
* @author Jennifer Ubah
* @version 1.0
* @since 2019-03-12
*
* */

public class Main {

    public static void main(String[] args) {
        Server server = new Server();
        server.start(8888);
    }
}
