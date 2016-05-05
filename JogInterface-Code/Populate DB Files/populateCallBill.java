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
		
		
		//call_bill
		int billID = 1;
        
        List <String> call_id = new ArrayList<String>();
        List <String> time = new ArrayList<String>();
        List <Double> call_rate = new ArrayList<Double>();
        List <Long> duration = new ArrayList<Long>();
        
        String searchString = "select usage_id,end_time,duration,call_rate\n" +
                            "from call_usage natural join usage natural join phone_number natural join account natural join billing_plan";
        ResultSet result = s.executeQuery(searchString);
        while (result.next())
        {
            call_id.add(result.getString(1));
            time.add(result.getString(2));
            call_rate.add(result.getDouble(4));
            duration.add(result.getLong(3));
        }
        for (int counter = 0; counter < call_id.size(); counter++)
        {
            double charging = call_rate.get(counter)*duration.get(counter)/60;
            String to_charge = String.format("%.2f", charging);
            billID += counter;
            String insertString = "insert into bill values (" + billID + "," + call_id.get(counter) + "," + to_charge + ", TO_TIMESTAMP('" + (time.get(counter)) + "', 'YYYY-MM-DD HH24:MI:SS'))";
            System.out.println(insertString);
            s.executeUpdate(insertString);
        }

       
           
        s.close();
        con.close();
    }