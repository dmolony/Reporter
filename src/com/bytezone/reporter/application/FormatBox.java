package com.bytezone.reporter.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bytezone.reporter.application.TreePanel.FileNode;
import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.tests.ReportScore;
import com.bytezone.reporter.text.TextMaker;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

class FormatBox
{
  private final Set<PaginationChangeListener> paginationChangeListeners =
      new HashSet<> ();

  final ToggleGroup recordsGroup = new ToggleGroup ();
  final ToggleGroup encodingsGroup = new ToggleGroup ();
  final ToggleGroup reportsGroup = new ToggleGroup ();

  private final List<RadioButton> recordMakerButtons = new ArrayList<> ();
  private final List<RadioButton> textMakerButtons = new ArrayList<> ();
  private final List<RadioButton> reportMakerButtons = new ArrayList<> ();

  private final Label lblSizeText = new Label ();
  private final Label lblRecordsText = new Label ();

  private final VBox recordsBox;
  private final VBox encodingsBox;
  private final VBox reportsBox;

  private ReportData reportData;
  private VBox formattingBox;

  public FormatBox (ReportData reportData)
  {
    this.reportData = reportData;

    List<RecordMaker> recordMakers = reportData.getRecordMakers ();
    List<TextMaker> textMakers = reportData.getTextMakers ();
    List<ReportMaker> reportMakers = reportData.getReportMakers ();

    recordsBox = createVBox (recordMakers, recordMakerButtons, recordsGroup);
    encodingsBox = createVBox (textMakers, textMakerButtons, encodingsGroup);
    reportsBox = createVBox (reportMakers, reportMakerButtons, reportsGroup);
  }

  private VBox createVBox (List<? extends Object> objects, List<RadioButton> buttons,
      ToggleGroup group)
  {
    VBox vbox = new VBox (10);
    vbox.setPadding (new Insets (10));

    // List of RecordMaker/TextMaker/ReportMaker
    for (Object userData : objects)
    {
      RadioButton button = new RadioButton (userData.toString ());
      button.setToggleGroup (group);
      button.setOnAction (e -> buttonSelection ());

      buttons.add (button);
      vbox.getChildren ().add (button);
    }

    linkButtons (buttons, objects);

    return vbox;
  }

  ReportData getReportData ()
  {
    return reportData;
  }

  private void linkButtons (List<RadioButton> buttons, List<? extends Object> objects)
  {
    for (int i = 0; i < buttons.size (); i++)
      buttons.get (i).setUserData (objects.get (i));
  }

  public VBox getFormattingBox ()
  {
    if (formattingBox == null)
    {
      Label lblSize = setLabel ("Bytes", 60);
      Label lblRecords = setLabel ("Records", 60);
      lblSizeText.setFont (Font.font ("monospaced", 14));
      lblRecordsText.setFont (Font.font ("monospaced", 14));

      HBox hbox1 = new HBox (10);
      hbox1.getChildren ().addAll (lblSize, lblSizeText);

      HBox hbox2 = new HBox (10);
      hbox2.getChildren ().addAll (lblRecords, lblRecordsText);

      VBox vbox = new VBox (10);
      vbox.setPadding (new Insets (10));
      vbox.getChildren ().addAll (hbox1, hbox2);
      vbox.setPrefWidth (180);

      formattingBox = new VBox ();
      addTitledPane ("Data size", vbox, formattingBox);
      addTitledPane ("Structure", recordsBox, formattingBox);
      addTitledPane ("Encoding", encodingsBox, formattingBox);
      addTitledPane ("Formatting", reportsBox, formattingBox);
    }

    return formattingBox;
  }

  private Label setLabel (String text, int width)
  {
    Label label = new Label (text);
    label.setPrefWidth (width);
    return label;
  }

  private TitledPane addTitledPane (String text, Node contents, VBox parent)
  {
    TitledPane titledPane = new TitledPane (text, contents);
    titledPane.setCollapsible (false);
    parent.getChildren ().add (titledPane);
    return titledPane;
  }

