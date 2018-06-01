package core

import squidpony.squidmath.Coord

/**
 * Interface representing listener of events,
 * every entity should implement this interface
 */
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


/**
 * Interface representing abstract router
 */
interface EventRouter {
    fun numKeyPress(numKey: Int, alt: Boolean)

    fun subscribe(listener: Listener)

    fun unsubscribe(listener: Listener)

    fun playerMove(oldCoord: Coord, newCoord: Coord, visible: Array<DoubleArray>)

    fun playerMoved(oldCoord: Coord, newCoord: Coord)

    fun mobDead(id: String)

    fun mobMove(id: String, oldCoord: Coord, newCoord: Coord)

    fun mobMoved(id: String, oldCoord: Coord, newCoord: Coord)

    fun mobAttack(id: String)

    fun playerAttack(playerCoord: Coord, mobCoord: Coord)

    fun moveKeyPress(moveType: MoveType, alt: Boolean)

    fun collision(oldCoord: Coord, newCoord: Coord)

    fun interactWithItem(id: Item)

    fun raiseItem(item: Item)

    fun dropItem(item: Item)

    fun drawInventory(inventory: Inventory)
}

/**
 * Main router of events, every entity should be subscribed
 * to this router.
 */
class MainRouter : EventRouter{
    private var listeners: List<Listener> = listOf()

    override fun numKeyPress(numKey: Int, alt: Boolean) {
        listeners.forEach { it -> it.onNumKeyPressed(numKey, alt) }
    }

    override fun subscribe(listener: Listener) {
        listeners += listener
    }

    override fun unsubscribe(listener: Listener) {
        listeners -= listener
    }

    override fun playerMove(oldCoord: Coord, newCoord: Coord, visible: Array<DoubleArray>) {
        listeners.forEach { it -> it.onPlayerMoveListener(oldCoord, newCoord, visible) }
    }

    override fun playerMoved(oldCoord: Coord, newCoord: Coord) {
        listeners.forEach { it -> it.onPlayerMovedListener(oldCoord, newCoord) }
    }

    override fun mobDead(id: String) {
        listeners.forEach { it -> it.onMobDead(id) }
    }

    override fun mobMove(id: String, oldCoord: Coord, newCoord: Coord) {
        listeners.forEach { it -> it.onMobMoveListener(id, oldCoord, newCoord) }
    }

    override fun mobMoved(id: String, oldCoord: Coord, newCoord: Coord) {
        listeners.forEach { it -> it.onMobMovedListener(id, oldCoord, newCoord) }
    }

    override fun mobAttack(id: String) {
        listeners.forEach { it -> it.onMobAttackListener(id) }
    }

    override fun playerAttack(playerCoord: Coord, mobCoord: Coord) {
        listeners.forEach { it -> it.onPlayerAttackListener(playerCoord, mobCoord) }
    }

    override fun moveKeyPress(moveType: MoveType, alt: Boolean) {
        listeners.forEach { it -> it.onMoveKeyPressed(moveType, alt) }
    }

    override fun collision(oldCoord: Coord, newCoord: Coord) {
        listeners.forEach { it -> it.onCollisionListener(oldCoord, newCoord) }
    }

    override fun interactWithItem(id: Item) {
        listeners.forEach { it -> it.onItemInteraction(id) }
    }

    override fun raiseItem(item: Item) {
        listeners.forEach { it -> it.onRaisedItem(item) }
    }

    override fun dropItem(item: Item) {
        listeners.forEach { it -> it.onDroppedItem(item) }
    }

    override fun drawInventory(inventory: Inventory) {
        listeners.forEach { it -> it.onDrawInventory(inventory) }
    }

}