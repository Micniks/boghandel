<%-- 
    Document   : selection
    Created on : 02-10-2019, 10:31:18
    Author     : Michael N. Korsgaard
--%>

<%@page import="logic.Books"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Search Results</title>
    </head>
    <body bgcolor="#FFFFD2">
        <br>
        <h1 align="center">Books with the searched title</h1>
        <br>
        <%if (request.getAttribute("QtyError") != null) {%>
        <h2 align="center" style="color:red;">You have not choosen a proper amount. <br>Please try again.</h2>  
            <%} else {%>
        <h2 align="center">Choose the books and amounts you wish to buy</h2>
        <%}%>
        <form action="FrontController" method="POST">
            <input type="hidden" name="cmd" value="Payment" />
            <input type="hidden" name="BogTitle" value="<%=request.getAttribute("BogTitle")%>" />
            <table width = "50%" border = "1" align = "center">
                <thead>
                    <tr bgcolor = "#87E187">
                        <td>Title</td>
                        <td>Author</td>
                        <td align="center">Price</td>
                        <td align="center">Amount</td>
                        <td align="center">Select</td>
                        <td align="center">In Stock</td>
                    </tr>
                </thead>
                <tbody>
                    <%
                        ArrayList<Books> books = (ArrayList<Books>) request.getAttribute("boger");
                        for (Books book : books) {
                            int lagerAntal = book.getQty();
                            if (lagerAntal > 10) {
                    %>
                    <tr bgcolor = "#CDFFCD">
                        <%
                        } else if (lagerAntal >= 1) {
                        %>
                    <tr bgcolor = "#FFFFA5">
                        <%
                        } else {
                        %>
                    <tr bgcolor = "#FFA0A0">
                        <%
                            }
                        %>                    
                        <td><%=book.getTitle()%></td>
                        <td><%=book.getAuthor()%></td>
                        <td align="center"><%=book.getPrice()%></td>
                        <%
                            if (lagerAntal >= 1) {
                        %>    
                        <td align="center"><input type="text" name=AmountOf<%=book.getBookID()%> value="1" size="1" style="text-align:center;"></td>
                        <td align="center"><input type="checkbox" name=id value="<%=book.getBookID()%>"></td>
                            <%
                            } else {
                            %>
                        <td align="center">Sold Out!</td>
                        <td align="center"> ! </td>
                        <%
                            }
                        %> 
                        <td align="center"><%=book.getQty()%></td>
                    </tr>
                    <% } //end loop %>
                </tbody>
            </table>
            <br>
            <p align="center">
                <input type="submit" value="Go to Buy" />
            </p>
        </form>
        <form action="FrontController" method="POST">
            <input type="hidden" name="cmd" value="Search" />
            <p align="center"><input type="submit" value="Go back to Search"/></p>
        </form>
    </body>
</html>
