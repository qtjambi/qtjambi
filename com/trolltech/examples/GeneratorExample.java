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

import java.util.List;

import com.trolltech.examples.generator.AbstractGameObjectInterface;
import com.trolltech.examples.generator.Game;
import com.trolltech.examples.generator.GameAction;
import com.trolltech.examples.generator.GameAnimation;
import com.trolltech.examples.generator.GameObject;
import com.trolltech.examples.generator.GameScene;
import com.trolltech.examples.generator.Point3D;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QImage;
import com.trolltech.qt.gui.QPainterPath;

class InventoryAction extends GameAction
{
    @Override
    public GameAction clone() {
        return new InventoryAction();
    }

    @Override
    public boolean perform(GameScene scene) {
        if (objects().isEmpty()) {
            List<AbstractGameObjectInterface> inventory = scene.egoInventory();
            
            if (inventory.isEmpty()) {
                scene.message("Your pockets are empty");
            } else {
                String msg = "You are carrying:\n";
                for (AbstractGameObjectInterface gameObject : inventory) {
                    if (gameObject instanceof GameObject) {
                        msg += ((GameObject) gameObject).description() + "\n";
                    }
                }       
                
                scene.message(msg);
            }
            
            return true;
        } else {
            return false;
        }
    }

    public InventoryAction() {
        super(Game.ActionType.resolve(Game.ActionType.UserAction.value()));               
    }
}

class ChickenObject extends GameObject {
    
    public ChickenObject(GameScene scene, String name) {
        super(scene, name);
        
        used.connect(this, "youCantUseTheChicken()");
    }
               
    @SuppressWarnings("unused")
    private void youCantUseTheChicken() {
        gameScene().message("You can't use the rubber chicken for anything");
    }        
}

@QtJambiExample(name = "Generator Example")
public class GeneratorExample {
    
    private static final String resourcesLocation = "classpath:com/trolltech/examples/generator/images/"; 
       
    public GeneratorExample()
    {               
        GameScene scene = new GameScene(null);
        int w = 0; int h = 0;
        
        // Set up scene
        {
            scene.setCacheMode(QGraphicsView.CacheModeFlag.CacheBackground);
            scene.setDescription("You are standing next to a restaurant and it is sunny outside.");
            
            QImage img = new QImage(resourcesLocation + "background.png");
            scene.setBackground(img);
            
            scene.setHorizon(250.0);
            w = img.width(); h = img.height();
            scene.setSceneRect(new QRectF(0.0, 0.0, w, h));
        }
        
        // Player avatar
        scene.setEgoObject(makeEgo(scene, w, h));
        
        // Inventory command
        scene.grammar().addVerb("inventory", new InventoryAction());
        scene.grammar().addVerb("inv", new InventoryAction());
               
        
        // Make boundary
        {
            QPainterPath path = new QPainterPath(new QPointF(0, 305));
            path.lineTo(new QPointF(306, 305));
            path.lineTo(new QPointF(326, 293));
            path.lineTo(new QPointF(357, 293));
            path.lineTo(new QPointF(378, 305));
            path.lineTo(new QPointF(635, 305));
            path.lineTo(new QPointF(635, 200));
            path.lineTo(new QPointF(0, 200));
            path.closeSubpath();
            
            GameObject boundary = new GameObject(scene, "boundary");
            boundary.setVisible(false);
            boundary.setShape(path);                        
            boundary.setFlags(Game.ObjectFlag.Blocking);
         

            scene.addGameObject(boundary);
        }
        
        // Make chicken
        {
            GameObject chicken = new ChickenObject(scene, "a rubber chicken with a pulley in the middle");
            
            chicken.setDescription("It's a rubber chicken with a pulley in the middle.");
            chicken.addName("rubber chicken");
            chicken.addName("chicken");
            chicken.addName("rubber chicken with a pulley in the middle");
            chicken.addName("rubber chicken with pulley");
            chicken.addName("rubber chicken with pulley in the middle");
            chicken.addName("rubber chicken with pulley in middle");
            chicken.addName("chicken with a pulley in the middle");
            chicken.addName("chicken with pulley");
            chicken.addName("chicken with pulley in the middle");
            chicken.addName("chicken with pulley in middle");
            chicken.setVisible(true);
            chicken.setPosition(new Point3D(100.0, 330.0, 0.0));
            chicken.setFlags(Game.ObjectFlag.Blocking, Game.ObjectFlag.CanPickUp);

            GameAnimation a = new GameAnimation(Game.AnimationType.NoAnimation);
            a.addFrame(new QImage(resourcesLocation + "chicken.png"));
            chicken.setAnimation(a);

            scene.addGameObject(chicken);
        }
     
        scene.setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
        scene.setWindowTitle(scene.tr("Generator Example"));
        
        scene.message("Press any letter to write a command and enter when you are done. Use the arrow keys to move around." 
                     +" Hit enter when you are done reading this message.");
        scene.show();        
    }
    
    private GameAnimation makeAnimation(Game.AnimationType type, String nameTemplate, int startIdx, int endIdx) {
        GameAnimation a = new GameAnimation(type);
        
        a.setSpeed(100);
        a.setLooping(true);
        
        for (int i=startIdx; i<=endIdx; ++i) 
            a.addFrame(new QImage(resourcesLocation + nameTemplate.replace("#", new Integer(i).toString())));
        
        return a;
    }
    
    private GameObject makeEgo(GameScene scene, int w, int h) {
        GameObject ego = new GameObject(scene);
        
        ego.setPosition(new Point3D(w / 2.0, 350.0, 0.0));
        ego.setVisible(true);
        
        ego.setAnimation(makeAnimation(Game.AnimationType.WalkingHorizontally, "walk#.png", 1, 4));
        ego.setAnimation(makeAnimation(Game.AnimationType.StandingStill, "walk#.png", 2, 2));
        ego.setAnimation(makeAnimation(Game.AnimationType.WalkingFromScreen, "walkaway#.png", 1, 2));
        ego.setAnimation(makeAnimation(Game.AnimationType.WalkingToScreen, "walktowards#.png", 1, 2));
        
        return ego;
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        QApplication.initialize(args);               
        GeneratorExample ex = new GeneratorExample();        
        QApplication.exec();
        
    }

}
