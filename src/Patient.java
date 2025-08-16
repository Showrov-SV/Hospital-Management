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
    this.scannner = scanner;

}

public void addPatient(){
    System.out.print("Enter Patient name:");
    String name = scannner.next();
    System.out.print("Enter Patient Age:");
    int age = scannner.nextInt();
        System.out.print("Enter Patient Gender:");
    String gender = scannner.next();


try{

String query = "INSERT INTO patients (name, age, gender) values(?,?,?)";
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
    String query = "SELECT * FROM patients";

try{
PreparedStatement preparedStatement = connection.prepareStatement(query);
ResultSet resultSet = preparedStatement.executeQuery();
    System.out.println("Patients: ");
    System.out.println("+-------------+--------------------+-------+---------+");
    System.out.println("| Patient ID  |        Name        |  Age  | Gender  |");
    System.out.println("+-------------+--------------------+-------+---------+");
 while (resultSet.next()){
     int id = resultSet.getInt("id");
     String name= resultSet.getString("name");
     int age = resultSet.getInt("age");
     String gender = resultSet.getString("gender");
     System.out.printf("|%-13s|%-20S|%-7S|%-9S|\n",id,name,age,gender);
     System.out.println("+-------------+--------------------+-------+---------+");



 }




}catch (SQLException e){
    e.printStackTrace();
}

}



    public boolean getPatientById(int id) {
        String query = "SELECT * FROM patients WHERE id =?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }else {
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public void deletePatient() {
        System.out.print("Enter Patient ID to delete: ");
        int id = scannner.nextInt();

        String query = "DELETE FROM patients WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Patient deleted successfully.");
            } else {
                System.out.println("No patient found with ID " + id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
