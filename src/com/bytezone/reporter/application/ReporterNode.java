package com.bytezone.reporter.application;

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;

import com.bytezone.reporter.application.TreePanel.FileNode;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

public class ReporterNode implements PaginationChangeListener, NodeSelectionListener
{
  private final static String OS = System.getProperty ("os.name");
  private final static boolean MAC_MENUBAR = OS != null && OS.startsWith ("Mac");

  private FormatBox formatBox;
  private TreePanel treePanel;

  private final BorderPane borderPane;
  private final MenuBar menuBar = new MenuBar ();

  private final Preferences prefs;
  private FileNode currentFileNode;

  private HBox transferPanel;
  private TextField txtDatasetName;
  private boolean isTransferPanelVisible;

  public ReporterNode (Preferences prefs)
  {
    this.borderPane = new BorderPane ();
    this.prefs = prefs;
  }

  public Parent getRootNode ()
  {
    String home = System.getProperty ("user.home") + "/Dropbox/testfiles";

    treePanel = new TreePanel (prefs);
    treePanel.addNodeSelectionListener (this);
    StackPane stackPane = new StackPane ();
    stackPane.setPrefWidth (180);
    stackPane.setMinHeight (180);

    TreeView<FileNode> tree = treePanel.getTree (home);
    stackPane.getChildren ().add (tree);

    borderPane.setLeft (stackPane);
    borderPane.setTop (menuBar);

    menuBar.getMenus ().addAll (getFileMenu ());

    if (MAC_MENUBAR)
      menuBar.useSystemMenuBarProperty ().set (true);

    return borderPane;
  }

  public TreePanel getTreePanel ()
  {
    return treePanel;
  }

  @Override
  public void nodeSelected (FileNode fileNode)
  {
    formatBox = fileNode.formatBox;
    borderPane.setRight (formatBox.getFormattingBox ());
    formatBox.setFileNode (fileNode, this);
    currentFileNode = fileNode;
  }

  private Menu getFileMenu ()
  {
    Menu menuFile = new Menu ("File");

    MenuItem menuItemOpen = getMenuItem ("Open...", e -> openFile (), KeyCode.O);
    MenuItem menuItemSave = getMenuItem ("Save...", e -> saveFile (), KeyCode.S);
    MenuItem menuItemUpload = getMenuItem ("Upload file", e -> uploadFile (), KeyCode.U);
    MenuItem menuItemDownload =
        getMenuItem ("Download file", e -> downloadFile (), KeyCode.D);
    MenuItem menuItemPrint = getMenuItem ("Page setup", e -> pageSetup (), null);
    MenuItem menuItemPageSetup = getMenuItem ("Print", e -> printFile (), KeyCode.P);
    MenuItem menuItemClose = getMenuItem ("Close window", e -> closeWindow (), KeyCode.W);

    menuFile.getItems ().addAll (menuItemOpen, menuItemSave, menuItemUpload,
                                 menuItemDownload, menuItemPageSetup, menuItemPrint,
                                 menuItemClose);
    return menuFile;
  }

  private MenuItem getMenuItem (String text, EventHandler<ActionEvent> eventHandler,
      KeyCode keyCode)
  {
    MenuItem menuItem = new MenuItem (text);
    menuItem.setOnAction (eventHandler);
    if (keyCode != null)
      menuItem.setAccelerator (new KeyCodeCombination (keyCode,
          KeyCombination.SHORTCUT_DOWN));
    return menuItem;
  }

  private void openFile ()
  {
    System.out.println ("Open");
  }

  private void pageSetup ()
  {
    SwingUtilities.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        PrinterJob printerJob = PrinterJob.getPrinterJob ();// AWT
        PageFormat pageFormat = printerJob.defaultPage ();
        printerJob.pageDialog (pageFormat);
      }
    });
  }

  private void printFile ()
  {
    SwingUtilities.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        PrinterJob printerJob = PrinterJob.getPrinterJob ();// AWT!!!

        if (printerJob.printDialog ())
        {
          try
          {
            printerJob.setPrintable (formatBox.getSelectedReportMaker ());
            printerJob.print ();
          }
          catch (PrinterException e)
          {
            e.printStackTrace ();
          }
        }
      }
    });
  }

  private void uploadFile ()
  {
    if (transferPanel == null)
    {
      Label lblTransfer = new Label ("Transfer file");
      txtDatasetName = new TextField ();

      transferPanel = new HBox (10);
      transferPanel.setPadding (new Insets (10, 10, 10, 10));// trbl
      transferPanel.setAlignment (Pos.CENTER_LEFT);
      transferPanel.getChildren ().addAll (lblTransfer, txtDatasetName);

    }

    if (isTransferPanelVisible)
    {
      borderPane.setBottom (null);
      isTransferPanelVisible = false;
    }
    else
    {
      borderPane.setBottom (transferPanel);
      isTransferPanelVisible = true;
    }
  }

  private void downloadFile ()
  {

  }

  private void saveFile ()
  {
    FileChooser fileChooser = new FileChooser ();

    fileChooser.setInitialFileName (currentFileNode.datasetName + ".txt");
    //Set extension filter
    //    FileChooser.ExtensionFilter extFilter =
    //        new FileChooser.ExtensionFilter ("TXT files (*.txt)", "*.txt");
    //    fileChooser.getExtensionFilters ().add (extFilter);

    File file = fileChooser.showSaveDialog (null);

    if (file != null && file.isFile () && !file.isHidden ())
      try
      {
        Files.write (file.toPath (), currentFileNode.getBuffer());
      }
      catch (IOException e)
      {
        e.printStackTrace ();
      }
  }

  private void closeWindow ()
  {
    System.out.println ("Close");
  }

  @Override
  public void paginationChanged (Pagination pagination)
  {
    borderPane.setCenter (pagination);
  }
}