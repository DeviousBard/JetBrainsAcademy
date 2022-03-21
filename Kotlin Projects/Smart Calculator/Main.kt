package calculator

import java.lang.Exception
import java.math.BigInteger
import kotlin.math.exp
import kotlin.math.pow

class InvalidIdentifierException: Exception()

class InvalidAssignmentException: Exception()

class UnknownVariableException: Exception()

class InvalidExpressionException: Exception()

class PostFix(private val expression: String) {

    companion object {
        private val operators = setOf("+", "-", "/", "*", "^")

        @JvmStatic
        fun fromInfix(infix: String): PostFix {
            var postFix = ""
            val scrubbed = scrubExpression(infix)
            val terms = scrubbed.split(" ")
            val stack = mutableListOf<String>()
            for (term in terms) {
                when (term) {
                    "(" -> stack.push(term)
                    in operators -> {
                        while (getPrecedence(stack.peek()) >= getPrecedence(term)) {
                            postFix += "${stack.pop()} "
                        }
                        stack.push(term)
                    }
                    ")" -> {
                        while (stack.peek() != "(" && stack.isNotEmpty()) {
                            postFix += "${stack.pop()} "
                        }
                        if (stack.peek() == "(") {
                            stack.pop()
                        } else {
                            throw InvalidExpressionException()
                        }
                    }
                    else -> postFix += "$term "
                }
            }
            return PostFix(postFix.trim())
        }

        @JvmStatic
        private fun scrubExpression(expression: String): String {
            // Replace multiple +'s in a row with a single "+"
            var scrubbed = expression.replace("""\s+\+{2,}\s+""".toRegex(), " + ")

            // Replace multiple -'s in a row with a single "+", if there's an even number,
            // or a single "-", if there's an odd number
            while (true) {
                val minuses = """\s+-{2,}\s+""".toRegex().findAll(scrubbed)
                if (!minuses.iterator().hasNext()) {
                    break
                }
                val match = minuses.iterator().next()
                val result = match.value.trim()
                scrubbed = scrubbed.substring(0, match.range.first + 1) + (if (result.length % 2 == 0) "+" else "-") + scrubbed.substring(match.range.last)
            }
            // Replace all single -'s with " + 0 - " (to make all unary minus signs binary)
            scrubbed = scrubbed.replace("-", "+0-")

            // Surround all operators with spaces
            scrubbed = scrubbed.replace("""([+-/*^)(])""".toRegex(), " $1 ")

            // Replace multiple spaces with a single space
            scrubbed = scrubbed.replace("""\s{2,}""".toRegex(), " ")

            // Remove any leading "+" symbols
            scrubbed = if (scrubbed.trim().startsWith("+")) scrubbed.trim().substring(1) else scrubbed.trim()

            // Surround the infix expression with parentheses to make the algorithm easier to implement
            scrubbed = "( $scrubbed )"
            return scrubbed
        }

        @JvmStatic
        private fun getPrecedence(operator: String?): Int {
            return when (operator) {
                "+", "-" -> 1
                "*", "/" -> 2
                "^" -> 3
                else -> 0
            }
        }
    }

    fun solve(): BigInteger {
        val stack = mutableListOf<BigInteger?>()
        val terms = this.expression.split(" ")
        for (term in terms) {
            when (term) {
                in operators -> {
                    if (stack.size >= 2) {
                        stack.push(applyOperator(term, stack.pop()!!, stack.pop()!!))
                    } else {
                        throw InvalidExpressionException()
                    }
                }
                else -> {
                    val value = if (VariableManager.hasVariable(term)) {
                        VariableManager.getValue(term)
                    } else {
                        try {
                            BigInteger(term)
                        } catch (e: Exception) {
                            throw UnknownVariableException()
                        }
                    }
                    stack.push(value)
                }
            }
        }
        return if (stack.size == 1) stack.pop()!! else throw InvalidExpressionException()
    }

    private fun applyOperator(operator: String, num1: BigInteger, num2: BigInteger): BigInteger {
        return when (operator) {
            "^" -> num2.pow(num1.toInt())
            "*" -> num2.times(num1)
            "/" -> num2.div(num1)
            "+" -> num2.plus(num1)
            "-" -> num2.minus(num1)
            else -> throw error("Invalid operator in applyOperator(String, BigInteger?, BigInteger?): \"$operator\".")
        }
    }

    override fun toString(): String {
        return this.expression
    }
}

class VariableManager {
    companion object {
        private val variableMap = mutableMapOf<String, BigInteger>()

        @JvmStatic
        fun setVariable(name: String, value: String) {
            val varName = name.trim()
            val varValue = value.trim()
            if (!isValidIdentifier(varName)) {
                throw InvalidIdentifierException()
            }
            if (isValidIdentifier(varValue)) {
                if (hasVariable(varValue)) {
                    try {
                        variableMap[varName] = getValue(varValue)
                    } catch(e: Exception) {
                        throw InvalidAssignmentException()
                    }
                } else {
                    throw UnknownVariableException()
                }
            } else {
                try {
                    variableMap[varName] = BigInteger(varValue)
                } catch (e: Exception) {
                    throw InvalidAssignmentException()
                }
            }
        }

        @JvmStatic
        fun getValue(name: String): BigInteger {
            if (!hasVariable(name)) {
                throw UnknownVariableException()
            }
            return variableMap[name]!!
        }

        @JvmStatic
        private fun isValidIdentifier(name: String): Boolean {
            if (name.contains("""[^A-Za-z]""".toRegex())) {
                return false
            }
            return true
        }

        @JvmStatic
        fun hasVariable(name: String): Boolean {
            return variableMap.containsKey(name)
        }
    }
}

fun <T> MutableList<T>.push(item: T) = this.add(this.count(), item)
fun <T> MutableList<T>.pop(): T? = if (this.isNotEmpty()) this.removeAt(this.count() - 1) else null
fun <T> MutableList<T>.peek(): T? = if (this.isNotEmpty()) this[this.count() - 1] else null

fun main() {
    loop@ while (true) {
        val input = readLine()!!
        when {
            input == "/exit" -> {
                println("Bye!")
                break@loop
            }
            input == "/help" -> {
                println("The program solves mathematical expressions.")
            }
            input == "" -> {
                // Ignore empty input
            }
            input.contains("=") -> {
                try {
                    val assignment = input.split("=")
                    if (assignment.size != 2) {
                        throw InvalidAssignmentException()
                    }
                    VariableManager.setVariable(assignment[0], assignment[1])
                } catch (e: InvalidAssignmentException) {
                    println("Invalid assignment")
                } catch (e: InvalidIdentifierException) {
                    println("Invalid identifier")
                } catch (e: UnknownVariableException) {
                    println("Unknown variable")
                }
            }
            input.startsWith("/") -> {
                println("Unknown command")
            }
            else -> {
                try {
                    println(PostFix.fromInfix(input).solve())
                } catch (e: InvalidExpressionException) {
                    println("Invalid expression")
                } catch (e: UnknownVariableException) {
                    println("Unknown variable")
                }
            }
        }
    }
}

