package com.bytezone.reporter.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.tests.Score;
import com.bytezone.reporter.text.TextMaker;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

class FormatBox extends VBox
{
  final ToggleGroup recordsGroup = new ToggleGroup ();
  final ToggleGroup encodingsGroup = new ToggleGroup ();
  final ToggleGroup reportsGroup = new ToggleGroup ();

  private final List<RadioButton> recordMakerButtons = new ArrayList<> ();
  private final List<RadioButton> textMakerButtons = new ArrayList<> ();
  private final List<RadioButton> reportMakerButtons = new ArrayList<> ();

  private final Label lblSizeText = new Label ();
  private final Label lblRecordsText = new Label ();

  private VBox recordsBox;
  private VBox encodingsBox;
  private VBox reportsBox;

  private ReportData reportData;

  public void setData (ReportData reportData, EventHandler<ActionEvent> rebuild)
  {
    this.reportData = reportData;

    List<RecordMaker> recordMakers = reportData.getRecordMakers ();
    List<TextMaker> textMakers = reportData.getTextMakers ();
    List<ReportMaker> reportMakers = reportData.getReportMakers ();

    if (recordsBox == null)
    {
      recordsBox = createVBox (recordMakers, recordMakerButtons, recordsGroup, rebuild);
      encodingsBox = createVBox (textMakers, textMakerButtons, encodingsGroup, rebuild);
      reportsBox = createVBox (reportMakers, reportMakerButtons, reportsGroup, rebuild);
    }
    else
    {
      assignButtons (recordMakerButtons, recordMakers);
      assignButtons (textMakerButtons, textMakers);
      assignButtons (reportMakerButtons, reportMakers);
    }
  }

  public VBox getFormattingBox ()
  {
    Label lblSize = new Label ("Bytes");
    Label lblRecords = new Label ("Records");
    lblSizeText.setFont (Font.font ("monospaced", 14));
    lblRecordsText.setFont (Font.font ("monospaced", 14));
    lblSize.setPrefWidth (60);
    lblRecords.setPrefWidth (60);

    HBox hbox1 = new HBox (10);
    hbox1.getChildren ().addAll (lblSize, lblSizeText);

    HBox hbox2 = new HBox (10);
    hbox2.getChildren ().addAll (lblRecords, lblRecordsText);

    VBox vbox = new VBox (10);
    vbox.setPadding (new Insets (10));
    vbox.getChildren ().addAll (hbox1, hbox2);

    VBox formattingBox = new VBox ();
    addTitledPane ("Data", vbox, formattingBox);
    addTitledPane ("Structure", recordsBox, formattingBox);
    addTitledPane ("Encoding", encodingsBox, formattingBox);
    addTitledPane ("Formatting", reportsBox, formattingBox);

    return formattingBox;
  }

  private VBox createVBox (List<? extends Object> objects, List<RadioButton> buttons,
      ToggleGroup group, EventHandler<ActionEvent> action)
  {
    VBox vbox = new VBox (10);
    vbox.setPadding (new Insets (10));

    // List of RecordMaker/TextMaker/ReportMaker
    for (Object userData : objects)
    {
      RadioButton button = new RadioButton (userData.toString ());
      button.setToggleGroup (group);
      button.setOnAction (action);
      button.setUserData (userData);

      buttons.add (button);
      vbox.getChildren ().add (button);
    }
    return vbox;
  }

  private void assignButtons (List<RadioButton> buttons, List<? extends Object> objects)
  {
    for (int i = 0; i < buttons.size (); i++)
      buttons.get (i).setUserData (objects.get (i));
  }

  private TitledPane addTitledPane (String text, Node contents, VBox parent)
  {
    TitledPane titledPane = new TitledPane (text, contents);
    titledPane.setCollapsible (false);
    parent.getChildren ().add (titledPane);
    return titledPane;
  }

  void process ()
  {
    List<Score> scores = reportData.getScores ();

    List<RecordMaker> missingRecordMakers = new ArrayList<> ();
    List<TextMaker> missingTextMakers = new ArrayList<> ();
    List<ReportMaker> missingReportMakers = new ArrayList<> ();

    List<RecordMaker> recordMakers = reportData.getRecordMakers ();
    List<TextMaker> textMakers = reportData.getTextMakers ();
    List<ReportMaker> reportMakers = reportData.getReportMakers ();

    missingRecordMakers.addAll (recordMakers);
    missingTextMakers.addAll (textMakers);
    missingReportMakers.addAll (reportMakers);

    missingRecordMakers.remove (0);// the 'None' option
    List<Score> perfectScores = new ArrayList<> ();

    for (Score score : scores)
      if (score.score == 100.0)
      {
        perfectScores.add (score);
        missingRecordMakers.remove (score.recordMaker);
        missingTextMakers.remove (score.textMaker);
        missingReportMakers.remove (score.reportMaker);
      }

    enable (recordMakerButtons);
    enable (textMakerButtons);
    enable (reportMakerButtons);

    disable (missingRecordMakers, recordMakerButtons);
    disable (missingTextMakers, textMakerButtons);
    disable (missingReportMakers, reportMakerButtons);

    List<ReportMaker> reversedReportMakers = new ArrayList<> ();
    reversedReportMakers.addAll (reportMakers);
    Collections.reverse (reversedReportMakers);
    loop: for (ReportMaker reportMaker : reversedReportMakers)
      for (Score score : perfectScores)
        if (score.reportMaker == reportMaker)
        {
          select (score);
          break loop;
        }
  }

  void select (Score score)
  {
    selectButton (recordMakerButtons, score.recordMaker);
    selectButton (textMakerButtons, score.textMaker);
    selectButton (reportMakerButtons, score.reportMaker);
  }

  private void selectButton (List<RadioButton> buttons, Object userData)
  {
    for (RadioButton button : buttons)
      if (button.getUserData () == userData)
      {
        button.setSelected (true);
        return;
      }
  }

  void setDataSize (int records)
  {
    RecordMaker recordMaker = getSelectedRecordMaker ();
    lblSizeText.setText (String.format ("%,10d", recordMaker.getBuffer ().length));
    lblRecordsText.setText (String.format ("%,10d", records));
  }

  private void disable (List<? extends Object> missingObjects, List<RadioButton> buttons)
  {
    for (Object userData : missingObjects)
      for (RadioButton button : buttons)
        if (button.getUserData () == userData)
          button.setDisable (true);
  }

  private void enable (List<RadioButton> buttons)
  {
    for (RadioButton button : buttons)
      button.setDisable (false);
  }

  RecordMaker getSelectedRecordMaker ()
  {
    return (RecordMaker) recordsGroup.getSelectedToggle ().getUserData ();
  }

  TextMaker getSelectedTextMaker ()
  {
    return (TextMaker) encodingsGroup.getSelectedToggle ().getUserData ();
  }

  ReportMaker getSelectedReportMaker ()
  {
    return (ReportMaker) reportsGroup.getSelectedToggle ().getUserData ();
  }
}