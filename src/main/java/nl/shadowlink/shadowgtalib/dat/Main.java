/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.shadowlink.shadowgtalib.dat;

import nl.shadowlink.shadowgtalib.dat.GTA_DAT;
import nl.shadowlink.shadowgtalib.utils.Constants.GameType;

/**
 *
 * @author Kilian
 */
public class Main {

    public Main(){
        //GTA_DAT gta_dat = new GTA_DAT("D:/Rockstar Games/GTA III/data/gta3.dat", 0); //III TEST
        //GTA_DAT gta_dat = new GTA_DAT("D:/Rockstar Games/GTA Vice City/data/gta_vc.dat", 1); //VC TEST
        GTA_DAT gta_dat = new GTA_DAT("D:/Rockstar Games/GTA San Andreas - Modding/data/gta.dat", GameType.GTA_SA); //SA TEST
        //GTA_DAT gta_dat = new GTA_DAT("D:/Rockstar Games/Grand Theft Auto IV - Modding/common/data/gta.dat", 3); //IV TEST
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }

}
