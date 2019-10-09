/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistenceTest;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.Test;
import static org.junit.Assert.*;
import persistence.DB;

/**
 *
 * @author Michael N. Korsgaard
 */
public class DBTest {
    
    @Test
    public void testConnection() throws SQLException{
        //arrange
        Connection result;
        
        //act
        result = DB.getConnection();
        
        //assert
        assertNotNull(result);
        assertTrue(!result.isClosed());
    }
    
}
