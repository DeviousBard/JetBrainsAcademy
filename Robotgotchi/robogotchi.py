import random
from dataclasses import dataclass


class Robotgotchi:
    @dataclass
    class GameStats:
        human_wins: int
        robot_wins: int
        draws: int

        def __init__(self):
            self.human_wins = 0
            self.robot_wins = 0
            self.draws = 0

        def print(self):
            print(f"\nYou won: {self.human_wins},")
            print(f"The robot won: {self.robot_wins},")
            print(f"Draws: {self.draws}.")

        def reset(self):
            self.human_wins = 0
            self.robot_wins = 0
            self.draws = 0

    class NumbersGame:
        def __init__(self):
            self.MIN_NUM = 0
            self.MAX_NUM = 1_000_000
            self.game_stats = Robotgotchi.GameStats()

        def valid_number(self, num_str):
            try:
                int(num_str)
            except ValueError:
                print("\nA string is not a valid input!")
                return False
            num = int(num_str)
            if num < self.MIN_NUM:
                print("\nThe number can't be negative!")
                return False
            elif num > self.MAX_NUM:
                print("\nInvalid input! The number can't be bigger than 1000000")
                return False
            return True

        def play_game(self):
            while True:
                response = Robotgotchi.get_user_input("\nWhat is your number? ")
                if response.lower() == "exit game":
                    self.game_stats.print()
                    break
                if self.valid_number(response):
                    human_number = int(response)
                    robot_number = random.randint(self.MIN_NUM, self.MAX_NUM)
                    goal_number = random.randint(self.MIN_NUM, self.MAX_NUM)
                    print(f"The robot entered the number {robot_number}.")
                    print(f"The goal number is {goal_number}. ")
                    if abs(human_number - goal_number) < abs(robot_number - goal_number):
                        print("You won!")
                        self.game_stats.human_wins += 1
                    elif abs(human_number - goal_number) > abs(robot_number - goal_number):
                        print("The robot won!")
                        self.game_stats.robot_wins += 1
                    else:
                        print("It's a draw!")
                        self.game_stats.draws += 1

    class RockPaperScissorsGame:
        def __init__(self):
            self.game_stats = Robotgotchi.GameStats()
            self.hierarchy = {"rock": "paper", "paper": "scissors", "scissors": "rock"}
            self.valid_choices = ["rock", "paper", "scissors"]

        def resolve_winner(self, human_choice, robot_choice):
            # Draw
            result = (0, 0, 1)
            if self.hierarchy[human_choice] == robot_choice:
                # Robot won
                result = (0, 1, 0)
                print("The robot won!")
            elif self.hierarchy[robot_choice] == human_choice:
                # Human won
                result = (1, 0, 0)
                print("You won!")
            else:
                print("It's a draw!")
            return result

        def play_game(self):
            while True:
                human_choice = Robotgotchi.get_user_input("\nWhat is your move? ").lower()
                if human_choice == "exit game":
                    self.game_stats.print()
                    break
                elif human_choice in self.valid_choices:
                    robot_choice = random.choice(self.valid_choices)
                    print(f"The robot chose {robot_choice}")
                    results = self.resolve_winner(human_choice, robot_choice)
                    self.game_stats.human_wins += results[0]
                    self.game_stats.robot_wins += results[1]
                    self.game_stats.draws += results[2]
                else:
                    print("No such option! Try again!")

    def __init__(self):
        self.numbers_game = Robotgotchi.NumbersGame()
        self.roshambo = Robotgotchi.RockPaperScissorsGame()
        self.valid_games = ["numbers", "rock-paper-scissors"]
        self.valid_options = ["exit", "info", "work", "play", "oil", "recharge", "sleep", "learn"]
        self.robot_name = ""
        self.levels = {
            "the battery": 100,
            "overheat": 0,
            "skill": 0,
            "boredom": 0,
            "rust": 0
        }

    @staticmethod
    def get_user_input(prompt):
        response = input(f"{prompt}")
        return response

    def check_for_robot_failure(self):
        if self.levels["overheat"] == 100:
            print(f"The level of overheat reached 100, {self.robot_name} has blown up! Game over. Try again?")
            exit(0)
        elif self.levels["rust"] == 100:
            print(f"{self.robot_name} is too rusty! Game over. Try again?")
            exit(0)

    def change_level(self, item, amount):
        last_level = self.levels[item]
        self.levels[item] += amount
        if self.levels[item] > 100:
            self.levels[item] = 100
        if self.levels[item] < 0:
            self.levels[item] = 0
        self.check_for_robot_failure()
        print(f"{self.robot_name}'s level of {item} was {last_level}. Now it is {self.levels[item]}.")

    def play_games(self):
        while True:
            selected_game = self.get_user_input("\nWhich game would you like to play? ").lower()
            if selected_game in self.valid_games:
                if selected_game == "numbers":
                    self.numbers_game.game_stats.reset()
                    self.numbers_game.play_game()
                    break
                elif selected_game == "rock-paper-scissors":
                    self.roshambo.game_stats.reset()
                    self.roshambo.play_game()
                    break
            else:
                print("\nPlease choose a valid option: Numbers or Rock-paper-scissors?")
        print()
        self.change_level("boredom", -20)
        self.change_level("overheat", 10)
        if self.levels["boredom"] == 0:
            print(f"{self.robot_name} is in a great mood!")
        self.check_for_unpleasant_event()

    def choose_option(self):
        while True:
            print(f"\nAvailable interactions with {self.robot_name}:")
            print("exit - Exit")
            print("info - Check the vitals")
            print("work - Work")
            print("play - Play")
            print("oil - Oil")
            print("recharge - Recharge")
            print("sleep - Sleep mode")
            print("learn - Learn skills")
            option = self.get_user_input("\nChoose: ").lower()
            if option in self.valid_options:
                break
            else:
                print("Invalid input, try again!")
        return option

    def show_info(self):
        print(f'\n{self.robot_name}\'s stats are: ')
        print(f'the battery is {self.levels["the battery"]},')
        print(f'overheat is {self.levels["overheat"]}')
        print(f'skill level is {self.levels["skill"]}')
        print(f'boredom is {self.levels["boredom"]}.')
        print(f'rust is {self.levels["rust"]}')

    def sleep(self):
        print("")
        if self.levels["overheat"] > 0:
            print(f"{self.robot_name} cooled off!")
            self.change_level("overheat", -20)
        if self.levels["overheat"] == 0:
            print(f"{self.robot_name} is cool!")

    def recharge(self):
        print("")
        self.change_level("overheat", -5)
        self.change_level("the battery", 10)
        self.change_level("boredom", 5)
        print(f"{self.robot_name} is recharged!")

    def check_for_unpleasant_event(self):
        if self.levels["the battery"] < 10:
            print(f"Guess what! {self.robot_name} fell into the pool!")
            self.change_level("rust", 50)
        elif self.levels["the battery"] < 30:
            print(f"Oh no, {self.robot_name} stepped into a puddle!")
            self.change_level("rust", 10)

    def learn(self):
        if self.levels["skill"] == 100:
            print(f"There's nothing for {self.robot_name} to learn!")
        else:
            self.change_level("skill", 10)
            self.change_level("overheat", 10)
            self.change_level("the battery", -10)
            self.change_level("boredom", 5)
            print(f"{self.robot_name} has become smarter!")
            self.check_for_unpleasant_event()

    def work(self):
        if self.levels["skill"] < 50:
            print(f"{self.robot_name} has got to learn before working!")
        else:
            self.change_level("boredom", 10)
            self.change_level("overheat", 10)
            self.change_level("the battery", -10)
            print(f"{self.robot_name} did well!")
            self.check_for_unpleasant_event()

    def oil(self):
        if self.levels["rust"] == 0:
            print(f"{self.robot_name} is fine, no need to oil!")
        else:
            self.change_level("rust", -20)

    def interact(self):
        self.robot_name = self.get_user_input("\nHow will you call your robot? ")
        while True:
            option = self.choose_option()
            if option == "info":
                self.show_info()
            elif option == "exit":
                print("Game over")
                exit(0)
            elif self.levels["the battery"] == 0 and not option == "recharge":
                print(f"The level of the battery is 0, {self.robot_name} needs recharging!")
            elif self.levels["boredom"] == 100 and not option == "play":
                print(f"{self.robot_name} is too bored! {self.robot_name} needs to have fun!")
            elif option == "recharge":
                self.recharge()
            elif option == "sleep":
                self.sleep()
            elif option == "play":
                self.play_games()
            elif option == "learn":
                self.learn()
            elif option == "work":
                self.work()
            elif option == "oil":
                self.oil()


def main():
    Robotgotchi().interact()


if __name__ == "__main__":
    main()
