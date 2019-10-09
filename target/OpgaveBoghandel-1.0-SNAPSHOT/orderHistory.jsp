<%-- 
    Document   : orderHistory
    Created on : 05-10-2019, 14:58:43
    Author     : Michael N. Korsgaard
--%>

<%@page import="logic.Books"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="logic.Order"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Order History</title>
    </head>
    <body bgcolor="#FFFFD2">
        <audio autoplay>
            <source src="decorations\\music.mp3" type="audio/mpeg">
            <p align="center">Your browser does not support the audio element.</p>
        </audio>
        <br>
        <h1 align="center">Order History</h1>

        <table width = "60%" border = "1" align = "center">
            <thead>
                <tr bgcolor = "#C8C89B">
                    <td align="center">Order Nr.</td>
                    <td align="center">Customer</td>
                    <td align="center">Order</td>
                    <td align="center">Price</td>
                </tr>
            </thead>
            <tbody>
                <%
                    int count = 0;
                    for (Order order : (ArrayList<Order>) request.getAttribute("ordre")) {

                        if (count % 2 == 0) {%>
                <tr bgcolor = "#EBEBBE">
                    <%} else {%>
                <tr bgcolor = "#DCDCAF">
                    <%}
                        count++;%>
                    <td align="center"><%=order.getOrderNr()%></td>
                    <td align="center"><%=order.getCustomerName()%></td>
                    <td align="center">
                        <table align = "center">
                            <tbody>
                                <%
                                    int price = 0;
                                    for (HashMap<String, Object> map : order.getBooksInOrder()) {
                                        Books book = (Books) map.get("book");
                                        int antal = (int) map.get("amount");
                                        price += book.getPrice() * antal;
                                        String orderMsg = antal + "x " + book.getTitle();
                                %>
                                <tr>
                                    <td align="center"><%=orderMsg%></td>
                                </tr>
                                <%}%>
                            </tbody>
                        </table>
                    </td>
                    <td align="center"><%out.print(price + " kr.");%></td>
                </tr>
                <%}%>
            </tbody>
        </table>
        <form action="FrontController" method="POST">
            <input type="hidden" name="cmd" value="Index" />
            <p align="center"><input type="submit" value="Homepage"/></p>
        </form>
    </body>
</html>
