package main

import (
	"bufio"
	"flag"
	"fmt"
	"math/rand"
	"os"
	"strconv"
	"strings"
)

type Flashcard struct {
	Term       string
	Definition string
	Mistakes   int
}

var log = ""
var cards = make(map[int]Flashcard)
var exportFile = ""

func main() {
	ParseCommandLineArguments()
	for {
		PrintText("\nInput the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
		var action = GetUserInput()
		switch {
		case action == "add":
			CreateNewCard()
		case action == "remove":
			RemoveCard()
		case action == "import":
			cards = ImportCards("")
		case action == "export":
			ExportCards(exportFile)
		case action == "ask":
			QuizUser()
		case action == "log":
			WriteLog()
		case action == "hardest card":
			FindHardestCard()
		case action == "reset stats":
			ResetStats()
		}
		if action == "exit" {
			break
		}
	}
	if exportFile != "" {
		ExportCards(exportFile)
	}
	fmt.Println("Bye bye!")
}

func ParseCommandLineArguments() {
	importFrom := flag.String("import_from", "", "The name of the flashcard file to be loaded at startup.")
	exportTo := flag.String("export_to", "", "The name of the flashcard file to export to when the user exits.")
	flag.Parse()
	if *importFrom != "" {
		ImportCards(*importFrom)
	}
	exportFile = *exportTo
	fmt.Print("EXPORT FILE: " + exportFile)
}

func PrintText(text string) {
	log = log + text + "\n"
	fmt.Println(text)
}

func WriteLog() {
	PrintText("File name:")
	var fileName = GetUserInput()
	file, err := os.Create(fileName)
	CheckForErrors(err)
	defer func(file *os.File) {
		CheckForErrors(file.Close())
	}(file)
	_, err = file.WriteString(log)
	CheckForErrors(err)
	CheckForErrors(file.Sync())
	PrintText("The log has been saved.")
}

func FindHardestCard() {
	var mostErrors = 0
	var mostErrorsCount = 0
	for i := 0; i < len(cards); i++ {
		if mostErrorsCount > 0 && cards[i].Mistakes == mostErrors {
			mostErrorsCount++
		} else if cards[i].Mistakes > mostErrors {
			mostErrors = cards[i].Mistakes
			mostErrorsCount = 1
		}
	}
	if mostErrorsCount == 0 {
		PrintText("There are no cards with errors.")
	} else {
		var text = ""
		if mostErrorsCount == 1 {
			text = text + "The hardest card is "
		} else {
			text = text + "The hardest cards are "
		}
		for i := 0; i < len(cards); i++ {
			if cards[i].Mistakes == mostErrors {
				text = text + fmt.Sprintf("\"%s\"", cards[i].Term)
			}
			if i < mostErrorsCount-1 {
				text = text + ", "
			}
		}
		text = text + fmt.Sprintf(". You have %d errors answering ", mostErrors)
		if mostErrorsCount == 1 {
			text = text + "it"
		} else {
			text = text + "them"
		}
		PrintText(text)
	}
}

func ResetStats() {
	for i := 0; i < len(cards); i++ {
		cards[i] = Flashcard{Term: cards[i].Term, Definition: cards[i].Definition, Mistakes: 0}
	}
	PrintText("Card statistics have been reset.")
}

func CheckForErrors(e error) {
	if e != nil {
		panic(e)
	}
}

func ImportCards(fileName string) map[int]Flashcard {
	if fileName == "" {
		PrintText("File name:")
		fileName = GetUserInput()
	}
	var numCardsImported = 0
	file, err := os.Open(fileName)
	if err != nil {
		PrintText("File not found.")
		return cards
	}
	defer func(file *os.File) {
		CheckForErrors(file.Close())
	}(file)
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		var cardStr = scanner.Text()
		var tildeIndex = strings.Index(cardStr, "~")
		if tildeIndex != -1 {
			var term = cardStr[:tildeIndex]
			var definitionMistakes = cardStr[tildeIndex+1:]
			tildeIndex = strings.Index(definitionMistakes, "~")
			var definition = definitionMistakes[:tildeIndex]
			var mistakes, _ = strconv.Atoi(definitionMistakes[tildeIndex+1:])
			var flashcard = Flashcard{Term: term, Definition: definition, Mistakes: mistakes}
			var cardFound = false
			for i := 0; i < len(cards); i++ {
				if cards[i].Term == term {
					cards[i] = flashcard
					cardFound = true
					break
				}
			}
			if !cardFound {
				cards[len(cards)] = flashcard
			}
			numCardsImported++
		}
	}
	CheckForErrors(scanner.Err())
	PrintText(fmt.Sprintf("%d cards have been loaded.", numCardsImported))
	return cards
}

