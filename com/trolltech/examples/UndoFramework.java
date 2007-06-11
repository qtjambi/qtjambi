package com.trolltech.examples;

import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;

import java.util.*;

public class UndoFramework extends QMainWindow
{
    private QAction deleteAction;
    private QAction addBoxAction;
    private QAction addTriangleAction;
    private QAction undoAction;
    private QAction redoAction;
    private QAction exitAction;
    private QAction aboutAction;

    private QMenu fileMenu;
    private QMenu editMenu;
    private QMenu itemMenu;
    private QMenu helpMenu;

    private DiagramScene diagramScene;
    private QUndoStack undoStack;
    private QUndoView undoView;

    public static int itemCount = 0;

    public enum DiagramType { Box, Triangle }

    public UndoFramework()
    {
        undoStack = new QUndoStack();
    
        createActions();
        createMenus();
    
        undoStack.canRedoChanged.connect(redoAction, "setEnabled(boolean)");
        undoStack.canUndoChanged.connect(undoAction, "setEnabled(boolean)");

        createUndoView();
    
        diagramScene = new DiagramScene();
        diagramScene.setSceneRect(new QRectF(0, 0, 500, 500));
    
        diagramScene.itemMoved.connect(this, "itemMoved(UndoFramework$DiagramItem,QPointF)");
    
        setWindowTitle("Undo Framework");
        QGraphicsView view = new QGraphicsView(diagramScene);
        setCentralWidget(view);
        resize(700, 500);
    }

    private void createUndoView()
    {
        undoView = new QUndoView(undoStack);
        undoView.setWindowTitle(tr("Command List"));
        undoView.show();
        undoView.setAttribute(Qt.WidgetAttribute.WA_QuitOnClose, false);
    }

    private void createActions()
    {
        deleteAction = new QAction(tr("&Delete Item"), this);
        deleteAction.setShortcut(tr("Del"));
        deleteAction.triggered.connect(this, "deleteItem()");
    
        addBoxAction = new QAction(tr("Add &Box"), this);
        addBoxAction.setShortcut(tr("Ctrl+O"));
        addBoxAction.triggered.connect(this, "addBox()");
    
        addTriangleAction = new QAction(tr("Add &Triangle"), this);
        addTriangleAction.setShortcut(tr("Ctrl+T"));
        addTriangleAction.triggered.connect(this, "addTriangle()");
    
        undoAction = new QAction(tr("&Undo"), this);
        undoAction.setShortcut(tr("Ctrl+Z"));
        undoAction.setEnabled(false);
        undoAction.triggered.connect(undoStack, "undo()");
    
        redoAction = new QAction(tr("&Redo"), this);
        List<QKeySequence> redoShortcuts = new LinkedList<QKeySequence>();
        redoShortcuts.add(new QKeySequence(tr("Ctrl+Y")));
        redoShortcuts.add(new QKeySequence(tr("Shift+Ctrl+Z")));
        redoAction.setShortcuts(redoShortcuts);
        redoAction.setEnabled(false);
        redoAction.triggered.connect(undoStack, "redo()");
    
        exitAction = new QAction(tr("E&xit"), this);
        exitAction.setShortcut(tr("Ctrl+Q"));
        exitAction.triggered.connect(this, "close()");
    
        aboutAction = new QAction(tr("&About"), this);
        List<QKeySequence> aboutShortcuts = new LinkedList<QKeySequence>();
        aboutShortcuts.add(new QKeySequence(tr("Ctrl+A")));
        aboutShortcuts.add(new QKeySequence(tr("Ctrl+B")));
        aboutAction.setShortcuts(aboutShortcuts);
        aboutAction.triggered.connect(this, "about()");
    }

    private void createMenus()
    {
        fileMenu = menuBar().addMenu(tr("&File"));
        fileMenu.addAction(exitAction);
    
        editMenu = menuBar().addMenu(tr("&Edit"));
        editMenu.addAction(undoAction);
        editMenu.addAction(redoAction);
        editMenu.addSeparator();
        editMenu.addAction(deleteAction);
        editMenu.aboutToShow.connect(this, "itemMenuAboutToShow()");
        editMenu.aboutToHide.connect(this, "itemMenuAboutToHide()");

        itemMenu = menuBar().addMenu(tr("&Item"));
        itemMenu.addAction(addBoxAction);
        itemMenu.addAction(addTriangleAction);
    
        helpMenu = menuBar().addMenu(tr("&About"));
        helpMenu.addAction(aboutAction);
    }

