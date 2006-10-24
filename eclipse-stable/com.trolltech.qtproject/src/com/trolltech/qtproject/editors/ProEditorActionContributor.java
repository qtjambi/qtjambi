package com.trolltech.qtproject.editors;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionFactory;

class ProEditorAction extends Action
{
	private ProEditorActionContributor m_cont;
	private int m_id;
	private int m_lastevent;
	
	public ProEditorAction(ProEditorActionContributor cont, int id) {
		super();
		m_cont = cont;
		m_id = id;
		m_lastevent = 0;
		setEnabled(m_cont.activeEditor().getDetailsView().isActionEnabled(m_id));
	}
	
	public void runWithEvent(Event event) {
		// workaround for getting an event two times when using shortcuts.
		// if the event has the same timestamp, something is wrong :)
		if (event.time != m_lastevent) {
			m_lastevent = event.time;
			run();
		}
	}
	
	public void run() {
		m_cont.activeEditor().getDetailsView().triggerAction(m_id);
	}
}

public class ProEditorActionContributor implements IEditorActionBarContributor
{
	private ArrayList actions = null;
	private IActionBars actionbars;
	private ProEditor editor;
	
	public void setActiveEditor(IEditorPart targetEditor)
	{
		if (targetEditor instanceof ProEditor)
		{
			editor = ((ProEditor)targetEditor);
			if (actions == null)
				setupActions();
		}
	}
	
	public ProEditor activeEditor()
	{
		return editor;		
	}
	
	public void init(IActionBars bars, IWorkbenchPage page)
	{
		actionbars = bars;
	}
	
	public void dispose()
	{
	}
	
	private void setupActions()
	{
		actions = new ArrayList();
	
		// the order of the actions in the actions list must be the
		// same as in the qtproparser
		ProEditorAction act = new ProEditorAction(this, 0);
		actionbars.setGlobalActionHandler(ActionFactory.CUT.getId(), act);
		actions.add(act);

		act = new ProEditorAction(this, 1);
		actionbars.setGlobalActionHandler(ActionFactory.COPY.getId(), act);
		actions.add(act);

		act = new ProEditorAction(this, 2);
		actionbars.setGlobalActionHandler(ActionFactory.PASTE.getId(), act);
		actions.add(act);

		act = new ProEditorAction(this, 3);
		actionbars.setGlobalActionHandler(ActionFactory.UNDO.getId(), act);
		actions.add(act);

		act = new ProEditorAction(this, 4);
		actionbars.setGlobalActionHandler(ActionFactory.REDO.getId(), act);
		actions.add(act);
	}
	
	public IActionBars actionBars()
	{
        return actionbars;
	}
	
	public void updateAction(int actId)
	{
		if (actions != null) {
			((ProEditorAction)actions.get(actId)).
				setEnabled(editor.getDetailsView().isActionEnabled(actId));
		}
	}	
}

