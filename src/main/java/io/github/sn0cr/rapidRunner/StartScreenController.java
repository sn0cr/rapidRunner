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
import io.github.sn0cr.rapidRunner.testRunner.TestCaseFinder;
import io.github.sn0cr.rapidRunner.util.Pair;
import io.github.sn0cr.rapidRunner.util.SettingsSaver;
import io.github.sn0cr.rapidRunner.util.SettingsSaver.KEYS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import external.NaturalOrderComparator;

/**
 * @author Christian Wahl
 *
 */
public class StartScreenController extends Application {
  public static void main(String[] args) {
    Application.launch(args);
  }

  public static final String SAMPLE_STRING = "%1$sXX.%2$s %1$sXX.%3$s - Then we're ready to go!";
  public static final String HEADING       = "Run your tests in %s!";
  @FXML
  private Label heading;

  @FXML
  private Button             runButton;

  @FXML
  private Button             workingDirectory;
  private Path  workingDirectoryPath;

  @FXML
  private Button             testDirectory;
  private Path  testDirectoryPath;

  @FXML
  private TextField commandLine;

  @FXML
  private TextField testCaseName;

  @FXML
  private TextField inputExtension;
  @FXML
  private TextField outputExtension;

  @FXML
  private Label     sampleLabel;
  private Stage              primaryStage;
  @FXML
  private GridPane           parent;

  @FXML
  public void changeCWD() {
    this.parent.setEffect(new BoxBlur(10, 10, 10));
    final File newWorkingDirectory = this
        .showDirectoryChooser(
            "Choose the directory in which your program should be run!",
            this.workingDirectoryPath);
    this.parent.setEffect(null);
    if ((newWorkingDirectory != null) && newWorkingDirectory.isDirectory()) {
      this.updateWorkingDirectory(newWorkingDirectory);
    }
  }
  @FXML
  public void changeTestDirectory() {
    this.parent.setEffect(new BoxBlur(10, 10, 10));
    final File newTestDirectory = this
        .showDirectoryChooser(
            "Choose the directory containing your test samples!",
            this.testDirectoryPath);
    this.parent.setEffect(null);
    if ((newTestDirectory != null) && newTestDirectory.isDirectory()) {
      this.updateTestDirectory(newTestDirectory);
    }
  }

  /**
   * @param inputExtension3
   * @param textField
   *
   */
  public void createListenerForPropertyUpdate(TextField textField, String key) {

    textField
    .textProperty()
    .addListener(
        (ChangeListener<String>) (observable, oldValue, newValue) -> SettingsSaver
        .setProperty(key, newValue));
  }

  /**
   *
   */
  private void handleNoTestCases(String message) {
    this.setBackgroundStyle(this.testDirectory, "#d2657d");
    final String previous = this.testDirectory.getText();
    this.testDirectory.setText(previous + "\n" + message);
    new Timer().schedule(new TimerTask() {

      @Override
      public void run() {
        Platform.runLater(() -> {
          StartScreenController.this.testDirectory.setText(previous);
          StartScreenController.this.setBackgroundStyle(
              StartScreenController.this.testDirectory, "#fcffff");
        });
      }
    }, 2500);
  }

  private void initDragDrop(Button acceptorButton, Consumer<File> callback) {
    acceptorButton.setOnDragOver(event -> {
      final Dragboard db = event.getDragboard();
      if (db.hasFiles()) {
        event.acceptTransferModes(TransferMode.LINK);
      } else {
        event.consume();
      }
    });

    // Dropping over surface
    acceptorButton.setOnDragDropped(event -> {
      final Dragboard db = event.getDragboard();
      boolean success = false;
      if (db.hasFiles()) {
        success = true;
        for (final File file : db.getFiles()) {
          if (file.isDirectory()) {
            callback.accept(file);
            break;
          }

        }
      }
      event.setDropCompleted(success);
      event.consume();
    });
  }

