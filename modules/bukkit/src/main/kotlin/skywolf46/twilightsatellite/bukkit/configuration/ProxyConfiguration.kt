package skywolf46.twilightsatellite.bukkit.configuration

import org.bukkit.configuration.ConfigurationSection

class ProxyConfiguration(section: ConfigurationSection) {
    val enabled = section.getBoolean("활성화")
    val host = section.getString("호스트.주소")!!
    val port = section.getInt("호스트.포트")
    val authKey = section.getString("보안.인증키")!!
}