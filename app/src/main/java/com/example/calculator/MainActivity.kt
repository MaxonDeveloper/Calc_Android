package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.calculation.R


class MainActivity : AppCompatActivity() {
    private val pi = 3.141592
    private var editText: EditText? = null
    private var editTextTwo: TextView? = null
    private val sign = listOf('+', '-', '/', '*', '.', '%')
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editText = findViewById(R.id.editText)
        editTextTwo = findViewById(R.id.textView)
    }

    fun clickButtonCalc(view: View) {
        var number = editText!!.text.toString()
        when (view.id) {
            R.id.luna -> {
                setContentView(R.layout.black_activity)
                editText = findViewById(R.id.editText)
                editTextTwo = findViewById(R.id.textView)

            }

            R.id.sun -> {
                setContentView(R.layout.activity_main)
                editText = findViewById(R.id.editText)
                editTextTwo = findViewById(R.id.textView)
            }

            R.id.imageButtonOne -> number += '1'
            R.id.imageButtonTwo -> number += '2'
            R.id.imageButtonTree -> number += '3'
            R.id.imageButtonFour -> number += '4'
            R.id.imageButtonFive -> number += '5'
            R.id.imageButtonSix -> number += '6'
            R.id.imageButtonSeven -> number += '7'
            R.id.imageButtonEight -> number += '8'
            R.id.imageButtonNine -> number += '9'
            R.id.imageButtonZero -> number += '0'
            R.id.imageButtonAC -> number = ""
            R.id.imageButtonDivision -> {
                if (number.length == 1 && number[0] == '-') {
                    number = ""
                } else if (number.isNotEmpty()) {
                    sign.forEach {
                        if (number[number.lastIndex] == it) {
                            number = number.dropLast(1)
                        }
                    }
                    number += '/'
                }
            }

            R.id.imageButtonPlus -> {
                if (number.length == 1 && number[0] == '-') {
                    number = ""
                } else if (number.isNotEmpty()) {
                    sign.forEach {
                        if (number[number.lastIndex] == it) {
                            number = number.dropLast(1)
                        }
                    }
                    number += '+'
                }
            }

            R.id.imageButtonMinus -> {
                if (number.length == 1 && number[0] == '-') {
                    number = ""
                } else if (number.isNotEmpty()) {
                    sign.forEach {
                        if (number[number.lastIndex] == it) {
                            number = number.dropLast(1)
                        }
                    }
                    number += '-'
                } else {
                    number += '-'
                }
            }

            R.id.imageButtonMultiplications -> {
                if (number.length == 1 && number[0] == '-') {
                    number = ""
                } else if (number.isNotEmpty()) {
                    sign.forEach {
                        if (number[number.lastIndex] == it) {
                            number = number.dropLast(1)
                        }
                    }
                    number += '*'
                }
            }

            R.id.imageButtonComma -> {
                if (number.isNotEmpty()) {
                    sign.forEach {
                        if (number[number.lastIndex] == it) {
                            number = number.dropLast(1)
                        }
                    }
                    number += '.'
                }
            }

            R.id.imageButtonPi -> {
                if (number.isEmpty()) {
                    number = pi.toString()
                } else {
                    sign.forEach {
                        if (number[number.lastIndex] == it) {
                            number += pi.toString()
                        }
                    }
                }
            }

            R.id.imageButtonStir -> number = number.dropLast(1)
            R.id.luna -> TODO()
            R.id.imageButtonEqually -> {
                editTextTwo!!.text = number
                val num = eval(number)
                number = if (num % 1.0 == 0.0) {
                    num.toInt().toString()
                } else {
                    num.toString()
                }

            }
        }
        editText!!.setText(number)
    }


    private fun eval(expr: String): Float {
        var index = 0 // current index
        val skipWhile =
            { cond: (Char) -> Boolean -> while (index < expr.length && cond(expr[index])) index++ }
        val tryRead =
            { c: Char -> (index < expr.length && expr[index] == c).also { if (it) index++ } }
        val skipWhitespaces = { skipWhile { it.isWhitespace() } }
        val tryReadOp =
            { op: Char -> skipWhitespaces().run { tryRead(op) }.also { if (it) skipWhitespaces() } }
        var rootOp: () -> Float = { 0.0f }

        val num = {
            if (tryReadOp('(')) {
                rootOp().also {
                    tryReadOp(')').also {
                        if (!it) throw IllegalExpressionException(
                            index,
                        )
                    }
                }
            } else {
                val start = index
                tryRead('-') or tryRead('+')
                skipWhile { it.isDigit() || it == '.' }
                try {
                    expr.substring(start, index).toFloat()
                } catch (e: NumberFormatException) {
                    throw IllegalExpressionException(start, "Invalid number", cause = e)
                }
            }
        }

        fun binary(left: () -> Float, op: Char): List<Float> = mutableListOf(left()).apply {
            while (tryReadOp(op)) addAll(binary(left, op))
        }

        val div = { binary(num, '/').reduce { a, b -> a / b } }
        val mul = { binary(div, '*').reduce { a, b -> a * b } }
        val sub = { binary(mul, '-').reduce { a, b -> a - b } }
        val add = { binary(sub, '+').reduce { a, b -> a + b } }

        rootOp = add
        return rootOp().also {
            if (index < expr.length) throw IllegalExpressionException(
                index,
                "Invalid expression"
            )
        }
    }

    class IllegalExpressionException(
        val index: Int,
        message: String? = null,
        cause: Throwable? = null
    ) :
        IllegalArgumentException("$message at:$index", cause)
}

