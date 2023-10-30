import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Server {

    private static String generateRandomWord() {
        List<String> words = List.of("python", "hangman", "network", "programming");
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    private static List<Object> initializeGame() {
        String word = generateRandomWord();
        List<Character> guessedLetters = new ArrayList<>();
        int attempts = 6;

        List<Object> gameData = new ArrayList<>();
        gameData.add(word);
        gameData.add(guessedLetters);
        gameData.add(attempts);

        return gameData;
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

    private static List<Object> handleGuess(char guess, String word, List<Character> guessedLetters, int attempts) {
        if (guessedLetters.contains(guess)) {
            return List.of("already_guessed", attempts);
        } else if (word.contains(String.valueOf(guess))) {
            guessedLetters.add(guess);
            return List.of(updateGameState(word, guessedLetters), attempts);
        } else {
            attempts--;
            guessedLetters.add(guess); // Add the guessed letter even for a wrong guess
            return List.of("wrong_guess", attempts);
        }
    }

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println("Server is running and waiting for players...");

            Socket player1 = serverSocket.accept();
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            PrintWriter writer1 = new PrintWriter(player1.getOutputStream(), true);

            writer1.println("Welcome! You are Player 1.");

            Socket player2 = serverSocket.accept();
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
            PrintWriter writer2 = new PrintWriter(player2.getOutputStream(), true);

            writer2.println("Welcome! You are Player 2.");

            System.out.println("Players connected!");

            List<Object> gameData = initializeGame();
            String word = (String) gameData.get(0);
            List<Character> guessedLetters = (List<Character>) gameData.get(1);
            int attempts = (int) gameData.get(2);

            while (attempts > 0) {
                // Player 1's turn
                writer1.println(updateGameState(word, guessedLetters));
                writer1.println("Lives remaining: " + attempts);
                char guess1 = reader1.readLine().charAt(0);
                List<Object> response1 = handleGuess(guess1, word, guessedLetters, attempts);
                attempts = (int) response1.get(1);
                writer1.println(response1.get(0));

                if (!response1.get(0).toString().contains("_")) {
                    writer2.println("You have guessed the word!");
                    break;
                }

                // Player 2's turn
                writer2.println(updateGameState(word, guessedLetters));
                writer2.println("Lives remaining: " + attempts);
                char guess2 = reader2.readLine().charAt(0);
                List<Object> response2 = handleGuess(guess2, word, guessedLetters, attempts);
                attempts = (int) response2.get(1);
                writer2.println(response2.get(0));

                if (!response2.get(0).toString().contains("_")) {
                    writer1.println("You have guessed the word!");
                    break;
                }

                if (attempts == 0) {
                    writer1.println("You lose! The word was " + word);
                    writer2.println("You lose! The word was " + word);
                    break;
                }
            }

        }
    }
}
