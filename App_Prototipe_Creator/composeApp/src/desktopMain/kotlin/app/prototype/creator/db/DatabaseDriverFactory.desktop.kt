package app.prototype.creator.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val databasePath = File(System.getProperty("user.home"), ".app_prototype_creator")
        databasePath.mkdirs()
        
        val databaseFile = File(databasePath, "app_prototype.db")
        val isNewDatabase = !databaseFile.exists()
        
        val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
        
        // Create tables only if database is new
        if (isNewDatabase) {
            AppDatabase.Schema.create(driver)
        }
        
        return driver
    }
}
