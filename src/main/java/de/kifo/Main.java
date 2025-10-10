package de.kifo;

import lombok.Getter;

@Getter
public class Main {

    private static JavaBot javaBot;

    public static void main(String[] args) {
        boolean production = args.length > 0 && args[0].equalsIgnoreCase("production");
        javaBot = new JavaBot(production);
    }
}