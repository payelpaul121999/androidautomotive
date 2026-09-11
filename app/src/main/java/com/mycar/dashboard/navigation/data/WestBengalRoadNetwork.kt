package com.mycar.dashboard.navigation.data

import com.mycar.dashboard.navigation.domain.GeoLocation
import com.mycar.dashboard.navigation.domain.Maneuver
import com.mycar.dashboard.navigation.domain.Place
import com.mycar.dashboard.navigation.domain.PlaceCategory
import com.mycar.dashboard.navigation.domain.RoadSegment
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Offline-capable demo graph: Kolkata → Howrah → Dankuni → Bardhaman.
 * Coordinates are approximate WGS84, good enough for a simulated map canvas.
 */
object WestBengalRoadNetwork {

    data class Node(val id: String, val place: Place)

    val kolkata = node(
        id = "kolkata",
        name = "Kolkata",
        address = "Park Street, Kolkata",
        lat = 22.5535,
        lng = 88.3516,
        category = PlaceCategory.CITY,
    )
    val howrah = node(
        id = "howrah",
        name = "Howrah",
        address = "Howrah Station, Howrah",
        lat = 22.5958,
        lng = 88.2636,
        category = PlaceCategory.STATION,
    )
    val dankuni = node(
        id = "dankuni",
        name = "Dankuni",
        address = "Dankuni, Hooghly",
        lat = 22.6747,
        lng = 88.2984,
        category = PlaceCategory.CITY,
    )
    val bardhaman = node(
        id = "bardhaman",
        name = "Bardhaman",
        address = "Bardhaman Junction",
        lat = 23.2324,
        lng = 87.8615,
        category = PlaceCategory.STATION,
    )

    val home = Place(
        id = "home",
        name = "Home",
        address = "Salt Lake Sector I, Kolkata",
        location = GeoLocation(22.5800, 88.4170),
        category = PlaceCategory.HOME,
    )
    val work = Place(
        id = "work",
        name = "Work",
        address = "Sector V, Salt Lake",
        location = GeoLocation(22.5760, 88.4330),
        category = PlaceCategory.WORK,
    )

    val victoria = Place(
        id = "victoria",
        name = "Victoria Memorial",
        address = "Queen's Way, Kolkata",
        location = GeoLocation(22.5448, 88.3426),
        category = PlaceCategory.LANDMARK,
    )

    val nodes: List<Node> = listOf(kolkata, howrah, dankuni, bardhaman)

    val catalogPlaces: List<Place> = listOf(
        kolkata.place,
        howrah.place,
        dankuni.place,
        bardhaman.place,
        home,
        work,
        victoria,
    )

    val edges: List<RoadSegment> = listOf(
        segment("e-kol-how", "Howrah Bridge / GT Road", kolkata, howrah, Maneuver.TURN_LEFT, "Keep left | | ·"),
        segment("e-how-dan", "Durgapur Expressway", howrah, dankuni, Maneuver.STRAIGHT, "| | | Keep centre"),
        segment("e-dan-bar", "NH19", dankuni, bardhaman, Maneuver.SLIGHT_LEFT, "Keep left | | ·"),
        segment("e-how-kol", "Howrah Bridge / GT Road", howrah, kolkata, Maneuver.TURN_RIGHT, "Keep right · | |"),
        segment("e-dan-how", "Durgapur Expressway", dankuni, howrah, Maneuver.STRAIGHT, "| | | Keep centre"),
        segment("e-bar-dan", "NH19", bardhaman, dankuni, Maneuver.STRAIGHT, "| | |"),
        segment("e-kol-vic", "Queen's Way", kolkata, Node("victoria", victoria), Maneuver.TURN_RIGHT, "Keep right · | |"),
        segment("e-vic-kol", "Queen's Way", Node("victoria", victoria), kolkata, Maneuver.TURN_LEFT, "Keep left | | ·"),
        segment("e-kol-home", "EM Bypass", kolkata, Node("home", home), Maneuver.TURN_RIGHT, "Keep right · | |"),
        segment("e-home-kol", "EM Bypass", Node("home", home), kolkata, Maneuver.TURN_LEFT, "Keep left | | ·"),
        segment("e-kol-work", "IT Park Rd", kolkata, Node("work", work), Maneuver.SLIGHT_RIGHT, "Keep right · | |"),
        segment("e-work-kol", "IT Park Rd", Node("work", work), kolkata, Maneuver.SLIGHT_LEFT, "Keep left | | ·"),
    )

