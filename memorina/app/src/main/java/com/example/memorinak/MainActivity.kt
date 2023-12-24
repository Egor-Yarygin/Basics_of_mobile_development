package com.example.memorinak

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private val numberOfPairs = 8
    private lateinit var cardViews: List<ImageView>
    private var randomArray: List<String> = generateRandomArray()
    private var firstCard: ImageView? = null
    private var secondCard: ImageView? = null
    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(applicationContext).apply {
            orientation = LinearLayout.VERTICAL
        }

        val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            weight = 1f
        }

        cardViews = (0 until numberOfPairs * 2).map {
            ImageView(applicationContext).apply {
                setImageResource(R.drawable.front)
                layoutParams = params
                tag = randomArray[it]
                setOnClickListener { openCard(this) }
            }
        }

        val rows = cardViews.chunked(4).map { rowImages ->
            LinearLayout(applicationContext).apply {
                rowImages.forEach { imageView ->
                    addView(imageView)
                }
            }
        }

        rows.forEach {
            layout.addView(it)
        }

        val resetButton = Button(applicationContext).apply {
            text = "New Game"
            textSize = 18f
            setBackgroundColor(Color.BLUE)
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
                bottomMargin = 16
            }
            setOnClickListener { resetGame() }
        }
        layout.addView(resetButton)

        setContentView(layout)
    }

    private fun resetGame() {
        cardViews.forEach {
            it.visibility = View.VISIBLE
            it.isClickable = true
            it.setImageResource(R.drawable.front)
        }

        firstCard = null
        secondCard = null
        count = 0

        randomArray = generateRandomArray()
        cardViews.forEachIndexed { index, imageView ->
            imageView.tag = randomArray[index]
        }
    }

    private suspend fun guessedPair(firstCard: ImageView, secondCard: ImageView) {
        delay(300)
        count += 1
        withContext(Dispatchers.Main) {
            firstCard.visibility = View.INVISIBLE
            firstCard.isClickable = false
            secondCard.visibility = View.INVISIBLE
            secondCard.isClickable = false
        }
        if (count == 8) {
            delay(100)
            showToast("You WIN")
        }
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun showPair(firstCard: ImageView, secondCard: ImageView) {
        val tag1 = resources.getIdentifier("i${firstCard.tag}", "drawable", packageName)
        val tag2 = resources.getIdentifier("i${secondCard.tag}", "drawable", packageName)
        if (tag1 == tag2) {
            guessedPair(firstCard, secondCard)
        } else {
            delay(1000)
            withContext(Dispatchers.Main) {
                firstCard.setImageResource(R.drawable.front)
                secondCard.setImageResource(R.drawable.front)
                firstCard.isClickable = true
                secondCard.isClickable = true
            }
        }
    }

    private fun generateRandomArray(): List<String> {
        val values = listOf("0", "1", "2", "3", "4", "5", "6", "7")
        val allValues = values + values
        return allValues.shuffled()
    }

    private fun openCard(card: ImageView) {
        GlobalScope.launch {
            delay(300)
            if (firstCard == null || secondCard == null) {
                val resourceId = resources.getIdentifier("i${card.tag}", "drawable", packageName)
                card.setImageResource(resourceId)

                if (firstCard == null) {
                    firstCard = card
                    card.isClickable = false
                } else {
                    secondCard = card
                    card.isClickable = false
                    showPair(firstCard!!, secondCard!!)
                    firstCard = null
                    secondCard = null
                }
            }
        }
    }
}
