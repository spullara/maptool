/*
 */
package net.rptools.maptool.client.tool;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 */
public class PointerTool extends DefaultTool {

	public PointerTool () {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/Tool_Draw_Select.gif"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
