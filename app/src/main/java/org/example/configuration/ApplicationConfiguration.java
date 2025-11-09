package org.example.configuration;

import org.example.exception.UserExitException;
import org.example.util.DataInitializer;

public class ApplicationConfiguration {
    private final ServiceConfiguration services;
    private final UIConfiguration ui;
    private final HandlerConfiguration handlers;
    private final MenuConfiguration menus;

    public ApplicationConfiguration() {
        this.services = new ServiceConfiguration();
        this.ui = new UIConfiguration();
        this.handlers = new HandlerConfiguration(services, ui);
        this.menus = new MenuConfiguration(services, ui, handlers);
    }

    public UIConfiguration getUi() {
        return ui;
    }

    public void initializeData() {
        DataInitializer.initializeDefaultData(services.getUserRepository(), services.getProductService());
    }

    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void start() throws UserExitException {
        menus.getMenuController().start();
    }

    public void shutdown() {
        try {
            ui.getConsoleUI().printMessage("Shutting down...");
        } catch (Exception ignored) {
            // quit application
        }
        try {
            ui.getConsoleUI().close();
        } catch (Exception ignored) {
            // quit application
        }
    }
}
