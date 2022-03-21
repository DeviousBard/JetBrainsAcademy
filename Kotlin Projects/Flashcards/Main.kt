package flashcards

import java.io.File
import java.io.IOException
import kotlin.random.Random

fun remove(cards: MutableMap<String, String>, definitions: MutableMap<String, String>, stats: MutableMap<String, Int>, log: MutableList<String>, cardToRemove: String="", showText: Boolean=true) {
    if (showText) {
        println("The card:".log(log))
    }
    val card = if (cardToRemove == "") {
        readLine()!!.log(log)
    } else {
        cardToRemove
    }
    if (cards.containsKey(card)) {
        val definition = cards[card]
        definitions.remove(definition)
        cards.remove(card)
        stats.remove(card)
    } else {
        if (showText) {
            println("Can't remove \"$card\": there is no such card.".log(log))
        }
        return
    }
    if (showText) {
        println("The card has been removed.".log(log))
    }
}

fun add(cards: MutableMap<String, String>, definitions: MutableMap<String, String>, stats: MutableMap<String, Int>, log: MutableList<String>) {
    println("The card:".log(log))
    val card = readLine()!!.log(log)
    if (cards.containsKey(card)) {
        println("The card \"$card\" already exists.".log(log))
        return
    }
    println("The definition of the card:".log(log))
    val definition = readLine()!!.log(log)
    if (definitions.containsKey(definition)) {
        println("The definition \"$definition\" already exists.".log(log))
        return
    }
    cards[card] = definition
    definitions[definition] = card
    stats[card] = 0
    println("The pair (\"$card\", \"$definition\") has been added.".log(log))
}

fun export(cards: MutableMap<String, String>, stats: MutableMap<String, Int>, log: MutableList<String>, exportFileName: String? = null) {
    val fileName = if (exportFileName == null) {
        println("File name:".log(log))
        readLine()!!.log(log)
    } else {
        exportFileName
    }
    var output = ""
    for ((key, value) in cards) {
        output += "$key|$value|${stats[key]}~"
    }
    if (cards.isNotEmpty()) {
        File(fileName).writeText(output.substring(0, output.length - 1), Charsets.UTF_8)
    }
    println("${cards.size} cards have been saved.".log(log))
}

fun import(cards: MutableMap<String, String>, definitions: MutableMap<String, String>, stats: MutableMap<String, Int>, log: MutableList<String>, importFileName: String? = null) {
    try {
        val fileName = if (importFileName == null) {
            println("File name:".log(log))
            readLine()!!.log(log)
        } else {
            importFileName
        }
        val pairs = File(fileName).readText(Charsets.UTF_8).split("~")
        for (pair in pairs) {
            val card = pair.split("|")
            remove(cards, definitions, stats, log, card[0], false)
            cards[card[0]] = card[1]
            definitions[card[1]] = card[0]
            stats[card[0]] = card[2].toInt()
        }
        println("${pairs.size} cards have been loaded.".log(log))
    } catch(e: IOException) {
        println("File not found.".log(log))
    }
}

fun ask(cards: MutableMap<String, String>, definitions: MutableMap<String, String>, stats: MutableMap<String, Int>, log: MutableList<String>) {
    println("How many times to ask?".log(log))
    val num = readLine()!!.log(log)
    repeat(num.toInt()) {
        val rand = Random.nextInt(cards.size)
        val card = cards.keys.toList()[rand]
        println("Print the definition of \"$card\":".log(log))
        val answer = readLine()!!.log(log)
        when {
            answer == cards[card] -> {
                println("Correct!".log(log))
            }
            definitions.keys.contains(answer) -> {
                println("Wrong. The right answer is \"${cards[card]}\", but your definition is correct for \"${definitions[answer]}\".".log(log))
                stats[card] = stats[card]!! + 1
            }
            else -> {
                println("Wrong. The right answer is \"${cards[card]}\".".log(log))
                stats[card] = stats[card]!! + 1
            }
        }
    }
}

fun hardestCard(stats: MutableMap<String, Int>, log: MutableList<String>) {
    val maxErrors = stats.values.max()
    val maxCards = stats.filter {maxErrors == it.value}
    var cardStr = ""
    if (maxErrors != null && maxErrors != 0) {
        for ((i, card) in maxCards.keys.withIndex()) {
            cardStr += "\"$card\""
            if (i < maxCards.size - 1) {
                cardStr += ", "
            }
        }
        cardStr = "The hardest ${if (maxCards.size > 1) "cards are" else "card is"} $cardStr"
        cardStr += ", . You have $maxErrors errors answering them."
        println(cardStr.log(log))
    } else {
        println("There are no cards with errors.".log(log))
    }
}

fun resetStats(stats: MutableMap<String, Int>, log: MutableList<String>) {
    stats.replaceAll { _, _ -> 0}
    println("Card statistics have been reset.".log(log))
}

fun log(log: MutableList<String>) {
    println("File name:")
    val fileName = readLine()!!
    var logText = ""
    for (logEntry in log) {
        logText += "$logEntry\n"
    }
    File(fileName).writeText(logText, Charsets.UTF_8)
    println("The log has been saved.")
}

fun String.log(list: MutableList<String>): String {
    list.add(this)
    return this
}

fun main(args: Array<String>) {
    val cards = mutableMapOf<String, String>()
    val definitions = mutableMapOf<String, String>()
    val stats = mutableMapOf<String, Int>()
    val log = mutableListOf<String>()
    val importFileName: String? = if (args.indexOf("-import") != -1) args[args.indexOf("-import") + 1] else null
    val exportFileName: String? = if (args.indexOf("-export") != -1) args[args.indexOf("-export") + 1] else null

    if (importFileName != null) {
        import(cards, definitions, stats, log, importFileName)
    }
    loop@while (true) {
        println("\nInput the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):".log(log))
        when (readLine()!!.log(log)) {
            "add" -> add(cards, definitions, stats, log)
            "remove" -> remove(cards, definitions, stats, log)
            "import" -> import(cards, definitions, stats, log)
            "export" -> export(cards, stats, log)
            "ask" -> ask(cards, definitions, stats, log)
            "log" -> log(log)
            "hardest card" -> hardestCard(stats, log)
            "reset stats" -> resetStats(stats, log)
            "exit" -> break@loop
        }
    }
    println("Bye bye!")
    if (exportFileName != null) {
        export(cards, stats, log, exportFileName)
    }
}
