/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.examples;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import java.util.*;

class TetrixBoard extends QFrame
{
    static final int redTable[] = new int[8];
    static final int greenTable[] = new int[8];
    static final int blueTable[] = new int[8];
    static {
        redTable[0] = 0x00; 
        redTable[1] = 0xCC; 
        redTable[2] = 0x66; 
        redTable[3] = 0x66;
        redTable[4] = 0xCC;
        redTable[5] = 0xCC; 
        redTable[6] = 0x66; 
        redTable[7] = 0xDA;
        
        greenTable[0] = 0x00; 
        greenTable[1] = 0x66; 
        greenTable[2] = 0xCC; 
        greenTable[3] = 0x66;
        greenTable[4] = 0xCC;
        greenTable[5] = 0x66; 
        greenTable[6] = 0xCC; 
        greenTable[7] = 0xAA;

        blueTable[0] = 0x00; 
        blueTable[1] = 0x66; 
        blueTable[2] = 0x66; 
        blueTable[3] = 0xCC;
        blueTable[4] = 0x66;
        blueTable[5] = 0xCC; 
        blueTable[6] = 0xCC; 
        blueTable[7] = 0x00;

    };

    enum TetrixShape { NoShape, ZShape, SShape, LineShape, TShape, SquareShape,
                   LShape, MirroredLShape }
    private static final int BoardWidth = 10;
    private static final int BoardHeight = 22;

    private boolean isStarted = false;
    private boolean isPaused = false;
    private boolean isWaitingAfterLine = false;
    private int numLinesRemoved = 0;
    private int numPiecesDropped = 0;
    private int score = 0;
    private int level = 0;
    private int curX = 0;
    private int curY = 0;
    private QLabel nextPieceLabel = null;
    private QBasicTimer timer = new QBasicTimer();
    private TetrixPiece curPiece = new TetrixPiece();
    private TetrixPiece nextPiece = new TetrixPiece();
    private TetrixShape board[] = new TetrixShape[BoardWidth * BoardHeight];
        
    public Signal1<Integer> scoreChanged;
    public Signal1<Integer> levelChanged;
    public Signal1<Integer> linesRemovedChanged;
    
    public TetrixBoard(QWidget parent) 
    {
        super(parent);
        
        setFrameStyle(QFrame.Panel | QFrame.Sunken);
        setFocusPolicy(Qt.StrongFocus);
        clearBoard();

        nextPiece.setRandomShape();
    }

    public void scoreChanged(int score)
    {
        scoreChanged.emit(score);
    }

    public void levelChanged(int level)
    {
        levelChanged.emit(level);
    }

    public void linesRemovedChanged(int numLines)
    {
        linesRemovedChanged.emit(numLines);
    }

    public void setNextPieceLabel(QLabel label)
    {
        nextPieceLabel = label;
    }
    
    TetrixShape shapeAt(int x, int y)
    {
        return board[x + y * BoardWidth];
    }

    void setShapeAt(int x, int y, TetrixShape shape)
    {
        board[x + y * BoardWidth] = shape;        
    }

    int timeoutTime()
    {
        return 1000 / (1 + level);
    }

    int squareWidth() { return contentsRect().width() / BoardWidth; }

    int squareHeight() { return contentsRect().height() / BoardHeight; }

    public QSize sizeHint()
    {
        return new QSize(BoardWidth * 15 + frameWidth() * 2, BoardHeight * 15 + frameWidth() * 2);
    }

    public QSize minimumSizeHint()
    {
        return new QSize(BoardWidth * 5 + frameWidth() * 2, BoardHeight * 5 + frameWidth() * 2);
    }

    public void start()
    {    
        if (isPaused)
            return ;
            

        isStarted = true;
        isWaitingAfterLine = false;
        numLinesRemoved = 0;
        numPiecesDropped = 0;
        score = 0;
        level = 1;
        clearBoard();

        linesRemovedChanged(numLinesRemoved);
        scoreChanged(score);
        levelChanged(level);

        newPiece();

        timer.start(timeoutTime(), this);
    }

    public void pause()
    {
        if (!isStarted)
            return ;

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
        } else {
            timer.start(timeoutTime(), this);
        }

