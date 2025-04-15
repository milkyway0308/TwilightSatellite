package skywolf46.twilightsatellite.bukkit.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent
import skywolf46.twilightsatellite.bukkit.TwilightSatellite

class MotdListener : Listener {
    @EventHandler
    fun onMotdPing(event: ServerListPingEvent) {
        runCatching {
            event.motd = "Satellite | ${TwilightSatellite.instance.client.getIdentify()}"
        }
    }
}