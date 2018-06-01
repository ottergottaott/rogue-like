package core

import com.badlogic.gdx.ApplicationAdapter
import squidpony.squidmath.RNG

/**
 * Adapter for running main app
 */
class RoguelikeAppAdapter(val config: Configuration, val eventRouter: EventRouter) : ApplicationAdapter() {
    lateinit var render: RogueLikeRender

    override fun create() {
        val level = firstLevel(config, RNG(), eventRouter)
        val player = Player(level.startPosition, level = level, eventRouter = eventRouter)
        render = RogueLikeRender(level, player.visible, config, eventRouter)

        eventRouter.subscribe(player)
        eventRouter.subscribe(render)
        eventRouter.subscribe(Logger(System.out))
        level.mobs.forEach { it ->
            run {
                eventRouter.subscribe(it)
                eventRouter.mobMoved(it.id, it.coord, it.coord)
            }
        }
    }

    override fun render() {
        render.render()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        render.resize(width, height)
    }
}




