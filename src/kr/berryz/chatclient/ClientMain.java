package kr.berryz.chatclient;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;

/**
 * @author Berryzed
 *
 */
public class ClientMain extends JFrame implements ActionListener, KeyListener
{
	private static final long serialVersionUID = 1L;

	private static final int SERVER_PORT = 9999;

	private JPanel contentPanel;
	private JPanel topPanel, centerPanel, bottomPanel;

	private JButton sendButton;
	private JLabel statusLabel;

	private JTextArea historyTextArea;
	private JScrollPane textScrollPane;
	private JScrollBar textScrollBar;

	private JTextField messageTextField;

	private JList<String> userList;

	private String userName;
	private String serverIP;

	PrintWriter pw;
	BufferedReader br;

	Socket client;

	/**
	 * Create the frame.
	 */
	public ClientMain(String _serverIP, String _userName)
	{
		serverIP = _serverIP;
		userName = _userName;

		try
		{
			client = new Socket(serverIP, SERVER_PORT);

			br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			pw = new PrintWriter(client.getOutputStream(), true);
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, "Server Error!");
		}

		pw.println(userName); // send name to server

		setUI();

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				pw.println("end");
			}
		});

		new MessagesThread().start();
	}

	private void setUI()
	{
		setTitle("ChatClient");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout(5, 5));

		topPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) topPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		centerPanel = new JPanel();
		bottomPanel = new JPanel();

		sendButton = new JButton("Send");
		statusLabel = new JLabel("Connection Info: " + userName + ", " + serverIP + ":" + SERVER_PORT);
		historyTextArea = new JTextArea();
		historyTextArea.setEditable(false);

		textScrollPane = new JScrollPane(historyTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textScrollBar = textScrollPane.getVerticalScrollBar();
		messageTextField = new JTextField();
		messageTextField.addKeyListener(this);

		userList = new JList<String>();

		sendButton.addActionListener(this);

		topPanel.add(statusLabel);

		centerPanel.setLayout(new BorderLayout(5, 5));
		centerPanel.add("Center", textScrollPane);
		centerPanel.add("East", userList);

		bottomPanel.setLayout(new BorderLayout(5, 5));
		bottomPanel.add("Center", messageTextField);
		bottomPanel.add("East", sendButton);

		contentPanel.add("North", topPanel);
		contentPanel.add("Center", centerPanel);
		contentPanel.add("South", bottomPanel);

		setContentPane(contentPanel);

		setVisible(true);

		messageTextField.requestFocus();
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == sendButton)
		{
			appendText("Me:" + messageTextField.getText());
			pw.println(messageTextField.getText());
			messageTextField.setText("");
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		// TODO Auto-generated method stub
		if (e.getSource() == messageTextField)
		{
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				appendText("Me:" + messageTextField.getText());
				pw.println(messageTextField.getText());
				messageTextField.setText("");
			}
		}

	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}

	public void appendText(String msg)
	{
		historyTextArea.append(msg + "\n");
		textScrollBar.setValue(textScrollBar.getMaximum());
	}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			{
				if ("Nimbus".equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}

		String _serverIP = (String) JOptionPane.showInputDialog(null, "Enter Server IP :", "ServerIP", JOptionPane.PLAIN_MESSAGE, null, null, "localhost");

		if (_serverIP == null || _serverIP.equals(""))
		{
			JOptionPane.showMessageDialog(null, "No input Server IP!");
			System.exit(0);
		}

		String _userName = JOptionPane.showInputDialog(null, "Enter your name :", "Username", JOptionPane.PLAIN_MESSAGE);
		if (_userName == null || _userName.equals(""))
		{
			JOptionPane.showMessageDialog(null, "No input Name!");
			System.exit(0);
		}

		try
		{
			new ClientMain(_serverIP, _userName);
		}
		catch (Exception ex)
		{
			// TODO Auto-generated catch block
			System.out.println(ex.getMessage());
		}
	}

	class MessagesThread extends Thread
	{
		public void run()
		{
			String line;
			try
			{
				while (true)
				{
					line = br.readLine();
					appendText(line);
				}
			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage());
			}
		}
	}

}
