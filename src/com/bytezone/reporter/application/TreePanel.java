package com.bytezone.reporter.application;

import java.io.File;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreePanel
{
  private final TreeView<File> fileTree = new TreeView<> ();

  public void initialise ()
  {
    String home = System.getProperty ("user.home") + "/Dropbox/testfiles/";
    File currentDir = new File (home);// current directory
    findFiles (currentDir, null);
  }

  private void findFiles (File directory, TreeItem<File> parent)
  {
    TreeItem<File> root = new TreeItem<> (directory);
    root.setExpanded (true);

    for (File file : directory.listFiles ())
      if (file.isDirectory ())
        findFiles (file, root);
      else
        root.getChildren ().add (new TreeItem<> (file));

    if (parent == null)
      fileTree.setRoot (root);
    else
      parent.getChildren ().add (root);
  }
}