/*
 * Created on Jun 13, 2006
 * 
 * Copyright 2006 Marc W??rlein
 * 
 * This file is part of ParMol.
 * ParMol is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * ParMol is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ParMol; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 */
package de.parmol;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import de.parmol.FFSM.Matrix;
import de.parmol.graph.Graph;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.parsers.PDGParser;
import de.parmol.util.FragmentSet;
import de.parmol.util.FrequentFragment;

/**
 * This class is a small demo tool to demonstrate the funktoinality of the parmol package
 *
 * @author Marc Woerlein <Woerlein@informatik.uni-erlangen.de>
 *
 */
public class Demo extends Thread implements ActionListener{

	static long startTime;
	static long costTime;
	
	private final Demo me;
	private static final String[] options=new String[]{"-findPathsOnly=flase","-findTreesOnly=true","-findPathsOnly=true"};
	private GraphView resultGraph;
	boolean isGraphView=false;
	boolean isSubgraph=true;
	
	private Demo() { me=this; }
	private JPanel createPane(){
		GridBagLayout gb=new GridBagLayout();
        JPanel pane = new JPanel(gb);
        //pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        pane.setLayout(gb);
        
        GridBagConstraints c = new GridBagConstraints();
        
        database=new JTextField("");//("data/IC93.sln.gz");
        frequency=new JTextField("");//("10%");
        outputfile=new JTextField("");
        start=new JButton("start search");
        start.addActionListener(this);
        change = new JButton("change view mode");
        change.addActionListener(this);
        graphBox=new JComboBox(new String[]{});
        graphBox.addActionListener(this);
        fragmentBox=new JComboBox(new String[]{});
        fragmentBox.addActionListener(this);
        closed = new JCheckBox("output more statement");

        
        c.fill = GridBagConstraints.BOTH;
        c.anchor=GridBagConstraints.CENTER;
        c.gridx=2;
        c.gridy=0;
        c.weightx=3.0;
        c.weighty=6.0;
        c.gridwidth = 5;
        c.gridheight = 7;
        chemPane=new JPanel(new GridLayout(1,1));
        gb.setConstraints(chemPane,c);
        pane.add(chemPane);
        
        

        JLabel ack=new JLabel("");
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        c.gridx=0;
        c.gridy=0;
        c.weightx=1.0;
        c.weighty=2.0;
        c.gridwidth = 2;
        c.gridheight = 1;
        gb.setConstraints(ack,c);
        pane.add(ack);
        
        //文本框--------------------------------------------
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor=GridBagConstraints.CENTER;
        c.gridx=1;
        c.weightx=1.0;
        c.weighty=2.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        Dimension textsize=new Dimension(130,25);

        
        c.gridy=1;
        database.setMaximumSize(textsize);
        database.setSize(textsize);
        database.setPreferredSize(textsize);
        gb.setConstraints(database,c);
        pane.add(database);
        
        c.gridy=2;
        frequency.setMaximumSize(textsize);
        frequency.setSize(textsize);
        frequency.setPreferredSize(textsize);
        gb.setConstraints(frequency,c);
        pane.add(frequency);

        c.gridy=3;
        outputfile.setMaximumSize(textsize);
        outputfile.setSize(textsize);
        outputfile.setPreferredSize(textsize);
        gb.setConstraints(outputfile,c);
        pane.add(outputfile);
        
        
        //标签文本---------------------------------------------
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        c.gridx=0;
        c.weightx=0.0;
        c.weighty=2.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        
        
        ack=new JLabel("database: ");      
        c.gridy=1;
        gb.setConstraints(ack,c);
        pane.add(ack);
        
        ack=new JLabel("frequency: ");
        c.gridy=2;
        gb.setConstraints(ack,c);
        pane.add(ack);
        
        ack=new JLabel("outputfile: ");
        c.gridy=3;
        gb.setConstraints(ack,c);
        pane.add(ack);
        
        
        //-------------------------------------------- 
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        c.gridx=0;
        c.gridy=4;
        c.weightx=1.0;
        c.weighty=2.0;
        c.gridwidth = 2;
        c.gridheight = 1;
        //c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(closed,c);
        pane.add(closed);

        //--------------------------------------------
        ack=new JLabel("");
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        c.gridx=0;
        c.gridy=5;
        c.weightx=1.0;
        c.weighty=2.0;
        c.gridwidth = 2;
        c.gridheight = 1;
        //c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(ack,c);
        pane.add(ack);
        
        ack=new JLabel("");
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        c.gridx=0;
        c.gridy=6;
        c.weightx=1.0;
        c.weighty=2.0;
        c.gridwidth = 2;
        c.gridheight = 1;
        //c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(ack,c);
        pane.add(ack);
        
        /*ack=new JLabel("");
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weighty=1.0;
        gb.setConstraints(ack,c);
        pane.add(ack);*/

        //------------------------------------------------------
        c.fill = GridBagConstraints.BOTH;
        c.anchor=GridBagConstraints.CENTER;
        c.weighty=1.0;
        c.gridheight = 1;
        c.gridy=7;
        
        
        c.weightx=0.0;
        c.gridx=6;       
        c.gridwidth=1;
        gb.setConstraints(change,c);
        pane.add(change);
        
        c.weightx=3.0;
        c.gridx=3;
        c.gridwidth=3;
        gb.setConstraints(graphBox,c);
        pane.add(graphBox);


        c.weightx=3.0;
        c.gridx=1;
        c.gridwidth=2;
        gb.setConstraints(fragmentBox,c);
        pane.add(fragmentBox);
   
        c.weightx=0.0;
        c.gridx=0;
        c.gridwidth=1;
        //c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(start,c);
        pane.add(start);

        return pane;
	}
	
