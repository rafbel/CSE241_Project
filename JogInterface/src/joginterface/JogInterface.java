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
public class JogInterface {

    /**
     * @param args the command line arguments
     */
    
    
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
                    while (!doQuit)
                    {
                try {
                        int option = 0;
                        do
                        {
                            System.out.println("Please choose one of the following available options:");
                            System.out.println("1 - Change billing plan");
                            System.out.println("2 - Request new phone");
                            System.out.println("3 - Terminate Service");
                            System.out.println("4 - Deactivate phone");
                            System.out.println("5 - Change account type");
                            System.out.println("6 - Return to main menu");

                            while (!sc.hasNextInt())
                            {
                                System.out.println("Please choose one of the available options");
                                sc.next();
                            }
                            option = sc.nextInt();
                        }while (option < 1 || option > 6);

                        CustomerOptions customerMenu = new CustomerOptions();
                        
                        switch(option)
                        {
                            case 1:
                                  customerMenu.changePlan();
                                break;

                            case 2:
                                customerMenu.requestNewPhone();
                                break;
                                
                            case 3:
                                customerMenu.terminateService();
                                break;
                                
                                
                            case 6:
                                System.out.println("Returning to main menu");
                                customerMenu.closeConnection();
                                doQuit = true;
                                break;

                            default:
                                break;
                    }
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
                    
                    break;
                
                case "System":
                case "system":
                    int option = 0;
                    while (true)
                    {
                        System.out.println("Please choose one of the following options");
                        System.out.println("1 - Read usage file");
                        System.out.println("2 - Show bills for customer");
                        System.out.println("3 - Return to main menu");
                        
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
                        SystemOptions system = new SystemOptions();
                        switch (option)
                        {
                            case 1:

                                break;

                            case 2:
                                system.printBill();
                                break;

                            case 3:
                                System.out.println("Returning to main menu");
                                system.close();
                                break;
                            default:
                                break;
                        }
                    } 
                    catch (ClassNotFoundException | SQLException ex) 
                    {
                        Logger.getLogger(JogInterface.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Database error. Please try again later");
                
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
