package com.bytezone.reporter.application;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreePanel
{
  private final TreeView<FileNode> fileTree = new TreeView<> ();
  private final Preferences prefs;

  public TreePanel (Preferences prefs)
  {
    this.prefs = prefs;
  }

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
    if (fileNode == null)
      return;

    if (!fileNode.getValue ().file.isDirectory ())
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