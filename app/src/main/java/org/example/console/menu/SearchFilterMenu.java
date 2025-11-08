package org.example.console.menu;

import java.util.HashMap;
import java.util.Map;
import org.example.console.ConsoleUI;
import org.example.console.MenuHandler;
import org.example.console.handler.SearchHandler;

public class SearchFilterMenu {
  private static final String INVALID_OPTION_MESSAGE = "Invalid option. Please try again.";
  private static final String SELECT_OPTION_MESSAGE = "Select an option: ";
  private static final String BACK_TO_MAIN_MENU_MESSAGE = "Back to Main Menu";

  private final ConsoleUI consoleUI;
  private final SearchHandler searchHandler;

  public SearchFilterMenu(ConsoleUI consoleUI, SearchHandler searchHandler) {
    this.consoleUI = consoleUI;
    this.searchHandler = searchHandler;
  }

  public void show() {
    String[] options = {
      "Search by Name",
      "Filter by Category",
      "Filter by Brand",
      "Filter by Price Range",
      "Combined Filters",
      BACK_TO_MAIN_MENU_MESSAGE
    };
    Map<Integer, MenuHandler> handlers = new HashMap<>();
    handlers.put(1, searchHandler::handleSearchByName);
    handlers.put(2, searchHandler::handleFilterByCategory);
    handlers.put(3, searchHandler::handleFilterByBrand);
    handlers.put(4, searchHandler::handleFilterByPriceRange);
    handlers.put(5, searchHandler::handleCombinedFilters);
    handlers.put(6, () -> {}); // Back to main menu - no action

    consoleUI.printMenu("Search & Filter", options);
    int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

    MenuHandler handler = handlers.get(choice);
    if (handler != null) {
      handler.handle();
    } else {
      consoleUI.printError(INVALID_OPTION_MESSAGE);
    }
  }
}

