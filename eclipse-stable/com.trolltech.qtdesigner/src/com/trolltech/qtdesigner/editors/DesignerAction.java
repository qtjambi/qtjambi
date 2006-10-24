package com.trolltech.qtdesigner.editors;

import org.eclipse.jface.action.Action;
import com.trolltech.qtdesigner.views.FormWindowW;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;

public class DesignerAction extends Action
{
    private DesignerActionBarContributor contributor;
    private int actId;
    private boolean tool;
    private boolean hasicon;
    private int lastevent = 0;
	
	public DesignerAction(DesignerActionBarContributor contributor, int actId, boolean tool)
	{
		this.actId = actId;
		this.contributor = contributor;
		this.tool = tool;
		hasicon = false;
		FormWindowW formwindow = contributor.activeEditor().formWindow();
		String strIcon;

		if(tool)
		{
			setText(formwindow.toolName(actId));
			if((strIcon = toolIconFile(actId)) != null)
			{
				hasicon = true;						
				try {
					setImageDescriptor(ImageDescriptor.createFromFile(
						getClass(), strIcon));
				} catch (Exception e) {
					System.out.println(e.toString());
				}
			}

			this.setToolTipText(formwindow.toolToolTip(actId));
			setEnabled(true);
		}
		else
		{
			setText(formwindow.actionName(actId));
			// don't use icons for the integrated ones
			if (actId > 6) {
				if((strIcon = actionIconFile(actId)) != null)
				{
					hasicon = true;						
					
					try {
						setImageDescriptor(ImageDescriptor.createFromFile(
							getClass(), strIcon));
					} catch (Exception e) {
						System.out.println(e.toString());
					}
				}
			}
			
			this.setToolTipText(formwindow.actionToolTip(actId));
			setEnabled(formwindow.isEnabled(actId));
		}
	}
	
	public void runWithEvent(Event event) {
		if (event.time != lastevent) {
			lastevent = event.time;			
			run();
		}
	}
	
	
	public void run()
	{
		FormWindowW formwindow = contributor.activeEditor().formWindow();
            		
		if (tool)
		{
			formwindow.setCurrentTool(actId);
			contributor.updateTools();
		}
		else
		{
	        formwindow.actionTrigger(actId);                       
		}
	}
	
	public boolean hasIcon()
	{
		return hasicon;
	}
	
	private String actionIconFile(int id)
	{
	    switch(id)
	    {
	    case 7:
	        return "actionicons/editlower.png";
	    case 8:
	        return "actionicons/editraise.png";
	    case 9:
	        return "actionicons/edithlayout.png";
	    case 10:
	        return "actionicons/editvlayout.png";
	    case 11:
	        return "actionicons/edithlayoutsplit.png";
	    case 12:
	        return "actionicons/editvlayoutsplit.png";
	    case 13:
	        return "actionicons/editgrid.png";
	    case 14:
	        return "actionicons/editbreaklayout.png";
	    case 15:
	        return "actionicons/adjustsize.png";
	    case 16:
	        return "actionicons/resourceeditortool.png";
	    }
	    
	    return null;
	}

	private String toolIconFile(int id)
	{
	    switch(id)
	    {
	    case 0:
	        return "actionicons/widgettool.png";
	    case 1:
	        return "actionicons/signalslottool.png";
	    case 2:
	        return "actionicons/buddytool.png";
	    case 3:
	        return "actionicons/tabordertool.png";
	    }
	    
	    return null;
	}
}
