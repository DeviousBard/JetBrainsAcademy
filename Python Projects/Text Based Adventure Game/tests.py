from random import choice
from re import search
from hstest import *
import json
import os


class TextBasedAdventureGameTest(StageTest):
    username = "new_user"
    name = "john"
    species = "human"
    gender = "male"
    snack = "apple"
    weapon = "sword"
    tool = "rope"
    diff_easy = "easy"
    diff_medium = "medium"
    diff_hard = "hard"
    lives = "5"
    picked_choice = ""

    username2 = "CakeIsALie"
    name2 = "jane"
    gender2 = "female"
    snack2 = "cake"
    weapon2 = "spear"
    tool2 = "jetpack"
    picked_choice2 = ""

    @dynamic_test
    def test1(self):
        main = TestedProgram()
        output = main.start()
        return self.check_welcome(output)

    @dynamic_test
    def test2(self):
        main = TestedProgram()
        output = main.start()
        message = "1- Press key '1' or type 'start' to start a new game"
        if message.lower() not in output.lower():
            return CheckResult.wrong(f"You didn't output: '{message}' in your menu.")

        if main.is_waiting_input():
            output = main.execute("1")
            feedback = "Your program couldn't process input '1' to start a new game! Make sure to output 'Starting a new game...'."
            return self.check_start_new(output, feedback)
        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test3(self):
        main = TestedProgram()
        main.start()

        if main.is_waiting_input():
            output = main.execute("start")
            feedback = "Your program couldn't process the input 'start' to start a new game! Make sure to output 'Starting a new game...'."
            return self.check_start_new(output, feedback)
        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test4(self):
        main = TestedProgram()
        main.start()

        if main.is_waiting_input():
            output = main.execute("StARt")
            feedback = "Your program shouldn't be case sensitive when starting a new game!"
            return self.check_start_new(output, feedback)
        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test5(self):
        main = TestedProgram()
        output = main.start()
        message = "2- Press key '2' or type 'load' to load your progress"
        if message.lower() not in output.lower():
            return CheckResult.wrong(f"You didn't output: '{message}' in your menu.")

        if main.is_waiting_input():
            output = main.execute("2")
            feedback = "Your program couldn't process input '2' to load a game! Make sure to say 'No save data found!' if there is no save file."
            return self.check_start_load(output, feedback)
        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test6(self):
        main = TestedProgram()
        main.start()

        if main.is_waiting_input():
            output = main.execute("load")
            feedback = "Your program couldn't process input 'load' to load a game! Make sure to say 'No save data found!' if there is no save file."
            return self.check_start_load(output, feedback)
        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test7(self):
        main = TestedProgram()
        main.start()

        if main.is_waiting_input():
            output = main.execute("lOAd")
            feedback = "Your program shouldn't be case sensitive when loading a game!."
            return self.check_start_load(output, feedback)
        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test8(self):
        main = TestedProgram()
        main.start()

        if main.is_waiting_input():
            output = main.execute("5")
            if main.is_waiting_input():
                return self.check_unknown(output)
            return CheckResult.wrong("Your program didn't ask for another input after an unknown input!")
        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test9(self):
        main = TestedProgram()
        output = main.start()
        message = "3- Press key '3' or type 'quit' to quit the game"
        if message.lower() not in output.lower():
            return CheckResult.wrong(f"You didn't output: '{message}' in your menu.")

        if main.is_waiting_input():
            output = main.execute("3")
            if main.is_finished():
                feedback = "Your program didn't output 'Goodbye!' before you exit with '3' as input!"
                return self.check_quit(output, feedback)
            return CheckResult.wrong("Your program should end with input '3'!")
        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test10(self):
        main = TestedProgram()
        main.start()

        if main.is_waiting_input():
            output = main.execute("quIt")
            if main.is_finished():
                feedback = "Your program didn't output 'Goodbye!' before you exit with 'quIt' as input! Your program must be case insensitive!"
                return self.check_quit(output, feedback)
            return CheckResult.wrong(
                "Your program should end with input 'quIt'! Your program must be case insensitive!")
        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test11(self):
        main = TestedProgram()
        main.start()

        if main.is_waiting_input():
            output = main.execute("quit")
            if main.is_finished():
                feedback = "Your program didn't output 'Goodbye!' before you exit with 'quit' as input!"
                return self.check_quit(output, feedback)
            return CheckResult.wrong("Your program should end with input 'quit'!")
        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test12(self):
        main = TestedProgram()
        main.start()
        output = main.execute("1")

        if "/b" not in output.lower():
            return CheckResult.wrong("Show the user that they can enter '/b' to go back!")
        elif "enter a user name" not in output.lower():
            return CheckResult.wrong("Tell the user to 'Enter a user name'!")

        if main.is_waiting_input():
            output = main.execute("/b").lower()

            if "create your character" in output:
                return CheckResult.wrong(
                    "You didn't process the right command for going back which is '/b'!")

            if "going back to menu" not in output:
                return CheckResult.wrong(
                    "You didn't output the correct message when going back to menu. Make sure the output contains 'Going back to menu...'")

            if not main.is_waiting_input():
                return CheckResult.wrong("Your program should continue working in the main menu")

            return self.check_welcome(output, feedback="You should output the same welcome message with the menu!")

        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test13(self):
        main = TestedProgram()
        main.start()
        if main.is_waiting_input():
            main.execute("1")
            if main.is_waiting_input():
                output = main.execute(self.username).lower()
                if "create your character" not in output:
                    return CheckResult.wrong("You should print 'Create your character:'!")
                elif "name" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask the name of the character!")
                output = main.execute(self.name).lower()
                if "species" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask the species of the character!")
                output = main.execute(self.species).lower()
                if "gender" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask the gender of the character!")
                output = main.execute(self.gender).lower()
                if "pack your bag" not in output:
                    return CheckResult.wrong("You should print 'Pack your bag for the journey:'!")
                elif "snack" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask for the favourite snack for the inventory!")
                output = main.execute(self.snack).lower()
                if "weapon" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask for a weapon for the inventory!")
                output = main.execute(self.weapon).lower()
                if "tool" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask for a traversal tool for the inventory!")
                output = main.execute(self.tool).lower()
                difficulties = ["easy", "hard", "medium"]
                have_difficulty = all([difficulty in output for difficulty in difficulties])
                if "choose your difficulty" not in output:
                    return CheckResult.wrong("You should print 'Choose your difficulty:'!")
                elif not have_difficulty and main.is_waiting_input():
                    return CheckResult.wrong("You should print all the difficulty options!")
                output = main.execute("insane").lower()
                if "unknown input! please enter a valid one" not in output and main.is_waiting_input():
                    return CheckResult.wrong(
                        "Your program should handle unknown input like before here! Make sure to say 'Unknown input! Please enter a valid one'. ")
                output = main.execute(self.diff_easy).lower()
                if "unknown input! please enter a valid one" in output and main.is_waiting_input():
                    return CheckResult.wrong(
                        "The user should be able to type the difficulty or enter the number of the difficulty!")
                elif "good luck on your journey" not in output:
                    return CheckResult.wrong("You should print 'Good luck on your journey!'")
                elif "your character" not in output:
                    return CheckResult.wrong("You should print 'Your character:' before the character traits.")
                elif self.name not in output:
                    return CheckResult.wrong("You should print the name of the character.")
                elif self.species not in output:
                    return CheckResult.wrong("You should print the  gender of the character.")
                elif self.gender not in output:
                    return CheckResult.wrong("You should print the gender of the character.")
                elif "your inventory" not in output:
                    return CheckResult.wrong("You should print 'Your inventory:' before the inventory items.")
                elif self.snack not in output:
                    return CheckResult.wrong("You should print the snack from the inventory.")
                elif self.weapon not in output:
                    return CheckResult.wrong("You should print the weapon from the inventory.")
                elif self.tool not in output:
                    return CheckResult.wrong("You should print the tool from the inventory.")
                elif "difficulty" not in output:
                    return CheckResult.wrong("You should print 'Difficulty:' before the difficulty.")
                elif self.diff_easy not in output:
                    return CheckResult.wrong("You should print the difficulty.")
                return CheckResult.correct()

            return CheckResult.wrong("Your program didn't ask for the user name!")
        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test13_1(self):
        main = TestedProgram()
        main.start()
        if main.is_waiting_input():
            main.execute("1")
            if main.is_waiting_input():
                output = main.execute(self.username).lower()
                if "create your character" not in output:
                    return CheckResult.wrong("You should print 'Create your character:'!")
                elif "name" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask the name of the character!")
                output = main.execute(self.name).lower()
                if "species" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask the species of the character!")
                output = main.execute(self.species).lower()
                if "gender" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask the gender of the character!")
                output = main.execute(self.gender).lower()
                if "pack your bag" not in output:
                    return CheckResult.wrong("You should print 'Pack your bag for the journey:'!")
                elif "snack" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask for the favourite snack for the inventory!")
                output = main.execute(self.snack).lower()
                if "weapon" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask for a weapon for the inventory!")
                output = main.execute(self.weapon).lower()
                if "tool" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask for a traversal tool for the inventory!")
                output = main.execute(self.tool).lower()
                difficulties = ["easy", "hard", "medium"]
                have_difficulty = all([difficulty in output for difficulty in difficulties])
                if "choose your difficulty" not in output:
                    return CheckResult.wrong("You should print 'Choose your difficulty:'!")
                elif not have_difficulty and main.is_waiting_input():
                    return CheckResult.wrong("You should print all the difficulty options!")
                output = main.execute("insane").lower()
                if "unknown input! please enter a valid one" not in output and main.is_waiting_input():
                    return CheckResult.wrong(
                        "Your program should handle unknown input like before here! Make sure to say 'Unknown input! Please enter a valid one'. ")
                output = main.execute(self.diff_medium).lower()
                if "unknown input! please enter a valid one" in output and main.is_waiting_input():
                    return CheckResult.wrong(
                        f"The user should be able to type the difficulty: '{self.diff_medium}' or enter the number of the difficulty!")
                elif "good luck on your journey" not in output:
                    return CheckResult.wrong("You should print 'Good luck on your journey!'")
                elif "your character" not in output:
                    return CheckResult.wrong("You should print 'Your character:' before the character traits.")
                elif self.name not in output:
                    return CheckResult.wrong("You should print the name of the character.")
                elif self.species not in output:
                    return CheckResult.wrong("You should print the  gender of the character.")
                elif self.gender not in output:
                    return CheckResult.wrong("You should print the gender of the character.")
                elif "your inventory" not in output:
                    return CheckResult.wrong("You should print 'Your inventory:' before the inventory items.")
                elif self.snack not in output:
                    return CheckResult.wrong("You should print the snack from the inventory.")
                elif self.weapon not in output:
                    return CheckResult.wrong("You should print the weapon from the inventory.")
                elif self.tool not in output:
                    return CheckResult.wrong("You should print the tool from the inventory.")
                elif "difficulty" not in output:
                    return CheckResult.wrong("You should print 'Difficulty:' before the difficulty.")
                elif self.diff_medium not in output:
                    return CheckResult.wrong("You should print the difficulty.")
                return CheckResult.correct()

            return CheckResult.wrong("Your program didn't ask for the user name!")
        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test13_2(self):
        main = TestedProgram()
        main.start()
        if main.is_waiting_input():
            main.execute("1")
            if main.is_waiting_input():
                output = main.execute(self.username).lower()
                if "create your character" not in output:
                    return CheckResult.wrong("You should print 'Create your character:'!")
                elif "name" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask the name of the character!")
                output = main.execute(self.name).lower()
                if "species" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask the species of the character!")
                output = main.execute(self.species).lower()
                if "gender" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask the gender of the character!")
                output = main.execute(self.gender).lower()
                if "pack your bag" not in output:
                    return CheckResult.wrong("You should print 'Pack your bag for the journey:'!")
                elif "snack" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask for the favourite snack for the inventory!")
                output = main.execute(self.snack).lower()
                if "weapon" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask for a weapon for the inventory!")
                output = main.execute(self.weapon).lower()
                if "tool" not in output and main.is_waiting_input():
                    return CheckResult.wrong("You should ask for a traversal tool for the inventory!")
                output = main.execute(self.tool).lower()
                difficulties = ["easy", "hard", "medium"]
                have_difficulty = all([difficulty in output for difficulty in difficulties])
                if "choose your difficulty" not in output:
                    return CheckResult.wrong("You should print 'Choose your difficulty:'!")
                elif not have_difficulty and main.is_waiting_input():
                    return CheckResult.wrong("You should print all the difficulty options!")
                output = main.execute("insane").lower()
                if "unknown input! please enter a valid one" not in output and main.is_waiting_input():
                    return CheckResult.wrong(
                        "Your program should handle unknown input like before here! Make sure to say 'Unknown input! Please enter a valid one'. ")
                output = main.execute(self.diff_hard).lower()
                if "unknown input! please enter a valid one" in output and main.is_waiting_input():
                    return CheckResult.wrong(
                        f"The user should be able to type the difficulty: '{self.diff_hard}' or enter the number of the difficulty!")
                elif "good luck on your journey" not in output:
                    return CheckResult.wrong("You should print 'Good luck on your journey!'")
                elif "your character" not in output:
                    return CheckResult.wrong("You should print 'Your character:' before the character traits.")
                elif self.name not in output:
                    return CheckResult.wrong("You should print the name of the character.")
                elif self.species not in output:
                    return CheckResult.wrong("You should print the  gender of the character.")
                elif self.gender not in output:
                    return CheckResult.wrong("You should print the gender of the character.")
                elif "your inventory" not in output:
                    return CheckResult.wrong("You should print 'Your inventory:' before the inventory items.")
                elif self.snack not in output:
                    return CheckResult.wrong("You should print the snack from the inventory.")
                elif self.weapon not in output:
                    return CheckResult.wrong("You should print the weapon from the inventory.")
                elif self.tool not in output:
                    return CheckResult.wrong("You should print the tool from the inventory.")
                elif "difficulty" not in output:
                    return CheckResult.wrong("You should print 'Difficulty:' before the difficulty.")
                elif self.diff_hard not in output:
                    return CheckResult.wrong("You should print the difficulty.")
                return CheckResult.correct()

            return CheckResult.wrong("Your program didn't ask for the user name!")
        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test14(self):

        main = TestedProgram()
        main.start()
        if main.is_waiting_input():
            main.execute("1")
            main.execute(self.username)
            main.execute(self.name)
            main.execute(self.species)
            main.execute(self.gender)
            main.execute(self.snack)
            main.execute(self.weapon)
            main.execute(self.tool)
            output = main.execute(self.diff_easy)
            if "what will you do? type the number of the option or type '/h' to show help" not in output.lower():
                return CheckResult.wrong(
                    "You should show the actions  with this message: 'What will you do? Type the number of the option or type '/h' to show help.' ")
            elif "1-" not in output or "2-" not in output or "3-" not in output:
                return CheckResult.wrong("You should number the options: '1-', so that the player can easily type it!")
            while True:
                choices = ["1", "2", "3"]
                random_choice = choice(choices)
                if search("({.*)|(\(.*\))", output.lower()):
                    return CheckResult.wrong(
                        "Words wrapped in brackets were found in your output: '{},()'. These words with brackets are not part of story! ")
                if "level 2" in output.lower() and not main.is_finished():
                    return CheckResult.correct()
                elif "game over" in output.lower() and "***welcome" in output.lower() and main.is_waiting_input():
                    main.execute("start")
                    main.execute(self.username)
                    main.execute(self.name)
                    main.execute(self.species)
                    main.execute(self.gender)
                    main.execute(self.snack)
                    main.execute(self.weapon)
                    main.execute(self.tool)
                    main.execute(self.diff_easy)

                if "level 2" in output.lower() and main.is_finished() and "goodbye" in output.lower():
                    return CheckResult.wrong(
                        "Your game should resume from Level 2 after reaching it!")
                elif "level 2" in output.lower() and "goodbye" in output.lower():
                    return CheckResult.wrong("You shouldn't output 'Goodbye!' after Level 2!")

                if "game over" in output.lower() and main.is_finished():
                    return CheckResult.wrong(
                        "Your program should not end after game over! It should return back to menu.")
                elif "game over" in output.lower() and "***welcome" not in output.lower():
                    return CheckResult.wrong(
                        "You should print the welcome message with the menu again after game over!")
                elif "game over" in output.lower() and not main.is_waiting_input():
                    return CheckResult.wrong("Your program wait for an input in the menu after game over!")

                if "you died" in output.lower() and "level 1" not in output.lower() and "game over" not in output.lower():
                    return CheckResult.wrong(
                        "Your program didn't start from the beginning of the level. Make sure you output the number of the level: 'Level 1' ")

                if "what will you do? type the number of the option or type '/h' to show help" not in output.lower():
                    choices.pop(choices.index(self.picked_choice))
                    self.picked_choice = choice(choices)
                    output = main.execute(self.picked_choice)

                else:
                    self.picked_choice = random_choice
                    output = main.execute(self.picked_choice)

        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test15(self):
        main = TestedProgram()
        main.start()

        if main.is_waiting_input():
            main.execute("1")
            main.execute(self.username)
            main.execute(self.name)
            main.execute(self.species)
            main.execute(self.gender)
            main.execute(self.snack)
            main.execute(self.weapon)
            main.execute(self.tool)
            main.execute(self.diff_easy)
            output = main.execute("/h")
            return self.check_help(output)

        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test16(self):
        main = TestedProgram()
        main.start()

        if main.is_waiting_input():
            main.execute("1")
            main.execute(self.username)
            main.execute(self.name)
            main.execute(self.species)
            main.execute(self.gender)
            main.execute(self.snack)
            main.execute(self.weapon)
            main.execute(self.tool)
            main.execute(self.diff_easy)
            output = main.execute("/i")
            return self.check_inventory(output)

        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test17(self):
        main = TestedProgram()
        main.start()

        if main.is_waiting_input():
            main.execute("1")
            main.execute(self.username)
            main.execute(self.name)
            main.execute(self.species)
            main.execute(self.gender)
            main.execute(self.snack)
            main.execute(self.weapon)
            main.execute(self.tool)
            main.execute(self.diff_easy)
            output = main.execute("/c")
            return self.check_char(output)

        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test18(self):
        main = TestedProgram()
        main.start()

        if main.is_waiting_input():
            main.execute("1")
            main.execute(self.username)
            main.execute(self.name)
            main.execute(self.species)
            main.execute(self.gender)
            main.execute(self.snack)
            main.execute(self.weapon)
            main.execute(self.tool)
            main.execute(self.diff_easy)
            output = main.execute("/q")

            if main.is_waiting_input():
                if "you sure you want to quit the game? y/n" not in output.lower() or "unknown input! please enter a valid one" in output.lower():
                    return CheckResult.wrong(
                        "You didn't ask to quit the game! Make sure you output: 'You sure you want to quit the game? Y/N'")
                output = main.execute("n")
                if "goodbye!" in output.lower() or main.is_finished():
                    return CheckResult.wrong(
                        "If the player chooses not to quit, you should resume the game! Make sure your program is not case sensitive!")
                main.execute("/q")
                output = main.execute("y")
                if "goodbye!" not in output.lower():
                    return CheckResult.wrong(
                        "Your didn't output: 'Goodbye!' before stopping the program! Make sure your program is not case sensitive!")
                elif main.is_finished():
                    return CheckResult.correct()
            return CheckResult.wrong("You didn't ask to quit the game!")

        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test19(self):
        main = TestedProgram()
        main.start()

        if main.is_waiting_input():
            main.execute("1")
            main.execute(self.username)
            main.execute(self.name)
            main.execute(self.species)
            main.execute(self.gender)
            main.execute(self.snack)
            main.execute(self.weapon)
            main.execute(self.tool)
            main.execute(self.diff_easy)
            output = main.execute("5")
            if "unknown input! please enter a valid one" not in output.lower() or not main.is_waiting_input():
                return CheckResult.wrong(
                    "Your program couldn't process unknown input. Make sure to say 'Unknown input! Please enter a valid one' .")
            return CheckResult.correct()
        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test20(self):
        main = TestedProgram()
        main.start()
        return self.check_save()

    @dynamic_test
    def test21(self):

        main = TestedProgram()
        main.start()
        if main.is_waiting_input():
            output = main.execute("2")
            if not main.is_waiting_input():
                return CheckResult.wrong("You should ask for the user name!")
            elif "type your user name" not in output.lower():
                return CheckResult.wrong("You should output: 'Type your user name from the list:'")
            elif self.username not in output.lower():
                return CheckResult.wrong(
                    f"You should output the list of users who have a save file! Your output doesn't contain this user: '{self.username}' !")
            output = main.execute("neu_use")
            if "no save data found" not in output.lower():
                return CheckResult.wrong(
                    "You should handle typos from user, output: 'No save data found!' and go back to menu.")
            elif "***welcome" not in output.lower():
                return CheckResult.wrong("You should go back to menu after a typo made by the user!")
            main.execute("2")
            output = main.execute(self.username)

            if "what will you do? type the number of the option or type '/h' to show help" not in output.lower():
                return CheckResult.wrong(
                    "You should show the actions with this message: 'What will you do? Type the number of the option or type '/h' to show help.' ")
            elif "1-" not in output or "2-" not in output or "3-" not in output:
                return CheckResult.wrong("You should number the options: '1-', so that the player can easily type it!")

            while True:
                choices = ["1", "2", "3"]
                random_choice = choice(choices)
                if search("({.*)|(\(.*\))", output.lower()):
                    return CheckResult.wrong(
                        "Words wrapped in brackets were found in your output: '{},()'. These words with brackets are not part of story! ")
                if "congratulations! you beat the game" in output.lower() and main.is_finished() and "goodbye" in output.lower():
                    return CheckResult.correct()

                if "congratulations! you beat the game" in output.lower() and not main.is_finished() and "goodbye" in output.lower():
                    return CheckResult.wrong(
                        "You should end the program directly after the player wins the game!")
                elif "congratulations! you beat the game" in output.lower() and "goodbye" not in output.lower():
                    return CheckResult.wrong("You should output 'Goodbye!' before ending the program!")

                if "game over" in output.lower() and "***welcome" in output.lower() and not main.is_finished():
                    return CheckResult.correct()

                elif "game over" in output.lower() and main.is_finished():
                    return CheckResult.wrong(
                        "Your program should not end after game over! It should return back to menu.")
                elif "game over" in output.lower() and "***welcome" not in output.lower():
                    return CheckResult.wrong(
                        "You should print the welcome message with the menu again after game over!")
                elif "game over" in output.lower() and not main.is_waiting_input():
                    return CheckResult.wrong("Your program should wait for an input in the menu after game over!")

                if "you died" in output.lower() and "level 2" not in output.lower() and "game over" not in output.lower():
                    return CheckResult.wrong(
                        "Your program didn't start from the beginning of the level. Make sure you output the number of the level: 'Level 2' ")

                if "what will you do? type the number of the option or type '/h' to show help" not in output.lower():
                    choices.pop(choices.index(self.picked_choice))
                    self.picked_choice = choice(choices)
                    output = main.execute(self.picked_choice)

                else:
                    self.picked_choice = random_choice
                    output = main.execute(self.picked_choice)

        return CheckResult.wrong("Your program didn't ask for input!")

    @dynamic_test
    def test22(self):
        save_dict = {
            "char_attrs": {"name": self.name2.title(), "species": self.species.title(), "gender": self.gender2.title()},
            "inventory": {"snack": self.snack2.title(), "weapon": self.weapon2.title(), "tool": self.tool2.title()},
            "difficulty": self.diff_easy.title(),
            "lives": int(self.lives),
            "level": 2
        }
        if not os.path.exists(f"./game/saves/{self.username}.json"):
            raise WrongAnswer("Make sure your program saves save files with the following path:\n"
                              "\'game\\saves\\N.json\',\n"
                              "where N is the username.")
        with open(f"./game/saves/{self.username2}.json", "w") as f_json:
            json.dump(save_dict, f_json)

        main = TestedProgram()
        main.start()
        output = main.execute("2")
        if main.is_waiting_input():
            if "type your user name" in output.lower() and self.username not in output.lower():
                return CheckResult.wrong(
                    f"You should output the list of users who have a save file! Your output doesn't contain this user: '{self.username}' !")
            elif "type your user name" in output.lower() and self.username2.lower() not in output.lower():
                return CheckResult.wrong(
                    f"You should output the list of users who have a save file! Your output doesn't contain this user: '{self.username2}'!")
            output = main.execute(self.username2)
            if "level 2" not in output.lower() or "what will you do? type the number of the option or type '/h' to show help" not in output.lower():
                return CheckResult.wrong(
                    f"The player, '{self.username2}' should be able to resume the game from Level 2!")
            return CheckResult.correct()
        return CheckResult.wrong("You should ask for the user name!")

    def check_welcome(self, output, feedback=""):
        if "welcome to" in output.lower() and "***" in output:
            return CheckResult.correct()
        return CheckResult.wrong(
            feedback or "Your welcome message doesn't include the following: ***Welcome to <game-title>***' !")

    def check_start_new(self, output, feedback):
        if "starting a new game" in output.lower():
            return CheckResult.correct()
        return CheckResult.wrong(feedback)

    def check_start_load(self, output, feedback):
        if "type your user name" in output.lower() or "no save data found" in output.lower():
            return CheckResult.correct()
        return CheckResult.wrong(feedback)

    def check_unknown(self, output):
        if "unknown input! please enter a valid one" in output.lower():
            return CheckResult.correct()
        return CheckResult.wrong(
            "Your program couldn't process unknown input. Make sure to say 'Unknown input! Please enter a valid one'. ")

    def check_quit(self, output, feedback):
        if "goodbye" in output.lower():
            return CheckResult.correct()
        return CheckResult.wrong(feedback)

    def check_help(self, output):
        message = "type the number of the option you want to choose.\n" + "commands you can use:\n/i => shows inventory.\n" \
                  + "/q => exits the game.\n" + "/c => shows the character traits.\n" + "/h => shows help."
        if message in output.lower():
            return CheckResult.correct()

        else:
            return CheckResult.wrong(
                "Your program didn't output the correct help message. Make sure to output:\n'" + message + "'")

    def check_inventory(self, output):
        inventory = [self.snack, self.weapon, self.tool]
        in_inventory = all([item in output.lower() for item in inventory])

        if "inventory" not in output.lower():
            return CheckResult.wrong("Your program didn't output the title: 'Inventory:'")
        elif inventory[0] not in output.lower():
            return CheckResult.wrong("The snack is missing from the inventory!")
        elif inventory[1] not in output.lower():
            return CheckResult.wrong("The weapon is missing from the inventory!")
        elif inventory[2] not in output.lower():
            return CheckResult.wrong("The tool is missing from the inventory!")
        elif not in_inventory:
            return CheckResult.wrong("The inventory is empty!")
        else:
            return CheckResult.correct()

    def check_char(self, output):
        char = [self.name, self.species, self.gender, self.lives]
        in_char = all([ch in output.lower() for ch in char])

        if "character" not in output.lower():
            return CheckResult.wrong("Your program didn't output the title: 'Your character:'")
        elif "lives remaining" not in output.lower():
            return CheckResult.wrong("Your program didn't output the title: 'Lives remaining:'")
        elif char[0] not in output.lower():
            return CheckResult.wrong("The name is missing from the character traits!")
        elif char[1] not in output.lower():
            return CheckResult.wrong("The species is missing from the character traits!")
        elif char[2] not in output.lower():
            return CheckResult.wrong("The gender is missing from the character traits!")
        elif char[3] not in output.lower():
            return CheckResult.wrong("The life count is missing from remaining lives!")
        elif not in_char:
            return CheckResult.wrong("The character traits are empty!")
        else:
            return CheckResult.correct()

    def check_save(self):
        try:
            import os

            path = ''
            for curr_path in f"game/saves/{self.username}.json", f"saves/{self.username}.json":
                if os.path.exists(curr_path):
                    path = curr_path
                    break

            if not path:
                return CheckResult.wrong(
                    f"Save file doesn't exist. "
                    f"Should be located at \"saves{os.sep}{self.username}.json\"")

            with open(path) as f_json:
                save_file = json.load(f_json)

                if "char_attrs" not in save_file:
                    return CheckResult.wrong(
                        "Save file doesn't contain the 'char_attrs' key.")
                elif "name" not in save_file["char_attrs"]:
                    return CheckResult.wrong(
                        "Save file doesn't contain the 'name' key in char_attrs.")
                elif "species" not in save_file["char_attrs"]:
                    return CheckResult.wrong(
                        "Save file doesn't contain the 'species' key in char_attrs.")
                elif "gender" not in save_file["char_attrs"]:
                    return CheckResult.wrong(
                        "Save file doesn't contain the 'gender' key in char_attrs.")
                elif "inventory" not in save_file:
                    return CheckResult.wrong(
                        "Save file doesn't contain the 'inventory' key.")
                elif "snack" not in save_file["inventory"]:
                    return CheckResult.wrong(
                        "Save file doesn't contain the 'snack' key in inventory.")
                elif "weapon" not in save_file["inventory"]:
                    return CheckResult.wrong(
                        "Save file doesn't contain the 'weapon' key in inventory.")
                elif "tool" not in save_file["inventory"]:
                    return CheckResult.wrong(
                        "Save file doesn't contain the 'tool' key in inventory.")
                elif "difficulty" not in save_file:
                    return CheckResult.wrong(
                        "Save file doesn't contain the 'difficulty' key.")
                elif "lives" not in save_file:
                    return CheckResult.wrong(
                        "Save file doesn't contain the 'lives' key.")
                elif "level" not in save_file:
                    return CheckResult.wrong(
                        "Save file doesn't contain the 'level' key.")
                elif save_file["char_attrs"]["name"].lower() != self.name:
                    return CheckResult.wrong(
                        "Save file doesn't contain the correct name of the character: " + self.name.title())
                elif save_file["char_attrs"]["species"].lower() != self.species:
                    return CheckResult.wrong(
                        "Save file doesn't contain the correct species of the character: " + self.species.title())
                elif save_file["char_attrs"]["gender"].lower() != self.gender:
                    return CheckResult.wrong(
                        "Save file doesn't contain the correct gender of the character: " + self.gender.title())
                elif save_file["inventory"]["snack"].lower() != self.snack:
                    return CheckResult.wrong(
                        "Save file doesn't contain the correct snack in the inventory: " + self.snack.title())
                elif save_file["inventory"]["weapon"].lower() != self.weapon:
                    return CheckResult.wrong(
                        "Save file doesn't contain the correct weapon in the inventory: " + self.weapon.title())
                elif save_file["inventory"]["tool"].lower() != self.tool:
                    return CheckResult.wrong(
                        "Save file doesn't contain the correct tool in the inventory: " + self.tool.title())
                elif save_file["difficulty"].lower() != self.diff_easy:
                    return CheckResult.wrong(
                        "Save file doesn't contain the correct difficulty: " + self.diff_easy.title())
                elif save_file["lives"] is None:
                    return CheckResult.wrong("Save file doesn't contain any life count.")
                elif save_file["level"] != 2:
                    return CheckResult.wrong("Save file doesn't contain the correct level count: 2")
                else:
                    return CheckResult.correct()

        except (TypeError, IndexError, FileNotFoundError):
            return CheckResult.wrong("Save file doesn't exist with the given player name.")


if __name__ == '__main__':
    TextBasedAdventureGameTest('game.game').run_tests()