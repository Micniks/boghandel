/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.Books;
import logic.Order;
import persistence.BooksMapper;
import persistence.Mapper;

/**
 *
 * @author Michael N. Korsgaard
 */
@WebServlet(name = "FrontController", urlPatterns = {"/FrontController"})
public class FrontController extends HttpServlet {

    private boolean needSetup = true;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Setup
        if (needSetup) {
            Mapper booksMap = new BooksMapper();
            Books.setBooksMapper(booksMap);
            Order.setBooksMapper(booksMap);
            Books.createBookListFromDB();
            Order.createOrdersFromDB();
            needSetup = false;
        }

        switch (request.getParameter("cmd")) {
            case "OrderHistory":
                showOrderHistory(request, response);
                break;
            case "Search":
                callSearch(request, response);
                break;
            case "Selection":
                showSelections(request, response);
                break;
            case "Payment":
                showOrder(request, response);
                break;
            case "ConfirmOrder":
                confirmOrder(request, response);
                break;
            case "Index":
                returnToHomepage(request, response);
                break;
            default:
                break;
        }
    }

    public void returnToHomepage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        RequestDispatcher rd = request.getRequestDispatcher("index.html");
        rd.forward(request, response);
    }

    public void callSearch(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        RequestDispatcher rd = request.getRequestDispatcher("search.jsp");
        rd.forward(request, response);
    }

    public void showOrderHistory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Get books and orders from DB, and send them to orderHistory.jsp
        Books.createBookListFromDB();
        Order.createOrdersFromDB();
        ArrayList<Order> order = Order.getListOfOrders();
        request.setAttribute("ordre", order);

        RequestDispatcher rd = request.getRequestDispatcher("orderHistory.jsp");
        rd.forward(request, response);
    }

    public void showSelections(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Set the string searchedBookTitle to match the users input or if an error was meet from selection, the last user inpot for searched book title.
        String searchedBookTitle;
        if (request.getAttribute("BogTitle") == null) {
            searchedBookTitle = request.getParameter("BogTitle");
        } else {
            searchedBookTitle = (String) request.getAttribute("BogTitle");
        }

        // Get all books from DB and send them to selection.jsp
        Books.createBookListFromDB();
        ArrayList<Books> books = Books.getBooksFromTitle(searchedBookTitle);
        request.setAttribute("boger", books);
        request.setAttribute("BogTitle", searchedBookTitle);

        RequestDispatcher rd = request.getRequestDispatcher("selection.jsp");
        rd.forward(request, response);
    }

    public void showOrder(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // If we enter this method from an error caught in confirmOrder(), we have already set the attributes from there that we need, and just go in again.
        if (request.getAttribute("ContactError") != null) {
            RequestDispatcher rd = request.getRequestDispatcher("payment.jsp");
            rd.forward(request, response);
            return;
        }

        // Setup variables from parameters
        request.setAttribute("BogTitle", request.getParameter("BogTitle"));
        String[] choices = request.getParameterValues("id");
        ArrayList<Books> allBooks = Books.getBooksList();
        HashMap<Integer, String> selectedAmountsAsStrings = new HashMap();
        for (Books book : allBooks) {
            selectedAmountsAsStrings.put(book.getBookID(), request.getParameter("AmountOf" + book.getBookID()));
        }

        // Setup variables for methods
        ArrayList<Books> selectedBooks = null;
        HashMap<Integer, Integer> selectedAmountsAsIntegers = new HashMap();
        boolean errorEncountered = false;

        // Check if the books chosen are available in the quantities chosen.
        try {
            selectedBooks = Books.getSelectedBooksFromBooksList(choices, selectedAmountsAsStrings);
            for (Books book : selectedBooks) {
                selectedAmountsAsIntegers.put(book.getBookID(), Integer.parseInt(selectedAmountsAsStrings.get(book.getBookID())));
            }
        } catch (IllegalArgumentException ex) {
            request.setAttribute("QtyError", "Error");
            errorEncountered = true;
        }

        if (!errorEncountered) {

            // Save order and change quantity for the selected books, and update/insert the new data to DB
            Order.createOrdersFromDB();
            Order order = new Order();
            order.addBooksToOrder(selectedBooks, selectedAmountsAsIntegers);

            // Prepare request object and go to payment.jsp
            request.setAttribute("order", order);
            RequestDispatcher rd = request.getRequestDispatcher("payment.jsp");
            rd.forward(request, response);

        } else {
            // If an error was encountered, we go back to selection.jsp through showSelections and have it use the request.Attribute("BogTitle")
            showSelections(request, response);
        }

    }

    public void confirmOrder(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // Setup variables
        request.setAttribute("BogTitle", request.getParameter("BogTitle"));
        String customerName = request.getParameter("KundeNavn");
        String customerEmail = request.getParameter("KundeEmail");
        String customerPhone = request.getParameter("KundeTelefon");
        boolean errorEncountered = false;

        //Find the Order object from showOrder
        int orderNr = Integer.parseInt(request.getParameter("orderNr"));
        Order order = Order.getOrderFromOrderNr(orderNr);

        // Check if any customerinformation was not provided. 
        boolean customerInfoMissing = customerName.length() == 0 || customerEmail.length() == 0 || customerPhone.length() == 0;
        if (customerInfoMissing) {
            request.setAttribute("ContactError", "Error");
            errorEncountered = true;
        } else {
            order.setCustomer(customerName, customerEmail, customerPhone);
        }

        if (!errorEncountered) {

            //Unpack books and amounts from the order
            ArrayList<Books> selectedBooks = new ArrayList();
            HashMap<Integer, Integer> selectedAmountsAsIntegers = new HashMap();
            for (HashMap<String, Object> booksInOrder : order.getBooksInOrder()) {
                Books book = (Books) booksInOrder.get("book");
                selectedBooks.add(book);
                selectedAmountsAsIntegers.put(book.getBookID(), (int) booksInOrder.get("amount"));
            }

            // Change the books qty and store the order in the database.
            Books.reduceBookQuantities(selectedBooks, selectedAmountsAsIntegers);
            order.uploadOrderToDB();

            //Go to the next file.jsp in the flow
            RequestDispatcher rd = request.getRequestDispatcher("index.html");
            rd.forward(request, response);

        } else {
            // If an error was found, we go back
            request.setAttribute("order", order);
            showOrder(request, response);
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
