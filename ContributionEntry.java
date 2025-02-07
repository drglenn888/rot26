import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;

public class ContributionEntry extends JFrame implements ActionListener
{
	public ContributionEntry ()
	{
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
      		catch (Throwable t) {}

		Toolkit toolkit = Toolkit.getDefaultToolkit ();
		Image sursIcon = toolkit.getImage ("surs.gif");
		setIconImage (sursIcon);

		setTitle ("SURS Contribution Entry");
		setSize (800, 400);
		addWindowListener (new WindowAdapter()
		{
           	 	public void windowClosing(WindowEvent e)
               		{
				System.exit(0);
			}
		});


		JMenuBar menuBar = new JMenuBar ();
		setJMenuBar (menuBar);

		JMenu fileMenu = new JMenu ("File");
		JMenuItem exportItem = new JMenuItem ("Export");
		exportItem.addActionListener (this);
		JMenuItem exitItem = new JMenuItem ("Exit");
		exitItem.addActionListener (this);

		fileMenu.add (exportItem);
		fileMenu.addSeparator ();
		fileMenu.add (exitItem);

		menuBar.add (fileMenu);

		JMenu taskMenu = new JMenu ("Task");
		JMenuItem setupItem = new JMenuItem ("Setup");
		setupItem.addActionListener (this);
		JMenuItem payPeriodItem = new JMenuItem ("Pay Period");
		payPeriodItem.addActionListener (this);

		taskMenu.add (setupItem);
		taskMenu.add (payPeriodItem);

		menuBar.add (taskMenu);

		JMenu reportMenu = new JMenu ("Report");
		JMenu balancingReportMenu = new JMenu ("Balancing Report");
		JMenuItem nameBalancingReportItem = new JMenuItem ("by Name");
		nameBalancingReportItem.addActionListener (this);
		JMenuItem SSNBalancingReportItem = new JMenuItem ("by SSN");
		SSNBalancingReportItem.addActionListener (this);

		balancingReportMenu.add (nameBalancingReportItem);
		balancingReportMenu.add (SSNBalancingReportItem);

		reportMenu.add (balancingReportMenu);

		menuBar.add (reportMenu);

		JMenu helpMenu = new JMenu ("Help");
		JMenuItem supportItem = new JMenuItem ("Support");
		supportItem.addActionListener (this);
		JMenuItem aboutItem = new JMenuItem ("About");
		aboutItem.addActionListener (this);

		helpMenu.add (supportItem);
		helpMenu.add (aboutItem);

		menuBar.add (helpMenu);

		readControlFile ();

		contributionVector = new Vector ();

		readPayFile ();

		selectedIndex = 0;

		getContentPane ().setLayout (new FlowLayout ());

		browsePanel = new BrowsePanel (controlRecord, contributionVector, this);

		getContentPane ().add (browsePanel);

	}

	public void actionPerformed (ActionEvent e)
	{
		if (e.getSource () instanceof JMenuItem)
		{
			String menuCommand = e.getActionCommand ();
			if (menuCommand.equals ("Export"))
			{
				readControlFile ();
				readPayFile ();
				exportContributions = new ExportContributions (controlRecord, contributionVector, this);
			}
			else if (menuCommand.equals ("Exit"))
				System.exit (0);
			else if (menuCommand.equals ("Setup"))
			{
				readControlFile ();
				getContentPane ().removeAll ();
				setupPanel = new SetupPanel (controlRecord, contributionVector, browsePanel);
				getContentPane ().add (setupPanel);
				getContentPane ().validate ();
				getContentPane ().repaint ();
			}
			else if (menuCommand.equals ("Pay Period"))
			{
				readControlFile ();
				getContentPane ().removeAll ();
				periodPanel = new PeriodPanel (controlRecord, contributionVector, browsePanel);
				getContentPane ().add (periodPanel);
				getContentPane ().validate ();
				getContentPane ().repaint ();
			}
			else if (menuCommand.equals ("by Name"))
			{
				readControlFile ();
				readPayFile ();
				printReport = new PrintReport (controlRecord, contributionVector, "N");
			}
			else if (menuCommand.equals ("by SSN"))
			{
				readControlFile ();
				readPayFile ();
				printReport = new PrintReport (controlRecord, contributionVector, "S");
			}
			else if (menuCommand.equals ("Support"))
			{
				JOptionPane.showMessageDialog (this,
								"Telephone support is available from the Employer Payroll Department at SURS. \n" +
								"                                                      1 (800) 275-7877 \n" +
								"                                                       in C-U 378-8800",
								"Contribution Entry Support",
								JOptionPane.PLAIN_MESSAGE);
			}
			else if (menuCommand.equals ("About"))
			{
				JOptionPane.showMessageDialog (this,
								"                 Contribution Entry Version 4.1\n" +
								"                           Copyright \u00A9 2000 \n" +
								"State Universities Retirement System of Illinois",
								"Contribution Entry",
								JOptionPane.PLAIN_MESSAGE);
			}
		}
	}


	public static void main (String [] args)
	{

		JFrame f = new ContributionEntry ();
		f.show ();

	}

	private void readControlFile ()
	{
		try
		{
			File controlFile = new File ("control");

			FileReader controlFileReader = new FileReader (controlFile);

			char [] controlBuffer = new char [100];

			controlFileReader.read (controlBuffer, 0, 100);

			controlRecord = String.valueOf (controlBuffer);

			controlFileReader.close ();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog (this,
							"Control File I/O Exception: " + e,
							"Input/Output Warning",
							JOptionPane.WARNING_MESSAGE);
			System.err.println ("I/O Exception: " + e);
			return;
		}
	}

	private void readPayFile ()
	{
		contributionVector.clear ();

		try
		{
			File payFile = new File("payfile2");

			FileReader payFileReader = new FileReader (payFile);

			char [] record = new char [80];

			while (payFileReader.read (record, 0, 80) != -1)
			{
				contributionVector.add (new String (record));
			}

			payFileReader.close ();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog (this,
							"Pay File I/O Exception: " + e,
							"Input/Output Warning",
							JOptionPane.WARNING_MESSAGE);
			System.err.println ("I/O Exception: " + e);
			return;
		}
	}

	public void displayDetailPanel (String controlRecord, Vector contributionVector, int selectedIndex)
	{
			getContentPane ().removeAll ();
			detailPanel = new DetailPanel (controlRecord, contributionVector, selectedIndex, browsePanel, this);
			getContentPane ().add (detailPanel);
			getContentPane ().validate ();
			getContentPane ().repaint ();
	}

	public void displayBrowsePanel ()
	{
			getContentPane ().removeAll ();
			getContentPane ().add (browsePanel);
			getContentPane ().validate ();
			getContentPane ().repaint ();
	}


private String controlRecord;
private Vector contributionVector;
private int selectedIndex;
private BrowsePanel browsePanel;
private DetailPanel detailPanel;
private SetupPanel setupPanel;
private PeriodPanel periodPanel;
private PrintReport printReport;
private ExportContributions exportContributions;
}

class BrowsePanel extends JPanel implements ActionListener, MouseListener, ListSelectionListener
{

	public BrowsePanel (String controlRecordParameter, Vector contributionVectorParameter, ContributionEntry parentParameter)
	{

		controlRecord = controlRecordParameter;
		contributionVector = contributionVectorParameter;
		parent = parentParameter;

		sequence = controlRecord.substring (8, 9);

		contributionVector = SortContributions.sort (contributionVector, sequence);
		contributionListModel = new DefaultListModel ();

		contributionListModel.removeAllElements ();

		for (i = 0; i < contributionVector.size (); i++)
		{
			record = (String)contributionVector.elementAt (i);
			accountingCode = record.substring (72, 73);
			memberName = record.substring (40, 61);
			socialSecurityNumber = SSNEdit.format (record.substring (63, 72));
			contributionListModel.addElement ("   " + accountingCode + "   " + memberName + "  " + socialSecurityNumber);
		}

		setLayout (new GridBagLayout ());
		GridBagConstraints gbc = new GridBagConstraints ();
		contributionList = new JList (contributionListModel);
//		contributionList = new JList (contributionVector);
		contributionList.addListSelectionListener (this);
		contributionList.addMouseListener (this);
		contributionList.setFont (new Font ("DialogInput", Font.PLAIN, 12));
		JScrollPane contributionScrollPane = new JScrollPane (contributionList);
		JPanel headerPanel = new JPanel ();
		JLabel headerLabel = new JLabel ("Source             Name                                      SSN           ");
		headerPanel.add (headerLabel);
		contributionScrollPane.setColumnHeaderView (headerPanel);

		gbc.gridx 	= 1;
		gbc.gridy 	= 0;
		gbc.gridwidth 	= 2;
		gbc.gridheight 	= 20;
		gbc.weightx	= 100;
		gbc.weighty	= 100;
		gbc.fill	= GridBagConstraints.VERTICAL;
		gbc.anchor	= GridBagConstraints.CENTER;

		add (contributionScrollPane, gbc);

		JLabel fillerLabel = new JLabel (" ");

		gbc.gridx 	= 0;
		gbc.gridy	= 21;
		gbc.gridwidth	= 4;
		gbc.gridheight	= 1;
		gbc.weightx	= 100;
		gbc.weighty	= 100;
		gbc.fill	= GridBagConstraints.NONE;
		gbc.anchor	= GridBagConstraints.CENTER;

		add (fillerLabel, gbc);

		JPanel controlPanel = new JPanel ();


		addButton = new JButton ("Add");
		addButton.addActionListener (this);

		changeButton = new JButton ("Change");
		changeButton.addActionListener (this);

		deleteButton = new JButton ("Delete");
		deleteButton.addActionListener (this);

		sequenceButton = new JButton ("SSN Sequence");
		sequenceButton.addActionListener (this);

		controlPanel.add (addButton);
		controlPanel.add (changeButton);
		controlPanel.add (deleteButton);

		gbc.gridx 	= 0;
		gbc.gridy 	= 22;
		gbc.gridwidth 	= 4;
		gbc.gridheight 	= 1;
		gbc.weightx	= 100;
		gbc.weighty	= 100;
		gbc.fill	= GridBagConstraints.HORIZONTAL;
		gbc.anchor	= GridBagConstraints.CENTER;

		add (controlPanel, gbc);

	}


