package com.hiking.art.programs.shared

import com.hiking.art.base.Program
import com.hiking.art.modules.printlnErr
import java.awt.Desktop
import java.io.File

class OpenAppFileInDesktopProgram(
    private val file: File
) : Program(
    title = "Open File"
) {

    override fun onStart() {
        val desktop = try {
            Desktop.getDesktop()
        } catch (e: Exception) {
            printlnErr("Failed to access desktop: ${e.message}")
            return
        }
        desktop.open(file)
        println("Opened ${file.name} in desktop.")
    }
}