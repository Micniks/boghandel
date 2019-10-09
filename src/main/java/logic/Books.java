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
public class Books {

    // TODO: Uniform variable and methods for ArrayList<Books> booksList to refer to either (booksList || bookList)
    private final int bookID;
    private final String title;
    private final String author;
    private final int price;
    private int qty;
    private static ArrayList<Books> booksList = new ArrayList<Books>();
    private static Mapper booksMapper;

    public Books(int bookID, String title, String author, int price, int qty) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.price = price;
        this.qty = qty;
        booksList.add(this);
    }

    /**
     * Return all the books in the ArrayList bookList with matching BookID to the chocies, and check if they have there are enough of them to match the amount asked for.
     *
     * @param choices String Array of BookIDs matching the books wanted to be selected.
     * @param selectedAmounts HashMap containing Amounts for each BookID, stored as a String. Key for each value is BookID. Is used to make sure the Book Qty is greater
     * then or equal to the selected amounts for the books.
     * @return selectedBooks ArrayList of Books that only contains the books with BookIDs contained in the choices Array.
     * @throws IllegalArgumentException if amount cannot be parsed as int or is greater then the available quantity
     */
    public static ArrayList<Books> getSelectedBooksFromBooksList(String[] choices, HashMap<Integer, String> selectedAmounts) throws IllegalArgumentException {
        ArrayList<Books> selectedBooks = new ArrayList();
        if (choices != null) {
            for (String id : choices) {
                int selectedBogID = Integer.parseInt(id);
                for (Books book : booksList) {
                    if (book.getBookID() == selectedBogID) {
                        int amount = 0;
                        try {
                            System.out.println("String length; " + selectedAmounts.get(book.getBookID()).length());
                            amount = Integer.parseInt(selectedAmounts.get(book.getBookID()));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("ParseInt Error");
                        }
                        if (book.getQty() < amount || amount <= 0) {
                            throw new IllegalArgumentException("Amount not possible");
                        }

                        selectedBooks.add(book);
                    }
                }
            }
        }
        return selectedBooks;
    }

    /**
     * This method reduces the qty for each Books object in the selectedBooks ArrayList by the amount in selectedAmounts, and sends the update to the Database thought booksMapper
     *
     * @param selectedBooks ArrayList of all books that need to be changed. This ArrayList have been sorted from the full ArrayList booksList by another method.
     * @param selectedAmounts HashMap containing Amounts for each BookID, stored as Integers. Key for each value is BookID
     */
    public static void reduceBookQuantities(ArrayList<Books> selectedBooks, HashMap<Integer, Integer> selectedAmounts) {
        for (Books selectedBook : selectedBooks) {
            int removeAmount = selectedAmounts.get(selectedBook.getBookID());
            for (Books book : booksList) {
                if (book.bookID == selectedBook.bookID) {
                    int newAmount = book.qty - removeAmount;
                    booksMapper.ReduceBookQuantityInDB(book.bookID, newAmount);
                    book.qty -= removeAmount;
                    break;
                }
            }
        }
    }

    /**
     * Find a Books object in the static ArrayList of Books, booksList, with a matching BookID to the searchedBookID
     *
     * @param searchedBookID int to find the matching BookID from a Books object in booksList
     * @return bog Books from the static booksList in Books, where the BookID matches the searchedBookID
     * @throws IllegalArgumentException if there is no book with a bookID matching the searchedBookID, this exception is thrown.
     */
    public static Books getBookFromID(int searchedBookID) throws IllegalArgumentException {
        for (Books bog : booksList) {
            if (bog.bookID == searchedBookID) {
                return bog;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Find every Books object where the title contains or is equal to the searchedBookTitle String, store them in an ArrayList, and returns it.
     *
     * @param searchedBookTitle String user inputed title
     * @return matchingBooks ArrayList of Books object with a title that contain or match the searchedBookTitle String.
     */
    public static ArrayList<Books> getBooksFromTitle(String searchedBookTitle) {
        ArrayList<Books> matchingBooks = new ArrayList<>();
        for (Books bog : booksList) {
            if (bog.title.toLowerCase().contains(searchedBookTitle.toLowerCase())) {
                matchingBooks.add(bog);
            }
        }
        return matchingBooks;
    }

    /**
     * Calls the BooksMapper to get data from the SQL Database to make all the Books objects stored in the DB.
     *
     */
    public static void createBookListFromDB() {
        emptyBookList();
        for (HashMap<String, String> map : booksMapper.getBooks()) {
            int id = Integer.parseInt(map.get("id"));
            String title = map.get("title");
            String author = map.get("author");
            int price = Integer.parseInt(map.get("price"));
            int qty = Integer.parseInt(map.get("qty"));
            Books bog = new Books(id, title, author, price, qty);
        }
    }

    /**
     * Set the BooksMapper that communicate with the Database.
     * This method should be called before the Books class is taken into use.
     *
     */
    public static void setBooksMapper(Mapper bogMap) {
        booksMapper = bogMap;
    }

    /**
     * Empty the static ArrayList of Books objects in the Books class.
     * Should be done before pulling down orders from the Database
     *
     */
    public static void emptyBookList() {
        booksList.clear();
    }

    public int getBookID() {
        return bookID;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPrice() {
        return price;
    }

    public int getQty() {
        return qty;
    }

    public static ArrayList<Books> getBooksList() {
        return booksList;
    }

    @Override
    public String toString() {
        return title + ", af " + author + ", prisen er " + price + ",- : Lager indeholder " + qty + " eksemplarer";
    }

}
