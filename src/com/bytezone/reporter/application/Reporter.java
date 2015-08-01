package com.bytezone.reporter.application;

import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Reporter extends Application
{
  private final BorderPane borderPane = new BorderPane ();
  private WindowSaver windowSaver;
  private final Preferences prefs = Preferences.userNodeForPackage (this.getClass ());

  @Override
  public void start (Stage primaryStage) throws Exception
  {
    String home = System.getProperty ("user.home") + "/Dropbox/testfiles";

    ReporterNode reporterNode = new ReporterNode (prefs);
    primaryStage.setTitle ("Reporter");
    primaryStage.setScene (new Scene (reporterNode.getRootNode (), 800, 592));
    primaryStage.setOnCloseRequest (e -> closeWindow ());

    windowSaver = new WindowSaver (prefs, primaryStage, "Reporter");
    windowSaver.restoreWindow ();

    reporterNode.getTreePanel ().getTree ().requestFocus ();

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