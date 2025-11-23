package org.example.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.dto.ProductForm;

public interface ProductFormRequestParser {
    ProductForm parse(HttpServletRequest req);
}
