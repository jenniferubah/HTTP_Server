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

        private DataOutputStream writer;
        private BufferedReader reader;
        private Socket connSocket;

        public RequestHandler(Socket connSocket) {
            this.connSocket = connSocket;
        }

        public void run(){

            try{

                writer = new DataOutputStream(connSocket.getOutputStream());
                reader = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));

/*                String line;
                String[] request = new String[5];
                int count = 0;

                while((line = reader.readLine()) != null){
                    if(line.isEmpty()){
                        break;
                    }

                    request[count] = line;
                    count++;

                }*/

                String request = reader.readLine();

                //parse the request
                String page = parseRequest(request);
                System.out.println("my page " + page);

                // send response based on reques
                sendResponse(page);

//                byte[] byteRes = "Hello World".getBytes();
//                writer.write(byteRes);

                //writer.close();
                reader.close();
                //connSocket.close();

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

        public String parseRequest(String request){

            String[] firstLine = request.split(" ");
            String page = firstLine[1];
            return page;
        }

        public void sendResponse(String page){

            if(page.equals("/index")){

                String file = "src/jennifer/server/resources/index.html";
                File path = new File(file);
                String absolutePath = path.getAbsolutePath();
                //System.out.println(absolutePath);
                try(FileInputStream fileReader = new FileInputStream(absolutePath);
                    BufferedOutputStream byteWriter = new BufferedOutputStream(new DataOutputStream(connSocket.getOutputStream()))){

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
            }

        }

    }
}
