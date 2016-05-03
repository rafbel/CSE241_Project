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
		
			 //has unactive_phone version
		 
		  String searchFor = "select *\n" +
                            "from customer natural left outer join has\n" +
                            "where account_id is null";
        List <Integer> customerList = new ArrayList<Integer>();
        List <Integer> meidList = new ArrayList<Integer>();
        ResultSet result = s.executeQuery(searchFor);
        while (result.next())
            customerList.add(result.getInt("cust_id"));
        searchFor = "select * from unactive_phone";
        result = s.executeQuery(searchFor);
        while (result.next())
            meidList.add(result.getInt("meid"));
       String insertString;
        for (int counter = 0; counter < customerList.size(); counter++)
        {
            insertString = "insert into owns values ('" + customerList.get(counter) + "','" + meidList.get(counter) + "')";
            s.executeUpdate(insertString);
        }
        
          

       
           
        s.close();
        con.close();
    }