package com.bytezone.reporter.application;

import com.bytezone.reporter.file.ReportData;
import javafx.scene.control.TreeItem;

import java.io.File;

public class FileNode
{
  private final ReportData reportData;
  private final String datasetName;
  private File file;
  private TreeItem<FileNode> treeItem;

  public FileNode (File file)
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

  public FileNode (String name, byte[] buffer)
  {
    datasetName = name;
    reportData = new ReportData (buffer);
  }

  public TreeItem<FileNode> getTreeItem ()
  {
    return treeItem;
  }

  public void setTreeItem (TreeItem<FileNode> treeItem)
  {
    this.treeItem = treeItem;
  }

  public boolean isAscii ()
  {
    return reportData.isAscii ();
  }

  public ReportData getReportData ()
  {
    return reportData;
  }

  public String getDatasetName ()
  {
    return datasetName;
  }

  public File getFile ()
  {
    return file;
  }

  public void setFile (File file)
  {
    this.file = file;
  }

  //    public byte[] getBuffer ()
  //    {
  //      return buffer;
  //    }

  //    public void setBuffer (byte[] buffer)
  //    {
  //      assert this.buffer == null;
  //      this.buffer = buffer;
  //    }

  @Override
  public String toString ()
  {
    return datasetName;
  }
}