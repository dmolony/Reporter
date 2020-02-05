package com.bytezone.reporter.application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.util.Callback;

// -----------------------------------------------------------------------------------//
class TreeCellFactory implements Callback<TreeView<FileNode>, TreeCell<FileNode>>
// -----------------------------------------------------------------------------------//
{
  private FileNode pendingFileNode;

  // ---------------------------------------------------------------------------------//
  @Override
  public TreeCell<FileNode> call (TreeView<FileNode> treeView)
  // ---------------------------------------------------------------------------------//
  {
    TreeCell<FileNode> treeCell = new TreeCellFileNode ();

    // detect the beginning of a drag
    MouseHandler mouseHandler = new MouseHandler (treeCell);
    treeCell.setOnDragDetected (mouseHandler);    // set pendingFileNode

    // handle the various drag events
    DragHandler dragHandler = new DragHandler (treeCell);
    treeCell.setOnDragOver (dragHandler);
    treeCell.setOnDragEntered (dragHandler);
    treeCell.setOnDragExited (dragHandler);
    treeCell.setOnDragDropped (dragHandler);      // clear pendingFileNode
    treeCell.setOnDragDone (dragHandler);         // clear pendingFileNode

    return treeCell;
  }

  // ---------------------------------------------------------------------------------//
  private boolean saveFile (FileNode fileNode, File targetFile)
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      if (targetFile.exists ())                   // check for overwrite
      {
        showAlert ("File already exists");
        return false;
      }

      File sourceFile = fileNode.getFile ();
      if (sourceFile == null)                     // create new file from buffer
        Files.write (targetFile.toPath (), fileNode.getReportData ().getBuffer ());
      else                                        // move existing file
        Files.move (sourceFile.toPath (), targetFile.toPath (),
            StandardCopyOption.ATOMIC_MOVE);
      return true;
    }
    catch (IOException e)
    {
      e.printStackTrace ();
    }
    return false;
  }

  // ---------------------------------------------------------------------------------//
  private boolean showAlert (String message)
  // ---------------------------------------------------------------------------------//
  {
    Alert alert = new Alert (AlertType.ERROR, message);
    alert.getDialogPane ().setHeaderText (null);
    Optional<ButtonType> result = alert.showAndWait ();
    return (result.isPresent () && result.get () == ButtonType.OK);
  }

  // ---------------------------------------------------------------------------------//
  private final class TreeCellFileNode extends TreeCell<FileNode>
  // ---------------------------------------------------------------------------------//
  {
    @Override
    protected void updateItem (FileNode item, boolean empty)
    {
      super.updateItem (item, empty);
      if (!empty && item != null)
      {
        setText (item.toString ());
        setGraphic (getTreeItem ().getGraphic ());
      }
      else
      {
        setText (null);
        setGraphic (null);
      }
    }
  }

  // ---------------------------------------------------------------------------------//
  private class MouseHandler implements EventHandler<MouseEvent>
  // ---------------------------------------------------------------------------------//
  {
    private final TreeCell<FileNode> treeCell;

    public MouseHandler (TreeCell<FileNode> treeCell)
    {
      this.treeCell = treeCell;
    }

    @Override
    public void handle (MouseEvent event)
    {
      EventType<? extends MouseEvent> type = event.getEventType ();
      if (type == MouseEvent.DRAG_DETECTED)
      {
        Dragboard db = treeCell.startDragAndDrop (TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent ();
        content.putString (treeCell.getItem ().toString ());
        db.setContent (content);
        pendingFileNode = treeCell.getItem ();
        event.consume ();
      }
    }
  }

  // ---------------------------------------------------------------------------------//
  private class DragHandler implements EventHandler<DragEvent>
  // ---------------------------------------------------------------------------------//
  {
    private final TreeCell<FileNode> treeCell;

    public DragHandler (TreeCell<FileNode> treeCell)
    {
      this.treeCell = treeCell;
    }

    @Override
    public void handle (DragEvent event)
    {
      EventType<DragEvent> type = event.getEventType ();
      if (type == DragEvent.DRAG_ENTERED)
      {
        File file = treeCell.getItem ().getFile ();
        if (file != null && file.isDirectory ())
          treeCell.setTextFill (Color.RED);

        event.consume ();
      }
      else if (type == DragEvent.DRAG_EXITED)
      {
        File file = treeCell.getItem ().getFile ();
        if (file != null && file.isDirectory ())
          treeCell.setTextFill (Color.BLACK);

        event.consume ();
      }
      else if (type == DragEvent.DRAG_OVER)
      {
        FileNode fileNode = treeCell.getItem ();
        File file = fileNode.getFile ();

        if (file != null && file.isDirectory ())
          event.acceptTransferModes (TransferMode.MOVE);

        event.consume ();
      }
      else if (type == DragEvent.DRAG_DROPPED)
      {
        FileNode targetFileNode = treeCell.getItem ();

        File targetDirectory = targetFileNode.getFile ();       // must be a folder
        assert targetDirectory.isDirectory ();

        File targetFile = new File (targetDirectory, pendingFileNode.getDatasetName ());

        if (saveFile (pendingFileNode, targetFile))
        {
          pendingFileNode.setFile (targetFile);

          // remove source TreeItem from the tree
          TreeItem<FileNode> pendingTreeItem = pendingFileNode.getTreeItem ();
          pendingTreeItem.getParent ().getChildren ().remove (pendingTreeItem);

          // create a new TreeItem
          TreeItem<FileNode> newItem = new TreeItem<> (pendingFileNode);
          pendingFileNode.setTreeItem (newItem);

          // connect new TreeItem to target TreeItem
          TreeItem<FileNode> treeItem = targetFileNode.getTreeItem ();
          treeItem.getChildren ().add (newItem);        // needs to be sorted

          // select the new TreeItem
          treeCell.getTreeView ().getSelectionModel ().select (newItem);
        }

        pendingFileNode = null;
        event.setDropCompleted (true);
        event.consume ();
      }
      else if (type == DragEvent.DRAG_DONE)
      {
        pendingFileNode = null;
        event.consume ();
      }
    }
  }
}
