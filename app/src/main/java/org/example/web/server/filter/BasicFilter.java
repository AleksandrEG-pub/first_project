package org.example.web.server.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.exception.AccessDeniedException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

@RequiredArgsConstructor
public class BasicFilter {
  private final ObjectMapper objectMapper;

  @SneakyThrows
  protected String getResponse(String title, String message, int status, HttpServletRequest httpRequest) {
    ProblemDetail problemDetail = ProblemDetail.forStatus(status);
    problemDetail.setInstance(toInstance(httpRequest));
    ErrorResponse errorResponse =
        ErrorResponse.builder(new AccessDeniedException(message), problemDetail)
            .title(title)
            .detail(message)
            .instance(URI.create(httpRequest.getRequestURI()))
            .build();
    return toJson(errorResponse);
  }

  private URI toInstance(HttpServletRequest request) {
    return URI.create(request.getRequestURI());
  }

  private String toJson(ErrorResponse errorResponse) throws JsonProcessingException {
    Map<String, Object> properties = errorResponse.getBody().getProperties();
    return objectMapper.writeValueAsString(properties);
  }
}
