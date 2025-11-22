package org.example.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String servletPath = req.getServletPath();
        resp.getWriter().println(servletPath);
    }

    // /products POST
    // /products GET
    // /products PUT
    // /products DELETE
    // /products?Id
    // /products?Name
    // /products?Category
    // /products?Brand
    // /products?Price Range
    // /products?combined-filter

}
