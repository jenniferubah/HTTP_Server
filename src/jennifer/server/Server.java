package jennifer.server;

import java.io.*;
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

        private BufferedReader reader;
        private Socket connSocket;

        public RequestHandler(Socket connSocket) {
            this.connSocket = connSocket;
        }

        public void run(){

            try{

                reader = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));

                String request = reader.readLine();

                // parse the request
                String page = parseRequest(request);
                System.out.println("my page " + page);

                // send response based on request
                sendResponse(page);


                reader.close();
                connSocket.close();

            } catch (IOException e){
                e.printStackTrace();
            }

        }

        public String parseRequest(String request){

            String[] firstLine = request.split(" ");
            String page = firstLine[1];
            return page;
        }

        public void sendResponse(String page){

            String statusCode = null;
            String date = "Date: Thur, 14 Mar 2019" + "\r\n";
            String serverName = "Server: Phoenix Server" + "\r\n";
            String contentType = "Content-Type: text/html" + "\r\n";
            String contentLength;

            if(page.equals("/index")){


                String file = "src/jennifer/server/resources/index.html";
                File path = new File(file);
                String absolutePath = path.getAbsolutePath();

                statusCode = "HTTP/1.1 200 OK" + "\r\n";
                contentLength = Long.toString(path.length());


                try(FileInputStream fileReader = new FileInputStream(absolutePath);
                    DataOutputStream byteWriter = new DataOutputStream(connSocket.getOutputStream())){

                    //writing response headers to the connecting socket (client)
                    byteWriter.writeBytes(statusCode);
                    byteWriter.writeBytes(date);
                    byteWriter.writeBytes(serverName);
                    byteWriter.writeBytes(contentType);
                    byteWriter.writeBytes(contentLength);

                    //writing response body to the connecting socket (client)
                    int byteRead;
                    byte[] buffer = new byte[1024];
                    while((byteRead = fileReader.read(buffer)) != -1){
                        byteWriter.write(buffer, 0, byteRead);

                    }

                    System.out.println(statusCode
                                        + date
                                        + serverName
                                        + contentType
                                        + contentLength);


                } catch (FileNotFoundException e){
                    System.out.println("File not found");
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("something went wrong while reading file");
                }
            }


        }

    }
}
