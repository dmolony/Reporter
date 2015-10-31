package com.bytezone.reporter.application;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
  private final Set<NodeSelectionListener> nodeSelectionListeners = new HashSet<> ();
  private final TreeView<FileNode> fileTree = new TreeView<> ();
  private final Preferences prefs;

  private File selectedFile;
  private Path treePath;
  private TreeItem<FileNode> selectedTreeItem;
  private TreeItem<FileNode> unsavedFilesItem;

  public TreePanel (Preferences prefs)
  {
    this.prefs = prefs;
    getLastFile ();
  }

  public TreeView<FileNode> getTree (Path path)
  {
    if (Files.notExists (path) || !Files.isDirectory (path))
    {
      System.out.println (path + " not valid");
    }

    treePath = path;
    FileNode directory = new FileNode (path.toFile ());
    TreeItem<FileNode> root = findFiles (directory);
    //    root.setExpanded (true);
    fileTree.setRoot (root);
    fileTree.setStyle ("-fx-font-size: 12; -fx-font-family: Monospaced");
    fileTree.setShowRoot (false);

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

    if (Files.notExists (path))
      addBuffer ("message", getMessage ());

    fileTree.setCellFactory (new TreeCellFactory ());

    return fileTree;
  }

  public TreeView<FileNode> getTree ()
  {
    return fileTree;
  }

  public void addBuffer (String name, byte[] buffer, String folderName)
  {
    Path filePath =
        Paths.get (System.getProperty ("user.home"), "dm3270", "files", folderName);

    if (Files.exists (filePath))
    {
      String buildPath = filePath.toString ();
      String[] segments = name.split ("\\.");
      for (String segment : segments)
      {
        System.out.println (segment);
        Path path = Paths.get (buildPath, segment);
        System.out.println (path);
        System.out.println ();
        if (Files.notExists (path))
          break;
        buildPath = path.toString ();
      }
      filePath = Paths.get (buildPath);
      System.out.printf ("Store file %s in %s%n", name, filePath);
    }

    addBuffer (name, buffer);
  }

  public void addBuffer (String name, byte[] buffer)
  {
    if (unsavedFilesItem == null)
    {
      FileNode fileNode = new FileNode ("downloads", null);
      unsavedFilesItem = new TreeItem<> (fileNode);
      fileNode.setTreeItem (unsavedFilesItem);
      fileTree.getRoot ().getChildren ().add (unsavedFilesItem);
    }

    FileNode fileNode = new FileNode (name, buffer);
    TreeItem<FileNode> treeItem = new TreeItem<> (fileNode);
    fileNode.setTreeItem (treeItem);
    unsavedFilesItem.getChildren ().add (treeItem);

    fileTree.getSelectionModel ().select (treeItem);
  }

  private TreeItem<FileNode> findFiles (FileNode directory)
  {
    TreeItem<FileNode> treeItem = new TreeItem<> (directory);
    directory.setTreeItem (treeItem);

    File directoryFile = directory.getFile ();
    if (Files.exists (directoryFile.toPath ()))
    {
      File[] files = directoryFile.listFiles ();
      if (files != null)
        for (File file : files)
        {
          if (file.isHidden ())
            continue;

          if (file.isDirectory ())
          {
            TreeItem<FileNode> newItem = findFiles (new FileNode (file));
            if (!newItem.isLeaf ())
              treeItem.getChildren ().add (newItem);
          }
          else
          {
            FileNode fileNode = new FileNode (file);
            TreeItem<FileNode> newItem = new TreeItem<> (fileNode);
            fileNode.setTreeItem (newItem);
            treeItem.getChildren ().add (newItem);
            if (file.equals (selectedFile))
              selectedTreeItem = newItem;
          }
        }
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
    if (fileNode == null)
    {
      System.out.println ("null filenode selected");
      return;
    }

    if (fileNode.getFile () == null)
    {
      if (fileNode.getReportData ().hasData ())
      {
        // file transfers have no file, but they have a buffer
        selectedFile = null;
        selectedTreeItem = treeItem;
        fireNodeSelected (fileNode);
      }
      else
        return;             // downloads node  has no directory and no buffer
    }
    // this includes all non-data nodes (except the downloads 'folder')
    else if (fileNode.getFile ().isFile ())
    {
      selectedFile = fileNode.getFile ();
      selectedTreeItem = treeItem;
      fireNodeSelected (fileNode);
      savePrefs ();
    }
  }

  private void fireNodeSelected (FileNode fileNode)
  {
    for (NodeSelectionListener listener : nodeSelectionListeners)
      listener.nodeSelected (fileNode);
  }

  void addNodeSelectionListener (NodeSelectionListener listener)
  {
    nodeSelectionListeners.add (listener);
  }

  void removeFileSelectionListener (NodeSelectionListener listener)
  {
    nodeSelectionListeners.remove (listener);
  }

  private void getLastFile ()
  {
    String fileName = prefs.get ("LastFile", "");
    selectedFile = fileName.isEmpty () ? null : new File (fileName);
  }

  private void savePrefs ()
  {
    String fileName = selectedFile == null ? "" : selectedFile.getAbsolutePath ();
    prefs.put ("LastFile", fileName);
  }

  private byte[] getMessage ()
  {
    Path path = Paths.get (System.getProperty ("user.home"), "dm3270", "files");
    String message1 = "Could not find download folder.\n \n";
    String message2 = "Please create: " + path;
    return (message1 + message2).getBytes ();
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
              children.add (createNode (childFile));

            return children;
          }
        }

        return FXCollections.emptyObservableList ();
      }
    };
  }
}