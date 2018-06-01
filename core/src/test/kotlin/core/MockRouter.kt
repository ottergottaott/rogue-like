package core

import squidpony.squidmath.Coord

class MockRouter: MainRouter() {

    var eventsHappened: List<Event> = listOf()

    override fun playerMoved(oldCoord: Coord, newCoord: Coord) {
        eventsHappened += PlayerMoved(oldCoord, newCoord)
        super.playerMoved(oldCoord, newCoord)
    }

    override fun mobMoved(id: String, oldCoord: Coord, newCoord: Coord) {
        eventsHappened += MobMoved(id, oldCoord, newCoord)
        super.mobMoved(id, oldCoord, newCoord)
    }

    override fun mobMove(id: String, oldCoord: Coord, newCoord: Coord) {
        eventsHappened += MobMoved(id, oldCoord, newCoord)
        super.mobMoved(id, oldCoord, newCoord)
    }


    override fun mobAttack(id: String) {
        eventsHappened += MobAttacked(id)
        super.mobAttack(id)
    }
}