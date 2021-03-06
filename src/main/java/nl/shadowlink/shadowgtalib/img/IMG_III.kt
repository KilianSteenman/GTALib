/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.shadowlink.shadowgtalib.img

import java.util.ArrayList
import nl.shadowlink.file_io.ReadFunctions
import nl.shadowlink.shadowgtalib.utils.Utils

/**
 * @author Shadow-Link
 */
class IMG_III {

    fun loadImg(image: IMG) {
        val items = ArrayList<IMG_Item>()

        val rf = ReadFunctions()
        rf.openFile(image.fileName?.replace(".img", ".dir"))

        var itemCount = 0

        while (rf.moreToRead() != 0) {
            val item = IMG_Item()
            val itemOffset = rf.readInt() * 2048
            val itemSize = rf.readInt() * 2048
            val itemName = rf.readNullTerminatedString(24)
            val itemType = Utils.getFileType(itemName, image)
            // Message.displayMsgHigh("Offset: " + Utils.getHexString(itemOffset));
            // Message.displayMsgHigh("Size: " + itemSize + " bytes");
            // Message.displayMsgHigh("Name: " + itemName);
            // Message.displayMsgHigh("Type: " + itemType);
            item.offset = itemOffset
            item.name = itemName
            item.size = itemSize
            item.type = itemType
            items.add(item)
            itemCount++
        }

        image.items = items
        // Message.displayMsgHigh("Final Item Count: " + itemCount);
    }
}
