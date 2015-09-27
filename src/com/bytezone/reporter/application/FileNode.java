package com.bytezone.reporter.application;

import java.io.File;

import com.bytezone.reporter.file.ReportData;

public class FileNode
{
  private File file;
  private final ReportData reportData;
  private final String datasetName;
  //  private TreeItem<FileNode> treeItem;

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

  //  public void setTreeItem (TreeItem<FileNode> treeItem)
  //  {
  //    this.treeItem = treeItem;
  //  }
  //
  //  public TreeItem<FileNode> getTreeItem ()
  //  {
  //    return treeItem;
  //  }

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