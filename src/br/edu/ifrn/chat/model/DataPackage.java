package br.edu.ifrn.chat.model;

import br.edu.ifrn.chat.enumarator.Commando;
import java.io.*;
import java.util.*;

public class DataPackage implements Serializable {
    
    private String user;
    private String userReserved;
    private Set<String> usersOnLine;
    private String message;    
    private Commando action;    

    public DataPackage() {
        this.usersOnLine = new HashSet<String>();
    }    

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserReserved() {
        return userReserved;
    }

    public void setUserReserved(String userReserved) {
        this.userReserved = userReserved;
    }

    public Set<String> getUsersOnLine() {
        return usersOnLine;
    }

    public void setUsersOnLine(Set<String> usersOnLine) {
        this.usersOnLine = usersOnLine;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Commando getAction() {
        return action;
    }

    public void setAction(Commando action) {
        this.action = action;
    }
    
}
