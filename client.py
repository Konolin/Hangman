import socket


def get_guess():
    guess = input("Enter a letter: ")
    return guess + '\n'


def display_game_state(game_state):
    print(f"Current state: {game_state}")


def main():
    # create client
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect(("127.0.0.1", 8888))

    # get the welcome message from the server
    welcome_message = client_socket.recv(1024).decode('utf-8')
    print(welcome_message)

    while True:
        # get the information from the sever
        game_state = client_socket.recv(1024).decode('utf-8')
        lives_remaining = client_socket.recv(1024).decode('utf-8')

        # check if game ended
        if "win" in game_state or "lose" in game_state:
            print(game_state)
            break

        # Display lives remaining
        display_game_state(game_state)
        print(lives_remaining)

        # check if game ended
        if "_" not in game_state:
            break

        # send guess to server
        guess = get_guess()
        client_socket.send(guess.encode('utf-8'))

        # get feedback
        feedback = client_socket.recv(1024).decode('utf-8')
        print(feedback)

    client_socket.close()


if __name__ == "__main__":
    main()
