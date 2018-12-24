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
class IMG_SA {

    fun loadImg(image: IMG) {
        val items = ArrayList<IMG_Item>()

        val rf = ReadFunctions()
        rf.openFile(image.fileName)

        var itemCount = 0

        // Message.displayMsgHigh("Ver2: " + rf.readChar() + rf.readChar() + rf.readChar() + rf.readChar());
        itemCount = rf.readInt()
        image.itemCount = itemCount
        // Message.displayMsgHigh("Item Count: " + itemCount);

        for (curItem in 0 until itemCount) {
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
        }

        image.items = items
    }

    /* private void updateCounter(int itemType){ switch(itemType){ case 0: //modelCount++; break; } } */

}
