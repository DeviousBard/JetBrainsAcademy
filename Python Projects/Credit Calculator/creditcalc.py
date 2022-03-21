import math
import sys
import argparse


def get_monthly_interest_rate(i):
    return i / 1200


def get_overpayment(a, n, p):
    return math.ceil(a * n - p)


def main():
    type_calculation = args.type
    p = args.principal
    a = args.payment
    i = args.interest
    n = args.periods

    if (type_calculation != 'annuity' and type_calculation != 'diff') or (type_calculation == "diff" and a) or i is None or (len(sys.argv) - 1 < 4) or \
            (not(p is None) and p < 0) or (not(a is None) and a < 0) or (not(i is None) and i < 0) \
            or (not(n is None) and n < 0):
        print("Incorrect parameters")

    if type_calculation == 'annuity':

        if p and a and i:
            i = get_monthly_interest_rate(i)
            n = math.ceil(math.log(a / (a - (i * p)), 1 + i))
            payoff_years = math.floor(n / 12)
            payoff_months = n % 12
            o = math.ceil(a * n - p)
            payoff_months_text = f" and {payoff_months} months"
            if payoff_months == 0:
                payoff_months_text = ""
            print(f"You need {payoff_years} years{payoff_months_text} to repay this credit!")
            print(f"Overpayment = {o}")

        elif p and n and i:
            i = get_monthly_interest_rate(i)
            a = math.ceil(p * ((i * math.pow(1 + i, n)) / (math.pow(1 + i, n) - 1)))
            o = get_overpayment(a, n, p)
            print(f"Your annuity payment = {a}!")
            print(f"Overpayment = {o}")

        elif a and n and i:
            i = get_monthly_interest_rate(i)
            p = math.floor(a / (i * math.pow(1 + i, n) / (math.pow(1 + i, n) - 1)))
            o = get_overpayment(a, n, p)
            print(f"You credit principal = {p}!")
            print(f"Overpayment = {o}")

    elif type_calculation == 'diff':

        if p and i and n:
            i = get_monthly_interest_rate(i)
            total_paid = 0
            for m in range(1, n + 1):
                d = math.ceil(p / n + i * (p - p * (m - 1) / n))
                total_paid += d
                print(f"Month {m}: paid out {d}")
            print()
            a = total_paid / n
            o = get_overpayment(a, n, p)
            print(f"Overpayment = {o}")


parser = argparse.ArgumentParser()
parser.add_argument("--type", type=str, help='The type of payment: "annuity" or "diff"', required=False)
parser.add_argument("--payment", type=float, help="The monthly payment", required=False)
parser.add_argument("--principal", type=float, help="The principal amount of the loan", required=False)
parser.add_argument("--periods", type=int, help="The number of months to repay the loan", required=False)
parser.add_argument("--interest", type=float, help="The interest on the loan as a percentage", required=False)
args = parser.parse_args()

main()
