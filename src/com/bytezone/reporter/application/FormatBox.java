package com.bytezone.reporter.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.bytezone.reporter.record.CrRecordMaker;
import com.bytezone.reporter.record.CrlfRecordMaker;
import com.bytezone.reporter.record.FbRecordMaker;
import com.bytezone.reporter.record.LfRecordMaker;
import com.bytezone.reporter.record.NoRecordMaker;
import com.bytezone.reporter.record.NvbRecordMaker;
import com.bytezone.reporter.record.RavelRecordMaker;
import com.bytezone.reporter.record.RdwRecordMaker;
import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.record.VbRecordMaker;
import com.bytezone.reporter.reports.AsaReport;
import com.bytezone.reporter.reports.HexReport;
import com.bytezone.reporter.reports.NatloadReport;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.reports.TextReport;
import com.bytezone.reporter.tests.Score;
import com.bytezone.reporter.text.AsciiTextMaker;
import com.bytezone.reporter.text.EbcdicTextMaker;
import com.bytezone.reporter.text.TextMaker;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

class FormatBox extends VBox
{
  final ToggleGroup recordsGroup = new ToggleGroup ();
  final ToggleGroup encodingsGroup = new ToggleGroup ();
  final ToggleGroup reportsGroup = new ToggleGroup ();

  private final List<RadioButton> recordMakerButtons = new ArrayList<> ();
  private final List<RadioButton> textMakerButtons = new ArrayList<> ();
  private final List<RadioButton> reportMakerButtons = new ArrayList<> ();

  private List<RecordMaker> recordMakers;
  private List<TextMaker> textMakers;
  private List<ReportMaker> reportMakers;

  private final RecordMaker crlf = new CrlfRecordMaker ();
  private final RecordMaker cr = new CrRecordMaker ();
  private final RecordMaker lf = new LfRecordMaker ();
  private final RecordMaker fb80 = new FbRecordMaker (80);
  private final RecordMaker fb132 = new FbRecordMaker (132);
  private final RecordMaker fb252 = new FbRecordMaker (252);
  private final RecordMaker vb = new VbRecordMaker ();
  private final RecordMaker nvb = new NvbRecordMaker ();
  private final RecordMaker rdw = new RdwRecordMaker ();
  private final RecordMaker ravel = new RavelRecordMaker ();
  private final RecordMaker none = new NoRecordMaker ();

  private final TextMaker asciiTextMaker = new AsciiTextMaker ();
  private final TextMaker ebcdicTextMaker = new EbcdicTextMaker ();

  private final ReportMaker hexReport = new HexReport ();
  private final ReportMaker textReport = new TextReport ();
  private final ReportMaker natloadReport = new NatloadReport ();
  private final ReportMaker asaReport = new AsaReport ();

  public VBox getFormattingBox (EventHandler<ActionEvent> rebuild)
  {
    hexReport.setNewlineBetweenRecords (true);
    hexReport.setAllowSplitRecords (true);
    asaReport.setAllowSplitRecords (true);

    recordMakers = new ArrayList<> (
        Arrays.asList (none, crlf, cr, lf, vb, rdw, nvb, ravel, fb80, fb132, fb252));
    textMakers = new ArrayList<> (Arrays.asList (asciiTextMaker, ebcdicTextMaker));
    reportMakers =
        new ArrayList<> (Arrays.asList (hexReport, textReport, asaReport, natloadReport));

    VBox recordsBox =
        createVBox (recordMakers, recordMakerButtons, recordsGroup, rebuild);
    VBox encodingsBox =
        createVBox (textMakers, textMakerButtons, encodingsGroup, rebuild);
    VBox reportsBox =
        createVBox (reportMakers, reportMakerButtons, reportsGroup, rebuild);

    VBox formattingBox = new VBox ();
    addTitledPane ("Records", recordsBox, formattingBox);
    addTitledPane ("Encoding", encodingsBox, formattingBox);
    addTitledPane ("Formatting", reportsBox, formattingBox);

    return formattingBox;
  }

  private VBox createVBox (List<? extends Object> objects, List<RadioButton> buttons,
      ToggleGroup group, EventHandler<ActionEvent> action)
  {
    VBox vbox = new VBox (10);
    vbox.setPadding (new Insets (10));
    for (Object userData : objects)
    {
      RadioButton button = new RadioButton (userData.toString ());
      buttons.add (button);
      button.setToggleGroup (group);
      button.setOnAction (action);
      button.setUserData (userData);
      vbox.getChildren ().add (button);
    }
    return vbox;
  }

  private TitledPane addTitledPane (String text, Node contents, VBox parent)
  {
    TitledPane titledPane = new TitledPane (text, contents);
    titledPane.setCollapsible (false);
    parent.getChildren ().add (titledPane);
    return titledPane;
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
        break;
      }
  }

  void process (List<Score> scores)
  {
    List<RecordMaker> missingRecordMakers = new ArrayList<> ();
    List<TextMaker> missingTextMakers = new ArrayList<> ();
    List<ReportMaker> missingReportMakers = new ArrayList<> ();

    missingRecordMakers.addAll (recordMakers);
    missingTextMakers.addAll (textMakers);
    missingReportMakers.addAll (reportMakers);

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

  List<RecordMaker> getRecordMakers ()
  {
    return recordMakers;
  }

  List<TextMaker> getTextMakers ()
  {
    return textMakers;
  }

  List<ReportMaker> getReportMakers ()
  {
    return reportMakers;
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