package skywolf46.test

import skywolf46.twilightsatellite.bukkit.util.ReadOnlyList
import skywolf46.twilightsatellite.bukkit.util.safeIterator

object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        val rol = ReadOnlyList(listOf("A", "B", "C"))
        println(rol.size)
        for (x in rol)
            println(x)
        println("---")
        for (x in rol.indices) {
            println(rol[x])
        }
        println("---")

        rol.iterator().forEachRemaining {
            println(it)
        }

        println("--- Safe Iterator Test 1")
        val testList = mutableListOf("A", "B", "C")

        val safeList = testList.safeIterator()
        while (safeList.hasNext()) {
            println(safeList.next())
            safeList.replace("D")
        }
        println(testList)

        println("--- Safe Iterator Test 2")

        val testList2 = mutableListOf("A", "B", "C")

        val safeList2 = testList2.safeIterator()
        while (safeList2.hasNext()) {
            println(safeList2.next())
            if (safeList2.movedCount() == 2) {
                safeList2.remove()
                println("Removed")
            }

        }

        println(testList2)

        println("--- Safe Iterator Test 3")

        val testList3 = mutableListOf("A", "B", "C")

        val safeList3 = testList3.safeIterator()
        while (safeList3.hasNext()) {
            println(safeList3.next())
            safeList3.add("D")
        }

        println(testList3)

        println("--- Safe Iterator Test 4")

        val testList4 = mutableListOf("A", "B", "C")
        val safeList4 = testList4.safeIterator()
        while (safeList4.hasNext()) {
            println(safeList4.next())
            testList4.add("D")
        }
        println(testList4)
    }
}