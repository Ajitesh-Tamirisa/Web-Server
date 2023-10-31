import java.io.*;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.net.*;

public class WebServer {
    String document_root;
    int port;
    HashMap<String, String> extensionsMap;
    public static void main(String[] args) throws IOException {
        WebServer server = new WebServer();

        //Extract document_root and port from command line args
        if(args.length == 4){
            if(args[0].equalsIgnoreCase("-document_root")){
                server.document_root = args[1];
            }
            else if(args[0].equalsIgnoreCase("-port")){
                server.port = Integer.parseInt(args[1]);
            }
            else{
                System.out.println("Please specify -doument_root and -port options in the correct format");
                return;
            }

            if(args[2].equalsIgnoreCase("-document_root")){
                server.document_root = args[3];
            }
            else if(args[2].equalsIgnoreCase("-port")){
                server.port = Integer.parseInt(args[3]);
            }
            else{
                System.out.println("Please specify -doument_root and -port options in the correct format");
                return;
            }

            if(server.port==0 || server.document_root==null){
                System.out.println("Please specify -doument_root and -port options in the correct format");
                return;
            }

        }
        else{
            System.out.println("Please enter both document_root and port.");
            return;
        }
        //Hashmap to handle different file extensions and their Content-Type value
        server.extensionsMap= new HashMap<String, String>();
        server.extensionsMap.put(".html", "text/html");
        server.extensionsMap.put(".htm", "text/html");
        server.extensionsMap.put(".txt", "text/plain");
        server.extensionsMap.put(".jpg", "image/jpg");
        server.extensionsMap.put(".gif", "image/gif");

        server.port = server.port==0?8080:server.port;//Defaults to port 8080 if it finds no proper port
        System.out.println("Port - "+server.port+"\nDocument_root - "+server.document_root);
        ServerSocket serverSocket = new ServerSocket(server.port);
        System.out.println("Server is listening for connections on port "+server.port);
        while (true) {
            // Forever loop
            Socket clientSocket = serverSocket.accept();
            System.out.println("New request");
            new Thread(() -> server.handleClientRequest(clientSocket)).start();
        }
    }

    public void handleClientRequest(Socket clientSocket) {
        try {
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream outputStream = clientSocket.getOutputStream();
            String clientRequest = inputStream.readLine();
            System.out.println("-------------------------REQUEST-------------------------\n"+clientRequest);
            if (clientRequest!=null) {
                String[] clientRequestArr = clientRequest.split(" ");
                if (clientRequestArr.length == 3) {
                    String HTTPMethod = clientRequestArr[0];
                    String filePath = clientRequestArr[1];
                    //Assume that the file extension is html. Value is changed if the requested file has any other extension.
                    String extension = "html";
                    int index = filePath.lastIndexOf(".");
                    extension = index>-1?filePath.substring(index):"html";
                    try{
                        if (HTTPMethod.equals("GET")) {
                            if(filePath.endsWith("/")){
                                //Route '/' requests to '/index.html'
                                filePath = "/index.html";
                                System.out.println("Adjusted file path - "+filePath);
                            }
                            File file = new File(document_root+filePath);
                            if(file.exists() && file.canRead()){
                                System.out.println("File exists");
                                //Code 200 if File Exists
                                System.out.println("200 OK");
                                String response = "HTTP/1.1 200 OK\r\n";
                                response += "Content-Type: "+extensionsMap.get(extension)+ "\r\n";
                                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
                                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                                response += "Date: "+dateFormat.format(new Date())+ "\r\n";
                                FileInputStream fis = new FileInputStream(file);
                                response += "Content-Length: "+file.length()+"\r\n\r\n";
                                outputStream.write(response.getBytes());
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = fis.read(buffer)) > 0) {
                                    outputStream.write(buffer, 0, bytesRead);
                                }
                                fis.close();
                                outputStream.close();
                                clientSocket.close();
                            }
                            else if(file.exists() && !file.canRead()){
                                //Error code 403 if Permission is denied
                                System.out.println("403 Permission Denied");
                                String response = "HTTP/1.1 403 Permission Denied\r\n";
                                response += "Content-Type: text/html \r\n";
                                outputStream.write(response.getBytes());
                                outputStream.close();
                                clientSocket.close();
                            }
                            else{
                                //Error code 404 if file does not exist
                                System.out.println("404 Page Not Found");
                                String response = "HTTP/1.1 404 Page Not Found\r\n";
                                response += "Content-Type: text/html" + "\r\n\r\n";
                                outputStream.write(response.getBytes());
                                outputStream.close();
                                clientSocket.close();
                            }
                        } else {
                                //Error code 405 if HTTP method is anything other than 'GET'.
                                System.out.println("405 Not Allowed");
                                String response = "HTTP/1.1 405 Not Allowed\r\n";
                                response += "Content-Type: text/html" + "\r\n\r\n";
                                outputStream.write(response.getBytes());
                                outputStream.close();
                                clientSocket.close();
                        }
                    }catch(Exception e){
                        System.out.println("!!!!!!!!!!!!!!!!!!! An exception occured !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        e.printStackTrace();
                    }
                }
            }
            else{
                //Error code 400 for Bad Requests
                System.out.println("400 Bad request");
                String response = "HTTP/1.1 400 Bad request\r\n";
                response += "Content-Type: text/html" + "\r\n\r\n";
                outputStream.write(response.getBytes());
                outputStream.close();
                clientSocket.close();
            }
        } catch (Exception e) {
            System.out.println("!!!!!!!!!!!!!!!!!!! An exception occured !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            e.printStackTrace();
        }
    }
}