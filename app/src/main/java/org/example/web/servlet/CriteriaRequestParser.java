package org.example.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import org.example.service.SearchCriteria;

public interface CriteriaRequestParser {
    SearchCriteria buildSearchCriteria(HttpServletRequest req);
}
