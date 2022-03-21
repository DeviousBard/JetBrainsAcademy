import random

choices = ["rock", "paper", "scissors"]
winning_moves = {"rock": ["paper"], "paper": ["scissors"], "scissors": ["rock"]}

file = open("rating.txt", 'r')
lines = file.readlines()
file.close()

existing_players = {}
for player in lines:
    data = player.strip().split()
    existing_players[data[0]] = int(data[1])

player = input("Enter your name: ")
print(f"Hello, {player}")
player_rating = existing_players[player] if player in existing_players else 0
game_style = input()
if game_style:
    choices = [x.strip() for x in game_style.split(",")]
    num_winners = len(choices) // 2
    winning_moves = {}
    for idx, x in enumerate(choices):
        winners = []
        for idy in range(idx + 1, idx + num_winners + 1):
            index = idy
            if index >= len(choices):
                index -= len(choices)
            winners.append(choices[index])
        winning_moves[x] = winners
print("Okay, let's start")
while True:
    player_choice = input()
    if player_choice == '!exit':
        print("Bye!")
        break
    if player_choice == '!rating':
        print(f"Your rating: {player_rating}")
    else:
        if player_choice not in choices:
            print("Invalid input")
        else:
            computer_choice = choices[random.randint(0, len(choices) - 1)]
            if computer_choice == player_choice:
                print(f"There is a draw ({player_choice})")
                player_rating += 50
            elif computer_choice in winning_moves[player_choice]:
                print(f"Sorry, but computer chose {computer_choice}")
            else:
                print(f"Well done. Computer chose {computer_choice} and failed")
                player_rating += 100


