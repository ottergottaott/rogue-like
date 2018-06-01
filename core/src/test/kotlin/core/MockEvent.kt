package core

import squidpony.squidmath.Coord

sealed class Event

data class MobMoved(val id: String, val oldCoord: Coord, val newCoord: Coord) : Event()
data class PlayerMoved(val oldCoord: Coord, val newCoord: Coord) : Event()
data class MobAttacked (val id: String) : Event()