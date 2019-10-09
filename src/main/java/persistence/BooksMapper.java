/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.Books;
import logic.Order;

/**
 * NEED DESCRIPTION
 * @author Michael N. Korsgaard
 */
public class BooksMapper implements Mapper {

    @Override
    public ArrayList<HashMap<String, String>> getBooks() {
        ArrayList<HashMap<String, String>> books = new ArrayList();

        String sql = "SELECT * FROM bogbutik.bog";

        try {
            ResultSet rs = DB.getConnection().prepareStatement(sql).executeQuery();
            while (rs.next()) {
                HashMap<String, String> map = new HashMap();
                map.put("id", rs.getString("id"));
                map.put("title", rs.getString("title"));
                map.put("author", rs.getString("author"));
                map.put("price", rs.getString("price"));
                map.put("qty", rs.getString("qty"));
                books.add(map);
            }

        } catch (SQLException ex) {
            Logger.getLogger(BooksMapper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return books;
    }

    @Override
    public void ReduceBookQuantityInDB(int bookID, int newAmount) {
        String sql = "UPDATE bog SET qty=" + newAmount + " WHERE id=\"" + bookID + "\"";
        try {
            DB.getConnection().prepareStatement(sql).executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(BooksMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addOrderToDB(Order order) {
        
        // Check if customer already exist
        String sqlCheckKunde = "SELECT * FROM bogbutik.kunde WHERE email='" + order.getCustomerEmail().toLowerCase() + "'";
        boolean kundeFindes = false;
        try {
            ResultSet rs = DB.getConnection().prepareStatement(sqlCheckKunde).executeQuery();
            while (rs.next()) {
                if (order.getCustomerEmail().toLowerCase().equals(rs.getString("email").toLowerCase())) {
                    kundeFindes = true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(BooksMapper.class.getName()).log(Level.SEVERE, null, ex);
        }

        // If the customer is not in the database, we add them
        if (!kundeFindes) {
            String sqlInsertKunde = "INSERT INTO bogbutik.kunde VALUES ('" + order.getCustomerEmail().toLowerCase()
                    + "', '" + order.getCustomerName()
                    + "', '" + order.getCustomerPhone()+ "')";
            try {
                DB.getConnection().prepareStatement(sqlInsertKunde).executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(BooksMapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Add Order to the DB
        for (HashMap<String, Object> map : order.getBooksInOrder()) {
            Books bog = (Books) map.get("book");
            String sqlInsertOrdre = "INSERT INTO bogbutik.ordre (`ordreNr`, `kundeEmail`, `bogID`, `bogAntalBesilt`) VALUES ("
                    + "'" + order.getOrderNr() + "', "
                    + "'" + order.getCustomerEmail().toLowerCase() + "', "
                    + "'" + bog.getBookID() + "', "
                    + "'" + (int) map.get("amount") + "')";
            try {
                DB.getConnection().prepareStatement(sqlInsertOrdre).executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(BooksMapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public ArrayList<HashMap<String, String>> getOrders() {
        ArrayList<HashMap<String, String>> orders = new ArrayList();

        String sql = "Select ordre.ordreNr, ordre.kundeEmail, kunde.navn, kunde.telefonnummer, ordre.bogID, ordre.bogAntalBesilt from bogbutik.ordre \n"
                + "left join kunde on ordre.kundeEmail=kunde.email";

        try {
            ResultSet rs = DB.getConnection().prepareStatement(sql).executeQuery();
            while (rs.next()) {
                HashMap<String, String> map = new HashMap();
                map.put("orderNr", rs.getString("ordreNr"));
                map.put("customerEmail", rs.getString("kundeEmail"));
                map.put("customerName", rs.getString("navn"));
                map.put("customerPhone", rs.getString("telefonnummer"));
                map.put("bookID", rs.getString("bogID"));
                map.put("bookAmountOrdered", rs.getString("bogAntalBesilt"));
                orders.add(map);
            }

        } catch (SQLException ex) {
            Logger.getLogger(BooksMapper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return orders;
    }
}
