package com.bytezone.reporter.application;

import java.util.ArrayList;
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

  public VBox getFormattingBox (EventHandler<ActionEvent> rebuild,
      EventHandler<ActionEvent> paginate, List<RecordMaker> recordMakers,
      List<TextMaker> textMakers, List<ReportMaker> reportMakers)
  {
    VBox recordsBox = new VBox (10);
    recordsBox.setPadding (new Insets (10));

    for (RecordMaker recordMaker : recordMakers)
    {
      RadioButton button = addRecordTypeButton (recordMaker, recordsGroup, rebuild);
      recordMakerButtons.add (button);
      recordsBox.getChildren ().add (button);
    }

    VBox encodingsBox = new VBox (10);
    encodingsBox.setPadding (new Insets (10));

    for (TextMaker textMaker : textMakers)
    {
      RadioButton button = addEncodingTypeButton (textMaker, encodingsGroup, paginate);
      textMakerButtons.add (button);
      encodingsBox.getChildren ().add (button);
    }

    VBox reportsBox = new VBox (10);
    reportsBox.setPadding (new Insets (10));

    for (ReportMaker reportMaker : reportMakers)
    {
      RadioButton button = addFormatTypeButton (reportMaker, reportsGroup, paginate);
      reportMakerButtons.add (button);
      reportsBox.getChildren ().add (button);
    }

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

  private TitledPane addTitledPane (String text, Node contents, VBox parent)
  {
    TitledPane titledPane = new TitledPane (text, contents);
    titledPane.setCollapsible (false);
    parent.getChildren ().add (titledPane);
    return titledPane;
  }

  private RadioButton addRecordTypeButton (RecordMaker recordMaker, ToggleGroup group,
      EventHandler<ActionEvent> evt)
  {
    RadioButton button = addRadioButton (recordMaker.toString (), group, evt);
    button.setUserData (recordMaker);
    return button;
  }

  private RadioButton addEncodingTypeButton (TextMaker textMaker, ToggleGroup group,
      EventHandler<ActionEvent> evt)
  {
    RadioButton button = addRadioButton (textMaker.toString (), group, evt);
    button.setUserData (textMaker);
    return button;
  }

  private RadioButton addFormatTypeButton (ReportMaker reportMaker, ToggleGroup group,
      EventHandler<ActionEvent> evt)
  {
    RadioButton button = addRadioButton (reportMaker.toString (), group, evt);
    button.setUserData (reportMaker);
    return button;
  }

  private RadioButton addRadioButton (String text, ToggleGroup group,
      EventHandler<ActionEvent> evt)
  {
    RadioButton button = new RadioButton (text);
    button.setToggleGroup (group);
    button.setOnAction (evt);
    return button;
  }
}