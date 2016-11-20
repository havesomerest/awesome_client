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

    @CliCommand(value = "get test", help = "Print a simple hello world message")
    public String simple(
            @CliOption(key = { "mapping" }, mandatory = false, help = "The hello world message") final String mapping) {

            String fromServer, fromUser;
        try {
            PrintWriter socketWriter = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader socketReader = new BufferedReader(
                    new InputStreamReader(kkSocket.getInputStream()));




            boolean finished = false;

            socketWriter.println("getrequest:" + mapping);
            fromServer = socketReader.readLine();

            if (fromServer != null && fromServer.equals("BYE.")) {
                kkSocket.close();
                finished = true;
            }

            //socketWriter.println(fromUser);


        } catch (Exception e) {
            return "exception: " + e.getMessage();
        }
        return new JSONObject(fromServer).toString(2);
        //return "Message = [" + message + "] Location = [" + location + "]";
    }

}
