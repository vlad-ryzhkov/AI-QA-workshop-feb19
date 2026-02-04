package reporting

import reporting.models.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Generates HTML reports for screenshot analysis results.
 * Uses embedded CSS for single-file output with no external dependencies.
 */
class ScreenshotReportGenerator {

    /**
     * Generates HTML report and writes it to the specified path.
     * @param screenshots List of analyzed screenshots
     * @param outputPath Path where the HTML report will be saved
     * @return ReportSummary with statistics
     */
    fun generate(screenshots: List<AnalyzedScreenshot>, outputPath: String): ReportSummary {
        val html = generateHtml(screenshots)
        File(outputPath).writeText(html)
        return calculateSummary(screenshots)
    }

    /**
     * Generates HTML content as a string.
     * @param screenshots List of analyzed screenshots
     * @return Complete HTML document as string
     */
    fun generateHtml(screenshots: List<AnalyzedScreenshot>): String {
        val summary = calculateSummary(screenshots)

        return buildString {
            append("<!DOCTYPE html>\n")
            append("<html lang=\"en\">\n")
            append("<head>\n")
            append("  <meta charset=\"UTF-8\">\n")
            append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
            append("  <title>Screenshot Analysis Report</title>\n")
            append(generateCss())
            append("</head>\n")
            append("<body>\n")
            append("  <div class=\"container\">\n")
            append(generateHeader(summary))
            append(generateSummaryCards(summary))
            append(generateIssuesByType(summary))
            append(generateScreenshotDetails(screenshots))
            append(generateFooter(summary))
            append("  </div>\n")
            append("</body>\n")
            append("</html>\n")
        }
    }

    /**
     * Calculates summary statistics from analyzed screenshots.
     * @param screenshots List of analyzed screenshots
     * @return ReportSummary with aggregated statistics
     */
    fun calculateSummary(screenshots: List<AnalyzedScreenshot>): ReportSummary {
        val screenshotsWithIssues = screenshots.count { it.issues.any { issue -> issue.type != IssueType.NO_ISSUES } }
        val screenshotsWithoutIssues = screenshots.size - screenshotsWithIssues

        val issuesByType = screenshots
            .flatMap { it.issues }
            .filter { it.type != IssueType.NO_ISSUES }
            .groupingBy { it.type }
            .eachCount()

        val issuesBySeverity = screenshots
            .flatMap { it.issues }
            .filter { it.type != IssueType.NO_ISSUES }
            .groupingBy { it.type.severity }
            .eachCount()

        return ReportSummary(
            totalScreenshots = screenshots.size,
            screenshotsWithIssues = screenshotsWithIssues,
            screenshotsWithoutIssues = screenshotsWithoutIssues,
            issuesByType = issuesByType,
            issuesBySeverity = issuesBySeverity,
            generatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        )
    }

    private fun generateCss(): String = """
  <style>
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }
    body {
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
      background: #f5f5f5;
      color: #333;
      line-height: 1.6;
    }
    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 20px;
    }
    header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 30px;
      border-radius: 12px;
      margin-bottom: 24px;
    }
    header h1 {
      font-size: 28px;
      margin-bottom: 8px;
    }
    header p {
      opacity: 0.9;
      font-size: 14px;
    }
    .summary-cards {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;
      margin-bottom: 24px;
    }
    .card {
      background: white;
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    }
    .card h3 {
      font-size: 14px;
      color: #666;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      margin-bottom: 8px;
    }
    .card .value {
      font-size: 36px;
      font-weight: 700;
    }
    .card.error .value { color: #dc3545; }
    .card.warning .value { color: #ffc107; }
    .card.ok .value { color: #28a745; }
    .card.neutral .value { color: #667eea; }
    .section {
      background: white;
      border-radius: 12px;
      padding: 24px;
      margin-bottom: 24px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    }
    .section h2 {
      font-size: 20px;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 2px solid #f0f0f0;
    }
    .issues-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
      gap: 12px;
    }
    .issue-type {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      border-radius: 8px;
      background: #f8f9fa;
    }
    .issue-type .name { font-weight: 500; }
    .issue-type .count {
      font-weight: 700;
      padding: 4px 12px;
      border-radius: 20px;
    }
    .issue-type.error .count { background: #dc3545; color: white; }
    .issue-type.warning .count { background: #ffc107; color: #333; }
    .screenshot-item {
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      margin-bottom: 16px;
      overflow: hidden;
    }
    .screenshot-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      background: #f8f9fa;
      border-bottom: 1px solid #e0e0e0;
    }
    .screenshot-header h3 {
      font-size: 16px;
    }
    .screenshot-meta {
      display: flex;
      gap: 16px;
      font-size: 13px;
      color: #666;
    }
    .badge {
      display: inline-block;
      padding: 4px 10px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 600;
    }
    .badge.pass { background: #d4edda; color: #155724; }
    .badge.fail { background: #f8d7da; color: #721c24; }
    .badge.review { background: #fff3cd; color: #856404; }
    .issues-table {
      width: 100%;
      border-collapse: collapse;
    }
    .issues-table th,
    .issues-table td {
      padding: 12px 16px;
      text-align: left;
      border-bottom: 1px solid #e0e0e0;
    }
    .issues-table th {
      background: #f8f9fa;
      font-weight: 600;
      font-size: 13px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      color: #666;
    }
    .issues-table tr:last-child td { border-bottom: none; }
    .severity-badge {
      display: inline-block;
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 11px;
      font-weight: 600;
      text-transform: uppercase;
    }
    .severity-badge.error { background: #dc3545; color: white; }
    .severity-badge.warning { background: #ffc107; color: #333; }
    .severity-badge.ok { background: #28a745; color: white; }
    .type-badge {
      display: inline-block;
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 12px;
      background: #e9ecef;
      color: #495057;
    }
    .no-issues {
      text-align: center;
      padding: 24px;
      color: #28a745;
      font-weight: 500;
    }
    .no-issues::before {
      content: "✓ ";
    }
    footer {
      text-align: center;
      padding: 20px;
      color: #666;
      font-size: 13px;
    }
    .thumbnail {
      max-width: 200px;
      max-height: 150px;
      border-radius: 4px;
      margin: 8px;
    }
  </style>
"""

