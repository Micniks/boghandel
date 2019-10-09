/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.util.ArrayList;
import java.util.HashMap;
import logic.Order;

/**
 * NEED DESCRIPTION
 * @author Michael N. Korsgaard
 */
public interface Mapper {

    /**
     * NEED DESCRIPTION
     *
     * @param bogID
     * @param newAmount
     */
    public void ReduceBookQuantityInDB(int bogID, int newAmount);

    /**
     * NEED DESCRIPTION
     *
     * @param order
     */
    public void addOrderToDB(Order order);

    /**
     * NEED DESCRIPTION
     *
     * @return 
     */
    public ArrayList<HashMap<String, String>> getBooks();

    /**
     * NEED DESCRIPTION
     *
     * @return 
     */
    public ArrayList<HashMap<String, String>> getOrders();

}
