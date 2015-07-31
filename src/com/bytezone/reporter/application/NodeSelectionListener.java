package com.bytezone.reporter.application;

import com.bytezone.reporter.application.TreePanel.FileNode;

public interface NodeSelectionListener
{
  public abstract void nodeSelected (FileNode fileNode);
}