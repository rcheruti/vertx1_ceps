package br.com.rcc_dev.testes

import com.codahale.metrics.MetricRegistry
import io.vertx.core.json.JsonObject
import java.io.BufferedReader
import java.io.InputStreamReader

object Utils {

  val metrics = MetricRegistry()
  val requests = metrics.meter("requests")

  val charUTF_8 = "UTF-8"
  val charISO_8859_1 = "ISO-8859-1"

  // ----------------------------------

  fun fileContentsJar(name: String): String{
    val lf = System.getProperty("line.separator")
    val inRes = Utils::class.java.classLoader.getResourceAsStream(name)
    return BufferedReader(InputStreamReader(inRes)).lines().reduce("") { a, b -> a + lf + b }
  }

  fun jInt(obj: JsonObject, path: String): Int {
    val paths = path.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    var node = obj
    for (i in 0 until paths.size - 1) {
      node = node.getJsonObject(paths[i])
    }
    return node.getInteger(paths[paths.size - 1])!!
  }

}