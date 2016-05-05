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
		
		
		
		 //Customer
        List <String> nameList = new ArrayList<String>();
        
         //Fills up student table:
        ResultSet result;
        String searchFor = "select * from instructor";      
        result = s.executeQuery(searchFor);
        int numCustomers = 0;
        
        try 
        {
            FileReader fileR = new FileReader("randomAddressList.txt");
            BufferedReader textReader = new BufferedReader(fileR);
            String line;
            int customerID = 8000;
            result.next();
            do {
                nameList.add(result.getString("name"));
            }while (result.next());
            
            while ((line = textReader.readLine()) != null)
            {
                
                String insertAddress = line + textReader.readLine();
                if (insertAddress.length() >= 40)
                {
                   insertAddress = insertAddress.substring(0,39);
                }
                String insertString = "insert into customer values ('" + customerID + "'," + "'" + nameList.get(numCustomers) + "','" + insertAddress + "')";
                                System.out.println("Added: " + insertString);
                s.executeUpdate(insertString);
                customerID++;
                numCustomers++;
            }
            
            textReader.close();
            fileR.close();
        }
        
        catch (FileNotFoundException ex)
        {
            System.out.println("Unable to read file");
        }
        System.out.println("Customers: " + numCustomers);
        
          

       
           
        s.close();
        con.close();
    }