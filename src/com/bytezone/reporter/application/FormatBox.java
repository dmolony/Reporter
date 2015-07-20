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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class FormatBox extends VBox
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

  public VBox getFormattingBox (EventHandler<ActionEvent> rebuild,
      EventHandler<ActionEvent> paginate, List<RecordMaker> recordMakers,
      List<TextMaker> textMakers, List<ReportMaker> reportMakers)
  {
    this.recordMakers = recordMakers;
    this.textMakers = textMakers;
    this.reportMakers = reportMakers;

    VBox recordsBox = new VBox (10);
    recordsBox.setPadding (new Insets (10));
    for (RecordMaker recordMaker : recordMakers)
      addButton (recordMaker, recordsGroup, rebuild, recordMakerButtons, recordsBox);

    VBox encodingsBox = new VBox (10);
    encodingsBox.setPadding (new Insets (10));
    for (TextMaker textMaker : textMakers)
      addButton (textMaker, encodingsGroup, paginate, textMakerButtons, encodingsBox);

    VBox reportsBox = new VBox (10);
    reportsBox.setPadding (new Insets (10));
    for (ReportMaker reportMaker : reportMakers)
      addButton (reportMaker, reportsGroup, paginate, reportMakerButtons, reportsBox);

    VBox formattingBox = new VBox ();
    addTitledPane ("Records", recordsBox, formattingBox);
    addTitledPane ("Encoding", encodingsBox, formattingBox);
    addTitledPane ("Formatting", reportsBox, formattingBox);

    return formattingBox;
  }

  public void select (Score score)
  {
    for (RadioButton button : recordMakerButtons)
      if (button.getUserData () == score.recordMaker)
      {
        button.setSelected (true);
        break;
      }

    for (RadioButton button : textMakerButtons)
      if (button.getUserData () == score.textMaker)
      {
        button.setSelected (true);
        break;
      }

    for (RadioButton button : reportMakerButtons)
      if (button.getUserData () == score.reportMaker)
      {
        button.setSelected (true);
        break;
      }
  }

  public void process (List<Score> scores)
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

    disable (missingRecordMakers, recordMakerButtons);
    disable (missingTextMakers, textMakerButtons);
    disable (missingReportMakers, reportMakerButtons);

    Collections.reverse (reportMakers);
    loop: for (ReportMaker reportMaker : reportMakers)
      for (Score score : perfectScores)
        if (score.reportMaker == reportMaker)
        {
          select (score);
          System.out.println (score);
          System.out.println ();
          break loop;
        }

    for (Score score : perfectScores)
      System.out.println (score);
  }

  private void disable (List<? extends Object> missingObjects, List<RadioButton> buttons)
  {
    for (Object o : missingObjects)
      for (RadioButton button : buttons)
        if (button.getUserData () == o)
          button.setDisable (true);
  }

  public RecordMaker getSelectedRecordMaker ()
  {
    RadioButton btn = (RadioButton) recordsGroup.getSelectedToggle ();
    return ((RecordMaker) btn.getUserData ());
  }

  public TextMaker getSelectedTextMaker ()
  {
    RadioButton btn = (RadioButton) encodingsGroup.getSelectedToggle ();
    return ((TextMaker) btn.getUserData ());
  }

  public ReportMaker getSelectedReportMaker ()
  {
    RadioButton btn = (RadioButton) reportsGroup.getSelectedToggle ();
    return ((ReportMaker) btn.getUserData ());
  }

  private TitledPane addTitledPane (String text, Node contents, VBox parent)
  {
    TitledPane titledPane = new TitledPane (text, contents);
    titledPane.setCollapsible (false);
    parent.getChildren ().add (titledPane);
    return titledPane;
  }

  private RadioButton addButton (Object userData, ToggleGroup group,
      EventHandler<ActionEvent> evt, List<RadioButton> buttonList, VBox vbox)
  {
    RadioButton button = new RadioButton (userData.toString ());
    button.setToggleGroup (group);
    button.setOnAction (evt);
    button.setUserData (userData);
    buttonList.add (button);
    vbox.getChildren ().add (button);
    return button;
  }
}