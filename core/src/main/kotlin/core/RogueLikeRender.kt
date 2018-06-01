package core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import squidpony.squidgrid.Direction
import squidpony.squidgrid.gui.gdx.*
import squidpony.squidgrid.mapping.LineKit
import squidpony.squidmath.Coord
import squidpony.squidmath.GreasedRegion
import java.util.*

data class MobMoveAction(val id: String, val newCoord: Coord, val oldCoord: Coord)

class RogueLikeRender(val level: Level, var visible: Array<DoubleArray>, var config: Configuration, mainRouter: EventRouter) : Listener {
    private val batch: SpriteBatch = SpriteBatch()
    private var stage: Stage
    private var display: SparseLayers
    private val seen: GreasedRegion
    private val blockage: GreasedRegion
    private var languageDisplay: SparseLayers


    private var pg: TextCellFactory.Glyph
    private var mobsGlyphs: MutableMap<String, TextCellFactory.Glyph>
    private var itemsGlyps: MutableMap<String, TextCellFactory.Glyph>


    private val input: SquidInput

    private val awaitedMoves: ArrayList<Pair<Coord, Coord>>
    private val awaitedMobMoves: ArrayList<MobMoveAction> = arrayListOf()

    private val eventRouter: EventRouter = mainRouter

    private var languageStage: Stage

    init {
        val mainViewport = StretchViewport((config.gridWidth * config.cellWidth).toFloat(),
                (config.gridHeight * config.cellHeight).toFloat())
        val languageViewport = StretchViewport((config.gridWidth * config.cellWidth).toFloat(),
                ((config.bonusHeight) * config.cellHeight).toFloat())
        mainViewport.setScreenBounds(0, 0,
                config.gridWidth * config.cellWidth,
                config.gridHeight * config.cellHeight)
        languageViewport.setScreenBounds(0, 0,
                config.gridWidth * config.cellWidth,
                config.bonusHeight * config.cellHeight)

        stage = Stage(mainViewport, batch)
        languageStage = Stage(languageViewport, batch)
        display = SparseLayers(config.wholeMapWidth,
                config.wholeMapHeight + config.bonusHeight,
                config.cellWidth.toFloat(),
                config.cellHeight.toFloat(),
                DefaultResources.getStretchableSlabFont())

        display.font.tweakWidth(config.cellWidth * 1.1f)
                .tweakHeight(config.cellHeight * 1.1f)
                .initBySize()


        languageDisplay = SparseLayers(config.gridWidth, config.bonusHeight - 1,
                config.cellWidth.toFloat(), config.cellHeight.toFloat(), display.font)

        languageDisplay.defaultPackedBackground = SColor.COSMIC_LATTE.toFloatBits()

        blockage = GreasedRegion(visible, 0.0)
        seen = blockage.not().copy()
        blockage.fringe8way()

        LineKit.pruneLines(level.dungeon.bareDungeon, seen, LineKit.lightAlt, level.dungeon.prunedDungeong)



        mobsGlyphs = level.mobs.map {
            it.id to display.glyph('W', SColor.SALMON.toFloatBits(), it.coord.x, it.coord.y)
        }.toMap().toMutableMap()

        itemsGlyps = hashMapOf(
                "Lal1" to display.glyph('S', SColor.SAFETY_ORANGE.toFloatBits(), level.startPosition.x + 1, level.startPosition.y + 3),
                "Lal2" to display.glyph('S', SColor.SAFETY_ORANGE.toFloatBits(), level.startPosition.x + 2, level.startPosition.y + 3),
                "Lal3" to display.glyph('S', SColor.SAFETY_ORANGE.toFloatBits(), level.startPosition.x + 3, level.startPosition.y - 2)
        )

        eventRouter.subscribe(
                SimpleShield("Lal1", Coord.get(level.startPosition.x + 1, level.startPosition.y + 3), eventRouter)
        )
        eventRouter.subscribe(
                SimpleShield("Lal2", Coord.get(level.startPosition.x + 2, level.startPosition.y + 3), eventRouter)
        )
        eventRouter.subscribe(
                SimpleShield("Lal3", Coord.get(level.startPosition.x + 3, level.startPosition.y - 2), eventRouter)
        )


        display.setPosition(0f, 0f)
        pg = display.glyph('@', SColor.SAFETY_ORANGE.toFloatBits(), level.startPosition.x, level.startPosition.y)
        pg.width = config.cellWidth * 1.4f
        pg.height = config.cellHeight * 1.4f

        awaitedMoves = ArrayList(200)

        input = SquidInput(
                SquidInput.KeyHandler { key, alt, ctrl, shift ->
                    when (key) {
                        SquidInput.UP_ARROW -> {
                            eventRouter.moveKeyPress(MoveType.UP, alt)
                        }
                        SquidInput.DOWN_ARROW -> {
                            eventRouter.moveKeyPress(MoveType.DOWN, alt)
                        }
                        SquidInput.LEFT_ARROW -> {
                            eventRouter.moveKeyPress(MoveType.LEFT, alt)
                        }
                        SquidInput.RIGHT_ARROW -> {
                            eventRouter.moveKeyPress(MoveType.RIGHT, alt)
                        }

                        else -> {
                            val numKey = key.toInt() - 48
                            if (numKey in 1..6) {
                                eventRouter.numKeyPress(numKey, alt)
                            }

                        }
                    }

                }
        )
        input.repeatGap = 100
        Gdx.input.inputProcessor = InputMultiplexer(stage, input)
        stage.addActor(display)

        languageStage.addActor(languageDisplay)
    }

