package reporting.models

/**
 * Severity level for screenshot issues.
 * Maps to CSS classes for HTML report styling.
 */
enum class Severity(val cssClass: String) {
    ERROR("error"),     // Red - critical issues requiring immediate fix
    WARNING("warning"), // Yellow - issues that should be addressed
    OK("ok")            // Green - no issues or informational
}

/**
 * Types of issues found during screenshot analysis.
 * Each type has an associated severity and display name.
 */
enum class IssueType(val severity: Severity, val displayName: String) {
    // L10N issues
    TRANSLATION(Severity.ERROR, "Translation"),
    DATE_FORMAT(Severity.WARNING, "Date Format"),
    NUMBER_FORMAT(Severity.WARNING, "Number Format"),
    CURRENCY_FORMAT(Severity.WARNING, "Currency Format"),
    TEXT_TRUNCATION(Severity.ERROR, "Text Truncation"),
    TEXT_OVERFLOW(Severity.ERROR, "Text Overflow"),
    RTL_ISSUE(Severity.ERROR, "RTL Issue"),
    MIXED_LANGUAGES(Severity.WARNING, "Mixed Languages"),

    // UI issues
    UI_QUALITY(Severity.ERROR, "UI Quality"),
    LAYOUT_BROKEN(Severity.ERROR, "Layout Broken"),
    OVERLAPPING(Severity.ERROR, "Overlapping"),
    ALIGNMENT(Severity.WARNING, "Alignment"),
    SPACING(Severity.WARNING, "Spacing"),
    FONT_ISSUE(Severity.ERROR, "Font Issue"),
    MISSING_ELEMENT(Severity.ERROR, "Missing Element"),

    // UX issues
    UX_ISSUE(Severity.WARNING, "UX Issue"),
    READABILITY(Severity.ERROR, "Readability"),
    NAVIGATION(Severity.WARNING, "Navigation"),

    // No issues marker
    NO_ISSUES(Severity.OK, "No Issues")
}

/**
 * Describes a single issue found in a screenshot.
 */
data class IssueDescription(
    val description: String,
    val type: IssueType,
    val suggestion: String,
    val location: String = ""
)

/**
 * Represents an analyzed screenshot with its issues.
 */
data class AnalyzedScreenshot(
    val fileName: String,
    val locale: String,
    val screenshotPath: String? = null,
    val issues: List<IssueDescription>,
    val platform: String = "unknown",
    val screenType: String = "unknown"
)

/**
 * Summary statistics for the generated report.
 */
data class ReportSummary(
    val totalScreenshots: Int,
    val screenshotsWithIssues: Int,
    val screenshotsWithoutIssues: Int,
    val issuesByType: Map<IssueType, Int>,
    val issuesBySeverity: Map<Severity, Int>,
    val generatedAt: String
)
