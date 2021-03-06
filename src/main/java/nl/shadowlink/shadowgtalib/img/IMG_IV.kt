/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.shadowlink.shadowgtalib.img

import java.io.File
import java.util.ArrayList
import java.util.logging.Level
import java.util.logging.Logger
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.swing.JOptionPane
import nl.shadowlink.file_io.ByteReader
import nl.shadowlink.file_io.ReadFunctions
import nl.shadowlink.file_io.WriteFunctions
import nl.shadowlink.shadowgtalib.utils.Utils

/**
 * @author Shadow-Link
 */
class IMG_IV {
    private val ident = ByteArray(4)

    fun loadImg(image: IMG) {
        // Message.displayMsgHigh("Started IV");

        val rf = ReadFunctions()
        rf.openFile(image.fileName)

        ident[0] = rf.readByte()
        ident[1] = rf.readByte()
        ident[2] = rf.readByte()
        ident[3] = rf.readByte()
        if (ident[0].toInt() == 82 && ident[1].toInt() == 42 && ident[2].toInt() == 78 && ident[3].toInt() == -87) {
            // Message.displayMsgHigh("Unencryped IMG");
            readUnEncryptedImg(rf, image)
        } else {
            image.encrypted = true
            // Message.displayMsgHigh("Encrypted IMG");
            readEncryptedImg(rf, image)
        }
        rf.closeFile()
    }

    fun saveImg(img: IMG) {
        println("Saving IV IMG")
        val wf = WriteFunctions()
        wf.openFile(img.fileName!! + ".temp")
        val rf = ReadFunctions()
        rf.openFile(img.fileName)
        wf.writeByte(82)// write R*N start bytes
        wf.writeByte(42)
        wf.writeByte(78)
        wf.writeByte(-87)
        wf.writeInt(3) // write version number
        wf.writeInt(img.items!!.size) // write item count
        var tableSize = 0
        for (i in 0 until img.items!!.size) {// calculate table size
            tableSize += img.items!![i].name!!.length + 17
        }
        wf.writeInt(tableSize)
        wf.writeShort(0x0010)// table item size (0x10)
        wf.writeShort(0x00E9) // unknown yet

        var offset = 0x800
        while (tableSize + 20 > offset) {
            offset += 0x800
        }
        val padding = offset - tableSize - 20

        for (i in 0 until img.items!!.size) {
            if (img.items!![i].isResource) {
                wf.writeInt(img.items!![i].flags) // write the flags
            } else {
                wf.writeInt(img.items!![i].size) // size
            }
            wf.writeInt(img.items!![i].type) // type
            wf.writeInt(offset / 0x800) // offset
            offset += img.items!![i].size // next offset
            var pad = 0
            while (offset % 0x800 != 0) {
                offset += 1
                pad += 1
                // System.out.println("Offset: " + offset + " " + pad);
            }
            wf.writeShort((img.items!![i].size + pad) / 0x800) // blocks
            if (img.items!![i].isResource) {
                wf.writeShort(pad + 8192) // padding
            } else {
                wf.writeShort(pad)
            }
        }
        for (i in 0 until img.items!!.size) {
            wf.writeNullTerminatedString(img.items!![i].name!!)
        }
        for (i in 0 until padding) {
            wf.writeByte(0)
        }
        var cOffset = 0x800
        for (i in 0 until img.items!!.size) {
            rf.seek(img.items!![i].offset)
            val array = rf.readArray(img.items!![i].size)
            wf.writeArray(array)
            cOffset += img.items!![i].size
            while (cOffset % 0x800 != 0) {
                cOffset += 1
                wf.writeByte(0)
            }
        }
        rf.closeFile()
        wf.closeFile()
        var origFile: File? = File(img.fileName!!)
        try {
            if (origFile!!.canWrite()) {
                println("Can write")
            }
            if (origFile.canExecute()) {
                println("Can execute")
            }
            if (origFile.delete()) {
                println("Deleted " + img.fileName!!)
            } else {
                println("Not deleted")
            }
        } catch (ex: Exception) {
            println("Unable to delete " + img.fileName + " " + ex)
        }

        var newFile: File? = File(img.fileName!! + ".temp")
        if (newFile!!.renameTo(File(img.fileName!!))) {
            println("rename file")
        } else {
            println("Unable to rename file")
        }
        newFile.delete()
        origFile = null
        newFile = null
    }

