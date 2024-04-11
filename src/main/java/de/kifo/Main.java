package de.kifo;

import lombok.Getter;

@Getter
public class Main {

    private static JavaBot javaBot;

    public static void main(String[] args) {
        javaBot = new JavaBot();
    }
}