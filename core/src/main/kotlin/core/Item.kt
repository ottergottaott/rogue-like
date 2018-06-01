package core

import squidpony.squidmath.Coord


enum class ItemType {
    ATTACK, DEFENSE
}

abstract class Item(val id: String, var coord: Coord, val bonus: Int, val type: ItemType, val eventRouter: EventRouter) : Listener {
    override fun onPlayerMovedListener(oldCoord: Coord, newCoord: Coord) {
        if (newCoord == coord) {
            eventRouter.interactWithItem(this)
        }
    }

    override fun onRaisedItem(item: Item) {
        if (this.id == item.id) {
            eventRouter.unsubscribe(this)
        }
    }

    abstract fun move(coord: Coord): Item
}

class SimpleShield(id: String, coord: Coord, eventRouter: EventRouter) : Item(id, coord, 3, ItemType.DEFENSE, eventRouter) {
    override fun toString(): String = "Simple shield"
    override fun move(coord: Coord): Item = SimpleShield(id, coord, eventRouter)
}

class SimpleSword(id: String, coord: Coord, eventRouter: EventRouter) : Item(id, coord, 2, ItemType.DEFENSE, eventRouter) {
    override fun toString(): String = "Simple sword"
    override fun move(coord: Coord): Item = SimpleSword(id, coord, eventRouter)
}
