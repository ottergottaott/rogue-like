package core

import squidpony.squidmath.Coord
import java.io.PrintStream
import java.io.PrintWriter

class Logger(out: PrintStream) : Listener {
    private val log = PrintWriter(out)
    override fun onPlayerMovedListener(oldCoord: Coord, newCoord: Coord) {
        log.write("Player moved from: $oldCoord to $newCoord \n")
        log.flush()
    }

    override fun onPlayerAttackListener(playerCoord: Coord, mobCoord: Coord) {
        log.write("Player at coord $playerCoord was attacked by mob at coord $mobCoord \n")
        log.flush()
    }

    override fun onCollisionListener(oldCoord: Coord, newCoord: Coord) {
        log.write("Player collided with object at $newCoord \n")
        log.flush()
    }

    override fun onMoveKeyPressed(moveType: MoveType, alt: Boolean) {
        log.write("Move key pressed: $moveType \n")
        log.flush()
    }

    override fun onMobMovedListener(id: String, oldCoord: Coord, newCoord: Coord) {
        log.write("Mob $id moved to $newCoord \n")
        log.flush()
    }

    override fun onMobAttackListener(id: String) {
        log.write("Mob $id try to attack player \n")
        log.flush()
    }

    override fun onMobDead(id: String) {
        log.write("Mob $id died \n")
        log.flush()
    }

    override fun onDroppedItem(item: Item) {
        log.write("$item was dropped \n")
        log.flush()
    }


    override fun onRaisedItem(item: Item) {
        log.write("$item was raised \n")
        log.flush()
    }

    override fun onItemInteraction(item: Item) {
        log.write("Player collided with $item \n")
        log.flush()
    }

    override fun onPlayerHealthChanged(hp: Int) {
        log.write("Player helath fell to $hp \n")
        log.flush()
    }

    override fun onNumKeyPressed(numKey: Int, alt: Boolean) {
        log.write("Num key pressed: $numKey \n")
        log.flush()
    }
}