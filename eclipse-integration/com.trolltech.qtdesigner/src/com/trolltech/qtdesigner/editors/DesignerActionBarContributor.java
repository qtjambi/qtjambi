package com.trolltech.qtdesigner.editors;

import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionFactory;

import java.util.ArrayList;
import com.trolltech.qtdesigner.views.FormWindowW;
import org.eclipse.jface.action.Separator;

public class DesignerActionBarContributor implements IEditorActionBarContributor
{
	private ArrayList actions = null;
	private ArrayList tools = null;
	private IActionBars actionbars;
	private IWorkbenchPage workbenchpage;
	private UiEditor editor;
	
	public void setActiveEditor(IEditorPart targetEditor)
	{
		if (targetEditor instanceof UiEditor)
		{
			editor = ((UiEditor)targetEditor);
			
			if (editor.formWindow() != null)
			{
				editor.formWindow().setActiveFormWindow();
				if (actions == null)
					setupActions();
				
				updateTools();
			}
		}
		
		
	}
	
	public UiEditor activeEditor()
	{
		return editor;		
	}
	
	public void init(IActionBars bars, IWorkbenchPage page)
	{
		actionbars = bars;
		workbenchpage = page;
	}
	
	public void dispose()
	{
	}
	
	private void setupActions()
	{
		actions = new ArrayList();
		IToolBarManager toolbar = actionbars.getToolBarManager();
		IMenuManager menubar = actionbars.getMenuManager();
		FormWindowW formwindow = activeEditor().formWindow();
	
		//the first 7 actions are hard coded because they are integrated
		DesignerAction act = new DesignerAction(this, 0, false);
		actionbars.setGlobalActionHandler(ActionFactory.CUT.getId(), act);
		actionbars.setGlobalActionHandler(IWorkbenchActionConstants.CUT_EXT, act);
		actions.add(act);

		act = new DesignerAction(this, 1, false);
		actionbars.setGlobalActionHandler(ActionFactory.COPY.getId(), act);
		actions.add(act);

		act = new DesignerAction(this, 2, false);
		actionbars.setGlobalActionHandler(ActionFactory.PASTE.getId(), act);
		actions.add(act);

		act = new DesignerAction(this, 3, false);
		actionbars.setGlobalActionHandler(ActionFactory.DELETE.getId(), act);
		actions.add(act);

		act = new DesignerAction(this, 4, false);
		actionbars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), act);
		actions.add(act);
		
		act = new DesignerAction(this, 5, false);
		actionbars.setGlobalActionHandler(ActionFactory.UNDO.getId(), act);
		actionbars.setGlobalActionHandler(IWorkbenchActionConstants.UNDO_EXT, act);
		actions.add(act);

		act = new DesignerAction(this, 6, false);
		actionbars.setGlobalActionHandler(ActionFactory.REDO.getId(), act);
		actions.add(act);
		
		MenuManager qtmenu = new MenuManager("Qt");
		menubar.insertBefore(IWorkbenchActionConstants.M_WINDOW, (qtmenu));
		
		// the rest of the actions are dynamic
		for(int actId=7; actId<formwindow.actionCount(); actId++)
		{
			act = new DesignerAction(this, actId, false);
			actions.add(act);
			if (act.hasIcon())
				toolbar.add(act);
			qtmenu.add(act);
		}
		
		tools = new ArrayList();
		toolbar.add(new Separator());
		
		MenuManager qteditmenu = new MenuManager("Editor");
		qtmenu.add(qteditmenu);

		// the tools
		for (int toolId=0; toolId<formwindow.toolCount(); toolId++)
		{
			act = new DesignerAction(this, toolId, true);
			tools.add(act);
			if (act.hasIcon())
				toolbar.add(act);
			qteditmenu.add(act);
		}
	}
	
	public IActionBars actionBars()
	{
        return actionbars;
	}
	
	public void updateAction(int actId)
	{
		if (actions != null)
		{
			DesignerAction act = (DesignerAction)actions.get(actId);
			FormWindowW formwindow = activeEditor().formWindow();

			String name = formwindow.actionName(actId);
			if (name != act.getText())
				act.setText(name);
			act.setEnabled(formwindow.isEnabled(actId));
		}
	}
	
	public void updateTools()
	{
		FormWindowW formwindow = activeEditor().formWindow();
		
		for (int toolId=0; toolId<tools.size(); toolId++)
		{
			if(formwindow.currentTool() == toolId)
				((DesignerAction)tools.get(toolId)).setChecked(true);
			else
				((DesignerAction)tools.get(toolId)).setChecked(false);
		}
	}
}
