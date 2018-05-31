package core
data class Configuration(
    val gridWidth: Int = 30,
    val gridHeight: Int = 30,

    val bonusHeight: Int = 10,

    val cellWidth: Int = 20,
    val cellHeight: Int= 20,

    val wholeMapWidth: Int = gridWidth * 2,
    val wholeMapHeight: Int = gridHeight * 2,

    val appWidth: Int = gridWidth * cellWidth,
    val appHeight: Int = (gridHeight + bonusHeight) * cellHeight
)

fun defaultConfiguration(): Configuration = Configuration()

// for now returns default
fun loadConfiguration(): Configuration = defaultConfiguration()
