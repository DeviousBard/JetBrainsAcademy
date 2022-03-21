package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

type UserInput struct {
	Command string
	Data    string
}

func GetMaxNotes() int {
	fmt.Print("Enter the maximum number of notes: ")
	scanner := bufio.NewScanner(os.Stdin)
	scanner.Scan()
	n, err := strconv.ParseInt(scanner.Text(), 10, 32)
	if err != nil {
		os.Exit(-1)
	}
	return int(n)
}

func GetCommand() UserInput {
	fmt.Print("Enter command and data: ")
	scanner := bufio.NewScanner(os.Stdin)
	scanner.Scan()
	var response = scanner.Text()
	var commandData = strings.Split(response, " ")
	var command = commandData[0]
	var data = ""
	if len(commandData) > 1 {
		data = strings.Join(commandData[1:], " ")
	}
	return UserInput{Command: strings.ToLower(command), Data: strings.TrimSpace(data)}
}

func ClearNotes() []string {
	fmt.Println("[OK] All notes were successfully deleted")
	var notes []string
	return notes

}

func Exit() {
	fmt.Println("[Info] Bye!")
	os.Exit(0)
}

func ListNotes(notes []string) {
	if len(notes) == 0 {
		fmt.Println("[Info] Notepad is empty")
	}
	for i := 0; i < len(notes); i++ {
		fmt.Printf("[Info] %d: %s\n", i+1, notes[i])
	}
}

func CreateNote(notes []string, newNote string, maxNotes int) []string {
	if len(notes) >= maxNotes {
		fmt.Println("[Error] Notepad is full")
	} else {
		if len(newNote) == 0 {
			fmt.Println("[Error] Missing note argument")
		} else {
			notes = append(notes, newNote)
			fmt.Println("[OK] The note was successfully created")
		}
	}
	return notes
}

func UnknownCommand() {
	fmt.Println("[Error] Unknown command")
}

func UpdateNote(notes []string, data string, maxNotes int) []string {
	if data == "" {
		fmt.Println("[Error] Missing position argument")
	} else {
		var positionData = strings.Split(data, " ")
		var position = positionData[0]
		var data = ""
		if len(positionData) > 1 {
			data = strings.Join(positionData[1:], " ")
		}
		if data == "" {
			fmt.Println("[Error] Missing note argument")
		} else {
			n, err := strconv.ParseInt(position, 10, 32)
			if err != nil {
				fmt.Printf("[Error] Invalid position: %s\n", position)
			} else {
				var index = int(n)
				if index > 0 && index <= maxNotes {
					if index > len(notes) {
						fmt.Println("[Error] There is nothing to update")
					} else {
						// update the element
						notes[index-1] = data
						fmt.Printf("[OK] The note at position %d was successfully updated\n", index)
					}
				} else {
					fmt.Printf("[Error] Position %d is out of the boundaries [1, %d]\n", index, maxNotes)
				}
			}
		}
	}
	return notes
}

func DeleteNote(notes []string, data string, maxNotes int) []string {
	if data == "" {
		fmt.Println("[Error] Missing position argument")
	} else {
		n, err := strconv.ParseInt(data, 10, 32)
		if err != nil {
			fmt.Printf("[Error] Invalid position: %s\n", data)
		} else {
			var index = int(n)
			if index > 0 && index <= maxNotes {
				if index > len(notes) {
					fmt.Println("[Error] There is nothing to delete")
				} else {
					// remove the element
					notes = append(notes[:index-1], notes[index:]...)
					fmt.Printf("[OK] The note at position %d was successfully deleted\n", index)
				}
			} else {
				fmt.Printf("[Error] Position %d is out of the boundaries [1, %d]\n", index, maxNotes)
			}
		}
	}
	return notes
}

func main() {
	var notes []string
	var maxNotes = GetMaxNotes()
	for {
		var userInput = GetCommand()
		switch {
		case userInput.Command == "exit":
			Exit()
		case userInput.Command == "clear":
			notes = ClearNotes()
		case userInput.Command == "list":
			ListNotes(notes)
		case userInput.Command == "create":
			notes = CreateNote(notes, userInput.Data, maxNotes)
		case userInput.Command == "update":
			notes = UpdateNote(notes, userInput.Data, maxNotes)
		case userInput.Command == "delete":
			notes = DeleteNote(notes, userInput.Data, maxNotes)
		default:
			UnknownCommand()
		}
	}
}
