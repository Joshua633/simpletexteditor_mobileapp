package com.example.texteditor

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var draggableText: EditText
    private lateinit var mainLayout: RelativeLayout
    private var isBold = false
    private var isItalic = false
    private var currentColor = Color.BLACK

    // Undo/Redo stacks
    private val undoStack: Stack<String> = Stack()
    private val redoStack: Stack<String> = Stack()
    private var isTextChanging = false

    // For dragging
    private var dX = 0f
    private var dY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        draggableText = findViewById(R.id.draggableText)
        mainLayout = findViewById(R.id.mainLayout)

        // Initialize with empty text
        undoStack.push("")

        // Track changes for undo/redo
        draggableText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isTextChanging) return

                // Save current state to undo stack
                undoStack.push(s.toString())
                // Clear redo stack when new change is made
                redoStack.clear()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        setupDrag()
        setupToolbar()
    }

    private fun setupDrag() {
        draggableText.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                }
            }
            false
        }
    }

    private fun setupToolbar() {
        val btnBold: Button = findViewById(R.id.btnBold)
        val btnItalic: Button = findViewById(R.id.btnItalic)
        val btnIncrease: Button = findViewById(R.id.btnIncrease)
        val btnDecrease: Button = findViewById(R.id.btnDecrease)
        val btnUndo: Button = findViewById(R.id.btnUndo)
        val btnRedo: Button = findViewById(R.id.btnRedo)

        // Color buttons
        val btnBlack: Button = findViewById(R.id.btnBlack)
        val btnRed: Button = findViewById(R.id.btnRed)
        val btnBlue: Button = findViewById(R.id.btnBlue)
        val btnGreen: Button = findViewById(R.id.btnGreen)
        val btnPurple: Button = findViewById(R.id.btnPurple)
        val btnOrange: Button = findViewById(R.id.btnOrange)

        btnBold.setOnClickListener {
            isBold = !isBold
            updateTextStyle()

            // Update button appearance
            btnBold.setBackgroundColor(if (isBold) Color.LTGRAY else Color.TRANSPARENT)
        }

        btnItalic.setOnClickListener {
            isItalic = !isItalic
            updateTextStyle()

            // Update button appearance
            btnItalic.setBackgroundColor(if (isItalic) Color.LTGRAY else Color.TRANSPARENT)
        }

        // Color button handlers
        btnBlack.setOnClickListener { setTextColor(Color.parseColor(it.tag.toString())) }
        btnRed.setOnClickListener { setTextColor(Color.parseColor(it.tag.toString())) }
        btnBlue.setOnClickListener { setTextColor(Color.parseColor(it.tag.toString())) }
        btnGreen.setOnClickListener { setTextColor(Color.parseColor(it.tag.toString())) }
        btnPurple.setOnClickListener { setTextColor(Color.parseColor(it.tag.toString())) }
        btnOrange.setOnClickListener { setTextColor(Color.parseColor(it.tag.toString())) }

        btnIncrease.setOnClickListener {
            val currentSize = draggableText.textSize / resources.displayMetrics.scaledDensity
            draggableText.textSize = currentSize + 2
        }

        btnDecrease.setOnClickListener {
            val currentSize = draggableText.textSize / resources.displayMetrics.scaledDensity
            if (currentSize > 8) {
                draggableText.textSize = currentSize - 2
            }
        }

        btnUndo.setOnClickListener {
            if (undoStack.size > 1) {
                isTextChanging = true
                val currentText = draggableText.text.toString()
                undoStack.pop() // Remove current state
                val previousText = undoStack.peek()
                redoStack.push(currentText)
                draggableText.setText(previousText)
                draggableText.setSelection(previousText.length)
                isTextChanging = false
            }
        }

        btnRedo.setOnClickListener {
            if (redoStack.isNotEmpty()) {
                isTextChanging = true
                val nextText = redoStack.pop()
                undoStack.push(nextText)
                draggableText.setText(nextText)
                draggableText.setSelection(nextText.length)
                isTextChanging = false
            }
        }
    }

    private fun updateTextStyle() {
        var style = Typeface.NORMAL
        if (isBold && isItalic) {
            style = Typeface.BOLD_ITALIC
        } else if (isBold) {
            style = Typeface.BOLD
        } else if (isItalic) {
            style = Typeface.ITALIC
        }

        draggableText.setTypeface(Typeface.DEFAULT, style)
    }

    private fun setTextColor(color: Int) {
        currentColor = color
        draggableText.setTextColor(color)
    }
}