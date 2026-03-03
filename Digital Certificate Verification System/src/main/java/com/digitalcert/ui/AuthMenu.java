package com.digitalcert.ui;

import com.digitalcert.service.UserService;

import java.util.Scanner;

public class AuthMenu {

    private final UserService userService;
    private final Scanner scanner;

    public AuthMenu(UserService userService) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Shows login/register menu.
     *
     * @return true if user authenticated and app should continue; false to exit.
     */
    public boolean start() {
        while (true) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> {
                    if (login()) {
                        return true;
                    }
                }
                case 2 -> register();
                case 3 -> {
                    System.out.println("Exiting application. Goodbye!");
                    return false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Digital Certificate Verification System ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
    }

    private boolean login() {
        System.out.println("\n-- Login --");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        boolean authenticated = userService.authenticate(username, password);
        if (authenticated) {
            System.out.println("Login successful. Welcome, " + username + "!");
            return true;
        } else {
            System.out.println("Invalid username or password.");
            return false;
        }
    }

    private void register() {
        System.out.println("\n-- Register --");
        System.out.print("Choose a username: ");
        String username = scanner.nextLine();
        System.out.print("Choose a password: ");
        String password = scanner.nextLine();

        try {
            userService.register(username, password);
            System.out.println("Registration successful. You can now log in.");
        } catch (RuntimeException ex) {
            System.out.println("Registration failed: " + ex.getMessage());
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }
}

