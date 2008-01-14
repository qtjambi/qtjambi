/*   Ported from: src.gui.graphicsview.qgraphicsscene.cpp
<snip>
//! [0]
        QGraphicsScene scene;
        scene.addText("Hello, world!");

        QGraphicsView view(&scene);
        view.show();
//! [0]


//! [1]
        QGraphicsScene scene;
        scene.addItem(...
        ...
        QPrinter printer(QPrinter::HighResolution);
        printer.setPageSize(QPrinter::A4);

        QPainter painter(&printer);
        scene.render(&painter);
//! [1]


//! [2]
        QSizeF segmentSize = sceneRect().size() / pow(2, depth - 1);
//! [2]


//! [3]
        QGraphicsScene scene;
        QGraphicsView view(&scene);
        view.show();

        // a blue background
        scene.setBackgroundBrush(Qt::blue);

        // a gradient background
        QRadialGradient gradient(0, 0, 10);
        gradient.setSpread(QGradient::RepeatSpread);
        scene.setBackgroundBrush(gradient);
//! [3]


//! [4]
        QGraphicsScene scene;
        QGraphicsView view(&scene);
        view.show();

        // a white semi-transparent foreground
        scene.setForegroundBrush(QColor(255, 255, 255, 127));

        // a grid foreground
        scene.setForegroundBrush(QBrush(Qt::lightGray, Qt::CrossPattern));
//! [4]


//! [5]
      QRectF TileScene::rectForTile(int x, int y) const
      {
          // Return the rectangle for the tile at position (x, y).
          return QRectF(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
      }

      void TileScene::setTile(int x, int y, const QPixmap &pixmap)
      {
          // Sets or replaces the tile at position (x, y) with pixmap.
          if (x >= 0 && x < numTilesH && y >= 0 && y < numTilesV) {
              tiles[y][x] = pixmap;
              invalidate(rectForTile(x, y), BackgroundLayer);
          }
      }

      void TileScene::drawBackground(QPainter *painter, const QRectF &exposed)
      {
          // Draws all tiles that intersect the exposed area.
          for (int y = 0; y < numTilesV; ++y) {
              for (int x = 0; x < numTilesH; ++x) {
                  QRectF rect = rectForTile(x, y);
                  if (exposed.intersects(rect))
                      painter->drawPixmap(rect.topLeft(), tiles[y][x]);
              }
          }
      }
//! [5]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_graphicsview_qgraphicsscene {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QGraphicsScene scene;
        scene.addText("Hello, world!");

        QGraphicsView view(cene);
        view.show();
//! [0]


//! [1]
        QGraphicsScene scene;
        scene.addItem(...
        ...
        QPrinter printer(QPrinter.HighResolution);
        printer.setPageSize(QPrinter.A4);

        QPainter painter(rinter);
        scene.render(ainter);
//! [1]


//! [2]
        QSizeF segmentSize = sceneRect().size() / pow(2, depth - 1);
//! [2]


//! [3]
        QGraphicsScene scene;
        QGraphicsView view(cene);
        view.show();

        // a blue background
        scene.setBackgroundBrush(Qt.blue);

        // a gradient background
        QRadialGradient gradient(0, 0, 10);
        gradient.setSpread(QGradient.RepeatSpread);
        scene.setBackgroundBrush(gradient);
//! [3]


//! [4]
        QGraphicsScene scene;
        QGraphicsView view(cene);
        view.show();

        // a white semi-transparent foreground
        scene.setForegroundBrush(QColor(255, 255, 255, 127));

        // a grid foreground
        scene.setForegroundBrush(QBrush(Qt.lightGray, Qt.CrossPattern));
//! [4]


//! [5]
      QRectF TileScene.rectForTile(int x, int y)
      {
          // Return the rectangle for the tile at position (x, y).
          return QRectF(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
      }

      void TileScene.setTile(int x, int y, QPixmap ixmap)
      {
          // Sets or replaces the tile at position (x, y) with pixmap.
          if (x >= 0 && x < numTilesH && y >= 0 && y < numTilesV) {
              tiles[y][x] = pixmap;
              invalidate(rectForTile(x, y), BackgroundLayer);
          }
      }

      void TileScene.drawBackground(QPainter ainter, QRectF xposed)
      {
          // Draws all tiles that intersect the exposed area.
          for (int y = 0; y < numTilesV; ++y) {
              for (int x = 0; x < numTilesH; ++x) {
                  QRectF rect = rectForTile(x, y);
                  if (exposed.intersects(rect))
                      painter.drawPixmap(rect.topLeft(), tiles[y][x]);
              }
          }
      }
//! [5]


    }
}
