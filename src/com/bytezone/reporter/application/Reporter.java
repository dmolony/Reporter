package com.bytezone.reporter.application;

import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Reporter extends Application
{
  private WindowSaver windowSaver;
  private final Preferences prefs = Preferences.userNodeForPackage (this.getClass ());

  @Override
  public void start (Stage primaryStage) throws Exception
  {
    ReporterNode reporterNode = new ReporterNode (prefs);
    primaryStage.setTitle ("Reporter");
    primaryStage.setScene (new Scene (reporterNode, 800, 592));
    primaryStage.setOnCloseRequest (e -> closeWindow ());

    windowSaver = new WindowSaver (prefs, primaryStage, "Reporter");
    windowSaver.restoreWindow ();

    reporterNode.requestFocus ();

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