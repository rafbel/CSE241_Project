/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package joginterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author Rafael
 */
public class DBCon 
{
    public DBCon(Statement s, Connection con) throws ClassNotFoundException
    {
        boolean credentialVerify = true;
        Scanner sc = new Scanner(System.in);
      
      //Checks to see if user entered the right credentials
      while (credentialVerify)
      {
          try
          {
            System.out.print("Enter Oracle user ID: ");
            String username = sc.next();
            System.out.print("Enter Oracle user password: ");
            String password = sc.next();

            credentialVerify = false;

            Class.forName ("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection
              ("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241",username,
               password);
              s=con.createStatement();
          }
          catch (SQLException e) {
              System.out.println("User not found. Please enter your credentials once again");
              credentialVerify = true;
          }
          catch (ClassNotFoundException ex)
          {
              System.out.println("Database error.Please try again.");
              credentialVerify = true;
          }
        }
    }
    public void close(Statement s, Connection con) throws SQLException
    {
        s.close();
        con.close();
    }
}