    fun nearestNode(location: GeoLocation): Node {
        return (nodes + listOf(Node("home", home), Node("work", work), Node("victoria", victoria)))
            .minBy { haversineMeters(location, it.place.location) }
    }

    fun shortestPath(from: Node, to: Node): List<RoadSegment>? {
        if (from.id == to.id) return emptyList()
        val adj = mutableMapOf<String, MutableList<RoadSegment>>()
        edges.forEach { edge ->
            adj.getOrPut(edge.fromNodeId) { mutableListOf() }.add(edge)
        }
        data class Step(val nodeId: String, val path: List<RoadSegment>, val cost: Double)
        val queue = ArrayDeque<Step>()
        val seen = mutableSetOf<String>()
        queue.add(Step(from.id, emptyList(), 0.0))
        while (queue.isNotEmpty()) {
            val step = queue.removeFirst()
            if (step.nodeId in seen) continue
            seen.add(step.nodeId)
            if (step.nodeId == to.id) return step.path
            adj[step.nodeId].orEmpty().forEach { edge ->
                queue.add(Step(edge.toNodeId, step.path + edge, step.cost + edge.distanceMeters))
            }
        }
        return null
    }

    fun interpolate(a: GeoLocation, b: GeoLocation, fraction: Double): GeoLocation {
        val t = fraction.coerceIn(0.0, 1.0)
        return GeoLocation(
            latitude = a.latitude + (b.latitude - a.latitude) * t,
            longitude = a.longitude + (b.longitude - a.longitude) * t,
            bearing = bearingDegrees(a, b),
            altitude = a.altitude + (b.altitude - a.altitude) * t,
        )
    }

    fun densify(a: GeoLocation, b: GeoLocation, points: Int = 24): List<GeoLocation> {
        val list = ArrayList<GeoLocation>(points + 1)
        for (i in 0..points) {
            list.add(interpolate(a, b, i / points.toDouble()))
        }
        return list
    }

    fun haversineMeters(a: GeoLocation, b: GeoLocation): Double {
        val r = 6_371_000.0
        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLon = Math.toRadians(b.longitude - a.longitude)
        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)
        val h = sin(dLat / 2) * sin(dLat / 2) +
            cos(lat1) * cos(lat2) * sin(dLon / 2) * sin(dLon / 2)
        return 2 * r * atan2(sqrt(h), sqrt(1 - h))
    }

    fun bearingDegrees(from: GeoLocation, to: GeoLocation): Float {
        val lat1 = Math.toRadians(from.latitude)
        val lat2 = Math.toRadians(to.latitude)
        val dLon = Math.toRadians(to.longitude - from.longitude)
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        val deg = Math.toDegrees(atan2(y, x))
        return ((deg + 360.0) % 360.0).toFloat()
    }

    private fun node(
        id: String,
        name: String,
        address: String,
        lat: Double,
        lng: Double,
        category: PlaceCategory,
    ) = Node(
        id = id,
        place = Place(
            id = id,
            name = name,
            address = address,
            location = GeoLocation(lat, lng),
            category = category,
        ),
    )

    private fun segment(
        id: String,
        name: String,
        from: Node,
        to: Node,
        maneuver: Maneuver,
        laneHint: String,
    ): RoadSegment {
        val poly = densify(from.place.location, to.place.location)
        val distance = haversineMeters(from.place.location, to.place.location)
        return RoadSegment(
            id = id,
            name = name,
            fromNodeId = from.id,
            toNodeId = to.id,
            distanceMeters = distance,
            maneuver = maneuver,
            polyline = poly,
            laneHint = laneHint,
        )
    }
}