        update();
    }

    protected void paintEvent(QPaintEvent e)
    {
        super.paintEvent(e);

        QPainter painter = new QPainter();
        painter.begin(this);
        QRect rect = contentsRect();

        if (isPaused) {
            painter.drawText(rect, Qt.AlignCenter, "Pause");
            painter.end();
            return ;
        }

        int boardTop = rect.bottom() - BoardHeight * squareHeight();

        for (int i=0; i<BoardHeight; ++i) {
            for (int j=0; j<BoardWidth; ++j) {
                TetrixShape shape = shapeAt(j, BoardHeight - i - 1);
                if (shape != TetrixShape.NoShape) {
                    drawSquare(painter, rect.left() + j * squareWidth(),
                        boardTop + i * squareHeight(), shape);
                } 
            }
        }

        if (curPiece.shape() != TetrixShape.NoShape) {
            for (int i=0; i<4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);

                drawSquare(painter, rect.left() + x * squareWidth(),
                    boardTop + (BoardHeight - y - 1) * squareHeight(),
                    curPiece.shape());
            }
        }
        
        painter.end();
    }

    protected void keyPressEvent(QKeyEvent event) 
    {
        if (!isStarted || isPaused || curPiece.shape() == TetrixShape.NoShape) {
            super.keyPressEvent(event);
            return ;
        }

        if (event.key() == Qt.Key_Left) 
            tryMove(curPiece, curX - 1, curY);
        else if (event.key() == Qt.Key_Right) 
            tryMove(curPiece, curX + 1, curY);
        else if (event.key() == Qt.Key_Down)
            tryMove(curPiece.rotatedRight(), curX, curY);
        else if (event.key() == Qt.Key_Up)
            tryMove(curPiece.rotatedLeft(), curX, curY);
        else if (event.key() == Qt.Key_Space) 
            dropDown();
        else if (event.key() == Qt.Key_D) 
            oneLineDown();
        else 
            super.keyPressEvent(event);        
    }

    protected void timerEvent(QTimerEvent event) 
    {
        if (event.timerId() == timer.timerId()) {
            if (isWaitingAfterLine) {
                isWaitingAfterLine = false;
                newPiece();
                timer.start(timeoutTime(), this);
            } else {
                oneLineDown();
            }
        } else {
            super.timerEvent(event);
        }
    }

    void clearBoard()
    {
        for (int i=0; i<BoardHeight * BoardWidth; ++i)
            board[i] = TetrixShape.NoShape;
    }

    void dropDown()
    {
        int dropHeight = 0;
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1))
                break ;
            --newY;
            ++dropHeight;
        }
        pieceDropped(dropHeight);
    }

    void oneLineDown()
    {
        if (!tryMove(new TetrixPiece(curPiece), curX, curY - 1))
            pieceDropped(0);
    }

    void pieceDropped(int dropHeight) 
    {
        for (int i=0; i<4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            setShapeAt(x, y, curPiece.shape());
        }

        ++numPiecesDropped;
        if (numPiecesDropped % 25 == 0) {
            ++level;
            timer.start(timeoutTime(), this);
            levelChanged(level);
        }

        score += dropHeight + 7;
        scoreChanged(score);

        removeFullLines();

        if (!isWaitingAfterLine)
            newPiece();
    }

    void removeFullLines()
    {
        int numFullLines = 0;

        for (int i=BoardHeight - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j=0; j<BoardWidth; ++j) {
                if (shapeAt(j, i) == TetrixShape.NoShape) {
                    lineIsFull = false;
                    break ;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k=i; k<BoardHeight - 1; ++k) {
                    for (int j=0; j<BoardWidth; ++j)
                        setShapeAt(j, k, shapeAt(j, k + 1));                    
                }
                for (int j=0; j<BoardWidth; ++j) 
                    setShapeAt(j, BoardHeight - 1, TetrixShape.NoShape);                
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            score += 10 * numFullLines;
            linesRemovedChanged(numLinesRemoved);
            scoreChanged(score);

            timer.start(500, this);
            isWaitingAfterLine = true;
            curPiece.setShape(TetrixShape.NoShape);
            update();
        }
    }

    void newPiece()
    {
        curPiece = new TetrixPiece(nextPiece);
        
        nextPiece.setRandomShape();
        showNextPiece();
        curX = BoardWidth / 2 + 1;
        curY = BoardHeight - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(TetrixShape.NoShape);
            timer.stop();
            isStarted = false;
        }
    }

    void showNextPiece()
    {
        if (nextPieceLabel == null)
            return ;

        int dx = nextPiece.maxX() - nextPiece.minX() + 1;
        int dy = nextPiece.maxY() - nextPiece.minY() + 1;
        
        QPixmap pixmap = new QPixmap(dx * squareWidth(), dy * squareHeight());
        QPainter painter = new QPainter();
        painter.begin(pixmap);
        painter.fillRect(pixmap.rect(), nextPieceLabel.palette().background());

        for (int i=0; i<4; ++i) {
            int x = nextPiece.x(i) - nextPiece.minX();
            int y = nextPiece.y(i) - nextPiece.minY();
            drawSquare(painter, x * squareWidth(), y * squareHeight(), nextPiece.shape());
        }
        painter.end();

        nextPieceLabel.setPixmap(pixmap);        
    }

    boolean tryMove(TetrixPiece newPiece, int newX, int newY) 
    {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (shapeAt(x, y) != TetrixShape.NoShape)
                return false;
        }

        curPiece = new TetrixPiece(newPiece);
        curX = newX;
        curY = newY;
        update();
        return true;
    }
    
    void drawSquare(QPainter painter, int x, int y, TetrixShape shape)
    {
        QColor color = new QColor(redTable[shape.ordinal()], greenTable[shape.ordinal()], blueTable[shape.ordinal()]);       
        painter.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2, 
            new QBrush(color));

        painter.setPen(color.light());
        painter.drawLine(x, y + squareHeight() - 1, x, y);
        painter.drawLine(x, y, x + squareWidth() - 1, y);
        
        painter.setPen(color.dark());
        painter.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        painter.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }
}


