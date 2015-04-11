package com.yannicklerestif.metapojos.plugin.console;

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

import com.yannicklerestif.metapojos.model.elements.beans.CallBean;
import com.yannicklerestif.metapojos.model.elements.beans.ClassBean;
import com.yannicklerestif.metapojos.model.elements.beans.JavaElementBean;
import com.yannicklerestif.metapojos.model.elements.beans.MethodBean;
import com.yannicklerestif.metapojos.plugin.MetaPojosConsole;
import com.yannicklerestif.metapojos.plugin.MetaPojosPluginImpl;

//TODO use a process console (allows process informations + hyperlinks in case of an exception)
public class MetaPojosConsoleImpl implements MetaPojosConsole {

	// console interface -------------------------------------------------------------------

	@Override
	public void printJavaElementBean(JavaElementBean bean) {
		if (bean instanceof ClassBean) {
			print("Class ");
			printHyperlink(((ClassBean) bean).getPrettyName(), bean);
		} else if (bean instanceof MethodBean) {
			MethodBean methodBean = (MethodBean) bean;
			print("Method ");
			if (methodBean.isShallow())
				print(methodBean.getPrettyName() + " [shallow]");
			else
				printHyperlink(methodBean.getPrettyName(), methodBean);
		} else if (bean instanceof CallBean) {
			CallBean callBean = (CallBean) bean;
			print("Call from ");
			printHyperlink(callBean.getSource().getPrettyName() + "(l:" + callBean.getLine() + ")", callBean);
			print(" to ");
			MethodBean target = callBean.getTarget();
			if (target.isShallow())
				print(target.getPrettyName() + " [shallow]");
			else
				printHyperlink(target.getPrettyName(), target);
		}
		println();

	}

	@Override
	public void printHyperlink(Object message, JavaElementBean bean) {
		String toPrint = message == null ? "null" : message.toString();
		listener.addHyperLink(toPrint, new MetaPojosConsoleHyperlink(plugin.getWorkspace(), bean));
		out.print(toPrint);
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

	@Override
	public void clear() {
		listener.clear();
		console.clearConsole();
	}

	//-----------------------------------------------------------------------------------

	public static final String META_POJOS_CONSOLE_NAME = "Meta Pojos";

	public static MetaPojosConsoleImpl createConsole(MetaPojosPluginImpl plugin) {
		ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = consolePlugin.getConsoleManager();
		MessageConsole messageConsole = new MessageConsole(META_POJOS_CONSOLE_NAME, null);
		MessageConsoleStream metaPojosConsoleOutputStream = messageConsole.newMessageStream();
		metaPojosConsoleOutputStream.setActivateOnWrite(true);
		MetaPojosConsoleImpl metaPojosConsole = new MetaPojosConsoleImpl(plugin, metaPojosConsoleOutputStream);
		conMan.addConsoles(new IConsole[] { messageConsole });
		return metaPojosConsole;
	}

	public MessageConsoleStream out = null;

	public MessageConsole console = null;

	private MetaPojosPatternMatchListener listener;

	private MetaPojosPluginImpl plugin;

	public MetaPojosConsoleImpl(MetaPojosPluginImpl plugin, MessageConsoleStream metaPojosConsoleOutputStream) {
		this.plugin = plugin;
		this.out = metaPojosConsoleOutputStream;
		this.console = metaPojosConsoleOutputStream.getConsole();
		this.listener = new MetaPojosPatternMatchListener();
		console.addPatternMatchListener(listener);
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

		public void clear() {
			hyperLinks.clear();
		}

		public void addHyperLink(String text, IHyperlink link) {
			if (text.length() == 0)
				return;
			if (text.contains("\n")) {
				String[] split = text.split("\n");
				for (int i = 0; i < split.length; i++) {
					String string = split[i];
					addHyperLink(string, link);
				}
				return;
			}
			//System.out.println("----- adding : ---|" + text + "|--- link : [" + link + "]");
			hyperLinks.add(new MetaPojosHyperLink(text, link));
		}

		@Override
		public void matchFound(PatternMatchEvent event) {
			try {
				int offset = event.getOffset();
				int length = event.getLength();
				String text = console.getDocument().get(offset, length);
				//System.out.println("----- match found : ---|" + text + "|---");
				do {
					MetaPojosHyperLink nextLink = hyperLinks.poll();
					if (nextLink == null) {
						System.err.println("Hyperlinks queue is empty. won't handle this text : " + text);
						break;
					}
					String hyperLinkText = nextLink.hyperLinkText;

					//something wrong happened
					if (!text.startsWith(hyperLinkText)) {
						System.out.println("expected : " + hyperLinkText + " - got : " + text);
						hyperLinks.clear();
						break;
					}

					int hyperLinkTextLength = hyperLinkText.length();
					if (nextLink.link != null)
						console.addHyperlink(nextLink.link, offset, hyperLinkTextLength);
					text = text.substring(hyperLinkTextLength);
					offset += hyperLinkTextLength;
				} while (text.length() > 0);
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
