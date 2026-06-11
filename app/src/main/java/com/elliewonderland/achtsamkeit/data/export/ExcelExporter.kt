package com.elliewonderland.achtsamkeit.data.export

import com.elliewonderland.achtsamkeit.model.Entry
import java.io.File
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Writes entries as a real .xlsx (Office Open XML / ZIP+XML) without an external library.
 */
object ExcelExporter {

    fun write(entries: List<Entry>, out: File) {
        ZipOutputStream(out.outputStream().buffered()).use { zip ->
            zip.putEntry("[Content_Types].xml", contentTypes())
            zip.putEntry("_rels/.rels", rels())
            zip.putEntry("xl/workbook.xml", workbook())
            zip.putEntry("xl/_rels/workbook.xml.rels", workbookRels())
            zip.putEntry("xl/styles.xml", styles())
            zip.putEntry("xl/sharedStrings.xml", sharedStrings(entries))
            zip.putEntry("xl/worksheets/sheet1.xml", sheet(entries))
        }
    }

    // ── XML building blocks ────────────────────────────────────────────────────────

    private fun contentTypes() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml"  ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml"
    ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml"
    ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/sharedStrings.xml"
    ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml"/>
  <Override PartName="/xl/styles.xml"
    ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
</Types>"""

    private fun rels() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument"
    Target="xl/workbook.xml"/>
</Relationships>"""

    private fun workbook() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"
  xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>
    <sheet name="Einträge" sheetId="1" r:id="rId1"/>
  </sheets>
</workbook>"""

    private fun workbookRels() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet"
    Target="worksheets/sheet1.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/sharedStrings"
    Target="sharedStrings.xml"/>
  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles"
    Target="styles.xml"/>
</Relationships>"""

    private fun styles() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts><font><sz val="11"/><name val="Calibri"/></font>
         <font><b/><sz val="11"/><name val="Calibri"/></font></fonts>
  <fills><fill><patternFill patternType="none"/></fill>
         <fill><patternFill patternType="gray125"/></fill></fills>
  <borders><border><left/><right/><top/><bottom/><diagonal/></border></borders>
  <cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
  <cellXfs>
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
    <xf numFmtId="0" fontId="1" fillId="0" borderId="0" xfId="0"/>
  </cellXfs>
</styleSheet>"""

    // Shared-String-Tabelle: alle Strings indizieren (Excel-Standard)
    private fun sharedStrings(entries: List<Entry>): String {
        val strings = buildSharedStrings(entries)
        val sb = StringBuilder()
        sb.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        sb.append("""<sst xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" count="${strings.size}" uniqueCount="${strings.size}">""")
        for (s in strings) sb.append("<si><t>${s.xmlEscape()}</t></si>")
        sb.append("</sst>")
        return sb.toString()
    }

    private fun buildSharedStrings(entries: List<Entry>): List<String> {
        val list = mutableListOf<String>()
        // Header
        list += listOf("Datum", "Typ", "Stimmung", "Energielevel", "Bewertung",
                       "Tags", "Achtsamkeits-Fokus", "Freier Text")
        for (e in entries) {
            list += e.dateStr
            list += if (e.type == "morning") "Morgen" else "Abend"
            list += moodLabel(e.mood)
            list += energyLabel(e.energyLevel)
            list += e.tags.joinToString(", ")
            list += e.mindfulnessFocus
            list += e.freeText
        }
        return list
    }

    private fun sheet(entries: List<Entry>): String {
        val strings = buildSharedStrings(entries)
        val idx     = strings.withIndex().associate { (i, s) -> s to i }

        fun ssi(s: String) = idx[s] ?: 0

        val sb = StringBuilder()
        sb.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        sb.append("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">""")
        sb.append("<sheetData>")

        // Kopfzeile (fett, s=1)
        sb.append("""<row r="1">""")
        val headers = listOf("Datum","Typ","Stimmung","Energielevel","Bewertung","Tags","Achtsamkeits-Fokus","Freier Text")
        headers.forEachIndexed { c, h -> sb.append("""<c r="${col(c)}1" t="s" s="1"><v>${ssi(h)}</v></c>""") }
        sb.append("</row>")

        entries.forEachIndexed { i, e ->
            val r = i + 2
            val cells = listOf(
                e.dateStr,
                if (e.type == "morning") "Morgen" else "Abend",
                moodLabel(e.mood),
                energyLabel(e.energyLevel),
                e.dayRating.toString(),   // Zahl
                e.tags.joinToString(", "),
                e.mindfulnessFocus,
                e.freeText,
            )
            sb.append("""<row r="$r">""")
            cells.forEachIndexed { c, v ->
                if (c == 4) { // Bewertung als Zahl
                    sb.append("""<c r="${col(c)}$r"><v>$v</v></c>""")
                } else {
                    sb.append("""<c r="${col(c)}$r" t="s"><v>${ssi(v)}</v></c>""")
                }
            }
            sb.append("</row>")
        }

        sb.append("</sheetData></worksheet>")
        return sb.toString()
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private fun col(index: Int): String {
        var n = index
        var result = ""
        do {
            result = ('A' + (n % 26)) + result
            n = n / 26 - 1
        } while (n >= 0)
        return result
    }

    private fun String.xmlEscape() = replace("&", "&amp;")
        .replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

    private fun moodLabel(mood: String) = when (mood) {
        "joy"     -> "Freude"
        "balance" -> "Ausgeglichen"
        "sadness" -> "Traurigkeit"
        "stress"  -> "Stress"
        else      -> mood
    }

    private fun energyLabel(level: String) = when (level) {
        "full"  -> "Hoch"
        "mid"   -> "Mittel"
        "empty" -> "Niedrig"
        else    -> level
    }

    private fun ZipOutputStream.putEntry(name: String, content: String) {
        putNextEntry(ZipEntry(name))
        write(content.toByteArray(Charsets.UTF_8))
        closeEntry()
    }
}
