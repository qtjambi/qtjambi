/*   Ported from: src.svg.qgraphicssvgitem.cpp
<snip>
//! [0]
    QSvgRenderer *renderer = new QSvgRenderer(QLatin1String("SvgCardDeck.svg"));
    QGraphicsSvgItem *black = new QGraphicsSvgItem();
    QGraphicsSvgItem *red   = new QGraphicsSvgItem();

    black->setSharedRenderer(renderer);
    black->setElementId(QLatin1String("black_joker"));

    red->setSharedRenderer(renderer);
    red->setElementId(QLatin1String("red_joker"));
//! [0]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_svg_qgraphicssvgitem {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    QSvgRenderer enderer = new QSvgRenderer(QLatin1String("SvgCardDeck.svg"));
    QGraphicsSvgItem lack = new QGraphicsSvgItem();
    QGraphicsSvgItem ed   = new QGraphicsSvgItem();

    black.setSharedRenderer(renderer);
    black.setElementId(QLatin1String("black_joker"));

    red.setSharedRenderer(renderer);
    red.setElementId(QLatin1String("red_joker"));
//! [0]


    }
}
