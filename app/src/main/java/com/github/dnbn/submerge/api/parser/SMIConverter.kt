package com.github.dnbn.submerge.api.parser

import com.github.dnbn.submerge.api.subtitle.srt.SRTLine
import org.mozilla.universalchardet.UniversalDetector
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths


class SMIConverter() {
    companion object {
        private fun findFileEncoding(file: File?): String? {
            val buf = ByteArray(4096)
            val fis = FileInputStream(file)

            val detector = UniversalDetector(null)

            var nread: Int
            while (fis.read(buf).also { nread = it } > 0 && !detector.isDone) {
                detector.handleData(buf, 0, nread)
            }

            detector.dataEnd()

            val encoding = detector.detectedCharset
            if (encoding != null) {
                println("Detected encoding = $encoding")
            } else {
                println("No encoding detected.")
            }

            detector.reset()
            return encoding
        }

        fun convertToFile(path : String) : String {
            val cs = Charset.forName(findFileEncoding(File(path))!!)
            val list = Files.readAllLines(Paths.get(path), cs)

            val subLines = ArrayList<SRTLine>()

            for (line in list) {
                TODO()
            }
            return list.joinToString(separator = "\n")
        }
    }
}