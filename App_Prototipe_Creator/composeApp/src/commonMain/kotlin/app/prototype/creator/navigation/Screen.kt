package app.prototype.creator.navigation

sealed class Screen(val route: String) {
    object Chat : Screen("chat")
    object Gallery : Screen("gallery")
    object PrototypeDetail : Screen("prototype/{prototypeId}") {
        fun createRoute(prototypeId: String) = "prototype/$prototypeId"
    }
    
    companion object {
        const val PROTOTYPE_ID_ARG = "prototypeId"
    }
}
