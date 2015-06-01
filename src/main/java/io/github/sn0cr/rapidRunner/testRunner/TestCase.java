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

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

/**
 * @author Christian Wahl
 *
 */
public class TestCase {
  private final Path workingDirectory;
  private final Pair<Path, Path> testCasePair;
  private final String           name;
  private final String     input;
  private final String           expectedOutput;
  private final String[]         arguments;

  /**
   * @throws IOException
   *
   */
  public TestCase(String name, Pair<Path, Path> testCasePair,
      String[] arguments, Path workingDirectory)
          throws IOException {
    this.testCasePair = testCasePair;
    this.input = FileUtils.readFileToString(testCasePair.getA().toFile());
    this.expectedOutput = FileUtils.readFileToString(testCasePair.getB()
        .toFile());
    this.arguments = arguments;
    this.name = name;
    this.workingDirectory = workingDirectory;
  }

  /**
   * @return
   */
  public String[] getArguments() {
    return this.arguments;
  }

  /**
   * @return
   */
  public String getExpectedOutput() {
    return this.expectedOutput;
  }

  /**
   * @return
   */
  public String getInput() {
    return this.input;
  }

  /**
   * @return
   */
  public String getName() {
    return this.name;
  }

  /**
   * @return
   */
  public Pair<Path, Path> getTestCasePair() {
    return this.testCasePair;
  }

  /**
   * @return
   */
  public Path getWorkingDirectory() {
    return this.workingDirectory;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.name;
  }
}
