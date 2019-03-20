package jennifer.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    private ServerSocket serverSocket;

    /**
     * This method creates a server with port number
     * @param port - port number of the server
     *
     * */

    public void start(int port) {

        try{

            // creates a server socket with port number {port}
            serverSocket = new ServerSocket(port);

            while(true){
                // creates a connection socket to listen and accept
                // requests from clients
                Socket connSocket = serverSocket.accept();

                //creates a new thread to handle requests
                new RequestHandler(connSocket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    *
    * This class creates a thread that handles requests for a client
    *
    * @author Jennifer Ubah
    *
    * */

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
                System.out.println("requesting... " + file);
                String[] fileExtension = file.split("\\.");
                System.out.println(fileExtension[1]);

                // send response based on request
                processRequest(file, fileExtension[1]);

            } catch (IOException e){
                e.printStackTrace();
            }

        }

        /**
        * This method parses a request
        *
        * @param request - request in the format GET /index.html HTTP/1.1
        * @return - file to get {/index.html}
         *
         */

        public String parseRequest(String request){

            String[] firstLine = request.split(" ");
            String filePath = firstLine[1];
            return filePath;
        }


        /**
         *
         * This method processes the request
         *
         * @param file - requested file {index}
         * @param fileExt - extension of the file {.html}
         *
         */

        public void processRequest(String file, String fileExt){

            //check if path exits
            String path = "src/jennifer/server/resources" + file;
            String absolutePath = new File(path).getAbsolutePath();
            //System.out.println(absolutePath);
            File rootPath = new File(absolutePath);

            if(rootPath.exists()){

                String contentLength = Long.toString(rootPath.length());
                System.out.println(contentLength);
                String[] headers = constructHeader("HTTP/1.1 200 OK", contentLength, fileExt);
                sendResponse(headers, rootPath);
            }
            else{
                path = "src/jennifer/server/resources/NotFound.txt";
                absolutePath = new File(path).getAbsolutePath();
                rootPath = new File(absolutePath);
                System.out.println(rootPath);

                String contentLength = Long.toString(rootPath.length());
                String[] headers = constructHeader("HTTP/1.1 404 NOT FOUND", contentLength, fileExt);
                sendResponse(headers, rootPath);
            }
        }

        /**
         * This method constructs a response header for the appropriate file
         *
         * @param statusCode - standard HTPP status code
         * @param contentLength - lenght of the requested file
         * @param ext - file extension
         * @return  an array of header values
         */

        public String[] constructHeader(String statusCode, String contentLength, String ext){

            String[] headers = new String[8];
            headers[0] = statusCode + "\r\n";
            headers[1]= "Date: Mon, 18 Mar 2019 17:00:00 GMT" + "\r\n";
            headers[2] = "Server: Phoenix Server" + "\r\n";

            if(ext.equals("png") || ext.equals("jpeg")){
                System.out.println("This is an image");
                headers[3] = "Content-Type: image/png" + "\r\n";
            }

            else if (ext.equals("css")){
                System.out.println("This is a css file");
                headers[3] = "Content-Type: text/css charset=utf-8; " + "\r\n";
            }

            else if (ext.equals("html")){
                System.out.println("This is an html file");
                headers[3] = "Content-Type: text/html charset=utf-8; " + "\r\n";
            }

            else if (ext.equals("pdf")){
                headers[3] = "Content-Type: application/pdf; charset=utf-8" + "\r\n";
            }

            else if (ext.equals("docx")){
                headers[3] = "Content-Type: application/vnd.openxmlformats-officedocument.wordprocessingml.document; charset=utf-8" + "\r\n";
            }

            else {
                headers[3] = "Content-Type: type/text; charset=utf-8" + "\r\n";
            }
            headers[4] = "Content-Length: " + contentLength  + "\r\n";
            headers[5] = "Connection: close "+ "\r\n";
            headers[6] = "\r\n";
            headers[7] = "\n\n";

            return headers;

        }


        /**
         * This method sends the response to the client
         *
         * @param headers - array of header values
         * @param file - requested file
         *
         *
         */

        public void sendResponse(String[] headers, File file){

            try (FileInputStream fileReader = new FileInputStream(file);
                 DataOutputStream byteWriter = new DataOutputStream(connSocket.getOutputStream())){

                //writing response headers to the connecting socket (client)
                for(int i = 0; i < headers.length - 1; i++){
                    byteWriter.writeBytes(headers[i]);
                }

                //writing response body to the connecting socket (client)
                int byteRead;
                byte[] buffer = new byte[1048576];
                while((byteRead = fileReader.read(buffer)) != -1){
                    byteWriter.write(buffer, 0, byteRead);
                }

            } catch (FileNotFoundException e){
                System.out.println("File not found");
            } catch (IOException e){
                e.printStackTrace();
                System.out.println("something went wrong while reading file");
            }
        }

    }
}