  @FXML
  public void initialize() throws Exception {
    final ChangeListener<String> listener = (ChangeListener<String>) (
        observable, oldValue, newValue) -> StartScreenController.this
        .updateSampleLabel();
        this.testCaseName.textProperty().addListener(listener);
        this.inputExtension.textProperty().addListener(listener);
        this.outputExtension.textProperty().addListener(listener);
        this.initDragDrop(this.workingDirectory,
            (dir) -> this.updateWorkingDirectory(dir));
        this.initDragDrop(this.testDirectory,
            (dir) -> this.updateTestDirectory(dir));
        this.updateFromSaves();
  }
  /**
   * @return
   *
   */
  public String[] prepareArguments() {
    final ArrayList<String> arguments = new ArrayList<>();
    final Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(
        this.commandLine.getText().trim());
    while (m.find()) {
      arguments.add(m.group(1));
    }
    return arguments.toArray(new String[arguments.size()]);
  }

  private LinkedList<TestCase> prepareTestCases() {
    final Map<String, Pair<Path, Path>> testCaseFilePairs = new TestCaseFinder(
        this.testDirectoryPath, this.testCaseName.getText() + "*",
        this.inputExtension.getText(), this.outputExtension.getText())
    .getTestCases();
    final String[] arguments = this.prepareArguments();
    final LinkedList<TestCase> testCases = new LinkedList<>();
    for (final Entry<String, Pair<Path, Path>> testCasePair : testCaseFilePairs
        .entrySet()) {
      try {
        testCases.add(new TestCase(testCasePair.getKey(), testCasePair
            .getValue(), arguments, this.workingDirectoryPath));
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
    return testCases;
  }

  @FXML
  private void runTests() {
    if (this.testDirectoryPath == null) {
      this.setBackgroundStyle(this.testDirectory, "#d2657d");
      return;
    }
    if (this.workingDirectoryPath == null) {
      this.setBackgroundStyle(this.workingDirectory, "#d2657d");
      return;
    }
    final File testDirectoryFile = this.testDirectoryPath.toFile();
    final File workingDirectoryFile = this.workingDirectoryPath.toFile();
    final boolean extensionsArOk = !this.inputExtension.getText().trim()
        .isEmpty()
        && !this.outputExtension.getText().trim().isEmpty();
    final boolean testDirectoryIsOk = testDirectoryFile.exists()
        && testDirectoryFile.isDirectory();
    final boolean workingDirectoryIsOk = workingDirectoryFile.exists()
        && workingDirectoryFile.isDirectory();
    if (extensionsArOk && testDirectoryIsOk && workingDirectoryIsOk) {
      Platform.runLater(() -> {
        try {
          final LinkedList<TestCase> preparedTestCases = this.prepareTestCases();
          if (preparedTestCases.size() == 0) {
            this.handleNoTestCases("No testcases found");
            return;
          }
          preparedTestCases.sort(new NaturalOrderComparator());
          this.runButton.setText("Testing started");
          TestWindowController.showNewWindow(preparedTestCases);
        } catch (final NullPointerException e) {
          this.handleNoTestCases("No testcases found");
          return;
        }
      });
    } else {
      if (!workingDirectoryIsOk) {
        this.setBackgroundStyle(this.workingDirectory, "#d2657d");
        this.workingDirectory
        .setText(this.workingDirectory.getText()
            + "\nThis file is not a directory or this directory does not exist");
      }
    }
  }

  private void setBackgroundStyle(Button pane, String color) {
    final String cssString = String
        .format(
            "-fx-background-color: %s; -fx-border-radius: 0; -fx-background-radius: 0;",
            color);
    pane.setStyle(cssString);
  }

  public File showDirectoryChooser(String title, Path openWithDestination) {

    final DirectoryChooser dirChooser = new DirectoryChooser();
    dirChooser.setTitle(title);
    if (openWithDestination != null) {
      dirChooser.setInitialDirectory(openWithDestination.toFile());
    }
    final Stage dialog = new Stage(StageStyle.UNIFIED);
    dialog.initModality(Modality.WINDOW_MODAL);
    dialog.initOwner(this.primaryStage);
    return dirChooser.showDialog(dialog);
  }

  /*
   * (non-Javadoc)
   * @see javafx.application.Application#start(javafx.stage.Stage)
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage;
    this.primaryStage.getIcons().add(
        new Image(this.getClass().getResourceAsStream("icon.png")));
    final Parent parent = FXMLLoader.load(this.getClass().getResource(
        "StartScreen.fxml"));
    final Scene scene = new Scene(parent);
    primaryStage.setTitle("Rapid runner");
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);
    primaryStage.setHeight(465);
    primaryStage.setWidth(960);
    primaryStage.show();
  }
  /**
   *
   */
  private void updateFromSaves() {
    this.commandLine.setText(SettingsSaver.getProperty(KEYS.COMMANDLINE,
        "java Program"));
    this.testCaseName.setText(SettingsSaver.getProperty(KEYS.TEST_CASE_NAME,
        "Sample"));
    this.inputExtension.setText(SettingsSaver.getProperty(KEYS.INPUT_EXTENSION,
        "in"));
    this.outputExtension.setText(SettingsSaver.getProperty(
        KEYS.OUTPUT_EXTENSION, "out"));
    this.createListenerForPropertyUpdate(this.commandLine,
        SettingsSaver.KEYS.COMMANDLINE);
    this.createListenerForPropertyUpdate(this.testCaseName,
        SettingsSaver.KEYS.TEST_CASE_NAME);
    this.createListenerForPropertyUpdate(this.testCaseName,
        SettingsSaver.KEYS.TEST_CASE_NAME);
    this.createListenerForPropertyUpdate(this.inputExtension,
        SettingsSaver.KEYS.OUTPUT_EXTENSION);
    final String testDirectoryPath = SettingsSaver.getProperty(SettingsSaver.KEYS.TESTDIRECTORY_PATH, "");
    if(!testDirectoryPath.isEmpty()){
      this.testDirectory.setText(testDirectoryPath);
      this.testDirectoryPath = Paths.get(testDirectoryPath);
    }
    final String workingDirectoryString = SettingsSaver.getProperty(
        SettingsSaver.KEYS.WORKINGDIRECTORY_PATH, "");
    if (!workingDirectoryString.isEmpty()) {
      this.workingDirectory.setText(testDirectoryPath);
      this.workingDirectoryPath = Paths.get(workingDirectoryString);
    }
    this.heading.setText(String.format(StartScreenController.HEADING,
        this.workingDirectoryPath.toFile().getName()));
  }


  /**
   *
   */
  @FXML
  public void updateSampleLabel() {
    this.sampleLabel.setText(String.format(
        StartScreenController.SAMPLE_STRING, this.testCaseName.getText(),
        this.inputExtension.getText(), this.outputExtension.getText()));
  }

  /**
   * @param newTestDirectory
   */
  private void updateTestDirectory(final File newTestDirectory) {
    SettingsSaver.setProperty(SettingsSaver.KEYS.TESTDIRECTORY_PATH,
        newTestDirectory.getAbsolutePath());
    this.testDirectory.setText(newTestDirectory.getAbsolutePath());
    this.setBackgroundStyle(this.testDirectory, "#fcffff");
    this.testDirectoryPath = Paths.get(newTestDirectory.getPath());
  }

  /**
   * @param newWorkingDirectory
   */
  private void updateWorkingDirectory(File newWorkingDirectory) {
    SettingsSaver.setProperty(SettingsSaver.KEYS.WORKINGDIRECTORY_PATH,
        newWorkingDirectory.getAbsolutePath());
    this.workingDirectory.setText(newWorkingDirectory.getAbsolutePath());
    this.workingDirectoryPath = Paths.get(newWorkingDirectory.getPath());
    this.setBackgroundStyle(this.workingDirectory, "#fcffff");
    this.heading.setText(String.format(StartScreenController.HEADING,
        newWorkingDirectory.getName()));
  }
}
