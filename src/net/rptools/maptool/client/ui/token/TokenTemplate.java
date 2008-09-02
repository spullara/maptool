/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package net.rptools.maptool.client.ui.token;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Token;

/**
 * Paint a template around a token.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public interface TokenTemplate {

  /**
   * Paint the template for the passed token.
   * 
   * @param g Graphics used to paint. It is already translated so that 0,0 is
   * the upper left corner of the token. It is also clipped so that the template can not
   * draw out renderer's bounding box.
   * @param token The token being painted.
   * @param bounds The bounds of the actual token. This will be different than the clip
   * since the clip also has to take into account the edge of the window. If you draw 
   * based on the clip it will be off for partial token painting.
   * @param renderer The renderer that is painting this template.
   */
  public abstract void paintTemplate(Graphics2D g, Token token, Rectangle bounds, ZoneRenderer renderer);
}
