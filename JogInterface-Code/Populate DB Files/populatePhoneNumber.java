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
		
		
		 SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
       List <String> phoneNum = new ArrayList<String>();
       List <String> dateList = new ArrayList<String>();
       String phoneList = "select * from phone_number";
       ResultSet result = s.executeQuery(phoneList);
       
       while (result.next())
           phoneNum.add(result.getString("phone_num"));

       int usage_id = 100;
       int randomNumber;
       try 
       {
           FileReader fileR = new FileReader("randomDateList.txt");
           BufferedReader bw = new BufferedReader(fileR);
           String line;
           String insertString;
           
           while ((line = bw.readLine()) != null)
               dateList.add(line);
           
            for (int counter = 0; counter < phoneNum.size(); counter++)
            {
                int randomUsage = rand.nextInt(8) + 1;
                
                for (int counter2 = 0; counter2 < randomUsage; counter2++)
                {
                    String number = phoneNum.get(counter);
                    randomNumber = rand.nextInt(1514);
                    String otherNumber = phoneNum.get(randomNumber);
                    while (otherNumber.equals(number))
                      {
                            randomNumber = rand.nextInt(1514);
                            otherNumber = phoneNum.get(randomNumber);
                      }
                    String type;
                    String otherType;
                    randomNumber = rand.nextInt(2);
                    if (randomNumber == 0)
                    {
                        type = "Source";
                        otherType = "Destination";
                    }
                    else
                    {
                        type = "Destination";
                        otherType = "Source";
                    }
                        
                    insertString = "insert into usage values ('" + usage_id + "'," + "'" + number + "','" + type + "')";
                    s.executeUpdate(insertString);
                    randomNumber = rand.nextInt(3);
                    
                    switch(randomNumber)
                    {
                    case 0: //internet
                        randomNumber = rand.nextInt(9999) + 1;
                        insertString = "insert into internet_usage values ('" + usage_id + "'," + "'" + randomNumber +  "')";
                        System.out.println(insertString);
                        s.executeUpdate(insertString);
                        break;
                    case 1: //text
                        int text_size = rand.nextInt(2000) + 1;
                        String time = dateList.get(rand.nextInt(1000));
                        insertString = "insert into text_usage values ('" + usage_id + "',TO_TIMESTAMP('" + time + "', 'DD-MM-YYYY HH24:MI:SS'),'" + text_size + "','" + otherNumber + "')";
                        System.out.println(insertString);
                        s.executeUpdate(insertString);
                    case 2: //call
                        try {
                            int randChoice1 = rand.nextInt(1999);
                            int randChoice2 = randChoice1 + 1;
                            String time1 = dateList.get(randChoice1);
                            String time2 = dateList.get(randChoice2);
                            java.util.Date d1 = format.parse(time1);
                            java.util.Date d2 = format.parse(time2);
                            long duration = d2.getTime() - d1.getTime() / 1000;
                            System.out.println(time1 + " " + time2 + " " + duration);

                            insertString = "insert into call_usage values ('" + usage_id + "',TO_TIMESTAMP('" + time1 + "', 'DD-MM-YYYY HH24:MI:SS'),TO_TIMESTAMP('" + time2 + "', 'DD-MM-YYYY HH24:MI:SS'),'" + duration + "','" + otherNumber + "')";
                            System.out.println(insertString);
                            s.executeUpdate(insertString);
                        }
                        catch (Exception e) 
                        {
                        }
                        
                            
                        break;
                     
                    default:
                        break;
                    }
                    usage_id++;
                }

            }

             bw.close();
             fileR.close();

       }
       catch (FileNotFoundException ex)
        {
            System.out.println("Unable to read file");
        }
        
          

       
           
        s.close();
        con.close();
    }