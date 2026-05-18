package com.elliewonderland.achtsamkeit.data.export

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.elliewonderland.achtsamkeit.model.Entry
import java.io.File

object PdfExporter {

    private const val PAGE_WIDTH  = 595   // A4 bei 72 dpi
    private const val PAGE_HEIGHT = 842
    private const val MARGIN      = 48f
    private const val LINE_H      = 18f

    fun write(entries: List<Entry>, displayName: String, out: File) {
        val doc = PdfDocument()

        val paintTitle = Paint().apply {
            textSize = 18f
            color    = Color.BLACK
            isFakeBoldText = true
        }
        val paintHeader = Paint().apply {
            textSize = 10f
            color    = Color.DKGRAY
            isFakeBoldText = true
        }
        val paintBody = Paint().apply {
            textSize = 9f
            color    = Color.BLACK
        }
        val paintSoft = Paint().apply {
            textSize = 9f
            color    = Color.GRAY
        }
        val paintLine = Paint().apply {
            color       = Color.LTGRAY
            strokeWidth = 0.5f
        }

        var pageNum     = 1
        var y           = MARGIN + 40f
        var currentPage = startPage(doc, pageNum)
        var canvas      = currentPage.canvas

        fun newPage() {
            doc.finishPage(currentPage)
            pageNum++
            y           = MARGIN + 20f
            currentPage = startPage(doc, pageNum)
            canvas      = currentPage.canvas
        }

        fun ensureSpace(needed: Float) {
            if (y + needed > PAGE_HEIGHT - MARGIN) newPage()
        }

        // ── Titelzeile ────────────────────────────────────────────────────────
        canvas.drawText("Meine Achtsamkeits-Einträge", MARGIN, MARGIN + 20f, paintTitle)
        if (displayName.isNotBlank()) {
            canvas.drawText(displayName, MARGIN, MARGIN + 36f, paintSoft)
        }
        canvas.drawLine(MARGIN, MARGIN + 44f, PAGE_WIDTH - MARGIN, MARGIN + 44f, paintLine)
        y = MARGIN + 54f

        // ── Einträge ──────────────────────────────────────────────────────────
        for (entry in entries) {
            ensureSpace(LINE_H * 6)

            val typeLabel = if (entry.type == "morning") "Morgenroutine" else "Abendroutine"
            canvas.drawText("${entry.dateStr}  ·  $typeLabel", MARGIN, y, paintHeader)
            y += LINE_H

            val mood   = when (entry.mood) {
                "joy"     -> "Freude"
                "balance" -> "Ausgeglichen"
                "sadness" -> "Traurigkeit"
                "stress"  -> "Stress"
                else      -> entry.mood
            }
            val energy = when (entry.energyLevel) {
                "full"  -> "Energie hoch"
                "mid"   -> "Energie mittel"
                "empty" -> "Energie niedrig"
                else    -> entry.energyLevel
            }
            canvas.drawText("Stimmung: $mood   |   $energy", MARGIN, y, paintBody)
            y += LINE_H

            if (entry.dayRating > 0) {
                canvas.drawText("Tagesbewertung: ${entry.dayRating}/5", MARGIN, y, paintBody)
                y += LINE_H
            }

            if (entry.tags.isNotEmpty()) {
                canvas.drawText("Tags: ${entry.tags.joinToString(", ")}", MARGIN, y, paintSoft)
                y += LINE_H
            }

            if (entry.guidedAnswer.isNotBlank()) {
                ensureSpace(LINE_H * 2 + wrapLines(entry.guidedAnswer).size * LINE_H)
                canvas.drawText("Reflexionsfrage: ${entry.guidedQuestion}", MARGIN, y, paintSoft)
                y += LINE_H
                for (line in wrapLines(entry.guidedAnswer)) {
                    ensureSpace(LINE_H)
                    canvas.drawText(line, MARGIN + 12f, y, paintBody)
                    y += LINE_H
                }
            }

            if (entry.freeText.isNotBlank()) {
                ensureSpace(LINE_H + wrapLines(entry.freeText).size * LINE_H)
                canvas.drawText("Notiz:", MARGIN, y, paintSoft)
                y += LINE_H
                for (line in wrapLines(entry.freeText)) {
                    ensureSpace(LINE_H)
                    canvas.drawText(line, MARGIN + 12f, y, paintBody)
                    y += LINE_H
                }
            }

            canvas.drawLine(MARGIN, y + 4f, PAGE_WIDTH - MARGIN, y + 4f, paintLine)
            y += 14f
        }

        doc.finishPage(currentPage)
        out.outputStream().use { doc.writeTo(it) }
        doc.close()
    }

    private fun startPage(doc: PdfDocument, num: Int): PdfDocument.Page {
        val info = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, num).create()
        return doc.startPage(info)
    }

    // Einfacher Zeilenumbruch bei ~85 Zeichen
    private fun wrapLines(text: String, maxChars: Int = 85): List<String> {
        val result = mutableListOf<String>()
        for (paragraph in text.lines()) {
            if (paragraph.length <= maxChars) {
                result += paragraph
            } else {
                val words = paragraph.split(" ")
                var line  = StringBuilder()
                for (word in words) {
                    if (line.length + word.length + 1 > maxChars) {
                        result += line.toString().trim()
                        line    = StringBuilder(word)
                    } else {
                        if (line.isNotEmpty()) line.append(' ')
                        line.append(word)
                    }
                }
                if (line.isNotEmpty()) result += line.toString().trim()
            }
        }
        return result.ifEmpty { listOf("") }
    }
}