	Graph[] graphs;
	FrequentFragment[] ff;
	int lastFF=-1;

	private JPanel chemPane;
	private JComboBox algo;
	private JTextField database;
	private JTextField frequency;
	private JTextField outputfile;
	private JButton start;
	private JButton change;
	private JComboBox graphBox;
	private JComboBox fragmentBox;
	private JComboBox typeBox;
	private JCheckBox closed;
	
	private AbstractMiner miner;
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event){
		final String command=event.getActionCommand();
		if (command.equals("start search")){

			
			
			Thread t=new Thread(){
				public void run() {
					me.actionPerformed(new ActionEvent(this,0,"running"));
					
					try{
						Settings settings;
						Settings.outputMoreStatement=closed.isSelected();
						if (false) {
							settings=new Settings(
									new String[]{"-graphFile="+database.getText(),"-outputFile=data/out.txt",
											"-minimumFrequencies="+frequency.getText(),
											"-debug=-1"
										});
						}
						else {
							settings=new Settings(
									new String[]{"-graphFile="+database.getText(),"-outputFile="+"data/"+outputfile.getText(),
											"-minimumFrequencies="+frequency.getText(),
											"-debug=-1"
										});
						}
						/*switch (algo.getSelectedIndex()) {						
						case 3: miner = new de.parmol.GSpan.Miner(settings);
							break;
						case 1: miner = new de.parmol.MoFa.Miner(settings);
							break;
						case 2: miner = new de.parmol.Gaston.Miner(settings);
							break;
						case 0: miner = new de.parmol.FFSM.Miner(settings);
							break;
						default:
							miner = new de.parmol.FFSM.Miner(settings);
						}*/
					startTime = System.nanoTime();
					miner = new de.parmol.FFSM.Miner(settings);
					miner.setUp();
					miner.startMining();
					miner.printFrequentSubgraphs();
					
					costTime = System.nanoTime()-startTime;
					AbstractMiner.costTime = costTime;
					FragmentSet frags=miner.getFrequentSubgraphs();
					int size=frags.size();
					for (Iterator it=frags.iterator();it.hasNext();){
						final FrequentFragment frag=(FrequentFragment) it.next();
						if (frag.getFragment().getNodeCount()==1) --size;
					}
					ff=new FrequentFragment[size];
					int i=-1;
					int sign = 1;
					for (Iterator it=frags.iterator();it.hasNext();){
						final FrequentFragment frag=(FrequentFragment) it.next();
						if (frag.getFragment().getNodeCount()>1){
							ff[++i]=frag;
							sign = 0;
//							if (SimpleGraphComparator.instance.compare(detect,frag.getFragment())==0) 
//							System.err.println("Fragment "+(i+1));
						}
					}
					
					//////////////////////////////////////////////
					if (sign == 1) ff = null;

					}catch(Exception e){
						me.actionPerformed(new ActionEvent(e,0,"EXCEPTION"));
					}catch(Error e){
						me.actionPerformed(new ActionEvent(e,0,"ERROR"));
					}

					/*/////////////////////////////////////////////////////////////////
					FileOutputStream myOutputFile = null;
					try {
						myOutputFile = new FileOutputStream(new File("data\\result.txt"));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if (ff != null){
						for(FrequentFragment fff: ff)
						{
							Graph g = fff.getFragment();
							
							if (g instanceof Matrix) g=new UndirectedListGraph((Matrix)g);
							
							try {
								myOutputFile.write(PDGParser.instance.serialize(g).getBytes());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}*/
					
					me.actionPerformed(new ActionEvent(this,0,"search done"));
				}
			};
			t.start();
			
		}else if (command.equals("comboBoxChanged")){
			
			if (event.getSource()==fragmentBox) {
				
				
				
				int index=fragmentBox.getSelectedIndex();
				if (index>=0 && index<ff.length) {
					final FrequentFragment frag=ff[index];
					graphs=frag.getSupportedGraphs();
					if (index!=lastFF){ 
						graphBox.removeAllItems();
						for (int i=0;i<graphs.length;++i) graphBox.addItem(graphs[i].getName());
						lastFF=index;
					}
					try {
						showGraph(frag.getFragment());
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					isSubgraph = true;
				}
			} else {
				int index=graphBox.getSelectedIndex();
				if (lastFF!=-1 && graphs!=null && index>=0 && index<graphs.length) {
					try {
						showGraph(graphs[index]);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				isSubgraph = false;
			}
		}else if (command.equals("running")){
			start.setEnabled(false);
			graphBox.setEnabled(false);
			fragmentBox.setEnabled(false);
			start.setText("running");
			graphs=null;
			ff=null;
			lastFF=-1;
			graphBox.removeAllItems();
			fragmentBox.removeAllItems();
			showObject(null);
		}else if (command.equals("search done")){
			
			if (ff!=null){
				for (int i=0;i<ff.length;++i) fragmentBox.addItem("Fragment "+(i+1));
				fragmentBox.setSelectedIndex(0);
				
				/*for (FrequentFragment f:ff) {
					if (f.getFragment().getNodeCount() < 2) continue;
					
				}
				*/
				
			}
			
			
			
			start.setText("start search");
			fragmentBox.setEnabled(true);
			graphBox.setEnabled(true);
			start.setEnabled(true);
		}else if (command.equals("EXCEPTION")){
			System.err.println("exception detected: "+event.getSource());
		}else if (command.equals("ERROR")){
			System.err.println("error detected: "+event.getSource());
		}else if(command.equals("change view mode")){
			isGraphView = !isGraphView;
			//		
			if (isSubgraph) {
				int index=fragmentBox.getSelectedIndex();
				if (index >= 0 && index < ff.length) {
					final FrequentFragment frag = ff[index];
					
					try {
						showGraph(frag.getFragment());
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				int index=graphBox.getSelectedIndex();
				if (lastFF!=-1 && graphs!=null && index>=0 && index<graphs.length) {
					try {
						showGraph(graphs[index]);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}else{ System.err.println("Unkown Event: "+command); }
	}
	
	
	
	private final void showObject(final JComponent p){
		chemPane.removeAll();//.remove(lastDemo);
		if (p!=null) {
			p.setSize(chemPane.getSize());
			chemPane.add(p);
		}
		chemPane.repaint();
	}
	private void showGraph(Graph g) throws FileNotFoundException{
		/*f (g instanceof Matrix) g=new UndirectedListGraph((Matrix)g);
		JTextArea result = new JTextArea();
		result.setText(PDGParser.instance.serialize(g));
		showObject(result);
		*/
		
		if (isGraphView){
			resultGraph = new GraphView(g);
			chemPane.removeAll();
			if (resultGraph!=null) {
				resultGraph.setSize(chemPane.getSize());
				chemPane.add(resultGraph);
				resultGraph.init();
			}
			chemPane.repaint();
		} else{
			chemPane.removeAll();
			
			JTextArea result = new JTextArea();
			//result.setText((FrequentFragment)g.toString(miner.m_settings.serializer));
			result.setText(PDGParser.instance.serialize(g));
	        JScrollPane s = new JScrollPane(result);
	        
	        if(s == null) return;
	        s.setPreferredSize(chemPane.getSize());
			chemPane.add(s);
			chemPane.repaint();
			
			frame.setSize(frame.getWidth()-1,frame.getHeight()-1);
			frame.setSize(frame.getWidth()+1,frame.getHeight()+1);
			
			/*Toolkit tk = Toolkit.getDefaultToolkit();  
	        Dimension d = tk.getScreenSize(); 
	        
	        frame.setSize(d);*/
	        //showObject(s);
		}
	}
	
	JFrame frame;
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
	public void run(){
		System.out.close();
        //Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);
		//Create and set up the window.
		//JFrame frame = new JFrame("HFFSM - heway");
		frame = new JFrame("HFFSM - heway");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(createPane(), BorderLayout.CENTER);
		frame.setSize(550,250);
		frame.setLocationRelativeTo(null); 
		
		//Display the window.
		frame.setVisible(true);
		
    }


	/**
	 * start a new Demo
	 * @param args
	 */
    public static void main(String[] args){
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
    	
        javax.swing.SwingUtilities.invokeLater(new Demo());
	}

}
