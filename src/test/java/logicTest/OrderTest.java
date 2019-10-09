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
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import persistence.Mapper;

/**
 *
 * @author Michael N. Korsgaard
 */
public class OrderTest {

    private ArrayList<String[]> fakeListOfOrders;
    private Mapper bookMapper;

    @Before
    public void setup() {
        Order.emptyListOfOrders();
        FakeMapper fakeMapper = new FakeMapper();

        // Fake Books for the Database that the orders can match with
        fakeMapper.insertBook(new String[]{"1", "Batman", "DC comics", "199", "730"});
        fakeMapper.insertBook(new String[]{"2", "Superman", "Marvel", "299", "75"});
        fakeMapper.insertBook(new String[]{"3", "IT-guy", "Cahit B.", "99", "13"});
        fakeMapper.insertBook(new String[]{"4", "Programming 101", "Marcus J.", "0", "1"});
        fakeMapper.insertBook(new String[]{"5", "Programming for Dummies", "Michael K.", "1", "200"});

        // Fake Orders for the Database
        fakeListOfOrders = new ArrayList();
        String[] subOrder1 = new String[]{"103", "Mr@Email.com", "Mr. Jack", "+45", "2", "2"};
        fakeMapper.insertOrder(subOrder1);
        fakeListOfOrders.add(subOrder1);
        String[] subOrder2 = new String[]{"103", "Mr@Email.com", "Mr. Jack", "+45", "3", "1"};
        fakeMapper.insertOrder(subOrder2);
        fakeListOfOrders.add(subOrder2);
        String[] subOrder3 = new String[]{"104", "Miss@Email.com", "Miss Maria", "+46", "1", "3"};
        fakeMapper.insertOrder(subOrder3);
        fakeListOfOrders.add(subOrder3);
        String[] subOrder4 = new String[]{"104", "Miss@Email.com", "Miss Maria", "+46", "3", "10"};
        fakeMapper.insertOrder(subOrder4);
        fakeListOfOrders.add(subOrder4);
        String[] subOrder5 = new String[]{"104", "Miss@Email.com", "Miss Maria", "+46", "5", "5"};
        fakeMapper.insertOrder(subOrder5);
        fakeListOfOrders.add(subOrder5);
        String[] subOrder6 = new String[]{"105", "Mr@Email.com", "Mr. Jack", "+45", "2", "2"};
        fakeMapper.insertOrder(subOrder6);
        fakeListOfOrders.add(subOrder6);
        String[] subOrder7 = new String[]{"106", "The@Email.com", "The Rock", "+47", "5", "7"};
        fakeMapper.insertOrder(subOrder7);
        fakeListOfOrders.add(subOrder7);
        String[] subOrder8 = new String[]{"106", "The@Email.com", "The Rock", "+47", "4", "8"};
        fakeMapper.insertOrder(subOrder8);
        fakeListOfOrders.add(subOrder8);

        // Final Setup
        bookMapper = fakeMapper;
        Order.setBooksMapper(bookMapper);

        // Setup Books to Match
        Books.setBooksMapper(bookMapper);
        Books.createBookListFromDB();
    }

    @Test
    public void testCreateOrderObject() {
        //act
        Order result = new Order();

        //assert
        String expectedCustomerName = "No Name";
        String expectedCustomerEmail = "No Email";
        String expectedCustomerPhone = "No Phonenumber";
        int expectedOrderNr = 101;  //this is the lowest orderNr given
        int expectedBooksInOrderSize = 0;
        assertTrue(result.getCustomerEmail().equals(expectedCustomerEmail));
        assertTrue(result.getCustomerName().equals(expectedCustomerName));
        assertTrue(result.getCustomerPhone().equals(expectedCustomerPhone));
        assertEquals(expectedOrderNr, result.getOrderNr());
        assertEquals(expectedBooksInOrderSize, result.getBooksInOrder().size());
    }

    @Test
    public void testCreateTwoOrderObjects() {
        //act
        Order result1 = new Order();
        Order result2 = new Order();

        //assert
        String expectedCustomerName = "No Name";
        String expectedCustomerEmail = "No Email";
        String expectedCustomerPhone = "No Phonenumber";
        int expectedOrderNr1 = 101;  //this is the lowest orderNr given
        int expectedOrderNr2 = 102;
        int expectedBooksInOrderSize = 0;
        assertTrue(result1.getCustomerEmail().equals(expectedCustomerEmail));
        assertTrue(result1.getCustomerName().equals(expectedCustomerName));
        assertTrue(result1.getCustomerPhone().equals(expectedCustomerPhone));
        assertEquals(expectedOrderNr1, result1.getOrderNr());
        assertEquals(expectedBooksInOrderSize, result1.getBooksInOrder().size());
        assertTrue(result2.getCustomerEmail().equals(expectedCustomerEmail));
        assertTrue(result2.getCustomerName().equals(expectedCustomerName));
        assertTrue(result2.getCustomerPhone().equals(expectedCustomerPhone));
        assertEquals(expectedOrderNr2, result2.getOrderNr());
        assertEquals(expectedBooksInOrderSize, result2.getBooksInOrder().size());
    }

