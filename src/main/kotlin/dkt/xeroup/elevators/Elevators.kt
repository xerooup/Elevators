package dkt.xeroup.elevators

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import net.kyori.adventure.text.Component as AdventureComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import net.kyori.adventure.key.Key
import org.bukkit.block.BlockFace
import org.bukkit.event.player.PlayerToggleFlightEvent
import net.kyori.adventure.sound.Sound as AdventureSound

class Elevators : JavaPlugin() {

    override fun onEnable() {
        try {
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage")
        } catch (e: ClassNotFoundException) {
            logger.severe("============================================")
            logger.severe("THIS PLUGIN REQUIRES PAPER/PURPUR SERVER!")
            logger.severe("Download from: https://papermc.io/downloads")
            logger.severe("============================================")
            server.pluginManager.disablePlugin(this)
            return
        }
        logger.info("Enabled!")
        saveDefaultConfig()
        reloadConfig()
        server.pluginManager.registerEvents(Listener(), this)
    }
    override fun onDisable() {
        logger.info("Disabled!")
    }
}

class Listener : Listener {
    private val plugin = Bukkit.getPluginManager().getPlugin("Elevators")
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val loc = event.player.location.clone(); loc.y -= 1
        val block = loc.block
        if (block.type == Material.RED_WOOL) {
            event.player.sendActionBar(MiniMessage
                .miniMessage()
                .deserialize(plugin?.config?.getString("messages.elevator.hint")
                    .toString()))
        }
    }
    @EventHandler
    fun onPlayerShift(event: PlayerToggleSneakEvent) {
        if (!event.player.isSneaking) return
        val loc = event.player.location.clone(); loc.y -= 1; val block = loc.block
        if (block.type == Material.RED_WOOL) {
            var attempt = 0
            val maxAttempts = plugin?.config?.getInt("settings.max_radius")
            var floor: Any = 0
            val blockLoc = loc.clone()
            if (maxAttempts != null) {
                while (floor == 0 && maxAttempts > attempt) {
                    attempt++
                    blockLoc.y -= 1
                    if (blockLoc.block.type == Material.RED_WOOL) {
                        floor = blockLoc.clone(); floor.y += 1
                        event.player.teleport(floor)
                        event.player.showTitle(
                            Title.title(
                                MiniMessage.miniMessage()
                                    .deserialize(plugin.config.getString("messages.elevator.down")
                                        .toString()),
                                AdventureComponent.empty()))
                        event.player.playSound(
                            AdventureSound.sound(
                                Key.key(plugin.config.getString("sounds.down", "entity.enderman.teleport")!!),
                                AdventureSound.Source.PLAYER,
                                1.0f,
                                1.0f
                            )
                        )
                            break
                        }
                    }
                }
            }
    }
    @EventHandler
    fun onPlayerSpace(event: PlayerJumpEvent) {
            val loc = event.player.location.clone(); loc.y -= 1; val block = loc.block
            if (event.player.location.block.getRelative(BlockFace.DOWN).type == Material.RED_WOOL) {
                var attempt = 0
                val maxAttempts = plugin?.config?.getInt("settings.max_radius")
                var floor: Any = 0
                val blockLoc = loc.clone()
                if (maxAttempts != null) {
                    while (floor == 0 && maxAttempts > attempt) {
                        attempt++
                        blockLoc.y += 1
                        if (blockLoc.block.type == Material.RED_WOOL) {
                            floor = blockLoc.clone(); floor.y += 1
                            event.player.teleport(floor)
                            event.player.showTitle(
                                Title.title(
                                    MiniMessage.miniMessage()
                                        .deserialize(plugin.config.getString("messages.elevator.up")
                                            .toString()),
                                    AdventureComponent.empty()))
                            event.player.playSound(
                                AdventureSound.sound(
                                    Key.key(plugin.config.getString("sounds.up", "entity.enderman.teleport")!!),
                                    AdventureSound.Source.PLAYER,
                                    1.0f,
                                    1.0f
                                )
                            )
                            break
                        }
                    }
                }
            }
    }
}