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

package io.github.sn0cr.rapidRunner.util;

import java.util.prefs.Preferences;


/**
 * @author Christian Wahl
 *
 */
public class SettingsSaver {
  private static final class Instance {
    private final Preferences prefs = Preferences.userNodeForPackage(this
        .getClass());
    private String getProperty(String key, String defaultValue) {
      return this.prefs.get(key, defaultValue);
    }

    private void setProperty(String key, String value) {
      this.prefs.put(key, value);
    }
  }
  public static final class KEYS{
    public static final String COMMANDLINE    = "COMMANDLINE";
    public static final String INPUT_EXTENSION = "INPUT_EXTENSION";
    public static final String OUTPUT_EXTENSION = "OUTPUT_EXTENSION";
    public static final String TESTDIRECTORY_PATH = "TESTDIRECTORY_PATH";
    public static final String WORKINGDIRECTORY_PATH = "WORKINGDIRECTORY_PATH";
    public static String TEST_CASE_NAME = "TEST_CASE_NAME";
  }

  public static String getProperty(String key, String value) {
    return SettingsSaver.instance.getProperty(key, value);
  }

  public static void setProperty(String key, String value) {
    SettingsSaver.instance.setProperty(key, value);
  }

  public static final Instance instance = new Instance();
}
