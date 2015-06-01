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

import io.github.sn0cr.rapidRunner.testRunner.StdInOutMatcher;
import io.github.sn0cr.rapidRunner.testRunner.TestCase;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

/**
 * @author Christian Wahl
 *
 */
public class TestPaneController extends GridPane {
  @FXML
  private Text            inputLabel;
  @FXML
  private Text            expectedOutputLabel;
  @FXML
  private Text            producedOutputLabel;
  @FXML
  private Text            statusLabel;
  @FXML
  private Label           testCaseName;
  @FXML
  private Text            stderr;

  @FXML
  private GridPane        mainGridPane;
  private TestCase        testCase = null;
  private StdInOutMatcher runner   = null;

  /**
   *
   */
  public TestPaneController() {
    final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(
        "TestPane.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);

    try {
      fxmlLoader.load();
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  @FXML
  private void copyExpectedOutput(){
    this.copyToPasteboard(this.testCase.getExpectedOutput());
  }

  @FXML
  private void copyInput() {
    this.copyToPasteboard(this.testCase.getInput());
  }

  @FXML
  private void copyProducedOutput() {
    this.copyToPasteboard(String.join("\n", this.runner.getStdOut()));
  }

  @FXML
  private void copyStderr() {
    this.copyToPasteboard(String.join("\n", this.runner.getStdErr()));
  }

  /**
   *
   */
  public void copyToPasteboard(String text) {
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();
    content.putString(text);
    clipboard.setContent(content);
  }
  /**
   *
   */

  public TestPaneController init(TestCase testCaseToRun) {
    this.testCase = testCaseToRun;
    this.runner = new StdInOutMatcher(this.testCase);
    this.setUp();
    return this;
  }

  @FXML
  private void rerunTestCase() {
    this.reset();
    new Thread(() -> {
      this.runRunner();
      Platform.runLater(() -> {
        this.updateText();
      });
    }).start();
  }
  /**
   *
   */
  private void reset() {
    this.setBackgroundStyle("#eef0f0");
    this.producedOutputLabel.setText("It did not run until now.");
    this.statusLabel.setText("Waiting to run...");
    this.stderr.setText("Waiting to run...");
  }

  public void runRunner() {
    this.runner.run();
  }

  private void setBackgroundStyle(String color){
    final String cssString = String.format("-fx-background-color: %s;", color);
    this.testCaseName.setStyle(cssString);
    this.mainGridPane.setStyle(cssString);
  }

  /**
   *
   */
  private void setUp() {
    this.inputLabel.setText(this.testCase.getInput());
    this.testCaseName.setText(this.testCase.getName());
    this.expectedOutputLabel.setText(this.testCase.getExpectedOutput());
    this.reset();
  }

  /**
   *
   */
  public void updateText() {
    this.producedOutputLabel
    .setText(String.join("\n", this.runner.getStdOut()));
    this.stderr.setText(String.join("\n", this.runner.getStdErr()));
    final boolean sucessfull = this.runner.hasMatched();
    String status = "Not successful";
    if (sucessfull) {
      status = "OK";
      // light green background
      this.setBackgroundStyle("#d7ffca");
    } else {
      // light red background
      this.setBackgroundStyle("#d2657d");
    }
    this.statusLabel.setText(
        String.format("%s in %,3dms with exit code %,3d", status,
            this.runner.getTimeTaken() / 1_000_000,
            this.runner.getExitCode()));
  }

}
