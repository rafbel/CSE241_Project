/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafael
 */
public class RetailStoreOptions 
{
    Statement s = null;
    Connection con = null; 
    int storeID;
    
    
    public RetailStoreOptions() throws ClassNotFoundException
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
              
            
            boolean incorrect = true;
            do
            {

                System.out.println("Please enter store identification number");
                while (!sc.hasNextInt())
                {
                    System.out.println("Please enter valid store ID");
                    sc.next();
                }
                storeID = sc.nextInt();

                String searchFor = "select store_id from physical_store where store_id = " + storeID;
                ResultSet result = s.executeQuery(searchFor);
                if (result.next())
                    incorrect = false;
                else
                    System.out.println("Store identification number not found.");

            }while (incorrect);
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
    
    public void checkInventory()
    {
        String searchFor = "select manufacturer,model,count(*) from physical_store natural join sells natural join phone where store_id = " + storeID + 
                " group by manufacturer,model";
        try 
        {
            ResultSet result = s.executeQuery(searchFor);
            if (!result.next())
                System.out.println("Inventory is empty!");
            else
            {
                System.out.println("Manufacturer: " + result.getString(1) + " - Model: " + result.getString(2) + " - Quantity: " + result.getInt(3));
                while (result.next())
                    System.out.println("Manufacturer: " + result.getString(1) + " - Model: " + result.getString(2) + " - Quantity: " + result.getInt(3));
            }
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(RetailStoreOptions.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Database error. Please try again later");
        }
    }
    
    public void restock()
    {
        try 
        {
            System.out.println("Enter the product's manufacturer");
            Scanner sc = new Scanner(System.in);
            String manufacturer = sc.nextLine();
            while (manufacturer.length() > 50)
            {
                System.out.println("Manufacturer name too large. Please enter a correct one");
                manufacturer = sc.nextLine();
            }
            
            System.out.println("Enter the product's model");
            String model = sc.nextLine();
            
            while (model.length() > 30)
            {
                System.out.println("Model name too large. Please enter a correct one");
                manufacturer = sc.nextLine();
            }
            

            int quantity;
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the desirable quantity");
            while (true)
            {
                while (!scanner.hasNextInt())
                {
                    System.out.println("Enter an appropriate quantity");
                    scanner.next();
                }
                quantity = scanner.nextInt();
                if (quantity > 0 && quantity < 50)
                    break;
                else
                    System.out.println("Please enter an acceptable quantity");
            }
            
            Long restock_id = 0L;
            
            String searchFor = "select restock_id from restock_order where restock_id >= all (select restock_id from restock_order)";
            ResultSet result = s.executeQuery(searchFor);
            
            if (result.next())
                restock_id = result.getLong(1);
            
            searchFor = "select meid from phone where meid >= all (select meid from phone)";
            result = s.executeQuery(searchFor);
            
            Long meid = 0L;
            if (result.next())
                meid = result.getLong(1);
                
           
            
            for (int counter = 0; counter < quantity; counter++ )
            {
                restock_id += 1L;
                meid += 1L;
                //Insert into phone
                String insertString = "insert into phone values (" + meid + ",'" + manufacturer + "','" + model + "')";
                s.executeUpdate(insertString);
                
                //Insert into restock
                insertString = "insert into restock_order values(" + restock_id + "," + meid + "," + storeID + ",CURRENT_TIMESTAMP)";
                s.executeUpdate(insertString);
            }
            
            System.out.println("Restock has been requested");
                
        } 
        catch (SQLException ex) {
            Logger.getLogger(RetailStoreOptions.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Database error. Please try again later");
        }
    }
    
    public void updateInventory()
    {
        try 
        {
            Scanner sc = new Scanner(System.in);
            System.out.println("These are the awaiting restock packages for this store:");
            String searchFor = "select restock_id,r_date,manufacturer,model from restock_order natural join phone where store_id = " + storeID;
            Long meid = 0L;
            
            
            ResultSet result = s.executeQuery(searchFor);
            
            if (!result.next())
                System.out.println("No restock packages are available.");
            else
            {
                System.out.println("ID: " + result.getString(1) + " - Manufacturer: " + result.getString(3) + " - Model: " + result.getString(4) + " - Order date: " + result.getString(2));
                while (result.next())
                   System.out.println("ID: " + result.getString(1) + " - Manufacturer: " + result.getString(3) + " - Model: " + result.getString(4) + " - Order date: " + result.getString(2));
            }
            int restock_id;
            while (true)
            {
                System.out.println("Enter the restock identification number of the desired request");
                while (!sc.hasNextInt())
                {
                    System.out.println("Enter a valid number");
                    sc.next();
                }
                restock_id = sc.nextInt();
                searchFor = "select meid from restock_order where restock_id = " + restock_id;
                result = s.executeQuery(searchFor);
                if (result.next())
                {
                    meid = result.getLong(1);
                    break;
                }
                else
                    System.out.println("Please enter an identification number from the provided list");
            }
            
            String insertString = "insert into sells values (" + storeID + "," + meid + ")";
            s.executeUpdate(insertString);
            
            String deleteString = "delete from restock_order where restock_id = " + restock_id;
            s.executeUpdate(deleteString);
            
            System.out.println("Order has been inserted into inventory");
        } 
        catch (SQLException ex) {
            System.out.println("Database error. Please try again later.");
            Logger.getLogger(RetailStoreOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sellPhone()
    {
        try
        {
            System.out.println("Please type customer name");
            String regex = "[0-9]+";
            Scanner sc = new Scanner(System.in);
            String cust_name;
            ResultSet result;
            String searchFor;


            while ((cust_name = sc.next()).matches(regex)) 
            {
                System.out.println("Unvalid name. A name consists of only letters");
            }
            searchFor = "select customer_address, account_id  from customer natural join has where name = '" + cust_name +"'";
            result = s.executeQuery(searchFor);
                
            if (!result.next())
            {
                System.out.println("There is no customer with such name. Please allow customer to start a service first");
            }
            
            else
            {
                
                System.out.println("Please enter the account identification number associated with the customer");

                System.out.format("%-14s  -  %-38s\n","ID","Address");
                System.out.format("%-14s  -  %-38s\n",result.getString(2),result.getString(1));
                
                while (result.next())
                    System.out.format("%-14s  -  %-38s\n",result.getString(2),result.getString(1));
                
                String account_id;
                String customerID;
                while (true)
                {
                    account_id = sc.next();
                    while (!account_id.matches(regex))
                    {
                        System.out.println("Please enter an identification number");
                        account_id = sc.next();
                    }
                    searchFor = "select cust_id from customer natural join has where name = '" + cust_name + "' and account_id = " +  account_id;
                    result = s.executeQuery(searchFor);
                    if (result.next())
                    {
                        customerID = result.getString(1);
                        break;
                    }
                    else
                        System.out.println("Please enter an identification number from the list");
                    
                }
                
                //Checks what type of account user has:
                searchFor = "select account_id from account natural join individual_account where account_id = " + account_id;
                result = s.executeQuery(searchFor);
                
                if (result.next())
                    System.out.println("Individual accounts only allow one phone");
                else
                {
                    searchFor = "select account_id,count(phone_num) from account natural join family_account natural join phone_number where account_id = " + account_id + " group by account_id";
                    result = s.executeQuery(searchFor);
                    
                    boolean doContinue = true;
                    if (result.next())
                    {
                        
                        if (result.getInt(2) >= 4 )
                        {
                            doContinue = false;
                            System.out.println("Family accounts have a maximum of 4 phones associated with them. The maximum has been reached");
                        }
                    }
                    
                    if (doContinue)
                    {
                        System.out.println("These are the phones available on inventory:");
                        checkInventory();
                        System.out.println();
                        String meid;

                        while (true)
                        {
                            Scanner manScan = new Scanner(System.in);
                            System.out.println("Please type the manufacturer of the phone the customer desires");
                            String manufacturer = manScan.nextLine();
                            
                            while (manufacturer.length() > 50)
                            {
                                System.out.println("Name too large. Please try again.");
                                manufacturer = manScan.nextLine();
                            }
                            

                            Scanner modScan = new Scanner(System.in);
                            System.out.println("Please type the model of the phone the customer desires");
                            String model = modScan.nextLine();
                            
                            while (model.length() > 30)
                            {
                               System.out.println("Name too large. Please try again."); 
                               model = modScan.nextLine();
                            }

                            searchFor = "select meid from phone natural join sells where store_id = " + storeID + " and model = '" + model + "' and manufacturer = '" + manufacturer + "'";
                            result = s.executeQuery(searchFor);

                            if (result.next())
                            {
                                meid = result.getString(1);
                                break;
                            }
                            else
                                System.out.println("Please enter an appropriate model and manufacturer combination");

                        }
                        
                        //Insert into sold
                        String insertString = "insert into sold values (" + storeID + "," + meid + ")";
                        s.executeUpdate(insertString);
                        
                        //Remove from sells
                        String removeString = "delete from sells where meid = " + meid;
                        s.executeUpdate(removeString);
                        
                        //Create phone number and insert it
                        Random rand = new Random();
                        
                        long drand;
                        
                        while (true)
                        {
                        
                            drand = (long)(rand.nextDouble()*10000000000L);
                            searchFor = "select phone_num from phone_number where phone_num =" + drand;
                            result = s.executeQuery(searchFor);
                            if (!result.next())
                                break;
                        }
                        
                        insertString = "insert into phone_number values (" + drand + "," + account_id + ",'Other')";
                        s.executeUpdate(insertString);
                        
                       
                        
                        //Insert into active_phone
                        insertString = "insert into active_phone values (" + meid + "," + drand + ")";
                        s.executeUpdate(insertString);
                        
                        //Insert into owns
                        insertString = "insert into owns values (" + customerID + "," + meid + ")";
                        s.executeUpdate(insertString);
                        
                        System.out.println("Phone selling process was a success.");
                        
                    }


                }
            }
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(RetailStoreOptions.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Database error. Please try again later");
        }
    }
    
    public void startService()
    {
        String regex = "[0-9]+";
        System.out.println("Please enter customer's name: ");
        
        try
        {
            Scanner sc = new Scanner(System.in);
            String name;
            while ((name = sc.next()).matches(regex))
            {
                System.out.println("Please enter a valid name");
            }


            System.out.println("Please enter customer's address");
            Scanner addressSc = new Scanner(System.in);
            String address = addressSc.nextLine();
            while (address.length() > 38)
            {
                System.out.println("Address length too large. Please enter another one:");
                addressSc.nextLine();
                
            }

            String searchFor = "select cust_id from customer where name ='" + name + "' and customer_address = '" + address + "'";
            ResultSet result = s.executeQuery(searchFor);
            Long cust_id = 0L;
            String insertString;

            if (result.next())
            {
                cust_id = result.getLong(1);

            }
            
            else
            {
                searchFor = "select cust_id from customer where cust_id >= all (select cust_id from customer)";
                result = s.executeQuery(searchFor);

                if (result.next())
                    cust_id = result.getLong(1) + 1;
                
                //Inserts customer into customer table
                insertString = "insert into customer values (" + cust_id + ",'" + name + "','" + address + "')";
                s.executeUpdate(insertString);
            }
            
            //Create new account:
            System.out.println("Allow user to choose account password. No spaces are allowed.");
            String password = sc.next();
            
            while (password.length() > 12)
            {
                System.out.println("Password too large!");
                password = sc.next();
            }
            
            
            searchFor = "select account_id from account where account_id >= all (select account_id from account)";
            result = s.executeQuery(searchFor);
            
            Long account_id = 1L;
            if (result.next())
                account_id = result.getLong(1) + 1;
            
            insertString = "insert into account values (" + account_id + ",'" + password + "')";
            s.executeUpdate(insertString);
            
            System.out.println("Enter account type:");
            System.out.println("The possible choices are: individual, family, business. Don't use capital letters please.");
            
            String type;
            while (!(type = sc.next()).equals("individual") && !type.equals("family") && !type.equals("usiness"))
                System.out.println("The possible choices are: individual, family, usiness. Please try again. Don't use capital letters please.");
            
            //Inserts into correct account type
            insertString = "insert into " + type + "_account values (" + account_id + ")";
            s.executeUpdate(insertString);
            
            insertString = "insert into has values (" + cust_id + "," + account_id + ")";
            s.executeUpdate(insertString);
            
            //Add a phone!
            System.out.println("These are the phones available on inventory:");
            checkInventory();
            System.out.println();
            
            String meid;

            while (true)
            {
                Scanner manScan = new Scanner(System.in);
                System.out.println("Please type the manufacturer of the phone the customer desires");
                String manufacturer = manScan.nextLine();

                Scanner modScan = new Scanner(System.in);
                System.out.println("Please type the model of the phone the customer desires");
                String model = modScan.nextLine();

                searchFor = "select meid from phone natural join sells where store_id = " + storeID + " and model = '" + model + "' and manufacturer = '" + manufacturer + "'";
                result = s.executeQuery(searchFor);

                if (result.next())
                {
                    meid = result.getString(1);
                    break;
                }
                else
                    System.out.println("Please enter an appropriate model and manufacturer combination");

            }
            
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
                String phones = "";
                while (result.next())
                {
                    if (result.getString(1).equals(phoneNum))
                    {
                        break;
                    }
                    phones = result.getString(1);
                }
                if (!phones.equals(phoneNum))
                    auth = false;
            }
            
            insertString = "insert into phone_number values (" + phoneNum + "," + account_id + ",'Primary')";
            s.executeUpdate(insertString);
            
            insertString = "insert into active_phone values (" + meid + "," + phoneNum + ")";
            s.executeUpdate(insertString);
            
            insertString = "insert into sold values (" + storeID + "," + meid + ")";
            s.executeUpdate(insertString);
            
            insertString = "insert into sells values (" + storeID + "," + meid + ")";
            s.executeUpdate(insertString);
            
            insertString = "insert into owns values (" + cust_id + "," + meid + ")";
            s.executeUpdate(insertString);
            
            System.out.println("Customer " + cust_id + " now has a new account." + type + "   account with identification number " + account_id + " created for customer. Primary phone number is: " + phoneNum);
            
                    
            
            
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(RetailStoreOptions.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Database error. Please try again later");
        }
        
    }
    
    public void endService()
    {
        try
        {
            String regex = "[0-9]+";
            System.out.println("Enter customer name:");
            Scanner nameSc = new Scanner(System.in);

            String name = "";
            while ((name = nameSc.nextLine()).matches(regex))
            {
                    System.out.println("Please enter a valid name");
            }

            String searchString = "select cust_id,customer_address from customer where name ='" + name + "'";
            ResultSet result = s.executeQuery(searchString);
            
            while (true)
            {
            
                if (!result.next())
                    System.out.println("No customer with such name.");
                else
                {
                    System.out.format("%-12s - %-38s\n","ID","Address");
                    System.out.format("%-12s - %-38s\n",result.getString(1),result.getString(2));
                    while (result.next())
                    {
                        System.out.format("%-12s - %-38s\n",result.getString(1),result.getString(2));
                    }
                    break;
                }
            
                
                    
            }
            Scanner sc = new Scanner(System.in);
            String id = "";
            while (true)
            {
                System.out.format("Please select an identification number from the list above.");
                id = sc.next();
                searchString = "select cust_id from customer where name ='" + name + "' and cust_id = " + id;
                result = s.executeQuery(searchString);
                
                if (result.next())
                    break;
                
            }
            
            //Selects which account for the customer: (might have two accounts)
            System.out.println("The customer has the following accounts:");
            searchString = "select account_id from has natural join customer where cust_id = " + id;
            result = s.executeQuery(searchString);
            
            while (result.next())
            {
                System.out.println(result.getString(1));
            }
            
            String account_id;
            while (true)
            {

                System.out.println("Please select an account identification number from the list above.");
                account_id = sc.next();
                
                searchString = "select account_id from account where account_id = " + account_id;
                result = s.executeQuery(searchString);
                
                if (result.next())
                    break;
                
                
            }
            
            //Insert into unactive_phone
            List <String> phoneList = new ArrayList<String>();
            List <String> meidList = new ArrayList<String>();
            
            searchString = "select phone_num,meid from active_phone natural join phone_number where account_id = " + account_id;
            s.executeQuery(searchString);
            while (result.next())
            {
                phoneList.add(result.getString(1));
                meidList.add(result.getString(2));
            }
            
            
            
            searchString = "select info_id from unactive_phone where info_id >= all (select info_id from unactive_phone)";
            result = s.executeQuery(searchString);
            Long info_id = 1L;
            
            if (result.next())
                info_id = result.getLong(1) + 1L;
            
            for (int counter = 0; counter < phoneList.size(); counter++)
            {
                String insertString = "insert into unactive_phone values (" + meidList.get(counter) + "," + info_id + "," + phoneList.get(counter) + ",CURRENT_TIMESTAMP)";
                s.executeUpdate(insertString);
            }
            
            //Delete acc_plan
            String deleteString = "delete from acc_plan where account_id = " + account_id;
            s.executeUpdate(deleteString);
            
            //Delete from phone_number
            deleteString = "delete from phone_number where account_id = " + account_id;
            s.executeUpdate(deleteString);
            
            //Delete active_phone
            deleteString = "delete from active_phone where phone_num is null";
            s.executeUpdate(deleteString);

            //Delete account
            deleteString = "delete from has where account_id = " + account_id;
            s.executeUpdate(deleteString);
            
            //Delete account
            deleteString = "delete from account where account_id = " + account_id;
            s.executeUpdate(deleteString);
            
            System.out.println("Account was deleted succesfuly.");
            
            
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(RetailStoreOptions.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Database error. Please try again later.");
        }
        
        
        
    }
    
    
}
