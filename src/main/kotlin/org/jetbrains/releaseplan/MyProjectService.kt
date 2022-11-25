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
        val popup = JBPopupFactory.getInstance().createListPopup(MyPopup(text, release.render(now)))
        popup.show(RelativePoint(it.component, Point(0, -popup.content.preferredSize.height)))
    }

    @PopupTitle
    override fun getText(): String {
        val now = LocalDate.now()
        return release.nearest(now)
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

class ReleaseStructure(
    val productName: String,
    val shortName: String,
    val releases: List<MajorVersion>,
) {
    fun render(now: LocalDate): List<String> {
        return listOf(productName) + releases.first().render(now)
    }

    fun nearest(now: LocalDate): String {
        val step = releases.map { it.nearest(now) }.minBy { it.date }
        return "$shortName ${step.type} in ${ChronoUnit.DAYS.between(now, step.date)} days"
    }
}

class MajorVersion(
    val featureFreezeDate: LocalDate?,
    val mainBranchName: String?,
    val releases: List<Release>
) {
    fun render(now: LocalDate): List<String> {
        return releases.flatMap { it.render(now) }
    }

    fun nearest(now: LocalDate): Step {
        return releases.map { it.nearest(now) }.minBy { it.date }
    }
}

class Release(
    val version: String,
    val branch: String?,
    val steps: List<Step>,
) {
    fun render(now: LocalDate): List<String> {
        val steps = steps.mapNotNull { it.render(now) }
        if (steps.isEmpty()) {
            return emptyList()
        }
        return listOf(version) + steps
    }

    fun nearest(now: LocalDate): Step {
        return steps.filter { it.date.isAfter(now) }.minBy { it.date }
    }
}

class Step(
    val type: String,
    val date: LocalDate,
) {
    fun render(now: LocalDate): String? {
        val dateDiff = ChronoUnit.DAYS.between(now, date)
        if (dateDiff < 0) {
            return null
        }
        return " - ${type}: $date (in $dateDiff days)"
    }
}

val release = ReleaseStructure(
    "IntelliJ IDEA",
    "IJ",
    listOf(
        MajorVersion(
            LocalDate.of(2022, 10, 18),
            "223",
            listOf(
                Release(
                    "2022.3",
                    "223.7571",
                    listOf(
                        Step("Beta", LocalDate.of(2022, 11, 1)),
                        Step("Release", LocalDate.of(2022, 11, 29)),
                    )
                ),
                Release(
                    "2022.3.1",
                    null,
                    listOf(
                        Step("Preview", LocalDate.of(2022, 12, 6)),
                        Step("RC", LocalDate.of(2022, 12, 13)),
                        Step("Release", LocalDate.of(2022, 12, 20)),
                    )
                ),
                Release(
                    "2022.3.2",
                    null,
                    listOf(
                        Step("Preview", LocalDate.of(2023, 1, 10)),
                        Step("RC", LocalDate.of(2023, 1, 17)),
                        Step("Release", LocalDate.of(2023, 1, 24)),
                    )
                ),
            )
        )
    )
)
