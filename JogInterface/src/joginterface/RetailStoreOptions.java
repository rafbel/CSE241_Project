/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package joginterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
            String manufacturer = sc.next();
            
            System.out.println("Enter the product's model");
            String model = sc.next();
            
            System.out.println("Enter the quantity desired");
            int quantity;
            while (true)
            {
                while (sc.hasNextInt())
                {
                    System.out.println("Please enter an appropriate quantity");
                    sc.next();
                }
                quantity = sc.nextInt();
                if (quantity > 0)
                    break;
            }
            
            Long restock_id = 0L;
            
            String searchFor = "select restock_id from restock where restock_id >= all (select restock_id from restock)";
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
                insertString = "insert into restock_order values(" + restock_id + "," + meid + ",CURRENT_TIMESTAMP)";
                s.executeUpdate(insertString);
            }
                
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
                while (sc.hasNextInt())
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
            searchFor = "select customer_address, account_id  from customer natural join has where name = " + cust_name;
            result = s.executeQuery(searchFor);
                
            if (!result.next())
            {
                System.out.println("There is no customer with such name. Please allow customer to start a service first");
            }
            
            else
            {
                
                System.out.println("Please enter the account identification number associated with the customer");

                System.out.format("14%s  -  38%s","ID","Address");
                System.out.format("14%s  -  38%s",result.getString(2),result.getString(1));
                
                while (result.next())
                    System.out.format("14%s  -  38%s",result.getString(2),result.getString(1));
                
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
                    searchFor = "select cust_id from customer natural join has where name = " + cust_name + " and account_id = " +  account_id;
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
                        String meid;
                        while (true)
                        {
                            System.out.println("Please type the manufacturer of the phone the customer desires");
                            String manufacturer = sc.next();

                            System.out.println("Please type the model of the phone the customer desires");
                            String model = sc.next();

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
            String address = sc.next();

            String searchFor = "select cust_id from customer where name =" + name + " and customer_address = " + address;
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
            System.out.println("Allow user to choose account password");
            String password = sc.next();
            
            searchFor = "select account_id from account where account_id >= all (select account_id from account)";
            result = s.executeQuery(searchFor);
            
            Long account_id = 1L;
            if (result.next())
                account_id = result.getLong(1) + 1;
            
            insertString = "insert into account values (" + account_id + ",'" + password + "')";
            s.executeUpdate(insertString);
            
            System.out.println("Enter account type:");
            System.out.println("The possible choices are: individual, bamily, business. Don't use capital letters please.");
            
            String type;
            while (!(type = sc.next()).equals("individual") && !type.equals("family") && !type.equals("usiness"))
                System.out.println("The possible choices are: individual, family, usiness. Please try again. Don't use capital letters please.");
            
            //Inserts into correct account type
            insertString = "insert into " + type + "_account values (" + account_id + ")";
            s.executeUpdate(insertString);
            
            insertString = "insert into has values (" + cust_id + "," + account_id + ")";
            s.executeUpdate(insertString);
            
            //Add a phone!
            
                    
            
            
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(RetailStoreOptions.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Database error. Please try again later");
        }
        
    }
    
    public void endService()
    {
        
    }
    
    
}
