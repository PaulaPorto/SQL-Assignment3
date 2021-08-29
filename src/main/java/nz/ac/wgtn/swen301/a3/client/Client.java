package nz.ac.wgtn.swen301.a3.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;

public class Client {
    public static void main (String[] args){
        if (args[0] != null && args[1] != null){
            String type = args[0];
            String fileName = args[1];
            var client = HttpClient.newHttpClient();
            String n = "Content-Disposition";
            String v = "attachment;filename=";

            if(type.equals("csv")){
                try{
                    String uriCSV = "http://localhost:8080/resthome4logs/statscsv";
                    var request = HttpRequest.newBuilder().GET().uri(URI.create(uriCSV))
                            .setHeader(n, v + fileName).build();
                    client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(fileName)));
                } catch(Exception e){
                    System.out.println("Error! Incorrect server.");
                }

            } else if(type.equals("excel")){
                try{
                    String uriEXCEL = "http://localhost:8080/resthome4logs/statsxls";
                    var request = HttpRequest.newBuilder().GET().uri(URI.create(uriEXCEL))
                            .setHeader(n, v + fileName)
                            .build();
                    client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(fileName)));
                } catch(Exception e){
                    System.out.println("Error! Incorrect server.");
                }

            }

        }
    }
}
