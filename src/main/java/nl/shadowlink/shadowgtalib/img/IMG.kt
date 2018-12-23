/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.shadowlink.shadowgtalib.img

import nl.shadowlink.file_io.ReadFunctions
import nl.shadowlink.file_io.WriteFunctions
import nl.shadowlink.shadowgtalib.model.model.Model
import nl.shadowlink.shadowgtalib.texturedic.TextureDic
import nl.shadowlink.shadowgtalib.utils.Constants
import nl.shadowlink.shadowgtalib.utils.Constants.GameType
import nl.shadowlink.shadowgtalib.utils.Utils

import java.io.File
import java.util.ArrayList

/**
 * @author Shadow-Link
 */
class IMG(var fileName: String?, var gameType: GameType?, key: ByteArray, autoLoad: Boolean, containsProps: Boolean) {
    var changed = false // True when the file needs to be saved

    var encrypted = false
    var containsProps = false

    var key = ByteArray(32)

    var itemCount = 0
    var cutCount = 0
    var wtdCount = 0
    var wbdCount = 0
    var wbnCount = 0
    var wplCount = 0
    var wddCount = 0
    var wdrCount = 0
    var wadCount = 0
    var wftCount = 0
    var unknownCount = 0

    var items: ArrayList<IMG_Item>? = ArrayList() // All items

    init {
        this.key = key
        this.containsProps = containsProps
        if (autoLoad)
            loadImg()
    }// Message.displayMsgSuper("Loading IMG: " + fileName);

    private fun loadImg(): Boolean {
        when (gameType) {
            Constants.GameType.GTA_III -> IMG_III().loadImg(this)
            Constants.GameType.GTA_VC -> IMG_VC().loadImg(this)
            Constants.GameType.GTA_SA -> IMG_SA().loadImg(this)
            Constants.GameType.GTA_IV -> IMG_IV().loadImg(this)
        }
        return if (items == null)
            false
        else
            true
    }

    fun getItemIndex(name: String): Int {
        var i = 0
        while (!items!![i].name!!.equals(name, ignoreCase = true)) {
            if (i < items!!.size - 1) {
                i++
            } else {
                break
            }
        }
        return i
    }

    fun findItem(name: String): IMG_Item? {
        var ret: IMG_Item? = null
        var i = 0
        while (!items!![i].name!!.equals(name, ignoreCase = true)) {
            if (i < items!!.size - 1) {
                i++
            } else {
                break
            }
        }
        if (items!![i].name!!.equals(name, ignoreCase = true)) {
            // Message.displayMsgSuper("<IMG " + fileName + ">Found file " + name + " at " + i + " offset " +
            // Items.get(i).getOffset());
            ret = items!![i]
        } else {
            // Message.displayMsgSuper("<IMG " + fileName + ">Unable to find file " + name);
        }
        return ret
    }

    fun addItem(mdl: Model, name: String) {
        var name = name
        var wf: WriteFunctions? = WriteFunctions()
        if (wf!!.openFile(fileName)) {
            name = name.toLowerCase()
            name = name.replace(".dff".toRegex(), ".wdr")
            val tempItem = IMG_Item()
            tempItem.name = name
            tempItem.type = Constants.rtWDR
            tempItem.offset = wf.fileSize
            wf.gotoEnd()
            mdl.convertToWDR(wf)
            tempItem.size = mdl.size
            tempItem.flags = mdl.flags

            items!!.add(tempItem)
            if (wf.closeFile()) {
                println("Closed file")
            } else {
                println("Unable to close the file")
            }
            wf = null
            changed = true
        } else {
            // JOptionPane.show//MessageDialog(this, "Unable to open " + fileName
            // + " for writing!");
        }
    }

    fun addItem(txd: TextureDic, name: String) {
        var name = name
        var wf: WriteFunctions? = WriteFunctions()
        if (wf!!.openFile(fileName)) {
            name = name.toLowerCase()
            name = name.replace(".txd".toRegex(), ".wtd")
            val tempItem = IMG_Item()
            tempItem.name = name
            tempItem.type = Constants.rtWTD
            tempItem.offset = wf.fileSize
            wf.gotoEnd()
            txd.convertToWTD(wf)
            tempItem.size = txd.size
            tempItem.flags = txd.flags

            items!!.add(tempItem)
            if (wf.closeFile()) {
                println("Closed file")
            } else {
                println("Unable to close the file")
            }
            wf = null
            changed = true
        } else {
            // JOptionPane.show//MessageDialog(this, "Unable to open " + fileName
            // + " for writing!");
        }
    }

    fun addItem(file: File) {
        if (file.isFile && file.canRead()) {
            var rf: ReadFunctions? = ReadFunctions()
            println("File: " + file.absolutePath)
            if (rf!!.openFile(file.absolutePath)) {
                var wf: WriteFunctions? = WriteFunctions()
                if (wf!!.openFile(fileName)) {
                    println("File size: " + file.length())
                    var newFile: ByteArray? = rf.readArray(file.length().toInt())
                    val tempItem = IMG_Item()
                    tempItem.name = file.name
                    tempItem.type = Utils.getResourceType(file.name)
                    tempItem.offset = wf.fileSize
                    tempItem.size = file.length().toInt()
                    if (tempItem.isResource) {
                        rf.seek(0x8)
                        tempItem.flags = rf.readInt()
                    }
                    items!!.add(tempItem)
                    wf.gotoEnd()
                    wf.writeArray(newFile)
                    if (wf.closeFile()) {
                        println("Closed file")
                    } else {
                        println("Unable to close the file")
                    }
                    newFile = null
                    wf = null
                    changed = true
                } else {
                    // JOptionPane.show//MessageDialog(this, "Unable to open " +
                    // fileName + " for writing!");
                }
                rf.closeFile()
                rf = null
            } else {
                // JOptionPane.show//MessageDialog(this, "Unable to open " +
                // file.getName() + " for reading!");
            }
        }
    }

    fun save() {
        when (gameType) {
            /* case Finals.gIII: new IMG_III().saveImg(this); break; case Finals.gVC: new IMG_VC().saveImg(this); break;
		 * case Finals.gSA: new IMG_SA().saveImg(this); break; */
            Constants.GameType.GTA_IV -> IMG_IV().saveImg(this)
        }
    }

}
