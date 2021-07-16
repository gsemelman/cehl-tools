package org.cehl.cehltools.draft;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
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
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;

public class DraftSimulator2 {

	LinkedHashMap<String,Double> teamMap;
	LinkedHashSet<String> finalOrder;
	RandomCollection<String> rc;
	
	JPanel mainPanel;
	ImagePanel teamCardPanel = new ImagePanel();
	
	private AtomicBoolean isRecording = new AtomicBoolean(false);
	
	JFrame frame;
	
	private static final String TEAM_CARD = "TEAM_CARD";
	private static final String DRAFT_CARD = "DRAFT_CARD";
	private static final String CAPTURE_LOCATION ="c:/temp/capture/";
	private static final int PICK_DISPLAY_DELAY_SECONDS = 3;
	private static final int DRAFT_DISPLAY_DELAY_SECONDS = 5;
	
	int rounds = 3;
	
    public static void main(String[] args) {
    	DraftSimulator2 sim = new DraftSimulator2();
    	//sim.test();
    	sim.run();
    }
    
    void run() {
    	init();
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

    	teamMap.put("Phoenix",20.0); //18.5
    	teamMap.put("Philly",13.5); //13.5
    	teamMap.put("Detroit",11.5); //11.5
    	teamMap.put("Islanders",9.5);//9.5
    	teamMap.put("Winnipeg",8.5);
    	teamMap.put("Calgary",7.5);
    	teamMap.put("Vancouver",6.5);
    	teamMap.put("Tampa Bay",6.0);
    	teamMap.put("Montreal",5.0);
    	teamMap.put("Dallas",3.5);
    	teamMap.put("Buffalo",3.0);
    	teamMap.put("St. Louis",2.5);
    	teamMap.put("Nashville",2.0);
    	teamMap.put("Boston",1.0);
    	
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
        //JFrame frame = new JFrame("Chat Frame");
		frame = new JFrame("CEHL Lottery Draft");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 1000);

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
        
        //JPanel mainPanel = new JPanel(new CardLayout ());
        mainPanel = new JPanel(new CardLayout());
        
      //  Object[] header = { "Order", "Logo"};
        Object[] header = { "Initial"," Team", "Order", "Team"};
        
        
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
	    
         JPanel tablePanel = new JPanel(new BorderLayout());
	     JTable table = new JTable(model);
	     table.setPreferredScrollableViewportSize(frame.getPreferredSize());
	     
	     int count = 1;
	     for(String team : teamMap.keySet()) {
	    	 model.addRow(new Object[] { count,getIcon(team), count, new ImageIcon()}); 
	    	 table.setRowHeight(count -1, 60);
	    	 count++;
	     }
	     
	     
	     
	     //center text, configure font
	     DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
	     centerRenderer.setHorizontalAlignment(JLabel.CENTER);
	     table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
	     table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
	     
	      Font font = table.getFont();
          font = font.deriveFont((float) (font.getSize2D() * 3));
          table.setFont(font);
	     
	     //add table to scrollpane and to main panel
	     JScrollPane scroll = new JScrollPane(table);
	     tablePanel.add(scroll);
	     mainPanel.add(tablePanel,DRAFT_CARD);
	     mainPanel.add(teamCardPanel,TEAM_CARD);
	     
	     
	     btnRun.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				record();
				
