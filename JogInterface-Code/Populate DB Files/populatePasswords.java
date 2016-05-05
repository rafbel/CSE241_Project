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
		
		//Insert passwords
		 int account_id = 10000;
        for (int counter = 0; counter < 614; counter++)
        {
             String uuid = UUID.randomUUID().toString().replaceAll("-", "");
             if (uuid.length() >= 12)
                uuid = uuid.substring(0,11);
             String updateString = "update account SET password = '" + uuid + "' where account_id = '" + account_id + "'";
             s.executeUpdate(updateString);
             account_id++;
        }
		
		
		//
		
		 String searchFor = "select account_id from account";
        ResultSet result = s.executeQuery(searchFor);
        
        while (result.next())
            idList.add(result.getString(1));
        
        for (int counter = 0; counter < 614; counter++)
        {
            int randomNumber = rand.nextInt(4) + 1;
            String insertString = "insert into acc_plan values (" + idList.get(counter) + "," + randomNumber + ")";
            System.out.println(insertString);
            s.executeUpdate(insertString);
            
        }  

       
           
        s.close();
        con.close();
    }