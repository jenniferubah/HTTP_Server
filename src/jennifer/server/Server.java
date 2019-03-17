package jennifer.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    private ServerSocket serverSocket;

    public void start(int port) {

        try{

            serverSocket = new ServerSocket(port);
            Socket connSocket = serverSocket.accept();

            while(true){

                new RequestHandler(connSocket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class RequestHandler extends Thread {


        private Socket connSocket;

        public RequestHandler(Socket connSocket) {
            this.connSocket = connSocket;
        }

        public void run(){

            try{

                BufferedReader reader = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));

                String request = reader.readLine();

                // parse the request
                String file = parseRequest(request);
                System.out.println("my page " + file);

                // send response based on request
                processRequest(file);




            } catch (IOException e){
                e.printStackTrace();
            }

        }

        public String parseRequest(String request){

            String[] firstLine = request.split(" ");
            String filePath = firstLine[1];
            return filePath;
        }

        public void processRequest(String file){
            //check if path exits
            String path = "src/jennifer/server/resources" + file;
            String absolutePath = new File(path).getAbsolutePath();
            //System.out.println(absolutePath);
            File rootPath = new File(absolutePath);

            if(rootPath.exists()){

                String contentLength = Long.toString(absolutePath.length());
                String[] headers = constructHeader("HTTP/1.1 200 OK", contentLength);
                sendResponse(headers, rootPath);
            }
            else{

                 path = "src/jennifer/server/resources/NotFound";
                 absolutePath = new File(path).getAbsolutePath();
                 rootPath = new File(absolutePath);
                 System.out.println(rootPath);

                String contentLength = Integer.toString(absolutePath.length());
                String[] headers = constructHeader("HTTP/1.1 404 NOT FOUND", contentLength);
                sendResponse(headers, rootPath);
            }

        }

        public String[] constructHeader(String statusCode, String contentLength){

            String[] headers = new String[5];
            headers[0] = statusCode + "\r\n";
            headers[1]= "Date: Fri, 15 Mar 2019" + "\r\n";
            headers[2] = "Server: Phoenix Server" + "\r\n";
            headers[3] = "text/html; text/css; charset=utf-8" + "\r\n";
            headers[4] = "Content-Length: " + contentLength + "\r\n";

            return headers;

        }

        public void sendResponse(String[] headers, File file){

            FileInputStream fileReader = null;
            DataOutputStream byteWriter = null;

            try{

                fileReader = new FileInputStream(file);
                byteWriter = new DataOutputStream(connSocket.getOutputStream());

                //writing response headers to the connecting socket (client)
                for(int i = 0; i < headers.length - 1; i++){

                    byteWriter.writeBytes(headers[i]);
                }

                //writing response body to the connecting socket (client)
                int byteRead;
                byte[] buffer = new byte[1024];
                while((byteRead = fileReader.read(buffer)) != -1){
                    byteWriter.write(buffer, 0, byteRead);

                }


            } catch (FileNotFoundException e){
                System.out.println("File not found");
            } catch (IOException e){
                e.printStackTrace();
                System.out.println("something went wrong while reading file");
            }

            try{
                fileReader.close();
                byteWriter.close();
            } catch(IOException e){
                e.printStackTrace();
            }



        }

    }
}