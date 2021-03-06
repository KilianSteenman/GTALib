/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.shadowlink.shadowgtalib.ide;

import nl.shadowlink.shadowgtalib.model.model.Vector3D;
import nl.shadowlink.shadowgtalib.model.model.Vector4D;
import nl.shadowlink.shadowgtalib.utils.Constants;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kilian
 */
public class Item_OBJS extends IDE_Item {
	public int id; // III, VC, SA
	public String modelName; // III, VC, SA, IV
	public String textureName; // III, VC, SA, IV
	public int objectCount; // III, VC, SA
	public float[] drawDistance; // III, VC, SA, IV
	public int flag1; // III, VC, SA, IV
	public int flag2; // IV
	public Vector3D boundsMin = new Vector3D(0.0f, 0.0f, 0.0f); // IV
	public Vector3D boundsMax = new Vector3D(0.0f, 0.0f, 0.0f);; // IV
	public Vector4D boundsSphere = new Vector4D(0.0f, 0.0f, 0.0f, 0.0f);; // IV
	public String WDD; // IV

	private int gameType;

	public Item_OBJS(int gameType) {
		this.gameType = gameType;
	}

	@Override
	public void read(String line) {
		line = line.replace(" ", "");
		String split[] = line.split(",");
		switch (gameType) {
			case Constants.gIV:
				modelName = split[0];
				textureName = split[1];
				drawDistance = new float[1];
				drawDistance[0] = Float.valueOf(split[2]);
				flag1 = Integer.valueOf(split[3]);
				flag2 = Integer.valueOf(split[4]);
				boundsMin = new Vector3D(Float.valueOf(split[5]), Float.valueOf(split[6]), Float.valueOf(split[7]));
				boundsMax = new Vector3D(Float.valueOf(split[8]), Float.valueOf(split[9]), Float.valueOf(split[10]));
				boundsSphere = new Vector4D(Float.valueOf(split[11]), Float.valueOf(split[12]), Float.valueOf(split[13]), Float.valueOf(split[14]));
				WDD = split[15];
				break;
			case Constants.gSA:
				id = Integer.valueOf(split[0]);
				modelName = split[1];
				textureName = split[2];
				drawDistance = new float[1];
				drawDistance[0] = Float.valueOf(split[3]);
				flag1 = Integer.valueOf(split[4]);
				break;
			default: // III, VC Share the same format
				id = Integer.valueOf(split[0]);
				modelName = split[1];
				textureName = split[2];
				objectCount = Integer.valueOf(split[3]);
				drawDistance = new float[objectCount];
				for (int i = 0; i < objectCount; i++) {
					drawDistance[i] = Float.valueOf(split[4 + i]);
				}
				flag1 = Integer.valueOf(split[4 + objectCount]);
		}
		this.display();
	}

	public void display() {
		// if (gameType != Constants.gIV) {
		// //Message.displayMsgHigh("ID: " + id);
		// }
		// //Message.displayMsgHigh("ModelName: " + modelName);
		// //Message.displayMsgHigh("TextureName: " + textureName);
		// if (gameType == Constants.gIII || gameType == Constants.gVC) {
		// //Message.displayMsgHigh("ObjectCount: " + objectCount);
		// for (int i = 0; i < objectCount; i++) {
		// //Message.displayMsgHigh("DrawDistance" + i + ": " + drawDistance[i]);
		// }
		// }
		// if (gameType == Constants.gIV) {
		// //Message.displayMsgHigh("Flag: " + flag1);
		// //Message.displayMsgHigh("Flag2: " + flag2);
		// //Message.displayMsgHigh("BoundsMAX: " + boundsMax.x + ", " + boundsMax.y + ", " + boundsMax.z);
		// //Message.displayMsgHigh("BoundsMIN: " + boundsMin.x + ", " + boundsMin.y + ", " + boundsMin.z);
		// //Message.displayMsgHigh("BoundsSphere: " + boundsSphere.x + ", " + boundsSphere.y + ", " + boundsSphere.z +
		// ", " + boundsSphere.w);
		// //Message.displayMsgHigh("WDD: " + WDD);
		// }
	}

	public void save(BufferedWriter output) {
		try {
			String line = modelName;
			line += ", " + textureName;
			line += ", " + drawDistance[0];
			line += ", " + flag1;
			line += ", " + flag2;
			line += ", " + boundsMin.x;
			line += ", " + boundsMin.y;
			line += ", " + boundsMin.z;
			line += ", " + boundsMax.x;
			line += ", " + boundsMax.y;
			line += ", " + boundsMax.z;
			line += ", " + boundsSphere.x;
			line += ", " + boundsSphere.y;
			line += ", " + boundsSphere.z;
			line += ", " + boundsSphere.w;
			line += ", " + WDD;
			output.write(line);
			output.newLine();
			System.out.println("Line: " + line);
		} catch (IOException ex) {
			Logger.getLogger(Item_OBJS.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
