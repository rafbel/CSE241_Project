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
		
		int remainingPhones = 2217 - 1515;
        List <Integer> idList = new ArrayList<Integer>();
        List <Integer> meidList = new ArrayList<Integer>();
        List <Integer> accountList = new ArrayList<Integer>();
        List <String> numberList = new ArrayList<String>();
        
        String searchFor = "select * from customer";
        
        ResultSet result = s.executeQuery(searchFor);
        while (result.next())
            idList.add(result.getInt("cust_id"));
        
        searchFor = "select *\n" +
                    "from active_phone natural join phone_number\n" +
                    "order by phone_num desc";
        result = s.executeQuery(searchFor);
        
        while (result.next())
        {
            accountList.add(result.getInt("account_id"));
            meidList.add(result.getInt("meid"));
        }
          
        String updateString;
        for (int counter = 0; counter < 1515; counter++)
        {
            updateString = "insert into owns values ('" + idList.get(counter) + "','" + meidList.get(counter) + "')";
            s.executeUpdate(updateString);
            
            updateString = "insert into has values ('" + idList.get(counter) + "','" + accountList.get(counter) + "')";
            s.executeUpdate(updateString);
        }
        
          

       
           
        s.close();
        con.close();
    }