package org.cehl.cehltools.draft;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class DraftSimulator {

	LinkedHashMap<String,Double> teamMap;
	LinkedHashSet<String> finalOrder;
	RandomCollection<String> rc;
	
	int rounds = 3;
	
    public static void main(String[] args) {
    	DraftSimulator sim = new DraftSimulator();
    	//sim.test();
    	sim.run();
    }
    
    void run() {
    	//init();
    //	runDraft();
    	
    	SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				displayFrame();
			}
    		
    	});
		
    }
    
    void init() {
    	
    	teamMap = new LinkedHashMap<>();
    	finalOrder = new LinkedHashSet<String>();
    	rc = new RandomCollection<>();
    	
    	teamMap.put("Phoenix",18.5);
    	teamMap.put("Detroit",13.5);
    	teamMap.put("Philly",11.5);
    	teamMap.put("Islanders",9.5);
    	teamMap.put("Winnipeg",8.5);
    	teamMap.put("Buffalo",7.5);
    	teamMap.put("Calgary",6.5);
    	teamMap.put("St. Louis",6.0);
    	teamMap.put("Tampa Bay",5.0);
    	teamMap.put("LosAngeles",3.5);
    	teamMap.put("Boston",3.0);
    	teamMap.put("Ottawa",2.75);
    	teamMap.put("Colorado",2.25);
    	teamMap.put("Vancouver",2.0);
    	
    	for(Entry<String, Double> entry : teamMap.entrySet()) {
    		rc.add(entry.getValue(), entry.getKey());
    	}
    }
    
    void runDraft() {
    	//3 rounds starting with first
		for (int i = 1; i <= rounds; i++) {
			
			String result = rc.next();
			
			while(true) {
				if(finalOrder.contains(result)) {
					result = rc.next();
				}else {
					break;
				}

			}
			
			
			finalOrder.add(result);
			teamMap.remove(result);

		} 
		
		//add remaining teams to final order
		finalOrder.addAll(teamMap.keySet());
    }
    

	public class RandomCollection<E> {
	    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
	    private final Map<E, Double > weightMap = new HashMap<>();
	    private final Random random;
	    private double total = 0;

	    public RandomCollection() {
	        this(new Random());
	    }

	    public RandomCollection(Random random) {
	        this.random = random;
	    }

	    public RandomCollection<E> add(double weight, E result) {
	        if (weight <= 0) return this;
	        total += weight;
	        map.put(total, result);
	        weightMap.put(result, weight);
	        return this;
	    }
	    
//	    public void removeEntry(double key, E value) {
//	        total -= weightMap.get(value);
//	    	map.remove(key,value);
//	    }

	    public E next() {
	     
	        
	        double nextDouble = random.nextDouble();
	        
	        System.out.println("random = " + nextDouble + ". total = " + total + ". value to check =" + nextDouble * total);

	        double value = nextDouble * total;
	        
	        Entry<Double, E> entry = map.higherEntry(value);
	        
	       // removeEntry(entry.getKey(), entry.getValue());
	        
	        return entry.getValue();
	    }
	}
	
	public void displayFrame() {
		 //Creating the Frame
        JFrame frame = new JFrame("Chat Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        //Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("File");
        mb.add(m1);
        JMenuItem m11 = new JMenuItem("Exit");
        m1.add(m11);


        //Creating the panel at bottom and adding components
        JPanel panel = new JPanel(); // the panel is not visible in output
        JButton btnRun = new JButton("Run Lottery Draft");

        panel.add(btnRun);
        
        JPanel mainPanel = new JPanel(new GridLayout());
        
      //  Object[] header = { "Order", "Logo"};
        Object[] header = { "Order", "Logo"};
        
        //DefaultTableModel model = new DefaultTableModel(new Object[][] {}, header);
        
        DefaultTableModel model = new DefaultTableModel(new Object[][] {}, header)
        {
            //  Returning the Class of each column will allow different
            //  renderers to be used based on Class
            public Class getColumnClass(int column)
            {
                return getValueAt(0, column).getClass();
            }
        };

        //mainPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "ODI Rankings", TitledBorder.CENTER, TitledBorder.TOP));
	    

	     JTable table = new JTable(model);
	     table.setPreferredScrollableViewportSize(frame.getPreferredSize());
	     
	     //center text, configure font
	     DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
	     centerRenderer.setHorizontalAlignment(JLabel.CENTER);
	     table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
	     
	      Font font = table.getFont();
          font = font.deriveFont((float) (font.getSize2D() * 3));
          table.setFont(font);
	     
	     
	     JScrollPane scroll = new JScrollPane(table);
	     mainPanel.add(scroll);
	     
	     btnRun.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
		
				
				SwingWorker worker = new SwingWorker() {

					@Override
					protected Object doInBackground() throws Exception {
						
						//remove rows if exists
						if (model.getRowCount() > 0) {
						    for (int i = model.getRowCount() - 1; i > -1; i--) {
						    	model.removeRow(i);
						    }
						}
						
						btnRun.setEnabled(false);
						
						init();
					    runDraft();
					
						LinkedList<String> list = new LinkedList<>(finalOrder);
						Iterator<String> itr = list.descendingIterator();
						int count = list.size();
						while(itr.hasNext()) {
							String item = itr.next();
							System.out.println("Pick " + count + " = " + item);

							model.insertRow(0, new Object[] { count,getIcon(item)});
							table.setRowHeight(0, 60);
							
							
			
							count--;
							
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
						
						return null;
					}
					
					 protected void done()
					    {
					        try
					        {
					        	btnRun.setEnabled(true);
					        	//frame.revalidate();
					        	//frame.pack();
					        }
					        catch (Exception e)
					        {
					            e.printStackTrace();
					        }
					    }
					
				};
				
				worker.execute();
				
//				
//				LinkedList<String> list = new LinkedList<>(finalOrder);
//				Iterator<String> itr = list.descendingIterator();
//				int count = list.size();
//				while(itr.hasNext()) {
//					String item = itr.next();
//					System.out.println("Pick " + count + " = " + item);
//
//					model.addRow(new String[] { String.valueOf(count),item,"test"});
//					
//					model.fireTableDataChanged();
//					table.revalidate();
//					table.repaint();
//					
//					count--;
//					
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}
				

				btnRun.setEnabled(false);
				//model.insertRow(0, new String[] {"2","Test","test"});
			}
	    	 
	     });

       

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setVisible(true);
	}
	
	private Icon getIcon(String name) {
		
		URL imgUrl = DraftSimulator.class.getClassLoader().getResource("logos/"+ name.toLowerCase() + ".png");

		ImageIcon imgicon = new ImageIcon(imgUrl);
		
		Image scaledImage = getScaledImage(imgicon.getImage(), 75,75);
		
		return new ImageIcon(scaledImage);
		
		// return new ImageIcon("logos/"+ name.toLowerCase() + ".png");
	}
	
	private Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
}