				SwingWorker worker = new SwingWorker() {

					@Override
					protected Object doInBackground() throws Exception {
						
						//remove rows if exists
						if (model.getRowCount() > 0) {
						    for (int i = model.getRowCount() - 1; i > -1; i--) {
						    	model.setValueAt(new ImageIcon(), i, 3);
						    }
						}
						
						btnRun.setEnabled(false);
						
						init();
					    runDraft();
					
						LinkedList<String> list = new LinkedList<>(finalOrder);
						Iterator<String> itr = list.descendingIterator();
						int count = list.size();
						while(itr.hasNext()) {
							
							try {
								TimeUnit.SECONDS.sleep(DRAFT_DISPLAY_DELAY_SECONDS);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								throw new RuntimeException(e);
							}

							String item = itr.next();
							System.out.println("Pick " + count + " = " + item);
							
							displayTeam(item);

							//model.insertRow(0, new Object[] { count,getIcon(item)});
							//table.setRowHeight(0, 60);
							model.setValueAt(getIcon(item), count-1, 3);
							
							//model.getValueAt(count, 4);
							
			
							count--;
							

							
						}
						
						return null;
					}
					
					 protected void done()
					    {
					        try
					        {
					        	//sleep once winner is shown for 7 seconds
								try {
									TimeUnit.SECONDS.sleep(PICK_DISPLAY_DELAY_SECONDS);
									
								} catch (InterruptedException e) {
									Thread.currentThread().interrupt();
								}

					        	
					        	stopRecord();
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

				btnRun.setEnabled(false);

			}
	    	 
	     });

       

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setVisible(true);
	}
	
	private void displayTeam(String name) {
		
		URL imgUrl = DraftSimulator2.class.getClassLoader().getResource("logos/"+ name.toLowerCase() + ".png");
		ImageIcon imgicon = new ImageIcon(imgUrl);
		
		teamCardPanel.setImage(imgicon.getImage());
		
		CardLayout cl = (CardLayout)(mainPanel.getLayout());

		cl.show(mainPanel, TEAM_CARD);
		
		try {
			TimeUnit.SECONDS.sleep(PICK_DISPLAY_DELAY_SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cl.show(mainPanel, DRAFT_CARD);
		
	}
	
	private Icon getIcon(String name) {
		
		URL imgUrl = DraftSimulator2.class.getClassLoader().getResource("logos/"+ name.toLowerCase() + ".png");

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
	
	void record() {
        //Create a timer.
		
		File root = new File(CAPTURE_LOCATION);
		if(root.exists()) {
			
			try {
				FileUtils.cleanDirectory(root);
			} catch (IOException e) {
				
			} 
		}else {
			root.mkdirs();
		}
		
	
		
		isRecording.set(true);
		
		Timer timer = new Timer(30, new MyTimerActionListener(root));
		
		timer.start();
		
//		SwingWorker worker = new SwingWorker() {
//
//			@Override
//			protected Object doInBackground() throws Exception {
//	
//				int count = 1;
//				while(true) {
//					try {
//
//						
//		            	File file = new File(root, count + "_cap" + ".png");
//
//		            	makeScreenshot(frame, file);
//		
//						Thread.sleep(33); //30fps ish
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//					if(!isRecording.get()) {
//						return count;
//					}
//					
//	            	count++;
//				}
//				
//				//return null;
//			}
//			
//		};
//		
//		worker.execute();

	}
	
	class MyTimerActionListener implements ActionListener {
		
		File root;
		
        public MyTimerActionListener(File root) {
        	this.root = root;
        }
		
		int count = 1;
		
		  public void actionPerformed(ActionEvent e) {


			  File file = new File(root, count + "_cap" + ".png");

			  makeScreenshot(frame, file);
			  count++;

		  }
		}
	
	void stopRecord() {
		isRecording.set(false);
		 createMovie();
	}
	
	void createMovie() {

		try {

	        File outputFile = new File(CAPTURE_LOCATION, "lottery-draft.mp4");
	        		
	        JCodecPNGtoMP4.generateVideoBySequenceImages(outputFile.getAbsolutePath(), CAPTURE_LOCATION, "png");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static final void makeScreenshot(JFrame argFrame, File file) {
	    Rectangle rec = argFrame.getBounds();
	    BufferedImage bufferedImage = new BufferedImage(rec.width, rec.height, BufferedImage.TYPE_INT_RGB);
	     argFrame.paint(bufferedImage.getGraphics());

	    try {
	        // Create temp file
	        //File temp = File.createTempFile("screenshot", ".png");
	    	//File temp = new File

	        // Use the ImageIO API to write the bufferedImage to a temporary file
	        ImageIO.write(bufferedImage, "png", file);

	        // Delete temp file when program exits
	        //temp.deleteOnExit();
	    } catch (IOException ioe) {
	        ioe.printStackTrace();
	    }
	}
	
	class ImagePanel extends JPanel {

		  private Image img;

		  public ImagePanel(String img) {
		    this(new ImageIcon(img).getImage());
		  }
		  
		  public ImagePanel() {
			  
		  }

		  public ImagePanel(Image img) {
		    this.img = img;
		    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		    setPreferredSize(size);
		    setMinimumSize(size);
		    setMaximumSize(size);
		    setSize(size);
		    setLayout(null);
		  }

		  public void paintComponent(Graphics g) {
		   // g.drawImage(img, 0, 0, null);
			  super.paintComponent(g);
			    Graphics2D g2d = (Graphics2D) g;
			    int x = (this.getWidth() - img.getWidth(null)) / 2;
			    int y = (this.getHeight() - img.getHeight(null)) / 2;
			    g2d.drawImage(img, x, y, null);
		  }
		  
			
			private void setImage(Image img) {
				 this.img = img;
				 Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
				    setPreferredSize(size);
				    setMinimumSize(size);
				    setMaximumSize(size);
				    setSize(size);
				    
				    this.revalidate();
				    this.repaint();
			}

	}
}
