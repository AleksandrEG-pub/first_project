package org.example.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.dto.SearchCriteria;

/** Parses HTTP requests to build search criteria. */
public interface CriteriaRequestParser {

    /** Builds search criteria from HTTP request parameters. */
    SearchCriteria buildSearchCriteria(HttpServletRequest req);
}
