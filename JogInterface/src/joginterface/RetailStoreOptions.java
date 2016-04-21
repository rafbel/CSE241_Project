/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package joginterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Rafael
 */
public class RetailStoreOptions 
{
    Statement s = null;
    Connection con = null;
    DBCon connection;
    
    public RetailStoreOptions() throws ClassNotFoundException
    {
        connection = new DBCon(s,con);
    }
    
    public void close() throws SQLException
    {
        connection.close(s,con);
    }
    
    
    
}
