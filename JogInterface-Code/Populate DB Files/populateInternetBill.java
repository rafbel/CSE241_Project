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
		
		
		//internet_bill

		Long billID = 199739803L;
        
        List <String> internet_id = new ArrayList<String>();
        List <String> time = new ArrayList<String>();
        List <Double> internet_rate = new ArrayList<Double>();
        List <Long> size_byte = new ArrayList<Long>();

        String searchString = "select end_time from call_usage order by usage_id";
        ResultSet result = s.executeQuery(searchString);
        while (result.next())
            time.add(result.getString(1));
        
        searchString = "select usage_id,byte_rate,total_usage\n" +
                            "from internet_usage natural join usage natural join phone_number natural join account natural join billing_plan";
        result = s.executeQuery(searchString);
        while (result.next())
        {
            internet_id.add(result.getString(1));

            internet_rate.add(result.getDouble(2));
            size_byte.add(result.getLong(3));
        }
        for (int counter = 0; counter < internet_id.size(); counter++)
        {
            double charging = internet_rate.get(counter) * size_byte.get(counter) ;
            String to_charge = String.format("%.2f", charging);
            billID += counter + 0L;
            String insertString = "insert into bill values (" + billID + "," + internet_id.get(counter) + "," + to_charge + ", TO_TIMESTAMP('" + (time.get(counter)) + "', 'YYYY-MM-DD HH24:MI:SS'))";
            System.out.println(insertString);
            s.executeUpdate(insertString);
        }
       
           
        s.close();
        con.close();
    }