func ExportCards(fileName string) {
	if fileName == "" {
		PrintText("File name:")
		fileName = GetUserInput()
	}
	file, err := os.Create(fileName)
	CheckForErrors(err)
	defer func(file *os.File) {
		CheckForErrors(file.Close())
	}(file)
	for i := 0; i < len(cards); i++ {
		var entry = cards[i].Term + "~" + cards[i].Definition + "~" + strconv.Itoa(cards[i].Mistakes) + "\n"
		_, err := file.WriteString(entry)
		CheckForErrors(err)
	}
	CheckForErrors(file.Sync())
	PrintText(fmt.Sprintf("%d cards have been saved.", len(cards)))
}

func RemoveCard() {
	PrintText("Which card?")
	var termToDelete = GetUserInput()
	var cardSize = len(cards)
	for i := 0; i < len(cards); i++ {
		if cards[i].Term == termToDelete {
			delete(cards, i)
			cards = ReindexCards(cardSize)
			PrintText("The card has been removed.")
			return
		}
	}
	PrintText(fmt.Sprintf("Can't remove \"%s\": there is no such card.", termToDelete))
}

func ReindexCards(size int) map[int]Flashcard {
	var reindexedCards = make(map[int]Flashcard)
	var index = 0
	for i := 0; i < size; i++ {
		if card, ok := cards[i]; ok {
			reindexedCards[index] = card
			index++
		}
	}
	return reindexedCards
}

func QuizUser() {
	PrintText("How many times to ask?")
	var numCards, _ = strconv.Atoi(GetUserInput())
	for i := 0; i < numCards; i++ {
		var cardNum = rand.Intn(len(cards))
		var selectedCard = cards[cardNum]
		PrintText(fmt.Sprintf("Print the definition of \"%s\":", selectedCard.Term))
		var answer = GetUserInput()
		CheckAnswer(cardNum, answer)
	}
}

func CheckAnswer(cardNum int, answer string) {
	var card = cards[cardNum]
	if answer == card.Definition {
		PrintText("Correct!")
	} else {
		cards[cardNum] = Flashcard{Term: card.Term, Definition: card.Definition, Mistakes: card.Mistakes + 1}
		var duplicate = FindDuplicateCard(answer, "definition")
		if duplicate != -1 {
			var dupCard = cards[duplicate]
			PrintText(fmt.Sprintf("Wrong. The right answer is \"%s\", but your definition is correct for \"%s\".", card.Definition, dupCard.Term))
		} else {
			PrintText(fmt.Sprintf("Wrong. The right answer is \"%s\".", card.Definition))
		}
	}
}

func CreateNewCard() {
	PrintText("The card:")
	var term = GetUserInput()
	for FindDuplicateCard(term, "term") != -1 {
		PrintText(fmt.Sprintf("The card \"%s\" already exists. Try again:", term))
		term = GetUserInput()
	}
	PrintText("The definition of the card:")
	var definition = GetUserInput()
	for FindDuplicateCard(definition, "definition") != -1 {
		PrintText(fmt.Sprintf("The definition \"%s\" already exists. Try again:", definition))
		definition = GetUserInput()
	}
	var flashcard = Flashcard{Term: term, Definition: definition, Mistakes: 0}
	cards[len(cards)] = flashcard
	PrintText(fmt.Sprintf("The pair (\"%s\":\"%s\") has been added.", flashcard.Term, flashcard.Definition))
}

func FindDuplicateCard(value string, side string) int {
	for i := 0; i < len(cards); i++ {
		var card = cards[i]
		if side == "term" {
			if card.Term == value {
				return i
			}
		} else {
			if card.Definition == value {
				return i
			}
		}
	}
	return -1
}

func GetUserInput() string {
	var response string
	scanner := bufio.NewScanner(os.Stdin)
	scanner.Scan()
	response = scanner.Text()
	log = log + response + "\n"
	return response
}
