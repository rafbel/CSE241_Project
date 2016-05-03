/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package joginterface;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafael
 */
public class JogInterface 
{

    
    public static void printMainMenu()
    {
        System.out.println("Main Menu:");
        System.out.println("Type the option you want.");
        System.out.println("Customer - Options for Jog customers");
        System.out.println("Retail - Brings up the menu for retail store managers");
        System.out.println("System - Brings up the menu for the Jog system essential operations");
        System.out.println("Quit - Exits the program.");
    }
    
    
    public static void main(String[] args) 
    {
       System.out.println("Welcome to the Jog Interface System.");
       String input = "No input yet";
       Scanner sc = new Scanner(System.in);
       boolean doContinue = true;
       
       while (doContinue)
       {
            printMainMenu();
            input = sc.next();

            
            switch(input)
            {
                case "Customer": //Customer Options Interface
                case "customer":
                    boolean doQuit = false;
                    boolean hasConnected = false;
                    CustomerOptions customerMenu = null;
                    while (!doQuit)
                    {
                      try {
                        int option = 0;
                        do
                        {
                            System.out.println("Please choose one of the following available options:");
                            System.out.println("1 - Change billing plan");
                            System.out.println("2 - Request new phone");
                            System.out.println("3 - Return to main menu");
 

                            while (!sc.hasNextInt())
                            {
                                System.out.println("Please choose one of the available options");
                                sc.next();
                            }
                            option = sc.nextInt();
                        }while (option < 1 || option > 6);
                        
                        
                        
                       
                        if (option == 1)
                        {
                            if (!hasConnected)
                            {
                                customerMenu = new CustomerOptions();
                                hasConnected = true;
                            }
                        
                            customerMenu.changePlan();
                        }
                               

                        else if (option == 2)
                        {
                            if (!hasConnected)
                            {
                                customerMenu = new CustomerOptions();
                                hasConnected = true;
                            }
                            customerMenu.requestNewPhone();
                        }
                            
                                
                                
                                
                        else if (option == 3)
                        {
                            System.out.println("Returning to main menu");
                            if (hasConnected)
                                customerMenu.closeConnection();
                            doQuit = true;
                        }

                        else
                        System.out.println("Not an available option");
                    
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(JogInterface.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Database error. Please try again later");
                } catch (SQLException ex) {
                    Logger.getLogger(JogInterface.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Database error. Please try again later");
                }
                    }
                    
                    break;
                
                case "Retail": //RetailStoreOptions Interface
                case "retail":
                    
                    int choice = 0;
                    boolean alreadyConnected = false;
                    RetailStoreOptions retail = null;
                    boolean keepLoop = true;
                    while (keepLoop)
                    {
                        System.out.println("Please choose one of the following options");
                        System.out.println("1 - Check inventory");
                        System.out.println("2 - Request restock");
                        System.out.println("3 - Update inventory");
                        System.out.println("4 - Sell phone");
                        System.out.println("5 - Start service");
                        System.out.println("6 - Terminate service");
                        System.out.println("7 - Return to main menu");
                        
                        while (true)
                        {
                            while (!sc.hasNextInt())
                            {
                                System.out.println("Please enter a valid number");
                                sc.next();
                            }
                            choice = sc.nextInt();

                            if (choice >= 1 && choice <= 7)
                                break;
                            else
                                System.out.println("Please enter a valid option");
                        }
                    
                        try
                        {



                            if (choice == 1)
                            {

                                if (!alreadyConnected)
                                {
                                    retail = new RetailStoreOptions();
                                    alreadyConnected = true;
                                }
                                retail.checkInventory();
                            }


                            else if (choice == 2)
                            {
                                if (!alreadyConnected)
                                {
                                    retail = new RetailStoreOptions();
                                    alreadyConnected = true;
                                }
                                retail.restock();
                            }


                            else if (choice == 3)
                            {
                                if (!alreadyConnected)
                                {
                                        retail = new RetailStoreOptions();
                                        alreadyConnected = true;
                                }
                                retail.updateInventory();
                            }


                            else if (choice == 4)
                            {
                                if (!alreadyConnected)
                                {
                                        retail = new RetailStoreOptions();
                                        alreadyConnected = true;
                                }    
                                retail.sellPhone();
                            }



                            else if (choice == 5)
                            {
                                    if (!alreadyConnected)
                                    {
                                        retail = new RetailStoreOptions();
                                        alreadyConnected = true;
                                    }   
                                    retail.startService();
                            }



                            else if (choice == 6)
                            {
                                    if (!alreadyConnected)
                                    {
                                        retail = new RetailStoreOptions();
                                        alreadyConnected = true;
                                    }
                                    retail.endService();
                            }



                            else 
                            {
                                    System.out.println("Returning to main menu");
                                    if (alreadyConnected)
                                        retail.close();
                                    keepLoop = false;
                            }




                        } 
                        catch (ClassNotFoundException | SQLException ex) 
                        {
                            Logger.getLogger(JogInterface.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Database error. Please try again later");

                        }
                    
                    }
                    break;
                
                case "System":
                case "system":
                    int option = 0;
                    boolean connectionEst = false;
                    SystemOptions system = null;
                    boolean stayLoop = true;
                    while (stayLoop)
                    {
                        System.out.println("Please choose one of the following options");
                        System.out.println("1 - Read usage file");
                        System.out.println("2 - Print bill");
                        System.out.println("3 - Return to main menu");
                        
                        while (true)
                        {
                            while (!sc.hasNextInt())
                            {
                                System.out.println("Please enter a valid number");
                                sc.next();
                            }
                            option = sc.nextInt();

                            if (option >= 1 || option <= 3)
                                break;
                            else
                                System.out.println("Please enter a valid option");
                        }
                    
                        try
                        {

                            if (option == 1)
                            {
                                if (!connectionEst)
                                {
                                    connectionEst = true;
                                    system = new SystemOptions();
                                }
                                system.readUsage();
                            }


                            else if (option == 2)
                            {

                                if (!connectionEst)
                                {
                                    connectionEst = true;
                                    system = new SystemOptions();
                                }
                                system.printBill();
                            }


                            else if (option == 3)
                            {
                                    System.out.println("Returning to main menu");
                                    if (connectionEst)
                                        system.close();
                                    stayLoop = false;
                            }

                        }

                        catch (ClassNotFoundException | SQLException ex) 
                        {
                            Logger.getLogger(JogInterface.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Database error. Please try again later");

                        }
                    
                    
                    }
                    break;
                
                case "Quit":
                case "quit":
                    System.out.println("Thank you for using the Jog Interface System.");
                    doContinue = false;
                    break;
                    
                default:
                    
                    break;
            }
            
       }
       
       sc.close();
    }
       
    
}
