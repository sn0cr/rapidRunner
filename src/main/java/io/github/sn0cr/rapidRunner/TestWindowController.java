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
import io.github.sn0cr.rapidRunner.testRunner.TestCase;

import java.nio.file.Path;
import java.util.LinkedList;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Christian Wahl
 *
 */
public class TestWindowController extends VBox implements Runnable {

  /**
   *
   */
  public static void showNewWindow(LinkedList<TestCase> data) {
    final TestWindowController pane = new TestWindowController();
    pane.initData(data);
    pane.setStyle("-fx-background-color: #FFFFFF;");
    final ScrollPane sp = new ScrollPane(pane);
    pane.setFillWidth(true);
    sp.setStyle("-fx-background-color: #FFFFFF;");
    final Stage stage = new Stage(StageStyle.DECORATED);
    stage.setScene(new Scene(sp));
    stage.show();
    stage.setMinWidth(360);
    stage.setMinHeight(440);
    pane.run();

  }

  private final LinkedList<TestPaneController> testPanes;

  private final Label                          headLabel;

  /**
   *
   */
  public TestWindowController() {
    this.testPanes = new LinkedList<TestPaneController>();
    this.headLabel = new Label();
    this.headLabel.setText("Running your tests in /i/C/UR/OMA");
    this.headLabel.setAlignment(Pos.TOP_CENTER);
    this.headLabel
    .setStyle("-fx-background-color: #FFFFFF;-fx-font-size: 25px;-fx-font-weight:bold;-fx-padding:10px");
    this.headLabel.setMaxWidth(Double.MAX_VALUE);
    this.getChildren().add(this.headLabel);
  }

  public void initData(LinkedList<TestCase> testCasesList) {
    final Path expectedWD = testCasesList.getFirst().getWorkingDirectory();
    this.headLabel.setText("Running your tests in "
        + expectedWD.toAbsolutePath().toString());
    // TODO find source of segfault (if to much testcases were loaded)
    //    for (final TestCase testCase : testCasesList) {
    for (int i = 0; i < testCasesList.size(); i++) {
      final TestCase testCase = testCasesList.get(i);
      final TestPaneController testPane = new TestPaneController();
      GridPane.setMargin(testPane, new Insets(10));
      testPane.init(testCase);
      this.testPanes.add(testPane);
    }
    this.getChildren().addAll(this.testPanes);
  }

  @Override
  public void run() {
    new Thread(() -> {
      for (final TestPaneController testPaneController : TestWindowController.this.testPanes) {
        testPaneController.runRunner();
        Platform.runLater(() -> {
          testPaneController.updateText();
        });
      }
    }).start();
  }
}
