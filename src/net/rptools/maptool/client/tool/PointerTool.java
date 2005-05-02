/*
 */
package net.rptools.maptool.client.tool;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.util.ImageUtil;

/**
 */
public class PointerTool extends DefaultTool {

	public PointerTool () {
        try {
            setIcon(new ImageIcon(ImageUtil.getImage("net/rptools/maptool/client/image/Tool_Draw_Select.gif")));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
