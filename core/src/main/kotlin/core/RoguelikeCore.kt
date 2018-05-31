package core

import com.badlogic.gdx.ApplicationAdapter
import squidpony.squidmath.RNG

class RoguelikeAppAdapter(val config: Configuration) : ApplicationAdapter() {
    lateinit var render: RogueLikeRender

    override fun create() {
        val level = firstLevel(config, RNG())
        val player = Player(level.startPosition, level = level)
        render = RogueLikeRender(level, player.visible, config)

        EventRouter.subscribe(player)
        EventRouter.subscribe(render)
        level.mobs.forEach { it ->
            run {
                EventRouter.subscribe(it)
                EventRouter.mobMoved(it.id, it.coord, it.coord)
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




