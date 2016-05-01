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
         
         
        Class.forName ("oracle.jdbc.driver.OracleDriver");
        con = DriverManager.getConnection
              ("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241","rcd315",
               "Med1nho93");
        s=con.createStatement();
        
        //Fills up phone table first:
        
        /*String manufacturerList[] = {"Samsung","Apple","Lenovo Motorola","Xiaomi","Huawei","LG Electronics","Sony Mobile","Microsoft"};
        
        String samsungPhones[] = {"Galaxy S7 Edge","Galaxy Note 6", "Galaxy S5", "Galaxy S3", "Galaxy S4"};
        String applePhones[] = {"iPhone 5","iPhone 5C","iPhone 6","iPhone 4","iPhone 4S"};
        String motorolaPhones[] = {"Moto X Force","Droid Turbo 2","Droid Maxx 2","Moto X Style","Moto G Turbo Edition"};
        String xiaomiPhones[] = {"Mi 5","Mi 4S","Redmi 3","Redmi Note Prime","Redmi Note 3"};
        String huaweiPhones[] = {"Honor Holly 2 Plus","Nexus 6P","Mate S","G7 Plus","Honor 5X","Y6 Pro"};
        String lgPhones[] = {"LG G5","LG V10","LG G4","LG G Flex 2"};
        String sonyPhones[] = {"Xperia X Performance","Xperia X","Xperia XA Dual","Xperia XA"};
        String microsoftPhones[] = {"Lumia 650","Lumia 950 XL Dual SIM","Lumia 950 XL","Lumia 950","Lumia 550"};
        
        
        FileReader fileR = new FileReader("randomDateList.txt");
                BufferedReader bw = new BufferedReader(fileR);
        List <String> store_id = new ArrayList<String>();
        List <String> dateList = new ArrayList<String>();
                String line;
                while ((line = bw.readLine()) != null)
                {

                    dateList.add(line);
                }
                    
        Long order_id = 1L;
        String searchFor = "select store_id from physical_store";
        ResultSet result = s.executeQuery(searchFor);
        
        while (result.next())
        {
            store_id.add(result.getString(1));
        }
        
  
        
       searchFor = "select account_id from sold natural join active_phone natural join phone_number natural join account\n" +
                    "group by account_id\n" +
                    "having (count(phone_num) > 1 and count(phone_num) < 4) or count(phone_num) > 4";
       result = s.executeQuery(searchFor);
       List <String> accountList = new ArrayList<String>();
       
       for (int counter = 0; counter < 50 && result.next(); counter++)
       {
           accountList.add(result.getString(1));
       }
       
       List <String> meid = new ArrayList<String>();
       List <String> cust_id = new ArrayList<String>();
       
       for (int counter = 0; counter < accountList.size(); counter++)
       {
           searchFor = "select meid,cust_id from owns natural join active_phone natural join phone_number natural join account\n" +
                        "where account_id = " + accountList.get(counter);
           result = s.executeQuery(searchFor);
           result.next();
           meid.add(result.getString(1));
           cust_id.add(result.getString(2));
       }
           
                
        
        for (int counter = 0; counter < accountList.size(); counter++)
        {
            int randomTimes = rand.nextInt(10) + 2;
            System.out.println(randomTimes);
            for (int counter2 = 0; counter2 < randomTimes; counter2++)
            {
                String model = "";
                int randomNum = rand.nextInt(8);
                String manufacturer = manufacturerList[randomNum];
                switch (randomNum)
                {
                    case 0:
                        randomNum = rand.nextInt(5);
                        model = samsungPhones[randomNum];
                        break;
                    case 1:
                        randomNum = rand.nextInt(5);
                        model = applePhones[randomNum];
                        break;
                    case 2:
                        randomNum = rand.nextInt(5);
                        model = motorolaPhones[randomNum];
                        break;
                    case 3:
                        randomNum = rand.nextInt(5);
                        model = xiaomiPhones[randomNum];
                        break;
                    case 4:
                        randomNum = rand.nextInt(6);
                        model = huaweiPhones[randomNum];
                        break;
                    case 5:
                        randomNum = rand.nextInt(4);
                        model = lgPhones[randomNum];
                        break;
                    case 6:
                        randomNum = rand.nextInt(4);
                        model = sonyPhones[randomNum];
                        break;
                    case 7:
                        randomNum = rand.nextInt(5);
                        model = microsoftPhones[randomNum];
                        break;
                    default:
                        break;

                }
                
                String removeString = "delete from sold where meid = " + meid.get(counter);
                s.executeUpdate(removeString);
               
                

                int number = rand.nextInt(174);
                String insertString = "insert into online_order values (" + order_id + "," + cust_id.get(counter) + "," + meid.get(counter) + ",TO_TIMESTAMP('" + dateList.get(number) + 
                        "','DD-MM-YYYY HH24:MI:SS'))";

                 System.out.println(insertString);
                
                s.executeUpdate(insertString);

               order_id += 1L;
            }
        }*/
        
       /* String searchFor = "select meid,cust_id from owns natural join phone natural left outer join sold\n" +
                            "where store_id is null";
        ResultSet  result = s.executeQuery(searchFor);
        
        List<String> meidList = new ArrayList<String>();
        List<String> idList = new ArrayList<String>();
        
        while (result.next())
        {
            meidList.add(result.getString(1));
            idList.add(result.getString(2));
        }
         Long order_id = 1L;
        
         FileReader fileR = new FileReader("randomDateList.txt");
         BufferedReader bw = new BufferedReader(fileR);
  
        List <String> dateList = new ArrayList<String>();
                String line;
                while ((line = bw.readLine()) != null)
                {

                    dateList.add(line);
                }
        for (int counter = 0; counter < idList.size(); counter++)
        {
            int number = rand.nextInt(174);
            String insertString = "insert into online_order values (" + order_id + "," + idList.get(counter) + "," + meidList.get(counter) + ",TO_TIMESTAMP('" + dateList.get(number) + 
                        "','DD-MM-YYYY HH24:MI:SS'))";
            s.executeUpdate(insertString);
            
            order_id += 1L;
        }*/
       
        FileReader fileR = new FileReader("randomDateList.txt");
         BufferedReader bw = new BufferedReader(fileR);
  
        List <String> dateList = new ArrayList<String>();
                String line;
                while ((line = bw.readLine()) != null)
                {

                    dateList.add(line);
                }
                
        String searchFor = "select meid from unactive_phone";
        ResultSet result = s.executeQuery(searchFor);
        List <String> idList = new ArrayList <String>();
        
        while (result.next())
            idList.add(result.getString(1));
                
                
        for (int counter = 0; counter < idList.size(); counter++)
        {
            int number = rand.nextInt(174);
            
           
           String insertString = "update unactive_phone SET network_time_period = TO_TIMESTAMP('" + dateList.get(number) + "','DD-MM-YYYY HH24:MI:SS') where meid = " + idList.get(counter);
            s.executeUpdate(insertString);
            
            
        }
        
        
       
        s.close();
        con.close();
    }
    
        
    
}