	public void valueChanged (ListSelectionEvent e)
	{
		selectedIndex = contributionList.getSelectedIndex ();
	}


	public void actionPerformed (ActionEvent e)
	{
		if (e.getSource () == addButton)
		{
			parent.displayDetailPanel (controlRecord, contributionVector, addIndex);
		}


		if (e.getSource () == changeButton)
		{
			parent.displayDetailPanel (controlRecord, contributionVector, selectedIndex);
		}


		if (e.getSource () == deleteButton)
		{
			record 	= (String)contributionVector.elementAt (selectedIndex);
			memberName = record.substring (40, 61);
			trimName = memberName.trim ();
			int operatorResponse = JOptionPane.showConfirmDialog (this,
									      "Are you sure you want to delete " + trimName + "?",
									      "Deletion Confirmation",
									      JOptionPane.YES_NO_OPTION,
									      JOptionPane.QUESTION_MESSAGE);

			if (operatorResponse == JOptionPane.YES_OPTION)
			{
				contributionVector.remove (selectedIndex);
				contributionListModel.remove (selectedIndex);
			}
			WriteContributions.writePayFile (contributionVector, this);
		}

		super.revalidate ();
		super.repaint ();

	}

	public void mouseClicked (MouseEvent e)
	{
		if (e.getClickCount () == 2)
		{
			selectedIndex = contributionList.locationToIndex (e.getPoint ());
			parent.displayDetailPanel (controlRecord, contributionVector, selectedIndex);
		}
	}

	public void mouseEntered (MouseEvent e)
	{
	}

	public void mousePressed (MouseEvent e)
	{
	}

	public void mouseReleased (MouseEvent e)
	{
	}

	public void mouseExited (MouseEvent e)
	{
	}

	public void reset (String controlRecordParameter, Vector contributionVectorParameter)
	{
		controlRecord = controlRecordParameter;
		contributionVector = contributionVectorParameter;

		sequence = controlRecord.substring (8, 9);

		contributionVector = SortContributions.sort (contributionVector, sequence);

		contributionListModel.removeAllElements ();

		for (i = 0; i < contributionVector.size (); i++)
		{
			record = (String)contributionVector.elementAt (i);
			accountingCode = record.substring (72, 73);
			memberName = record.substring (40, 61);
			socialSecurityNumber = SSNEdit.format (record.substring (63, 72));
			contributionListModel.addElement ("   " + accountingCode + "   " + memberName + "  " + socialSecurityNumber);
		}
	}



private int i;

private String controlRecord;
private Vector contributionVector;
private ContributionEntry parent;
private int selectedIndex;

private String sequence;

private Vector sortedListVector;
private DefaultListModel contributionListModel;
private JList contributionList;

private JButton addButton;
private JButton changeButton;
private JButton deleteButton;
private JButton sequenceButton;

private int addIndex = 999999;
private DetailPanel detailPanel;

private	String record;
private	String accountingCode;
private	String memberName;
private	String socialSecurityNumber;

private String trimName;

private BrowsePanel browsePanel;

}

class DetailPanel extends JPanel implements ActionListener
{

	public DetailPanel (String controlRecordParameter, Vector contributionVectorParameter, int selectedIndexParameter, BrowsePanel browsePanelParameter, ContributionEntry parentParameter)
	{
		controlRecord = controlRecordParameter;
		contributionVector = contributionVectorParameter;
		selectedIndex = selectedIndexParameter;
		browsePanel = browsePanelParameter;
		parent = parentParameter;

		setLayout (new GridBagLayout ());

		GridBagConstraints gbc = new GridBagConstraints ();

		memberNameLabel = new JLabel ("Name: ");

		gbc.gridx	= 0;
		gbc.gridy	= 0;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.weightx	= 100;
		gbc.weighty	= 100;
		gbc.fill	= GridBagConstraints.NONE;
		gbc.anchor	= GridBagConstraints.WEST;

		add (memberNameLabel, gbc);

		memberNameField = new JTextField (21);
		memberNameField.addActionListener (this);

		gbc.gridx	= 1;
		gbc.gridy	= 0;

		add (memberNameField, gbc);

		socialSecurityNumberLabel = new JLabel ("Social Security Number: ");

		gbc.gridx	= 2;
		gbc.gridy	= 0;

		add (socialSecurityNumberLabel, gbc);

		socialSecurityNumberField = new JTextField (9);
		socialSecurityNumberField.addActionListener (this);

		gbc.gridx	= 3;
		gbc.gridy	= 0;

		add (socialSecurityNumberField, gbc);

// the accounting code indicates the source of funds

		accountingCodeLabel = new JLabel ("Source of Funds: ");

		gbc.gridx	= 0;
		gbc.gridy	= 1;

		add (accountingCodeLabel, gbc);

		accountingCodeComboBox = new JComboBox ();
		accountingCodeComboBox.addItem ("General State Funding");
		accountingCodeComboBox.addItem ("Trust or Grant Funds");
		accountingCodeComboBox.addItem ("Federal Ag Funds");

		gbc.gridx	= 1;
		gbc.gridy	= 1;

		add (accountingCodeComboBox, gbc);

// the status field has been usurped to indicate the record type

		statusCodeLabel = new JLabel ("Record Type: ");

		gbc.gridx	= 2;
		gbc.gridy	= 1;

		add (statusCodeLabel, gbc);

		statusCodeComboBox = new JComboBox ();
		// statusCodeComboBox.addItem ("Earnings"); during pension reform it was decided to prevent this
		statusCodeComboBox.addItem ("Insurance");
		statusCodeComboBox.addItem ("Installment Purchase");
		statusCodeComboBox.addItem ("Employer Contributions");

		gbc.gridx	= 3;
		gbc.gridy	= 1;

		add (statusCodeComboBox, gbc);
		statusCodeComboBox.addActionListener (this);

		JLabel fillerLabel1 = new JLabel (" ");

		gbc.gridx	= 0;
		gbc.gridy	= 2;

		add (fillerLabel1, gbc);

		contributionLabel = new JLabel ("Employee Pension Contribution: ");

		gbc.gridx	= 0;
		gbc.gridy	= 3;

		add (contributionLabel, gbc);

		contributionField = new JTextField (7);
		contributionField.addActionListener (this);

		gbc.gridx	= 1;
		gbc.gridy	= 3;

		add (contributionField, gbc);

		earningsLabel = new JLabel ("Employee Earnings: ");

		gbc.gridx	= 2;
		gbc.gridy	= 3;

		add (earningsLabel, gbc);

		earningsField = new JTextField (8);
		earningsField.addActionListener (this);

		gbc.gridx	= 3;
		gbc.gridy	= 3;

		add (earningsField, gbc);

		policeFirefighterCodeLabel = new JLabel ("Police/Firefighter Check:");

		gbc.gridx	= 0;
		gbc.gridy	= 4;

		add (policeFirefighterCodeLabel, gbc);

		policeFirefighterCodeCheckBox = new JCheckBox ();

		gbc.gridx	= 1;
		gbc.gridy	= 4;

		add (policeFirefighterCodeCheckBox, gbc);

		transactionDateLabel = new JLabel ("Transaction Date: ");

		gbc.gridx	= 2;
		gbc.gridy	= 4;

		add (transactionDateLabel, gbc);

		transactionDateField = new JTextField (6);
		transactionDateField.addActionListener (this);

		gbc.gridx	= 3;
		gbc.gridy	= 4;

		add (transactionDateField, gbc);

		JLabel fillerLabel2 = new JLabel (" ");

		gbc.gridx	= 0;
		gbc.gridy	= 5;

		add (fillerLabel2, gbc);

		percentOfTimeWorkedLabel = new JLabel ("Percent of Time Worked: ");

		gbc.gridx	= 0;
		gbc.gridy	= 6;

		add (percentOfTimeWorkedLabel, gbc);

		percentOfTimeWorkedField = new JTextField (4);
		percentOfTimeWorkedField.addActionListener (this);

		gbc.gridx	= 1;
		gbc.gridy	= 6;

		add (percentOfTimeWorkedField, gbc);

		hoursWorkedLabel = new JLabel ("Hours Worked: ");

		gbc.gridx	= 2;
		gbc.gridy	= 6;

		add (hoursWorkedLabel, gbc);

		hoursWorkedField = new JTextField (4);
		hoursWorkedField.addActionListener (this);

		gbc.gridx	= 3;
		gbc.gridy	= 6;

		add (hoursWorkedField, gbc);

		hoursInWorkYearLabel = new JLabel ("Hours in Work Year: ");

		gbc.gridx	= 0;
		gbc.gridy	= 7;

		add (hoursInWorkYearLabel, gbc);

		hoursInWorkYearField = new JTextField (4);
		hoursInWorkYearField.addActionListener (this);

		gbc.gridx	= 1;
		gbc.gridy	= 7;

		add (hoursInWorkYearField, gbc);

		JLabel fillerLabel3 = new JLabel (" ");

		gbc.gridx	= 0;
		gbc.gridy	= 8;

		add (fillerLabel3, gbc);

		JPanel controlPanel = new JPanel ();

		exitButton = new JButton (" Exit ");
		exitButton.addActionListener (this);

		cancelButton = new JButton ("Cancel");
		cancelButton.addActionListener (this);

		priorButton = new JButton ("Prior ");
		priorButton.addActionListener (this);

		nextButton = new JButton (" Next ");
		nextButton.addActionListener (this);

		controlPanel.add (exitButton);
		controlPanel.add (cancelButton);

		if (selectedIndex != 999999)
		{
			controlPanel.add (priorButton);
			controlPanel.add (nextButton);
		}

		gbc.gridx	= 0;
		gbc.gridy	= 9;
		gbc.gridwidth	= 4;
		gbc.fill	= GridBagConstraints.HORIZONTAL;
		gbc.anchor	= GridBagConstraints.CENTER;

		add (controlPanel, gbc);

		displayRecord ();
	}


