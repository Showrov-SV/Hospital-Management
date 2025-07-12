import java.util.Scanner;
import java.sql.*;



public class Patient {
private Connection connection;

private Scanner scannner;


public Patient(Connection connection, Scanner scanner ){
    this.connection = connection;
    this.scannner = scannner;

}

public void addPatient(){
    System.err.print("Enter Patient name:");
    String name = scannner.next();
    System.err.print("Enter Patient Age:");
    String age = scannner.next();
        System.err.print("Enter Patient Gender:");
    String gender = scannner.next();


try{



}
catch(SQLException e){

}



}





}
