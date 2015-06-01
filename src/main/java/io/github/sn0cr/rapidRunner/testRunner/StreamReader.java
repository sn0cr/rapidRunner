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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Christian Wahl
 *
 */
public class StreamReader implements Runnable {

  private final BufferedReader stream;
  private final List<String>   buffer;
  private Consumer<Exception>  exceptionHandler = (e) -> System.err
      .println(e);

  /**
   *
   */
  public StreamReader(final BufferedReader stream) {
    this.stream = stream;
    this.buffer = new LinkedList<>();
  }

  /**
   * @param stderr
   * @param object
   */
  public StreamReader(BufferedReader stderr,
      Consumer<Exception> exceptionHandler) {
    this(stderr);
    this.exceptionHandler = exceptionHandler;
  }

  /**
   * @return the buffer
   */
  public final List<String> getBuffer() {
    return this.buffer;
  }

  @Override
  public void run() {
    String line = "";
    try {
      line = this.stream.readLine();
      while (line != null) {
        this.buffer.add(line);
        line = this.stream.readLine();
      }
    } catch (final IOException e) {
      this.exceptionHandler.accept(e);
      // e.printStackTrace();
    }
  }
}
