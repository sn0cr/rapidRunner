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
package io.github.sn0cr.rapidRunner.simpleWindow;

import io.github.sn0cr.rapidRunner.TestWindowController;
import io.github.sn0cr.rapidRunner.testRunner.TestCase;
import io.github.sn0cr.rapidRunner.testRunner.TestCaseFinder;
import io.github.sn0cr.rapidRunner.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import external.NaturalOrderComparator;

/**
 * @author Christian Wahl
 *
 */
public class SimpleWindowController extends Application {
  public static void main(String[] args) {
    Application.launch(args);
  }

  private Stage stage;
  @FXML
  private Label cwdLabel;
  @FXML
  private Label testDirectoryLabel;

  @FXML
  private TextField testFilePattern;

  @FXML
  private TextField inputFileExtension;
  @FXML
  private TextField outputFileExtension;
  private Path      testDirectory;
  private Path      workingDirectory;

  @FXML
  public void changeCWD() {
    final File newTestDirectory = this
        .showDirectoryChooser("Choose the directory in which your program should be run!");
    if ((newTestDirectory != null) && newTestDirectory.isDirectory()) {
      this.cwdLabel.setText(newTestDirectory.getAbsolutePath());
      this.workingDirectory = Paths.get(newTestDirectory.getPath());
    }
  }

  @FXML
  public void changeTestDirectory() {
    final File newTestDirectory = this
        .showDirectoryChooser("Choose the directory containing your test samples!");
    if ((newTestDirectory != null) && newTestDirectory.isDirectory()) {
      this.testDirectoryLabel.setText(newTestDirectory.getAbsolutePath());
      this.testDirectory = Paths.get(newTestDirectory.getPath());
    }
  }

  @FXML
  private void runTests(){
    final Map<String, Pair<Path, Path>> testCases = new TestCaseFinder(
        this.testDirectory, this.testFilePattern.getText(),
        this.inputFileExtension.getText(), this.outputFileExtension.getText())
    .getTestCases();
    final String[] testArgs = new String[] { "java", "Program" };
    final LinkedList<TestCase> testCasesList = new LinkedList<>();
    for (final Entry<String, Pair<Path, Path>> testCasePair : testCases
        .entrySet()) {
      try {
        testCasesList.add(new TestCase(testCasePair.getKey(), testCasePair
            .getValue(), testArgs, this.workingDirectory));
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
    testCasesList.sort(new NaturalOrderComparator());
    TestWindowController.showNewWindow(testCasesList);
  }

  /**
   * @return
   *
   */
  public File showDirectoryChooser(String title) {
    final DirectoryChooser dirChooser = new DirectoryChooser();
    dirChooser.setTitle(title);
    return dirChooser.showDialog(this.stage);
  }

  /*
   * (non-Javadoc)
   * @see javafx.application.Application#start(javafx.stage.Stage)
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    final Parent parent = FXMLLoader.load(this.getClass().getResource(
        "SimpleWindow.fxml"));
    this.stage = primaryStage;
    final Scene scene = new Scene(parent);
    this.stage.setTitle("Test runner");
    this.stage.setScene(scene);
    this.stage.setMinHeight(270);
    this.stage.setMinWidth(400);
    this.stage.show();
  }

}
