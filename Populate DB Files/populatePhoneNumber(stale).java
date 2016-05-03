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
		
		
		
		
		//phonenumber
		 List <Integer> indList = new ArrayList<Integer>();
        List <Integer> famList = new ArrayList<Integer>();
        List <Integer> busList = new ArrayList<Integer>();
        int randomNumber;
       
		
		try 
        {
            ResultSet result;
            FileReader fileR = new FileReader("randomPhoneNumberList.txt");
            BufferedReader textReader = new BufferedReader(fileR);
            String line;
            result = s.executeQuery("select * from individual_account");
            result.next();
            do {
                indList.add(result.getInt("account_id"));
            }while (result.next());
            
            result = s.executeQuery("select * from family_account");
            result.next();
            do {
                famList.add(result.getInt("account_id"));
            }while (result.next());
            
            result = s.executeQuery("select * from business_account");
            result.next();
            do {
                busList.add(result.getInt("account_id"));
            }while (result.next());
            
            int indCounter = 0;int famCounter = 0;int busCounter = 0;
            String insertString = "";
            while ((line = textReader.readLine()) != null)
            {
                if (indCounter < indList.size())
                {
                    insertString = "insert into phone_number values ('" + line + "'," + "'" + indList.get(indCounter) + "','" + "Primary" + "')";
                    System.out.println("Added: " + insertString);
                    s.executeUpdate(insertString);
                    indCounter++;
                }
                
                else if (famCounter < famList.size())
                {
                   randomNumber = rand.nextInt(4) + 1; 
                   int randomCounter = 1;
                   int famID = famList.get(famCounter);
                   
                    
                    
                        insertString = "insert into phone_number values ('" + line + "'," + "'" + famID + "','" + "Primary" + "')";
                        System.out.println("Added: " + insertString);
                        s.executeUpdate(insertString);
                    
                   
                   while ((line = textReader.readLine()) != null && (randomCounter  < randomNumber) )
                   {
                       insertString = "insert into phone_number values ('" + line + "'," + "'" + famID + "','" + "Other" + "')";
                       System.out.println("Added: " + insertString);
                       s.executeUpdate(insertString);
                       randomCounter++;
                   }
                   famCounter++;
                }
                
               else if (busCounter < busList.size())
                {
                   randomNumber = rand.nextInt(7) + 2; 
                   int randomCounter = 2;
                   int busID = busList.get(busCounter);
                   
                    
                    
                        insertString = "insert into phone_number values ('" + line + "'," + "'" + busID + "','" + "Primary" + "')";
                        System.out.println("Added: " + insertString);
                        s.executeUpdate(insertString);
                    
                   
                   while ((line = textReader.readLine()) != null && (randomCounter  < randomNumber) )
                   {
                       insertString = "insert into phone_number values ('" + line + "'," + "'" + busID + "','" + "Other" + "')";
                       System.out.println("Added: " + insertString);
                       s.executeUpdate(insertString);
                       randomCounter++;
                   }
                   busCounter++;
                }
                
                
            }
            
            textReader.close();
            fileR.close();
        }
        
        catch (FileNotFoundException ex)
        {
            System.out.println("Unable to read file");
        }
        
          

       
           
        s.close();
        con.close();
    }