package joginterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafael
 */
public class CustomerOptions
{
    Statement s = null;
    Connection con = null;
    String customerID;
    String accountID;
    String accountType;

    
    
    public CustomerOptions() throws ClassNotFoundException, SQLException //initializes BD -> completed
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
    
      
        authAccount();
    }
    
    public void changePlan() throws SQLException //changes billing plan -> completed
    {
        int newPlan = 0;
        String callRate =""; String textRate = "";String byteRate =""; String monthlyRate = ""; String billing_id = "";
        
        String searchString = "select plan_type,billing_id from billing_plan";
        //String searchString = "select billing_id from customer natural join has natural join account natural join acc_plan natural join billing_plan where cust_id = '" + customerID + "'";
        
        //get billing id:
        ResultSet result = s.executeQuery(searchString);
        
        System.out.println("Please enter the number associated with the type of billing plan you desire");
        System.out.println("Available plans:");
        
        while (result.next())
        {
            System.out.println(result.getString(1) + " - " + result.getString(2));
        }
        
        Scanner sc = new Scanner(System.in);
        //gets rates depending on the new plan
        while (true)
        {
            while (!sc.hasNextInt())
            {
                System.out.println("Please enter an appropriate number"); 
                sc.next();
            }
            newPlan = sc.nextInt();
            
            if (newPlan > 0 && newPlan < 5)
                break;
            else 
                System.out.println("Please enter an appropriate choice"); 
       
        }
        
        String insertString = "update acc_plan SET billing_id = " + newPlan + " where account_id =" + accountID;
        s.executeUpdate(insertString);
        System.out.println("Billing plan was changed.");
        
        
    }
    
    public void requestNewPhone() throws SQLException //requests new phone from the online store with INFINITE inventory -> complete
    {
        String searchFor;
        boolean authorized = false;
        ResultSet result;
        //First checks account type to see if it can be placed into the account
        if (accountType.equals("Family")) //checks to see 
        {
            searchFor = "select count(*) from phone_number where account_id = " + accountID + " group by account_id";
            result = s.executeQuery(searchFor);
            result.next();
            if (result.getInt(1) > 4)
            {
                System.out.println("This family account is full and doesn't support additional phones");
            }
            else
                authorized = true;
        }
        
        else if (accountType.equals("Business"))
            authorized = true;
        
        else
            System.out.println("This is an individual account. Individual accounts only support one phone");
        
        if (authorized)
        {
            Scanner scanner = new Scanner(System.in);
            List <String> modelList = new ArrayList<String>();
            List <String> manufacturerList = new ArrayList<String>();
            //Brings a new list of phones:
            searchFor = "select distinct model,manufacturer\n" +
                                "from phone\n" +
                                "order by manufacturer";

            result = s.executeQuery(searchFor);
            System.out.format("%-32s %-32s\n", "Model","Manufacturer");
            while (result.next())
            {
                modelList.add( result.getString(1));
                manufacturerList.add(result.getString(2));
                System.out.format("%-32s %-32s\n", result.getString(1),result.getString(2));
            }

            int counter;
            String reqModel = "";
            String manufacturer = "";
            
            
            while (true)
            {
                System.out.println("Enter the manufacturer of the product desired.");
                Scanner manSc = new Scanner(System.in);
                manufacturer = manSc.nextLine(); 
                System.out.println("Enter the model of the product desired.");
                Scanner modSc = new Scanner(System.in);
                reqModel = modSc.nextLine(); 
                
                searchFor = "select *" +
                                " from phone where manufacturer = '" + manufacturer + "' and model = '" + reqModel+ "'";

                result = s.executeQuery(searchFor);
                if (result.next())
                    break;
                else
                {
                    
                    System.out.println("No such product found. Please try again.");
                }
                
            }


            //Inserts new phone into phone table
            int meid = 1000;
            searchFor = "select meid\n" +
                        "from phone\n" +
                        "where meid >=all (select meid from phone)";
            result = s.executeQuery(searchFor);

            if (result.next())
            {
                meid = result.getInt(1) + 1;
            }

            

            //Inserts Order into online_order table
            searchFor = "select order_id "
                    + "from online_order "
                    + "where order_id >= all(select order_id from online_order)";
            result = s.executeQuery(searchFor);
            int order_id = 1;

            if (result.next())
            {
                order_id = result.getInt(1) + 1;
            }
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            String formattedDate = sdf.format(date);

            String insertString = "insert into phone values ('" + meid + "','" + manufacturer + "','" + reqModel + "')";
            s.executeUpdate(insertString);
            
            insertString = "insert into online_order values ('" + order_id + "','" + customerID + "','" + meid + "',TO_TIMESTAMP('" + formattedDate + "', 'DD-MM-YYYY HH24:MI:SS') )";
            s.executeUpdate(insertString);

            //Inserts into owns table
            insertString = "insert into owns values ('" + customerID + "','" + meid +  "')";
            s.executeUpdate(insertString);
            

            //GENERATE PHONE NUMBER
            Random rand = new Random();
            String phoneNum = "";
            while (true)
            {
                
                Long randomNumber = rand.nextLong();
                if (randomNumber < 0)
                    randomNumber = -randomNumber;
                phoneNum = "" + randomNumber;
                if (phoneNum.length() > 10)
                    phoneNum = phoneNum.substring(0,10);
                searchFor = "select phone_num from phone_number where phone_num = " + phoneNum;
                result = s.executeQuery(searchFor);
                
                if (!result.next())
                    break;
                
                
            }
            
            //insert into phone_number table
            insertString = "insert into phone_number values ('" + phoneNum + "','" + accountID + "','Other')";
            s.executeUpdate(insertString);
            
            //insert into active_phone
            insertString = "insert into active_phone values ('" + meid + "','" + phoneNum + "')";
            s.executeUpdate(insertString);
                    
            System.out.println("Phone number for the requested phone is " + phoneNum);
            
        }
        
    }
    
   
  
    private void authAccount() throws SQLException //authorizes user -> completed
    {
        boolean auth = false;
        Scanner scanner = new Scanner(System.in);
        ResultSet result;
        String searchFor = "select account_id, password from account";
        
        int id = 1000;
        while (!auth)
        {
            System.out.println("Please enter your account ID and password");
            while (!scanner.hasNextInt()) 
            {
               System.out.println("Please enter an acceptable ID");
               scanner.next();
            }
            id = scanner.nextInt();
            String pass = scanner.next();
            
            result = s.executeQuery(searchFor);
            
            while (result.next())
            {
                if (result.getString(2).equals(pass) && id == result.getInt(1))
                {
                    auth = true;
                    break;
                }
            }
        }
        
        searchFor = "select name,customer_address,cust_id from customer natural join has where account_id = " + id;
        result = s.executeQuery(searchFor);
        System.out.format("%-10s %-20s %-48s\n", "Option", "Name", "Customer Address");
        int option = 0;
        List <String> customerList = new ArrayList<String>(); 
        
        while (result.next())
        {
            option++;
            System.out.format("%-10s %-20s %-48s\n", option, result.getString(1), result.getString(2));
            customerList.add(result.getString(3));
        }
        
        auth = false;
        int choice = 0;
        while (true)
        {
            System.out.println("Enter the number associated with your name");

            while (!scanner.hasNextInt()) 
            {
                   System.out.println("Please enter an acceptable number");
                   scanner.next();
            }
            choice = scanner.nextInt();
            if (choice >= 1 && choice <= customerList.size())
                    break;
        }
        
        customerID = customerList.get(choice - 1);
        accountID = Integer.toString(id);
        
        //Tries to figure out what kind of account it is
        searchFor = "select * from individual_account where account_id = " + accountID;
        result = s.executeQuery(searchFor);
        
        if (result.next())
        {
            accountType = "Individual";
        }
        else
        {
            searchFor = "select * from family_account where account_id = " + accountID;
             result = s.executeQuery(searchFor);
        
            if (result.next())
            {
                accountType = "Family";
            }
            else
                accountType = "Business";
        }
        
        //Missing inserts, figure out what do with network_time_period
        
        
        
    }
    
    public void closeConnection() throws SQLException //closes the connection -> completed
    {
        s.close();
        con.close();
        
    }
    
}   
