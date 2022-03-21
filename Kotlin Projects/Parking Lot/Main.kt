package parking

class Car(val id: String, val color: String)

class ParkingLot(private val lotSize: Int) {
    private val lotMap: MutableList<Car?> = mutableListOf()

    init {
        println("Created a parking lot with $lotSize spots.")
    }

    fun park(car: Car?): String {
        var spot = 0
        for (i in 0 until lotSize) {
            if (i < lotMap.size) {
                if (lotMap[i] == null) {
                    lotMap[i] = car
                    spot = i + 1
                    break
                }
            } else {
                lotMap.add(car)
                spot = lotMap.size
                break
            }
        }
        if (spot == 0) {
            return "Sorry, the parking lot is full."
        }
        return "${car!!.color} car parked in spot $spot."
    }

    fun leave(parkingSpace: Int): String {
        if (parkingSpace <= lotMap.size) {
            if (lotMap[parkingSpace - 1] != null) {
                lotMap[parkingSpace - 1] = null
                return "Spot $parkingSpace is free."
            }
        }
        return "There is no car in spot $parkingSpace."
    }

    fun status(): String {
        var status = ""
        for (i in 0 until lotMap.size) {
            if (lotMap[i] != null) {
                val car = lotMap[i]!!
                status += "${i + 1} ${car.id} ${car.color}\n"
            }
        }
        return if (status == "") "Parking lot is empty." else status.substring(0, status.length - 1)
    }

    fun regByColor(color: String): String {
        var regByColor = ""
        for (i in 0 until lotMap.size) {
            if (lotMap[i]?.color?.toUpperCase() == color.toUpperCase()) {
                regByColor += "${lotMap[i]?.id}, "
            }
        }
        return if (regByColor == "")
            "No cars with color ${color.toUpperCase()} were found."
        else regByColor.substring(0, regByColor.length - 2)
    }

    fun spotByColor(color: String): String {
        var spotByColor = ""
        for (i in 0 until lotMap.size) {
            if (lotMap[i]?.color?.toUpperCase() == color.toUpperCase()) {
                spotByColor += "${i + 1}, "
            }
        }
        return if (spotByColor == "")
            "No cars with color $color were found."
        else spotByColor.substring(0, spotByColor.length - 2)
    }

    fun spotByReg(id: String): String {
        var spotByReg = ""
        for (i in 0 until lotMap.size) {
            if (lotMap[i]?.id == id) {
                spotByReg += "${i + 1}, "
            }
        }
        return if (spotByReg == "")
            "No cars with registration number $id were found."
        else spotByReg.substring(0, spotByReg.length - 2)
    }
}

fun main() {
    var parkingLot: ParkingLot? = null
    val parkingLotNotCreatedMessage = "Sorry, a parking lot has not been created."
    while (true) {
        val input = readLine()!!
        val command = input.split(" ")
        if (command[0] == "exit") {
            break
        }
        when (command[0]) {
            "park" -> {
                if (parkingLot == null) {
                    println(parkingLotNotCreatedMessage)
                } else {
                    val car = Car(command[1], command[2])
                    println(parkingLot.park(car))
                }
            }
            "leave" -> {
                if (parkingLot == null) {
                    println(parkingLotNotCreatedMessage)
                } else {
                    println(parkingLot.leave(command[1].toInt()))
                }
            }
            "create" -> {
                parkingLot = ParkingLot(command[1].toInt())
            }
            "status" -> {
                if (parkingLot == null) {
                    println(parkingLotNotCreatedMessage)
                } else {
                    println(parkingLot.status())
                }
            }
            "reg_by_color" -> {
                if (parkingLot == null) {
                    println(parkingLotNotCreatedMessage)
                } else {
                    println(parkingLot.regByColor(command[1]))
                }
            }
            "spot_by_color" -> {
                if (parkingLot == null) {
                    println(parkingLotNotCreatedMessage)
                } else {
                    println(parkingLot.spotByColor(command[1]))
                }
            }
            "spot_by_reg" -> {
                if (parkingLot == null) {
                    println(parkingLotNotCreatedMessage)
                } else {
                    println(parkingLot.spotByReg(command[1]))
                }
            }
        }
    }
}
