package sorting

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import kotlin.math.round

fun getWordInput(inputStream: InputStream): List<String> {
    val scanner = Scanner(inputStream)
    val inputList = mutableListOf<String>()
    while (scanner.hasNext()) {
        val input = scanner.next()
        inputList.add(input)
    }
    scanner.close()
    return inputList
}

fun getLongInput(inputStream: InputStream): List<Long> {
    val scanner = Scanner(inputStream)
    val inputList = mutableListOf<Long>()
    while (scanner.hasNext()) {
        val input = scanner.next()
        try {
            input.toLong()
            inputList.add(input.toLong())
        } catch (e: Exception) {
            println("\"${input}\" is not a long. It will be skipped.")
        }
    }
    scanner.close()
    return inputList
}

fun getLineInput(inputStream: InputStream): List<String> {
    val scanner = Scanner(inputStream)
    val inputList = mutableListOf<String>()
    while (scanner.hasNext()) {
        val input = scanner.nextLine()
        inputList.add(input)
    }
    scanner.close()
    return inputList
}

fun sortInput(sortType: String, dataType: String, inputFile: File?, outputFile: File?) {
    when (dataType) {
        "word" -> {
            val wordList = when {
                inputFile != null -> {
                    getWordInput(FileInputStream(inputFile))
                }
                else -> {
                    getWordInput(System.`in`)
                }
            }
            when (sortType) {
                "byCount" -> {
                    val wordMap = mutableMapOf<String, Int>()
                    for (word in wordList) {
                        wordMap.putIfAbsent(word, 0)
                        wordMap[word] = wordMap[word]!! + 1
                    }
                    val sortedWordMap = wordMap.toSortedMap(compareBy<String> { wordMap[it] }.thenBy { it })
                    val count = wordList.size
                    val totalTextByCount = "Total words: $count."
                    var sortedTextByCount = ""
                    for ((key, value) in sortedWordMap) {
                        sortedTextByCount += ("$key: $value time(s), ${round(value.toDouble() / count.toDouble() * 100.0)}%\n")
                    }
                    when {
                        outputFile != null -> {
                            outputFile.writeText("$totalTextByCount\n$sortedTextByCount")
                        }
                        else -> {
                            println(totalTextByCount)
                            println(sortedTextByCount)
                        }
                    }
                }
                "natural" -> {
                    val totalTextNatural = "Total words: ${wordList.size}."
                    val sortedTextNatural = "Sorted data: ${wordList.sorted().joinToString(" ")}"
                    when {
                        outputFile != null -> {
                            outputFile.writeText("$totalTextNatural\n$sortedTextNatural")
                        }
                        else -> {
                            println(totalTextNatural)
                            println(sortedTextNatural)
                        }
                    }
                }
            }
        }
        "long" -> {
            val longList = when {
                inputFile != null -> {
                    getLongInput(FileInputStream(inputFile))
                }
                else -> {
                    getLongInput(System.`in`)
                }
            }
            when (sortType) {
                "byCount" -> {
                    val longMap = mutableMapOf<Long, Int>()
                    for (num in longList) {
                        longMap.putIfAbsent(num, 0)
                        longMap[num] = longMap[num]!! + 1
                    }
                    val sortedWordMap = longMap.toSortedMap(compareBy<Long> { longMap[it] }.thenBy { it })
                    val count = longList.size
                    val totalTextByCount = "Total numbers: $count."
                    var sortedTextByCount = ""
                    for ((key, value) in sortedWordMap) {
                        sortedTextByCount += ("$key: $value time(s), ${round(value.toDouble() / count.toDouble() * 100.0)}%\n")
                    }
                    when {
                        outputFile != null -> {
                            outputFile.writeText("$totalTextByCount\n$sortedTextByCount")
                        }
                        else -> {
                            println(totalTextByCount)
                            println(sortedTextByCount)
                        }
                    }
                }
                "natural" -> {
                    val totalTextNatural = "Total numbers: ${longList.size}."
                    val sortedTextNatural = "Sorted data: ${longList.sorted().joinToString(" ")}"
                    when {
                        outputFile != null -> {
                            outputFile.writeText("$totalTextNatural\n$sortedTextNatural")
                        }
                        else -> {
                            println(totalTextNatural)
                            println(sortedTextNatural)
                        }
                    }
                }
            }
        }
        "line" -> {
            val lineList = when {
                inputFile != null -> {
                    getLineInput(FileInputStream(inputFile))
                }
                else -> {
                    getLineInput(System.`in`)
                }
            }
            when (sortType) {
                "byCount" -> {
                    val lineMap = mutableMapOf<String, Int>()
                    for (line in lineList) {
                        lineMap.putIfAbsent(line, 0)
                        lineMap[line] = lineMap[line]!! + 1
                    }
                    val sortedLineMap = lineMap.toSortedMap(compareBy<String> { lineMap[it] }.thenBy { it })
                    val count = lineList.size
                    val totalTextByCount = "Total lines: $count."
                    var sortedTextByCount = ""
                    for ((key, value) in sortedLineMap) {
                        sortedTextByCount += ("$key: $value time(s), ${round(value.toDouble() / count.toDouble() * 100.0)}%\n")
                    }
                    when {
                        outputFile != null -> outputFile.writeText("$totalTextByCount\n$sortedTextByCount")
                        else -> {
                            println(totalTextByCount)
                            println(sortedTextByCount)
                        }
                    }
                }
                "natural" -> {
                    val totalTextNatural = "Total lines: ${lineList.size}."
                    val sortedTextNatural = "Sorted data:\n${lineList.sorted().joinToString("\n")}"
                    when {
                        outputFile != null -> outputFile.writeText("$totalTextNatural\n$sortedTextNatural")
                        else -> {
                            println(totalTextNatural)
                            println(sortedTextNatural)
                        }
                    }
                }
            }
        }
    }
}

