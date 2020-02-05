package com.bytezone.reporter.application;

import java.io.File;

import com.bytezone.reporter.file.ReportData;

import javafx.scene.control.TreeItem;

// -----------------------------------------------------------------------------------//
public class FileNode
// -----------------------------------------------------------------------------------//
{
  private final ReportData reportData;
  private final String datasetName;
  private File file;
  private TreeItem<FileNode> treeItem;

  // ---------------------------------------------------------------------------------//
  public FileNode (File file)
  // ---------------------------------------------------------------------------------//
  {
    this.file = file;
    if (file.isDirectory ())
    {
      reportData = null;
      datasetName = file.getName ();
    }
    else
    {
      reportData = new ReportData ();
      datasetName = file.getName ().toUpperCase ();
    }
  }

  // ---------------------------------------------------------------------------------//
  public FileNode (String name, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    datasetName = name;
    reportData = new ReportData (buffer);
  }

  // ---------------------------------------------------------------------------------//
  public TreeItem<FileNode> getTreeItem ()
  // ---------------------------------------------------------------------------------//
  {
    return treeItem;
  }

  // ---------------------------------------------------------------------------------//
  public void setTreeItem (TreeItem<FileNode> treeItem)
  // ---------------------------------------------------------------------------------//
  {
    this.treeItem = treeItem;
  }

  // ---------------------------------------------------------------------------------//
  public boolean isAscii ()
  // ---------------------------------------------------------------------------------//
  {
    return reportData.isAscii ();
  }

  // ---------------------------------------------------------------------------------//
  public ReportData getReportData ()
  // ---------------------------------------------------------------------------------//
  {
    return reportData;
  }

  // ---------------------------------------------------------------------------------//
  public String getDatasetName ()
  // ---------------------------------------------------------------------------------//
  {
    return datasetName;
  }

  // ---------------------------------------------------------------------------------//
  public File getFile ()
  // ---------------------------------------------------------------------------------//
  {
    return file;
  }

  // ---------------------------------------------------------------------------------//
  public void setFile (File file)
  // ---------------------------------------------------------------------------------//
  {
    this.file = file;
  }

  // ---------------------------------------------------------------------------------//
  public String toDetailedString ()
  {
    // ---------------------------------------------------------------------------------//
    StringBuilder text = new StringBuilder ();

    text.append (String.format ("Dataset name ... %s%n", datasetName));
    text.append (String.format ("File name ...... %s", file));

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return datasetName;
  }
}