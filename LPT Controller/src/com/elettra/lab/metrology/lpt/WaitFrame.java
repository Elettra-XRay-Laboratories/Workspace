package com.elettra.lab.metrology.lpt;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WaitFrame extends JFrame
{
	private static final long serialVersionUID = 7784794628794955380L;

	private static WaitFrame  uniqueInstance;

	public static synchronized WaitFrame getInstance()
	{
		if (uniqueInstance == null)
			uniqueInstance = new WaitFrame();

		return uniqueInstance;
	}

	private WaitFrame()
	{
		super("Wait");

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setBounds(50, 50, 400, 100);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0 };
		gridBagLayout.rowHeights = new int[] { 0 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0 };
		getContentPane().setLayout(gridBagLayout);

		JPanel leftPanel = new JPanel();
		GridBagConstraints gbc_leftPanel = new GridBagConstraints();
		gbc_leftPanel.insets = new Insets(10, 5, 5, 5);
		gbc_leftPanel.fill = GridBagConstraints.BOTH;
		gbc_leftPanel.gridx = 0;
		gbc_leftPanel.gridy = 0;
		getContentPane().add(leftPanel, gbc_leftPanel);

		JLabel label = new JLabel("Wait...Initializing GUI");
		label.setFont(new Font("Tahoma", Font.PLAIN, 14));

		leftPanel.add(label);
	}
}