    fun render() {
        val bgColor = level.dungeon.bgColor
        Gdx.gl.glClearColor(bgColor.r / 255.0f, bgColor.g / 255.0f, bgColor.b / 255.0f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.camera.position.x = pg.x
        stage.camera.position.y = pg.y

        level.renderMap(display, visible, seen)
        languageDisplay.fillBackground(languageDisplay.defaultBackgroundColor);

        if (awaitedMobMoves.isNotEmpty()) {
            val (id, newCoord, oldCoord) = awaitedMobMoves.removeAt(0)
            mobMove(id, oldCoord, newCoord)
        }

        if (awaitedMoves.isNotEmpty()) {
            val (oldCoord, newCoord) = awaitedMoves.removeAt(0)
            playerMove(oldCoord, newCoord)

            stage.camera.position.x = pg.x
            stage.camera.position.y = pg.y
        } else if (input.hasNext()) {
            input.next()
        }

        languageStage.viewport.apply()
        languageStage.draw()
        stage.act()
        stage.viewport.apply()
        stage.draw()
    }

    fun resize(width: Int, height: Int) {
        val currentZoomY = height.toFloat() / (config.gridHeight + config.bonusHeight)
        languageDisplay.setBounds(0f, 0f, width.toFloat(), currentZoomY * config.bonusHeight)
        languageStage.viewport.update(width, height, false)
        languageStage.viewport.setScreenBounds(0, 0, width, languageDisplay.height.toInt())
        stage.viewport.update(width, height, false)
        stage.viewport.setScreenBounds(0, languageDisplay.height.toInt(),
                width, height - languageDisplay.height.toInt())
    }

    override fun onPlayerMoveListener(oldCoord: Coord, newCoord: Coord, visible: Array<DoubleArray>) {
        awaitedMoves.add(Pair(oldCoord, newCoord))
        this.visible = visible
    }

    override fun onMobMoveListener(id: String, oldCoord: Coord, newCoord: Coord) {
        super.onMobMoveListener(id, oldCoord, newCoord)
        awaitedMobMoves.add(MobMoveAction(id, newCoord, oldCoord))
    }

    override fun onPlayerAttackListener(playerCoord: Coord, mobCoord: Coord) {
        display.bump(pg, Direction.getRoughDirection(mobCoord.x - playerCoord.x, mobCoord.y - playerCoord.y), 0.05f)
    }

    override fun onMobDead(id: String) {
        display.removeGlyph(mobsGlyphs[id])
    }

    private fun playerMove(oldCoord: Coord, newCoord: Coord) {
        val newX = newCoord.x
        val newY = newCoord.y
        if (level.isValidCell(newX, newY)) {
            pg.setPosition(display.worldX(newX), display.worldY(newY))

            blockage.refill(visible, 0.0)
            seen.or(blockage.not())
            blockage.fringe8way()
            LineKit.pruneLines(level.dungeon.bareDungeon, seen, LineKit.lightAlt, level.dungeon.prunedDungeong)
            eventRouter.playerMoved(oldCoord, newCoord)
        } else {
            eventRouter.collision(oldCoord, newCoord)
            display.bump(pg, Direction.getRoughDirection(newX - oldCoord.x, newY - oldCoord.y), 0.05f)
            display.addAction(PanelEffect.PulseEffect(display, 0.05f, level.dungeon.floors,
                    oldCoord, 1, floatArrayOf(SColor.GOLD.toFloatBits())
            ))
        }
    }

    private fun mobMove(id: String, oldCoord: Coord, newCoord: Coord) {
        val newX = newCoord.x
        val newY = newCoord.y
        if (level.isValidCell(newX, newY)) {
            mobsGlyphs[id]?.setPosition(display.worldX(newX), display.worldY(newY))
            eventRouter.mobMoved(id, oldCoord, newCoord)
        } else {

        }
    }

    override fun onDrawInventory(inv: Inventory) {
        languageDisplay.clear(0)

        inv.items.forEachIndexed { idx, it ->

            val color = if (inv.getSelectedItem() == it) {
                SColor.ALIZARIN
            } else {
                SColor.DB_LEAD
            }
            languageDisplay.put(1, idx, "${idx + 1}. ${it}", color)
        }
    }

    override fun onRaisedItem(item: Item) {
        display.removeGlyph(itemsGlyps[item.id])
    }

    override fun onDroppedItem(item: Item) {
        renderItem(item)
    }

    private fun renderItem(item: Item) {
        val color = when (item.type) {
            ItemType.DEFENSE -> {
                SColor.CW_GREEN
            }
            ItemType.ATTACK -> {
                SColor.ALICE_BLUE
            }
        }
        itemsGlyps[item.id] = display.glyph('S', color.toFloatBits(), item.coord.x, item.coord.y)
    }
}