class Argument(val name: String, val value: String) {
}

class ArgumentParser() {
    private val argMap = mutableMapOf<String, Set<String>>()

    fun addArgument(name: String, validValues: Set<String>) {
        argMap[name] = validValues
    }

    fun parseArguments(args: Array<String>): MutableList<Argument> {
        val argList = mutableListOf<Argument>()
        var i = 0
        while (i < args.size) {
            if (args[i].startsWith("-")) {
                if (!argMap.containsKey(args[i])) {
                    println("\"${args[i]}\" is not a valid parameter. It will be skipped.")
                    i++
                } else {
                    if (i + 1 < args.size) {
                        val value = args[i + 1]
                        if (argMap[args[i]]!!.isNotEmpty() && value !in argMap[args[i]]!!) {
                            println("No ${args[i].split("Type")[0].substring(1)} type defined!")
                            argList.add(Argument(args[i], "none"))
                            i++
                        } else {
                            argList.add(Argument(args[i], args[i + 1]))
                            i += 2
                        }
                    } else {
                        println("No ${args[i].split("Type")[0].substring(1)} type defined!")
                        i++
                    }
                }
            }
        }
        return argList
    }
}

fun main(args: Array<String>) {
    val argParser = ArgumentParser()
    argParser.addArgument("-sortingType", setOf("natural", "byCount"))
    argParser.addArgument("-dataType", setOf("long", "word", "line"))
    argParser.addArgument("-inputFile", setOf())
    argParser.addArgument("-outputFile", setOf())
    val argList = argParser.parseArguments(args)
    var sortType = "natural"
    var dataType = "word"
    var inputFile: File? = null
    var outputFile: File? = null
    for (arg in argList) {
        if (arg.name == "-sortingType") {
            sortType = arg.value
        }
        if (arg.name == "-dataType") {
            dataType = arg.value
        }
        if (arg.name == "-inputFile") {
            inputFile = File(arg.value)
        }
        if (arg.name == "-outputFile") {
            outputFile = File(arg.value)
        }
    }
    if (sortType != "none" || dataType != "none") {
        sortInput(sortType, dataType, inputFile, outputFile)
    }
}