    private fun increaseTypeCounter(itemName: String, img: IMG) {
        var itemName = itemName
        itemName = itemName.toLowerCase()
        if (itemName.endsWith(".cut")) {
            img.cutCount = img.cutCount + 1
        } else if (itemName.endsWith(".wad")) {
            img.wadCount = img.wadCount + 1
        } else if (itemName.endsWith(".wbd")) {
            img.wbdCount = img.wbdCount + 1
        } else if (itemName.endsWith(".wbn")) {
            img.wbnCount = img.wbnCount + 1
        } else if (itemName.endsWith(".wdr")) {
            img.wdrCount = img.wdrCount + 1
        } else if (itemName.endsWith(".wdd")) {
            img.wddCount = img.wddCount + 1
        } else if (itemName.endsWith(".wft")) {
            img.wftCount = img.wftCount + 1
        } else if (itemName.endsWith(".wpl")) {
            img.wplCount = img.wplCount + 1
        } else if (itemName.endsWith(".wtd")) {
            img.wtdCount = img.wtdCount + 1
        }
    }

    fun readUnEncryptedImg(rf: ReadFunctions, image: IMG) {
        val items = ArrayList<IMG_Item>()
        // Message.displayMsgHigh("Version 3: " + rf.readInt());
        val itemCount = rf.readInt()
        image.itemCount = itemCount
        println("Item Count: $itemCount")
        println("Table Size: " + rf.readInt())
        println("Size of table items: " + Utils.getHexString(rf.readShort()))
        println("Unknown: " + rf.readShort())

        // read table
        for (curItem in 0 until itemCount) {
            val item = IMG_Item()
            var itemSize = rf.readInt() // or flags
            val itemType = rf.readInt()
            val itemOffset = rf.readInt() * 0x800
            val usedBlocks = rf.readShort()
            val Padding = rf.readShort() and 0x7FF
            if (itemType <= 0x6E) {
                item.flags = itemSize
                itemSize = Utils.getTotalMemSize(itemSize)
            }
            println("-------------------------------")
            println("Offset: " + Utils.getHexString(itemOffset))
            println("Size: $itemSize bytes")
            println("Type: " + Utils.getHexString(itemType) + " " + itemType)
            println("UsedBlocks: $usedBlocks")
            println("Padding: $Padding")
            item.offset = itemOffset
            item.size = usedBlocks * 0x800 - Padding
            item.type = itemType
            items.add(item)
        }

        // read names
        for (curName in 0 until itemCount) {
            val name = rf.readNullTerminatedString()
            items.get(curName).name = name
            increaseTypeCounter(name, image)
            // items.get(curName).setType(Utils.getFileType(name, image));
            // Message.displayMsgHigh(name);
        }

        image.items = items
    }

