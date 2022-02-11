package main

import (
	"fmt"
	"strconv"
)

const waterId = 0
const milkId = 1
const coffeeBeansId = 2
const cupsId = 3
const moneyId = 4

func main() {
	var contents = new([5]int)
	*contents = [5]int{400, 540, 120, 9, 550}

	for {
		action := getAction()

		if action == "exit" {
			break
		}

		switch {
		case action == "buy":
			buyCoffee(contents)
		case action == "fill":
			fillMachine(contents)
		case action == "take":
			takeMoney(contents)
		case action == "remaining":
			printMachineContents(contents)
		}
	}
}

func takeMoney(contents *[5]int) {
	fmt.Printf("\n")
	fmt.Printf("I gave you $%d\n", contents[moneyId])
	contents[moneyId] = 0
}

func fillMachine(contents *[5]int) {
	var (
		waterToAdd       int
		milkToAdd        int
		coffeeBeansToAdd int
		cupsToAdd        int
	)
	fmt.Printf("\n")
	fmt.Printf("Write how many ml of water you want to add: ")
	_, _ = fmt.Scan(&waterToAdd)
	fmt.Printf("Write how many ml of milk you want to add: ")
	_, _ = fmt.Scan(&milkToAdd)
	fmt.Printf("Write how many grams of coffee beans you want to add: ")
	_, _ = fmt.Scan(&coffeeBeansToAdd)
	fmt.Printf("Write how many disposable cups of coffee you want to add: ")
	_, _ = fmt.Scan(&cupsToAdd)
	contents[waterId] += waterToAdd
	contents[milkId] += milkToAdd
	contents[coffeeBeansId] += coffeeBeansToAdd
	contents[cupsId] += cupsToAdd
}

func buyCoffee(contents *[5]int) {
	var coffeeType = [3][5]int{{250, 0, 16, 1, 4}, {350, 75, 20, 1, 7}, {200, 100, 12, 1, 6}}
	var choiceStr string

	fmt.Printf("\n")
	fmt.Printf("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino: ")
	_, _ = fmt.Scan(&choiceStr)
	if choiceStr != "back" {
		var choice, _ = strconv.Atoi(choiceStr)
		choice--
		if contents[waterId] < coffeeType[choice][waterId] {
			fmt.Printf("Sorry, not enough water!\n")
		} else if contents[milkId] < coffeeType[choice][milkId] {
			fmt.Printf("Sorry, not enough milk!\n")
		} else if contents[coffeeBeansId] < coffeeType[choice][coffeeBeansId] {
			fmt.Printf("Sorry, not enough coffee beans!\n")
		} else if contents[cupsId] < coffeeType[choice][cupsId] {
			fmt.Printf("Sorry, not enough cups!\n")
		} else {
			fmt.Printf("I have enough resources, making you a coffee!\n")
			contents[waterId] -= coffeeType[choice][waterId]
			contents[milkId] -= coffeeType[choice][milkId]
			contents[coffeeBeansId] -= coffeeType[choice][coffeeBeansId]
			contents[cupsId] -= coffeeType[choice][cupsId]
			contents[moneyId] += coffeeType[choice][moneyId]
		}
	}
}

func getAction() string {
	var action string
	fmt.Printf("\n")
	fmt.Printf("Write action (buy, fill, take): ")
	_, _ = fmt.Scanf("%s", &action)
	return action
}

func printMachineContents(contents *[5]int) {
	fmt.Printf("\n")
	fmt.Printf("The coffee machine has:\n")
	fmt.Printf("%d ml of water\n", contents[waterId])
	fmt.Printf("%d ml of milk\n", contents[milkId])
	fmt.Printf("%d g of coffee beans\n", contents[coffeeBeansId])
	fmt.Printf("%d disposable cups\n", contents[cupsId])
	fmt.Printf("$%d of money\n", contents[moneyId])
}
