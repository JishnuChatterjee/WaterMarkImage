package com.jishnu;



import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/*
 * WaterMarkerFileChooser.java uses these files:
 *   images/Open16.gif
 *   images/Save16.gif
 */
public class WaterMarkerFileChooser extends JPanel
                             implements ActionListener {
    private JButton inputButton, outputButton,startButton;
    private  JTextArea log;
    private  JFileChooser fc;
    private JTextField inputPath;
    private JTextField outputPath;
    private File inputFile;
    private File outputFile;
    private  class CustomDocument extends PlainDocument {
    	  private  int MAX_LENGTH = 30	;
    	 

    	  public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

    	    if(str != null && log.getText().length() < MAX_LENGTH ) 
    	    {
    	      super.insertString(offs, str, a);
    	    }
    	    else {
    		  JOptionPane.showMessageDialog(WaterMarkerFileChooser.this, "Message cannot be more than 30 characters", "Message Error", JOptionPane.ERROR_MESSAGE);
    	    }
    	    
    	  }
    	}
    
    public WaterMarkerFileChooser() {
       // super(new BoxLayout(,BoxLayout.Y_AXIS));

        //Create the log first, because the action listeners
        //need to refer to it.
        log = new JTextArea(2,20);
        log.setMargin(new Insets(5,5,5,5));
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setEditable(true);
        log.setDocument(new CustomDocument());
        JScrollPane logScrollPane = new JScrollPane(log);
        
        //Create a file chooser
        fc = new JFileChooser();

        //Uncomment one of the following lines to try a different
        //file selection mode.  The first allows just directories
        //to be selected (and, at least in the Java look and feel,
        //shown).  The second allows both files and directories
        //to be selected.  If you leave these lines commented out,
        //then the default mode (FILES_ONLY) will be used.
        //
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        //Create the open button.  We use the image from the JLF
        //Graphics Repository (but we extracted it from the jar).
        inputPath=new JTextField(20);
        inputPath.setMargin(new Insets(5,5,5,5));
        inputPath.setEditable(false);
        inputButton = new JButton("Input Dir");
        inputButton.addActionListener(this);
        JPanel inputPanel = new JPanel(); //use FlowLayout
        inputPanel.add(inputPath);
        inputPanel.add(inputButton);
        //Create the save button.  We use the image from the JLF
        //Graphics Repository (but we extracted it from the jar).
        outputPath=new JTextField(20);
        outputPath.setMargin(new Insets(5,5,5,5));
        outputPath.setEditable(false);
        outputButton = new JButton("Output Dir");
        outputButton.addActionListener(this);
        JPanel outputPanel = new JPanel(); //use FlowLayout
        outputPanel.add(outputPath);
        outputPanel.add(outputButton);
        
        JPanel startPanel = new JPanel(); //use FlowLayout
        startButton=new JButton("start");
        startButton.addActionListener(this);
        startPanel.add(startButton);

        //Add the buttons and the log to this panel.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(inputPanel);
        add(outputPanel);
        add(logScrollPane);
        add(startPanel);
    }

    public void actionPerformed(ActionEvent e) {

        //Handle open button action.
        if (e.getSource() == inputButton) {
            int returnVal = fc.showOpenDialog(WaterMarkerFileChooser.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
            	if(outputFile==null || !fc.getSelectedFile().getPath().equals(outputFile.getPath())){
            		inputFile = fc.getSelectedFile();
            		inputPath.setText(inputFile.getPath());
            	}
            	else{
            		JOptionPane.showMessageDialog(WaterMarkerFileChooser.this, "Input and Output folders must be different", "Message Error", JOptionPane.ERROR_MESSAGE);
            	}
            } 

        //Handle save button action.
        } else if (e.getSource() == outputButton) {
            int returnVal = fc.showSaveDialog(WaterMarkerFileChooser.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
            	if(inputFile==null || !fc.getSelectedFile().getPath().equals(inputFile.getPath())){
            		outputFile = fc.getSelectedFile();
            		outputPath.setText(outputFile.getPath());
            	}
            	else{
            		JOptionPane.showMessageDialog(WaterMarkerFileChooser.this, "Input and Output folders must be different", "Message Error", JOptionPane.ERROR_MESSAGE);
            	}
            	
            }
        }
        else if (e.getSource()==startButton){
        	if(inputFile!=null && outputFile!=null && log.getText().length()>0 && log.getText().trim().length() >0){
        		File[] inFilesArray = inputFile.listFiles(new FilenameFilter() {

        			public boolean accept(File dir, String name) {
        				if (name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith("jpeg")) {
        					return true;
        				}
        				return false;
        			}
        		});
        		if(inFilesArray.length==0){
        			
        			JOptionPane.showMessageDialog(WaterMarkerFileChooser.this, "Images must be of jpg or jpeg extension", "Message Error", JOptionPane.ERROR_MESSAGE);
        			return;
        		}
        		for (int i = 0; i < inFilesArray.length; i++) {
        			System.out.println("in file: " + inFilesArray[i].getName()
        					+ " out Dir: " + outputFile.getPath()
        					+ System.getProperty("file.separator")
        					+ inFilesArray[i].getName());
        			try{
        			WatermarkImage.createWaterMark(
        					inFilesArray[i],
        					new File(outputFile.getPath()
        							+ System.getProperty("file.separator")
        							+ inFilesArray[i].getName()), log.getText());
        			}
        			catch(Exception exp){
        				JOptionPane.showMessageDialog(WaterMarkerFileChooser.this, "Yikes!!!! Something went wrong :P", "Message Error", JOptionPane.ERROR_MESSAGE);
        				return;
        			}
        
        		}
        		
        			JOptionPane.showMessageDialog(WaterMarkerFileChooser.this, "All images with extension jpg and jpeg watermarked successfully", "Message Success", JOptionPane.PLAIN_MESSAGE);
        		
        	}
        	else{
        		JOptionPane.showMessageDialog(WaterMarkerFileChooser.this, "Please Select Input Directory, Output Directory and Watermark Text before clicking start", "Message Error", JOptionPane.ERROR_MESSAGE);
        	}
        }
    }
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("WaterMarkerFileChooser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new WaterMarkerFileChooser());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE); 
                createAndShowGUI();
            }
        });
    }
}
