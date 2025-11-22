package org.example.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import org.example.dto.ProductForm;

public interface ProductFormRequestParser {
    ProductForm parse(HttpServletRequest req);
}
