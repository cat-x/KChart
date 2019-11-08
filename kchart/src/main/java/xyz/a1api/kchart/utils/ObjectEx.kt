package xyz.a1api.kchart.utils

/**
 * Created by Cat-x on 2018/11/26.
 * For KChart
 * Cat-x All Rights Reserved
 */

@JvmName("enableItemListToSaveString")
fun ArrayList<EnableItem>?.toSaveString(): String {
    var string = ""
    if (this != null) {
        for (num in this) {
            string = "$string${num.name}:${num.enable}|"
        }
    }
    return string
}

@JvmName("enableItemHashToSaveString")
fun HashMap<Int, EnableItem>?.toSaveString(): String {
    var string = ""
    if (this != null) {
        for (num in this) {
            string = "$string${num.key}:${num.value.name}:${num.value.enable}|"
        }
    }
    return string
}

fun Triple<Int, Int, Int>?.toSaveString(): String {
    var string = ""
    if (this != null) {
        string = "$first|$second|$third"
    }
    return string
}

fun String.toUseConfig(): Triple<Int, Int, Int>? {
    val list = split("|").map { it.toIntOrNull() }
    if (list.size == 3) {
        return Triple(list[0]!!, list[1]!!, list[2]!!)
    }
    return null
}

//fun String.toShowConfig(): ArrayList<EnableItem>? {
//    if (this.isEmpty()) {
//        return null
//    }
//    val configs = arrayListOf<EnableItem>()
//    //去掉末尾的|
//    val list = this.dropLast(1).split("|")
//    for (string in list) {
//        val data = string.split(":")
//        configs.add(EnableItem(data[0].toInt(), data[1].toBoolean()))
//    }
//    return configs;
//}


fun String.toShowConfig(): HashMap<Int, EnableItem>? {
    if (this.isEmpty()) {
        return null
    }
    val configs = hashMapOf<Int, EnableItem>()
    //去掉末尾的|
    val list = this.dropLast(1).split("|")
    for (string in list) {
        val data = string.split(":")
        configs[data[0].toInt()] = (EnableItem(data[1].toInt(), data[2].toBoolean()))
    }
    return configs;
}

fun ArrayList<EnableItem>.toCalculate(): IntArray {
    val list = arrayListOf<Int>()
    for (enableItem in this) {
        if (enableItem.enable && enableItem.name > 0) {
            list.add(enableItem.name)
        }
    }
    return list.toIntArray()
}

fun HashMap<Int, EnableItem>.toCalculate(): IntArray {
    val list = arrayListOf<Int>()
    for (enableItem in this) {
        if (enableItem.value.enable && enableItem.value.name > 0) {
            list.add(enableItem.value.name)
        }
    }
    return list.toIntArray()
}


@JvmName("intListToSaveString")
fun ArrayList<Int>?.toSaveString(): Set<String> {
    if (this == null) {
        return setOf()
    }
    val set = hashSetOf<String>()
    for (i in this.toList()) {
        set.add(i.toString())
    }
    return set;
}

fun ArrayList<String>.toCalculate(): Set<Int> {
    val set = hashSetOf<Int>()
    for (i in this.toList()) {
        set.add(i.toInt())
    }
    return set;
}

fun String.toCalculatePair(): Pair<Int, Int>? {
    if (this.isEmpty()) {
        return null
    }
    val list = this.trim().replace("(", "")
        .replace(")", "").split(",")
    return list[0].toInt() to list[1].toInt()
}

fun String.toCalculateTriple(): Triple<Int, Int, Int>? {
    if (this.isEmpty()) {
        return null
    }
    val list = this.trim().replace("(", "")
        .replace(")", "").split(",")
    return Triple(list[0].toInt(), list[1].toInt(), list[2].toInt())
}

public inline fun <K, V> Map<out K, V>.unfold(action: (K, V) -> Unit): Unit {
    for (element in this) action(element.key, element.value)
}

public inline fun Map<out Int, EnableItem>.unfold(action: (index: Int, num: Int, enable: Boolean) -> Unit): Unit {
    for (element in this) action(element.key, element.value.name, element.value.enable)
}
