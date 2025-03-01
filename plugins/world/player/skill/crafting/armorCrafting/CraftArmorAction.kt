package world.player.skill.crafting.armorCrafting

import api.attr.Attr
import api.predef.*
import io.luna.game.action.Action
import io.luna.game.action.ProducingAction
import io.luna.game.model.item.Item
import io.luna.game.model.mob.Animation
import io.luna.game.model.mob.Player

/**
 * A [ProducingAction] used for crafting armor from tanned hides.
 */
class CraftArmorAction(private val plr: Player,
                       private val armor: HideArmor,
                       amount: Int) : ProducingAction(plr, true, 4, amount) {

    companion object {

        /**
         * The crafting animation.
         */
        val ANIM = Animation(1249)

        /**
         * The needle identifier.
         */
        val NEEDLE_ID = 1733

        /**
         * The thread identifier.
         */
        val THREAD_ID = 1734
    }

    /**
     * An attribute that signifies when a spool of thread will be consumed.
     */
    private var Player.threadLeft by Attr<Int>("thread_left")

    override fun canProduce() =
        when {
            plr.crafting.level < armor.level -> {
                plr.sendMessage("You need a Crafting level of ${armor.level} to make this.")
                false
            }
            !plr.inventory.containsAll(NEEDLE_ID, THREAD_ID) -> {
                plr.sendMessage("You need a needle and thread in order to craft armor.")
                false
            }
            else -> true
        }

    override fun onProduce() {
        mob.animation(ANIM)
        mob.sendMessage("You craft the hide into armor.")
    }

    override fun add(): Array<Item> = arrayOf(armor.armorItem)

    override fun remove(): Array<Item> {
        val rem = armor.hidesItem!!
        return if (mob.threadLeft <= 0) {
            // We have no thread left, remove one from inventory and reset counter.
            mob.threadLeft = rand(4) + 1
            arrayOf(rem, Item(THREAD_ID))
        } else {
            // Decrement thread counter.
            mob.threadLeft--
            arrayOf(rem)
        }
    }

    override fun isEqual(other: Action<*>?) =
        when (other) {
            is CraftArmorAction -> armor == other.armor
            else -> false
        }
}
