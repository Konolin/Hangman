import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class Client2 {

    private static String getGuess() {
        System.out.print("Enter a letter: ");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static void displayGameState(String gameState) {
        System.out.println("Current state: " + gameState);
    }

    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 8888);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            // get the welcome message from the server
            String welcomeMessage = reader.readLine();
            System.out.println(welcomeMessage);

            while (true) {
                // get the information from the server
                String gameState = reader.readLine();
                String livesRemaining = reader.readLine();

                // check if game ended
                if (gameState.contains("win") || gameState.contains("lose")) {
                    System.out.println(gameState);
                    break;
                }

                // display lives remaining
                displayGameState(gameState);
                System.out.println(livesRemaining);

                // check if game ended
                if (!gameState.contains("_")) {
                    break;
                }

                // send guess to server
                String guess = getGuess();

                // check if guess is not empty
                if (!guess.isEmpty()) {
                    writer.println(guess);
                }

                // get feedback
                String feedback = reader.readLine();
                System.out.println(feedback);
            }
        } catch (SocketException se) {
            System.out.println("Connection to the server lost.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