  private void adjustButtons ()
  {
    // Create lists of buttons to disable
    List<RecordMaker> imperfectRecordMakers = new ArrayList<> ();
    List<TextMaker> imperfectTextMakers = new ArrayList<> ();
    List<ReportMaker> imperfectReportMakers = new ArrayList<> ();

    // Add every button to the list
    imperfectRecordMakers.addAll (reportData.getRecordMakers ());
    imperfectTextMakers.addAll (reportData.getTextMakers ());
    imperfectReportMakers.addAll (reportData.getReportMakers ());

    // Remove buttons that should always be valid
    imperfectRecordMakers.remove (0);// the 'None' option
    imperfectReportMakers.remove (0);// the Hex option

    // Remove buttons that have been used in a perfect report
    List<ReportScore> perfectScores = new ArrayList<> ();
    for (ReportScore score : reportData.getScores ())
      if (score.score == 100.0)
      {
        imperfectRecordMakers.remove (score.recordMaker);
        imperfectTextMakers.remove (score.textMaker);
        imperfectReportMakers.remove (score.reportMaker);
        perfectScores.add (score);
      }

    // Enable all buttons
    enable (recordMakerButtons);
    enable (textMakerButtons);
    enable (reportMakerButtons);

    // Disable the buttons that don't have perfect scores
    disable (imperfectRecordMakers, recordMakerButtons);
    disable (imperfectTextMakers, textMakerButtons);
    disable (imperfectReportMakers, reportMakerButtons);

    // Find the best report possible and select its buttons
    ReportScore bestReportScore = getBestReportScore (perfectScores);
    if (bestReportScore != null)
      selectButtons (bestReportScore);
    else
      System.out.println ("Imperfect ReportScore selected");
  }

  private void enable (List<RadioButton> buttons)
  {
    for (RadioButton button : buttons)
      button.setDisable (false);
  }

  private ReportScore getBestReportScore (List<ReportScore> perfectScores)
  {
    List<ReportMaker> reversedReportMakers = new ArrayList<> ();
    reversedReportMakers.addAll (reportData.getReportMakers ());
    Collections.reverse (reversedReportMakers);

    for (ReportMaker reportMaker : reversedReportMakers)
      for (ReportScore score : perfectScores)
        if (score.reportMaker == reportMaker)
          return score;

    return null;
  }

  private void buttonSelection ()
  {
    RecordMaker recordMaker = getSelectedRecordMaker ();
    TextMaker textMaker = getSelectedTextMaker ();
    ReportMaker reportMaker = getSelectedReportMaker ();

    List<Record> records = recordMaker.getRecords ();
    setDataSize (records.size ());

    ReportScore currentReportScore =
        findReportScore (recordMaker, textMaker, reportMaker);
    assert currentReportScore != null;

    reportMaker.setPagination (currentReportScore);

    System.out.println (currentReportScore);
    notifyPaginationChanged (currentReportScore.getPagination ());
  }

  void selectButtons (ReportScore reportScore)
  {
    selectButton (recordMakerButtons, reportScore.recordMaker);
    selectButton (textMakerButtons, reportScore.textMaker);
    selectButton (reportMakerButtons, reportScore.reportMaker);
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

  private ReportScore findReportScore (RecordMaker recordMaker, TextMaker textMaker,
      ReportMaker reportMaker)
  {
    for (ReportScore score : reportData.getScores ())
      if (score.matches (recordMaker, textMaker, reportMaker))
        return score;
    return null;
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

  public void setFileNode (FileNode fileNode, PaginationChangeListener listener)
  {
    reportData = getReportData ();

    if (!reportData.hasData ())
      try
      {
        reportData.readFile (fileNode.file);// creates scores
        addPaginationChangeListener (listener);
        adjustButtons ();// uses scores to enable/disable buttons
      }
      catch (IOException e)
      {
        e.printStackTrace ();
      }

    buttonSelection ();// force a pagination change
  }

  private RecordMaker getSelectedRecordMaker ()
  {
    return (RecordMaker) recordsGroup.getSelectedToggle ().getUserData ();
  }

  private TextMaker getSelectedTextMaker ()
  {
    return (TextMaker) encodingsGroup.getSelectedToggle ().getUserData ();
  }

  ReportMaker getSelectedReportMaker ()
  {
    return (ReportMaker) reportsGroup.getSelectedToggle ().getUserData ();
  }

  private void notifyPaginationChanged (Pagination pagination)
  {
    for (PaginationChangeListener listener : paginationChangeListeners)
      listener.paginationChanged (pagination);
  }

  public void addPaginationChangeListener (PaginationChangeListener listener)
  {
    paginationChangeListeners.add (listener);
    System.out.println (paginationChangeListeners.size ());
  }

  public void removePaginationChangeListener (PaginationChangeListener listener)
  {
    paginationChangeListeners.remove (listener);
  }
}