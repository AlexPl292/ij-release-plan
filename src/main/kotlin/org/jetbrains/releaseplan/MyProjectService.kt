package org.jetbrains.releaseplan

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.intellij.openapi.util.NlsContexts.PopupTitle
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.Consumer
import java.awt.Point
import java.awt.event.MouseEvent
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.swing.Icon

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
        val now = LocalDate.now()
        val structure = info.take(10).filter { it.date.isAfter(now) }.sortedBy { it.date }.groupBy { it.project }.mapValues { it.value.groupBy { it.version } }
        val items = buildList {
            structure.forEach { project, projectItems ->
                this.add(project)
                projectItems.forEach { version, items ->
                    this.add(version)
                    items.forEach { item ->
                        this.add(" - ${item.releaseType}: ${item.date} (in ${ChronoUnit.DAYS.between(now, item.date)} days)")
                    }
                }
            }
        }
        val popup = JBPopupFactory.getInstance().createListPopup(MyPopup(text, items))
        popup.show(RelativePoint(it.component, Point(0, -popup.content.preferredSize.height)))
    }

    @PopupTitle
    override fun getText(): String {
        val now = LocalDate.now()
        return info.filter { it.date.isAfter(now) }.minBy { it.date }.let { "${it.shortName} ${it.releaseType} in ${ChronoUnit.DAYS.between(now, it.date)} days" }
    }

    override fun getAlignment(): Float = 0.0F

    override fun getPresentation() = this
}

private class MyPopup(header: String, items: List<String>) : BaseListPopupStep<String>(header, items) {
    override fun isSelectable(value: String?) = false

    override fun getIconFor(value: String?): Icon? {
        return if (value == "IntelliJ IDEA") {
            AllIcons.Debugger.Db_muted_breakpoint
        } else {
            null
        }
    }
}

data class ReleaseItem(
        val project: String,
        val shortName: String,
        val releaseType: String,
        val date: LocalDate,
        val version: String,
)

val info = listOf(
        ReleaseItem("IntelliJ IDEA", "IJ", "Beta", LocalDate.of(2022, 11, 1), "2022.3"),
        ReleaseItem("IntelliJ IDEA", "IJ", "Release", LocalDate.of(2022, 11, 29), "2022.3"),
        ReleaseItem("IntelliJ IDEA", "IJ", "Preview", LocalDate.of(2022, 12, 6), "2022.3.1"),
        ReleaseItem("IntelliJ IDEA", "IJ", "RC", LocalDate.of(2022, 12, 13), "2022.3.1"),
        ReleaseItem("IntelliJ IDEA", "IJ", "Release", LocalDate.of(2022, 12, 20), "2022.3.1"),
        ReleaseItem("IntelliJ IDEA", "IJ", "Preview", LocalDate.of(2023, 1, 10), "2022.3.2"),
        ReleaseItem("IntelliJ IDEA", "IJ", "RC", LocalDate.of(2023, 1, 17), "2022.3.2"),
        ReleaseItem("IntelliJ IDEA", "IJ", "Release", LocalDate.of(2023, 1, 24), "2022.3.2"),
)
