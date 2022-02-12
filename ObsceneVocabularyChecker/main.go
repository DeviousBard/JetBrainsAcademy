package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

func main() {
	var fileName = GetResponseFromUser()
	var restrictedWords = CreateWordSetFromFile(fileName)
	for {
		var sentence = GetResponseFromUser()
		if strings.ToLower(sentence) == "exit" {
			fmt.Println("Bye!")
			break
		}
		fmt.Println(CensorSentence(restrictedWords, sentence))
	}
}

func CensorSentence(wordSet map[string]struct{}, sentence string) string {
	var censoredSentence = ""
	sentenceWords := strings.Fields(sentence)
	for _, word := range sentenceWords {
		if WordSetContainsWord(wordSet, word) {
			censoredSentence += MaskWord(word) + " "
		} else {
			censoredSentence += word + " "
		}
	}
	return censoredSentence[0 : len(censoredSentence)-1]
}

func MaskWord(word string) string {
	var mask = "**************************************************************************************"
	return mask[:len(word)]
}

func WordSetContainsWord(wordSet map[string]struct{}, word string) bool {
	_, ok := wordSet[strings.ToLower(word)]
	return ok
}

func GetResponseFromUser() string {
	var response string
	scanner := bufio.NewScanner(os.Stdin)
	scanner.Scan()
	response = scanner.Text()
	return response
}

func CreateWordSetFromFile(fileName string) map[string]struct{} {
	var wordSet = make(map[string]struct{})

	file, err := os.Open(fileName)
	if err != nil {
		fmt.Println(err)
	}
	defer func(file *os.File) {
		_ = file.Close()
	}(file)

	scanner := bufio.NewScanner(file)
	scanner.Split(bufio.ScanWords)
	for scanner.Scan() {
		wordSet[strings.ToLower(scanner.Text())] = struct{}{}
	}
	if err := scanner.Err(); err != nil {
		fmt.Println(err)
	}
	return wordSet
}
