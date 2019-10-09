/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import java.util.ArrayList;
import java.util.HashMap;
import persistence.Mapper;

/**
 * Before using this class, it's booksMapper should be set using the static method setBooksMapper(Mapper bogMap)
 *
 * @author Michael N. Korsgaard
 */
public class Order {

    private final int orderNr;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private final ArrayList<HashMap<String, Object>> booksInOrder;
    private static ArrayList<Order> listOfOrders = new ArrayList();
    private static Mapper booksMapper;

    private Order(int orderNr, String customerName, String customerEmail, String customerPhone) {
        this.orderNr = orderNr;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.booksInOrder = new ArrayList();
        listOfOrders.add(this);
    }

//    public Order(String customerName, String customerEmail, String customerPhone) {
//        this.orderNr = Order.findHighestOrderNumber();
//        this.customerName = customerName;
//        this.customerEmail = customerEmail;
//        this.customerPhone = customerPhone;
//        this.booksInOrder = new ArrayList();
//        listOfOrders.add(this);
//    }

    public Order() {
        this.orderNr = Order.findNewHighestOrderNumber();
        this.customerName = "No Name";
        this.customerEmail = "No Email";
        this.customerPhone = "No Phonenumber";
        this.booksInOrder = new ArrayList();
        listOfOrders.add(this);
    }

    /**
     * Add a book to the order with an associated amount, describing how many of the book has been ordered. Stored in an ArrayList of HashMaps where the: Books book is stored under key "book" int amount is stored under key "amount"
     *
     * @param book Books object of the book in the order.
     * @param amount int of how many of the book there is on the order.
     */
    private void addBookToOrder(Books book, int amount) {
        HashMap<String, Object> bookAmountMap = new HashMap();
        bookAmountMap.put("book", book);
        bookAmountMap.put("amount", amount);
        booksInOrder.add(bookAmountMap);
    }

    /**
     * This method calls the addBookToOrder() method on multiple books from an ArrayList of books.
     *
     * @param selectedBooks ArrayList of the Books object that should be added to the Order
     * @param selectedAmounts HashMap containing Amounts for each BookID, stored as Integers. Key for each value is BookID
     */
    public void addBooksToOrder(ArrayList<Books> selectedBooks, HashMap<Integer, Integer> selectedAmounts) {
        for (Books book : selectedBooks) {
            int amount = selectedAmounts.get(book.getBookID());
            addBookToOrder(book, amount);
        }
    }

    /**
     * Store this Order thought the booksMapper to the Database
     *
     */
    public void uploadOrderToDB() {
        booksMapper.addOrderToDB(this);
    }

    /**
     * Look thought all Order objects, and find the old highest orderNr, and return one that is 1 greater.
     * Default return is 101.
     *
     */
    private static int findNewHighestOrderNumber() {
        int result = 101;
        for (Order order : listOfOrders) {
            if(result <= order.orderNr){
                result = order.orderNr+1;
            }
        }
        return result;
    }

    /**
     * Calls the BooksMapper to get data from the SQL Database to make all the Order objects stored in the DB.
     *
     */
    public static void createOrdersFromDB() {
        emptyListOfOrders();
        for (HashMap<String, String> mapOfOrders : booksMapper.getOrders()) {
            Order order = null;
            boolean orderExist = false;
            int orderNr = Integer.parseInt(mapOfOrders.get("orderNr"));
            //Check if there already is an order with the OrdreNr
            for (Order orderCheck : listOfOrders) {
                if (orderCheck.orderNr == orderNr) {
                    orderExist = true;
                    order = orderCheck;
                    break;
                }
            }
            if (!orderExist) {
                String customerName = mapOfOrders.get("customerName");
                String customerEmail = mapOfOrders.get("customerEmail");
                String customerPhone = mapOfOrders.get("customerPhone");
                order = new Order(orderNr, customerName, customerEmail, customerPhone);
            }
            int amount = Integer.parseInt(mapOfOrders.get("bookAmountOrdered"));
            int bookID = Integer.parseInt(mapOfOrders.get("bookID"));
            order.addBookToOrder(Books.getBookFromID(bookID), amount);
        }
    }

    /**
     * Find an Order object in the static ArrayList listOfOrders, matching on a searched orderNr as an int.
     *
     * @param orderNr int to match the orderNr of an Order in the listOfOrders
     * @return Order with OrderNr matching the searched orderNr
     * @throws IllegalArgumentException
     */
    public static Order getOrderFromOrderNr(int orderNr) throws IllegalArgumentException {
        for (Order order : listOfOrders) {
            if (order.orderNr == orderNr) {
                return order;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Set customer info for the order
     *
     * @param customerName
     * @param customerEmail
     * @param customerPhone
     */
    public void setCustomer(String customerName, String customerEmail, String customerPhone) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
    }

    /**
     * Empty the static ArrayList of Order objects in the Order class. Should be done before pulling down orders from the Database
     *
     */
    public static void emptyListOfOrders() {
        listOfOrders.clear();
    }

    /**
     * Set the BooksMapper that communicate with the Database. This method should be called before the Order class is taken into use.
     *
     */
    public static void setBooksMapper(Mapper booksMap) {
        booksMapper = booksMap;
    }

    public int getOrderNr() {
        return orderNr;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public ArrayList<HashMap<String, Object>> getBooksInOrder() {
        return booksInOrder;
    }

    public static ArrayList<Order> getListOfOrders() {
        return listOfOrders;
    }

}
