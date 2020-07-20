package me.vshnv.persistanceframework.sql;

public class ConnectorDriverNotFoundException extends Exception{
    public ConnectorDriverNotFoundException(String driverName){
        super("Driver not found: " +driverName);
    }
}
