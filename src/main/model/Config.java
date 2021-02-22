package main.model

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.regex.Pattern

object Config {
    @JvmField
    var delayTimeList: MutableList<Int>? = null
    @JvmField
    var stockList: MutableList<Stock>? = null
    @JvmField
    var title: String? = null
    @JvmStatic
    fun init() {
        loadSettingConfig()
        loadStockConfig()
    }

    private fun loadSettingConfig() {
        if (delayTimeList == null) {
            delayTimeList = ArrayList()
        }
        var br: BufferedReader? = null
        try {
            val filePath = "config.txt"
            val sb = StringBuilder()
            br = BufferedReader(InputStreamReader(FileInputStream(filePath)))
            var temp: String?
            while (br.readLine().also { temp = it } != null) {
                sb.append(temp)
                sb.append("\n")
            }
            temp = sb.toString().trim { it <= ' ' }
            val m = Pattern.compile("=\"\\S+\"").matcher(temp)
            while (m.find()) {
                var s = m.group()
                s = s.substring(2, s.length - 1)
                if (isInteger(s)) {
                    delayTimeList!!.add(s.toInt())
                } else {
                    title = s
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                assert(br != null)
                br!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun loadStockConfig() {
        if (stockList == null) {
            stockList = ArrayList()
        }
        val filePath = "stock.txt"
        var br: BufferedReader? = null
        try {
            br = BufferedReader(InputStreamReader(FileInputStream(filePath)))
            var temp: String
            while (br.readLine().also { temp = it } != null) {
                val ss = temp.trim { it <= ' ' }.split("\\s+".toRegex()).toTypedArray()
                val stock = Stock(ss[0], ss[1], ss[2], ss[3])
                stockList!!.add(stock)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                assert(br != null)
                br!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun isInteger(s: String): Boolean {
        val pattern = Pattern.compile("^[-+]?[\\d]*$")
        return pattern.matcher(s).matches()
    }
}