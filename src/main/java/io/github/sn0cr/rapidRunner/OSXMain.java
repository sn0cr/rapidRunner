/**
 * This file is part of rapidRunner <https://github.com/sn0cr/rapidRunner>
 * Copyright (C) 2015 Christian Wahl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.sn0cr.rapidRunner;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

import com.apple.eawt.Application;

import external.OSValidator;

/**
 * @author Christian Wahl
 *
 */
public class OSXMain {

  /**
   * @param args
   */
  public static void main(String[] args) {
    if (OSValidator.isMac()) {
      // set dock icon
      try {
        final URL iconURL = StartScreenController.class.getResource("icon.png");
        final Image image = new ImageIcon(iconURL).getImage();
        Application.getApplication().setDockIconImage(image);
      } catch (final Exception e) {
        // Won't work on Windows or Linux.
      }
    }
    StartScreenController.main(args);
  }
}
