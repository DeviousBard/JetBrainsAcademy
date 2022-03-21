package signature

import java.io.File
import java.util.*

fun getStringWidth(font: Array<Array<String>>, str: String): Int {
    var length = 1
    for (c in str) {
        length += (font[c.toInt()][0].length)
    }
    return length
}

fun getStringRow(font: Array<Array<String>>, str: String, rowIndex: Int): String {
    var row = ""
    for (c in str) {
        row += font[c.toInt()][rowIndex]
    }
    return row
}

fun printTagString(font: Array<Array<String>>, offset: Int, str: String, extraSpace: String) {
    for (i in 0..font['A'.toInt()].lastIndex) {
        print("\n88  ")
        repeat(offset) {
            print(" ")
        }
        print(getStringRow(font, str, i))
        repeat(offset) {
            print(" ")
        }
        print("$extraSpace  88")
    }
}

fun printNameTag(name: String, status: String, romanFont: Array<Array<String>>, mediumFont: Array<Array<String>>) {
    val nameWidth = getStringWidth(romanFont, name)
    val statusWidth = getStringWidth(mediumFont, status)
    val tagWidth = if (nameWidth > statusWidth) nameWidth else statusWidth
    val nameOffset = (tagWidth - nameWidth) / 2
    val statusOffset = (tagWidth - statusWidth) / 2
    val nameExtraSpace = if (nameOffset * 2 + nameWidth < tagWidth) " " else ""
    val statusExtraSpace = if (statusOffset * 2 + statusWidth < tagWidth) " " else ""
    val borderWidth = tagWidth + 7
    repeat(borderWidth) {
        print("8")
    }
    printTagString(romanFont, nameOffset, name, nameExtraSpace)
    printTagString(mediumFont, statusOffset, status, statusExtraSpace)
    print("\n")
    repeat(borderWidth) {
        print("8")
    }
    print("\n")
}

fun loadFont(fileName: String, spaceTemplateChar: Char): Array<Array<String>> {
    val fontFile = Scanner(File(fileName))
    val fontSize = fontFile.nextInt()
    val numChars = fontFile.nextInt()
    val font = Array(123) { Array(fontSize) { "" } }
    for (i in 0 until numChars) {
        val char = fontFile.next().toCharArray()[0].toInt()
        fontFile.nextLine().trim().toInt()
        val row = Array(fontSize){""}
        for (j in 0 until fontSize) {
            val charRow = fontFile.nextLine()
            row[j] = charRow
        }
        font[char] = row
    }
    var spaces = ""
    repeat(font[spaceTemplateChar.toInt()][0].length)  {
        spaces += " "
    }
    for (i in font[spaceTemplateChar.toInt()].indices) {
        font[32][i] = spaces
    }
    return font
}

fun main() {
    val romanFont = loadFont("C:/projects/KotlinProjects/ASCII Text Signature/ASCII Text Signature/task/roman.txt", 'a')
    val mediumFont = loadFont("C:/projects/KotlinProjects/ASCII Text Signature/ASCII Text Signature/task/medium.txt", 'A')
    val scanner = Scanner(System.`in`)
    print("Enter the name and surname: ")
    val firstName = scanner.next().trim()
    val surName = scanner.nextLine().trim()
    val name = "$firstName $surName"
    print("Enter the person's status: ")
    val status = scanner.nextLine().trim().toUpperCase()
    printNameTag(name, status, romanFont, mediumFont)
}
