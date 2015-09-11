package com.bytezone.reporter.application;

import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Reporter extends Application
{
  private final static String OS = System.getProperty ("os.name");
  private final static boolean SYSTEM_MENUBAR = OS != null && OS.startsWith ("Mac");

  private WindowSaver windowSaver;
  private final Preferences prefs = Preferences.userNodeForPackage (this.getClass ());

  @Override
  public void start (Stage primaryStage) throws Exception
  {
    ReporterNode reporterNode = new ReporterNode (prefs);
    BorderPane borderPane = new BorderPane ();
    borderPane.setCenter (reporterNode);
    borderPane.setTop (reporterNode.getMenuBar ());

    primaryStage.setTitle ("Reporter");
    primaryStage.setScene (new Scene (borderPane, 800, 592));
    primaryStage.setOnCloseRequest (e -> closeWindow ());

    windowSaver = new WindowSaver (prefs, primaryStage, "Reporter");
    windowSaver.restoreWindow ();

    reporterNode.requestFocus ();

    MenuBar menuBar = reporterNode.getMenuBar ();
    menuBar.setUseSystemMenuBar (SYSTEM_MENUBAR);

    primaryStage.show ();
  }

  private void closeWindow ()
  {
    windowSaver.saveWindow ();
  }

  public static void main (String[] args)
  {
    launch (args);
  }
}