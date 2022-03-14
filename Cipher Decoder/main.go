package main

import (
	"bufio"
	"fmt"
	"math/rand"
	"os"
	"strconv"
	"strings"
)

type DiffieHellman struct {
	g int
	p int
	b int
	A int
	B int
	s int
}

var cipherMap = make(map[string]string)

/*
	Calculate the Diffie-Hellman parameters
*/
func GetDiffieHellman() DiffieHellman {
	// Prepare to read data from the standard input
	scanner := bufio.NewScanner(os.Stdin)

	// Extract the base number (g) from the STUPID input format "g is <int> and p is <int>"
	scanner.Scan()
	text := strings.Split(scanner.Text(), " ")
	var base, err1 = strconv.Atoi(text[2])
	HandleNumericParseError(err1, text[2])

	// Extract the modulus number (p) from the STUPID input format "g is <int> and p is <int>"
	var modulus, err2 = strconv.Atoi(text[6])
	HandleNumericParseError(err2, text[6])

	// For no apparent reason, "OK" must be printed to the output
	fmt.Println("OK")

	// Extract A's shared secret (A) from the STUPID input format "A is <int>"
	scanner.Scan()
	text = strings.Split(scanner.Text(), " ")
	var secretA, err3 = strconv.Atoi(text[2])
	HandleNumericParseError(err3, text[2])

	// Create a randomizer (b) between 2 and (modulus - 1) inclusive (i.e. 1 < b < p)
	randomizer := rand.Intn(modulus-2) + 2

	// Calculate B's secret using the iterative method
	secretB := CalculateSecret(base, modulus, randomizer)

	// Shared secret (s) is A^b mod p
	sharedSecret := CalculateSecret(secretA, modulus, randomizer)

	return DiffieHellman{g: base, p: modulus, b: randomizer, A: secretA, B: secretB, s: sharedSecret}
}

/*
	Calculate the shared secret using the iterative method from the base, modulus, and randomizer
*/
func CalculateSecret(base int, modulus int, randomizer int) int {
	lastRemainder := 1
	for i := 0; i < randomizer; i++ {
		lastRemainder = (lastRemainder * base) % modulus
	}
	return lastRemainder
}

/*
	Handle numeric parsing errors that may occur from trying to parse the numbers from the STUPID
    input formats.
*/
func HandleNumericParseError(err error, text string) {
	if err != nil {
		fmt.Printf("Invalid number: %s\n", text)
		os.Exit(-1)
	}
}

func BuildCipherMap(diffieHellman DiffieHellman) {
	alphabet := []string{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"}
	caesarCipherOffset := diffieHellman.s % 26
	for i := 0; i < len(alphabet); i++ {
		offset := i + caesarCipherOffset
		if offset > 25 {
			offset -= 26
		}
		cipherMap[alphabet[i]] = alphabet[offset]
	}
}

/*
	Encode a message using the cipher map
*/
func EncodeMessage(message string) string {
	sb := strings.Builder{}
	chars := strings.Split(message, "")
	for i := 0; i < len(chars); i++ {
		lowerChar := strings.ToLower(chars[i])
		encodedChar, ok := cipherMap[lowerChar]
		if ok {
			// For characters found in the cipher map, ensure that they're output to the encoded string in the same case
			// (upper or lower) as the input
			if lowerChar != chars[i] {
				sb.WriteString(strings.ToUpper(encodedChar))
			} else {
				sb.WriteString(encodedChar)
			}
		} else {
			// For characters not in the cipher map, just output them unchanged to the encoded string
			sb.WriteString(chars[i])
		}
	}
	return sb.String()
}

/*
	Decode a message using the cipher map
*/
func DecodeMessage(message string) string {
	sb := strings.Builder{}
	chars := strings.Split(message, "")
	for i := 0; i < len(chars); i++ {
		lowerChar := strings.ToLower(chars[i])
		decodedChar, ok := GetMapKeyByValue(cipherMap, lowerChar)
		if ok {
			// For characters found in the cipher map, ensure that they're output to the encoded string in the same case
			// (upper or lower) as the input
			if lowerChar != chars[i] {
				sb.WriteString(strings.ToUpper(decodedChar))
			} else {
				sb.WriteString(decodedChar)
			}
		} else {
			// For characters not in the cipher map, just output them unchanged to the encoded string
			sb.WriteString(chars[i])
		}
	}
	return sb.String()
}

/*
	Look up a map key by it's value
*/
func GetMapKeyByValue(m map[string]string, value string) (key string, ok bool) {
	for k, v := range m {
		if v == value {
			key = k
			ok = true
			return
		}
	}
	return
}

/*
	Get A's encrypted reply to the encrypted question
*/
func GetEncodedReply() string {
	// Prepare to read data from the standard input
	scanner := bufio.NewScanner(os.Stdin)
	scanner.Scan()
	return scanner.Text()
}

/*
	The driver for the Diffie-Hellman calculation
*/
func main() {
	diffieHellman := GetDiffieHellman()
	BuildCipherMap(diffieHellman)
	fmt.Printf("B is %d\n", diffieHellman.B)
	fmt.Printf("%s\n", EncodeMessage("Will you marry me?"))
	encodedReply := GetEncodedReply()
	decodedReply := DecodeMessage(encodedReply)
	encodedResponse := ""
	if decodedReply == "Yeah, okay!" {
		encodedResponse = EncodeMessage("Great!")
	} else if decodedReply == "Let's be friends." {
		encodedResponse = EncodeMessage("What a pity!")
	}
	if encodedResponse != "" {
		fmt.Printf("%s\n", encodedResponse)
	}
}
