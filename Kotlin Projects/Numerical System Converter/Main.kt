package converter

import java.lang.Exception
import kotlin.math.pow
import kotlin.math.truncate

class Converter(private val num: String, private val radix: Int) {
    private var decimal = 0.0
    private var lowerNum = this.num.toLowerCase()

    init {
        if (radix !in 1..36) {
            throw error("")
        }
        this.decimal = convertToDecimal(this.lowerNum)
    }

    private fun convertToDecimal(num: String): Double {
        return if (this.radix == 10) {
            num.toDouble()
        } else {
            val wholeFraction = num.split(".")
            convertWholeNumberToDecimal(wholeFraction[0]) +
                    if (wholeFraction.size > 1)
                        convertFractionToDecimal(wholeFraction[1])
                    else
                        0.0
        }
    }

    private fun convertWholeNumberToDecimal(num: String): Double {
        var sum = 0.0
        val revNum = num.reversed()
        for (i in revNum.indices) {
            sum += getBase10Value(revNum[i]).times(radix.toDouble().pow(i))
        }
        return sum
    }

    private fun convertFractionToDecimal(num: String): Double {
        var sum = 0.0
        for (i in num.indices) {
            sum += getBase10Value(num[i]).div(radix.toDouble().pow(i + 1))
        }
        return sum
    }

    private fun getBase10Value(digit: Char): Int {
        return if (digit in '0'..'9') {
            digit.toInt() - 48
        } else {
            digit.toInt() - 87
        }
    }

    private fun getBase36Value(num: Int): Char {
        if (num in 0..9) {
            return (num + 48).toChar()
        }
        return (num + 87).toChar()
    }

    fun convertTo(radix: Int): Converter {
        if (radix !in 1..36) {
            throw error("")
        }
        if (radix == 1) {
            return Converter("1".repeat(this.decimal.toInt()), 1)
        }
        val wholeNum = truncate(this.decimal)
        val fractionalNum = this.decimal - wholeNum
        var numStr = "${convertWholeNumber(wholeNum, radix)}.${convertFractionalNumber(fractionalNum, radix)}"
        if (numStr.endsWith(".00000")) {
            numStr = numStr.substring(0, numStr.length - 6)
        }
        return Converter(numStr, radix)
    }

    private fun convertWholeNumber(number: Double, radix: Int): String {
        var numStr = ""
        var num = number
        while (num >= radix) {
            val digit = num % radix
            num = num.div(radix)
            numStr += getBase36Value(digit.toInt())
        }
        numStr += getBase36Value(num.toInt())
        return numStr.reversed()
    }

    private fun convertFractionalNumber(number: Double, radix: Int): String {
        var numStr = ""
        var fraction = number
        repeat(5) {
            fraction *= radix.toDouble()
            val num = truncate(fraction)
            fraction -= num
            numStr += getBase36Value(num.toInt())
        }
        return numStr
    }

    override fun toString(): String {
        return num
    }
}

fun main() {
    try {
        val sourceRadix = readLine()?.toInt() ?: throw error("")
        val number = readLine() ?: throw error("")
        val targetRadix = readLine()?.toInt() ?: throw error("")
        println(Converter(number, sourceRadix).convertTo(targetRadix))
    } catch (e: Exception) {
        println("error")
    }
}
