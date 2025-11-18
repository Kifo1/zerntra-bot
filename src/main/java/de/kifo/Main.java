package de.kifo;

import lombok.Getter;

@Getter
public class Main {

    public static void main(String[] args) {
        boolean isProduction = args.length > 0 && args[0].equalsIgnoreCase("production");
        new JavaBot(isProduction);
    }
}