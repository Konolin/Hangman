package org.example;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Server {

    private static String generateRandomWord() {
        String[] words = {"python", "hangman", "network", "programming"};
        Random rand = new Random();
        return words[rand.nextInt(words.length)];
    }

    private static String updateGameState(String word, List<Character> guessedLetters) {
        StringBuilder displayWord = new StringBuilder();
        for (char letter : word.toCharArray()) {
            if (guessedLetters.contains(letter)) {
                displayWord.append(letter).append(" ");
            } else {
                displayWord.append("_ ");
            }
        }
        return displayWord.toString().trim();
    }

    private static String handleGuess(char guess, String word, List<Character> guessedLetters, int attempts) {
        if (guessedLetters.contains(guess)) {
            return "already_guessed#" + attempts;
        } else if (word.indexOf(guess) != -1) {
            guessedLetters.add(guess);
            return updateGameState(word, guessedLetters) + "#" + attempts;
        } else {
            attempts--;
            return "wrong_guess#" + attempts;
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        System.out.println("Waiting for the first player to connect...");
        Socket client1 = serverSocket.accept();
        PrintWriter out1 = new PrintWriter(client1.getOutputStream(), true);
        out1.println("Welcome! You are Player 1.");
        System.out.println("Player 1 connected from: " + client1.getRemoteSocketAddress());

        System.out.println("Waiting for the second player to connect...");
        Socket client2 = serverSocket.accept();
        PrintWriter out2 = new PrintWriter(client2.getOutputStream(), true);
        out2.println("Welcome! You are Player 2.");
        System.out.println("Player 2 connected from: " + client2.getRemoteSocketAddress());

        String word = generateRandomWord();
        List<Character> guessedLetters = new ArrayList<>();
        int attempts = 6;

        while (attempts > 0) {
            for (Socket client : new Socket[]{client1, client2}) {
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);

                String gameStatus = updateGameState(word, guessedLetters);
                out.println(gameStatus);
                out.println("Lives remaining: " + attempts);

                if (!gameStatus.contains("_")) {
                    out.println("You win!");
                    break;
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                char guess = in.readLine().charAt(0);
                String response = handleGuess(guess, word, guessedLetters, attempts);

                String[] parts = response.split("#");
                String result = parts[0];
                attempts = Integer.parseInt(parts[1]);

                out.println(result);
            }

            if (attempts == 0) {
                out1.println("You lose! The word was " + word);
                out2.println("You lose! The word was " + word);
                break;
            }
        }

        client1.close();
        client2.close();
    }
}