    public void itemMoved(DiagramItem movedItem, QPointF oldPosition)
    {
        undoStack.push(new MoveCommand(movedItem, oldPosition));
    }

    private void deleteItem()
    {
        if (diagramScene.selectedItems().isEmpty())
            return;

        QUndoCommand deleteCommand = new DeleteCommand(diagramScene);
        undoStack.push(deleteCommand);
    }

    private void itemMenuAboutToHide()
    {
        deleteAction.setEnabled(true);
    }

    private void itemMenuAboutToShow()
    {
        undoAction.setText(tr("Undo ") + undoStack.undoText());
        redoAction.setText(tr("Redo ") + undoStack.redoText());
        deleteAction.setEnabled(!diagramScene.selectedItems().isEmpty());
    }

    private void addBox()
    {
        QUndoCommand addCommand = new AddCommand(DiagramType.Box, diagramScene);
        undoStack.push(addCommand);
    }

    private void addTriangle()
    {
        QUndoCommand addCommand = new AddCommand(DiagramType.Triangle,
                                                 diagramScene);
        undoStack.push(addCommand);
    }

    private void about()
    {
        QMessageBox.about(this, tr("About Undo"),
                          tr("The <b>Undo</b> example demonstrates how to " +
                          "use Qt's undo framework."));
    }

    class DiagramItem extends QGraphicsPolygonItem
    {

        private QPolygonF boxPolygon;
        private QPolygonF trianglePolygon;

        public DiagramItem(DiagramType diagramType)
        {
            boxPolygon = new QPolygonF();
            trianglePolygon = new QPolygonF();

            if (diagramType == DiagramType.Box) {
                boxPolygon.add(new QPointF(0, 0));
                boxPolygon.add(new QPointF(0, 30));
                boxPolygon.add(new QPointF(30, 30));
                boxPolygon.add(new QPointF(30, 0));
                boxPolygon.add(new QPointF(0, 0));
                setPolygon(boxPolygon);
            } else {
                trianglePolygon.add(new QPointF(15, 0));
                trianglePolygon.add(new QPointF(30, 30));
                trianglePolygon.add(new QPointF(0, 30));
                trianglePolygon.add(new QPointF(15, 0));
                setPolygon(trianglePolygon);
            }

            Random random = new Random();
            QColor color = new QColor(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            QBrush brush = new QBrush(color);
            setBrush(brush);
            setFlag(QGraphicsItem.GraphicsItemFlag.ItemIsSelectable, true);
            setFlag(QGraphicsItem.GraphicsItemFlag.ItemIsMovable, true);
        }

        public DiagramType diagramType() {
            return polygon() == boxPolygon ? DiagramType.Box : DiagramType.Triangle;
        }
    } 

    class DiagramScene extends QGraphicsScene
    {
        public Signal2<DiagramItem,QPointF> itemMoved =
            new Signal2<DiagramItem,QPointF>();

        private DiagramItem movingItem;
        private QPointF oldPos;

        public DiagramScene()
        {
            movingItem = null;
        }

        public void mousePressEvent(QGraphicsSceneMouseEvent event)
        {
            QPointF mousePos = new QPointF(event.buttonDownScenePos(Qt.MouseButton.LeftButton).x(),
                             event.buttonDownScenePos(Qt.MouseButton.LeftButton).y());
            movingItem = (DiagramItem) itemAt(mousePos.x(), mousePos.y());

            if (movingItem != null && event.button() == Qt.MouseButton.LeftButton) {
                oldPos = movingItem.pos();
            }
            super.mousePressEvent(event);
        }

        public void mouseReleaseEvent(QGraphicsSceneMouseEvent event)
        {
            if (movingItem != null && event.button() == Qt.MouseButton.LeftButton) {
                if (oldPos != movingItem.pos()) {
                    itemMoved.emit(movingItem, oldPos);
                }
                movingItem = null;
            }
            super.mouseReleaseEvent(event);
        }

