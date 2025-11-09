package org.example.console.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.example.console.ui.ConsoleUI;
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
    LinkedHashMap<String, MenuHandler> options = new LinkedHashMap<>();
    options.put("Search by Id", searchHandler::handleSearchById);
    options.put("Search by Name", searchHandler::handleSearchByName);
    options.put("Filter by Category", searchHandler::handleFilterByCategory);
    options.put("Filter by Brand", searchHandler::handleFilterByBrand);
    options.put("Filter by Price Range", searchHandler::handleFilterByPriceRange);
    options.put("Combined Filters", searchHandler::handleCombinedFilters);
    options.put(BACK_TO_MAIN_MENU_MESSAGE, () -> {}); // Back to main menu - no action

    consoleUI.printMenu("Search & Filter", new ArrayList<>(options.keySet()));
    int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

    List<MenuHandler> handlers = new ArrayList<>(options.values());
    if (choice >= 1 && choice <= handlers.size()) {
      handlers.get(choice - 1).handle();
    } else {
      consoleUI.printError(INVALID_OPTION_MESSAGE);
    }
  }
}
