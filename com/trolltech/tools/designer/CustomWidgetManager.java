package com.trolltech.tools.designer;

import java.util.*;

import com.trolltech.examples.*;
import com.trolltech.qt.gui.*;

/**
 * The CustomWidgetManager class is used by the designer custom widget plugin to
 * load Java Widgets and expose them to designer.
 * 
 * @author gunnar
 */
public class CustomWidgetManager {
    
    private CustomWidgetManager() {
        try {
            Class cl = Class.forName("com.trolltech.tests.CustomWidgetTester");
            CustomWidget tester = new CustomWidget(cl);
            tester.setName("Test Widget");
            tester.setGroup("Qt Jambi");
            customWidgets.add(tester);
        } catch (Exception e) {
            System.out.println("Failed to load com.trolltech.tests.CustomWidgetTester, is it compiled?");
        }

        try {
            CustomWidget analogClock = new CustomWidget(AnalogClock.class);
            analogClock.setName("Analog Clock");
            analogClock.setGroup("Qt Jambi");
            customWidgets.add(analogClock);
            
            CustomWidget collidingMice = new CustomWidget(CollidingMice.class);
            collidingMice.setName("Colliding Mice");
            collidingMice.setGroup("Qt Jambi");
            customWidgets.add(collidingMice);

            CustomWidget lineEdits = new CustomWidget(LineEdits.class);
            lineEdits.setName("Line Edits");
            lineEdits.setGroup("Qt Jambi");
            
            QPixmap pm = new QPixmap(32, 32);
            pm.fill(QColor.transparent);
            
            QPainter p = new QPainter();
            p.begin(pm);
            p.drawEllipse(0, 0, 31, 31);
            p.end();
            
            analogClock.setIcon(new QIcon(pm));
            
            customWidgets.add(lineEdits);            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static CustomWidgetManager instance() { return instance; }
    
    public List<CustomWidget> customWidgets() {
        return customWidgets;
    }
    
    private List<CustomWidget> customWidgets = new ArrayList<CustomWidget>();
    
    private static CustomWidgetManager instance = new CustomWidgetManager();
    
    public static void main(String[] args) {
        QApplication.initialize(args);
        
        List<CustomWidget> list = instance().customWidgets();
        for (CustomWidget w : list)
            w.createWidget(null).show();
        
        QApplication.exec();
    }

}