    private fun generateHeader(summary: ReportSummary): String = """
    <header>
      <h1>Screenshot Analysis Report</h1>
      <p>Generated: ${escapeHtml(summary.generatedAt)}</p>
    </header>
"""

    private fun generateSummaryCards(summary: ReportSummary): String {
        val totalIssues = summary.issuesByType.values.sum()
        val errorCount = summary.issuesBySeverity[Severity.ERROR] ?: 0
        val warningCount = summary.issuesBySeverity[Severity.WARNING] ?: 0

        return """
    <div class="summary-cards">
      <div class="card neutral">
        <h3>Total Screenshots</h3>
        <div class="value">${summary.totalScreenshots}</div>
      </div>
      <div class="card ${if (summary.screenshotsWithIssues > 0) "error" else "ok"}">
        <h3>With Issues</h3>
        <div class="value">${summary.screenshotsWithIssues}</div>
      </div>
      <div class="card ok">
        <h3>Without Issues</h3>
        <div class="value">${summary.screenshotsWithoutIssues}</div>
      </div>
      <div class="card ${if (errorCount > 0) "error" else if (warningCount > 0) "warning" else "ok"}">
        <h3>Total Issues</h3>
        <div class="value">$totalIssues</div>
      </div>
    </div>
"""
    }

    private fun generateIssuesByType(summary: ReportSummary): String {
        if (summary.issuesByType.isEmpty()) {
            return """
    <div class="section">
      <h2>Issues by Type</h2>
      <div class="no-issues">No issues found</div>
    </div>
"""
        }

        val issueItems = summary.issuesByType
            .entries
            .sortedByDescending { it.value }
            .joinToString("\n") { (type, count) ->
                val severityClass = type.severity.cssClass
                """      <div class="issue-type $severityClass">
        <span class="name">${escapeHtml(type.displayName)}</span>
        <span class="count">$count</span>
      </div>"""
            }

        return """
    <div class="section">
      <h2>Issues by Type</h2>
      <div class="issues-grid">
$issueItems
      </div>
    </div>
"""
    }

    private fun generateScreenshotDetails(screenshots: List<AnalyzedScreenshot>): String {
        val screenshotItems = screenshots.joinToString("\n") { screenshot ->
            val hasIssues = screenshot.issues.any { it.type != IssueType.NO_ISSUES }
            val issueCount = screenshot.issues.count { it.type != IssueType.NO_ISSUES }
            val badgeClass = if (hasIssues) "fail" else "pass"
            val badgeText = if (hasIssues) "FAIL ($issueCount)" else "PASS"

            val issuesHtml = if (hasIssues) {
                val rows = screenshot.issues
                    .filter { it.type != IssueType.NO_ISSUES }
                    .mapIndexed { index, issue ->
                        """          <tr>
            <td>${index + 1}</td>
            <td><span class="type-badge">${escapeHtml(issue.type.displayName)}</span></td>
            <td><span class="severity-badge ${issue.type.severity.cssClass}">${issue.type.severity.name}</span></td>
            <td>${escapeHtml(issue.description)}</td>
            <td>${escapeHtml(issue.location)}</td>
            <td>${escapeHtml(issue.suggestion)}</td>
          </tr>"""
                    }
                    .joinToString("\n")

                """        <table class="issues-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Type</th>
              <th>Severity</th>
              <th>Issue</th>
              <th>Location</th>
              <th>Suggestion</th>
            </tr>
          </thead>
          <tbody>
$rows
          </tbody>
        </table>"""
            } else {
                """        <div class="no-issues">No issues found</div>"""
            }

            """      <div class="screenshot-item">
        <div class="screenshot-header">
          <h3>${escapeHtml(screenshot.fileName)}</h3>
          <div class="screenshot-meta">
            <span>Locale: <strong>${escapeHtml(screenshot.locale)}</strong></span>
            <span>Platform: <strong>${escapeHtml(screenshot.platform)}</strong></span>
            <span>Screen: <strong>${escapeHtml(screenshot.screenType)}</strong></span>
            <span class="badge $badgeClass">$badgeText</span>
          </div>
        </div>
$issuesHtml
      </div>"""
        }

        return """
    <div class="section">
      <h2>Screenshot Details</h2>
$screenshotItems
    </div>
"""
    }

    private fun generateFooter(summary: ReportSummary): String = """
    <footer>
      <p>Screenshot Analysis Report | Generated by ScreenshotReportGenerator</p>
      <p>${summary.totalScreenshots} screenshots analyzed | ${summary.generatedAt}</p>
    </footer>
"""

    private fun escapeHtml(text: String): String = text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")
}
