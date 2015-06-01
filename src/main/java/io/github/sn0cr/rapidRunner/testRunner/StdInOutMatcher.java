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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * @author Christian Wahl
 *
 */
public class StdInOutMatcher implements Runnable {
  private final String input;
  private final String expectedOutput;
  private final CommandRunner commandRunner;
  private List<String>        stdErr;
  private List<String> stdOut;
  private long                timeTaken;
  private boolean             matched;
  private int                 exitCode;

  /**
   * @throws IOException
   *
   */
  public StdInOutMatcher(Pair<Path, Path> stdinOutFiles, Path workingDirectory,
      String[] arguments) throws IOException {
    this.stdErr = new LinkedList<String>();
    this.stdOut = new LinkedList<String>();
    this.input = FileUtils.readFileToString(stdinOutFiles.getA().toFile());
    this.expectedOutput = FileUtils
        .readFileToString(stdinOutFiles.getB().toFile());
    this.commandRunner = new CommandRunner(arguments, this.input,
        workingDirectory);
  }

  /**
   * @param testCase
   * @throws IOException
   */
  public StdInOutMatcher(TestCase testCase) {
    this.stdErr = new LinkedList<String>();
    this.stdOut = new LinkedList<String>();
    this.input = testCase.getInput();
    this.expectedOutput = testCase.getExpectedOutput();
    this.commandRunner = new CommandRunner(testCase.getArguments(), this.input,
        testCase.getWorkingDirectory());
  }

  /**
   * @return the exitCode
   */
  public int getExitCode() {
    return this.exitCode;
  }

  /**
   * @return the expectedOutput
   */
  public final String getExpectedOutput() {
    return this.expectedOutput;
  }

  /**
   * @return the input
   */
  public final String getInput() {
    return this.input;
  }

  /**
   * @return the stdErr
   */
  public final List<String> getStdErr() {
    return this.stdErr;
  }

  /**
   * @return the stdOut
   */
  public final List<String> getStdOut() {
    return this.stdOut;
  }

  /**
   * @return the timeTaken
   */
  public long getTimeTaken() {
    return this.timeTaken;
  }

  /**
   * @return the matched
   */
  public boolean hasMatched() {
    return this.matched;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    this.commandRunner.run();
    this.stdOut = this.commandRunner.getStdOut();
    this.stdErr = this.commandRunner.getStdErr();
    this.timeTaken = this.commandRunner.getTimeTaken();
    final List<String> expectedStdOut = Arrays.asList(this.expectedOutput
        .split("\\n"));
    this.matched = this.stdOut.equals(expectedStdOut);
    this.exitCode = this.commandRunner.getExitCode();
  }
}
