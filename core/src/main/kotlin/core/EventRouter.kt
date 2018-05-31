package core

import squidpony.squidmath.Coord
import javax.swing.text.StyledEditorKit

interface Listener {
    fun onPlayerMoveListener(oldCoord: Coord, newCoord: Coord, visible: Array<DoubleArray>) {}

    fun onPlayerMovedListener(oldCoord: Coord, newCoord: Coord) {}
    fun onPlayerAttackListener(playerCoord: Coord, mobCoord: Coord) {}

    fun onCollisionListener(oldCoord: Coord, newCoord: Coord) {}
    fun onMoveKeyPressed(moveType: MoveType, alt: Boolean) {}


    fun onMobMoveListener(id: String, oldCoord: Coord, newCoord: Coord) {}
    fun onMobMovedListener(id: String, oldCoord: Coord, newCoord: Coord) {}
    fun onMobAttackListener(id: String) {}
    fun onMobDead(id: String) {}

    fun onDroppedItem(item: Item) {}
    fun onRaisedItem(item: Item) {}
    fun onItemInteraction(item: Item) {}

    fun onDrawInventory(inv: Inventory) {}

    fun onPlayerHealthChanged(hp: Int) {}

    fun onNumKeyPressed(numKey: Int, alt: Boolean) {}
}

enum class MoveType {
    DOWN, UP, LEFT, RIGHT
}

object EventRouter {
    private var listeners: List<Listener> = listOf()

    fun numKeyPress(numKey: Int, alt: Boolean) {
        listeners.forEach { it -> it.onNumKeyPressed(numKey, alt) }
    }

    fun subscribe(listener: Listener) {
        listeners += listener
    }

    fun unsubscribe(listener: Listener) {
        listeners -= listener
    }

    fun playerMove(oldCoord: Coord, newCoord: Coord, visible: Array<DoubleArray>) {
        listeners.forEach { it -> it.onPlayerMoveListener(oldCoord, newCoord, visible) }
    }

    fun playerMoved(oldCoord: Coord, newCoord: Coord) {
        listeners.forEach { it -> it.onPlayerMovedListener(oldCoord, newCoord) }
    }

    fun mobDead(id: String) {
        listeners.forEach { it -> it.onMobDead(id) }
    }

    fun mobMove(id: String, oldCoord: Coord, newCoord: Coord) {
        listeners.forEach { it -> it.onMobMoveListener(id, oldCoord, newCoord) }
    }

    fun mobMoved(id: String, oldCoord: Coord, newCoord: Coord) {
        listeners.forEach { it -> it.onMobMovedListener(id, oldCoord, newCoord) }
    }

    fun mobAttack(id: String) {
        listeners.forEach { it -> it.onMobAttackListener(id) }
    }

    fun playerAttack(playerCoord: Coord, mobCoord: Coord) {
        listeners.forEach { it -> it.onPlayerAttackListener(playerCoord, mobCoord) }
    }

    fun moveKeyPress(moveType: MoveType, alt: Boolean) {
        listeners.forEach { it -> it.onMoveKeyPressed(moveType, alt) }
    }

    fun collision(oldCoord: Coord, newCoord: Coord) {
        listeners.forEach { it -> it.onCollisionListener(oldCoord, newCoord) }
    }

    fun interactWithItem(id: Item) {
        listeners.forEach { it -> it.onItemInteraction(id) }
    }

    fun raiseItem(item: Item) {
        listeners.forEach { it -> it.onRaisedItem(item) }
    }

    fun dropItem(item: Item) {
        listeners.forEach { it -> it.onDroppedItem(item) }
    }

    fun drawInventory(inventory: Inventory) {
        listeners.forEach { it -> it.onDrawInventory(inventory) }
    }

}