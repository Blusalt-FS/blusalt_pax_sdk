package com.blusalt.blusaltpaxsdk.processor.util

import android.content.Context
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.util.*
import kotlin.experimental.and
import kotlin.experimental.or

object StringUtil {

    private val hexArray = "0123456789ABCDEF".toCharArray()
    fun fromByteArray(data: ByteArray): String {
        val hexChars = CharArray(data.size * 2)
        for (j in data.indices) {
            val v: Int = (data[j] and (0xFF).toByte()).toInt()
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    fun asciiFromByteArray(data: ByteArray): String {
        return hexToAscii(fromByteArray(data))
    }

    //it's come from http://www.baeldung.com/java-convert-hex-to-ascii
    fun asciiToHex(asciiStr: String): String {
        val chars = asciiStr.toCharArray()
        val hex = StringBuilder()
        for (ch in chars) {
            hex.append(Integer.toHexString(ch.toInt()))
        }
        return hex.toString()
    }

    //it's come from http://www.baeldung.com/java-convert-hex-to-ascii
    fun hexToAscii(hexStr: String): String {
        val output = StringBuilder("")
        var i = 0
        while (i < hexStr.length) {
            val str = hexStr.substring(i, i + 2)
            output.append(str.toInt(16).toChar())
            i += 2
        }
        return output.toString()
    }

    fun asciiToHex(data: ByteArray): ByteArray {
        var hexChars: CharArray? = CharArray(data.size * 2)
        for (j in data.indices) {
            val v: Int = (data[j] and (0xFF).toByte()).toInt()
            hexChars!![j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        val res = ByteArray(hexChars!!.size)
        for (i in hexChars.indices) {
            res[i] = hexChars[i].toByte()
        }
        Arrays.fill(hexChars, '\u0000')
        hexChars = null
        return res
    }

    fun hexStringToByteArray(s: String): ByteArray {
        var s = s
        var len = s.length
        var padd = false
        if (len % 2 != 0) {
            s = "0$s"
            len++
            padd = true
        }
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                    + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    fun fromByteBuffer(readBuffer: ByteBuffer): String {
        return fromByteArray(Arrays.copyOfRange(readBuffer.array(), 0, readBuffer.position()))
    }

    fun intToHexString(value: Int): String {
        var hs = Integer.toHexString(value)
        if (hs.length % 2 != 0) hs = "0$hs"
        hs = hs.toUpperCase()
        return hs
    }

    fun asciiToByteArray(bytes: ByteArray): ByteArray {
        return hexStringToByteArray(hexToAscii(fromByteArray(bytes)))
    }

    fun toHexString(str: String): String {
        val sb = StringBuffer()
        for (i in 0 until str.length) {
            sb.append(toHexString(str[i]))
        }
        return sb.toString()
    }

    /**
     * convert into Hexadecimal notation of Unicode.<br></br>
     * example)a?\u0061
     * @param ch
     * @return
     */
    fun toHexString(ch: Char): String {
        var hex = Integer.toHexString(ch.toInt())
        while (hex.length < 4) {
            hex = "0$hex"
        }
        hex = "\\u$hex"
        return hex
    }

    /**
     * str fill，totalLength。
     *
     * @param fill
     * @param totalLength
     * @param str
     * @return
     */
    fun leftPadding(fill: Char, totalLength: Int, str: String): String {
        val buffer = StringBuffer()
        for (i in str.length until totalLength) {
            buffer.append(fill)
        }
        buffer.append(str)
        return buffer.toString()
    }

    fun leftPadding(fill: String?, totalLength: Int, str: String): String {
        val buffer = StringBuffer()
        for (i in str.length until totalLength) {
            buffer.append(fill)
        }
        buffer.append(str)
        return buffer.toString()
    }

    fun leftAppend(fill: String?, appendLength: Int, str: String?): String {
        val buffer = StringBuffer()
        for (i in 0 until appendLength) {
            buffer.append(fill)
        }
        buffer.append(str)
        return buffer.toString()
    }

    fun leftPad(pad: Char = '0', len: Int, str: String): String {
        if (str == null) return ""
        val sb = StringBuilder()
        while (sb.length + str.length < len) {
            sb.append(pad)
        }
        sb.append(str)
        return sb.toString()
    }

    fun rightAppend(fill: String?, appendLength: Int, str: String?): String {
        val buffer = StringBuilder(str.toString())
        for (i in 0 until appendLength) {
            buffer.append(fill)
        }
        return buffer.toString()
    }

    fun rightPadding(fill: String?, totalLength: Int, str: String): String {
        val buffer = StringBuilder(str)
        while (str.length < totalLength) {
            buffer.append(fill)
        }
        return buffer.toString()
    }

    fun rightPad(pad: Char, len: Int, str: String): String {
        if (str == null) return ""
        val sb = StringBuilder()
        sb.append(str)
        while (sb.length < len) {
            sb.append(pad)
        }
        return sb.toString()
    }

//    fun isEmpty(msg: String?): Boolean {
//        return !(msg != null && "" != msg)
//    }
//
    /**
     * String 非空判断
     * @param msg
     * @return
     */
    fun isEmpty(msg: String?): Boolean {
        val `is`: Boolean
        `is` = if (msg != null && "" != msg) {
            false
        } else {
            true
        }
        return `is`
    }


    fun isNull(str: CharSequence?): Boolean {
        return str == null || str.length == 0
    }


    //为 EditText 获取相应的 selection index.即设置光标位置为最右方
    fun getSelectionIndex(str: CharSequence): Int {
        return if (isNull(str)) 0 else str.length
    }


    /**
     * 在str左边填充fill内容，填充后的总长度为totalLength。
     *
     * @param fill
     * @param totalLength
     * @param str
     * @return
     */
//    fun leftPadding(fill: Char, totalLength: Int, str: String): String? {
//        val buffer = StringBuffer()
//        for (i in str.length until totalLength) {
//            buffer.append(fill)
//        }
//        buffer.append(str)
//        return buffer.toString()
//    }
//
//    fun leftPadding(fill: String?, totalLength: Int, str: String): String? {
//        val buffer = StringBuffer()
//        for (i in str.length until totalLength) {
//            buffer.append(fill)
//        }
//        buffer.append(str)
//        return buffer.toString()
//    }
//
//    //左边增加一定长度的字符串
//    fun leftAppend(fill: String?, appendLength: Int, str: String?): String? {
//        val buffer = StringBuffer()
//        for (i in 0 until appendLength) {
//            buffer.append(fill)
//        }
//        buffer.append(str)
//        return buffer.toString()
//    }
//
//    //右边增加一定长度的字符串
//    fun rightAppend(fill: String?, appendLength: Int, str: String?): String? {
//        val buffer = StringBuilder(str)
//        for (i in 0 until appendLength) {
//            buffer.append(fill)
//        }
//        return buffer.toString()
//    }
//
//    fun rightPadding(fill: String?, totalLength: Int, str: String): String? {
//        val buffer = StringBuilder(str)
//        while (str.length < totalLength) {
//            buffer.append(fill)
//        }
//        return buffer.toString()
//    }


    //得到字符串的字节长度
    fun getContentByteLength(content: String?): Int {
        if (content == null || content.length == 0) return 0
        var length = 0
        for (i in 0 until content.length) {
            length += getByteLength(content[i])
        }
        return length
    }

    //得到几位字节长度
    private fun getByteLength(a: Char): Int {
        val tmp = Integer.toHexString(a.toInt())
        return tmp.length shr 1
    }

    //文本右边补空格
    fun fillRightSpacePrintData(context: String?, fillDataLength: Int): String? {
        var context = context
        if (context != null) {
            val printDataLength = fillDataLength - context.length
            if (printDataLength > 0) {
                for (i in 0 until printDataLength) {
                    context += " "
                }
            }
        } else {
            context = ""
            for (i in 0 until fillDataLength) {
                context += " "
            }
        }
        return context
    }

    //文本左边补空格
    fun fillLeftSpacePrintData(context: String?, fillDataLength: Int): String? {
        var context = context
        if (context != null) {
            val printDataLength = fillDataLength - context.length
            if (printDataLength > 0) {
                var tempSpace = ""
                for (i in 0 until printDataLength) {
                    tempSpace += " "
                }
                context = tempSpace + context
            }
        } else {
            context = ""
            for (i in 0 until fillDataLength) {
                context += " "
            }
        }
        return context
    }


    fun leftPad(str: String?, len: Int, pad: Char): String? {
        if (str == null) return null
        val sb = StringBuilder()
        while (sb.length + str.length < len) {
            sb.append(pad)
        }
        sb.append(str)
        return sb.toString()
    }

    fun rightPad(str: String?, len: Int, pad: Char): String? {
        if (str == null) return null
        val sb = StringBuilder()
        sb.append(str)
        while (sb.length < len) {
            sb.append(pad)
        }
        return sb.toString()
    }

    fun maskString(string: String): String? {
        return try {
            val sb = StringBuilder()
            for (i in 0 until string.length) {
                sb.append("*")
            }
            sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "********"
        }
    }

    fun maskPan(pan: String): String? {
        return try {
            val firstPart = pan.substring(0, 6)
            val len = pan.length
            val lastPart = pan.substring(len - 4, len)
            val middlePartLength = len - 6
            val middleLastPart = leftPad("", middlePartLength, '*')
            firstPart + middleLastPart!!.substring(0, middleLastPart.length - 4) + lastPart
        } catch (e: Exception) {
            e.printStackTrace()
            "0000********0000"
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        var a: String? = "阿斯顿法"
        a = leftAppend("-", 10, a)
        Timber.d(a)
        a = rightAppend("*", 6, a)
        Timber.d(a)
        Timber.d("51: " + convertStringToHex("51"))
    }


    /**
     * 例子：“3132ABCD”》{0x31,0x32,0xab,0xcd}
     * @param hex
     * @return
     */
    fun hexStringToByte(hex: String?): ByteArray? {
        if (hex == null || hex.length == 0) {
            return null
        }
        val len = hex.length / 2
        val result = ByteArray(len)
        val achar = hex.toUpperCase().toCharArray()
        for (i in 0 until len) {
            val pos = i * 2
            result[i] = (toByte(achar[pos shl 4]) or toByte(achar[pos + 1])) as Byte
        }
        return result
    }


    //字符序列转换为16进制字符串
    //字符序列转换为16进制字符串
    /**
     * 例子 {0x31,0x32,0xab,0xcd}  >  “3132ABCD”
     * @param
     * @return
     */
    fun bytesToHexString(src: ByteArray?): String? {
        val stringBuilder = StringBuilder("")
        if (src == null || src.size <= 0) {
            return ""
        }
        val buffer = CharArray(2)
        for (i in src.indices) {
            buffer[0] = Character.forDigit((src[i ushr 4] and 0x0F).toInt(), 16)
            buffer[1] = Character.forDigit((src[i] and 0x0F).toInt(), 16)
            stringBuilder.append(buffer)
        }
        return stringBuilder.toString().toUpperCase()
    }


    fun toByte(c: Char): Byte {
        return "0123456789ABCDEF".indexOf(c).toByte()
    }


    //字符序列转换为16进制字符串,大写
    //字符序列转换为16进制字符串,大写
    /**
     * 例子 {0x31,0x32,0xab,0xcd}  >  “31 32 AB CD”
     * @param
     * @return
     */
    fun bytesToHexString_upcase(src: ByteArray?): String? {
        val stringBuilder = StringBuilder("")
        if (src == null || src.size <= 0) {
            return ""
        }
        val buffer = CharArray(2)
        for (i in src.indices) {
            buffer[0] = Character.forDigit((src[i ushr 4] and 0x0F).toInt(), 16)
            buffer[1] = Character.forDigit((src[i] and 0x0F).toInt(), 16)
            stringBuilder.append(buffer)
            stringBuilder.append(" ")
        }
        return stringBuilder.toString().toUpperCase()
    }

    fun Byte2HexString_upcase(a: Int): String? {
        var result = String.format("%02X", a)
        if (result.length == 8) {
            result = result.substring(6, 8)
        }
        return result
    }


    fun Int2HexString_upcase(a: Int): String? {
        val result = StringBuffer()
        result.append(Integer.toHexString(a))
        if (result.length % 2 == 1) {
            result.insert(0, '0')
        }
        return result.toString().toUpperCase()
    }


    fun convertHexToString(hex: String): String? {
        val sb = StringBuilder()
        val temp = StringBuilder()

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        var i = 0
        while (i < hex.length - 1) {


            //grab the hex in pairs
            val output = hex.substring(i, i + 2)
            //convert hex to decimal
            val decimal = output.toInt(16)
            //convert the decimal to character
            sb.append(decimal.toChar())
            temp.append(decimal)
            i += 2
        }
        Timber.d("Decimal : $temp")
        return sb.toString()
    }

    fun convertStringToHex(str: String): String {
        val chars = str.toCharArray()
        val hex = StringBuffer()
        for (i in chars.indices) {
            hex.append(Integer.toHexString(chars[i].toInt()))
        }
        return hex.toString()
    }

    fun getAssetsJson(fileName: String?, context: Context): String? {
        val stringBuilder = StringBuilder()
        try {
            val assetManager = context.assets
            val bf = BufferedReader(InputStreamReader(
                assetManager.open(fileName!!)))
            var line: String?
            while (bf.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

}