package com.yannicklerestif.metapojos.plugin;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

//TODO clear console when executing a new query
//TODO output hyperlinks
public class MetaPojosConsole implements Console {

	public static final String META_POJOS_CONSOLE_NAME = "Meta Pojos";

	public static MetaPojosConsole createConsole() {
		ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = consolePlugin.getConsoleManager();
		MessageConsole messageConsole = new MessageConsole(META_POJOS_CONSOLE_NAME, null);
		MessageConsoleStream metaPojosConsoleOutputStream = messageConsole.newMessageStream();
		metaPojosConsoleOutputStream.setActivateOnWrite(true);
		MetaPojosConsole metaPojosConsole = new MetaPojosConsole(metaPojosConsoleOutputStream);
		conMan.addConsoles(new IConsole[] { messageConsole });
		return metaPojosConsole;
	}
	
	public MessageConsoleStream out = null;

	public MessageConsole console = null;

	private MetaPojosPatternMatchListener listener;

	public MetaPojosConsole(MessageConsoleStream metaPojosConsoleOutputStream) {
		this.out = metaPojosConsoleOutputStream;
		this.console = metaPojosConsoleOutputStream.getConsole();
		this.listener = new MetaPojosPatternMatchListener();
		console.addPatternMatchListener(listener);
	}

	public void println(Object message) {
		String toPrint = message == null ? "null" : message.toString();
		listener.addHyperLink(toPrint, null);
		out.println(toPrint);
	}

	public void println() {
		out.println();
	}

	public void print(Object message) {
		String toPrint = message == null ? "null" : message.toString();
		listener.addHyperLink(toPrint, null);
		out.print(toPrint);
	}

	public void printHyperLink(String text, IHyperlink link) {
		listener.addHyperLink(text, link);
		out.print(text);
	}
	
	public void closeIfNecessary() throws IOException {
		if (out.isClosed())
			System.out.println("console stream is already closed.");
		else
			out.close();
	}
	
	public static class MetaPojosHyperLink {
		
		public String hyperLinkText;
		
		public IHyperlink link;
		
		public MetaPojosHyperLink(String hyperLinkText, IHyperlink link) {
			super();
			this.hyperLinkText = hyperLinkText;
			this.link = link;
		}
	}
	
	public class MetaPojosPatternMatchListener implements IPatternMatchListener {
		
		private Queue<MetaPojosHyperLink> hyperLinks = new ConcurrentLinkedQueue<>();
		
		public void addHyperLink(String text, IHyperlink link) {
			hyperLinks.add(new MetaPojosHyperLink(text, link));
		}
		
		@Override
		public void matchFound(PatternMatchEvent event) {
			try {
				int offset = event.getOffset();
				int length = event.getLength();
				String text = console.getDocument().get(offset, length);
				System.out.println("match found => " + text);
				while(true) {
					MetaPojosHyperLink nextLink = hyperLinks.poll();
					if(nextLink == null)
						break;
					String hyperLinkText = nextLink.hyperLinkText;
					
					//something wrong happened
					if(!text.startsWith(hyperLinkText)) {
						System.out.println("expected : " + hyperLinkText + " - got : " + text);
						hyperLinks.clear();
					}
					int length2 = hyperLinkText.length();
					if(nextLink.link != null)
						console.addHyperlink(nextLink.link, offset, length2);
					text = text.substring(length2);
					offset += length2;
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void disconnect() {
		}
		
		@Override
		public void connect(TextConsole console) {
		}
		
		@Override
		public String getPattern() {
			return "..*";
		}
		
		@Override
		public String getLineQualifier() {
			return null;
		}
		
		@Override
		public int getCompilerFlags() {
			return 0;
		}
	}

}
