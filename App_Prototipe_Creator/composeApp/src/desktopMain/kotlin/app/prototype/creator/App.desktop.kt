package app.prototype.creator

import app.prototype.creator.ui.components.initializeSharedWindowManager

/**
 * Desktop implementation of initializeHtmlViewer
 * Pre-initializes the JavaFX WebView window
 */
actual fun initializeHtmlViewer() {
    initializeSharedWindowManager()
}