class TetrixPiece
{
    static final int coordsTable[][][] = 
    { { { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },
        { { 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } },
        { { 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } },
        { { 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 } },
        { { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } },
        { { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } },
        { { -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } },
        { { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } } };

    private TetrixBoard.TetrixShape pieceShape;
    private int coords[][] = new int[4][2];

    public TetrixPiece()
    {
        setShape(TetrixBoard.TetrixShape.NoShape);
    }
    
    public TetrixPiece(TetrixPiece copy)
    {
    	pieceShape = copy.shape();
        for (int i=0; i<4; ++i) {
	    	setX(i, copy.x(i));
	    	setY(i, copy.y(i));
        }    	
    }
    
    public void setRandomShape()
    {        
        Random rand = new Random();
        int shapeint = rand.nextInt(7) + 1;

        TetrixBoard.TetrixShape shape = TetrixBoard.TetrixShape.NoShape;
        switch (shapeint) {
        case 1: shape = TetrixBoard.TetrixShape.ZShape; break ; 
        case 2: shape = TetrixBoard.TetrixShape.SShape; break ;
        case 3: shape = TetrixBoard.TetrixShape.LineShape; break ;
        case 4: shape = TetrixBoard.TetrixShape.TShape; break ; 
        case 5: shape = TetrixBoard.TetrixShape.SquareShape; break ;
        case 6: shape = TetrixBoard.TetrixShape.LShape; break ;
        case 7: shape = TetrixBoard.TetrixShape.MirroredLShape; break ;
        }

        setShape(shape);
    }

    public TetrixBoard.TetrixShape shape()
    {
        return pieceShape;
    }

    public void setShape(TetrixBoard.TetrixShape shape) 
    {
        for (int i=0; i<4; ++i) {
            for (int j=0; j<2; ++j) 
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
        }

        pieceShape = shape;
    }

    public int minX() 
    {
        int min = coords[0][0];
        for (int i=1; i<4; ++i)
            min = min < coords[i][0] ? min : coords[i][0];
        return min;
    }

    public int maxX()
    {
        int max = coords[0][0];
        for (int i=1; i<4; ++i) 
            max = max > coords[i][0] ? max : coords[i][0];
        return max;
    }

    public int minY() 
    {
        int min = coords[0][1];
        for (int i=1; i<4; ++i)
            min = min < coords[i][1] ? min : coords[i][1];
        return min;
    }

    public int maxY()
    {
        int max = coords[0][1];
        for (int i=1; i<4; ++i) 
            max = max > coords[i][1] ? max : coords[i][1];
        return max;
    }

    public TetrixPiece rotatedLeft()
    {
        if (pieceShape == TetrixBoard.TetrixShape.SquareShape)
            return this;

        TetrixPiece result = new TetrixPiece();
        result.pieceShape = pieceShape;
        for (int i=0; i<4; ++i) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }

        return result;
    }

