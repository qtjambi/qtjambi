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
import com.trolltech.qt.svg.*;
import java.util.*;

public class SvgCards extends QGraphicsView {
    private static String[] CARDS = {
        "black_joker",
        "red_joker",
        "back",
        "king_club",
        "king_diamond",
        "king_heart",
        "king_spade",
        "queen_club",
        "queen_diamond",
        "queen_heart",
        "queen_spade",
        "jack_club",
        "jack_diamond",
        "jack_heart",
        "jack_spade",
        "1_club",
        "1_diamond",
        "1_heart",
        "1_spade",
        "2_club",
        "2_diamond",
        "2_heart",
        "2_spade",
        "3_club",
        "3_diamond",
        "3_heart",
        "3_spade",
        "4_club",
        "4_diamond",
        "4_heart",
        "4_spade",
        "5_club",
        "5_diamond",
        "5_heart",
        "5_spade",
        "6_club",
        "6_diamond",
        "6_heart",
        "6_spade",
        "7_club",
        "7_diamond",
        "7_heart",
        "7_spade",
        "8_club",
        "8_diamond",
        "8_heart",
        "8_spade",
        "9_club",
        "9_diamond",
        "9_heart",
        "9_spade",
        "10_club",
        "10_diamond",
        "10_heart",
        "10_spade"
    };

    private static class Card extends QGraphicsSvgItem {
        private double opacity = 1.0;
        private CardManager manager;

        public Card(String card, QSvgRenderer renderer) {
            super();
            setElementId(card);
            setSharedRenderer(renderer);
        }

        public void setManager(CardManager newManager) {
            manager = newManager;
        }

        public String cardName() {
            return elementId();
        }

        public void mousePressEvent(QGraphicsSceneMouseEvent event) {
            setZValue(10);
            opacity = 0.7;
            manager.startedMove(this);
        }

        public void mouseReleaseEvent(QGraphicsSceneMouseEvent event) {
            opacity = 1.0;
            setZValue(5);
            manager.stoppedMove(this);
        }

        public void paint(QPainter painter, QStyleOptionGraphicsItem option, 
                          QWidget widget) {
            painter.setOpacity(opacity);
            super.paint(painter, option, widget);
        }
    }

    private static class CardBox extends QGraphicsItem {
        private QRectF rectangle;
        private String status = "Loading Cards...";

        public CardBox() {
            super();
            rectangle = new QRectF(0, 0, 200, 50);
        }

        public QRectF boundingRect() {
            return rectangle;
        }

        public void paint(QPainter painter, QStyleOptionGraphicsItem option, 
                          QWidget widget) {

            painter.setRenderHint(QPainter.RenderHint.Antialiasing);
            painter.setPen(new QPen(QColor.black));
            QLinearGradient gradient = new QLinearGradient(rectangle.topLeft(),
                                                           rectangle.bottomLeft());
            gradient.setColorAt(0, new QColor(0, 0, 0, 127));
            gradient.setColorAt(1, new QColor(255, 255, 255, 127));
            painter.setBrush(new QBrush(gradient));
            painter.drawRoundRect(rectangle, (int) (25 * rectangle.height() 
                                                    / rectangle.width()), 25);
            painter.save();

            QFont font = painter.font();
            font.setBold(true);
            font.setUnderline(true);
            painter.setFont(font);
            painter.drawText(new QPointF(rectangle.x() + 10, rectangle.y() + 20),
                             "Svg Cards");

            painter.restore();

            painter.drawText(new QPointF(rectangle.x() + 20, rectangle.y() + 40), 
                             status);
        }

        void cardMoving(String card) {
            status = "Moving : " + card;
        }

        void cardStopped(String card) {
            status = "Dropped : " + card;
        }

        void cardInfo(String info) {
            status = info;
        }
    }

    private static class CardDeck extends QObject {
        private QSvgRenderer renderer;
        private String fileName;
        private List<Card> cards;