        protected void drawBackground(QPainter painter, QRectF rect)
        {
            double startY = rect.top() - Math.IEEEremainder(rect.top(), 30.0);
            for (; startY < rect.bottom(); startY += 30.0)
                painter.drawLine(new QPointF(rect.left(), startY),
                                 new QPointF(rect.right(), startY)); 

            double startX = rect.left() - Math.IEEEremainder(rect.left(), 30.0);
            for (; startX < rect.right(); startX += 30.0)
                painter.drawLine(new QPointF(startX, rect.top()),
                                 new QPointF(startX, rect.bottom()));
        }
    }

    class DeleteCommand extends QUndoCommand
    {
        private DiagramItem myDiagramItem;
        private QGraphicsScene myGraphicsScene;

        public DeleteCommand(QGraphicsScene scene)
        {
            myGraphicsScene = scene;
            List<QGraphicsItemInterface> list = myGraphicsScene.selectedItems();
            list.get(0).setSelected(false);
            myDiagramItem = (DiagramItem) list.get(0);
            setText("Delete " + UndoFramework.createCommandString(myDiagramItem, myDiagramItem.pos()));
        }

        public void redo()
        {
            myGraphicsScene.removeItem(myDiagramItem);
        }

        public void undo()
        {
            myGraphicsScene.addItem(myDiagramItem);
            myGraphicsScene.update();
        }
    }

    class MoveCommand extends QUndoCommand
    {
        private DiagramItem myDiagramItem;
        private QPointF myOldPos;
        private QPointF newPos;


        public MoveCommand(DiagramItem diagramItem, QPointF oldPos)
        {
            myDiagramItem = diagramItem;
            newPos = diagramItem.pos();
            myOldPos = oldPos;
        }

        public int id() { return 1; }

        public void undo()
        {
            myDiagramItem.setPos(myOldPos);
            myDiagramItem.scene().update();
            setText(tr("Move " + UndoFramework.createCommandString(myDiagramItem, newPos)));
       } 

        public void redo()
        {
            myDiagramItem.setPos(newPos);
            setText(tr("Move " + UndoFramework.createCommandString(myDiagramItem, newPos)));
        }    

        public boolean mergeWith(QUndoCommand other)
        {
            MoveCommand moveCommand = (MoveCommand) other;
            DiagramItem item = moveCommand.myDiagramItem;
        
            if (!myDiagramItem.equals(item))
                return false;
        
            newPos = item.pos();
            setText(tr("Move " + UndoFramework.createCommandString(myDiagramItem, newPos)));
        
            return true;
        }
    }

    class AddCommand extends QUndoCommand
    {
        private DiagramItem myDiagramItem;
        private QGraphicsScene myGraphicsScene;
        private QPointF initialPosition;

        public AddCommand(DiagramType addType, QGraphicsScene scene)
        {
            myGraphicsScene = scene;
            myDiagramItem = new DiagramItem(addType);
            initialPosition = new QPointF((UndoFramework.itemCount * 15) % (int) scene.width(),
                              (UndoFramework.itemCount * 15) % (int) scene.height());
            scene.update();
            ++UndoFramework.itemCount;
            setText(tr("Add " + UndoFramework.createCommandString(myDiagramItem, initialPosition)));
        }

        public void redo()
        {
            myGraphicsScene.addItem(myDiagramItem);
            myDiagramItem.setPos(initialPosition);
            myGraphicsScene.clearSelection();
            myGraphicsScene.update();
        }

        public void undo()
        {
            myGraphicsScene.removeItem(myDiagramItem);
            myGraphicsScene.update();
        }
    }

    public static String createCommandString(DiagramItem item, QPointF pos)
    {
        return QCoreApplication.translate("DiagramItem", item.diagramType() == DiagramType.Box ? "Box" : "Triangle" +
               " at (" + pos.x() + ", " + pos.y() + ")");
    }

    public static void main(String args[])
    {
        QApplication.initialize(args);

        UndoFramework mainWindow = new UndoFramework();
        mainWindow.show();

        QApplication.exec();
    }
}
