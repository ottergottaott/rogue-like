package core

import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import squidpony.squidgrid.Direction
import squidpony.squidmath.Coord
import squidpony.squidmath.RNG


class MobPlayerTest {
    private lateinit var eventRouter: MockRouter
    private lateinit var defaultLevel: Level

    @Before
    fun init() {
        eventRouter = MockRouter()
        defaultLevel = firstLevel(loadConfiguration(), RNG("test"), eventRouter)
    }

    @After
    fun reset() {
        eventRouter = MockRouter()
    }

    @Test
    fun `player move`() {
        val player = Player(Coord.get(10, 10), level = defaultLevel, eventRouter = eventRouter)
        val mob1 = SimpleWarrior("war1" , Coord.get(10, 12), defaultLevel, eventRouter)
        val mob2 = SimpleWarrior("war1" , Coord.get(10, 13), defaultLevel, eventRouter)

        eventRouter.subscribe(player)
        eventRouter.subscribe(mob1)
        eventRouter.subscribe(mob2)

        eventRouter.playerMoved(player.coord, player.coord.translate(Direction.DOWN))

        assertThat(eventRouter.eventsHappened)
                .contains(PlayerMoved(Coord.get(10, 10), Coord.get(10, 11)))

        assertThat(eventRouter.eventsHappened)
                .contains(MobAttacked("war1"))

        assertThat(eventRouter.eventsHappened.size)
                .isEqualTo(3)

    }

    @Test
    fun `mob moved`() {
        val player = Player(Coord.get(10, 10), level = defaultLevel, eventRouter = eventRouter)
        val mob1 = SimpleWarrior("war1" , Coord.get(10, 15), defaultLevel, eventRouter)
        val mob2 = SimpleWarrior("war2" , Coord.get(10, 17), defaultLevel, eventRouter)

        eventRouter.subscribe(player)
        eventRouter.subscribe(mob1)
        eventRouter.subscribe(mob2)

        eventRouter.playerMoved(player.coord, player.coord.translate(Direction.DOWN))

        println(eventRouter.eventsHappened)
        assertThat(eventRouter.eventsHappened)
                .contains(PlayerMoved(Coord.get(10, 10), Coord.get(10, 11)))
        assertThat(eventRouter.eventsHappened)
                .contains(MobMoved("war1", Coord.get(10, 15), Coord.get(10, 14)))

        assertThat(eventRouter.eventsHappened)
                .contains(MobMoved("war2", Coord.get(10, 17), Coord.get(10, 17)))


        assertThat(eventRouter.eventsHappened.size)
                .isEqualTo(3)

    }
}