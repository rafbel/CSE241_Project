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
       
           
       
         //store
		try 
        {
            FileReader fileR = new FileReader("randomAddressList2.txt");
            BufferedReader textReader = new BufferedReader(fileR);
            String line;
            int storeID = 1000;
            
            while ((line = textReader.readLine()) != null)
            {
                
                String insertAddress = line + textReader.readLine();
                if (insertAddress.length() >= 40)
                {
                   insertAddress = insertAddress.substring(0,39);
                }
                String insertString = "insert into physical_store values ('" + storeID+ "'," + "'" +  insertAddress + "')";
                                System.out.println("Added: " + insertString);
                s.executeUpdate(insertString);
                storeID++;

            }
            
            textReader.close();
            fileR.close();
        }
        
        catch (FileNotFoundException ex)
        {
            System.out.println("Unable to read file");
        }
        
		
        //set up and sells
         String searchFor = "select * from active_phone natural join phone_number";
         String insertString;
         List<Integer> meidList = new ArrayList<Integer>();
         List<Integer> accountList = new ArrayList<Integer>();
         List<Integer> storeList = new ArrayList<Integer>();
         List<Integer> unactiveList = new ArrayList<Integer>();
         
         ResultSet result = s.executeQuery(searchFor);
         while (result.next())
         {
             meidList.add(result.getInt("meid"));
             accountList.add(result.getInt("account_id"));
         }
         
         searchFor = "select * from physical_store";
         result = s.executeQuery(searchFor);
         while (result.next())
             storeList.add(result.getInt("store_id"));
         
         for (int counter = 0; counter < accountList.size(); counter++)
         {
             int randomNumber = rand.nextInt(1913);
             insertString = "insert into sold values ('" + storeList.get(randomNumber) + "','" + meidList.get(counter) + "')";
             s.executeUpdate(insertString);
             
             insertString = "insert into set_up values ('" + accountList.get(counter) + "','" + storeList.get(randomNumber) + "')";
             s.executeUpdate(insertString);
         }
         searchFor = "select * from unactive_phone";
         result = s.executeQuery(searchFor);
         while (result.next())
             unactiveList.add(result.getInt("meid"));
         
         for (int counter = 0; counter < unactiveList.size(); counter++)
         {
             int randomNumber = rand.nextInt(1913);
             insertString = "insert into sold values ('" + storeList.get(randomNumber) + "','" + unactiveList.get(counter) + "')";
             s.executeUpdate(insertString);
         }
          

       
           
        s.close();
        con.close();
    }