class CoffeeMachine:
    def __init__(self):
        self.stock = {
            "water": 400,
            "milk": 540,
            "coffee_beans": 120,
            "disposable_cups": 9
        }
        self.drinks = {
            "espresso": {"water": 250, "milk": 0, "coffee_beans": 16, "disposable_cups": 1, "price": 4},
            "latte": {"water": 350, "milk": 75, "coffee_beans": 20, "disposable_cups": 1, "price": 7},
            "cappuccino": {"water": 200, "milk": 100, "coffee_beans": 12, "disposable_cups": 1, "price": 6}
        }
        self.stock_items = ["water", "milk", "coffee_beans", "disposable_cups"]
        self.money = 550

    def fill(self, stock_item, amount):
        self.__adjust_stock_amount__(stock_item, amount)

    def status(self):
        print()
        print("The coffee machine has:")
        for stock_item in self.stock_items:
            print(f"{self.stock[stock_item]} of {stock_item.replace('_', ' ')}")
        print(f"{self.money} of money")
        print()

    def buy(self, coffee_type):
        can_buy = True
        for stock_item in self.stock_items:
            if not self.__has_enough_stock__(coffee_type, stock_item):
                can_buy = False
                print(f"Sorry, not enough {stock_item.replace('_', ' ')}")
        if can_buy:
            for stock_item in self.stock_items:
                self.__adjust_stock_amount__(stock_item, (self.drinks[coffee_type][stock_item] * -1))
            self.money += self.drinks[coffee_type]["price"]
            print("I have enough resources, making you a coffee!")

    def take(self):
        print(f"I gave you ${self.money}")
        print()
        self.money = 0

    def __adjust_stock_amount__(self, stock_item, amount):
        self.stock[stock_item] += amount

    def __has_enough_stock__(self, coffee_type, stock_item):
        amount = self.stock[stock_item] - self.drinks[coffee_type][stock_item]
        return amount >= 0


def max_cups(water, milk, coffee):
    return int(min([water / 200, milk / 50, coffee / 15]))


def can_make_cups(water, milk, coffee, cups):
    if cups * 200 <= water and cups * 50 <= milk and cups * 15 <= coffee:
        return True
    return False


def process_input(coffee_machine):
    print("Write action (buy, fill, take, remaining, exit):")
    action = input()
    if action == 'buy':
        print("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:")
        choice = input()
        if choice == '1':
            coffee_machine.buy("espresso")
        if choice == '2':
            coffee_machine.buy("latte")
        if choice == '3':
            coffee_machine.buy("cappuccino")
    elif action == 'fill':
        print("Write how many ml of water do you want to add:")
        water = int(input())
        coffee_machine.fill("water", water)
        print("Write how many ml of milk do you want to add:")
        milk = int(input())
        coffee_machine.fill("milk", milk)
        print("Write how many grams of coffee beans do you want to add:")
        coffee_beans = int(input())
        coffee_machine.fill("coffee_beans", coffee_beans)
        print("Write how many disposable cups of coffee do you want to add:")
        cups = int(input())
        coffee_machine.fill("disposable_cups", cups)
        print()
    elif action == 'take':
        coffee_machine.take()
    elif action == 'remaining':
        coffee_machine.status()
    return action


def main():
    coffee_machine = CoffeeMachine()
    while True:
        action = process_input(coffee_machine)
        if action == 'exit':
            break


main()
