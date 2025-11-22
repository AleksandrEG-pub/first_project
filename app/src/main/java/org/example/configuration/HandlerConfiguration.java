package org.example.configuration;

import org.example.console.handler.impl.AuditHandler;
import org.example.console.handler.impl.LoginHandler;
import org.example.console.handler.impl.ProductHandler;
import org.example.console.handler.impl.SearchHandler;

public class HandlerConfiguration {
  private final ProductHandler productHandler;
  private final SearchHandler searchHandler;
  private final AuditHandler auditHandler;
  private final LoginHandler loginHandler;

  public HandlerConfiguration(ServiceConfiguration services, UIConfiguration ui) {
    this.productHandler = new ProductHandler(ui.getConsoleUI(), services.getProductService());
    this.searchHandler = new SearchHandler(ui.getConsoleUI(), services.getProductService());
    this.auditHandler = new AuditHandler(ui.getConsoleUI(), services.getAuditService());
    this.loginHandler = new LoginHandler(ui.getConsoleUI(), services.getAuthService());
  }

  public ProductHandler getProductHandler() {
    return productHandler;
  }

  public SearchHandler getSearchHandler() {
    return searchHandler;
  }

  public AuditHandler getAuditHandler() {
    return auditHandler;
  }

  public LoginHandler getLoginHandler() {
    return loginHandler;
  }
}
