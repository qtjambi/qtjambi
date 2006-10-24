package com.trolltech.qtproject;

import java.util.HashMap;
import java.util.Map;

public class ProEditorModelManager {
	private Map m_models;
	
	ProEditorModelManager() {
		m_models = new HashMap();
	}
	
	public boolean hasModel(String filename) {
		return m_models.containsKey(filename);
	}

	public int getModelHandle(String filename) {
		Integer handle = (Integer)(m_models.get(filename));
		if (handle == null)
			return 0;
		
		return handle.intValue();
	}
	
	public void registerModelHandle(String filename, int handle) {
		if (handle != 0)
			m_models.put(filename, new Integer(handle));		
	}
	
	public void unregisterModelHandle(String filename) {
		m_models.remove(filename);
	}
}
