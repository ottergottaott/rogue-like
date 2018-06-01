package core

import squidpony.squidai.DijkstraMap
import squidpony.squidgrid.FOV
import squidpony.squidgrid.Radius
import squidpony.squidmath.Coord
import squidpony.squidmath.GreasedRegion

sealed class Mob(val id: String, var coord: Coord, var health: Int, val level: Level, val eventRouter: EventRouter) : Listener {
    val visible: Array<DoubleArray> = Array(level.wholeMapHeight, { it -> DoubleArray(level.wholeMapWidth) })
    val blockage: GreasedRegion
    val playerToCursor = DijkstraMap(level.dungeon.bareDungeon, DijkstraMap.Measurement.MANHATTAN)
    init {
        FOV.reuseFOV(level.dungeon.resistance, visible, coord.x, coord.y, 10.0, Radius.CIRCLE)
        blockage = GreasedRegion(visible, 0.0)
    }
}

class SimpleWarrior(id: String, coord: Coord, level: Level, eventRouter: EventRouter)
    : Mob(id, coord, health = 5, level = level, eventRouter = eventRouter) {
    override fun onPlayerAttackListener(playerCoord: Coord, mobCoord: Coord) {
        if (coord == mobCoord) {
            health -= 1

            if (health <= 0) {
                eventRouter.mobDead(id)
            }
        }

        attackPlayer(playerCoord, playerCoord)
    }

    override fun onMobMovedListener(id: String, oldCoord: Coord, newCoord: Coord) {
        if (id == this.id) {
            coord = newCoord
        }
    }

    override fun onPlayerMovedListener(oldCoord: Coord, newCoord: Coord) {
        attackPlayer(oldCoord, newCoord)
    }

    private fun attackPlayer(oldCoord: Coord, newCoord: Coord) {
        if (newCoord.distance(coord) <= 1) {
            eventRouter.mobAttack(id)
            return
        }

        FOV.reuseFOV(level.dungeon.resistance, visible, coord.x, coord.y, 5.0, Radius.CUBE)
        blockage.refill(visible, 0.0)

        if (blockage.not().contains(newCoord)) {
            blockage.fringe8way()
            playerToCursor.clearGoals()
            playerToCursor.setGoal(newCoord)

            playerToCursor.partialScan(20, blockage)
            val findAttackPath = playerToCursor.findAttackPath(1, 1, null, null, null, coord, newCoord)
            if  (findAttackPath.isNotEmpty()) {
                eventRouter.mobMove(id, coord, findAttackPath[0])
            }

            return
        }

        eventRouter.mobMove(id, coord, coord)

    }

    override fun onMobDead(id: String) {
        if (this.id == id) {
            eventRouter.unsubscribe(this)
        }
    }
}