package com.example.daiquiri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TCPServer {
    private ServerSocket serverSocket;
    private Map<String, ObjectOutputStream> clients = new HashMap<>();

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

                String username = ((UsernameInfo) in.readObject()).getUsername();
                clients.put(username, out);

                System.out.println("Received username from client: " + username);
                broadcast(new UsernameInfo(username));
                broadcastClientList();

                new Thread(new ClientHandler(clientSocket, in, username)).start();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void broadcastClientList() {
        String[] clientList = clients.keySet().toArray(new String[0]);
        broadcast(new UserListInfo(clientList));
    }

    private void broadcast(TransInfo info) {
        for (ObjectOutputStream out : clients.values()) {
            try {
                out.writeObject(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void sendCommandToClient(TransInfo command, String username) {
        ObjectOutputStream clientOut = clients.get(username);
        if (clientOut != null) {
            try {
                clientOut.writeObject(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private ObjectInputStream in;
        private String username;

        public ClientHandler(Socket clientSocket, ObjectInputStream in, String username) {
            this.clientSocket = clientSocket;
            this.in = in;
            this.username = username;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    TransInfo info = (TransInfo) in.readObject();
                    if (info instanceof SimulationStatusInfo) {
                        SimulationStatusInfo simInfo = (SimulationStatusInfo) info;
                        sendCommandToClient(simInfo, simInfo.getTargetClient());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clients.remove(username);
                broadcastClientList();
            }
        }
    }
    public static void main(String[] args) {
        TCPServer server = new TCPServer();
        server.start(9090);
    }
}
