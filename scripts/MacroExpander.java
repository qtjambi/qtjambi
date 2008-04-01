import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

public class MacroExpander {
	// should not hardcode this
	static String lisenceHeader =
		"** This file may be used under the terms of the GNU General Public\n"+
		"** License version 2.0 as published by the Free Software Foundation\n"+
		"** and appearing in the file LICENSE.GPL included in the packaging of\n"+
		"** this file.  Please review the following information to ensure GN\n"+
		"** General Public Licensing requirements will be met:\n"+
		"** http://www.trolltech.com/products/qt/opensource.html\n"+
		"**\n"+
		"** If you are unsure which license is appropriate for your use, please\n"+
		"** review the following information:\n"+
		"** http://www.trolltech.com/products/qt/licensing.html or contact the\n"+
		"** sales department at sales@trolltech.com.\n";

	public static String expandMacros(String rawHtml)
	{
		rawHtml = rawHtml.replaceAll("\\$THISYEAR\\$","2007");
		rawHtml = rawHtml.replaceAll("\\$TROLLTECH\\$", "Trolltech ASA");
	    rawHtml = rawHtml.replaceAll("\\$PRODUCT\\$", "Qt Jambi");
		rawHtml = rawHtml.replaceAll("\\$LICENSE\\$", lisenceHeader);
	    rawHtml = rawHtml.replaceAll("\\$JAVA_LICENSE\\$", lisenceHeader);
	    rawHtml = rawHtml.replaceAll("\\$CPP_LICENSE\\$", lisenceHeader);

		return rawHtml;
	}

	public static void doJarFile(File jarFile, File outFile) throws IOException
	{
		JarOutputStream jarOut = new JarOutputStream(
				new FileOutputStream(outFile));
		jarOut.setLevel(9);
		jarOut.setMethod(ZipOutputStream.DEFLATED);

		JarFile jar = new JarFile(jarFile);
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			InputStream in = jar.getInputStream(entry);

			byte buff[] = new byte[4096];
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			while(in.available() != 0) {
				int read = in.read(buff);
				bout.write(buff, 0, read);
			}
			byte buffer[] = bout.toByteArray();
			in.close();

			if (entry.getName().endsWith(".html")) {
				String expandedFile = expandMacros(new String(buffer));
				buffer = expandedFile.getBytes();
			}

			JarEntry entryOut = new JarEntry(entry.getName());
			jarOut.putNextEntry(entryOut);
			jarOut.write(buffer, 0, buffer.length);
			jarOut.closeEntry();
		}
		jarOut.close();
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: MacroExpander inJarFile outJarFile");
			System.exit(1);
		}

		MacroExpander.doJarFile(new File(args[0]), new File(args[1]));
		System.err.println("Finished");
	}

}
