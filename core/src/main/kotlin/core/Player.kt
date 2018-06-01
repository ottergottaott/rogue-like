package core

import squidpony.squidgrid.FOV
import squidpony.squidgrid.Radius
import squidpony.squidmath.Coord

/**
 * Class for player
 */
class Player(var coord: Coord,
             private var health: Int = 10,
             private val level: Level,
             eventRouter: EventRouter) : Listener {
    val visible: Array<DoubleArray> = Array(level.wholeMapHeight, { it -> DoubleArray(level.wholeMapWidth) })
    private val mobsNearest: MutableMap<String, Coord> = mutableMapOf()
    private val eventRouter: EventRouter = eventRouter

    init {
        FOV.reuseFOV(level.dungeon.resistance, visible, coord.x, coord.y, 5.0, Radius.CIRCLE)
    }

    private val inventory: Inventory = Inventory(5)

    override fun onMoveKeyPressed(moveType: MoveType, alt: Boolean) {
        val newCoord = when (moveType) {
            MoveType.UP -> {
                coord.translate(0, -1)
            }
            MoveType.DOWN -> {
                coord.translate(0, 1)
            }

            MoveType.LEFT -> {
                coord.translate(-1, 0)
            }
            MoveType.RIGHT -> {
                coord.translate(1, 0)
            }
        }
        if (newCoord in mobsNearest.values) {
            eventRouter.playerAttack(coord, newCoord)
        } else {
            FOV.reuseFOV(level.dungeon.resistance, visible, newCoord.x, newCoord.y, 5.0, Radius.CIRCLE)
            eventRouter.playerMove(coord, newCoord, visible)
        }
    }

    override fun onPlayerMovedListener(oldCoord: Coord, newCoord: Coord) {
        coord = newCoord
    }

    override fun onCollisionListener(oldCoord: Coord, newCoord: Coord) {
        coord = oldCoord
    }

    override fun onMobMovedListener(id: String, oldCoord: Coord, newCoord: Coord) {
        if (newCoord.distance(coord) < 10) {
            mobsNearest[id] = newCoord
        } else {
            mobsNearest.remove(id)
        }
    }

    override fun onMobAttackListener(id: String) {
        health -= 1
        println("Player health $health")
    }

    override fun onMobDead(id: String) {
        mobsNearest.remove(id)
    }

    override fun onItemInteraction(item: Item) {
        if (inventory.addItem(item)) {
            eventRouter.raiseItem(item)
            eventRouter.drawInventory(inventory)
        }
    }

    override fun onNumKeyPressed(numKey: Int, alt: Boolean) {
        if (alt) {
            val removedItem = inventory.removeItem(numKey - 1)
            if (removedItem != null) {
                val itemToDrop = removedItem.move(coord.translate(1, 0))
                eventRouter.subscribe(itemToDrop)
                eventRouter.dropItem(itemToDrop)
            }
        } else {
            inventory.selectItem(numKey - 1)
        }

        eventRouter.drawInventory(inventory)
    }

}