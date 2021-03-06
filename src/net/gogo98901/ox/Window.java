package net.gogo98901.ox;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.gogo98901.Bootstrap;
import net.gogo98901.ox.web.packet.Packet01Disconnect;

public class Window extends JFrame {
	private static final long serialVersionUID = 1L;
	private static Intro intro;
	private static String introText = "start";
	private static Page page;
	private static String pageText = "game";
	private static Lobby lobby;
	private static String lobbyText = "lobby";
	private static JPanel cards;

	private static boolean isMultiplayer = false;
	public static boolean hasStarted = false;

	public Window(int width, int height) {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				disconect();
				System.exit(0);
			}
		});
		getContentPane().setLayout(new BorderLayout());
		setSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
		intro = new Intro(width, height);
		page = new Page(width, height);
		lobby = new Lobby(width, height);

		cards = new JPanel(new CardLayout());
		cards.add(intro, introText);
		cards.add(lobby, lobbyText);
		cards.add(page, pageText);
		getContentPane().add(cards, BorderLayout.CENTER);
		pack();
		if (Bootstrap.multi) goToLobby();
	}

	public static void goToIntro() {
		// disconect();
		intro.reset();
		CardLayout cl = (CardLayout) (cards.getLayout());
		cl.show(cards, introText);
		hasStarted = false;
	}

	public static void goToLobby() {
		disconect();
		intro.reset();
		CardLayout cl = (CardLayout) (cards.getLayout());
		cl.show(cards, lobbyText);
		hasStarted = false;
	}

	public static boolean isMultiplayer() {
		return isMultiplayer;
	}

	public static void goToPage(boolean multiplayer) {
		isMultiplayer = multiplayer;
		CardLayout cl = (CardLayout) (cards.getLayout());
		cl.show(cards, pageText);
		page.start();
		hasStarted = true;
	}

	public static void disconect() {
		if (isMultiplayer && hasStarted) {
			Packet01Disconnect packet = new Packet01Disconnect(Page.getClientName());
			packet.writeData(page.socketClient);
			if (page.socketClient != null) page.socketClient.stopClient();
			if (page.socketServer != null) page.socketServer.stopServer();
			page.socketClient = null;
			page.socketServer = null;
		}
	}

	public static Page getPage() {
		return page;
	}

	public static void restartApp(boolean multi) {
		Bootstrap.multi = multi;
		restartApp();
	}

	public static void restartApp() {
		try {
			final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
			File currentJar;
			currentJar = new File(Window.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if (currentJar.getName().endsWith(".jar")) {
				final List<String> command = new ArrayList<String>();
				command.add(javaBin);
				command.add("-jar");
				command.add(currentJar.getPath());
				if (Bootstrap.showUndo) command.add("-undo");
				if (Bootstrap.multi) command.add("-goToMultiplayer");

				final ProcessBuilder builder = new ProcessBuilder(command);
				builder.start();
			}
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
