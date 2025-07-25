import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class Patient {
private Connection connection;

private Scanner scannner;


public Patient(Connection connection, Scanner scanner ){
    this.connection = connection;
    this.scannner = scannner;

}

public void addPatient(){
    System.out.print("Enter Patient name:");
    String name = scannner.next();
    System.out.print("Enter Patient Age:");
    int age = scannner.nextInt();
        System.out.print("Enter Patient Gender:");
    String gender = scannner.next();


try{

String query = "INSERT INTO patient(name, age, gender) values(?,?,?)";
PreparedStatement preparedStatement = connection.prepareStatement(query);

preparedStatement.setString(1, name);
preparedStatement.setInt(2, age);
preparedStatement.setString(3, gender);

int affectedRows = preparedStatement.executeUpdate();
if(affectedRows>0){
    System.out.println("Patient Added Successfully");
}
else{
    System.out.println("Failed to add Patient!!");
}

}
catch(SQLException e){

    e.printStackTrace();

}



}

public void viewPatients(){
    String query = "selected * from patients";

try{
PreparedStatement preparedStatement = connection.prepareStatement(query);
ResultSet resultSet = preparedStatement.executeQuery();
 
}catch (SQLException e){
    e.printStackTrace();
}


}



}
