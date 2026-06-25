package com.exchange.jdbc.console;

import java.util.Scanner;

public final class InputReader {

    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    public String readLine(String label, String defaultValue) {
        if (defaultValue != null && !defaultValue.isBlank()) {
            System.out.print(label + " [" + defaultValue + "]: ");
        } else {
            System.out.print(label + ": ");
        }

        String value = scanner.nextLine().trim();
        return value.isEmpty() ? defaultValue : value;
    }

    public boolean readBoolean(String label, boolean defaultValue) {
        System.out.print(label + " [" + defaultValue + "]: ");
        String value = scanner.nextLine().trim();
        if (value.isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    public long readLong(String label) {
        while (true) {
            System.out.print(label + ": ");
            String value = scanner.nextLine().trim();
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                System.out.println("Enter a number");
            }
        }
    }

    public short readShort(String label) {
        while (true) {
            System.out.print(label + ": ");
            String value = scanner.nextLine().trim();
            try {
                return Short.parseShort(value);
            } catch (NumberFormatException e) {
                System.out.println("Enter a number");
            }
        }
    }
}