    fun readEncryptedImg(test: ReadFunctions, img: IMG) {
        val items = ArrayList<IMG_Item>()

        val key = img.key

        var data = withIdent(test, key)

        var br = ByteReader(data, 0)
        val id = br.ReadUInt32()
        val version = br.ReadUInt32()
        val itemCount = br.ReadUInt32()
        val tableSize = br.ReadUInt32()

        println("ID: $id")
        println("Version: $version")
        println("Number of items: $itemCount")
        println("Size of table: $tableSize")

        var itemSize = test.readShort()
        val unknown = test.readShort()

        val namesSize = tableSize - itemCount * itemSize

        println("Item size: $itemSize")
        println("Unknown: $unknown")
        println("Names: $namesSize")

        for (i in 0 until itemCount) {
            data = decrypt16byteBlock(test, key) // decrypt all table items
            br = ByteReader(data, 0)
            val item = IMG_Item()
            itemSize = br.ReadUInt32() // or flags
            val itemType = br.ReadUInt32()
            val itemOffset = br.ReadUInt32() * 2048
            val usedBlocks = br.ReadUInt16()
            val Padding = br.ReadUInt16() and 0x7FF
            if (itemType <= 0x6E) {
                item.flags = itemSize
                itemSize = Utils.getTotalMemSize(itemSize)
            }
            println("-------------------------------")
            println("Offset: " + Utils.getHexString(itemOffset))
            println("Size: $itemSize bytes")
            println("Type: " + Utils.getHexString(itemType))
            println("UsedBlocks: $usedBlocks")
            println("Padding: $Padding")
            item.offset = itemOffset
            item.size = usedBlocks * 0x800 - Padding
            item.type = itemType
            items.add(item)
        }

        var i = 0
        val names = ByteArray(namesSize)
        while (i < namesSize) {
            data = decrypt16byteBlock(test, key) // decrypt all table names
            for (j in 0..15) {
                names[i + j] = data[j]
            }
            i += 16
            if (i + 16 > namesSize) {
                val lastName = test.readNullTerminatedString()
                val lastBytes = lastName.toByteArray()
                for (j in lastBytes.indices) {
                    names[i + j] = lastBytes[j]
                }
                i += 16
            }
        }

        br = ByteReader(names, 0)

        i = 0
        while (i < itemCount) {
            val name = br.ReadNullTerminatedString()
            items.get(i).name = name
            increaseTypeCounter(name, img)
            println("Name$i: $name")
            br.ReadByte()
            i++
        }

        // rf.closeFile();
        test.closeFile()

        img.items = items
    }

    fun withIdent(test: ReadFunctions, key: ByteArray): ByteArray {
        var data = ByteArray(16)

        data[0] = ident[0]
        data[1] = ident[1]
        data[2] = ident[2]
        data[3] = ident[3]

        for (j in 4..15) {
            data[j] = test.readByte()
        }
        for (j in 1..16) { // 16 (pointless) repetitions
            try {
                data = decryptAES(key, data)
            } catch (ex: Exception) {
                Logger.getLogger(IMG_IV::class.java.name).log(Level.SEVERE, null, ex)
            }

        }
        return data
    }

    fun decrypt16byteBlock(test: ReadFunctions, key: ByteArray): ByteArray {
        var data = ByteArray(16)
        for (j in 0..15) {
            data[j] = test.readByte()
        }
        for (j in 1..16) { // 16 (pointless) repetitions
            try {
                data = decryptAES(key, data)
            } catch (ex: Exception) {
                Logger.getLogger(IMG_IV::class.java.name).log(Level.SEVERE, null, ex)
            }

        }
        return data
    }

    @Throws(Exception::class)
    fun encryptAES(key: ByteArray, msg: ByteArray): ByteArray {

        val skeySpec = SecretKeySpec(key, "Rijndael")

        // Instantiate the cipher
        val cipher = Cipher.getInstance("Rijndael/ECB/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec)

        return cipher.doFinal(msg)
    }

    @Throws(Exception::class)
    fun decryptAES(key: ByteArray, msg: ByteArray): ByteArray {

        val skeySpec = SecretKeySpec(key, "Rijndael")

        // Instantiate the cipher
        val cipher = Cipher.getInstance("Rijndael/ECB/NoPadding")
        try {
            cipher.init(Cipher.DECRYPT_MODE, skeySpec)
        } catch (ex: Exception) {
            JOptionPane.showMessageDialog(null,
                    "You didn't install the JCE unlimited strength files. Follow the usage instructions in the readme.\nThis program will now exit.")
            Logger.getLogger("IMG").log(Level.SEVERE, "Unable to use JCE: " + ex.toString())
            System.exit(0)
        }

        return cipher.doFinal(msg)
    }
}