    public TetrixPiece rotatedRight()
    {
        if (pieceShape == TetrixBoard.TetrixShape.SquareShape)
            return this;

        TetrixPiece result = new TetrixPiece();
        result.pieceShape = pieceShape;
        for (int i=0; i<4; ++i) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }

        return result;
    }

    public int x(int index) 
    {
        return coords[index][0];
    }

    public int y(int index)
    {
        return coords[index][1];
    }

    private void setX(int index, int x) 
    {
        coords[index][0] = x;
    }

    private void setY(int index, int y)
    {
        coords[index][1] = y;
    }
}

public class Tetrix extends QWidget
{
    public Tetrix()
    {
        super();

        board = new TetrixBoard(null);

        nextPieceLabel = new QLabel();
        nextPieceLabel.setFrameStyle(QFrame.Box | QFrame.Raised);
        nextPieceLabel.setAlignment(Qt.AlignCenter);
        board.setNextPieceLabel(nextPieceLabel);

        scoreLcd = new QLCDNumber(5);
        scoreLcd.setSegmentStyle(QLCDNumber.Filled);

        levelLcd = new QLCDNumber(2);
        levelLcd.setSegmentStyle(QLCDNumber.Filled);

        linesLcd = new QLCDNumber(5);
        linesLcd.setSegmentStyle(QLCDNumber.Filled);

        startButton = new QPushButton("&Start");
        startButton.setFocusPolicy(Qt.NoFocus);
        quitButton = new QPushButton("&Quit");
        quitButton.setFocusPolicy(Qt.NoFocus);
        pauseButton = new QPushButton("&Pause");
        pauseButton.setFocusPolicy(Qt.NoFocus);

        startButton.clicked.connect(board, "start()");                        
        quitButton.clicked.connect(this, "close()");
        pauseButton.clicked.connect(board, "pause()");
        board.scoreChanged.connect(scoreLcd, "display(int)");
        board.levelChanged.connect(levelLcd, "display(int)");
        board.linesRemovedChanged.connect(linesLcd, "display(int)");

        layout = new QGridLayout();
        layout.addWidget(createLabel("NEXT"), 0, 0);
        layout.addWidget(nextPieceLabel, 1, 0);
        layout.addWidget(createLabel("LEVEL"), 2, 0);
        layout.addWidget(levelLcd, 3, 0);
        layout.addWidget(startButton, 4, 0);
        layout.addWidget(board, 0, 1, 6, 1);
        layout.addWidget(createLabel("SCORE"), 0, 2);
        layout.addWidget(scoreLcd, 1, 2);
        layout.addWidget(createLabel("LINES REMOVED"), 2, 2);
        layout.addWidget(linesLcd, 3, 2);
        layout.addWidget(quitButton, 4, 2);
        layout.addWidget(pauseButton, 5, 2);

        setLayout(layout);

        setWindowTitle("Tetrix");
        setWindowIcon(new QIcon("classpath:com/trolltech/images/logo_32.png"));
        resize(550, 370);
    }

    private QLabel createLabel(String text)
    {
        QLabel lbl = new QLabel(text);
        lbl.setAlignment(Qt.AlignBottom);
        return lbl;
    }

    private TetrixBoard board = null;
    private QPushButton startButton = null;
    private QPushButton quitButton = null;
    private QPushButton pauseButton = null;
    private QLabel nextPieceLabel = null;
    private QLCDNumber scoreLcd = null;
    private QLCDNumber levelLcd = null;
    private QLCDNumber linesLcd = null;    
    private QGridLayout layout = null;


    public static void main(String args[]) 
    {
        QApplication.initialize(args);

        Tetrix window = new Tetrix();
        window.show();

        QApplication.exec();
    }

}
