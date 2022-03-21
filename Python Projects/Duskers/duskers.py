import argparse
import os
from dataclasses import dataclass
from datetime import datetime
import json
import random
from enum import Enum
from typing import List
from resources import *


class Screens(Enum):
    MAIN = 1
    NEW_GAME = 2
    STATUS = 3
    HIGH_SCORES = 4
    UPGRADE_STORE = 5
    EXPLORE = 6
    SAVE = 7
    LOAD = 8
    HELP = 9
    STATUS_MENU = 10


class Duskers:
    @dataclass
    class HighScore:
        name: str
        score: int

        def __init__(self, name, score):
            self.name = name
            self.score = score

        def to_json(self):
            return {
                "name": self.name,
                "score": self.score
            }

    @dataclass
    class GameData:
        seed: str
        min_animation_duration: int
        max_animation_duration: int
        locations_str: str
        player_name: str
        total_titanium: int
        robot_count: int
        titanium_scan_purchased: bool
        enemy_encounter_scan_purchased: bool
        save_date: str
        locations: List[str]

        def __init__(self, random_seed, min_animation_duration, max_animation_duration, locations_str, player_name="",
                     total_titanium=0, robot_count=3, titanium_scan_purchased=False,
                     enemy_encounter_scan_purchased=False, save_date="1753-01-01 00:00"):
            self.seed = random_seed
            self.min_animation_duration = min_animation_duration
            self.max_animation_duration = max_animation_duration
            self.locations_str = locations_str
            self.player_name = player_name
            self.total_titanium = total_titanium
            self.robot_count = robot_count
            self.titanium_scan_purchased = titanium_scan_purchased
            self.enemy_encounter_scan_purchased = enemy_encounter_scan_purchased
            self.save_date = save_date
            self.locations = self.locations_str.replace(",", " ").split("/")

        def to_json(self):
            return {
                "name": self.player_name,
                "titanium": self.total_titanium,
                "robots": self.robot_count,
                "titanium_scanner": self.titanium_scan_purchased,
                "encounter_scanner": self.enemy_encounter_scan_purchased,
                "last_save": datetime.now().strftime("%Y-%m-%d %H:%M")
            }

    def __init__(self, random_seed, min_animation_duration, max_animation_duration, locations_str):
        self.arg_random_seed = random_seed
        self.arg_min_animation_duration = min_animation_duration
        self.arg_max_animation_duration = max_animation_duration
        self.arg_locations_str = locations_str
        self.game_data = Duskers.GameData(self.arg_random_seed, self.arg_min_animation_duration,
                                          self.arg_max_animation_duration, self.arg_locations_str)
        self.valid_commands = {
            Screens.MAIN: ["new", "load", "high", "help", "exit"],
            Screens.NEW_GAME: ["yes", "no", "main"],
            Screens.LOAD: ["1", "2", "3", "back"],
            Screens.SAVE: ["1", "2", "3", "back"],
            Screens.HIGH_SCORES: ["back"],
            Screens.HELP: ["back"],
            Screens.STATUS: ["ex", "save", "up", "m", "exit"],
            Screens.EXPLORE: ["s", "1", "2", "3", "4", "5", "6", "7", "8", "9", "back"],
            Screens.UPGRADE_STORE: ["1", "2", "3", "back"],
            Screens.STATUS_MENU: ["back", "main", "save", "exit"]
        }
        self.saved_games = {}
        self.high_scores = []
        self.load_save_games_file()
        self.load_high_scores_file()
        random.seed(self.arg_random_seed)

    def load_save_games_file(self):
        # Check if the saved games file exists. If not, create it.
        if os.path.isfile(saved_games_file_name):
            with open(file=saved_games_file_name, mode="r", encoding="utf-8") as saved_games_file:
                saved_games_text = saved_games_file.read()
                rows = saved_games_text.split("\n")
                for i in range(0, len(rows)):
                    data = rows[i].split(" ")
                    name = data[0]
                    titanium = int(data[2])
                    robots = int(data[4])
                    last_save = f"{data[7]} {data[8]}"
                    titanium_scanner = False
                    encounter_scanner = False
                    try:
                        rows[i].index('titanium_info')
                        titanium_scanner = True
                    except ValueError:
                        pass
                    try:
                        rows[i].index('enemy_info')
                        encounter_scanner = True
                    except ValueError:
                        pass
                    self.saved_games[str(i + 1)] = {
                        "name": name,
                        "titanium": titanium,
                        "robots": robots,
                        "last_save": last_save,
                        "titanium_scanner": titanium_scanner,
                        "encounter_scanner": encounter_scanner
                    }

    def load_high_scores_file(self):
        # Check if the high scores file exists. If not, create it.
        if not os.path.isfile(high_scores_file_name):
            with open(file=high_scores_file_name, mode="w", encoding="utf-8") as high_scores_file:
                json.dump([], high_scores_file)

        with open(file=high_scores_file_name, mode="r", encoding="utf-8") as high_scores_file:
            json_data = json.load(high_scores_file)
            for i in range(0, len(json_data)):
                high_score_data = json_data[i]
                self.high_scores.append(Duskers.HighScore(
                    high_score_data["name"],
                    high_score_data["score"]
                ))

    def get_command(self, screen):
        valid_commands = self.valid_commands[screen]
        command = None
        if valid_commands is not None:
            while (command := input("\nYour command:").lower()) not in valid_commands:
                print("Invalid input")
        return command

    def save_high_scores(self):
        self.high_scores.append(Duskers.HighScore(
            self.game_data.player_name,
            self.game_data.total_titanium
        ))
        self.high_scores.sort(key=lambda x: x.score, reverse=True)
        if len(self.high_scores) > 10:
            self.high_scores = self.high_scores[0:10]
        json_data = []
        for i in range(0, len(self.high_scores)):
            json_data.append(self.high_scores[i].to_json())
        with open(file=high_scores_file_name, mode="w", encoding="utf-8") as high_scores_file:
            json.dump(json_data, high_scores_file)

    def save_game(self, save_slot):
        self.saved_games[save_slot] = self.game_data.to_json()
        with open(file=saved_games_file_name, mode="w", encoding="utf-8") as save_games_file:
            saved_game_rows = ""
            for i in range(1, 4):
                key = str(i)
                if key in self.saved_games:
                    save_game = self.saved_games[key]
                    saved_game_rows += f'{key} {save_game["name"]} Titanium: {save_game["titanium"]}' \
                                       f' Robots: {save_game["robots"]} Last save: {save_game["last_save"]}\n'
                    json.dump(self.saved_games, save_games_file)

    @staticmethod
    def exit_game():
        print("\nThanks for playing, bye!")
        exit(0)

    def end_game(self):
        print("Mission aborted, the last robot lost...")
        Duskers.frame_text(game_over)
        self.save_high_scores()

    def deploy_robots(self, search_location):
        print("Deploying robots")
        encounter_check = random.random()
        if encounter_check < search_location["encounter_rate"]:
            print("Enemy encounter")
            print(f'{search_location["location"]} explored successfully, 1 robot lost..')
            self.game_data.robot_count -= 1
            if self.game_data.robot_count == 0:
                self.end_game()
                self.main_screen()
        else:
            print(f'{search_location["location"]} explored successfully, with no damage taken.')
        self.game_data.total_titanium += search_location["titanium"]
        print(f'Acquired {search_location["titanium"]} lumps of titanium')

    def explore_screen(self):
        searched_locations = []
        total_searchable_locations = random.randint(1, 9)
        current_location = -1
        while True:
            current_location += 1
            if current_location > total_searchable_locations - 1:
                print("Nothing more in sight.\n       [Back]")
            else:
                location = random.choice(self.game_data.locations)
                titanium = random.randint(10, 100)
                encounter_rate = random.random()
                searched_locations.append({
                    "location": location,
                    "titanium": titanium,
                    "encounter_rate": encounter_rate
                })
                print("Searching")
                for index in range(0, len(searched_locations)):
                    searched_location = searched_locations[index]
                    text = f'[{index + 1}] {searched_location["location"]}'
                    if self.game_data.titanium_scan_purchased:
                        text += f' Titanium: {searched_location["titanium"]}'
                    if self.game_data.enemy_encounter_scan_purchased:
                        text += f' Encounter rate: {"{:.0%}".format(searched_location["encounter_rate"])}'
                    print(text)
                print("\n[S] to continue searching")
            command = self.get_command(Screens.EXPLORE)
            if command == "back":
                break
            elif command == "s":
                pass
            else:
                index = int(command) - 1
                if index > len(searched_locations) - 1:
                    print("Invalid input")
                else:
                    self.deploy_robots(searched_locations[index])
                    break
        self.status_screen()

    def display_saved_games(self):
        empty_slots = []
        print("   Select save slot:")
        for i in range(0, 3):
            if str(i + 1) in self.saved_games:
                saved_game = self.saved_games[str(i + 1)]
                print(f'    [{i + 1}] {saved_game["name"]} Titanium: {saved_game["titanium"]}'
                      f' Robots: {saved_game["robots"]} Last save: {saved_game["last_save"]}')
            else:
                print(f'    [{i + 1}] empty')
                empty_slots.append(i + 1)
        return empty_slots

    def save_game_screen(self, from_screen):
        self.display_saved_games()
        command = self.get_command(Screens.SAVE)
        if command == "back":
            if from_screen == Screens.STATUS:
                self.status_screen()
            elif from_screen == Screens.STATUS_MENU:
                self.status_menu_screen()
        else:
            self.game_data.save_date = datetime.now().strftime("%Y-%m-%d %H:%M")
            self.game_data.saved_to_file = True
            self.save_game(command)
            Duskers.frame_text(game_saved)
            if from_screen == Screens.STATUS:
                self.status_screen()

    def load_game_screen(self):
        empty_slots = self.display_saved_games()
        while True:
            command = self.get_command(Screens.LOAD)
            if command == "back":
                self.main_screen()
                break
            if int(command) in empty_slots:
                print("Empty slot!")
                empty_slots = self.display_saved_games()
            else:
                saved_game_data = self.saved_games[command]
                self.game_data = Duskers.GameData(
                    self.arg_random_seed,
                    self.arg_min_animation_duration,
                    self.arg_max_animation_duration,
                    self.arg_locations_str,
                    saved_game_data["name"],
                    saved_game_data["titanium"],
                    saved_game_data["robots"],
                    saved_game_data["titanium_scanner"],
                    saved_game_data["encounter_scanner"]
                )
                Duskers.frame_text(game_loaded)
                print(f" Welcome back, commander {self.game_data.player_name}!")
                self.status_screen()
                break

    def upgrade_store_screen(self):
        Duskers.frame_text(upgrade_store_menu)
        while True:
            command = self.get_command(Screens.UPGRADE_STORE)
            if command == "back":
                break
            elif command == "1":
                if self.game_data.total_titanium >= 250:
                    print(f"Purchase successful. You can now see how much titanium you can get from each"
                          f" found location.")
                    self.game_data.titanium_scan_purchased = True
                    self.game_data.total_titanium -= 250
                    break
                else:
                    print("Not enough titanium!")
            elif command == "2":
                if self.game_data.total_titanium >= 500:
                    print(f"Purchase successful. You will now see how likely you will encounter an enemy at each"
                          f" found location.")
                    self.game_data.enemy_encounter_scan_purchased = True
                    self.game_data.total_titanium -= 500
                    break
                else:
                    print("Not enough titanium!")
            elif command == "3":
                if self.game_data.total_titanium >= 1000:
                    print("Purchase successful. You now have an additional robot")
                    self.game_data.robot_count += 1
                    self.game_data.total_titanium -= 1000
                    break
                else:
                    print("Not enough titanium!")
        self.status_screen()

    def status_menu_screen(self):
        Duskers.frame_text(status_menu)
        command = self.get_command(Screens.STATUS_MENU)
        if command == "exit":
            self.exit_game()
        elif command == "back":
            self.status_screen()
        elif command == "main":
            self.main_screen()
        elif command == "save":
            self.save_game_screen(Screens.STATUS_MENU)
            self.exit_game()

    def status_screen(self):
        print(f"{frame_chars.TOP_LEFT_CORNER}{frame_chars.TOP_EDGE * ((len(robot_image[0]) + 1) * 4)}"
              f"{frame_chars.TOP_RIGHT_CORNER}")
        for j in range(0, len(robot_image)):
            robot_row = ""
            for i in range(self.game_data.robot_count):
                robot_row += robot_image[j] + " "
            print(robot_row)
        print(f"  Titanium: {self.game_data.total_titanium}\n")
        Duskers.frame_text(status)
        command = self.get_command(Screens.STATUS)
        if command == "ex":
            self.explore_screen()
        elif command == "save":
            self.save_game_screen(Screens.STATUS)
        elif command == "up":
            self.upgrade_store_screen()
        elif command == "m":
            self.status_menu_screen()
        elif command == "exit":
            self.exit_game()

    def new_game_screen(self):
        player_name = input("\nEnter your name:")
        print(f"\nWelcome, commander {player_name}!\n"
              f"Are you ready to begin?\n[Yes] [No] Return to Main[Menu]")
        command = self.get_command(Screens.NEW_GAME)
        if command == "no" or command == "menu":
            self.main_screen()
        elif command == "yes":
            self.game_data = Duskers.GameData(self.arg_random_seed, self.arg_min_animation_duration,
                                              self.arg_max_animation_duration, self.arg_locations_str)
            self.game_data.player_name = player_name
            self.status_screen()

    def high_scores_screen(self):
        print("\n	HIGH SCORES\n")
        if len(self.high_scores) == 0:
            print("No scores to display.")
        else:
            for i in range(0, 10):
                if i > len(self.high_scores) - 1:
                    print(f"({i + 1})")
                else:
                    print(f"({i + 1}) {self.high_scores[i].name} {self.high_scores[i].score}")
        print("\n       [Back]")
        self.get_command(Screens.HIGH_SCORES)
        self.main_screen()

    def help_screen(self):
        print("\nThis game is fun.")
        print("\n       [Back]")
        self.get_command(Screens.HELP)
        self.main_screen()

    def main_screen(self):
        print(game_logo)
        print(main_menu)
        command = self.get_command(Screens.MAIN)
        if command == "new":
            self.new_game_screen()
        elif command == "load":
            self.load_game_screen()
        elif command == "high":
            self.high_scores_screen()
        elif command == "help":
            self.help_screen()
        elif command == "exit":
            self.exit_game()

    @staticmethod
    def frame_text(text_rows):
        width = len(text_rows[0])
        print(f"{frame_chars.TOP_LEFT_CORNER}{frame_chars.TOP_EDGE * width}{frame_chars.TOP_RIGHT_CORNER}")

        for i in range(0, len(text_rows)):
            print(f"{frame_chars.LEFT_EDGE}{text_rows[i]}{frame_chars.RIGHT_EDGE}")

        print(f"{frame_chars.BOTTOM_LEFT_CORNER}{frame_chars.BOTTOM_EDGE * width}{frame_chars.BOTTOM_RIGHT_CORNER}")
        print()


def parse_arguments():
    argument_parser = argparse.ArgumentParser(description="Duskers Command Line Arguments")
    argument_parser.add_argument("random_seed", help="Random Seed", nargs="?", default=default_random_seed)
    argument_parser.add_argument("min_animation_duration", help="Minimum Animation Duration", type=int, nargs="?",
                                 default=default_min_animation_duration)
    argument_parser.add_argument("max_animation_duration", help="Maximum Animation Duration", type=int, nargs="?",
                                 default=default_max_animation_duration)
    argument_parser.add_argument("locations_str",
                                 help="Locations separated by '/' (spaces must be replaced with commas)", nargs="?",
                                 default=default_locations_str)
    return argument_parser.parse_args()


def main():
    args = parse_arguments()
    duskers = Duskers(args.random_seed, args.min_animation_duration, args.max_animation_duration, args.locations_str)
    duskers.main_screen()


if __name__ == "__main__":
    main()
