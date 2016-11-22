package org.springframework.shell.samples.helloworld.commands;

import org.json.JSONObject;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Component
public class TcpConnectionService implements CommandMarker {

    private Socket kkSocket;

    @PostConstruct
    public void init() {
        String hostName = "localhost";
        int portNumber = 4444;

        try {
            kkSocket = new Socket(hostName, portNumber);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @CliCommand(value = "get request", help = "Print a simple hello world message")
    public String simple(
            @CliOption(key = { "position" }, mandatory = false, help = "The hello world message") final Integer pos) {

        Integer position = pos;
            String fromServer, fromUser;
        try {
            PrintWriter socketWriter = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader socketReader = new BufferedReader(
                    new InputStreamReader(kkSocket.getInputStream()));

            boolean finished = false;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("command", "LIST_REQUESTS");
            jsonObject.put("position", position);

            socketWriter.println(jsonObject.toString());
            fromServer = socketReader.readLine();

            if (fromServer != null && fromServer.equals("BYE.")) {
                kkSocket.close();
                finished = true;
            }
        } catch (Exception e) {
            return new JSONObject(e).toString(2);
        }
        String returnValue = "";

        if (fromServer != null) {
            JSONObject fromServerObject = new JSONObject(fromServer);
            returnValue = fromServerObject.toString(2);
        } else {
            returnValue = fromServer;
        }

        return returnValue + "\n";
    }

}
