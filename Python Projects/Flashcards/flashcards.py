import json
import os
import random
import sys


class Flashcards:

    def __init__(self):
        self.cards = {}
        self.cards_by_def = {}
        self.actions = {"add", "remove", "import", "export", "ask", "exit", "log", "hardest card", "reset stats"}
        self.session_stream = ""
        self.import_file_name = None
        self.export_file_name = None

    def add_card(self):
        self.log_print(f"The card:")
        while (term := input()) in self.cards.keys():
            self.log_print(f'The card "{term}" already exists.  Try again:')
        self.log_print(f"The definition of the card:")
        while (definition := input()) in self.cards_by_def.keys():
            self.log_print(f'The definition "{definition}" already exists. Try again')
        self.cards[term] = {}
        self.cards[term]["definition"] = definition
        self.cards[term]["mistakes"] = 0

        self.cards_by_def[definition] = term
        self.log_print(f'The pair ("{term}":"{definition}") has been added.')

    def remove_card(self):
        self.log_print("Which card?")
        term = input()
        if term in self.cards.keys():
            self.cards.pop(term)
            self.log_print("The card has been removed.")
        else:
            self.log_print(f'Can\'t remove "{term}": there is no such card.')

    def import_file(self, file_name):
        if os.path.exists(file_name):
            with open(file_name) as json_file:
                card_data = json.load(json_file)
                count = 0
                for term in card_data.keys():
                    count += 1
                    definition = card_data[term]["definition"]
                    mistakes = card_data[term]["mistakes"]
                    self.cards[term] = {}
                    self.cards[term]["definition"] = definition
                    self.cards[term]["mistakes"] = mistakes
                    self.cards_by_def[definition] = term
                self.log_print(f"{count} cards have been loaded.")
        else:
            self.log_print("File not found.")

    def import_cards(self):
        self.log_print("File name:")
        file_name = input()
        self.import_file(file_name)

    def export_file(self, file_name):
        with open(file_name, 'w') as json_file:
            json.dump(self.cards, json_file)
        self.log_print(f"{len(self.cards)} cards have been saved.")

    def export_cards(self):
        self.log_print("File name:")
        file_name = input()
        self.export_file(file_name)

    def ask(self):
        self.log_print("How many times to ask?")
        count = int(input())
        for i in range(0, count):
            term = random.choice(list(self.cards.keys()))
            self.log_print(f'Print the definition of "{term}":')
            answer = input()
            definition = self.cards[term]
            if answer == definition:
                self.log_print("Correct!")
            else:
                if answer in self.cards_by_def.keys():
                    self.log_print(
                        f'Wrong. The right answer is "{definition}", but your definition is correct for "{self.cards_by_def[answer]}".')
                else:
                    self.log_print(f'Wrong. The right answer is "{definition}".')
                self.cards[term]["mistakes"] += 1

    def log_print(self, text):
        self.session_stream += text
        print(text)
        
    def log(self):
        self.log_print("File name:")
        file_name = input()
        log_file = open(file_name, "w")
        log_file.write(self.session_stream)
        self.log_print("The log has been saved.")

    def hardest_card(self):
        max_term = ""
        max_mistakes = 0
        for term in self.cards.keys():
            mistakes = self.cards[term]["mistakes"]
            if mistakes > max_mistakes:
                max_term = term
                max_mistakes = mistakes
        if max_mistakes == 0:
            self.log_print(f"There are no cards with errors.")
        else:
            self.log_print(f'The hardest card is "{max_term}".  You have {max_mistakes} errors answering it.')

    def reset_stats(self):
        for term in self.cards.keys():
            self.cards[term]["mistakes"] = 0
        self.log_print(f"Card statistics have been reset.")

    def parse_args(self):
        for arg in sys.argv:
            name_value = arg.split("=")
            if name_value[0] == '--import_from':
                self.import_file_name = name_value[1]
            if name_value[0] == '--export_to':
                self.export_file_name = name_value[1]
        if self.import_file_name is not None:
            self.import_file(self.import_file_name)

    def manage_flashcards(self):
        self.parse_args()
        while True:
            self.log_print(f"\nInput the action (add, remove, import, export, ask, exit, log, hardest card, reset stats)")
            while (action := input().lower()) not in self.actions:
                self.log_print("Invalid action.  Try again.")
            if action == "add":
                self.add_card()
            elif action == "remove":
                self.remove_card()
            elif action == "import":
                self.import_cards()
            elif action == "export":
                self.export_cards()
            elif action == "ask":
                self.ask()
            elif action == "exit":
                self.log_print("Bye bye!")
                if self.export_file_name is not None:
                    self.export_file(self.export_file_name)
                exit(0)
            elif action == "log":
                self.log()
            elif action == "hardest card":
                self.hardest_card()
            elif action == "reset stats":
                self.reset_stats()


Flashcards().manage_flashcards()

