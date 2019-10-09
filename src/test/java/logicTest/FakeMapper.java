/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logicTest;

import java.util.ArrayList;
import java.util.HashMap;
import logic.Books;
import logic.Order;
import persistence.Mapper;

/**
 * NEED DESCRIPTION
 *
 * @author Michael N. Korsgaard
 */
public class FakeMapper implements Mapper {

    private ArrayList<HashMap<String, String>> bookList;
    private ArrayList<HashMap<String, String>> listOfOrders;

    public FakeMapper() {
        this.bookList = new ArrayList();
        this.listOfOrders = new ArrayList();
    }

    // Methods for setup
    public void insertBook(String[] book) {
        HashMap<String, String> map = new HashMap();
        map.put("id", book[0]);
        map.put("title", book[1]);
        map.put("author", book[2]);
        map.put("price", book[3]);
        map.put("qty", book[4]);
        bookList.add(map);
    }

    public void insertOrder(String[] order) {
        HashMap<String, String> map = new HashMap();
        map.put("orderNr", order[0]);
        map.put("customerEmail", order[1]);
        map.put("customerName", order[2]);
        map.put("customerPhone", order[3]);
        map.put("bookID", order[4]);
        map.put("bookAmountOrdered", order[5]);
        listOfOrders.add(map);
    }

    public void insertBooktoBookList(HashMap<String, String> map) {
        bookList.add(map);
    }

    public void insertOrderToOrderList(HashMap<String, String> map) {
        listOfOrders.add(map);
    }

    public void setBookList(ArrayList<HashMap<String, String>> bogList) {
        this.bookList = bogList;
    }

    public void setListOfOrders(ArrayList<HashMap<String, String>> listOfOrders) {
        this.listOfOrders = listOfOrders;
    }

    @Override
    public ArrayList<HashMap<String, String>> getBooks() {
        return bookList;
    }

    @Override
    public void ReduceBookQuantityInDB(int bogID, int newAmount) {
        for (HashMap<String, String> map : bookList) {
            if (Integer.parseInt(map.get("id")) == bogID) {
                map.put("qty", Integer.toString(newAmount));
            }
        }
    }

    @Override
    public void addOrderToDB(Order order) {
        for (HashMap<String, Object> hashMap : order.getBooksInOrder()) {
            HashMap<String, String> map = new HashMap();
            int amount = (int) hashMap.get("amount");
            Books book = (Books) hashMap.get("book");
            map.put("orderNr", Integer.toString(order.getOrderNr()));
            map.put("customerEmail", order.getCustomerEmail());
            map.put("customerName", order.getCustomerName());
            map.put("customerPhone", order.getCustomerPhone());
            map.put("bookID", Integer.toString(book.getBookID()));
            map.put("bookAmountOrdered", Integer.toString(amount));
            listOfOrders.add(map);
        }
    }

    @Override
    public ArrayList<HashMap<String, String>> getOrders() {
        return listOfOrders;
    }

}
