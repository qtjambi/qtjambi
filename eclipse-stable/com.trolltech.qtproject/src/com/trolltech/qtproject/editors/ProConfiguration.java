package com.trolltech.qtproject.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.trolltech.qtproject.QtProConstants;

public class ProConfiguration extends SourceViewerConfiguration
{
	private ProScanner scanner = null;
	private ColorManager manager = null;

	private class SingleTokenScanner  extends BufferedRuleBasedScanner
	{
		public SingleTokenScanner(TextAttribute attribute)
	    {
			setDefaultReturnToken(new Token(attribute));
	    }
	}
	
	public ProConfiguration()
	{
		super();
		manager = new ColorManager();
	}
	
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { ProPartitionScanner.PRO_COMMENT,
				IDocument.DEFAULT_CONTENT_TYPE, ProPartitionScanner.PRO_FILES,
				ProPartitionScanner.PRO_SETTINGS};
	}
	
	protected ProScanner getProScanner() {
		if (scanner == null) {
			scanner = new ProScanner(manager);
		}
		return scanner;
	}
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
	{
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getProScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		
		dr = new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(manager.getColor(QtProConstants.PRO_SETTINGS_COLOR))));
		reconciler.setDamager(dr, ProPartitionScanner.PRO_SETTINGS);
		reconciler.setRepairer(dr, ProPartitionScanner.PRO_SETTINGS);
		
		dr = new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(manager.getColor(QtProConstants.PRO_SOURCE_COLOR))));
		reconciler.setDamager(dr, ProPartitionScanner.PRO_FILES);
		reconciler.setRepairer(dr, ProPartitionScanner.PRO_FILES);
		
		dr = new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(manager.getColor(QtProConstants.PRO_COMMENT_COLOR))));
		reconciler.setDamager(dr, ProPartitionScanner.PRO_COMMENT);
		reconciler.setRepairer(dr, ProPartitionScanner.PRO_COMMENT);
		
		return reconciler;
	}
	
}