        public CardDeck(String file, QObject parent) {
            super(parent);

            fileName = file;
            renderer = new QSvgRenderer(fileName);
            cards = new LinkedList<Card>();
            for (int i = 0; i < CARDS.length; ++i) {
                Card item = new Card(CARDS[i], renderer);
                cards.add(item);
            }

        }

        public List<Card> cards() {
            return cards;
        }
    }

    private static class CardManager extends QObject {
        private CardBox box;

        public void setBox(CardBox newBox) {
            box = newBox;
        }

        public void startedMove(Card card) {
            box.cardMoving(card.cardName());
            box.update(box.boundingRect());
        }

        public void stoppedMove(Card card) {
            box.cardStopped(card.cardName());
            box.update(box.boundingRect());
        }

        public void setOperation(String operation) {
            box.cardInfo(operation);
            box.update(box.boundingRect());
        }
    }

    private CardDeck deck;
    private CardManager manager;
    private Random random;
    private int cardsToLoad = 13;
    private int totalCards = cardsToLoad;
    private int x = 100;
    private int y = 100;
    private QGraphicsScene scene;

    public final void loadCards() {
        if (cardsToLoad != 0) {
            addCard(random.nextInt(50));
            --cardsToLoad;
            if (cardsToLoad != 0) {
                double percent = (totalCards - cardsToLoad) 
                                 / (double) totalCards * 100.0;
                manager.setOperation("Loading Cards : " + (int) percent + "% ");
            } else {
                manager.setOperation("Click on a Card");
	    }
            viewport().update();
            QApplication.invokeLater(new Runnable() { 
                    public void run() { loadCards(); } });
        }
    }

    public SvgCards() {
        scene = new QGraphicsScene(this);
        setScene(scene);

        deck = new CardDeck("classpath:com/trolltech/images/svg-cards.svg", 
                            this);
        manager = new CardManager();
        random = new Random();

        CardBox box = new CardBox();
        box.setPos(sceneRect().width() - box.boundingRect().width() - 10, 
                   sceneRect().height()- box.boundingRect().height() - 10);
        box.setZValue(99);
        scene.addItem(box);
        manager.setBox(box);

        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
        setWindowTitle("SVG Cards Example");

        QLinearGradient grad = new QLinearGradient(0, 0, 800, 600);
        grad.setColorAt(0, QColor.fromRgbF(0.5, 0.5, 0.7));
        grad.setColorAt(1, QColor.fromRgbF(1, 1, 1));
        setRenderHint(QPainter.RenderHint.Antialiasing);
        setBackgroundBrush(new QBrush(grad));
        setFrameStyle(QFrame.Shape.NoFrame.value());

        QPixmapCache.setCacheLimit(5 * 1024);

        QApplication.invokeLater(new Runnable() { 
                    public void run() { loadCards(); } });
    }

    public QSize sizeHint() {
        return new QSize(800, 600);
    }

    private final void addCard(int i) {
        Card item = deck.cards().get(i);
        while (item.scene() != null) {
            item = deck.cards().get(random.nextInt(50));
        }

        item.rotate(0 + 180.0 * random.nextDouble());
        double scaleF = 0.5 + 0.9 * random.nextDouble();
        item.scale(scaleF, scaleF);
        x += 80;
        if (x >= 650) {
            x = 100;
            y += 100;
        }
        item.setPos(x, y);
        item.setFlag(QGraphicsItem.GraphicsItemFlag.ItemIsMovable, true);
        item.setManager(manager);
        scene().addItem(item);
    }

    public static void main(String args[]) {
        QApplication.initialize(args);

        SvgCards view = new SvgCards();
        view.show();

        QApplication.exec();
    }

    // REMOVE-START

    public static String exampleName() {
        return "SVG Cards Example";
    }

    public static boolean canInstantiate() {
        return System.getProperty("com.trolltech.launcher.webstart") == null;
    }

    // REMOVE-END
}
