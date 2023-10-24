import socket


def get_guess():
    guess = input("Enter a letter: ")
    return guess


def display_game_state(game_state):
    print(f"Current state: {game_state}")


def main():
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect(("127.0.0.1", 8888))

    welcome_message = client_socket.recv(1024).decode('utf-8')
    print(welcome_message)

    while True:
        game_state = client_socket.recv(1024).decode('utf-8')
        lives_remaining = client_socket.recv(1024).decode('utf-8')

        if "win" in game_state or "lose" in game_state:
            print(game_state)
            break

        display_game_state(game_state)
        print(lives_remaining)  # Display lives remaining

        if "_" not in game_state:
            break

        guess = get_guess()
        client_socket.send(guess.encode('utf-8'))

        feedback = client_socket.recv(1024).decode('utf-8')
        print(feedback)

    client_socket.close()


if __name__ == "__main__":
    main()
