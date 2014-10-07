package com.elettra.lab.powder.diffractometer.panels;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import java.awt.Color;

public class SuggestedZReferencePanel extends JPanel
{

	/**
   * 
   */
  private static final long serialVersionUID = 7412470654088924805L;

	/**
	 * Create the panel.
	 */
	public SuggestedZReferencePanel()
	{
		GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.columnWidths = new int[]{0};
		gridBagLayout_1.rowHeights = new int[]{0};
		gridBagLayout_1.columnWeights = new double[]{1.0};
		gridBagLayout_1.rowWeights = new double[]{1.0};
		setLayout(gridBagLayout_1);
		JTabbedPane tabbedPane = new JTabbedPane();	
		JPanel innerPanel = new JPanel();
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		innerPanel.setLayout(gridBagLayout);
		
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		this.add(tabbedPane, gbc_tabbedPane);
		tabbedPane.add(innerPanel);
		tabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		tabbedPane.setTitleAt(0, "Z Position");
		
		JLabel lblApproximatedZPosition = new JLabel("Approximated Z Position for Spinner:");
		GridBagConstraints gbc_lblApproximatedZPosition = new GridBagConstraints();
		gbc_lblApproximatedZPosition.anchor = GridBagConstraints.EAST;
		gbc_lblApproximatedZPosition.insets = new Insets(10, 50, 5, 5);
		gbc_lblApproximatedZPosition.gridx = 0;
		gbc_lblApproximatedZPosition.gridy = 0;
		innerPanel.add(lblApproximatedZPosition, gbc_lblApproximatedZPosition);
		
		JLabel lblMm = new JLabel("11.600 mm");
		lblMm.setForeground(new Color(0, 102, 51));
		lblMm.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblMm = new GridBagConstraints();
		gbc_lblMm.anchor = GridBagConstraints.WEST;
		gbc_lblMm.insets = new Insets(10, 0, 5, 5);
		gbc_lblMm.gridx = 1;
		gbc_lblMm.gridy = 0;
		innerPanel.add(lblMm, gbc_lblMm);
		
		JLabel lblApproximatedZPosition_1 = new JLabel("Approximated Z Position for Fixed Sample Holder:");
		GridBagConstraints gbc_lblApproximatedZPosition_1 = new GridBagConstraints();
		gbc_lblApproximatedZPosition_1.insets = new Insets(0, 5, 5, 5);
		gbc_lblApproximatedZPosition_1.gridx = 0;
		gbc_lblApproximatedZPosition_1.gridy = 1;
		innerPanel.add(lblApproximatedZPosition_1, gbc_lblApproximatedZPosition_1);
		
		JLabel lblMm_1 = new JLabel("57.100 mm");
		lblMm_1.setForeground(new Color(0, 102, 51));
		lblMm_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblMm_1 = new GridBagConstraints();
		gbc_lblMm_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblMm_1.gridx = 1;
		gbc_lblMm_1.gridy = 1;
		innerPanel.add(lblMm_1, gbc_lblMm_1);
		
		JLabel lblApproximatedZPosition_2 = new JLabel("Approximated Z Position for Phi Motor:");
		GridBagConstraints gbc_lblApproximatedZPosition_2 = new GridBagConstraints();
		gbc_lblApproximatedZPosition_2.anchor = GridBagConstraints.EAST;
		gbc_lblApproximatedZPosition_2.insets = new Insets(0, 5, 5, 5);
		gbc_lblApproximatedZPosition_2.gridx = 0;
		gbc_lblApproximatedZPosition_2.gridy = 2;
		innerPanel.add(lblApproximatedZPosition_2, gbc_lblApproximatedZPosition_2);
		
		JLabel lblMm_2 = new JLabel("10.000 mm");
		lblMm_2.setForeground(new Color(0, 102, 51));
		lblMm_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblMm_2 = new GridBagConstraints();
		gbc_lblMm_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblMm_2.gridx = 1;
		gbc_lblMm_2.gridy = 2;
		innerPanel.add(lblMm_2, gbc_lblMm_2);

	}

}
