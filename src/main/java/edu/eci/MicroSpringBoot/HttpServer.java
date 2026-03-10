package edu.eci.MicroSpringBoot;

import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {
    static Map<String, WebMethod> endPoints = new HashMap();

    static String staticFilesPath = null;

    public static void staticfiles(String path) {
        staticFilesPath = path;
    }

    private static String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".html") || fileRequested.endsWith(".htm"))
            return "text/html";
        if (fileRequested.endsWith(".css"))
            return "text/css";
        if (fileRequested.endsWith(".js"))
            return "text/javascript";
        if (fileRequested.endsWith(".png"))
            return "image/png";
        if (fileRequested.endsWith(".jpg") || fileRequested.endsWith(".jpeg"))
            return "image/jpeg";
        return "text/plain";
    }

    public static void main(String[] args) throws IOException, URISyntaxException {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        Socket clientSocket = null;
        boolean running = true;
        while (running) {
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine;

            boolean isFirstLine = true;
            String reqpath = "";
            String query = "";

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);

                if (isFirstLine) {
                    String[] flTokens = inputLine.split(" ");
                    String method = flTokens[0];
                    String struripath = flTokens[1];
                    String protocolversion = flTokens[2];

                    URI uripath = new URI(struripath);
                    reqpath = uripath.getPath();

                    query = uripath.getQuery();

                    isFirstLine = false;
                }

                if (inputLine.isEmpty()) {
                    break;
                }
            }

            WebMethod currentWm = endPoints.get(reqpath);

            if (currentWm != null) {
                HttpRequest req = new HttpRequest(query);
                HttpResponse res = new HttpResponse();

                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + "<meta charset=\"UTF-8\">"
                        + "<title>Title of the document</title>\n"
                        + "</head>"
                        + "<body>"
                        + currentWm.execute(req, res)
                        + "</body>"
                        + "</html>";
                out.print(outputLine);
                out.flush();

            } else {
                if (staticFilesPath != null) {
                    try {
                        if (reqpath.equals("/")) {
                            reqpath = "/index.html";
                        }

                        String basePath = HttpServer.class
                                .getProtectionDomain()
                                .getCodeSource()
                                .getLocation()
                                .toURI()
                                .getPath();
                        Path filePath = Paths.get(basePath + staticFilesPath + reqpath);
                        System.out.println("Buscando archivo en: " + filePath);

                        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                            byte[] fileBytes = Files.readAllBytes(filePath);
                            String contentType = getContentType(reqpath);

                            out.print("HTTP/1.1 200 OK\r\n");
                            out.print("Content-Type: " + contentType + "\r\n");
                            out.print("Content-Length: " + fileBytes.length + "\r\n");
                            out.print("\r\n");
                            out.flush();

                            clientSocket.getOutputStream().write(fileBytes);

                        } else {
                            out.println(
                                    "HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n<h1>404 File Not Found</h1>");
                        }
                    } catch (Exception e) {
                        out.println("HTTP/1.1 500 Internal Server Error\r\n\r\n");
                    }
                } else {
                    out.println(
                            "HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n<h1>No static folder configured</h1>");
                }
            }
            out.close();
            in.close();
            clientSocket.close();
        }
    }

    public static void get(String path, WebMethod wm) {
        endPoints.put(path, wm);
    }
}