    @Test //Requires testCreateOrderObject() to pass
    public void testAddBooksToOrder() {
        //arrange
        Order order = new Order();
        ArrayList<Books> selectedBooks = new ArrayList();
        int bookID = 3;
        int bookAmount = 4;
        selectedBooks.add(Books.getBookFromID(bookID));
        HashMap<Integer, Integer> bookOrdersAmount = new HashMap();
        bookOrdersAmount.put(bookID, bookAmount);

        //act
        order.addBooksToOrder(selectedBooks, bookOrdersAmount);
        ArrayList<HashMap<String, Object>> result = order.getBooksInOrder();

        //assert
        int expectedBooksInOrderSize = 1;
        assertEquals(expectedBooksInOrderSize, result.size());
        assertTrue(selectedBooks.get(0).equals(result.get(0).get("book")));
        assertEquals(bookAmount, result.get(0).get("amount"));

    }

    @Test
    public void testSetCustomer() {
        //arrange
        String customerName = "Tester";
        String customerEmail = "Test@Email.com";
        String customerPhone = "+45 ## ## ## ##";
        Order order = new Order();

        //act
        order.setCustomer(customerName, customerEmail, customerPhone);

        //assert
        assertTrue(order.getCustomerEmail().equals(customerEmail));
        assertTrue(order.getCustomerName().equals(customerName));
        assertTrue(order.getCustomerPhone().equals(customerPhone));
    }

    @Test
    public void testGetOrderFromOrderNr() {
        //arrange
        Order order = new Order();
        int orderNr = order.getOrderNr();
        
        //act
        Order result = Order.getOrderFromOrderNr(orderNr);
        
        //assert
        assertEquals(order, result);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOrderFromOrderNrOrderNotFound() {
        //arrange
        Order order = new Order();
        int orderNr = 15;
        
        //act
        Order result = Order.getOrderFromOrderNr(orderNr);

    }

    @Test
    public void testCreateOrdersFromDB() {
        //arrange

        int expectedSize = 0;
        ArrayList<Order> result = Order.getListOfOrders();
        assertEquals(expectedSize, result.size());

        //act
        Order.createOrdersFromDB();

        //assert
        expectedSize = 4;
        result = Order.getListOfOrders();
        assertEquals(expectedSize, result.size());
        for (Order order : result) {
            boolean orderFound = false;
            for (String[] orderFromDB : fakeListOfOrders) {
                if (order.getOrderNr() == Integer.parseInt(orderFromDB[0])) {
                    orderFound = true;
                    assertTrue(orderFromDB[1].equals(order.getCustomerEmail()));
                    assertTrue(orderFromDB[2].equals(order.getCustomerName()));
                    assertTrue(orderFromDB[3].equals(order.getCustomerPhone()));
                    boolean bookInOrderFound = false;
                    for (HashMap<String, Object> map : order.getBooksInOrder()) {
                        Books book = (Books) map.get("book");
                        if (orderFromDB[4].equals(Integer.toString(book.getBookID()))) {
                            assertEquals(Integer.parseInt(orderFromDB[5]), map.get("amount"));
                            bookInOrderFound = true;
                        }
                    }
                    assertTrue(bookInOrderFound);
                }
            }
            assertTrue(orderFound);
        }
    }

    @Test //Requires testAddBooksToOrder() and testCreateOrderObject() to pass
    public void testUploadOrderToDB() {
        //arrange
        String customerName = "Tester";
        String customerEmail = "Test@Email.com";
        String customerPhone = "+45 ## ## ## ##";
        Order order = new Order();
        order.setCustomer(customerName, customerEmail, customerPhone);
        ArrayList<Books> selectedBooks = new ArrayList();
        selectedBooks.add(Books.getBookFromID(3));
        selectedBooks.add(Books.getBookFromID(4));
        HashMap<Integer, Integer> bookOrdersAmount = new HashMap();
        bookOrdersAmount.put(3, 2);
        bookOrdersAmount.put(4, 5);
        order.addBooksToOrder(selectedBooks, bookOrdersAmount);
        int expectedSize = 8;
        assertEquals(expectedSize, bookMapper.getOrders().size());

        //act
        order.uploadOrderToDB();

        //assert
        expectedSize = 10;
        assertEquals(expectedSize, bookMapper.getOrders().size());
    }

}
