package br.edu.ifrn.chat.service;

import br.edu.ifrn.chat.enumarator.Commando;
import br.edu.ifrn.chat.model.DataPackage;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorService {

    private static ServidorService instance;
    

    private ServerSocket serverSocket;
    private Socket socket;
    private final Map<String, ObjectOutputStream> usersMap;

    private ServidorService(int PORT) {
        this.usersMap = new HashMap<>();        
        startServer(PORT);
    }
    
    public static synchronized ServidorService getInstance(int PORT) {
        if (instance == null) {
            instance = new ServidorService(PORT);
        }
        return instance;
    }

    public static synchronized ServidorService closeServerService() throws Throwable{
        if (instance != null){
            instance.finalize();
        }
        return instance;
    }

    private void startServer(int PORT) {
        try {
            this.serverSocket = new ServerSocket(PORT);
            while (true) {
                this.socket = this.serverSocket.accept();
                new Thread(new Listener(this.socket)).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void conectar(DataPackage dataPackage, ObjectOutputStream output) {
        if (!this.usersMap.containsKey(dataPackage.getUser())) {
            dataPackage.setAction(Commando.CONECTAR);
            enviar(dataPackage, output);
            this.usersMap.put(dataPackage.getUser(), output);            
            atualizarOnline();
            this.usersMap.entrySet().stream().filter((outputMap) -> (!outputMap.getKey().equals(dataPackage.getUser()))).forEachOrdered((outputMap) -> {
                enviar(dataPackage, outputMap);
            });
            System.out.println(dataPackage.getUser() + " está online");
        } else {
            dataPackage.setAction(Commando.NAO_CONECTADO);
            enviar(dataPackage, output);
        }
    }
    
    private void desconectar(DataPackage dataPackage, ObjectOutputStream output) {
        dataPackage.setAction(Commando.DESCONECTADO);
        enviar(dataPackage, output);
        this.usersMap.remove(dataPackage.getUser());
        atualizarOnline();        
        this.usersMap.entrySet().stream().filter((outputMap) -> (!outputMap.getKey().equals(dataPackage.getUser()))).forEachOrdered((outputMap) -> {
            dataPackage.setMessage(dataPackage.getUser() + " saiu do bate-papo\n");
            dataPackage.setAction(Commando.ENVIAR_TODOS);
            enviar(dataPackage, outputMap);
        });       
        System.out.println(dataPackage.getUser() + " saiu do chat");
    }

    private void enviar(DataPackage dataPackage, ObjectOutputStream output) {
        try {
            output.writeObject(dataPackage);
        } catch (IOException e) {
            System.out.println(e.getMessage());            
        }
    }
    
    private void enviar(DataPackage dataPackage, Map.Entry<String, ObjectOutputStream> outputMap) {
        try {
            outputMap.getValue().writeObject(dataPackage);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void enviarReservado(DataPackage dataPackage) {
        this.usersMap.entrySet().stream().filter((outputMap) -> (outputMap.getKey().equals(dataPackage.getUserReserved()))).forEachOrdered((outputMap) -> {
            dataPackage.setAction(Commando.RECEBIDO);
            enviar(dataPackage, outputMap);
        });
    }

    private void enviarTodos(DataPackage dataPackage) {
        this.usersMap.entrySet().stream().filter((outputMap) -> (!outputMap.getKey().equals(dataPackage.getUser()))).forEachOrdered((outputMap) -> {
            dataPackage.setAction(Commando.RECEBIDO);
            enviar(dataPackage, outputMap);
        });
    }
    
    private void atualizarOnline() {
        Set<String> users = new HashSet<>();
        this.usersMap.entrySet().forEach((keysMap) -> {
            users.add(keysMap.getKey());
        });        
        DataPackage dataPackage = new DataPackage();
        dataPackage.setAction(Commando.USUARIOS_ONLINE);
        dataPackage.setUsersOnLine(users);
        this.usersMap.entrySet().stream().map((outputMap) -> {
            dataPackage.setUser(outputMap.getKey());
            return outputMap;
        }).forEachOrdered((outputMap) -> {
            enviar(dataPackage, outputMap);
        });        
    }
    
    private class Listener implements Runnable {

        private ObjectInputStream input;
        private ObjectOutputStream output;

        public Listener(Socket socket) {
            try {
                this.input = new ObjectInputStream(socket.getInputStream());
                this.output = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        @Override
        public void run() {
            DataPackage dataPackage = null;
            try {
                while ((dataPackage = (DataPackage) this.input.readObject()) != null) {
                    Commando commands = dataPackage.getAction();
                    switch (commands) {
                        case CONECTAR:                            
                            conectar(dataPackage, this.output);
                            break;
                        case DESCONECTAR:
                            desconectar(dataPackage, this.output);                            
                            break;
                        case ENVIAR_RESERVADO:
                            enviarReservado(dataPackage);
                            break;
                        case ENVIAR_TODOS:
                            enviarTodos(dataPackage);
                            break;
                        default:
                    }
                }
            } catch (IOException | ClassNotFoundException e) {             
                System.out.println(e.getMessage());
            }
        }
    }
}