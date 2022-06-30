package connectfour

import java.util.*
import kotlin.system.exitProcess

class Dimension(val rows: Int, val columns: Int)

class Player(val name: String, val number: Int)

class Board(val dimension: Dimension) {

    private val board: MutableList<MutableList<Int>> =
        MutableList(dimension.columns) { MutableList(dimension.rows) { -1 } }

    fun addPiece(player: Player) {
        var column: Int
        while (true) {
            println("${player.name}'s turn:")
            var c: Int
            try {
                val input: String = readln().lowercase(Locale.getDefault())
                if (input == "end") {
                    println("Game over!")
                    exitProcess(0)
                } else {
                    c = input.toInt()
                }
                if (c >= 1 && c <= dimension.columns) {
                    if (columnIsFull(c)) {
                        println("Column $c is full")
                    } else {
                        column = c - 1
                        break
                    }
                } else {
                    println("The column number is out of range (1 - ${dimension.columns})")
                }
            } catch (e: Exception) {
                println("Incorrect column number")
            }
        }
        for (row in dimension.rows - 1 downTo 0) {
            if (board[column][row] == -1) {
                board[column][row] = player.number
                break
            }
        }
    }

    private fun columnIsFull(column: Int): Boolean {
        return board[column - 1][0] != -1
    }

    fun printBoard() {
        for (c in 0 until dimension.columns) {
            print(" ${c + 1}")
        }
        println()
        for (r in 0 until dimension.rows) {
            for (c in 0..dimension.columns) {
                print("║")
                if (c < dimension.columns) {
                    when (board[c][r]) {
                        -1 -> {
                            print(" ")
                        }
                        0 -> {
                            print("o")
                        }
                        1 -> {
                            print("*")
                        }
                    }
                }
            }
            println()
        }
        for (c in 0..dimension.columns) {
            when (c) {
                0 -> {
                    print("╚═")
                }
                dimension.columns -> {
                    print("╝")
                }
                else -> {
                    print("╩═")
                }
            }
        }
        println()
    }

    fun get(c: Int, r: Int): Int {
        return board[c][r]
    }
}

enum class Direction {
    HORIZONTAL, VERTICAL
}

class Game(_players: MutableList<Player>? = null, _dimension: Dimension? = null) {

    val players: MutableList<Player>
    val board: Board

    init {
        players = if (_players == null) {
            println("First player's name:")
            val p1 = Player(readln(), 0)
            println("Second player's name:")
            val p2 = Player(readln(), 1)
            mutableListOf(p1, p2)
        } else {
            _players
        }

        board = if (_dimension != null) Board(_dimension) else Board(askForDimension())
    }

    private fun askForDimension(): Dimension {
        var valid = false
        var rows = 6
        var columns = 7
        while (!valid) {
            println("Set the board dimensions (Rows x Columns)")
            println("Press Enter for default (6 x 7)")
            val line: String = readln().replace(" ", "").replace("\t", "").lowercase(Locale.getDefault())
            if (line == "") {
                break
            }
            try {
                val (r, c) = line.split("x").map { it.toInt() }
                if (r in 5..9) {
                    if (c in 5..9) {
                        valid = true
                        rows = r
                        columns = c
                    } else {
                        println("Board columns should be from 5 to 9")
                    }
                } else {
                    println("Board rows should be from 5 to 9")
                }
            } catch (e: Exception) {
                println("Invalid Input")
            }
        }
        return Dimension(rows, columns)
    }

    fun isGameOver(): Int {
        if (isDraw()) {
            board.printBoard()
            println("It is a draw")
            return 2
        }
        for (p in 0..1) {
            if (isVerticalWin(p) || isHorizontalWin(p) || isDiagonalWin(p)) {
                board.printBoard()
                println("Player ${players[p].name} won")
                return p
            }
        }
        return -1
    }

    private fun isVerticalWin(playerNum: Int): Boolean {
        return has4InALine(playerNum, Direction.VERTICAL)
    }

