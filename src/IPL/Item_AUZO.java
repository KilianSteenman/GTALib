/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IPL;

import ch.ubique.inieditor.IniEditor;
import file_io.Message;
import file_io.ReadFunctions;

/**
 *
 * @author Kilian
 */
public class Item_AUZO extends IPL_Item{
    private int gameType;

    public Item_AUZO(int gameType) {
        this.gameType = gameType;
    }

    @Override
    public void read(String line) {
        Message.displayMsgHigh(line);
    }

    @Override
    public void read(ReadFunctions rf) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void read(ReadFunctions rf, IniEditor ini) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}