	public void actionPerformed (ActionEvent e)
	{

		if (e.getSource () == exitButton ||
		    e.getSource () == memberNameField ||
		    e.getSource () == socialSecurityNumberField ||
	 	    e.getSource () == contributionField ||
		    e.getSource () == earningsField ||
		    e.getSource () == transactionDateField ||
		    e.getSource () == percentOfTimeWorkedField ||
		    e.getSource () == hoursWorkedField ||
		    e.getSource () == hoursInWorkYearField)
		{
			editAndUpdate ();

			if (editError)
				return;

			browsePanel.reset (controlRecord, contributionVector);
			parent.displayBrowsePanel ();
		}

		if (e.getSource () == cancelButton)
		{
			parent.displayBrowsePanel ();
		}

		if (e.getSource () == priorButton)
		{
			editAndUpdate ();

			if (editError)
				return;

			if (selectedIndex == 0)
			{
				JOptionPane.showMessageDialog (this,
								"There are no more records.",
								"Beginning Of File",
								JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			selectedIndex--;

			displayRecord ();
		}

		if (e.getSource () == nextButton)
		{
			editAndUpdate ();

			if (editError)
				return;

			if (selectedIndex == contributionVector.size () - 1)
			{
				JOptionPane.showMessageDialog (this,
								"There are no more records.",
								"End Of File",
								JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			selectedIndex++;

			displayRecord ();
		}

		if (e.getSource () == statusCodeComboBox)
		{
			switch (statusCodeComboBox.getSelectedIndex ())
			{
				case 0:
					statusCodeComboBox.setSelectedItem ("Earnings");
					contributionLabel.setText ("Employee Pension Contribution: ");
					earningsLabel.setText ("Employee Earnings: ");
					earningsField.setVisible (true);
					break;
				case 1:
					statusCodeComboBox.setSelectedItem ("Insurance");
					contributionLabel.setText ("Employee Health Contribution: ");
					earningsLabel.setText ("Employer Health Contribution: ");
					earningsField.setVisible (true);
					break;
				case 2:
					statusCodeComboBox.setSelectedItem ("Installment Purchase");
					contributionLabel.setText ("Employee Purchase Payment: ");
					earningsLabel.setText (" ");
					earningsField.setVisible (false);
					break;
				case 3:
					statusCodeComboBox.setSelectedItem ("Employer Contributions");
					contributionLabel.setText ("Employee Pension Contribution: ");
					earningsLabel.setText ("Employer Pension Contribution: ");
					earningsField.setVisible (true);
					break;
			}
		}

	}

	public void displayRecord ()
	{

		agency = controlRecord.substring (0, 2);
		transactionDate = controlRecord.substring (2, 8);

		if (selectedIndex == 999999)
		{
			record = new StringBuffer ("                                                                                ");
			record.replace (1, 7, transactionDate);
		}
		else
			record = new StringBuffer ((String)contributionVector.elementAt (selectedIndex));

		memberName = record.substring (40, 61);
		socialSecurityNumber = SSNEdit.format (record.substring (63, 72));
		accountingCode = record.substring (72, 73);
		contribution = NumberEdit.format (record.substring (15, 22), 5, 2, "L");
		earnings = NumberEdit.format (record.substring (73, 80), 5, 2, "L");
		policeFirefighterCode = record.substring (0, 1);
		transactionDate = DateEdit.format (record.substring (1, 7));
		statusCode = record.substring (9, 10);
		percentOfTimeWorked = NumberEdit.format (record.substring (28, 32), 3, 1, "L");
		hoursWorked = NumberEdit.format (record.substring (32, 36), 3, 1, "L");
		hoursInWorkYear = NumberEdit.format (record.substring (36, 40), 4, 0, "L");

		memberNameField.setText (memberName);
		if (selectedIndex == 999999)
			socialSecurityNumberField.setText ("");
		else
			socialSecurityNumberField.setText (socialSecurityNumber);
		switch (accountingCode.charAt (0))
		{
			case ' ':
				accountingCodeComboBox.setSelectedItem ("General State Funding");
				break;
			case '1':
				accountingCodeComboBox.setSelectedItem ("Trust or Grant Funds");
				break;
			case '2':
				accountingCodeComboBox.setSelectedItem ("Federal Ag Funds");
				break;
		}
		switch (statusCode.charAt (0))
		{
			case ' ':
				statusCodeComboBox.setSelectedItem ("Earnings");
				contributionLabel.setText ("Employee Pension Contribution: ");
				earningsLabel.setText ("Employee Earnings: ");
				earningsField.setVisible (true);
				break;
			case 'I':
				statusCodeComboBox.setSelectedItem ("Insurance");
				contributionLabel.setText ("Employee Health Contribution: ");
				earningsLabel.setText ("Employer Health Contribution: ");
				earningsField.setVisible (true);
				break;
			case 'P':
				statusCodeComboBox.setSelectedItem ("Installment Purchase");
				contributionLabel.setText ("Employee Purchase Payment: ");
				earningsLabel.setText (" ");
				earningsField.setVisible (false);
				break;
			case 'Z':
				statusCodeComboBox.setSelectedItem ("Employer Contributions");
				contributionLabel.setText ("Employee Pension Contribution: ");
				earningsLabel.setText ("Employer Pension Contribution");
				earningsField.setVisible (true);
				break;
		}
		contributionField.setText (contribution);
		earningsField.setText (earnings);
		if (policeFirefighterCode.equals ("P"))
			policeFirefighterCodeCheckBox.setSelected (true);
		else
			policeFirefighterCodeCheckBox.setSelected (false);
		transactionDateField.setText (transactionDate);
		percentOfTimeWorkedField.setText (percentOfTimeWorked);
		hoursWorkedField.setText (hoursWorked);
		hoursInWorkYearField.setText (hoursInWorkYear);

	}


	public void editAndUpdate ()
	{
		editError = false;

		memberName = TextEdit.parse (memberNameField.getText ().trim (), 21);
		if (TextEdit.tooManyCharacters ())
		{
			JOptionPane.showMessageDialog (this,
						    	"The member name contains too many characters.",
				     		 	"Field Size Warning",
					    		JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		socialSecurityNumber = SSNEdit.parse (socialSecurityNumberField.getText ().trim ());
		if (SSNEdit.invalidCharacters ())
		{
			JOptionPane.showMessageDialog (this,
							"The social security number contains invalid characters.",
							"Invalid Character Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		if (SSNEdit.tooManyCharacters ())
		{
			JOptionPane.showMessageDialog (this,
							"The social security number contains too many characters.",
							"Field Size Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
			switch (accountingCodeComboBox.getSelectedIndex ())
		{
			case 0:
				accountingCode = " ";
				break;
			case 1:
				accountingCode = "1";
				break;
			case 2:
				accountingCode = "2";
				break;
		}
		contribution = NumberEdit.parse (contributionField.getText ().trim (), 5, 2);
		if (NumberEdit.invalidCharacters ())
		{
			JOptionPane.showMessageDialog (this,
							"The contribution amount contains invalid characters.",
							"Invalid Character Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		if (NumberEdit.tooManyIntegralDigits ())
		{
			JOptionPane.showMessageDialog (this,
							"The contribution amount contains too many integral digits.",
							"Field Size Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		if (NumberEdit.tooManyDecimalDigits ())
		{
			JOptionPane.showMessageDialog (this,
							"The contribution amount contains too many decimal digits.",
							"Field Size Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		earnings = NumberEdit.parse (earningsField.getText ().trim (), 5, 2);
		if (NumberEdit.invalidCharacters ())
		{
			JOptionPane.showMessageDialog (this,
							"The earnings amount contains invalid characters.",
							"Invalid Character Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		if (NumberEdit.tooManyIntegralDigits ())
		{
			JOptionPane.showMessageDialog (this,
							"The earnings amount contains too many integral digits.",
							"Field Size Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		if (NumberEdit.tooManyDecimalDigits ())
		{
			JOptionPane.showMessageDialog (this,
							"The earnings amount contains too many decimal digits.",
							"Field Size Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		if (policeFirefighterCodeCheckBox.isSelected () == true)
			policeFirefighterCode = "P";
		else
			policeFirefighterCode = " ";
		transactionDate = DateEdit.parse (transactionDateField.getText ().trim ());
		if (DateEdit.invalidCharacters ())
		{
			JOptionPane.showMessageDialog (this,
							"The transaction date contains invalid characters.",
							"Invalid Character Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		if (DateEdit.invalidDate ())
		{
			JOptionPane.showMessageDialog (this,
							"The transaction date is invalid.",
							"Invalid Date Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		switch (statusCodeComboBox.getSelectedIndex ())
		{
			case 0:
				statusCode = " ";
				break;
			case 1:
				statusCode = "I";
				break;
			case 2:
				statusCode = "P";
				break;
			case 3:
				statusCode = "Z";
				break;
		}
		percentOfTimeWorked = NumberEdit.parse (percentOfTimeWorkedField.getText ().trim (), 3, 1);
		if (NumberEdit.invalidCharacters ())
		{
			JOptionPane.showMessageDialog (this,
							"The percent of time worked contains invalid characters.",
							"Invalid Character Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		if (NumberEdit.tooManyIntegralDigits ())
		{
			JOptionPane.showMessageDialog (this,
							"The percent of time worked contains too many integral digits.",
							"Field Size Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		if (NumberEdit.tooManyDecimalDigits ())
		{
			JOptionPane.showMessageDialog (this,
							"The percent of time worked contains too many decimal digits.",
							"Field Size Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
			hoursWorked = NumberEdit.parse (hoursWorkedField.getText ().trim (), 3, 1);
		if (NumberEdit.invalidCharacters ())
		{
			JOptionPane.showMessageDialog (this,
							"The number of hours worked contains invalid characters.",
							"Invalid Character Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		if (NumberEdit.tooManyIntegralDigits ())
		{
			JOptionPane.showMessageDialog (this,
							"The number of hours worked contains too many integral digits.",
							"Field Size Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		if (NumberEdit.tooManyDecimalDigits ())
		{
			JOptionPane.showMessageDialog (this,
							"The number of hours worked contains too many decimal digits.",
							"Field Size Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		hoursInWorkYear = NumberEdit.parse (hoursInWorkYearField.getText ().trim (), 4, 0);
		if (NumberEdit.invalidCharacters ())
		{
			JOptionPane.showMessageDialog (this,
							"The number of hours in the work year contains invalid characters.",
							"Invalid Character Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		if (NumberEdit.tooManyIntegralDigits ())
		{
			JOptionPane.showMessageDialog (this,
							"The number of hours in the work year contains too many integral digits.",
							"Field Size Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		if (NumberEdit.tooManyDecimalDigits ())
		{
			JOptionPane.showMessageDialog (this,
							"The number of hours in the work year contains too many decimal digits.",
							"Field Size Warning",
							JOptionPane.WARNING_MESSAGE);
			editError = true;
			return;
		}
		record.replace ( 0, 80, "                                                                                ");
		record.replace ( 0,  1, policeFirefighterCode);
		record.replace ( 1,  7, transactionDate);
		record.replace ( 9, 10, statusCode);
		record.replace (15, 22, contribution);
		record.replace (28, 32, percentOfTimeWorked);
		record.replace (32, 36, hoursWorked);
		record.replace (36, 40, hoursInWorkYear);
		record.replace (40, 61,	memberName);
		record.replace (61, 63, agency);
		record.replace (63, 72, socialSecurityNumber);
		record.replace (72, 73, accountingCode);
		record.replace (73, 80, earnings);

		if (selectedIndex == 999999)
			contributionVector.add (new String(record));
		else
			contributionVector.setElementAt (new String(record), selectedIndex);

		WriteContributions.writePayFile (contributionVector, this);
	}


private String controlRecord;
private Vector contributionVector;
private int selectedIndex;
private BrowsePanel browsePanel;
private ContributionEntry parent;

private StringBuffer record;
private String memberName;
private String socialSecurityNumber;
private String accountingCode;
private String contribution;
private String earnings;
private String policeFirefighterCode;
private String transactionDate;
private String statusCode;
private String percentOfTimeWorked;
private String hoursWorked;
private String hoursInWorkYear;
private String agency;

private JLabel memberNameLabel;
private JLabel socialSecurityNumberLabel;
private JLabel accountingCodeLabel;
private JLabel contributionLabel;
private JLabel earningsLabel;
private JLabel policeFirefighterCodeLabel;
private JLabel transactionDateLabel;
private JLabel statusCodeLabel;
private JLabel percentOfTimeWorkedLabel;
private JLabel hoursWorkedLabel;
private JLabel hoursInWorkYearLabel;

private JTextField memberNameField;
private JTextField socialSecurityNumberField;
private JComboBox  accountingCodeComboBox;
private JComboBox  statusCodeComboBox;
private JTextField contributionField;
private JTextField earningsField;
private JCheckBox  policeFirefighterCodeCheckBox;
private JTextField transactionDateField;
private JTextField percentOfTimeWorkedField;
private JTextField hoursWorkedField;
private JTextField hoursInWorkYearField;

// private DecimalFormat contributionFormat = new DecimalFormat ("##,###.00");
// private DecimalFormat earningsFormat = new DecimalFormat ("##,###.00");
// private DecimalFormat percentOfTimeWorkedFormat  = new DecimalFormat ("###.0");
// private DecimalFormat hoursWorkedFormat = new DecimalFormat ("###.0");
// private DecimalFormat hoursInWorkYearFormat = new DecimalFormat ("####");

private JButton exitButton;
private JButton cancelButton;
private JButton priorButton;
private JButton nextButton;

private boolean editError;

}

class SetupPanel extends JPanel implements ActionListener
{

	public SetupPanel (String controlRecordParameter, Vector contributionVectorParameter, BrowsePanel browsePanelParameter)
	{
		controlRecord = controlRecordParameter;
		contributionVector = contributionVectorParameter;
		browsePanel = browsePanelParameter;

		agency = controlRecord.substring (0, 2);
		sequence = controlRecord.substring (8, 9);
		exportType = controlRecord.substring (9, 10);
		exportPath = controlRecord.substring (10, 40);
		payrollDescriptionCode = controlRecord.substring (40, 43);

		setLayout (new GridBagLayout ());

		GridBagConstraints gbc = new GridBagConstraints ();

		agencyLabel = new JLabel ("Agency: ");

		gbc.gridx	= 1;
		gbc.gridy	= 0;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.weightx	= 100;
		gbc.weighty	= 100;
		gbc.fill	= GridBagConstraints.NONE;
		gbc.anchor	= GridBagConstraints.WEST;

		add (agencyLabel, gbc);

		agencyField = new JTextField (2);
		agencyField.addActionListener (this);
		agencyField.setText (agency);

		gbc.gridx	= 2;
		gbc.gridy	= 0;

		add (agencyField, gbc);

		JLabel fillerLabel1 = new JLabel (" ");

		gbc.gridx	= 1;
		gbc.gridy	= 1;

		add (fillerLabel1, gbc);

		sequenceLabel = new JLabel ("Sequence: ");

		gbc.gridx	= 1;
		gbc.gridy	= 2;

		add (sequenceLabel, gbc);

		nameSequenceButton = new JRadioButton ("Name");
		nameSequenceButton.addActionListener (this);
		SSNSequenceButton = new JRadioButton ("SSN");
		SSNSequenceButton.addActionListener (this);

		ButtonGroup sequenceButtonGroup = new ButtonGroup ();
		sequenceButtonGroup.add (nameSequenceButton);
		sequenceButtonGroup.add (SSNSequenceButton);

		if (sequence.equals ("N"))
			nameSequenceButton.setSelected (true);
		else
			SSNSequenceButton.setSelected (true);

		JPanel sequenceButtonPanel = new JPanel ();
		sequenceButtonPanel.add (nameSequenceButton);
		sequenceButtonPanel.add (SSNSequenceButton);

		gbc.gridx	= 2;
		gbc.gridy	= 2;

		add (sequenceButtonPanel, gbc);

		JLabel fillerLabel2 = new JLabel (" ");

		gbc.gridx	= 1;
		gbc.gridy	= 3;

		add (fillerLabel2, gbc);

		exportTypeLabel = new JLabel ("Export Type: ");

		gbc.gridx	= 1;
		gbc.gridy	= 4;

		add (exportTypeLabel, gbc);

		disketteExportTypeButton = new JRadioButton ("Diskette");
		disketteExportTypeButton.addActionListener (this);
		FTPExportTypeButton = new JRadioButton ("FTP");
		FTPExportTypeButton.addActionListener (this);

		ButtonGroup exportTypeButtonGroup = new ButtonGroup ();
		exportTypeButtonGroup.add (disketteExportTypeButton);
		exportTypeButtonGroup.add (FTPExportTypeButton);

		if (exportType.equals ("D"))
			disketteExportTypeButton.setSelected (true);
		else
			FTPExportTypeButton.setSelected (true);

		JPanel exportTypeButtonPanel = new JPanel ();
		exportTypeButtonPanel.add (disketteExportTypeButton);
		exportTypeButtonPanel.add (FTPExportTypeButton);

		gbc.gridx	= 2;
		gbc.gridy	= 4;

		add (exportTypeButtonPanel, gbc);

		JLabel fillerLabel3 = new JLabel (" ");

		gbc.gridx	= 1;
		gbc.gridy	= 5;

		add (fillerLabel3, gbc);

		exportPathLabel = new JLabel ("Export Path: ");

		gbc.gridx	= 1;
		gbc.gridy	= 6;

		add (exportPathLabel, gbc);

		exportPathField = new JTextField (20);
		exportPathField.addActionListener (this);
		exportPathField.setText (exportPath);

		gbc.gridx	= 2;
		gbc.gridy	= 6;

		add (exportPathField, gbc);

		JLabel fillerLabel4 = new JLabel (" ");

		gbc.gridx	= 1;
		gbc.gridy	= 7;

		add (fillerLabel4, gbc);

		payrollDescriptionLabel = new JLabel ("Payroll Description: ");

		gbc.gridx	= 1;
		gbc.gridy	= 8;

		add (payrollDescriptionLabel, gbc);

		payrollDescriptionComboBox = new JComboBox ();
		payrollDescriptionComboBox.addItem ("Biweekly");
		payrollDescriptionComboBox.addItem ("City College of Chicago");
		payrollDescriptionComboBox.addItem ("City College of Germany");
		payrollDescriptionComboBox.addItem ("City of Champaign Firefighters");
		payrollDescriptionComboBox.addItem ("City of Urbana Firefighters");
		payrollDescriptionComboBox.addItem ("Civil Service Exempt");
		payrollDescriptionComboBox.addItem ("Civil Service Hourly");
		payrollDescriptionComboBox.addItem ("Monthly");
		payrollDescriptionComboBox.addItem ("Student & Hourly");
		payrollDescriptionComboBox.addItem ("Semi-Monthly");
		payrollDescriptionComboBox.addItem ("Semi-Monthly Local");
		payrollDescriptionComboBox.addItem ("Semi-Monthly State");
		payrollDescriptionComboBox.addItem ("Supplemental");

		     if (payrollDescriptionCode.equals ("BI ")) payrollDescriptionComboBox.setSelectedItem ("Biweekly");
		else if (payrollDescriptionCode.equals ("CCC")) payrollDescriptionComboBox.setSelectedItem ("City College of Chicago");
		else if (payrollDescriptionCode.equals ("CCG")) payrollDescriptionComboBox.setSelectedItem ("City College of Germany");
		else if (payrollDescriptionCode.equals ("COC")) payrollDescriptionComboBox.setSelectedItem ("City of Champaign Firefighters");
		else if (payrollDescriptionCode.equals ("COU")) payrollDescriptionComboBox.setSelectedItem ("City of Urbana Firefighters");
		else if (payrollDescriptionCode.equals ("CSE")) payrollDescriptionComboBox.setSelectedItem ("Civil Service Exempt");
		else if (payrollDescriptionCode.equals ("CSH")) payrollDescriptionComboBox.setSelectedItem ("Civil Service Hourly");
		else if (payrollDescriptionCode.equals ("MO ")) payrollDescriptionComboBox.setSelectedItem ("Monthly");
		else if (payrollDescriptionCode.equals ("SH ")) payrollDescriptionComboBox.setSelectedItem ("Student & Hourly");
		else if (payrollDescriptionCode.equals ("SM ")) payrollDescriptionComboBox.setSelectedItem ("Semi-Monthly");
		else if (payrollDescriptionCode.equals ("SML")) payrollDescriptionComboBox.setSelectedItem ("Semi-Monthly Local");
		else if (payrollDescriptionCode.equals ("SMS")) payrollDescriptionComboBox.setSelectedItem ("Semi-Monthly State");
		else if (payrollDescriptionCode.equals ("SU ")) payrollDescriptionComboBox.setSelectedItem ("Supplemental");

		gbc.gridx	= 2;
		gbc.gridy	= 8;

		add (payrollDescriptionComboBox, gbc);

		JLabel fillerLabel5 = new JLabel (" ");

		gbc.gridx	= 1;
		gbc.gridy	= 9;

		add (fillerLabel5, gbc);

		JPanel controlPanel = new JPanel ();

		exitButton = new JButton (" Exit ");
		exitButton.addActionListener (this);

		cancelButton = new JButton ("Cancel");
		cancelButton.addActionListener (this);

		controlPanel.add (exitButton);
		controlPanel.add (cancelButton);

		gbc.gridx	= 0;
		gbc.gridy	= 10;
		gbc.gridwidth	= 4;
		gbc.fill	= GridBagConstraints.HORIZONTAL;
		gbc.anchor	= GridBagConstraints.CENTER;

		add (controlPanel, gbc);

	}

	public void actionPerformed (ActionEvent e)
	{
		if (e.getSource () == nameSequenceButton)
			sequence = "N";

		if (e.getSource () == SSNSequenceButton)
			sequence = "S";

		if (e.getSource () == disketteExportTypeButton)
			exportType = "D";

		if (e.getSource () == FTPExportTypeButton)
			exportType = "F";

		if (e.getSource () == exitButton ||
		    e.getSource () == agencyField ||
		    e.getSource () == exportPathField)
		{
			agency = TextEdit.parse (agencyField.getText ().trim (), 2);
			if (TextEdit.tooManyCharacters ())
			{
				JOptionPane.showMessageDialog (this,
								"The agency identification contains too many characters.",
								"Field Size Warning",
								JOptionPane.WARNING_MESSAGE);
				return;
			}

			exportPath = TextEdit.parse (exportPathField.getText ().trim (), 30);
			if (TextEdit.tooManyCharacters ())
			{
				JOptionPane.showMessageDialog (this,
								"The export path contains too many characters.",
								"Field Size Warning",
								JOptionPane.WARNING_MESSAGE);
				return;
			}

			switch (payrollDescriptionComboBox.getSelectedIndex ())
			{
				case 0:
					payrollDescriptionCode = "BI ";
					break;
				case 1:
					payrollDescriptionCode = "CCC";
					break;
				case 2:
					payrollDescriptionCode = "CCG";
					break;
				case 3:
					payrollDescriptionCode = "COC";
					break;
				case 4:
					payrollDescriptionCode = "COU";
					break;
				case 5:
					payrollDescriptionCode = "CSE";
					break;
				case 6:
					payrollDescriptionCode = "CSH";
					break;
				case 7:
					payrollDescriptionCode = "MO ";
					break;
				case 8:
					payrollDescriptionCode = "SH ";
					break;
				case 9:
					payrollDescriptionCode = "SM ";
					break;
				case 10:
					payrollDescriptionCode = "SML";
					break;
				case 11:
					payrollDescriptionCode = "SMS";
					break;
				case 12:
					payrollDescriptionCode = "SU ";
					break;
			}

			controlRecordBuffer = new StringBuffer (controlRecord);
			controlRecordBuffer.replace ( 0,  2, agency);
			controlRecordBuffer.replace ( 8,  9, sequence);
			controlRecordBuffer.replace ( 9, 10, exportType);
			controlRecordBuffer.replace (10, 40, exportPath);
			controlRecordBuffer.replace (40, 43, payrollDescriptionCode);

			controlRecord = controlRecordBuffer.toString ();

			WriteControl.writeControlFile (controlRecord, this);

			super.removeAll ();
			browsePanel.reset (controlRecord, contributionVector);
			super.add (browsePanel);
			super.revalidate ();
			super.repaint ();
		}

		if (e.getSource () == cancelButton)
		{
			super.removeAll ();
			super.add (browsePanel);
			super.revalidate ();
			super.repaint ();
		}

	}

private String controlRecord;
private Vector contributionVector;

private String agency;
private String sequence;
private String exportType;
private String exportPath;
private String payrollDescriptionCode;
private StringBuffer controlRecordBuffer;

private JLabel agencyLabel;
private JLabel sequenceLabel;
private JLabel exportTypeLabel;
private JLabel exportPathLabel;
private JLabel payrollDescriptionLabel;

private JTextField agencyField;
private JRadioButton nameSequenceButton;
private JRadioButton SSNSequenceButton;
private JRadioButton disketteExportTypeButton;
private JRadioButton FTPExportTypeButton;
private JTextField exportPathField;
private JComboBox payrollDescriptionComboBox;

private JButton exitButton;
private JButton cancelButton;

private BrowsePanel browsePanel;
}

class PeriodPanel extends JPanel implements ActionListener
{
	public PeriodPanel (String controlRecordParameter, Vector contributionVectorParameter, BrowsePanel browsePanelParameter)
	{
		controlRecord = controlRecordParameter;
		contributionVector = contributionVectorParameter;
		browsePanel = browsePanelParameter;

		payPeriod = DateEdit.format (controlRecord.substring (2, 8));

		setLayout (new GridBagLayout ());

		GridBagConstraints gbc = new GridBagConstraints ();

		JLabel fillerLabel1 = new JLabel (" ");

		gbc.gridx 	= 0;
		gbc.gridy 	= 0;
		gbc.gridwidth 	= 1;
		gbc.gridheight 	= 1;
		gbc.weightx	= 100;
		gbc.weighty	= 100;
		gbc.fill	= GridBagConstraints.NONE;
		gbc.anchor	= GridBagConstraints.WEST;

		add (fillerLabel1, gbc);

		JLabel fillerLabel2 = new JLabel (" ");

		gbc.gridx 	= 1;
		gbc.gridy 	= 1;

		add (fillerLabel2, gbc);

		payPeriodLabel = new JLabel ("Pay Period: ");

		gbc.gridx 	= 1;
		gbc.gridy 	= 2;

		add (payPeriodLabel, gbc);

		payPeriodField = new JTextField (8);
		payPeriodField.addActionListener (this);

		gbc.gridx	= 2;
		gbc.gridy	= 2;

		payPeriodField.setText (payPeriod);
		add (payPeriodField, gbc);

		JLabel fillerLabel3 = new JLabel (" ");

		gbc.gridx 	= 0;
		gbc.gridy 	= 3;

		add (fillerLabel3, gbc);

		JLabel fillerLabel4 = new JLabel (" ");

		gbc.gridx 	= 0;
		gbc.gridy 	= 4;

		add (fillerLabel4, gbc);

		JPanel controlPanel = new JPanel ();

		exitButton = new JButton (" Exit ");
		exitButton.addActionListener (this);

		cancelButton = new JButton ("Cancel");
		cancelButton.addActionListener (this);

		gbc.gridx	= 0;
		gbc.gridy	= 5;
		gbc.gridwidth	= 4;
		gbc.fill 	= GridBagConstraints.HORIZONTAL;
		gbc.anchor	= GridBagConstraints.CENTER;

		controlPanel.add (exitButton);
		controlPanel.add (cancelButton);

		add(controlPanel, gbc);

	}

	public void actionPerformed (ActionEvent e)
	{

		if (e.getSource () == exitButton ||
		    e.getSource () == payPeriodField)
		{

			payPeriod = DateEdit.parse (payPeriodField.getText ().trim ());
			if (DateEdit.invalidCharacters ())
			{
					JOptionPane.showMessageDialog (this,
									"The pay period end date contains invalid characters.",
									"Invalid Character Warning",
									JOptionPane.WARNING_MESSAGE);
					return;
			}
			if (DateEdit.invalidDate ())
			{
					JOptionPane.showMessageDialog (this,
									"The pay period end date is invalid.",
									"Invalid Date Warning",
									JOptionPane.WARNING_MESSAGE);
					return;
			}

			for (i = 0; i < contributionVector.size (); i++)
			{
				recordBuffer = new StringBuffer ((String)contributionVector.elementAt (i));
				recordBuffer.replace (1, 7, payPeriod);
				contributionVector.setElementAt (new String(recordBuffer), i);
			}


			WriteContributions.writePayFile (contributionVector, this);

			controlRecordBuffer = new StringBuffer (controlRecord);
			controlRecordBuffer.replace (2, 8, payPeriod);
			controlRecord = controlRecordBuffer.toString ();

			WriteControl.writeControlFile (controlRecord, this);

			super.removeAll ();
			super.add (browsePanel);
			super.revalidate ();
			super.repaint ();

		}


		if (e.getSource () == cancelButton)
		{

			super.removeAll ();
			super.add (browsePanel);
			super.revalidate ();
			super.repaint ();

		}

	}



private String controlRecord;
private Vector contributionVector;

private StringBuffer controlRecordBuffer;
private StringBuffer recordBuffer;

private JLabel payPeriodLabel;
private JTextField payPeriodField;

private JButton exitButton;
private JButton cancelButton;

private int i;

private String payPeriod;

private BrowsePanel browsePanel;
}

class ExportContributions
{

	public ExportContributions (String controlRecord, Vector contributionVector, JFrame parent)
	{

		agency = controlRecord.substring (0, 2);
		payPeriod = controlRecord.substring (2, 8);
		exportType = controlRecord.substring (9, 10);
		exportPath = controlRecord.substring (10, 40);
		payrollDescriptionCode = controlRecord.substring (40, 43);

		if (exportType.equals ("D"))
		{

			disketteDrive = controlRecord.substring (10, 11);

			int operatorResponse = JOptionPane.showConfirmDialog (parent,
									      "Place a formatted diskette in drive " + disketteDrive + ":",
									      "Export",
									      JOptionPane.OK_CANCEL_OPTION,
									      JOptionPane.PLAIN_MESSAGE);

			if (operatorResponse == JOptionPane.CANCEL_OPTION)
				return;
		}

		if (exportType.equals ("D"))
			fileName = exportPath.trim () + agency + payPeriod;
		else
			fileName = exportPath.trim () + payrollDescriptionCode.trim () + payPeriod.substring (0, 4) + ".PAY";

		try
		{
			File exportFile = new File (fileName);

			FileWriter exportFileWriter = new FileWriter (exportFile);

			for (int i = 0; i < contributionVector.size (); i++)
			{
				recordBuffer = new StringBuffer ((String)contributionVector.elementAt (i));
				recordBuffer.append ("\r\n");
				exportFileWriter.write (recordBuffer.toString ());
			}

			exportFileWriter.close ();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog (parent,
							"I/O Exception: " + e,
							"Input/Output Warning",
							JOptionPane.WARNING_MESSAGE);
			System.err.println ("I/O Exception: " + e);
			return;
		}

		JOptionPane.showMessageDialog (parent,
						"The export is complete.",
						"Export",
						JOptionPane.INFORMATION_MESSAGE);

	}

private String agency;
private String payPeriod;
private String exportType;
private String exportPath;
private String payrollDescriptionCode;
private String disketteDrive;
private String fileName;

private StringBuffer recordBuffer;
}

class PrintReport implements Printable
{

	public PrintReport (String controlRecordParameter, Vector contributionVectorParameter, String sequence)
	{

		controlRecord = controlRecordParameter;
		agency = controlRecord.substring (0, 2);
		payPeriod = controlRecord.substring (2, 8);

		contributionVector = contributionVectorParameter;

		contributionVector = SortContributions.sort (contributionVector, sequence);

		PrinterJob printJob = PrinterJob.getPrinterJob ();
		printJob.setPrintable (this);
		if (printJob.printDialog ())
		{
			try
			{
				printJob.print ();
			}
			catch (Exception ex)
			{
				ex.printStackTrace ();
			}
		}
	}

	public int print (Graphics g, PageFormat pf, int pi) throws PrinterException
	{

		x = (int)pf.getImageableX ();
		y = (int)pf.getImageableY ();
		width  = (int)pf.getImageableWidth ();
		height = (int)pf.getImageableHeight ();

// bug fix
// this fix will correct pages 2 - n printing blank on certain printers under Windows 9x
// this fix will no longer be required with Java 1.3

		    g.setColor(Color.white);
		    g.fillRect(x, y, width, height);
		    g.setColor(Color.black);

// end of bug fix

// the baseline must be positioned below the upper left corner

		y = y + 12;

// The number of pages is calculated assuming a page size of 54 lines
// with 6 title lines and 48 detail lines

		numberOfPages = contributionVector.size () / 48 + 1;

// Determine how many total lines will print

		totalLines = 2;
		purchaseTotalLines = 0;
		for (i = 0; i < contributionVector.size (); i++)
		{
			record = (String)contributionVector.elementAt (i);
			statusCode = record.substring (9, 10);
			if (statusCode.equals ("P")) purchaseTotalLines = 2;
		}
		totalLines = totalLines + purchaseTotalLines;

		if (contributionVector.size () % 48 > 48 - totalLines)
			numberOfPages++;

		if (pi >= numberOfPages)
		{
			return Printable.NO_SUCH_PAGE;
		}
		printReport (g, pf, pi);
		return Printable.PAGE_EXISTS;
	}

	public void printReport (Graphics g, PageFormat pf, int pi)
	{
		g.setFont (new Font ("DialogInput", Font.PLAIN, 10));

		printTitles (g, pi);

		firstDetail = pi * 48;
		lastDetail  = firstDetail + 48;
		if (lastDetail > contributionVector.size ())
			lastDetail = contributionVector.size ();

		for (i = firstDetail; i < lastDetail; i++)
		{
			printDetailLine (g);
			y = y + 12;
		}

		if (pi == numberOfPages - 1)
			printTotals (g);
	}


	public void printTitles (Graphics g, int pi)
	{
		SimpleDateFormat df = new SimpleDateFormat ("MM/dd/yy");

		g.drawString ("Print Date " + df.format (new Date ())	  + "   State Universities Retirement System        Page " + (pi + 1), x, y);
		g.drawString ("Pay Period " + DateEdit.format (payPeriod) + "               Contributions                   Agency " + agency, x, y + 12);

		g.drawString ("Rec  Source   Social                                                       ", x, y + 36);
		g.drawString ("Type Funds  Security #  Name                    Contribution       Earnings", x, y + 48);

		y = y + 72;
	}

	public void printDetailLine (Graphics g)
	{
		record = (String)contributionVector.elementAt (i);

		memberName = record.substring (40, 61);
		socialSecurityNumber = SSNEdit.format (record.substring (63, 72));
		accountingCode = record.substring (72, 73);
		contribution = NumberEdit.format (record.substring (15, 22), 5, 2, "T");
		earnings = NumberEdit.format (record.substring (73, 80), 5, 2, "T");
		policeFirefighterCode = record.substring (0, 1);
		transactionDate = DateEdit.format (record.substring (1, 7));
		statusCode = record.substring (9, 10);
		percentOfTimeWorked = NumberEdit.format (record.substring (28, 32), 3, 1, "T");
		hoursWorked = NumberEdit.format (record.substring (32, 36), 3, 1, "T");
		hoursInWorkYear = NumberEdit.format (record.substring (36, 40), 4, 0, "T");

		g.drawString ("  " + statusCode + "    " + accountingCode + "   " + socialSecurityNumber + "  " + memberName + "      " + contribution + "     " + earnings, x, y);
	}

	public void printTotals (Graphics g)
	{
		contributionsAccumulator = 0;
		earningsAccumulator = 0;
		purchasesAccumulator = 0;

		for (i = 0; i < contributionVector.size (); i++)
		{
			record = (String)contributionVector.elementAt (i);

			statusCode = record.substring (9, 10);

// parse the fields to replace any blanks with zeros

			contribution = NumberEdit.parse (record.substring (15, 22), 7, 0);
			earnings = NumberEdit.parse (record.substring (73, 80), 7, 0);

// use integer arithmetic to avoid precision errors in floating point arithmetic

			contributionInteger = Integer.parseInt (contribution);
			earningsInteger = Integer.parseInt (earnings);

			if (statusCode.equals ("P"))
				purchasesAccumulator = purchasesAccumulator + contributionInteger;
			else
				contributionsAccumulator = contributionsAccumulator + contributionInteger;

			earningsAccumulator = earningsAccumulator + earningsInteger;
		}

		contributionsTotal = Integer.toString (contributionsAccumulator);
		earningsTotal = Integer.toString (earningsAccumulator);

// parse the fields to set the field length

		contributionsTotal = NumberEdit.parse (contributionsTotal, 8, 0);
		earningsTotal = NumberEdit.parse (earningsTotal, 8, 0);

		contributionsTotal = NumberEdit.format (contributionsTotal, 6, 2, "T");
		earningsTotal = NumberEdit.format (earningsTotal, 6, 2, "T");

		if (purchaseTotalLines > 0)
		{
			g.drawString ("                                    Contributions " + contributionsTotal + "    " + earningsTotal, x, y + 12);

			purchasesTotal = Integer.toString (purchasesAccumulator);
			purchasesTotal = NumberEdit.parse (purchasesTotal, 8, 0);
			purchasesTotal = NumberEdit.format (purchasesTotal, 6, 2, "T");
			g.drawString ("                                    Purchases     " + purchasesTotal, x, y + 24);

			grandTotal = Integer.toString (contributionsAccumulator + purchasesAccumulator);
			grandTotal = NumberEdit.parse (grandTotal, 8, 0);
			grandTotal = NumberEdit.format (grandTotal, 6, 2, "T");
			g.drawString ("                                    Grand Total   " + grandTotal, x, y + 36);
		}
		else
		{
			g.drawString ("                                                  " + contributionsTotal + "    " + earningsTotal, x, y + 12);
		}

	}

private int numberOfPages;

private int i;
private int x;
private int y;
private int width;
private int height;

private int firstDetail;
private int lastDetail;

private String controlRecord;

private Vector contributionVector;

private String sequence;

private String record;
private String memberName;
private String socialSecurityNumber;
private String accountingCode;
private String contribution;
private String earnings;
private String policeFirefighterCode;
private String transactionDate;
private String statusCode;
private String percentOfTimeWorked;
private String hoursWorked;
private String hoursInWorkYear;

private String agency;
private String payPeriod;

private int totalLines;
private int purchaseTotalLines;

private int contributionInteger;
private int earningsInteger;
private int contributionsAccumulator;
private int earningsAccumulator;
private int purchasesAccumulator;

private String contributionsTotal;
private String earningsTotal;
private String purchasesTotal;
private String grandTotal;
}

class SortContributions
{
	public static Vector sort (Vector contributionVectorParameter, String sequence)
	{
		contributionVector = contributionVectorParameter;

		sortArray = new String [contributionVector.size ()];

		for (i = 0; i < contributionVector.size (); i++)
		{
			record 	= (String)contributionVector.elementAt (i);
			accountingCode = record.substring (72, 73);
			memberName = record.substring (40, 61);
			socialSecurityNumber = record.substring (63, 72);
			if (sequence.equals ("N"))
				sortRecord = memberName + accountingCode + record;
			else
				sortRecord = socialSecurityNumber + accountingCode + "            " + record;
				sortArray [i] = sortRecord;
		}

		Arrays.sort (sortArray);

		for (i = 0; i < contributionVector.size (); i++)
		{
			record = sortArray [i].substring (22);
			contributionVector.setElementAt (record, i);
		}

		return contributionVector;
	}

private static Vector contributionVector;

private static int i;

private static String record;
private static String accountingCode;
private static String memberName;
private static String socialSecurityNumber;
private static String sortRecord;

private static String [] sortArray;

}

class WriteControl
{
	public static void writeControlFile (String controlRecord, JPanel parent)
	{
			try
			{
				File controlFile = new File ("control");

				FileWriter controlFileWriter = new FileWriter (controlFile);

				controlFileWriter.write (controlRecord, 0, 100);

				controlFileWriter.close ();
			}
			catch (IOException exception)
			{
				JOptionPane.showMessageDialog (parent,
								"Control File I/O Exception: " + exception,
								"Input/Output Warning",
								JOptionPane.WARNING_MESSAGE);
				System.err.println ("I/O Exception: " + exception);
				return;
			}
	}
}

class WriteContributions
{
	public static void writePayFile (Vector contributionVector, JPanel parent)
	{
		try
		{
			File payFile = new File("payfile2");

			FileWriter payFileWriter = new FileWriter (payFile);

			for (int i = 0; i < contributionVector.size (); i++)
			{
				record = (String)contributionVector.elementAt (i);
				payFileWriter.write (record);
			}

			payFileWriter.close ();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog (parent,
							"Pay File I/O Exception: " + e,
							"Input/Output Warning",
							JOptionPane.WARNING_MESSAGE);
			System.err.println ("I/O Exception: " + e);
			return;
		}
	}
private static String record;
}


class TextEdit
{
	public static String parse (String textField, int fieldLength)
	{
		editBuffer = new StringBuffer (textField);
		tooManyCharactersError = false;
		notAlphabetic = false;

// pad with trailing blanks

		while (editBuffer.length () < fieldLength)
		{
			editBuffer.append (' ');
		}

		if (editBuffer.length () > fieldLength)
			tooManyCharactersError = true;

		for (i = 0; i < editBuffer.length (); i++)
			if ((editBuffer.charAt (i) < 'A' || editBuffer.charAt (i) > 'Z') && (editBuffer.charAt (i) < 'a' || editBuffer.charAt (i) > 'z'))
				notAlphabetic = true;

		return editBuffer.toString ();
	}

	public static boolean tooManyCharacters ()
	{
		return tooManyCharactersError;
	}

	public static boolean notAlphabetic ()
	{
		return notAlphabetic;
	}

private static StringBuffer editBuffer;
private static int i;
private static boolean tooManyCharactersError;
private static boolean notAlphabetic;
}

class NumberEdit
{
	public static String format (String numberField, int integralPositions, int decimalPositions, String signPosition)
	{
		negativeNumber = false;

		int fieldLength = integralPositions + decimalPositions;

		if (numberField.length () != fieldLength)
		{
			System.err.println ("Field size does not match the sum of integral and decimal positions. " + numberField + " " + integralPositions + " " + decimalPositions);
//			System.exit (0);
		}

		editBuffer = new StringBuffer (numberField);

		for (i = 0; i < editBuffer.length (); i++)
		{
			if (editBuffer.charAt (i) == ' ')
				editBuffer.setCharAt (i, '0');
		}

		if (decimalPositions > 0)
			editBuffer.insert (integralPositions, '.');

		i = integralPositions - 3;
		while (i > 0)
		{
			editBuffer.insert (i, ',');
			i = i - 3;
		}

		if (editBuffer.charAt (0) == '-')
		{
			negativeNumber = true;
		}

		for (i = 0; i < editBuffer.length () - 1; i++)
		{
			if (editBuffer.charAt (i) == '0' || editBuffer.charAt (i) == ',' || editBuffer.charAt (i) == '-')
				editBuffer.setCharAt (i, ' ');
			else
				break;
		}

		if (signPosition.equals ("L"))
		{
			if (negativeNumber)
				editBuffer.insert (i, '-');
			else
				editBuffer.insert (i, ' ');
		}
		else if (signPosition.equals ("T"))
		{
			if (negativeNumber)
				editBuffer.append ('-');
			else
				editBuffer.append (' ');
		}

		return editBuffer.toString ();

	}

	public static String parse (String numberField, int integralPositions, int decimalPositions)
	{
		editBuffer = new StringBuffer (numberField);

		integralCount = 0;
		decimalCount = 0;

		negativeNumber = false;
		decimalPointFound = false;

		invalidCharactersError = false;
		tooManyIntegralDigitsError = false;
		tooManyDecimalDigitsError = false;

		for (i = 0; i < editBuffer.length (); i++)
		{
			switch (editBuffer.charAt (i))
			{
				case '-':
					negativeNumber = true;
					integralCount++;
					editBuffer.deleteCharAt (i);
					i--;
					break;
				case '.':
					decimalPointFound = true;
					editBuffer.deleteCharAt (i);
					i--;
					break;
				case ',':
					editBuffer.deleteCharAt (i);
					i--;
					break;
				case '0':
					if (decimalPointFound == true)
						decimalCount++;
					else
						integralCount++;
					break;
				case '1':
					if (decimalPointFound == true)
						decimalCount++;
					else
						integralCount++;
					break;
				case '2':
					if (decimalPointFound == true)
						decimalCount++;
					else
						integralCount++;
					break;
				case '3':
					if (decimalPointFound == true)
						decimalCount++;
					else
						integralCount++;
					break;
				case '4':
					if (decimalPointFound == true)
						decimalCount++;
					else
						integralCount++;
					break;
				case '5':
					if (decimalPointFound == true)
						decimalCount++;
					else
						integralCount++;
					break;
				case '6':
					if (decimalPointFound == true)
						decimalCount++;
					else
						integralCount++;
					break;
				case '7':
					if (decimalPointFound == true)
						decimalCount++;
					else
						integralCount++;
					break;
				case '8':
					if (decimalPointFound == true)
						decimalCount++;
					else
						integralCount++;
					break;
				case '9':
					if (decimalPointFound == true)
						decimalCount++;
					else
						integralCount++;
					break;
				default:
					invalidCharactersError = true;
					editBuffer.deleteCharAt (i);
					i--;
					break;
			}
		}

		while ((integralCount > integralPositions) && (editBuffer.charAt (0) == '0'))
		{
			editBuffer.deleteCharAt (0);
			integralCount--;
		}


		while ((decimalCount > decimalPositions) && (editBuffer.charAt (editBuffer.length () - 1) == '0'))
		{
			editBuffer.deleteCharAt (editBuffer.length () - 1);
			decimalCount--;
		}

		if (integralCount > integralPositions)
			tooManyIntegralDigitsError = true;

		if (decimalCount > decimalPositions)
			tooManyDecimalDigitsError = true;

// pad with leading zeros

		while (integralPositions - integralCount > 0)
		{
			editBuffer.insert (0, '0');

			integralCount++;
		}

// pad with trailing zeros

		while (decimalPositions - decimalCount > 0)
		{
			editBuffer.append ('0');

			decimalCount++;
		}

// sign negative numbers

		if (negativeNumber == true)
			editBuffer.insert (0, '-');

		return editBuffer.toString ();
	}

	public static boolean invalidCharacters ()
	{
		return invalidCharactersError;
	}

	public static boolean tooManyIntegralDigits ()
	{
		return tooManyIntegralDigitsError;
	}

	public static boolean tooManyDecimalDigits ()
	{
		return tooManyDecimalDigitsError;
	}

private static StringBuffer editBuffer;
private static int i;
private static int integralCount;
private static int decimalCount;
private static boolean negativeNumber;
private static boolean decimalPointFound;
private static boolean invalidCharactersError;
private static boolean tooManyIntegralDigitsError;
private static boolean tooManyDecimalDigitsError;

}

class DateEdit
{
	public static String format (String dateField)
	{
		return dateField.substring (0, 2) + "/" + dateField.substring (2, 4) + "/" + dateField.substring (4, 6);
	}

	public static String parse (String dateField)
	{
		editBuffer = new StringBuffer (dateField);

		monthBuffer = new StringBuffer ();
		dayBuffer = new StringBuffer ();
		yearBuffer = new StringBuffer ();

		invalidCharactersError = false;
		invalidDateError = false;

		for (i = 0; i < editBuffer.length (); i++)
		{
			switch (editBuffer.charAt (i))
			{
				case '/':
					break;
				case '0':
					break;
				case '1':
					break;
				case '2':
					break;
				case '3':
					break;
				case '4':
					break;
				case '5':
					break;
				case '6':
					break;
				case '7':
					break;
				case '8':
					break;
				case '9':
					break;
				default:
					invalidCharactersError = true;
					editBuffer.deleteCharAt (i);
					i--;
					break;
			}
		}

		for (i = 0; i < editBuffer.length (); i++)
		{
			if (editBuffer.charAt (i) == '/')
				break;
			else
				monthBuffer.append(editBuffer.charAt (i));
		}

		for (i = i + 1; i < editBuffer.length (); i++)
		{
			if (editBuffer.charAt (i) == '/')
				break;
			else
				dayBuffer.append(editBuffer.charAt (i));
		}

		for (i = i + 1; i < editBuffer.length (); i++)
		{
			yearBuffer.append(editBuffer.charAt (i));
		}

		if (monthBuffer.length () > 2)
			invalidDateError = true;

		if (dayBuffer.length () > 2)
			invalidDateError = true;

		if (yearBuffer.length () > 2)
			invalidDateError = true;

		while (monthBuffer.length () < 2)
			monthBuffer.insert (0, '0');

		while (dayBuffer.length () < 2)
			dayBuffer.insert (0, '0');

		while (yearBuffer.length () < 2)
			yearBuffer.insert (0, '0');

		month = Integer.parseInt (monthBuffer.toString ());
		day = Integer.parseInt (dayBuffer.toString ());
		year = Integer.parseInt (yearBuffer.toString ());

		if (year % 4 == 0)
			daysInMonth [1] = 29;
		else
			daysInMonth [1] = 28;

		if (month < 1 || month > 12)
			invalidDateError = true;
		else if (day < 1 || day > daysInMonth [month - 1])
			invalidDateError = true;

		return monthBuffer.toString  () + dayBuffer.toString () + yearBuffer.toString ();
	}

	public static boolean invalidCharacters ()
	{
		return invalidCharactersError;
	}

	public static boolean invalidDate ()
	{
		return invalidDateError;
	}


private static StringBuffer editBuffer;
private static int i;
private static StringBuffer monthBuffer;
private static StringBuffer dayBuffer;
private static StringBuffer yearBuffer;
private static int month;
private static int day;
private static int year;
private static boolean invalidCharactersError;
private static boolean invalidDateError;
private static int [] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

}

class SSNEdit
{
	public static String format (String SSNField)
	{
		return  SSNField.substring (0, 3) + "-" + SSNField.substring (3, 5) + "-" + SSNField.substring (5, 9);

	}

	public static String parse (String SSNField)
	{
		editBuffer = new StringBuffer (SSNField);

		characterCount = 0;

		invalidCharactersError = false;
		tooManyCharactersError = false;

		for (i = 0; i < editBuffer.length (); i++)
		{
			switch (editBuffer.charAt (i))
			{
				case '-':
					editBuffer.deleteCharAt (i);
					i--;
					break;
				case '0':
					characterCount++;
					break;
				case '1':
					characterCount++;
					break;
				case '2':
					characterCount++;
					break;
				case '3':
					characterCount++;
					break;
				case '4':
					characterCount++;
					break;
				case '5':
					characterCount++;
					break;
				case '6':
					characterCount++;
					break;
				case '7':
					characterCount++;
					break;
				case '8':
					characterCount++;
					break;
				case '9':
					characterCount++;
					break;
				default:
					invalidCharactersError = true;
					editBuffer.deleteCharAt (i);
					i--;
					break;
			}
		}

		if (characterCount > 9)
			tooManyCharactersError = true;

// pad with leading zeros

		while (editBuffer.length () < 9)
			editBuffer.insert (0, '0');

		return editBuffer.toString ();
	}

	public static boolean invalidCharacters ()
	{
		return invalidCharactersError;
	}

	public static boolean tooManyCharacters ()
	{
		return tooManyCharactersError;
	}

private static StringBuffer editBuffer;
private static int i;
private static int characterCount;
private static boolean invalidCharactersError;
private static boolean tooManyCharactersError;
}


