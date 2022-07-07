package indigo

import kotlin.random.Random
import kotlin.system.exitProcess

class Card(val rank: String, val suit: String) {
    val points = if (rank in listOf("A", "10", "J", "Q", "K")) 1 else 0

    companion object {
        val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val suits = listOf("♦", "♥", "♠", "♣")
    }

    override fun toString(): String {
        return "$rank$suit"
    }
}

class Deck {
    private var deck = mutableListOf<Card>()

    init {
        reset()
    }

    fun dealCard(): Card {
        return deck.removeFirst()
    }

    fun size(): Int {
        return deck.size
    }

    fun shuffle() {
        deck.shuffle()
    }

    private fun reset() {
        deck = mutableListOf()
        for (s in Card.suits) {
            for (r in Card.ranks) {
                deck.add(Card(r, s))
            }
        }
    }
}

abstract class Player(val name: String) {
    val hand = mutableListOf<Card>()
    val wonCards = mutableListOf<Card>()

    fun hasCardsInHand(): Boolean {
        return hand.size > 0
    }

    fun getScore(): Int {
        return wonCards.sumOf {x -> x.points}
    }

    abstract fun playCard(discardPileTopCard: Card?): Card

    abstract fun showHand()
}

class HumanPlayer(name: String) : Player(name) {
    override fun playCard(discardPileTopCard: Card?): Card {
        var selectedCardIndex: Int
        while(true) {
            println("Choose a card to play (1-${hand.size}):")
            try {
                val input = readln()
                if (input == "exit") {
                    println("Game Over")
                    exitProcess(0)
                }
                selectedCardIndex = input.toInt()
                if (selectedCardIndex >= 1 && selectedCardIndex <= hand.size) {
                    selectedCardIndex--
                    break
                }
            } catch (e : Exception) {
                // Intentionally ignored
            }
        }
        return hand.removeAt(selectedCardIndex)
    }

    override fun showHand() {
        print("Cards in hand: ")
        for (i in 0 until hand.size) {
            print("${i + 1})${hand[i]} ")
        }
        println()
    }
}

class ComputerPlayer(name: String) : Player(name) {
    override fun playCard(discardPileTopCard: Card?): Card {
        val cardIndex = determineCardToPlay(discardPileTopCard)
        val card = hand[cardIndex]
        hand.removeAt(cardIndex)
        println("Computer plays $card")
        return card
    }

    private fun determineCardToPlay(discardPileTopCard: Card?): Int {
        val candidateCards = findCandidateCards(discardPileTopCard)
        return if (candidateCards.size > 1) candidateCards[Random.nextInt(candidateCards.size)] else candidateCards[0]
    }

    private fun findCandidateCards(discardPileTopCard: Card?): List<Int> {
        val suitMap = getSuitMap()
        val rankMap = getRankMap()
        val matchingRanks = rankMap.getOrDefault(discardPileTopCard?.rank ?: "", mutableListOf())
        val matchingSuits = suitMap.getOrDefault(discardPileTopCard?.suit ?: "", mutableListOf())
        if (matchingRanks.size > 0 && matchingRanks.size >= matchingSuits.size) {
            return matchingRanks.toList()
        } else if (matchingSuits.size > 0) {
            return matchingSuits.toList()
        }
        val maxSuits = (suitMap.maxByOrNull { it.value.size })!!.value
        val maxRanks = (rankMap.maxByOrNull {it.value.size})!!.value
        if (maxSuits.size > 1) {
            return maxSuits.toList()
        } else if (maxRanks.size > 1) {
            return maxRanks.toList()
        }
        return (0 until hand.size).toList()
    }

    private fun getSuitMap(): Map<String, MutableList<Int>> {
        val suitMap = mutableMapOf<String, MutableList<Int>>()
        for (i in 0 until hand.size) {
            val indexList = suitMap.getOrDefault(hand[i].suit, mutableListOf())
            indexList.add(i)
            suitMap[hand[i].suit] = indexList
        }
        return suitMap
    }

    private fun getRankMap(): Map<String, MutableList<Int>> {
        val rankMap = mutableMapOf<String, MutableList<Int>>()
        for (i in 0 until hand.size) {
            val indexList = rankMap.getOrDefault(hand[i].rank, mutableListOf())
            indexList.add(i)
            rankMap[hand[i].rank] = indexList
        }
        return rankMap
    }

    override fun showHand() {
        for (card in hand) {
            print("$card ")
        }
        println()
    }
}

fun main() {
    println("Indigo Card Game")
    var currentPlayerIndex: Int
    val players = mutableListOf(HumanPlayer("Player"), ComputerPlayer("Computer"))
    while (true) {
        println("Play first?")
        when (readln().lowercase()) {
            "yes" -> {
                currentPlayerIndex = 0
                break
            }
            "no" -> {
                currentPlayerIndex = 1
                break
            }
        }
    }
    val firstPlayer = currentPlayerIndex
    var lastWinner = currentPlayerIndex
    val deck = Deck()
    deck.shuffle()
    val discardPile = mutableListOf<Card>()
    for (i in 1..4) {
        discardPile.add(deck.dealCard())
    }
    print("Initial cards on the table: ")
    println(discardPile.joinToString(" "))

    for (round in 1 .. 4) {
        for (i in 1..6) {
            for (player in players) {
                player.hand.add(deck.dealCard())
            }
        }
        while (players[0].hasCardsInHand() || players[1].hasCardsInHand()) {
            val currentPlayer = players[currentPlayerIndex]
            println()
            var lastDiscard: Card? = null
            if (discardPile.size > 0) {
                lastDiscard = discardPile[discardPile.lastIndex]
                println("${discardPile.size} cards on the table, and the top card is $lastDiscard")
            } else {
              println("No cards on the table")
            }
            currentPlayer.showHand()
            val playedCard = currentPlayer.playCard(lastDiscard)
            if (playedCard.rank == (lastDiscard?.rank ?: "") || playedCard.suit == (lastDiscard?.suit ?: "")) {
                discardPile.add(playedCard)
                currentPlayer.wonCards.addAll(discardPile)
                println("${currentPlayer.name} wins cards")
                println("Score: Player ${players[0].getScore()} - Computer ${players[1].getScore()}")
                println("Cards: Player ${players[0].wonCards.size} - Computer ${players[1].wonCards.size}")
                discardPile.clear()
                lastWinner = currentPlayerIndex
            } else {
                discardPile.add(playedCard)
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % 2
        }
    }
    println()
    if (discardPile.size > 0) {
        val lastDiscard = discardPile[discardPile.lastIndex]
        println("${discardPile.size} cards on the table, and the top card is $lastDiscard")
    } else {
        println("No cards on the table")
    }
    players[lastWinner].wonCards.addAll(discardPile)
    var playerScore = players[0].getScore()
    var computerScore = players[1].getScore()
    if (players[0].wonCards.size > players[1].wonCards.size) {
        playerScore += 3
    } else if (players[1].wonCards.size > players[0].wonCards.size) {
        computerScore += 3
    } else {
        if (firstPlayer == 0) {
            playerScore += 3
        } else {
            computerScore += 3
        }
    }
    println("Score: Player $playerScore - Computer $computerScore")
    println("Cards: Player ${players[0].wonCards.size} - Computer ${players[1].wonCards.size}")
    println("Game Over")
    exitProcess(0)
}
