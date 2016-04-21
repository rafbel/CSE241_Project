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
    DBCon connection;
    
    
    public CustomerOptions() throws ClassNotFoundException, SQLException //initializes BD -> completed
    {
        connection = new DBCon(s,con);
    
      
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
            System.out.println(result.getString(result.getString(1) + " - " + result.getString(2)));
        }
        
        Scanner sc = new Scanner(System.in);
        //gets rates depending on the new plan
        while (true)
        {
            while (sc.hasNextInt())
            {
                System.out.println("Please enter an appropriate choice"); 
                sc.next();
            }
            newPlan = sc.nextInt();
            
            if (newPlan > 0 || newPlan < 5)
                break;
            else 
                System.out.println("Please enter an appropriate choice"); 
       
        }
        
        String insertString = "update acc_plan SET billing_id = " + newPlan + " where account_id =" + accountID;
        s.executeUpdate(insertString);
        
        //updates billing plan on Oracle DB
       /* String updateString = "update billing_plan SET plan_type = '" + newPlan + "' ,call_rate = '" + callRate + "' ,textRate = '" + textRate + "' ,byteRate = '" + byteRate + "' ,monthlyRate = '" + monthlyRate 
                + "' where billing_id = '" + billing_id + "'";
        s.executeUpdate(updateString);*/
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
                System.out.println("This family account is full. Please upgrade account to a business account if you wish to add more phones");
            }
            else
                authorized = true;
        }
        
        else if (accountType.equals("Business"))
            authorized = true;
        
        else
            System.out.println("This is an individual account. Please upgrade account to a business or family account if you wish to add more phones");
        
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
            System.out.format("%32s %32s\n", "Model","Manufacturer");
            while (result.next())
            {
                modelList.add( result.getString(1));
                manufacturerList.add(result.getString(2));
                System.out.format("%32s %32s\n", result.getString(1),result.getString(2));
            }

            int counter;
            String reqModel;
            String manufacturer = "";
            do
            {
                System.out.println("Please select a phone model from the list above:");
                reqModel = scanner.next();
                for (counter = 0; counter < modelList.size(); counter++)
                {
                    if (modelList.get(counter).equals(reqModel))
                    {
                        manufacturer = manufacturerList.get(counter);
                        break;
                    }
                }
            }while (!modelList.get(counter).equals(reqModel));


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

            String insertString = "insert into phone values ('" + meid + "','" + manufacturer + "','" + reqModel + "')";
            s.executeUpdate(insertString);

            //Inserts Order into online_order table
            searchFor = "select order_id "
                    + "from online_order"
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

            insertString = "insert into online_order values ('" + order_id + "','" + customerID + "','" + meid + "',TO_TIMESTAMP('" + formattedDate + "', 'DD-MM-YYYY HH24:MI:SS') )";
            s.executeUpdate(insertString);

            //Inserts into owns table
            insertString = "insert into owns values ('" + customerID + "','" + meid +  "')";
            s.executeUpdate(insertString);

            //GENERATE PHONE NUMBER
            Random rand = new Random();
            boolean auth = true;
            String phoneNum = "";
            while (auth)
            {
                Long randomNumber = rand.nextLong();
                if (randomNumber < 0)
                    randomNumber = -randomNumber;
                phoneNum = "" + randomNumber;
                if (phoneNum.length() > 10)
                    phoneNum = phoneNum.substring(0,10);
                searchFor = "select phone_num from phone_number";
                result = s.executeQuery(searchFor);
                
                while (result.next())
                {
                    if (result.getString(1).equals(phoneNum))
                    {
                        break;
                    }
                }
                if (!result.getString(1).equals(phoneNum))
                    auth = false;
            }
            
            //insert into phone_number table
            insertString = "insert into phone_number values ('" + phoneNum + "','" + accountID + "','Other')";
            s.executeUpdate(insertString);
            
            //insert into active_phone
            insertString = "insert into active_phone values ('" + meid + "','" + phoneNum + "')";
            s.executeUpdate(insertString);
                    
            
            
        }
        
    }
    
    
    public void terminateService() //user wishes to terminate his account
    {
        try //user wishes to terminate his account
        {
            //Makes all phones unactive, deletes account
            String searchFor = "select phone_num,meid from active_phone natural join phone_number natural join account";
            List <String> phoneList = new ArrayList<String>();
            List <String> meidList = new ArrayList<String>();
             
            ResultSet result = s.executeQuery(searchFor);
            while(result.next())
            {
                phoneList.add(result.getString(1));
                meidList.add(result.getString(2));
            }
            
            
                    
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(CustomerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public void deactivatePhone() //turns active phone into unactive phone
    {
        String checkString = "select count(*) from phone_number where account_id = " + accountID;
        try
        {
            ResultSet result = s.executeQuery(checkString);
            if (!result.next())
            {
                System.out.println("Database error. Please try again later.");
            }
            else
            {
                int numberPhone = result.getInt(1);
                if (numberPhone <= 1)
                {
                    System.out.println("Only one phone number was detected in this account. Would you like to remove this phone number and terminate your account?");
                    System.out.println("1 - Yes");
                    System.out.println("2 - No");
                    
                    int choice = 0;
                    Scanner scanner = new Scanner(System.in);
                    while (true)
                    {
                        while (scanner.hasNextInt())
                        {
                            System.out.println("Please enter a valid option");
                            scanner.next();
                        }
                        choice = scanner.nextInt();
                        
                        if (choice == 1 || choice == 2)
                        {
                            break;
                        }
                        else
                        {
                            System.out.println("Please enter a valid option");
                        }
                    }
                    
                    if (choice == 0)
                        System.out.println("Returning to main menu");
                    else
                        terminateService();
                }
                else
                {
                    List<String> phoneList = new ArrayList<String>();

                    String searchFor = "select phone_num from phone_number where account_id = " + accountID;
                    result = s.executeQuery(searchFor);

                    System.out.println("Phone numbers linked to your account:");
                    int counter = 0;
                    while (result.next())
                    {
                        counter++;
                        phoneList.add(result.getString(1));
                        System.out.println(counter + " - " + result.getString(1));
                    }
                    boolean stay = true;
                    Scanner sc = new Scanner(System.in);
                    String phoneNum ="";
                    int option = 0;
                    while (stay)
                    {
                        System.out.println("Enter the number next to the phone number you wish to deactivate");
                        while (sc.hasNextInt())
                        {
                            System.out.println("Please enter the number next to the phone number you desire to deactivate");
                            sc.next();
                        }
                        option = sc.nextInt();
                        if (option >= 1 || option <= counter)
                        {
                            option -= 1;
                            phoneNum = phoneList.get(option);
                            stay = false;
                        }
                    }
                    
                    searchFor = "select phone_type from phone_number where phone_num = " + phoneNum;
                    result = s.executeQuery(searchFor); result.next();
                    String phoneType = result.getString(1);
                    
                    if (phoneType.equals("Primary")) //makes another phone primary if the current one is
                    {
                        System.out.println("Phone number is set as a primary phone number. Please choose another number to be set as primary");
                        for (int count = 0; count < option; count++)
                        {
                            System.out.println(count + " - " + phoneList.get(count));
                        }
                        for (int count = option + 1; count < phoneList.size(); count++)
                        {
                            System.out.println(count + " - " + phoneList.get(count));
                        }
                        int theChoice;
                        while (true)
                        {
                            while (sc.hasNextInt())
                            {
                                System.out.println("Please choose an appropriate option");
                                sc.next();
                            }
                            theChoice = sc.nextInt();
                            
                            if (theChoice >= 0 || theChoice <= phoneList.size() || theChoice != option)
                                break;
                        }
                        
                        String newPrime = phoneList.get(theChoice);
                        String updateString = "update phone_number SET phone_type = 'Primary' where phone_num = " + newPrime;
                        s.executeUpdate(updateString);
                            
                    }

                    //Makes phone unactive:
                    int info_id = 1;
                    searchFor = "select info_id from unactive_phone where info_id >= all (select info_id from unactive_phone)";
                    result = s.executeQuery(searchFor);
                    if (result.next())
                        info_id = result.getInt(1) + 1;
                    
                    //Calculate network time period:
                    
                    //Deletes phone number:



                }
            } 
        }
        catch (SQLException ex)
        {
            Logger.getLogger(CustomerOptions.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Database error. Please try again later.");
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
                if (result.getString(2).equals(pass) || id == result.getInt(1))
                {
                    auth = true;
                    break;
                }
            }
        }
        
        searchFor = "select name,customer_address,customer_id from customer natural join has where account_id = " + id;
        result = s.executeQuery(searchFor);
        System.out.format("%10s %20s %48s", "Option", "Name", "Customer Address");
        int option = 0;
        List <String> customerList = new ArrayList<String>(); 
        
        while (result.next())
        {
            option++;
            System.out.format("%10s %20s %48s", option, result.getString(1), result.getString(2));
            customerList.add(result.getString(3));
        }
        
        auth = false;
        int choice = 0;
        while (!auth)
        {
            System.out.println("Enter the number associated with your name");

            while (!scanner.hasNextInt()) 
            {
                   System.out.println("Please enter an acceptable number");
                   scanner.next();
            }
            choice = scanner.nextInt();
            if (choice > 0 || choice <= customerList.size())
            {
                System.out.println("Please enter an acceptable number");
                auth = true;
            }
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
        connection.close(s,con);
        
    }
    
      
   /* public void switchAccType()
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter one of the available choices");
        switch(accountType)
        {
            case "Individual":
                System.out.println("1 - Upgrade to a family account");
                System.out.println("2 - Upgrade to a business account");
                System.out.println("3 - Cancel");
                int option;
              
                
                while (true)
                {
                
                    while (!sc.hasNextInt()) 
                    {
                       System.out.println("Please enter an accetable option");
                       sc.next();
                    }
                    option = sc.nextInt();
                    
                    if (option > 0 || option < 4)
                        break;
                    else
                        System.out.println("Please enter an accetable option");
                
                }
                String table ="";
                
                switch(option)
                {
                    case 1: 
                        table = "family_account";
                                
                        
                }
                
                
                
                
                break;
            case "Family":
                break;
        }
    }*/
    
    
    
    
    /*public void newService() //creates new account -> insert into other class if it is to be made
    {
        //Checks if user is an existing customer
        
        
        System.out.println("Choose account type by entering its number:");
        System.out.println("1 - Individual (one customer)");
        System.out.println("2 - Family (up to 4 customers)");
        System.out.println("3 - Business (unlimited customers)");
        
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        while (true)
        {
            while (!scanner.hasNextInt())
            {
                System.out.println("Please enter one of the options above");
                scanner.next();
            }
            option = scanner.nextInt();
            if (option > 0 && option < 4)
                break;
        }
        String pass ="";
        
        do
        {
            System.out.println("Enter a password of minimum 4 characters and maximum 11");
            pass = scanner.next();
        } while (pass.length() < 4 && pass.length() > 11);
        
        switch (option)
        {
            case 1:
                break;
        }
    }*/
}