    private fun isHorizontalWin(playerNum: Int): Boolean {
        return has4InALine(playerNum, Direction.HORIZONTAL)
    }

    private fun isDiagonalWin(playerNum: Int): Boolean {
        val maxCol = board.dimension.columns
        val maxRow = board.dimension.rows
        val minBDiagonal = (-1) * maxRow + 1
        val forwardDiagonal = mutableListOf<MutableList<Int>>()
        val backwardDiagonal = mutableListOf<MutableList<Int>>()
        for (i in 0 until maxRow + maxCol - 1) {
            forwardDiagonal.add(mutableListOf())
            backwardDiagonal.add(mutableListOf())
        }
        for (x in 0 until maxCol) {
            for (y in 0 until maxRow) {
                forwardDiagonal[x + y].add(board.get(x, y))
                backwardDiagonal[x - y - minBDiagonal].add(board.get(x, y))
            }
        }
        if (isDiagonalWinByDirection(forwardDiagonal, playerNum)) {
            return true
        }
        if (isDiagonalWinByDirection(backwardDiagonal, playerNum)) {
                return true
        }
        return false
    }

    private fun isDiagonalWinByDirection(diagonalDir: MutableList<MutableList<Int>>, playerNum: Int): Boolean {
        for (diagonal in diagonalDir) {
            if (diagonal.size >= 4) {
                var count = 0
                for (i in diagonal) {
                    if (i == playerNum) {
                        count++
                        if (count >= 4) {
                            return true
                        }
                    } else {
                        count = 0
                    }
                }
            }
        }
        return false
    }

    private fun has4InALine(playerNum: Int, direction: Direction): Boolean {
        val endI: Int = if (direction == Direction.HORIZONTAL) board.dimension.rows else board.dimension.columns
        val endJ: Int = if (direction == Direction.VERTICAL) board.dimension.rows else board.dimension.columns
        for (i in 0 until endI) {
            var count = 0
            for (j in 0 until endJ) {
                if (board.get(if (direction == Direction.HORIZONTAL) j else i, if (direction == Direction.VERTICAL) j else i) == playerNum) {
                    count++
                    if (count >= 4) {
                        return true
                    }
                } else {
                    count = 0
                }
            }
        }
        return false
    }

    private fun isDraw(): Boolean {
        for (c in 0 until board.dimension.columns) {
            for (r in 0 until board.dimension.rows) {
                if (board.get(c, r) == -1) {
                    return false
                }
            }
        }
        return true
    }
}

fun main() {
    println("Connect Four")
    val scores = mutableListOf(0, 0)
    var numGames = 1
    var game = Game()
    while(true) {
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        println("Input a number of games:")
        try {
            val numGamesString: String = readln()
            if (numGamesString == "") {
                break
            }
            val numGamesInput = numGamesString.toInt()
            if (numGamesInput >= 1) {
                numGames = numGamesInput
                break
            }
            println("Invalid input")
        } catch (e: Exception) {
            println("Invalid input")
        }
    }
    println(game.players[0].name + " VS " + game.players[1].name)
    println("" + game.board.dimension.rows + " X " + game.board.dimension.columns + " board")
    if (numGames == 1) {
        println("Single game")
    } else {
        println("Total $numGames games")
    }
    for (i in 0 until numGames) {
        var currentPlayer = i % 2
        if (numGames > 1) {
            println("Game #${i + 1}")
        }
        var gameResult = -1
        while (gameResult < 0) {
            game.board.printBoard()
            game.board.addPiece(game.players[currentPlayer])
            gameResult = game.isGameOver()
            currentPlayer = (currentPlayer + 1) % 2
        }
        if (gameResult == 2) {
            scores[0]++
            scores[1]++
        } else {
            scores[gameResult] += 2
        }
        if (numGames > 1) {
            println("Score")
            println("${game.players[0].name}: ${scores[0]} ${game.players[1].name}: ${scores[1]}")
            game = Game(game.players, game.board.dimension)
        }
    }
    println("Game over!")
    exitProcess(0)
}
