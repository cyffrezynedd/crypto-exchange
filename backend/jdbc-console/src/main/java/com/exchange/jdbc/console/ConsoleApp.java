package com.exchange.jdbc.console;

import com.exchange.jdbc.dao.CurrencyDao;
import com.exchange.jdbc.dao.UserDao;
import com.exchange.jdbc.dao.UserRoleDao;
import com.exchange.jdbc.model.Currency;
import com.exchange.jdbc.model.Role;
import com.exchange.jdbc.model.User;
import com.exchange.jdbc.model.UserRoleAssignment;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleApp {

    private static final String MSG_UNKNOWN_OPTION = "Unknown option";
    private static final String MSG_NOT_FOUND = "Not found";
    private static final String PROMPT_USER_ID = "user_id";

    private final InputReader input;
    private final UserDao userDao = new UserDao();
    private final CurrencyDao currencyDao = new CurrencyDao();
    private final UserRoleDao userRoleDao = new UserRoleDao();

    public ConsoleApp(Scanner scanner) {
        this.input = new InputReader(scanner);
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMainMenu();
            String choice = input.readLine(">");

            try {
                switch (choice) {
                    case "1" -> userMenu();
                    case "2" -> currencyMenu();
                    case "3" -> userRoleMenu();
                    case "0" -> running = false;
                    default -> System.out.println(MSG_UNKNOWN_OPTION);
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }

        System.out.println("Bye");
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("=== Crypto Exchange JDBC ===");
        System.out.println("1 - users");
        System.out.println("2 - currencies");
        System.out.println("3 - user roles (M2M)");
        System.out.println("0 - exit");
    }

    private void userMenu() throws SQLException {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("-- Users --");
            System.out.println("1 create | 2 list | 3 find | 4 update | 5 delete | 0 back");

            switch (input.readLine(">")) {
                case "1" -> createUser();
                case "2" -> listUsers();
                case "3" -> findUser();
                case "4" -> updateUser();
                case "5" -> deleteUser();
                case "0" -> back = true;
                default -> System.out.println(MSG_UNKNOWN_OPTION);
            }
        }
    }

    private void currencyMenu() throws SQLException {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("-- Currencies --");
            System.out.println("1 create | 2 list | 3 find | 4 update | 5 delete | 0 back");

            switch (input.readLine(">")) {
                case "1" -> createCurrency();
                case "2" -> listCurrencies();
                case "3" -> findCurrency();
                case "4" -> updateCurrency();
                case "5" -> deleteCurrency();
                case "0" -> back = true;
                default -> System.out.println(MSG_UNKNOWN_OPTION);
            }
        }
    }

    private void userRoleMenu() throws SQLException {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("-- User Roles --");
            System.out.println("1 assign | 2 list all | 3 list by user | 4 remove | 5 list roles | 0 back");

            switch (input.readLine(">")) {
                case "1" -> assignRole();
                case "2" -> listAssignments(userRoleDao.findAllAssignments());
                case "3" -> listByUser();
                case "4" -> removeRole();
                case "5" -> listRoles();
                case "0" -> back = true;
                default -> System.out.println(MSG_UNKNOWN_OPTION);
            }
        }
    }

    private void createUser() throws SQLException {
        User user = readUser(null);
        System.out.println("Created: " + userDao.create(user));
    }

    private void listUsers() throws SQLException {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("No users");
            return;
        }
        users.forEach(System.out::println);
    }

    private void findUser() throws SQLException {
        Optional<User> user = userDao.findById(input.readLong("id"));
        System.out.println(user.map(Object::toString).orElse(MSG_NOT_FOUND));
    }

    private void updateUser() throws SQLException {
        long id = input.readLong("id");
        Optional<User> existing = userDao.findById(id);
        if (existing.isEmpty()) {
            System.out.println(MSG_NOT_FOUND);
            return;
        }

        User user = readUser(existing.get());
        user.setId(id);
        System.out.println(userDao.update(user) ? "Updated" : "Update failed");
    }

    private void deleteUser() throws SQLException {
        System.out.println(userDao.delete(input.readLong("id")) ? "Deleted" : MSG_NOT_FOUND);
    }

    private User readUser(User defaults) {
        User user = defaults != null ? defaults : new User();
        user.setEmail(input.readLine("email", user.getEmail()));
        user.setPasswordHash(input.readLine("password_hash", user.getPasswordHash()));
        user.setUsername(input.readLine("username", user.getUsername()));
        user.setKycStatus(input.readLine("kyc_status", user.getKycStatus() != null ? user.getKycStatus() : "PENDING").trim().toUpperCase());
        user.setActive(input.readBoolean("active", defaults == null || user.isActive()));
        return user;
    }

    private void createCurrency() throws SQLException {
        Currency currency = readCurrency(null);
        System.out.println("Created: " + currencyDao.create(currency));
    }

    private void listCurrencies() throws SQLException {
        List<Currency> currencies = currencyDao.findAll();
        if (currencies.isEmpty()) {
            System.out.println("No currencies");
            return;
        }
        currencies.forEach(System.out::println);
    }

    private void findCurrency() throws SQLException {
        Optional<Currency> currency = currencyDao.findById(input.readLong("id"));
        System.out.println(currency.map(Object::toString).orElse(MSG_NOT_FOUND));
    }

    private void updateCurrency() throws SQLException {
        long id = input.readLong("id");
        Optional<Currency> existing = currencyDao.findById(id);
        if (existing.isEmpty()) {
            System.out.println(MSG_NOT_FOUND);
            return;
        }

        Currency currency = readCurrency(existing.get());
        currency.setId(id);
        System.out.println(currencyDao.update(currency) ? "Updated" : "Update failed");
    }

    private void deleteCurrency() throws SQLException {
        System.out.println(currencyDao.delete(input.readLong("id")) ? "Deleted" : MSG_NOT_FOUND);
    }

    private Currency readCurrency(Currency defaults) {
        Currency currency = defaults != null ? defaults : new Currency();
        currency.setCode(input.readLine("code", currency.getCode()));
        currency.setName(input.readLine("name", currency.getName()));

        if (defaults != null) {
            String decimals = input.readLine("decimals", String.valueOf(currency.getDecimals()));
            currency.setDecimals(Short.parseShort(decimals));
        } else {
            currency.setDecimals(input.readShort("decimals"));
        }

        currency.setActive(input.readBoolean("active", defaults == null || currency.isActive()));
        return currency;
    }

    private void assignRole() throws SQLException {
        long userId = input.readLong(PROMPT_USER_ID);
        long roleId = input.readLong("role_id");

        if (userDao.findById(userId).isEmpty()) {
            System.out.println("User not found");
            return;
        }

        boolean assigned = userRoleDao.assign(userId, roleId);
        if (assigned) {
            userRoleDao.findAssignment(userId, roleId).ifPresent(a -> System.out.println("Assigned: " + a));
        } else {
            System.out.println("Already assigned or role not found");
        }
    }

    private void listByUser() throws SQLException {
        listAssignments(userRoleDao.findByUserId(input.readLong(PROMPT_USER_ID)));
    }

    private void listAssignments(List<UserRoleAssignment> assignments) {
        if (assignments.isEmpty()) {
            System.out.println("No assignments");
            return;
        }
        assignments.forEach(System.out::println);
    }

    private void removeRole() throws SQLException {
        long userId = input.readLong(PROMPT_USER_ID);
        long roleId = input.readLong("role_id");
        System.out.println(userRoleDao.remove(userId, roleId) ? "Removed" : MSG_NOT_FOUND);
    }

    private void listRoles() throws SQLException {
        List<Role> roles = userRoleDao.findAllRoles();
        if (roles.isEmpty()) {
            System.out.println("No roles");
            return;
        }
        roles.forEach(System.out::println);
    }
}
