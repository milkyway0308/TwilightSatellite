package skywolf46.twilightsatellite.bukkit.configuration

import org.bukkit.configuration.file.YamlConfiguration

class SatelliteConfiguration(yaml: YamlConfiguration) {
    val proxy = ProxyConfiguration(yaml.getConfigurationSection("프록시 연동")!!)
}