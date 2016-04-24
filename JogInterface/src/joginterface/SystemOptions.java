/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package joginterface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafael
 */
public class SystemOptions 
{
    
    
    Statement s;
    Connection con;
    
    public SystemOptions() throws ClassNotFoundException
    {
        boolean credentialVerify = true;
        Scanner sc = new Scanner(System.in);
      
      //Checks to see if user entered the right credentials
      while (credentialVerify)
      {
          try
          {
            System.out.print("Enter Oracle user ID: ");
            String username = sc.next();
            System.out.print("Enter Oracle user password: ");
            String password = sc.next();

            credentialVerify = false;

            Class.forName ("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection
              ("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241",username,
               password);
              s=con.createStatement();
          }
          catch (SQLException e) {
              System.out.println("User not found. Please enter your credentials once again");
              credentialVerify = true;
          }
          catch (ClassNotFoundException ex)
          {
              System.out.println("Database error.Please try again.");
              credentialVerify = true;
          }
        }
    }
    
     public void close() throws SQLException
    {
        s.close();
        con.close();
    }
     
    public void readUsage()
    {
        boolean auth = false;
        String regex = "[0-9]+";
        try {
                Scanner sc = new Scanner(System.in);
                String inputFile;

                
                
                
                ResultSet result;
                String searchFor = "select usage_id from usage where usage_id >= all (select usage_id from usage)";
                result = s.executeQuery(searchFor);
                int usage_id = 1;
                if (result.next())
                    usage_id = result.getInt(1);
            
                int turn = 0;
                /*
                turn order:
                0 - text
                1 - call
                2 - internet
                */
                
                List <String> errorRecordList = new ArrayList<String>();

                
                do
                {
                    
                    String line;
                    try
                    {
                        System.out.println("Please type the input file name:");
                        inputFile = sc.next();
                        
                        FileReader fileR = new FileReader(inputFile);
                        BufferedReader buff = new BufferedReader(fileR);
                        
                        while ((line = buff.readLine()) != null)
                        {
                            String[] splitLine = line.split(",");
                            
                            //Decide what type of usage it is: (first read is text, second is call, third is internet)

                                if (turn == 0) //text
                                {
                                    turn = 1;
                                    boolean presentError = false;
                                    if ( splitLine.length != 4) //if contains more/less than 4 arguments
                                    {
                                        errorRecordList.add(line);
                                        presentError = true;
                                    }
                                    else
                                    {
                                        try
                                        {
                                            
                                            //Check length of phone and if it is composed of only numbers
                                            if (splitLine[0].length() != 10 && !splitLine[0].matches(regex))
                                            {
                                                errorRecordList.add(line);
                                                presentError = true;
                                            }
                                            else
                                            {
                                                //Check if it is included in the DB

                                                boolean includedFirstNum;
                                                boolean includedSecondNum;

                                                searchFor = "select phone_num from phone_number where phone_num = " + splitLine[0];
                                                result = s.executeQuery(searchFor);

                                                includedFirstNum = result.next();

                                                //For destination phone number:
                                                if (splitLine[1].length() != 10 && !splitLine[1].matches(regex))
                                                {
                                                    errorRecordList.add(line);
                                                    presentError = true;
                                                }
                                                else
                                                {
                                                    searchFor = "select phone_num from phone_number where phone_num = " + splitLine[1];
                                                    result = s.executeQuery(searchFor);
                                                    includedSecondNum = result.next();

                                                    if (!includedSecondNum && !includedFirstNum) //if both phone numbers are not in the database
                                                    {
                                                        errorRecordList.add(line);
                                                        presentError = true;
                                                    }
                                                    else
                                                    {
                                                        if (!splitLine[3].matches(regex)) //size is a string and not a number
                                                        {
                                                            presentError = true;
                                                            errorRecordList.add(line);
                                                        }

                                                        else //can procede to insert (must check throw exception for timestamp format)
                                                        {
                                                            String insertString;
                                                            if (includedFirstNum)
                                                            {
                                                                usage_id++;
                                                                insertString = "insert into usage values (" + usage_id + "," + splitLine[0] + ",'Source')";
                                                                s.executeUpdate(insertString);

                                                                insertString = "insert into text_usage values (" + usage_id + ", TO_TIMESTAMP('" + splitLine[2] + 
                                                                        "', 'DD-MM-YYYY HH24:MI:SS')," + splitLine[3] + "," + splitLine[1] + ")";
                                                                s.executeUpdate(insertString);
                                                            }

                                                            if (includedSecondNum)
                                                            {
                                                                usage_id++;
                                                                insertString = "insert into usage values (" + usage_id + "," + splitLine[1] + ",'Source')";
                                                                s.executeUpdate(insertString);

                                                                insertString = "insert into text_usage values (" + usage_id + ", TO_TIMESTAMP('" + splitLine[2] + 
                                                                        "', 'DD-MM-YYYY HH24:MI:SS')," + splitLine[3] + "," + splitLine[0] + ")";
                                                                s.executeUpdate(insertString);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } 
                                        catch (SQLException ex) 
                                        {
                                            Logger.getLogger(SystemOptions.class.getName()).log(Level.SEVERE, null, ex);
                                            if (!presentError)
                                                errorRecordList.add(line);
                                        }

                                        
                                        
                                    }
                                    
                                    
                                }
                                else if (turn == 1) //call
                                {
                                    turn = 2;
                                    boolean addError = false;
                                    if (splitLine.length != 4) //checks if received the correct number of arguments
                                    {

                                        errorRecordList.add(line);
                                        addError = true;
                                    }
                                    else
                                    {
                                        if (splitLine[0].length() != 10 || !splitLine[0].matches(regex) || splitLine[1].length() != 10 || !splitLine[1].matches(regex))
                                        {
                                            errorRecordList.add(line);
                                            addError = true;
                                        }
                                        else
                                        {
                                            try
                                            {

                                                searchFor = "select phone_num from phone_number where phone_num = " + splitLine[0];
                                                result = s.executeQuery(searchFor);

                                                boolean validFirstNum = result.next();

                                                searchFor = "select phone_num from phone_number where phone_num = " + splitLine[1];
                                                result = s.executeQuery(searchFor);

                                                boolean validStNum = result.next();
                                                
                                                if (!validFirstNum && !validStNum)
                                                {

                                                    errorRecordList.add(line);
                                                    addError = true;
                                                    
                                                }
                                                else
                                                {
                                                    
                                                    //CHECK IF DURATION IS VALID
                                                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                                                    Date d1 = format.parse(splitLine[2]);
                                                    Date d2 = format.parse(splitLine[3]);
                                                    
                                                    long duration = (d2.getTime() - d1.getTime()) / 1000;
                                                    
                                                    if (duration < 0)
                                                    {

                                                        errorRecordList.add(line);
                                                        addError = true;
                                                    }
                                                    else
                                                    {
                                                    
                                                    
                                                    
                                                        String insertString;


                                                        if (validFirstNum)
                                                        {
                                                            usage_id++;
                                                            insertString = "insert into usage values (" + usage_id + "," + splitLine[0] + ",'Source')";
                                                            s.executeUpdate(insertString);

                                                            insertString = "insert into call_usage values (" + usage_id + ",TO_TIMESTAMP('" + splitLine[2] + "','DD-MM-YYYY HH24:MI:SS'),"
                                                                    + "TO_TIMESTAMP('" + splitLine[3] + "','DD-MM-YYYY HH24:MI:SS')," + duration + "," + splitLine[1] + ")";
                                                            s.executeUpdate(insertString);
                                                        }

                                                        if (validStNum)
                                                        {
                                                            usage_id++;
                                                            insertString = "insert into usage values (" + usage_id + "," + splitLine[1] + ",'Destination')";
                                                            s.executeUpdate(insertString);

                                                            insertString = "insert into call_usage values (" + usage_id + ",TO_TIMESTAMP('" + splitLine[2] + "','DD-MM-YYYY HH24:MI:SS'),"
                                                                    + "TO_TIMESTAMP('" + splitLine[3] + "','DD-MM-YYYY HH24:MI:SS')," + duration + "," + splitLine[0] + ")";
                                                            s.executeUpdate(insertString);
                                                        }
                                                    }
                                                }
                                            } 
                                            catch (SQLException ex) 
                                            {
                                                Logger.getLogger(SystemOptions.class.getName()).log(Level.SEVERE, null, ex);
                                                if (addError == false)
                                                    errorRecordList.add(line);
                                            }
                                            catch (java.text.ParseException ex)
                                            {
                                                if (addError == false)
                                                    errorRecordList.add(line);
                                            }
                                        }
                                        }
                                    
                                    
                                
                                }
                                    
                                else //internet
                                {
                                    turn = 0;
                                    boolean insertError = false;
                                    if (splitLine.length != 3)
                                    {

                                        errorRecordList.add(line);
                                        insertError = true;
                                        
                                    }
                                    else
                                    {
                                        if (!splitLine[0].matches(regex) || splitLine[0].length() != 10)
                                        {
                                            errorRecordList.add(line);
                                            insertError = true;
                                        }
                                        else
                                        {
                                            if (!splitLine[1].equals("upload") && !splitLine[1].equals("download"))
                                            {
                                                errorRecordList.add(line);
                                                insertError = true;
                                            }
                                            else
                                            {
                                                if (!splitLine[2].matches(regex))
                                                {
                                                    insertError = true;
                                                    errorRecordList.add(line);
                                                }
                                                
                                                else
                                                {
                                                    String type;
                                                    if (splitLine[1].equals("upload"))
                                                        type = "Source";
                                                    else
                                                        type = "Destination";
                                                    
                                                    try
                                                    {
                                                        usage_id++;
                                                        String insertString = "insert into usage values (" + usage_id + "," + splitLine[0] + ",'" + type + "')";
                                                        s.executeUpdate(insertString);

                                                        insertString = "insert into internet_usage values (" + usage_id + "," + splitLine[2] + ")";
                                                        s.executeUpdate(insertString);
                                                    }
                                                    catch (SQLException ex)
                                                    {
                                                        Logger.getLogger(SystemOptions.class.getName()).log(Level.SEVERE, null, ex);
                                                        if (insertError == false)
                                                            errorRecordList.add(line);
                                                       
                                                    }
                                                }
                                            }
                                        }
                                            
                                    }
                                }

                                    
                                    
                                    
                        }
                           
                            
                          
                            
                            auth = false;
                         
                        
                        
                        
                        
                    }
                    catch (FileNotFoundException e)
                    {
                        System.out.println("File not found. Please try again");
                        auth = true;
                    }
                    catch (IOException ex)
                    {
                        System.out.println("File could not be read. Please try again");
                        auth = true;
                    }
                    
                    
                }while (auth);

                
            boolean keepLoop = false;
            String outputFile = null;
            do
            {
                try {
                    //Error file write:
                    System.out.println("Please type the output error file name:");
                    outputFile = sc.next();
                    
                    File outFile = new File(outputFile);
                    
                    if (outFile.createNewFile())
                        System.out.println("Output file " + outputFile + " has been created!");
                    
                    FileWriter fileW = new FileWriter(outFile);
                    BufferedWriter buffW = new BufferedWriter(fileW);
                    PrintWriter writer = new PrintWriter(buffW);
                    
                    for (Iterator<String> iter = errorRecordList.iterator(); iter.hasNext(); )
                    {
                        String element = iter.next();
                        writer.println(element);
                        
                    }
                  writer.close();
                  buffW.close();
                  fileW.close();
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(SystemOptions.class.getName()).log(Level.SEVERE, null, ex);
                    keepLoop = true;
                    System.out.println("An error occured when trying to write on the following file: " + outputFile);
                    

                }
                
            } while (keepLoop);
                
        } catch (SQLException ex) {
            Logger.getLogger(SystemOptions.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Database Server Error. Please try again later.");
        }
        

        
        
        
    }
    public void printBill()
    {
        try {
            Scanner sc = new Scanner (System.in);
            int customerID = 0;
            while (true)
            {
                System.out.println("Please enter a valid customer ID");
                
                while (!sc.hasNextInt())
                {
                    System.out.println("Please enter valid customer ID");
                    sc.next();
                }
                customerID = sc.nextInt();
                String searchFor = "select cust_id from customer where cust_id = " + customerID;
                ResultSet result = s.executeQuery(searchFor);

                if (result.next())
                    break;
                else
                    System.out.println("No customer with given ID found");
            }
            int month = 0;
            System.out.println("Please enter the desired month in numeric form");
            while (true)
            {
                while (!sc.hasNextInt())
                {
                    System.out.println("Month must be in numeric form. Please enter again");
                    sc.next();
                }
                month = sc.nextInt();
                
                if (month >= 1 || month <= 12)
                    break;
                else
                {
                    System.out.println("Invalid month. Please enter value again");
                }
                
               
            }
            
            //System gets current year:
            int year = Calendar.getInstance().get(Calendar.YEAR);
            
            //Selects from bill: individual cost + time created
            String monthString = Integer.toString(month);
            if (monthString.length() == 1)
                monthString = "0" + monthString;
            int day;
            switch (monthString)
            {
                case "01":
                case "03":
                case "05":
                case "07":
                case "08":
                case "10":
                case "12":
                    day = 31;
                    break;
                case "02":
                    day = 29;
                    break;
                default:
                    day = 30;
                    break;
                    
            }
            
            String searchFor = "select to_charge,curr_date from bill natural join usage natural join phone_number natural join active_phone natural join owns natural join customer"
                    + " where cust_id = " + customerID + " and curr_date >= TO_TIMESTAMP('" + year + "-" + monthString + "-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')"
                    + " and curr_date <= TO_TIMESTAMP('" + year + "-" + monthString + "-" + day + " 23:59:59', 'YYYY-MM-DD HH24:MI:SS')";
            ResultSet result = s.executeQuery(searchFor);
            
            int billCounter = 1;
            double total_value = 0;
            while (result.next())
            {
                System.out.println("Bill #" + billCounter);
                System.out.println("Issued: " + result.getString(2) + " - Value to be paid: $" + result.getString(1));
                System.out.println();
                total_value += result.getDouble(1);
                billCounter ++;
            }
            System.out.println("Total value: $" + total_value );
            System.out.println();
            
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(SystemOptions.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Database error. Please try again later");
        }
        
    }
    
}
