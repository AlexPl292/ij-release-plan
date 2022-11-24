package com.github.alexpl292.ijreleaseplan.services

import com.github.alexpl292.ijreleaseplan.MyBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.Consumer
import java.awt.Point
import java.awt.event.MouseEvent
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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

    override fun getClickConsumer() = Consumer<MouseEvent> {
        val sortedDates = dates.sortedBy { it.first }
        val now = LocalDate.now()
        val items = sortedDates.take(5).map { "${it.second}: ${it.first} (in ${ChronoUnit.DAYS.between(now, it.first)} days)" }
        val popup = JBPopupFactory.getInstance().createListPopup(BaseListPopupStep("Nearest: ${items.first()}", items.drop(1)))
        popup.show(RelativePoint(it.component, Point(0, -popup.content.preferredSize.height)))
    }

    override fun getText(): String {
        val now = LocalDate.now()
        return dates.sortedBy { it.first }.first().let { "in ${ChronoUnit.DAYS.between(now, it.first)} days" }
    }

    override fun getAlignment(): Float = 0.0F

    override fun getPresentation() = this
}

val dates = listOf(
        LocalDate.of(2022, 11, 29) to "2022.3 Release",
        LocalDate.of(2022, 12, 6) to "2022.3.1 Preview",
        LocalDate.of(2022, 12, 13) to "2022.3.1 RC",
        LocalDate.of(2022, 12, 20) to "2022.3.1 Release",
        LocalDate.of(2023, 1, 10) to "2022.3.2 Preview",
        LocalDate.of(2023, 1, 17) to "2022.3.2 RC",
        LocalDate.of(2023, 1, 24) to "2022.3.2 Release",
)
