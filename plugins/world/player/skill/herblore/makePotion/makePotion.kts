package world.player.skill.herblore.makePotion

import api.predef.*
import io.luna.game.action.Action
import io.luna.game.action.ProducingAction
import io.luna.game.event.impl.ItemOnItemEvent
import io.luna.game.model.mob.Animation
import io.luna.game.model.mob.Player
import io.luna.game.model.mob.dialogue.MakeItemDialogueInterface
import world.player.skill.herblore.makePotion.MakePotion.MakePotionAction

/**
 * A [ProducingAction] that will make potions.
 */
class MakePotionAction(plr: Player,
                       val potion: Potion,
                       makeTimes: Int) : ProducingAction(plr, true, 2, makeTimes) {

    companion object {

        /**
         * Potion making animation.
         */
        val ANIMATION = Animation(363)
    }

    override fun canProduce() =
        when {
            mob.herblore.level < potion.level -> {
                mob.sendMessage("You need a Herblore level of ${potion.level} to make this potion.")
                false
            }
            else -> true
        }

    override fun onProduce() {
        mob.sendMessage("You mix the ${itemDef(potion.secondary).name} into your potion.")
        mob.animation(ANIMATION)
        mob.herblore.addExperience(potion.exp)
    }

    override fun add() = arrayOf(potion.idItem)

    override fun remove() = arrayOf(potion.unfItem, potion.secondaryItem)

    override fun isEqual(other: Action<*>) =
        when (other) {
            is MakePotionAction -> potion == other.potion
            else -> false
        }
}

/**
 * Opens a [MakeItemDialogueInterface] to make finished potions.
 */
fun makePotion(plr: Player, potion: Potion) {
    plr.interfaces.open(object : MakeItemDialogueInterface(potion.id) {
        override fun makeItem(plr: Player, id: Int, index: Int, forAmount: Int) =
            plr.submitAction(MakePotionAction(plr, potion, forAmount))
    })
}

/**
 * Start a [MakePotionAction] if the intercepted event contains the required items.
 */
on(ItemOnItemEvent::class) {
    val potion = Potion.getPotion(usedId, targetId)
    if (potion != null) {
        makePotion(plr, potion)
    }
}
