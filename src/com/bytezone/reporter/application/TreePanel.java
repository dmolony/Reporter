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
  private final Set<FileSelectionListener> fileSelectionListeners = new HashSet<> ();
  private final TreeView<FileNode> fileTree = new TreeView<> ();
  private final Preferences prefs;

  private File selectedFile;
  private TreeItem<FileNode> selectedTreeItem;

  public TreePanel (Preferences prefs)
  {
    this.prefs = prefs;
    getLastFile ();
  }

  public TreeView<FileNode> getTree (String directoryName)
  {
    FileNode directory = new FileNode (new File (directoryName));
    findFiles (directory, null);

    ChangeListener<TreeItem<FileNode>> changeListener =
        (observable, oldValue, newValue) -> selection (newValue);
    fileTree.getSelectionModel ().selectedItemProperty ().addListener (changeListener);

    if (selectedTreeItem != null)
      fileTree.getSelectionModel ().select (selectedTreeItem);

    return fileTree;
  }

  private void findFiles (FileNode directory, TreeItem<FileNode> parent)
  {
    TreeItem<FileNode> treeItem = new TreeItem<> (directory);
    treeItem.setExpanded (true);

    for (File file : directory.file.listFiles ())
      if (file.isDirectory ())
        findFiles (new FileNode (file), treeItem);
      else
      {
        TreeItem<FileNode> newItem = new TreeItem<> (new FileNode (file));
        treeItem.getChildren ().add (newItem);
        if (file.equals (selectedFile))
          selectedTreeItem = newItem;
      }

    if (parent == null)
      fileTree.setRoot (treeItem);
    else
      parent.getChildren ().add (treeItem);
  }

  private void selection (TreeItem<FileNode> treeItem)
  {
    if (treeItem == null)
      return;

    FileNode fileNode = treeItem.getValue ();
    if (!fileNode.file.isDirectory ())
    {
      selectedFile = fileNode.file;
      selectedTreeItem = treeItem;
      notifyFileSelected (fileNode);
      saveLastFile ();
    }
  }

  private void notifyFileSelected (FileNode fileNode)
  {
    for (FileSelectionListener listener : fileSelectionListeners)
      listener.fileSelected (fileNode);
  }

  public void addFileSelectionListener (FileSelectionListener listener)
  {
    fileSelectionListeners.add (listener);
  }

  public void removeFileSelectionListener (FileSelectionListener listener)
  {
    fileSelectionListeners.remove (listener);
  }

  private void getLastFile ()
  {
    String fileName = prefs.get ("LastFile", "");
    selectedFile = fileName.isEmpty () ? null : new File (fileName);
  }

  private void saveLastFile ()
  {
    String fileName = selectedFile == null ? "" : selectedFile.getAbsolutePath ();
    prefs.put ("LastFile", fileName);
  }

  class FileNode
  {
    File file;
    ReportData reportData;
    String datasetName;

    public FileNode (File file)
    {
      this.file = file;
      reportData = new ReportData ();
    }

    @Override
    public String toString ()
    {
      return datasetName == null ? file.getName () : datasetName;
    }
  }
}