package com.github.alexpl292.ijreleaseplan.services

import com.intellij.openapi.project.Project
import com.github.alexpl292.ijreleaseplan.MyBundle
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.util.Consumer
import java.awt.event.MouseEvent

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }

    /**
     * Chosen by fair dice roll, guaranteed to be random.
     */
    fun getRandomNumber() = 4
}

class PlannerStatusBarWidgetFactory : StatusBarWidgetFactory {
    override fun getId(): String = "ij-plan"

    override fun getDisplayName(): String = "ij-plan"

    override fun isAvailable(project: Project): Boolean = true

    override fun createWidget(project: Project): StatusBarWidget {
        return VimStatusBar()
    }

    override fun disposeWidget(widget: StatusBarWidget) {
    }

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
}

class VimStatusBar : StatusBarWidget, StatusBarWidget.TextPresentation {
    override fun dispose() {
    }

    override fun ID(): String = "ij-plan"

    override fun install(statusBar: StatusBar) {
    }

    override fun getTooltipText(): String? {
        return null
    }

    override fun getClickConsumer(): Consumer<MouseEvent>? {
        return null
    }

    override fun getText(): String {
        return "Hello"
    }

    override fun getAlignment(): Float = 0.0F

    override fun getPresentation() = this
}
