/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class PopulateProjectDB {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    throws SQLException, IOException, java.lang.ClassNotFoundException
    {
        
         Statement s = null;
         Connection con = null;
         Random rand = new Random();
         List <String> idList = new ArrayList<String>();
         
        Class.forName ("oracle.jdbc.driver.OracleDriver");
        con = DriverManager.getConnection
              ("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241","rcd315",
               "Med1nho93");
        s=con.createStatement();
        
        
        
      
       List <String> dateList = new ArrayList<String>();
        List <Long> phoneList = new ArrayList<Long>();
        List <String> otherList = new ArrayList<String>();
        try
        {
            FileReader fileR = new FileReader("randomDateList.txt");
            BufferedReader bw = new BufferedReader(fileR);
            String line;
            while  (( line = bw.readLine() ) != null)
            {
                dateList.add(line);
            }
            bw.close();
            fileR.close();
            
            FileReader fileReader = new FileReader("newPhoneList.txt");
            BufferedReader buff = new BufferedReader(fileReader);
            while  (( line = buff.readLine() ) != null)
            {
                otherList.add(line);
            }
            buff.close();
            fileReader.close();
            
            String searchFor = "select phone_num from phone_number";
            ResultSet result = s.executeQuery (searchFor);
            while (result.next())
                phoneList.add(result.getLong(1));
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            int usage_id = 217341;
            for (int counter = 0; counter < 1515; counter++)
            {
                
                for (int count = rand.nextInt(dateList.size()) ; count < dateList.size() -1; count = count +  20)
                {
                    usage_id++;
                    int type = rand.nextInt(2);
                    String typeOf;
                    if (type == 1)
                        typeOf = "Destination";
                    else
                        typeOf = "Source";
                    String insertString = "insert into usage values("+usage_id + "," + phoneList.get(counter) + ",'" +typeOf + "')";

                    s.executeUpdate(insertString);
                                        System.out.println(insertString);
                    //int randomNumber = rand.nextInt(3);
                    int randomNumber = 2;
                    //System.out.println(randomNumber);
                    switch (randomNumber) 
                    {
                    //internet
                        case 0:
                            
                            int randomByte = rand.nextInt(30) + 1;
                            insertString = "insert into internet_usage values (" + usage_id + "," + randomByte + ")";
                            s.executeUpdate(insertString);
                            System.out.println("Net");
                        break;
                    //text
                        case 1:
                            
                                int randomPhone = rand.nextInt(299);
                                int size = rand.nextInt(200) + 1;
                                insertString = "insert into text_usage values (" + usage_id + ",TO_TIMESTAMP('" + dateList.get(count) + "','DD-MM-YYYY HH24:MI:SS')," +
                                        size + "," + otherList.get(randomPhone) + ")";
                                s.executeUpdate(insertString);
                                System.out.println("Text");
                        break;
                            
                        default:
                            //call
                            
                            int randomPhones = rand.nextInt(299);
                            int biscoito = rand.nextInt(613);
                            String time1 = dateList.get(count);
                            //String time2 = dateList.get(count + 1);
    
                            Calendar cal = Calendar.getInstance();
                           
                            java.util.Date d1 = format.parse(time1);
                            cal.setTime(d1);
                            cal.add(Calendar.MINUTE,rand.nextInt(5) + 1);
                            java.util.Date d2 = cal.getTime();
                            String time2 = format.format(d2);
                            //java.util.Date d2 = format.parse(time2);
                            
                            long duration = (d2.getTime() - d1.getTime()) / 1000;
                            insertString = "insert into call_usage values (" + usage_id + ",TO_TIMESTAMP('" + time1 + "','DD-MM-YYYY HH24:MI:SS')," +
                                    "TO_TIMESTAMP('" + time2 + "','DD-MM-YYYY HH24:MI:SS')," + duration + "," + otherList.get(randomPhones) + ")";
                            //System.out.println(insertString);
                            s.executeUpdate(insertString);
                            System.out.println("Call");
                        break;
                            
                    }
                }
                System.out.println("Moving on!");
            }
            
            
            
            
            
        }
        catch (FileNotFoundException ex)
        {
            System.out.println("Abiboreboborebabo");
        } catch (ParseException ex) {
            Logger.getLogger(PopulateProjectDB.class.getName()).log(Level.SEVERE, null, ex);
        }
                 
       
        s.close();
        con.close();
    }
    
        
    
}
