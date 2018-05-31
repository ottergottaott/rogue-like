package core

import com.badlogic.gdx.graphics.Color
import squidpony.ArrayTools
import squidpony.squidgrid.gui.gdx.MapUtility
import squidpony.squidgrid.gui.gdx.SColor
import squidpony.squidgrid.gui.gdx.SparseLayers
import squidpony.squidgrid.mapping.DungeonGenerator
import squidpony.squidgrid.mapping.DungeonUtility
import squidpony.squidgrid.mapping.styled.TilesetType
import squidpony.squidmath.Coord
import squidpony.squidmath.GreasedRegion
import squidpony.squidmath.RNG

class Level {
    lateinit var dungeon: Dungeon
    var wholeMapWidth: Int = 0
    var wholeMapHeight: Int = 0
    lateinit var startPosition: Coord
    lateinit var mobs: MutableList<Mob>
    fun renderMap(display: SparseLayers, visible: Array<DoubleArray>, seen: GreasedRegion) {
        for (x in 0 until wholeMapWidth) {
            for (y in 0 until wholeMapHeight) {
                if (visible[x][y] > 0.0) {
                    display.putWithConsistentLight(x, y, dungeon.prunedDungeong[x][y], dungeon.colors[x][y],
                            dungeon.bgColors[x][y], SColor.COSMIC_LATTE.toFloatBits(), visible[x][y])
                } else if (seen.contains(x, y)) {
                    display.put(x, y, dungeon.prunedDungeong[x][y], dungeon.colors[x][y],
                            SColor.lerpFloatColors(dungeon.bgColors[x][y], SColor.CW_ALMOST_BLACK.toFloatBits(), 0f))
                }

            }
        }
    }

    fun isValidCell(x: Int, y: Int): Boolean {
        return x >= 0
                && y >= 0
                && x < wholeMapWidth
                && y < wholeMapHeight
                && dungeon.bareDungeon[x][y] != '#'
    }
}

open class LevelBuilder() {
    constructor(init: LevelBuilder.() -> Unit) : this() {
        init()
    }

    private var dungeonHolder: Dungeon? = null
    private var wholeMapWidthHolder: Int? = null
    private var wholeMapHeightHolder: Int? = null
    private var startPositionHolder: Coord? = null
    private var mobsHolder: MutableList<Mob>? = null

    fun dungeon(init: () -> Dungeon) {
        dungeonHolder = init()
    }

    fun wholeMapWidth(init: () -> Int) {
        wholeMapWidthHolder = init()
    }

    fun wholeMapHeight(init: () -> Int) {
        wholeMapHeightHolder = init()
    }

    fun startPosition(init: () -> Coord) {
        startPositionHolder = init()
    }

    fun mobs(init: () -> MutableList<Mob>) {
        mobsHolder = init()
    }

    fun build(): Level {
        val level = Level()

        dungeonHolder?.apply {
            level.dungeon = Dungeon(
                    bgColor, bareDungeon, prunedDungeong, floors, colors, bgColors, resistance
            )
        }

        wholeMapWidthHolder?.apply {
            level.wholeMapWidth = this
        }

        wholeMapHeightHolder?.apply {
            level.wholeMapHeight = this
        }

        startPositionHolder?.apply {
            level.startPosition = this
        }

        mobsHolder?.apply {
            level.mobs = this
        }

        return level
    }
}

fun level(init: LevelBuilder.() -> Unit): Level {
    return LevelBuilder(init).build()
}

fun firstLevel(config: Configuration, rng: RNG): Level {
    val dungeonGen: DungeonGenerator = DungeonGenerator(config.wholeMapWidth, config.wholeMapHeight, rng)
    val decoDungeon = dungeonGen.generate(TilesetType.MAZE_B)

    val bareDungeon = dungeonGen.bareDungeon
    val floors = GreasedRegion(bareDungeon, '.')
    val bgColor = Color.DARK_GRAY
    val colors = MapUtility.generateDefaultColorsFloat(decoDungeon)
    val bgColors = MapUtility.generateDefaultBGColorsFloat(decoDungeon)
    val resistance = DungeonUtility.generateResistances(decoDungeon)
    val startPosition = floors.singleRandom(rng)


    val levelToReturn = level {
        dungeon {
            Dungeon(bgColor, bareDungeon, ArrayTools.copy(bareDungeon), floors, colors, bgColors, resistance)
        }

        wholeMapHeight {
            config.wholeMapHeight
        }

        wholeMapWidth {
            config.wholeMapWidth
        }

        startPosition {
            startPosition
        }

        mobs {
            mutableListOf()
        }

    }

    levelToReturn.mobs.addAll(listOf(
            SimpleWarrior("id2", startPosition.translate(1, 0), levelToReturn),
            SimpleWarrior("id3", startPosition.translate(1, 1), levelToReturn),
            SimpleWarrior("id4", startPosition.translate(2, 1), levelToReturn)))
    return levelToReturn
}

data class Dungeon(
        val bgColor: Color,
        val bareDungeon: Array<CharArray>,
        val prunedDungeong: Array<CharArray>,
        val floors: GreasedRegion,
        val colors: Array<FloatArray>,
        val bgColors: Array<FloatArray>,
        val resistance: Array<DoubleArray>
)