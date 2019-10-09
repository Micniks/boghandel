/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logicTest;

import java.util.ArrayList;
import java.util.HashMap;
import logic.Books;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import persistence.Mapper;

/**
 *
 * @author Michael N. Korsgaard
 */
public class BooksTest {

    private ArrayList<String> bookListIDs;
    private ArrayList<String> bookListTitles;
    private ArrayList<String> bookListAuthors;
    private Mapper bookMapper;

    @Before
    public void setup() {
        Books.emptyBookList();
        FakeMapper fakeMapper = new FakeMapper();
        bookListIDs = new ArrayList();
        bookListTitles = new ArrayList();
        bookListAuthors = new ArrayList();
        fakeMapper.insertBook(new String[]{"1", "Batman", "DC comics", "199", "730"});
        bookListIDs.add("1");
        bookListTitles.add("Batman");
        bookListAuthors.add("DC comics");
        fakeMapper.insertBook(new String[]{"2", "Superman", "Marvel", "299", "75"});
        bookListIDs.add("2");
        bookListTitles.add("Superman");
        bookListAuthors.add("Marvel");
        fakeMapper.insertBook(new String[]{"3", "IT-guy", "Cahit B.", "99", "13"});
        bookListIDs.add("3");
        bookListTitles.add("IT-guy");
        bookListAuthors.add("Cahit B.");
        fakeMapper.insertBook(new String[]{"4", "Programming 101", "Marcus J.", "0", "1"});
        bookListIDs.add("4");
        bookListTitles.add("Programming 101");
        bookListAuthors.add("Marcus J.");
        fakeMapper.insertBook(new String[]{"5", "Programming for Dummies", "Michael K.", "1", "200"});
        bookListIDs.add("5");
        bookListTitles.add("Programming for Dummies");
        bookListAuthors.add("Michael K.");
        bookMapper = fakeMapper;
        Books.setBooksMapper(bookMapper);
    }

    @Test
    public void testCreateBookObject() {
        //arrange
        int bookID = 9;
        String title = "Den Lille Bog";
        String author = "Den Lille Mand";
        int price = 199;
        int qty = 500;

        //act
        Books result = new Books(bookID, title, author, price, qty);

        //assert
        ArrayList<Books> bookList = Books.getBooksList();
        int expectedSize = 1;
        assertEquals(expectedSize, bookList.size());
        assertEquals(bookID, result.getBookID());
        assertTrue(result.getTitle().equals(title));
        assertTrue(result.getAuthor().equals(author));
        assertEquals(price, result.getPrice());
        assertEquals(qty, result.getQty());
    }

    @Test
    public void testCreateBookListFromDB() {
        //arrange
        int expectedSize = 0;
        ArrayList<Books> result = Books.getBooksList();
        assertEquals(expectedSize, result.size());

        //act
        Books.createBookListFromDB();

        //assert
        expectedSize = 5;
        result = Books.getBooksList();
        assertEquals(expectedSize, result.size());
        for (Books bog : result) {
            assertTrue(bookListIDs.contains(Integer.toString(bog.getBookID())));
            assertTrue(bookListTitles.contains(bog.getTitle()));
            assertTrue(bookListAuthors.contains(bog.getAuthor()));
        }
    }

    @Test
    public void testGetSelectedBooksFromBooksList() {
        //arrange
        Books book1 = new Books(1, "abc", "dad", 150, 31);
        Books book2 = new Books(2, "abcde", "dad", 155, 51);
        Books book3 = new Books(3, "cde", "dad", 160, 13);
        String[] choices = new String[]{"2", "3"};
        HashMap<Integer, String> amounts = new HashMap();
        amounts.put(2, "5");
        amounts.put(3, "13");
        ArrayList<Books> result;

        //act
        result = Books.getSelectedBooksFromBooksList(choices, amounts);

        //assert
        int expectedSize = 2;
        assertEquals(expectedSize, result.size());
        assertFalse(result.contains(book1));
        assertTrue(result.contains(book2));
        assertTrue(result.contains(book3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSelectedBooksFromBooksListAmountNotAnInt() {
        //arrange
        Books book1 = new Books(1, "abc", "dad", 150, 31);
        Books book2 = new Books(2, "abcde", "dad", 155, 51);
        Books book3 = new Books(3, "cde", "dad", 160, 13);
        String[] choices = new String[]{"2"};
        HashMap<Integer, String> amounts = new HashMap();
        amounts.put(2, "Hello");
        ArrayList<Books> result;

        //act
        result = Books.getSelectedBooksFromBooksList(choices, amounts);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSelectedBooksFromBooksListAmountWantedHigherThenQuantity() {
        //arrange
        Books book1 = new Books(1, "abc", "dad", 150, 31);
        Books book2 = new Books(2, "abcde", "dad", 155, 51);
        Books book3 = new Books(3, "cde", "dad", 160, 13);
        String[] choices = new String[]{"1"};
        HashMap<Integer, String> amounts = new HashMap();
        amounts.put(1, "50");
        ArrayList<Books> result;

        //act
        result = Books.getSelectedBooksFromBooksList(choices, amounts);
    }

    @Test
    public void testGetBooksFromTitle() {
        //arrange
        Books book1 = new Books(1, "abc", "dad", 150, 31);
        Books book2 = new Books(2, "abcde", "dad", 155, 51);
        Books book3 = new Books(3, "cde", "dad", 160, 13);
        String searchedTitlePart = "abc";

        //act
        ArrayList<Books> result = Books.getBooksFromTitle(searchedTitlePart);

        //assert
        int expectedSize = 2;
        assertEquals(expectedSize, result.size());
        for (Books book : result) {
            assertTrue(book.getTitle().contains(searchedTitlePart));
        }
    }

    @Test
    public void testGetBooksFromTitleNoMatch() {
        //arrange
        Books book1 = new Books(1, "abc", "dad", 150, 31);
        Books book2 = new Books(2, "abcde", "dad", 155, 51);
        Books book3 = new Books(3, "cde", "dad", 160, 13);
        String searchedTitlePart = "xyz";

        //act
        ArrayList<Books> result = Books.getBooksFromTitle(searchedTitlePart);

        //assert
        int expectedSize = 0;
        assertEquals(expectedSize, result.size());
    }

    @Test
    public void testGetBookFromID() {
        //arrange
        Books book1 = new Books(1, "abc", "dad", 150, 31);
        Books book2 = new Books(2, "abcde", "dad", 155, 51);
        Books book3 = new Books(3, "cde", "dad", 160, 13);
        int searchedBookID = 2;
        Books result;

        //act
        result = Books.getBookFromID(searchedBookID);

        //assert
        Books expected = book2;
        assertEquals(expected, result);
    }

    @Test
    public void testReduceBookQuantities() {
        //arrange
        int removeAmount = 4;
        ArrayList<Books> bookList = new ArrayList();
        Books book = new Books(3, "Den Lille Bog", "Den Lille Mand", 99, 200);
        bookList.add(book);
        HashMap<Integer, Integer> amounts = new HashMap();
        amounts.put(book.getBookID(), removeAmount);

        //act
        Books.reduceBookQuantities(bookList, amounts);

        //assert
        ArrayList<Books> result = Books.getBooksList();
        int expected = 196;
        assertEquals(book.getQty(), expected);
        assertEquals(result.get(0).getQty(), expected);
    }
}
