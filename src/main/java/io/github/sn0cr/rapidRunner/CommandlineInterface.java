/**
 * This file is part of rapidRunner
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * @author Christian Wahl
 *
 */
public class CommandlineInterface {
  public static void main(String[] args) {
    // create Options object
    final Options options = new Options();
    // add t option
    // options.addOption("t", false, "display current time");
    final CommandLineParser parser = new PosixParser();
    try {
      final CommandLine cmd = parser.parse(options, args);
      if (cmd.hasOption("t")) {
        System.out.println("has option " + cmd.getOptionValue("t"));
      } else {
        System.out.println("hasn't option");
      }
    } catch (final ParseException e) {
      e.printStackTrace();
    }
  }
}
