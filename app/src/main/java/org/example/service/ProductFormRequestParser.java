package org.example.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.dto.ProductForm;

/** Parses HTTP requests to create ProductForm objects. */
public interface ProductFormRequestParser {

    /** Creates a ProductForm from HTTP request parameters. */
    ProductForm parse(HttpServletRequest req);
}