<%-- 
    Document   : search
    Created on : 02-10-2019, 11:01:29
    Author     : Michael N. Korsgaard
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Search Site</title>
    </head>
    <body bgcolor="#FFFFD2">
        <br>
        <h1 align="center">Search for Book Title</h1>
        <br>
        <h2 align="center">Search for books from a given title</h2>
        <form action="FrontController" method="POST">
            <p align="center"><input type="text" name="BogTitle" value="" placeholder="bogtitle fx Harry Potter" style="text-align:center;"/>
            <br>
            <br>
            <input type="hidden" name="cmd" value="Selection" />
            <input type="submit" value="Søg" /></p>
        </form>
        <p style="text-align:center; color:rgba(0, 0, 55, 0.6)"><i>If left blank, all books will be shown</i></p >
    </body>
</html>
