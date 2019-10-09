<%-- 
    Document   : payment
    Created on : 02-10-2019, 10:31:31
    Author     : Michael N. Korsgaard
--%>

<%@page import="java.util.HashMap"%>
<%@page import="logic.Order"%>
<%@page import="logic.Books"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Payment</title>
    </head>
    <body bgcolor="#FFFFD2">
        <br>
        <h1 align = "center">Confirm Order</h1>
        <%
            Order order = (Order) request.getAttribute("order");
            ArrayList<Books> books = new ArrayList();
            if (order.getBooksInOrder().size() > 0) {
        %>
        <br>
        <h2 align="center">You are about to buy the following</h2>
        <table width = "50%" border = "1" align = "center">
            <thead>
                <tr bgcolor = "#87E187">
                    <td>Title</td>
                    <td>Author</td>
                    <td align="center">Book Price</td>
                    <td align="center">Amount</td>
                    <td align="center">Subtotal Price</td>
                </tr>
            </thead>
            <tbody>
                <%
                    double totalPris = 0;
                    for (HashMap<String, Object> map : order.getBooksInOrder()) {
                        Books book = (Books) map.get("book");
                        int amount = (int) map.get("amount");
                %>                    
                <tr bgcolor = "#CDFFCD">
                    <td><%=book.getTitle()%></td>
                    <td><%=book.getAuthor()%></td>
                    <td align="center"><%=book.getPrice()%></td>
                    <td align="center"><%=amount%></td>
                    <td align="center"><%=amount * book.getPrice()%></td>
                </tr>
                <%
                        totalPris += book.getPrice() * amount;
                    } //end loop 
                %>
            </tbody>
        </table>
        <br>
        <h2 align ="center">Total Pris
            <br>
            <%
                String totalPrisStr = String.format("%.02f", totalPris);
                out.print(totalPrisStr + " kr.");
            %>
        </h2>
        <br>
        <form action="FrontController" method="POST">
            <%if (request.getAttribute("ContactError") != null) {%>
            <h2 align="center" style="color:red;">Remeber to insert all contact info</h2>  
            <%}%>
            <table width = "40%" border = "1" align = "center">
                <thead>
                    <tr bgcolor = "#C3C3C3">
                        <td align="center">Name</td>
                        <td align="center">Email</td>
                        <td align="center">Phone Number</td>
                    </tr>
                </thead>
                <tbody>
                    <tr bgcolor = "#E1E1E1">
                        <td align="center"><input type="text" name="KundeNavn" value="" placeholder="Fx. Michael N. K." style="text-align:center;"/></td>
                        <td align="center"><input type="text" name="KundeEmail" value="" placeholder="MyEmail@gmail.com" style="text-align:center;"/></td>
                        <td align="center"><input type="text" name="KundeTelefon" value="" placeholder="Fx. 33 11 56 66" style="text-align:center;"/></td>
                    </tr>
                </tbody>
            </table>
            <input type="hidden" name="cmd" value="ConfirmOrder" />
            <input type="hidden" name="BogTitle" value="<%=request.getAttribute("BogTitle")%>" />
            <input type="hidden" name="orderNr" value="<%=order.getOrderNr()%>" />
            <p align="center"><input type="submit" value="Confirm Payment"/></p>
        </form>
        <%
        } else {
        %>
        <br>
        <h2 align = "center">You did not buy any books</h2>
        <%}%>
        <br>
        <form action="FrontController" method="POST">
            <input type="hidden" name="cmd" value="Selection" />
            <input type="hidden" name="BogTitle" value="<%=request.getAttribute("BogTitle")%>" />
            <p align="center"><input type="submit" value="Back to Book Selection"/></p>
        </form>
    </body>
</html>
