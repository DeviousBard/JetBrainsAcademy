import json
import os
import re


class AdventureGame:

    def __init__(self):
        self.game_title = "the Journey to Mount Qaf"
        self.game_file = None
        self.character = {
            "name": None,
            "species": None,
            "gender": None,
            "snack": None,
            "weapon": None,
            "tool": None,
            "lives": -1,
            "inventory": set(),
            "difficulty": -1,
            "level": -1,
            "scene": 1,
            "last_level": -1,
            "last_scene": -1,
            "user_name": None
        }
        self.valid_commands = {"/i", "/c", "/h", "/q"}

    def main_menu(self):
        print(f"***Welcome to {self.game_title}***")
        print()
        print("1- Press key '1' or type 'start' to start a new game")
        print("2- Press key '2' or type 'load' to load your progress")
        print("3- Press key '3' or type 'quit' to quit the game")
        valid_choices = ["1", "2", "3", "start", "load", "quit"]
        while (selection := str(input()).lower()) not in valid_choices:
            print("Unknown input! Please enter a valid one.")
        if selection in ["1", "start"]:
            self.start_new_game()
        elif selection in ["2", "load"]:
            self.load_game()
        elif selection in ["3", "quit"]:
            self.quit()

    @staticmethod
    def display_help():
        print("Type the number of the option you want to choose.")
        print("Commands you can use:")
        print("/i => Shows inventory.")
        print("/q => Exits the game.")
        print("/c => Shows the character traits.")
        print("/h => Shows help.")

    def display_character(self):
        print(f'Your character: {self.character["name"]}, {self.character["species"]}, {self.character["gender"]}')
        print(f'Lives remaining: {self.character["lives"]}')

    def display_inventory(self):
        print(f'Inventory: {self.character["weapon"]}, {self.character["tool"]}, {self.character["snack"]}, '
              f'{", ".join(list(self.character["inventory"]))}')

    def load_story(self):
        path = f'{os.getcwd()}/story/story.json'
        game_file = open(path, "r").read()
        self.game_file = json.loads(game_file)

    def replace_character_fields_in_text(self, text):
        character_fields = {"name", "species", "gender", "weapon", "tool", "snack"}
        for character_field in character_fields:
            text = str(text).replace(f'{{{character_field}}}', self.character[character_field])
        return text

    def get_level_title(self, level):
        return self.game_file["story"]["lvl" + str(level)]["title"]

    def get_scene_text(self, level, scene):
        if f"lvl{str(level)}" not in self.game_file["story"] or \
                f"scene{str(scene)}" not in self.game_file["story"][f"lvl{str(level)}"]["scenes"]:
            self.quit()
        return self.game_file["story"][f"lvl{str(level)}"]["scenes"][f"scene{str(scene)}"]

    def get_scene_choices(self, level, scene):
        return self.game_file["choices"]["lvl" + str(level)]["scene" + str(scene)]

    @staticmethod
    def print_scene_choices(choices):
        i = 1
        while (option := ("choice" + str(i))) in choices:
            print(f"{i}- {choices[option]}")
            i += 1

    def play_game(self):
        while self.character['lives'] > 0:
            if f'lvl{self.character["level"]}' not in self.game_file["story"]:
                print("Congratulations! You beat the game!")
                exit(0)
            print(self.get_level_title(self.character["level"]))
            print()
            print()
            self.character["last_level"] = self.character["level"]
            print(f'{self.replace_character_fields_in_text(self.get_scene_text(self.character["level"], self.character["scene"]))}')
            self.character["last_scene"] = self.character["scene"]
            print()
            print("What will you do? Type the number of the option or type '/h' to show help.")
            print()
            choices = self.get_scene_choices(self.character["level"], self.character["scene"])
            valid_choices = set().union(self.valid_commands).union({str(i) for i in range(1, len(choices) + 1)})
            self.print_scene_choices(choices)
            while (choice := input().lower()) not in valid_choices:
                print("Unknown input! Please enter a valid one.")
            if choice == '/q':
                while (yesno := input('You sure you want to quit the game? Y/N').lower()) not in {"y", "n"}:
                    continue
                if yesno == 'y':
                    self.quit()
            elif choice == '/h':
                self.display_help()
            elif choice == '/c':
                self.display_character()
            elif choice == '/i':
                self.display_inventory()
            else:
                self.handle_outcome(self.character["level"], self.character["scene"], choice)
        if self.character["lives"] == 0:
            print(f"You've run out of lives! Game over!")
            self.launch_game()

    def set_difficulty(self):
        print("Choose your difficulty:")
        print("1- Easy")
        print("2- Medium")
        print("3- Hard")
        valid_choices = ["1", "2", "3", "easy", "medium", "hard"]
        while (selection := str(input()).lower()) not in valid_choices:
            print("Unknown input! Please enter a valid one.")
        if selection in ["1", "easy"]:
            self.character["difficulty"] = "Easy"
            self.character["lives"] = 5
        elif selection in ["2", "medium"]:
            self.character["difficulty"] = "Medium"
            self.character["lives"] = 3
        elif selection in ["3", "hard"]:
            self.character["difficulty"] = "Hard"
            self.character["lives"] = 1

    def set_inventory(self):
        print("Pack your bag for the journey:")
        self.character["snack"] = " ".join(word.capitalize() for word in input("1- Favourite Snack").split())
        self.character["weapon"] = " ".join(word.capitalize() for word in input("2- A weapon for the journey").split())
        self.character["tool"] = " ".join(word.capitalize() for word in input("3- A traversal tool").split())

    def create_character(self):
        print("Create your character:")
        self.character["name"] = " ".join(word.capitalize() for word in input("1- Name").split())
        self.character["species"] = " ".join(word.capitalize() for word in input("2- Species").split())
        self.character["gender"] = " ".join(word.capitalize() for word in input("3- Gender").split())
        self.character["level"] = 1
        self.character["scene"] = 1

    def start_new_game(self):
        print("Starting a new game...")
        selection = input("Enter a user name to save your progress or type '/b' to go back")
        if selection.lower() == "/b":
            print("Going back to menu...")
            self.main_menu()
        else:
            self.character["user_name"] = selection
            self.create_character()
            self.set_inventory()
            self.set_difficulty()
            self.load_story()
            print("Good luck on your journey!")
            print(f'Your character: {self.character["name"]}, {self.character["species"]}, {self.character["gender"]}')
            print(f'Your inventory: {self.character["snack"]}, {self.character["weapon"]}, {self.character["tool"]}')
            print(f'Difficulty: {self.character["difficulty"]}')
            self.play_game()

    def load_game(self):
        user_names = set()
        for file in os.listdir("game/saves/"):
            if ".json" in file:
                user_names.add(file.replace(".json", ""))
        if len(user_names) > 0:
            print("Choose your user name from the list:")
            for user_name in user_names:
                print(user_name)
            selection = input("Type your user name: ")
            if selection in user_names:
                file_name = f'game/saves/{selection}.json'
                file = open(file_name, 'r')
                game_data = json.load(file)
                self.character["name"] = game_data["char_attrs"]["name"]
                self.character["species"] = game_data["char_attrs"]["species"]
                self.character["gender"] = game_data["char_attrs"]["gender"]
                self.character["snack"] = game_data["inventory"]["snack"]
                self.character["weapon"] = game_data["inventory"]["weapon"]
                self.character["tool"] = game_data["inventory"]["tool"]
                self.character["lives"] = game_data["lives"]
                self.character["difficulty"] = game_data["difficulty"]
                self.character["level"] = game_data["level"]
                self.character["user_name"] = selection
                print("Loading your progress...")
                self.load_story()
                self.play_game()
            else:
                print("No save data found!")
                self.main_menu()
        else:
            print("No save data found!")
            self.main_menu()

    @staticmethod
    def quit():
        print("Goodbye!")
        exit(0)

    def launch_game(self):
        self.__init__()
        self.main_menu()

    def handle_outcome(self, level, scene, choice):
        outcome = self.game_file["outcomes"][f"lvl{level}"][f"scene{scene}"][f"outcome{choice}"]
        if "option1" in outcome:
            parsed_outcome = self.determine_option(outcome)
        else:
            parsed_outcome = self.parse_outcome(outcome)
        print(parsed_outcome[0])
        for result in parsed_outcome[1].split(" and "):
            if result == 'life+1':
                self.character["lives"] += 1
                print(f'You gained an extra life! Lives remaining: {self.character["lives"]}')
            if result == 'life-1':
                self.character["lives"] -= 1
                print(f'You died! Lives remaining: {self.character["lives"]}')
            if result == 'move':
                self.character["scene"] += 1
                if f'scene{self.character["scene"]}' not in self.game_file["story"][f"lvl{level}"]["scenes"]:
                    self.character["level"] += 1
                    self.character["scene"] = 1
                    self.save_game()
            if result == 'repeat':
                pass
            if 'inventory-' in result:
                inventory_item = re.search(r"inventory-'(\w+)'", str(result)).group(1)
                if inventory_item in self.character["inventory"]:
                    self.character["inventory"].remove(inventory_item)
                    print(f'An item has been removed from your inventory: {inventory_item}')
            if 'inventory+' in result:
                inventory_item = re.search(r"inventory\+'(\w+)'", str(result)).group(1)
                self.character["inventory"].add(inventory_item)
                print(f'A new item has been added to your inventory: {inventory_item}')

    def determine_option(self, outcome):
        parsed_outcome = {}
        for i in range(1, len(outcome) + 1):
            option = outcome[f"option{i}"]
            parsed_outcome = self.parse_outcome(option)
            parsed_results = self.parse_outcome_results(parsed_outcome[1])
            if "inventory-" in str(parsed_results):
                inventory_item = re.search(r"inventory-'(\w+)'", str(parsed_results)).group(1)
                if inventory_item in self.character["inventory"]:
                    break
        return parsed_outcome

    def parse_outcome(self, outcome):
        parsed_outcome = []
        results_search = re.search(r"\s\((.+)\)", str(outcome))
        results_to_remove = results_search.group()
        parsed_outcome.append(self.replace_character_fields_in_text(str(outcome).replace(results_to_remove, '')))
        parsed_outcome.append(results_search.group(1))
        return parsed_outcome

    @staticmethod
    def parse_outcome_results(results):
        outcome_results = set()
        for result in results.split(" and "):
            outcome_results.add(result)
        return outcome_results

    def save_game(self):
        save_json = {
            "char_attrs": {
                "name": self.character["name"],
                "species": self.character["species"],
                "gender": self.character["gender"]
            },
            "inventory": {
                "snack": self.character["snack"],
                "weapon": self.character["weapon"],
                "tool":  self.character["tool"]
            },
            "lives": self.character["lives"],
            "difficulty": self.character["difficulty"],
            "level": self.character["level"]
        }
        file_name = f'game/saves/{self.character["user_name"]}.json'
        with open(file_name, 'w') as outfile:
            json.dump(save_json, outfile)


AdventureGame().launch_game()
