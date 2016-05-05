package populateprojectdb;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
        import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafael
 */

public static void main(String[] args)
    throws SQLException, IOException, java.lang.ClassNotFoundException
    {
       
           
       
         Statement s = null;
         Connection con = null;
         Random rand = new Random();
         
         
        Class.forName ("oracle.jdbc.driver.OracleDriver");
        con = DriverManager.getConnection
              ("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241","rcd315",
               "Med1nho93");
        s=con.createStatement();
		
		
		
		  //Account
        int account_id = 10000;
        int numIndividual = 0; int numFamily = 0; int numBusiness = 0;
        for (int counter = 0; counter < 1501; counter++)       
        {
            String insertString = "insert into account values ('" + account_id + "')";
            s.executeUpdate(insertString);
            
            //Marking that account as one of the 3 types of possible accounts:
            int randomNum = rand.nextInt(3);
            switch(randomNum)
            {
                case 0: //individual account

                    insertString = "insert into individual_account values ('" + account_id +"')";
                    s.executeUpdate(insertString);
                    numIndividual++;
                    break;
                case 1: //family account
                    insertString = "insert into family_account values ('" + account_id +"')";
                    s.executeUpdate(insertString);
                    numFamily++;
                    break;
                    
                case 2://business account
                    insertString = "insert into business_account values ('" + account_id +"')";
                    s.executeUpdate(insertString);
                    numBusiness++;
                    break;
                default:
                    break;            
            }
            
            account_id++;
        }
        
        System.out.println(numIndividual + " " + numFamily + " " + numBusiness);
        
          

       
           
        s.close();
        con.close();
    }