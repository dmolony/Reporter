package com.bytezone.reporter.application;

import com.bytezone.reporter.application.TreePanel.FileNode;

public interface FileSelectionListener
{
  public abstract void fileSelected (FileNode fileNode);
}