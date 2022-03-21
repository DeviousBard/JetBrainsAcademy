import random
import sqlite3


# noinspection SqlNoDataSourceInspection
class Bank:
    conn = sqlite3.connect('card.s3db')
    # accounts = {}
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(Bank, cls).__new__(cls)
        return cls._instance

    def __is_available_card_number__(self, card_number):
        cur = self._instance.conn.cursor()
        cur.execute(f"select id from card where number = {card_number}")
        row = cur.fetchone()
        cur.close()
        # return card_number not in self._instance.accounts
        return not row

    def open_account(self):
        card_number = None
        while not card_number or not self._instance.__is_available_card_number__(card_number):
            bank_id_num = '400000'
            account_number = random.randint(0, 999999999)
            card_number_15 = bank_id_num + str(account_number).zfill(9)
            luhn = self._instance.__calculate_luhn__(card_number_15)
            card_number = card_number_15 + str(luhn)
        pin = self._instance.__generate_pin__()
        credit_card = CreditCard(card_number, pin)
        self._instance.__create_account__(credit_card)
        return credit_card

    def __calculate_luhn__(self, card_number_15):
        luhn_alg = []
        for idx, num in enumerate([int(x) for x in card_number_15], start=1):
            if idx % 2 == 1:
                luhn_alg.append(num * 2 if num * 2 < 10 else num * 2 - 9)
            else:
                luhn_alg.append(num)
        sum_mod_10 = sum(luhn_alg) % 10
        return 0 if sum_mod_10 == 0 else 10 - sum_mod_10

    def __generate_pin__(self):
        return str(random.randint(0, 9999)).zfill(4)

    def __create_account__(self, credit_card):
        new_id = random.randint(0, 9999999)
        self._instance.conn.execute(
            f"insert into card (id, number, pin) "
            f"values ({new_id}, {credit_card.number}, {credit_card.pin})")
        self._instance.conn.commit()
        # self._instance.accounts[credit_card.number] = {"pin": credit_card.pin, "balance": 0}

    def is_valid_pin(self, account_number, pin):
        cur = self._instance.conn.cursor()
        cur.execute(f"select id from card where number = {account_number} and pin = {pin}")
        row = cur.fetchone()
        cur.close()
        # if account_number in self._instance.accounts:
        #     if self._instance.accounts[account_number]["pin"] == pin:
        #         return True
        return True if row else False

    def get_balance(self, account_number):
        cur = self._instance.conn.cursor()
        cur.execute(f"select balance from card where number = {account_number}")
        row = cur.fetchone()
        cur.close()
        return row[0]
        # if account_number in self._instance.accounts:
        #     return self._instance.accounts[account_number]["balance"]

    def deposit_money(self, account_number, amount):
        balance = self._instance.get_balance(account_number)
        self._instance.conn.execute(f"update card "
                                    f"set balance = {balance + amount} "
                                    f"where number = {account_number}")
        self._instance.conn.commit()

    def close_account(self, account_number):
        self._instance.conn.execute(f"delete from card where number = {account_number}")
        self._instance.conn.commit()

    def transfer_money(self, account1, account2, amount):
        self._instance.deposit_money(account1, amount * -1)
        self._instance.deposit_money(account2, amount)

    def account_exists(self, account_number):
        return not self._instance.__is_available_card_number__(account_number)

    def is_valid_card_number(self, account_number):
        calculated_luhn = self._instance.__calculate_luhn__(account_number[0:15])
        actual_luhn = int(account_number[15])
        return calculated_luhn == actual_luhn

    def create_table(self):
        try:
            self._instance.conn.execute(f"create table IF NOT EXISTS card ("
                                        f"id INTEGER, "
                                        f"number TEXT, "
                                        f"pin TEXT, "
                                        f"balance INTEGER default 0"
                                        f")")
            self._instance.conn.commit()
        except sqlite3.DatabaseError:
            # Table already exists
            pass


class CreditCard:
    def __init__(self, number=None, pin=None):
        self.number = number if number else CreditCard.__generate_card__()
        self.pin = pin if pin else CreditCard.__generate_pin__()

    @staticmethod
    def __generate_card__():
        return Bank().open_account()

    @staticmethod
    def __generate_pin__():
        return Bank().generate_pin()


def main():
    exit_app = False
    while True:
        print("1. Create an account")
        print("2. Log into account")
        print("0. Exit")
        option = input()
        if option == '0':
            print()
            print("Bye!")
            break
        elif option == '1':
            card = Bank().open_account()
            print()
            print("Your card has been created")
            print("Your card number:")
            print(card.number)
            print("Your card PIN:")
            print(card.pin)
        elif option == '2':
            print("Enter your card number:")
            card_number = input()
            print("Enter your PIN:")
            pin = input()
            print()
            if Bank().is_valid_pin(card_number, pin):
                print("You have successfully logged in!")
                while True:
                    print()
                    print("1. Balance")
                    print("2. Add income")
                    print("3. Do transfer")
                    print("4. Close account")
                    print("5. Log out")
                    print("0. Exit")
                    action = input()
                    if action == '1':
                        print()
                        print(f"Balance: {Bank().get_balance(card_number)}")
                    if action == '2':
                        print("Enter income:")
                        amount = int(input())
                        Bank().deposit_money(card_number, amount)
                        print("Income was added!")
                    if action == '3':
                        print("Transfer")
                        print("Enter card number:")
                        card2 = input()
                        if not Bank().is_valid_card_number(card2):
                            print("Probably you made mistake in the card number. Please try again!")
                        elif not Bank().account_exists(card2):
                            print("Such a card does not exist.")
                        else:
                            print("Enter how much money you want to transfer:")
                            amount = int(input())
                            bal = Bank().get_balance(card_number)
                            if amount > bal:
                                print("Not enough money!")
                            else:
                                Bank().transfer_money(card_number, card2, amount)
                                print("Success!")
                    if action == '4':
                        Bank().close_account(card_number)
                        print()
                        print("The account has been closed!")
                    if action == '5':
                        print()
                        print("You have successfully logged out!")
                        break
                    if action == '0':
                        exit_app = True
                        break
                print()
                if exit_app:
                    print()
                    print("Bye!")
                    break
            else:
                print("Wrong card number or PIN!")
        print()


Bank().create_table()
main()
