package search

import java.awt.image.BufferStrategy
import java.io.File

fun menu(): Int {
    var option: String
    while (true) {
        println("=== Menu ===")
        println("1. Search information.")
        println("2. Print all data.")
        println("0. Exit.")
        println()
        option = readLine()!!
        if (option in "0".."2") {
            break
        }
        println()
        println("Incorrect option! Try again.")
        println()
    }
    return option.toInt()
}

fun printAllData(data: List<String>) {
    println("=== List of people ===")
    println(data.joinToString(separator = "\n"))
    println()
}

fun readFile(fileName: String): List<String> {
    return File(fileName).readText().split("\n")
}

fun queryData(invertedIndex: Map<String, List<Int>>, data: List<String>) {
    println("Select a matching strategy: ALL, ANY, NONE")
    val strategy = readLine()!!
    println()
    println("Enter a name or email to search all suitable people.")
    val query = readLine()!!
    println()
    val resultStr = search(invertedIndex, data, query, strategy).joinToString(separator = "\n")
    if (resultStr == "") {
        println("No matching people found.")
    } else {
        println("Found people:")
        println(resultStr)
    }
    println()
}

fun buildInvertedIndex(data: List<String>): Map<String, List<Int>> {
    val invertedIndex = mutableMapOf<String, MutableList<Int>>()
    for ((i, line) in data.withIndex()) {
        val terms = line.split(" ")
        for (term in terms) {
            val lowerTerm = term.toLowerCase().trim()
            if (!invertedIndex.containsKey(lowerTerm)) {
                invertedIndex[lowerTerm] = mutableListOf()
            }
            invertedIndex[lowerTerm]?.add(i)
        }
    }
    for ((key, value) in invertedIndex) {
        println("$key: $value")
    }
    return invertedIndex
}

fun search(invertedIndex: Map<String, List<Int>>, data: List<String>, query: String, strategy: String): List<String> {
    val terms = query.split(" ")
    var lineNums = mutableListOf<Int>()
    when (strategy) {
        "ANY" -> {
            val anySet = mutableSetOf<Int>()
            for (term in terms) {
                anySet.addAll(invertedIndex[term.toLowerCase().trim()] ?: mutableListOf())
            }
            lineNums = anySet.toMutableList()
        }
        "ALL" -> {
            val allLines = mutableListOf<MutableList<Int>>()
            for (term in terms) {
                allLines.add((invertedIndex[term.toLowerCase().trim()] ?: listOf()).toMutableList())
            }
            lineNums = allLines.reduce { acc, it -> acc.apply { retainAll(it) } }
        }
        "NONE" -> {
            val anySet = mutableSetOf<Int>()
            for (term in terms) {
                anySet.addAll(invertedIndex[term.toLowerCase().trim()] ?: mutableListOf())
            }
            for (i in data.indices) {
                if (i !in anySet) {
                    lineNums.add(i)
                }
            }
        }
    }
    val results = mutableListOf<String>()
    for (lineNum in lineNums) {
        results.add(data[lineNum])
    }
    return results
}

fun main(args: Array<String>) {
    val data = readFile(args[args.indexOf("--data") + 1])
    val invertedIndex = buildInvertedIndex(data)
    loop@ while (true) {
        when (menu()) {
            0 -> break@loop
            1 -> queryData(invertedIndex, data)
            2 -> printAllData(data)
        }
    }
    println()
    println("Bye!")
}
