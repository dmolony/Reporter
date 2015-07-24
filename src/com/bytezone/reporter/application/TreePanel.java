package com.bytezone.reporter.application;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
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

    TreeItem<FileNode> root = findFiles (directory);
    root.setExpanded (true);
    fileTree.setRoot (root);

    if (false)
    {
      EventHandler<TreeItem.TreeModificationEvent<FileNode>> expandListener =
          (TreeItem.TreeModificationEvent<FileNode> node) -> openDirectory (node);
      root.addEventHandler (TreeItem.<FileNode> branchExpandedEvent (), expandListener);
    }

    ChangeListener<TreeItem<FileNode>> changeListener =
        (observable, oldValue, newValue) -> selection (newValue);
    fileTree.getSelectionModel ().selectedItemProperty ().addListener (changeListener);

    if (selectedTreeItem != null)
      fileTree.getSelectionModel ().select (selectedTreeItem);

    return fileTree;
  }

  private TreeItem<FileNode> findFiles (FileNode directory)
  {
    TreeItem<FileNode> treeItem = new TreeItem<> (directory);

    for (File file : directory.file.listFiles ())
      if (file.isDirectory ())
      {
        TreeItem<FileNode> newItem = findFiles (new FileNode (file));
        if (!newItem.isLeaf ())
          treeItem.getChildren ().add (newItem);
      }
      else
      {
        TreeItem<FileNode> newItem = new TreeItem<> (new FileNode (file));
        treeItem.getChildren ().add (newItem);
        if (file.equals (selectedFile))
          selectedTreeItem = newItem;
      }

    return treeItem;
  }

  public void openDirectory (TreeItem.TreeModificationEvent<FileNode> evt)
  {
    TreeItem<FileNode> treeItem = evt.getSource ();
    System.out.println ("Open: " + treeItem);
  }

  private void selection (TreeItem<FileNode> treeItem)
  {
    if (treeItem == null)
    {
      System.out.println ("null tree item selected");
      return;
    }

    FileNode fileNode = treeItem.getValue ();
    if (fileNode.file.isDirectory ())
    {
      //      System.out.println (treeItem.getChildren ().size ());
    }
    else
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

  TreeView<File> buildFileSystemBrowser ()
  {
    TreeItem<File> root = createNode (new File ("/"));
    return new TreeView<File> (root);
  }

  // This method creates a TreeItem to represent the given File. It does this
  // by overriding the TreeItem.getChildren() and TreeItem.isLeaf() methods 
  // anonymously, but this could be better abstracted by creating a 
  // 'FileTreeItem' subclass of TreeItem. However, this is left as an exercise
  // for the reader.
  private TreeItem<File> createNode (final File f)
  {
    return new TreeItem<File> (f)
    {
      // We cache whether the File is a leaf or not. A File is a leaf if
      // it is not a directory and does not have any files contained within
      // it. We cache this as isLeaf() is called often, and doing the 
      // actual check on File is expensive.
      private boolean isLeaf;

      // We do the children and leaf testing only once, and then set these
      // booleans to false so that we do not check again during this
      // run. A more complete implementation may need to handle more 
      // dynamic file system situations (such as where a folder has files
      // added after the TreeView is shown). Again, this is left as an
      // exercise for the reader.
      private boolean isFirstTimeChildren = true;
      private boolean isFirstTimeLeaf = true;

      @Override
      public ObservableList<TreeItem<File>> getChildren ()
      {
        if (isFirstTimeChildren)
        {
          isFirstTimeChildren = false;

          // First getChildren() call, so we actually go off and 
          // determine the children of the File contained in this TreeItem.
          super.getChildren ().setAll (buildChildren (this));
        }
        return super.getChildren ();
      }

      @Override
      public boolean isLeaf ()
      {
        if (isFirstTimeLeaf)
        {
          isFirstTimeLeaf = false;
          File f = getValue ();
          isLeaf = f.isFile ();
        }

        return isLeaf;
      }

      private ObservableList<TreeItem<File>> buildChildren (TreeItem<File> TreeItem)
      {
        File f = TreeItem.getValue ();
        if (f != null && f.isDirectory ())
        {
          File[] files = f.listFiles ();
          if (files != null)
          {
            ObservableList<TreeItem<File>> children =
                FXCollections.observableArrayList ();

            for (File childFile : files)
            {
              children.add (createNode (childFile));
            }

            return children;
          }
        }

        return FXCollections.emptyObservableList ();
      }
    };
  }
}