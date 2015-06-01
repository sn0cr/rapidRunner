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
package io.github.sn0cr.rapidRunner.testRunner;

import io.github.sn0cr.rapidRunner.util.Pair;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import external.NaturalOrderComparator;

/**
 * @author Christian Wahl
 *
 */
public class TestCaseFinder {
  /**
   *
   */

  private final HashMap<String, Pair<Path, Path>> testCases = new HashMap<>();

  public TestCaseFinder(Path parentFolder, String filePattern,
      String inExtension, String outExtension) {
    final String[] files = parentFolder.toFile().list(
        new WildcardFileFilter(filePattern));
    Arrays.sort(files, new NaturalOrderComparator());
    for (final String filename : files) {
      final String extension = FilenameUtils.getExtension(filename);
      final String name = FilenameUtils.getBaseName(filename);
      final Path toFile = Paths.get(parentFolder.toString(), filename);
      Pair<Path, Path> testCasePair;
      if (this.testCases.containsKey(name)) {
        testCasePair = this.testCases.get(name);
      } else {
        testCasePair = new Pair<Path, Path>();
      }
      if (extension.equals(inExtension)) {
        testCasePair.setA(toFile);
      } else if (extension.equals(outExtension)) {
        testCasePair.setB(toFile);
      }
      if (testCasePair.notEmpty()) {
        this.testCases.put(name, testCasePair);
      }
    }
  }

  /**
   * @return the testCases
   */
  public final HashMap<String, Pair<Path, Path>> getTestCases() {
    return this.testCases;
  }
}
