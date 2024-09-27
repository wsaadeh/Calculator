package com.saadeh.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun changeSign(view: View){
        val workingTv = findViewById<TextView>(R.id.workingsTV)
        val digitsOperators = digitsOperators()
        //if(digitsOperators.isEmpty()) return ""

        //digitsOperators[digitsOperators.size -1].toString().toFloat() * -1
        workingTv.text = ""
        for (i in digitsOperators.indices){
            var digit = digitsOperators[i]
            if (digit != '+' && digit != '-' && digit != '/' && digit != 'x' ){
                val vdigit = digit as Float
                digitsOperators[i] = vdigit * -1
            }
            workingTv.text = workingTv.text.toString() + digitsOperators[i].toString()
        }

    }

    fun percent(view: View){
        val workingTv = findViewById<TextView>(R.id.workingsTV)
        val digitsOperators = digitsOperators()

        val digit = digitsOperators[digitsOperators.lastIndex]
        if (digit != '+' && digit != '-' && digit != '/' && digit != 'x' ){
            val vdigit = digit as Float
            digitsOperators[digitsOperators.lastIndex] = vdigit / 100
        }
        workingTv.text = ""
        for (i in digitsOperators.indices){
            workingTv.text = workingTv.text.toString() + digitsOperators[i].toString()
        }


    }

    @SuppressLint("SuspiciousIndentation")
    fun numberAction(view: View){
        val workingsTv = findViewById<TextView>(R.id.workingsTV)


        if (view is Button){
            if (view.text == "."){
                if (canAddDecimal){
                    workingsTv.append(view.text)
                    canAddDecimal=false
                }
            }else
            workingsTv.append(view.text)

            canAddOperation = true
        }

    }
    fun operationAction(view: View){
        val workingsTv = findViewById<TextView>(R.id.workingsTV)


        if (view is Button && canAddOperation){
            workingsTv.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }
    fun allClearAction(view: View)
    {
        val workingsTv = findViewById<TextView>(R.id.workingsTV)
        val resultsTV = findViewById<TextView>(R.id.resultsTV)

        workingsTv.text = ""
        resultsTV.text = ""
    }
    fun backSpaceAction(view: View){
        val workingsTv = findViewById<TextView>(R.id.workingsTV)


        val length = workingsTv.length()
        if (length > 0){
            workingsTv.text = workingsTv.text.subSequence(0,length - 1 )
        }

    }
    fun equalsAction(view: View){
        val resultsTV = findViewById<TextView>(R.id.resultsTV)

        resultsTV.text = calculateResults()
    }

    private fun calculateResults(): String{
        val digitsOperators = digitsOperators()
        if(digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)

        if(timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)

        return result.toString()
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices){
            if (passedList[i] is Char && i != passedList.lastIndex)
            {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit

            }
        }

        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('/')){
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()

        var restartIndex = passedList.size

        for (i in passedList.indices){
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex){
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when(operator){
                    'x'->
                    {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/'->
                    {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    else ->
                    {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if (i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    private fun digitsOperators(): MutableList<Any>{
        val workingsTv = findViewById<TextView>(R.id.workingsTV)
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in workingsTv.text){
            if (character.isDigit() || character == '.')
                currentDigit += character
            else{
               list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }

        if (currentDigit != "")
            list.add(currentDigit.toFloat())

        return list
    }
}