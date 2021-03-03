package com.github.dnbn.submerge.api.parser

import java.io.BufferedReader
import java.io.FileReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class SMIConverter() {
    companion object {
        fun convertToFile(path : String) : String {
            var line : String? = null
            var lines : String = ""
            val buf = BufferedReader(FileReader(path))
            line = buf.readLine()
            while(line != null) {
                lines += line + '\n'
                line = buf.readLine()
            }
//            val cs = StandardCharsets.UTF_8
//            val list = Files.readAllLines(Paths.get(path), cs)
//            return list.joinToString(separator = "\n")
            return lines
        }
    }
}