/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

/* 
 * example snippet: Hello World
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class QtExample {
	public static Shell shell;
	
	public static void main (String [] args) {
		Display display = new Display ();
		shell = new Shell (display);
		Button ok = new Button (shell, SWT.PUSH);
		ok.setText ("I'm a SWT button!");
		
		QtWindow qtwidget = new QtWindow(shell, SWT.EMBEDDED);
		qtwidget.addQtWindowListener(new QtWindowListener() {
			public void messageBoxClosed()
			{
				MessageBox msg = new MessageBox(shell);
				msg.setMessage("Got the messageBoxClosed signal from Qt!");
				msg.setText("SWT Message Box");
				msg.open();
			}
		});
		
		shell.setLayout (new FillLayout());
		shell.pack ();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}