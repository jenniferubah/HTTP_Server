package jennifer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;

    public void start(int port){

        try{

            serverSocket = new ServerSocket(port);
            Socket connSocket = serverSocket.accept();

            new RequestHandler(connSocket).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class RequestHandler extends Thread {

        private PrintWriter writer;
        private BufferedReader reader;
        private Socket connSocket;

        public RequestHandler(Socket connSocket) {
            this.connSocket = connSocket;
        }

        public void run(){

            try{

                writer = new PrintWriter(connSocket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));

                String request;
                StringBuffer buffer = new StringBuffer();
                while((request = reader.readLine()) != null){
                    if(request.isEmpty()){
                        break;
                    }
                    buffer.append(request);
                    System.out.println(request);
                }

                writer.println("Hello World");

                writer.close();
                reader.close();
                connSocket.close();

            } catch (IOException e){
                e.printStackTrace();
            }

/*            try {
                writer.close();
                reader.close();
                connSocket.close();
            } catch (IOException e){
                e.printStackTrace();
            }*/
        }
    }
}
