import random


def play_game_check():
    play_game = ""
    while play_game != "play" and play_game != "exit":
        play_game = input('Type "play" to play the game, "exit" to quit: ')
    return play_game


print("H A N G M A N")

word_list = ['python', 'java', 'kotlin', 'javascript']
while play_game_check() == 'play':
    chosen_word = random.choice(word_list)
    chosen_letters = set()
    attempts = 8
    while True:
        print()

        correct_letters = ""
        for letter in chosen_word:
            if letter in chosen_letters:
                correct_letters += letter
            else:
                correct_letters += "-"
        print(correct_letters)

        if not ("-" in correct_letters):
            print("You guessed the word!")
            print("You survived!")
            print()
            break

        letter = input("Input a letter: ")
        if len(letter) > 1:
            print("You should input a single letter")
        elif not(letter.isascii() and letter.islower()):
            print("It is not an ASCII lowercase letter")
        elif letter in chosen_letters:
            print("You already typed this letter")
        elif letter in chosen_word:
            chosen_letters.add(letter)
        else:
            print("No such letter in the word")
            chosen_letters.add(letter)
            attempts -= 1

        if attempts == 0:
            print("You are hanged!")
            print()
            break
