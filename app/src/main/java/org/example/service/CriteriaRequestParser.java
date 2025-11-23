package org.example.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.dto.SearchCriteria;

public interface CriteriaRequestParser {
    SearchCriteria buildSearchCriteria(HttpServletRequest req);
}
