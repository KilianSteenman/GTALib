/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.shadowlink.shadowgtalib.img

/**
 * @author Shadow-Link
 */
class IMG_Item {
    var type: Int = 0
        set(type) {
            if (type < 1000) {
                isResource = true
            }
            field = type
        }
    var offset: Int = 0
    var size: Int = 0
    var name: String? = null

    var isResource = false
    var flags = 0
}
