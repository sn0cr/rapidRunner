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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Christian Wahl
 *
 */
public class CommandRunner implements Runnable {

  public static void main(String[] args) {
    final CommandRunner commandRunner = new CommandRunner(new String[]{"/bin/bash", "-c",
    ">&2 ls -la && ls -la && exit 4" }, "");
    commandRunner.run();
    System.out.println("ERR: "
        + String.join("\nERR: ", commandRunner.getStdErr()));
    System.out.println(String.join("\n", commandRunner.getStdOut()));
  }
  private StreamReader stderrReader;
  private StreamReader stdoutReader;
  private final ProcessBuilder builder;
  private final String                     input;
  private long                             timeTaken;
  private int                              exitCode;
  private static final Consumer<Exception> ERROR_CONSUMER = (e) -> System.err
      .println(e
          .getLocalizedMessage());
  /**
   *
   */
  public CommandRunner(String arguments[], String input) {
    //    final ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c",
    //        ">&2 ls -la && ls -la && exit 4");
    this.builder = new ProcessBuilder(arguments);
    this.builder.redirectErrorStream(false);
    this.input = input;
  }

  public CommandRunner(String arguments[], String input, Path workingDirectory) {
    this(arguments, input);
    this.builder.directory(workingDirectory.toFile());
  }

  /**
   * @param proc
   */
  public void createOutputReader(Process proc) {
    final BufferedReader stderr = new BufferedReader(new InputStreamReader(
        proc.getErrorStream()));
    final BufferedReader stdout = new BufferedReader(new InputStreamReader(
        proc.getInputStream()));
    this.stderrReader = new StreamReader(stderr, CommandRunner.ERROR_CONSUMER);
    this.stdoutReader = new StreamReader(stdout, CommandRunner.ERROR_CONSUMER);
  }

  public int getExitCode() {
    return this.exitCode;
  }

  public List<String> getStdErr() {
    return this.stderrReader.getBuffer();
  }

  public List<String> getStdOut(){
    return this.stdoutReader.getBuffer();
  }

  /**
   * @return the timeTaken in ns
   */
  public long getTimeTaken() {
    return this.timeTaken;
  }
  /**
   * @param builder
   */
  @Override
  public void run() {
    Process proc;
    try {
      final long timeBefore = System.nanoTime();
      proc = this.builder.start();
      this.createOutputReader(proc);
      final BufferedWriter stdin = new BufferedWriter(new OutputStreamWriter(
          proc.getOutputStream()));
      stdin.write(this.input);
      stdin.flush();
      new Thread(this.stderrReader).start();
      new Thread(this.stdoutReader).start();
      proc.waitFor();
      this.timeTaken = System.nanoTime() - timeBefore;
      this.exitCode = proc.exitValue();
    } catch (final IOException | InterruptedException e1) {
      CommandRunner.ERROR_CONSUMER.accept(e1);
    }
  }
}
