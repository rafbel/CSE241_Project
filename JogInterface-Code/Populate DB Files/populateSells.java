//populate sells
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
        
        //Fills up phone table first:
        int MEID = 21444;
        String manufacturerList[] = {"Samsung","Apple","Lenovo Motorola","Xiaomi","Huawei","LG Electronics","Sony Mobile","Microsoft"};
        
        String samsungPhones[] = {"Galaxy S7 Edge","Galaxy Note 6", "Galaxy S5", "Galaxy S3", "Galaxy S4"};
        String applePhones[] = {"iPhone 5","iPhone 5C","iPhone 6","iPhone 4","iPhone 4S"};
        String motorolaPhones[] = {"Moto X Force","Droid Turbo 2","Droid Maxx 2","Moto X Style","Moto G Turbo Edition"};
        String xiaomiPhones[] = {"Mi 5","Mi 4S","Redmi 3","Redmi Note Prime","Redmi Note 3"};
        String huaweiPhones[] = {"Honor Holly 2 Plus","Nexus 6P","Mate S","G7 Plus","Honor 5X","Y6 Pro"};
        String lgPhones[] = {"LG G5","LG V10","LG G4","LG G Flex 2"};
        String sonyPhones[] = {"Xperia X Performance","Xperia X","Xperia XA Dual","Xperia XA"};
        String microsoftPhones[] = {"Lumia 650","Lumia 950 XL Dual SIM","Lumia 950 XL","Lumia 950","Lumia 550"};
        
        List <String> store_id = new ArrayList<String>();
        
        String searchFor = "select store_id from physical_store";
        ResultSet result = s.executeQuery(searchFor);
        while (result.next())
            store_id.add(result.getString(1));
        
        for (int counter = 0; counter < 1913; counter++)
        {
            int randomTimes = rand.nextInt(10) + 2;
            
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
                String insertString = "insert into phone values (" + MEID + "," + "'" + manufacturer + "','" + model + "')";

                s.executeUpdate(insertString);

                insertString = "insert into sells values (" + store_id.get(counter) + "," + MEID + ")";

                s.executeUpdate(insertString);

               MEID++;
            }
        }
       
        s.close();
        con.close();
    }