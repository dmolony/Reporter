package com.bytezone.reporter.application;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreePanel
{
  private final TreeView<FileNode> fileTree = new TreeView<> ();

  public TreeView<FileNode> getTree ()
  {
    String home = System.getProperty ("user.home") + "/Dropbox/testfiles/";
    FileNode directory = new FileNode (new File (home));
    findFiles (directory, null);

    ChangeListener<TreeItem<FileNode>> changeListener =
        (observable, oldValue, newValue) -> selection (newValue);
    fileTree.getSelectionModel ().selectedItemProperty ().addListener (changeListener);

    return fileTree;
  }

  private void selection (TreeItem<FileNode> fileNode)
  {
    notifyFileSelected (fileNode.getValue ().file);
  }

  private void findFiles (FileNode directory, TreeItem<FileNode> parent)
  {
    TreeItem<FileNode> treeItem = new TreeItem<> (directory);
    treeItem.setExpanded (true);

    for (File file : directory.file.listFiles ())
      if (file.isDirectory ())
        findFiles (new FileNode (file), treeItem);
      else
        treeItem.getChildren ().add (new TreeItem<FileNode> (new FileNode (file)));

    if (parent == null)
      fileTree.setRoot (treeItem);
    else
      parent.getChildren ().add (treeItem);
  }
  private final Set<FileSelectionListener> fileSelectionListeners = new HashSet<> ();

  void notifyFileSelected (File file)
  {
    for (FileSelectionListener listener : fileSelectionListeners)
      listener.fileSelected (file);
  }

  public void addFileSelectionListener (FileSelectionListener listener)
  {
    fileSelectionListeners.add (listener);
  }

  public void removeFileSelectionListener (FileSelectionListener listener)
  {
    fileSelectionListeners.remove (listener);
  }

  class FileNode
  {
    File file;

    public FileNode (File file)
    {
      this.file = file;
    }

    @Override
    public String toString ()
    {
      return file.getName ();
    }
  }
}