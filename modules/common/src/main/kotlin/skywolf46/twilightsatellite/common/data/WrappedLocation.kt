package skywolf46.twilightsatellite.common.data

import skywolf46.atmospherereentry.api.packetbridge.annotations.ReflectedSerializer

@ReflectedSerializer
class WrappedLocation(val world: String, val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float)