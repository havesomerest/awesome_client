package org.springframework.shell.samples.helloworld.commands;

import org.json.JSONObject;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Component
public class TcpConnectionService implements CommandMarker {

    private Socket kkSocket;
    private PrintWriter socketWriter;
    private BufferedReader socketReader;

    @PostConstruct
    public void init() {
        String hostName = "localhost";
        int portNumber = 4444;

        try {
            kkSocket = new Socket(hostName, portNumber);
            socketWriter = new PrintWriter(kkSocket.getOutputStream(), true);
            socketReader = new BufferedReader(
                    new InputStreamReader(kkSocket.getInputStream()));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @CliCommand(value = "get request body", help = "Get the body of the last, or the specified request")
    public String getRequestBody(
            @CliOption(key = { "position" }, mandatory = false, help = "the position of the request") final Integer position) {

        String returnValue = "";

        try {

            boolean finished = false;

            JSONObject requestJsonObject = getRequest(position, Command.GET_REQUEST);
            String response = getResponse(requestJsonObject);

            handleExit(response);

            if (response != null) {
                JSONObject responseObject = new JSONObject(response);
                if (responseObject.has("requestBody")) {
                    returnValue = (String) responseObject.get("requestBody");
                } else {
                    returnValue = (String) responseObject.get("errorMessage");
                }
            }

        } catch (Exception e) {
            return new JSONObject(e).toString(2);
        }

        return returnValue + "\n";
    }

    @CliCommand(value = "get request headers", help = "Get the header of the last, or the specified request")
    public String getRequestHeaders(
            @CliOption(key = { "position" }, mandatory = false, help = "the position of the request") final Integer position) {

        String returnValue = "";

        try {

            boolean finished = false;

            JSONObject requestJsonObject = getRequest(position, Command.GET_REQUEST);
            String response = getResponse(requestJsonObject);

            handleExit(response);

            if (response != null) {
                JSONObject responseObject = new JSONObject(response);
                if (responseObject.has("requestHeaders")) {
                    JSONObject requestHeaders = (JSONObject) responseObject.get("requestHeaders");
                    StringBuilder sb = new StringBuilder();

                    requestHeaders.keySet().forEach(key -> {
                        sb.append(key + ": " + requestHeaders.get(key) + "\n");
                    });
                    returnValue = sb.toString();
                } else {
                    returnValue = (String) responseObject.get("errorMessage");
                }
            }

        } catch (Exception e) {
            return new JSONObject(e).toString(2);
        }

        return returnValue + "\n";
    }

    private void handleExit(String response) throws IOException {
        boolean finished;
        if (response != null && response.equals("BYE.")) {
            kkSocket.close();
            finished = true;
        }
    }

    private String getResponse(JSONObject requestJsonObject) throws IOException {
        String response;
        socketWriter.println(requestJsonObject.toString());
        response = socketReader.readLine();
        return response;
    }

    private JSONObject getRequest(Integer position, Command command) {
        JSONObject requestJsonObject = new JSONObject();
        requestJsonObject.put("command", command);
        requestJsonObject.put("position", position);
        return requestJsonObject;
    }

}
