package minesweeper

import java.util.*
import kotlin.random.Random

class MineField(val rows: Int, val cols: Int, val numMines: Int, val firstX: Int, val firstY: Int) {
    private val field =  buildField()
    private val area: Int = rows * cols

    private fun buildField() :Array<IntArray> {
        var open = 0
        var mine = 9
        if (numMines > area / 2) {
            open = 9
            mine = 0
        }
        val field = Array(rows) { IntArray(cols) }
        var count = 0
        while (count < numMines) {
            val x = Random.nextInt(rows)
            val y = Random.nextInt(cols)
            if (field[x][y] != mine && x != firstX && y != firstY) {
                field[x][y] = mine
                count++
            } else {
                field[x][y] = open
            }
        }
        for (x in 0 until rows) {
            for (y in 0 until cols) {
                if (field[x][y] != 9) {
                    var bombCount = 0
                    for (i in x - 1..x + 1) {
                        for (j in y - 1..y + 1) {
                            if (i >= 0 && j >= 0 && i < rows && j < cols) {
                                if (field[i][j] == 9) {
                                    bombCount++
                                }
                            }
                        }
                    }
                    field[x][y] = bombCount
                }
            }
        }
        return field
    }

    fun isMarked(x: Int, y: Int): Boolean {
        return x >= 0 && y >= 0 && (field[x][y] in 10..19)
    }

    fun isHidden(x: Int, y: Int): Boolean {
        return x >= 0 && y >= 0 && field[x][y] < 10
    }

    fun isSelectable(x: Int, y: Int): Boolean {
        return isMarked(x, y) || isHidden(x, y)
    }

    fun isBomb(x: Int, y: Int): Boolean {
        return x >= 0 && y >= 0 && field[x][y] == 9
    }

    fun gameWon(): Boolean {
        var markedCells = 0
        var markedBombs = 0
        var revealedCells = 0
        var hiddenBombs = 0
        for (x in 0 until rows) {
            for (y in 0 until rows) {
                if (field[x][y] == 9) {
                    hiddenBombs++
                }
                if (field[x][y] > 19) {
                    revealedCells++
                }
                if (field[x][y] == 19) {
                    markedBombs++
                }
                if (field[x][y] in 10..18) {
                    markedCells++
                }
            }
        }
        return (markedBombs - markedCells == numMines || revealedCells + hiddenBombs + markedBombs == area)
    }

    fun toggleMark(x: Int, y: Int) {
        if (field[x][y] < 20) {
            field[x][y] += if (field[x][y] < 10) 10 else -10
        }
    }

    fun print() {
        val symbols = arrayOf(
                ".", ".", ".", ".", ".", ".", ".", ".", ".", ".",
                "*", "*", "*", "*", "*", "*", "*", "*", "*", "*",
                "/", "1", "2", "3", "4", "5", "6", "7", "8", "X"
        )
        println()
        println(" |123456789|")
        println("-|---------|")
        var index = 1
        for (row in field) {
            print("${index++}|")
            for (cell in row) {
                print(symbols[cell])
            }
            println("|")
        }
        println("-|---------|")
    }

    fun clearSurroundingCells(x: Int, y: Int) {
        if (field[x][y] == 0 || field[x][y] == 10) {
            exposeCell(x, y)
            for (i in x - 1..x + 1) {
                for (j in y - 1..y + 1) {
                    if (i in 0..8 && j in 0..8 && ! (i == x && j == y)) {
                        if (field[i][j] in 1..8) {
                            exposeCell(i, j)
                        }
                        if (field[i][j] == 0) {
                            clearSurroundingCells(i, j)
                        }
                        if (field[i][j] == 10) {
                            toggleMark(i, j)
                            clearSurroundingCells(i, j)
                        }
                        if (field[i][j] in 11..18) {
                            toggleMark(i, j)
                            exposeCell(i, j)
                        }
                    }
                }
            }
        } else {
            exposeCell(x, y)
        }
    }

    fun exposeCell(x: Int, y: Int) {
        if (field[x][y] in 10..19) {
            field[x][y] += 10
        } else {
            field[x][y] += 20
        }
    }
}

fun playGame() {
    val rows = 9
    val cols = 9
    val scanner = Scanner(System.`in`)
    print("How many mines do you want on the field? ")
    val numMines = scanner.nextInt()
    var field = MineField(rows, cols, 2, 0, 0)
    var firstMove = true
    while (true) {
        field.print()
        if (field.gameWon()) {
            println("Congratulations! You found all the mines!")
            break
        }
        print("Set/unset mines marks or claim a cell as free: ")
        val y = scanner.nextInt() - 1
        val x = scanner.nextInt() - 1
        val moveType = scanner.next()
        if (firstMove) {
            field = MineField(rows, cols, numMines, x, y)
            firstMove = false
        }
        if (field.isSelectable(x, y) && (moveType == "free" || moveType == "mine")) {
            if (moveType == "mine") {
                field.toggleMark(x, y)
            } else {
                if (field.isBomb(x, y)) {
                    field.exposeCell(x, y)
                    field.print()
                    println("You stepped on a mine and failed!")
                    break
                } else {
                    field.clearSurroundingCells(x, y)
                }
            }
        }
    }

}

fun main() {
    playGame()
}
