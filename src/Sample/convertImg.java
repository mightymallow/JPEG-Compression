package Sample;

import java.io.File;
import java.io.IOException;
import java.awt.AWTException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;

import javax.swing.*;
import javax.imageio.*;

public class convertImg extends JFrame implements ActionListener{
	
	JButton m_btOpen, m_btSave, m_btConvert, m_btConvertU, m_btConvertV, m_btConvert5, m_btConvert4, m_btConvert2, m_btConvert1, m_btConvert050, m_btConvert025, m_btConvert020, m_btConvertLowY, m_btConvertHighY;
	IMGPanel  m_panelImgInput, m_panelImgOutput;
	BufferedImage m_imgInput, m_imgOutput;
	//Create a file chooser
	final JFileChooser m_fc = new JFileChooser();
	
	//Declare quantization tables and necessary global variables
    private double[][] luminanceTable = { {16,11,10,16,24,40,51,61} , {12,12,14,19,26,58,60,55} , {14,13,16,24,40,57,69,56} , {14,17,22,29,51,87,80,62} , {18,22,37,56,68,109,103,77} , {24,35,55,64,81,104,113,92} , {49,64,78,87,103,121,120,101} , {72,92,95,98,112,100,103,99} };
    private double[][] chrominanceTable = { {17,18,24,47,99,99,99,99} , {18,21,26,66,99,99,99,99} , {24,26,56,99,99,99,99,99} , {47,66,99,99,99,99,99,99} , {99,99,99,99,99,99,99,99} , {99,99,99,99,99,99,99,99} , {99,99,99,99,99,99,99,99} , {99,99,99,99,99,99,99,99} };
	private final int BLOCK_WIDTH = 8;
	private final int BLOCK_HEIGHT = 8;
	private final double MATH_BLOCK = 8;
	private double[][] tArray = new double[BLOCK_HEIGHT][BLOCK_WIDTH];
	private double[][] transposedTArray = new double[BLOCK_HEIGHT][BLOCK_WIDTH];
	private double scalingFactor;
	private boolean isGrey;
    
	//setup some GUI stuff
	public JPanel createContentPane (){	    
	    
		// We create a bottom JPanel to place everything on.
        JPanel totalGUI = new JPanel();
        totalGUI.setLayout(null);
	    
        m_panelImgInput = new IMGPanel();        
        m_panelImgInput.setLocation(10, 10);
        m_panelImgInput.setSize(800, 600);
	    totalGUI.add(m_panelImgInput);
	    
	    // create a panel for buttons
	    JPanel panelButtons = new JPanel();
	    panelButtons.setLayout(null);
	    panelButtons.setLocation(840, 50);
	    panelButtons.setSize(400, 950);
        totalGUI.add(panelButtons);
        
        m_panelImgOutput = new IMGPanel();
        m_panelImgOutput.setLocation(980, 10);
        m_panelImgOutput.setSize(800, 600);
        totalGUI.add(m_panelImgOutput);
	    
	    m_btOpen = new JButton("OPEN");
	    m_btOpen.setLocation(0, 0);
	    m_btOpen.setSize(120, 60);
	    m_btOpen.addActionListener(this);
	    panelButtons.add(m_btOpen);
	    
	    m_btSave = new JButton("SAVE");
	    m_btSave.setLocation(0, 60);
	    m_btSave.setSize(120, 60);
	    m_btSave.addActionListener(this);
	    panelButtons.add(m_btSave);
	    
	    m_btConvert = new JButton("RGB->Y");
	    m_btConvert.setLocation(0, 120);
	    m_btConvert.setSize(120, 60);
	    m_btConvert.addActionListener(this);
	    panelButtons.add(m_btConvert);
	    
	    m_btConvertU = new JButton("RGB->U");
	    m_btConvertU.setLocation(0, 180);
	    m_btConvertU.setSize(120, 60);
	    m_btConvertU.addActionListener(this);
	    panelButtons.add(m_btConvertU);
	    
	    m_btConvertV = new JButton("RGB->V");
	    m_btConvertV.setLocation(0, 240);
	    m_btConvertV.setSize(120, 60);
	    m_btConvertV.addActionListener(this);
	    panelButtons.add(m_btConvertV);
	    
	    m_btConvert5 = new JButton("Scale 5");
	    m_btConvert5.setLocation(0, 300);
	    m_btConvert5.setSize(120, 60);
	    m_btConvert5.addActionListener(this);
	    panelButtons.add(m_btConvert5);
	    
	    m_btConvert4 = new JButton("Scale 4");
	    m_btConvert4.setLocation(0, 360);
	    m_btConvert4.setSize(120, 60);
	    m_btConvert4.addActionListener(this);
	    panelButtons.add(m_btConvert4);
	    
	    m_btConvert2 = new JButton("Scale 2");
	    m_btConvert2.setLocation(0, 420);
	    m_btConvert2.setSize(120, 60);
	    m_btConvert2.addActionListener(this);
	    panelButtons.add(m_btConvert2);
	    
	    m_btConvert1 = new JButton("Scale 1");
	    m_btConvert1.setLocation(0, 480);
	    m_btConvert1.setSize(120, 60);
	    m_btConvert1.addActionListener(this);
	    panelButtons.add(m_btConvert1);
	    
	    m_btConvert050 = new JButton("Scale 0.75");
	    m_btConvert050.setLocation(0, 540);
	    m_btConvert050.setSize(120, 60);
	    m_btConvert050.addActionListener(this);
	    panelButtons.add(m_btConvert050);
	    
	    m_btConvert025 = new JButton("Scale 0.50");
	    m_btConvert025.setLocation(0, 600);
	    m_btConvert025.setSize(120, 60);
	    m_btConvert025.addActionListener(this);
	    panelButtons.add(m_btConvert025);
	    
	    m_btConvert020 = new JButton("Scale 0.20");
	    m_btConvert020.setLocation(0, 660);
	    m_btConvert020.setSize(120, 60);
	    m_btConvert020.addActionListener(this);
	    panelButtons.add(m_btConvert020);
	    
	    m_btConvertLowY = new JButton("LOW Y");
	    m_btConvertLowY.setLocation(0, 720);
	    m_btConvertLowY.setSize(120, 60);
	    m_btConvertLowY.addActionListener(this);
	    panelButtons.add(m_btConvertLowY);
	    
	    m_btConvertHighY = new JButton("HIGH Y");
	    m_btConvertHighY.setLocation(0, 780);
	    m_btConvertHighY.setSize(120, 60);
	    m_btConvertHighY.addActionListener(this);
	    panelButtons.add(m_btConvertHighY);
	    
	    
	    	    
	    totalGUI.setOpaque(true);
	    return totalGUI;
	}
	
    // This is the new ActionPerformed Method.
    // It catches any events with an ActionListener attached.
    // Using an if statement, we can determine which button was pressed
    // and change the appropriate values in our GUI.
    public void actionPerformed(ActionEvent evnt) {
        // button OPEN is clicked
    	if(evnt.getSource() == m_btOpen){
        	m_fc.addChoosableFileFilter(new ImageFilter());
        	m_fc.setAcceptAllFileFilterUsed(false);
        	int returnVal = m_fc.showOpenDialog(convertImg.this);
        	if (returnVal == JFileChooser.APPROVE_OPTION) {
                 File file = m_fc.getSelectedFile();
                 try {
                	 m_imgInput = ImageIO.read(file);
                     m_panelImgInput.setBufferedImage(m_imgInput);	
                 }catch (IOException ex) {
                	 //...
                 }
            }
        }
        // convert RGB to Y 
        else if(evnt.getSource() == m_btConvert){
        	if(m_imgInput == null)
        		return;
        	
        	int w = m_imgInput.getWidth(null);
        	int h = m_imgInput.getHeight(null);
        	int YValues[] = initialSetup(1, h, w);
        	
        	// write Y values to the output image
            m_imgOutput = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        	WritableRaster raster = (WritableRaster) m_imgOutput.getData();
        	raster.setPixels(0, 0, w, h, YValues);
        	m_imgOutput.setData(raster);
        	m_panelImgOutput.setBufferedImage(m_imgOutput);	
        	
        } else if (evnt.getSource() == m_btConvertU) {
        	if(m_imgInput == null)
        		return;
        	
        	int w = m_imgInput.getWidth(null);
        	int h = m_imgInput.getHeight(null);
        	int UValues[] = initialSetup(2, h, w);
        	
        	// write Y values to the output image
            m_imgOutput = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        	WritableRaster raster = (WritableRaster) m_imgOutput.getData();
        	raster.setPixels(0, 0, w, h, UValues);
        	m_imgOutput.setData(raster);
        	m_panelImgOutput.setBufferedImage(m_imgOutput);
        	
        } else if (evnt.getSource() == m_btConvertV) {
        	if(m_imgInput == null)
        		return;
        	
        	int w = m_imgInput.getWidth(null);
        	int h = m_imgInput.getHeight(null);
        	int VValues[] = initialSetup(3, h, w);
        	
        	// write Y values to the output image
            m_imgOutput = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        	WritableRaster raster = (WritableRaster) m_imgOutput.getData();
        	raster.setPixels(0, 0, w, h, VValues);
        	m_imgOutput.setData(raster);
        	m_panelImgOutput.setBufferedImage(m_imgOutput);
        
        } else if (evnt.getSource() == m_btConvert5) {
        	
        	isGrey = false;
        	scalingFactor = 5;
        	mainFunction();
        	
        } else if (evnt.getSource() == m_btConvert4) {
        	isGrey = false;
        	scalingFactor = 4;
        	mainFunction();
        	
        } else if (evnt.getSource() == m_btConvert2) {
        	isGrey = false;
        	scalingFactor = 2;
        	mainFunction();
        	
        } else if (evnt.getSource() == m_btConvert1) {
        	isGrey = false;
        	scalingFactor = 1;
        	mainFunction();
        	
        } else if (evnt.getSource() == m_btConvert050) {
        	isGrey = false;
        	scalingFactor = 0.50;
        	mainFunction();
        	
        } else if (evnt.getSource() == m_btConvert025) {
        	isGrey = false;
        	scalingFactor = 0.25;
        	mainFunction();
        	
        } else if (evnt.getSource() == m_btConvert020) {
        	isGrey = false;
        	scalingFactor = 0.20;
        	mainFunction();
        	
        } else if (evnt.getSource() == m_btConvertLowY) {
        	isGrey = true;
        	scalingFactor = 0.20;
        	mainFunction();
        } else if (evnt.getSource() == m_btConvertHighY) {
        	isGrey = true;
        	scalingFactor = 5;
        	mainFunction();
        	
        }
    	
        // button SAVE is clicked
        else if(evnt.getSource() == m_btSave){
        	if(m_imgOutput == null)
        		return;
        	m_fc.addChoosableFileFilter(new ImageFilter());
        	m_fc.setAcceptAllFileFilterUsed(false);
        	int returnVal = m_fc.showSaveDialog(convertImg.this);
        	if (returnVal == JFileChooser.APPROVE_OPTION) {
        		File file = m_fc.getSelectedFile();	
        		try {
            	    ImageIO.write(m_imgOutput, "jpg", file);
            	} catch (IOException e) {
            		//...
            	}
        	}
        }
    }
	
    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Convert Image");

        //Create and set up the content pane.
        convertImg demo = new convertImg();
        frame.setContentPane(demo.createContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(940, 360);
        frame.setVisible(true);
    }
    
	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
	
	private int[][] calculateDCT (int[][] inputArray, int height, int width) {
		
		double[][] tempArray = new double[height][width];
		int[][] adjustedArray = new int[height][width];
		int[][] outputArray = new int[height][width];
		
		
		//Test subtracting 128
		for (int index = 0; index < height; index++) {
			for (int index2 = 0; index2 < width; index2++) {
				adjustedArray[index][index2] = (inputArray[index][index2]) - 128;
			}
		}
		
		int heightInterval = height / BLOCK_HEIGHT;
		int widthInterval = width / BLOCK_WIDTH;
		int heightAdjustment = 0;
		int widthAdjustment = 0;
		
		for (int heightCounter = 0; heightCounter < heightInterval; heightCounter++) {
			heightAdjustment = heightCounter * BLOCK_HEIGHT;
			widthAdjustment = 0;
			for (int widthCounter = 0; widthCounter < widthInterval; widthCounter++) {
				
				widthAdjustment = widthCounter * BLOCK_WIDTH;
				int tempCounter = -1;
				double tempValue = 0;
				
				for (int index = 0; index < BLOCK_HEIGHT; index++) {
					tempCounter = -1;
					for (int index2 = 0; index2 < BLOCK_WIDTH; index2++) {
						if (index2 > 0) {
							tempArray[index+heightAdjustment][index2-1+widthAdjustment] = tempValue;
						}
						tempValue = 0;
						for (int index3 = 0; index3 < BLOCK_WIDTH; index3++) {
							tempValue = tempValue + (tArray[index][index3] * adjustedArray[index3+heightAdjustment][index2+widthAdjustment]);
						}
						tempCounter++;
						if (tempCounter == (BLOCK_WIDTH-1)) {
							tempArray[index+heightAdjustment][index2+widthAdjustment] = tempValue;
						}
					}
				}
				
				for (int index = 0; index < BLOCK_HEIGHT; index++) {
					tempCounter = -1;
					for (int index2 = 0; index2 < BLOCK_WIDTH; index2++) {
						if (index2 > 0) {
							outputArray[index+heightAdjustment][index2-1+widthAdjustment] = (int) Math.round(tempValue);
						}
						tempValue = 0;
						for (int index3 = 0; index3 < BLOCK_WIDTH; index3++) {
							tempValue = tempValue + (tempArray[index+heightAdjustment][index3+widthAdjustment] * transposedTArray[index3][index2]);
						}
						tempCounter++;
						if (tempCounter == (BLOCK_WIDTH-1)) {
							outputArray[index+heightAdjustment][index2+widthAdjustment] = (int) Math.round(tempValue);
						}
					}
				}
			}
		}
			return outputArray;
	}
	
	
	private int[][] quantizeScale(int[][] inputArray, int type, int height, int width, double scaling) {
		
		int[][] outputArray = new int[height][width];
		int heightInterval = height / BLOCK_HEIGHT;
		int widthInterval = width / BLOCK_WIDTH;
		int heightAdjustment = 0;
		int widthAdjustment = 0;
		
		for (int heightCounter = 0; heightCounter < heightInterval; heightCounter++) {
			heightAdjustment = heightCounter * BLOCK_HEIGHT;
			widthAdjustment = 0;
			for (int widthCounter = 0; widthCounter < widthInterval; widthCounter++) {
				
				widthAdjustment = widthCounter * BLOCK_WIDTH;
				
				if (type == 1) {
					for (int index = 0; index < BLOCK_HEIGHT; index++) {
						for (int index2 = 0; index2 < BLOCK_WIDTH; index2++) {
							outputArray[index+heightAdjustment][index2+widthAdjustment] = (int) Math.round(inputArray[index+heightAdjustment][index2+widthAdjustment] / (scaling * luminanceTable[index][index2]));
						}
					}
				} else {
					for (int index = 0; index < BLOCK_HEIGHT; index++) {
						for (int index2 = 0; index2 < BLOCK_WIDTH; index2++) {
							outputArray[index+heightAdjustment][index2+widthAdjustment] = (int) Math.round(inputArray[index+heightAdjustment][index2+widthAdjustment] / (scaling * chrominanceTable[index][index2]));	
						}
					}
				}
			}
		}
		return outputArray;
	}
	
	private int[][] calculateIDCT(int[][] inputArray, int type, int height, int width) {
		
		int reconstructedArray[][] = new int[height][width];
		double[][] tempArray = new double[height][width];
		int[][] outputArray = new int[height][width];
		int heightInterval = height / BLOCK_HEIGHT;
		int widthInterval = width / BLOCK_WIDTH;
		int heightAdjustment = 0;
		int widthAdjustment = 0;
		
		for (int heightCounter = 0; heightCounter < heightInterval; heightCounter++) {
			heightAdjustment = heightCounter * BLOCK_HEIGHT;
			widthAdjustment = 0;
			for (int widthCounter = 0; widthCounter < widthInterval; widthCounter++) {
				
				widthAdjustment = widthCounter * BLOCK_WIDTH;
				if (type == 1) {
					for (int index = 0; index < BLOCK_HEIGHT; index++) {
						for (int index2 = 0; index2 < BLOCK_WIDTH; index2++) {
							reconstructedArray[index+heightAdjustment][index2+widthAdjustment] = (int) Math.round(inputArray[index+heightAdjustment][index2+widthAdjustment] * luminanceTable[index][index2]);
						}
					}
				} else {
					for (int index = 0; index < BLOCK_HEIGHT; index++) {
						for (int index2 = 0; index2 < BLOCK_WIDTH; index2++) {
							reconstructedArray[index+heightAdjustment][index2+widthAdjustment] = (int) Math.round(inputArray[index+heightAdjustment][index2+widthAdjustment] * chrominanceTable[index][index2]);
						}
					}
				
				}
			}
		}
		
		
		for (int heightCounter = 0; heightCounter < heightInterval; heightCounter++) {
			heightAdjustment = heightCounter * BLOCK_HEIGHT;
			widthAdjustment = 0;
			for (int widthCounter = 0; widthCounter < widthInterval; widthCounter++) {
				
				widthAdjustment = widthCounter * BLOCK_WIDTH;
				int tempCounter = -1;
				double tempValue = 0;
	
				for (int index = 0; index < BLOCK_HEIGHT; index++) {
					tempCounter = -1;
					for (int index2 = 0; index2 < BLOCK_WIDTH; index2++) {
						if (index2 > 0) {
							tempArray[index+heightAdjustment][index2-1+widthAdjustment] = tempValue;
						}
						tempValue = 0;
						for (int index3 = 0; index3 < BLOCK_WIDTH; index3++) {
							tempValue = tempValue + (transposedTArray[index][index3] * reconstructedArray[index3+heightAdjustment][index2+widthAdjustment]);
						}
						tempCounter++;
						if (tempCounter == (BLOCK_WIDTH-1)) {
							tempArray[index+heightAdjustment][index2+widthAdjustment] = tempValue;
						}
					}
				}
	
				for (int index = 0; index < BLOCK_HEIGHT; index++) {
					tempCounter = -1;
					for (int index2 = 0; index2 < BLOCK_WIDTH; index2++) {
						if (index2 > 0) {
							outputArray[index+heightAdjustment][index2-1+widthAdjustment] = (int) Math.round(tempValue);
						}
						tempValue = 0;
						for (int index3 = 0; index3 < BLOCK_WIDTH; index3++) {
							tempValue = tempValue + (tempArray[index+heightAdjustment][index3+widthAdjustment] * tArray[index3][index2]);
						}
						tempCounter++;
						if (tempCounter == (BLOCK_WIDTH-1)) {
							outputArray[index+heightAdjustment][index2+widthAdjustment] = (int) Math.round(tempValue);
						}
					}
				}
			}
		}
		
		for (int index = 0; index < BLOCK_HEIGHT; index++) {
			for (int index2 = 0; index2 < BLOCK_WIDTH; index2++) {
				outputArray[index][index2] = (outputArray[index][index2]) + 128;
			}
		}
		return outputArray;
	}
	
	private void setupTArrays() {
		
		for (int index = 0; index < BLOCK_HEIGHT; index++) {
			for (int index2 = 0; index2 < BLOCK_WIDTH; index2++) {
				if (index == 0) {
					tArray[index][index2] = 1/(2*Math.sqrt(2));
				} else {
					tArray[index][index2] = Math.sqrt(2/MATH_BLOCK) * Math.cos((((2*index2)+1)*(index*Math.PI))/(2*BLOCK_WIDTH));
				}
			}
		}
		
		for (int index = 0; index < BLOCK_HEIGHT; index++) {
			for (int index2 = 0; index2 < BLOCK_WIDTH; index2++) {
				transposedTArray[index2][index] = tArray[index][index2]; 
			}
		}
	}
	
	private int[] initialSetup(int type, int h, int w) {
	
    	// Calculates Y,U, and V values
    	int YValues[] = new int[w*h];
    	int UValues[] = new int[w*h];
    	int VValues[] = new int[w*h];
    	int inputValues[] = new int[w*h];
    	PixelGrabber grabber = new PixelGrabber(m_imgInput.getSource(), 0, 0, w, h, inputValues, 0, w);
        try{
          if(grabber.grabPixels() != true){
            try{
        	  throw new AWTException("Grabber returned false: " + grabber.status());
        	}catch (Exception e) {};
          }
        } catch (InterruptedException e) {};
        
        int red,green, blue; 
        for (int index = 0; index < h * w; ++index){
        	red = ((inputValues[index] & 0x00ff0000) >> 16);
        	green =((inputValues[index] & 0x0000ff00) >> 8);
        	blue = ((inputValues[index] & 0x000000ff) );
        	YValues[index] = (int)((0.299 * (float)red) + (0.587 * (float)green) + (0.114 * (float)blue));
        	UValues[index] = (int)((-0.299 * (float)red) + (-0.587 * (float)green) + (0.886 * (float)blue));
        	VValues[index] = (int)((0.701 * (float)red) + (-0.587 * (float)green) + (-0.114 * (float)blue));
        }
        
        if (type == 1) {
        	return YValues;
        } else if (type == 2) {
        	return UValues;
        } else {
        	return VValues;
        }
	}
	
	private int[][] chromaSubsample(int[][] inputArray, int height, int width) {
		int[][] outputArray = new int [height/2][width/2];
		
		int heightCounter = -1;
		int widthCounter = 0;
		for (int index = 0; index < height; index+=2) {
			heightCounter++;
			widthCounter = 0;
			for (int index2 = 0; index2 < width; index2+=2) {
				outputArray[heightCounter][widthCounter] = (int) Math.round((inputArray[index][index2] + inputArray[index+1][index2] + inputArray[index][index2+1] + inputArray[index+1][index2+1]) / 4);
				widthCounter++;
			}
		}
		return outputArray;
	}
	
	private int[][] expandInterpolateArray(int[][] inputArray, int height, int width) {
		
		int[][] outputArray = new int [height][width];
		
		for(int index = 0; index < height; index+=2) {
			for (int index2 = 0; index2 < width; index2+=2) {
				outputArray[index][index2] = inputArray[index/2][index2/2];
			}
		}
		
		for (int index = 0; index < height; index++) {
			for (int index2 = 0; index2 < width; index2++) {
				int temp1 = width;
				int temp2 = height;
				
				if (index == temp2 - 1) {
					outputArray[index][index2] = outputArray[index-1][index2];
				} else if (index2 == temp1 - 1 ) {
					outputArray[index][index2] = outputArray[index][index2-1];
				} else {
					if (index2 % 2 != 0 && index % 2 == 0) {
						outputArray[index][index2] = (int) Math.round((outputArray[index][index2-1] + outputArray[index][index2+1]) / 2);
					} else if (index2 % 2 == 0 && index % 2 != 0) {
						outputArray[index][index2] = (int) Math.round((outputArray[index-1][index2] + outputArray[index+1][index2]) / 2);
					} else if (index % 2 != 0 && index2 % 2 != 0) {
						outputArray[index][index2] = (int) Math.round((outputArray[index-1][index2-1] + outputArray[index+1][index2+1]) / 2);
					}
				}
			}
		}
		return outputArray;
	}
	
	private void mainFunction() {
		
		int originalWidth = m_imgInput.getWidth(null);
        int originalHeight = m_imgInput.getHeight(null);
         	
     	while (originalWidth % 8 != 0) {
     		originalWidth--;
     	}
     	while (originalHeight % 8 != 0) {
     		originalHeight--;
     	}
     	
     	int w = originalWidth;
     	int h = originalHeight;
     	
     	int YValues[] = new int[w*h];
     	YValues = initialSetup(1, h, w);
    	int UValues[] = new int[w*h];
    	UValues = initialSetup(2, h, w);
    	int VValues[] = new int[w*h];
    	VValues = initialSetup(3, h, w);
     	
     	//Puts Y,U, and V values into 2D arrays
        int counter = 0;
        int[][] newYValues = new int[h][w];
        int[][] newUValues = new int[h][w];
        int[][] newVValues = new int[h][w];
        
        for (int index = 0; index < h; index++) {
        	for (int index2 = 0; index2 < w; index2++) {
        		newYValues[index][index2] = YValues[counter];
        		newUValues[index][index2] = UValues[counter];
        		newVValues[index][index2] = VValues[counter];
        		counter++;
        	}
        }
        
        int[][] scaledYArray = new int[h][w];
        int[][] scaledUArray = new int[h/2][w/2];
        int[][] scaledVArray = new int[h/2][w/2];
        setupTArrays();
        
        scaledYArray = calculateDCT(newYValues, h, w);
        scaledYArray = quantizeScale(scaledYArray, 1, h, w, scalingFactor);
        scaledYArray = calculateIDCT(scaledYArray, 1, h, w);
        
        scaledUArray = chromaSubsample(newUValues, h, w);
        scaledUArray = calculateDCT(scaledUArray, h/2, w/2);
        scaledUArray = quantizeScale(scaledUArray, 2, h/2, w/2, scalingFactor);
        scaledUArray = calculateIDCT(scaledUArray, 2, h/2, w/2);
        
        scaledVArray = chromaSubsample(newVValues, h, w);
        scaledVArray = calculateDCT(scaledVArray, h/2, w/2);
        scaledVArray = quantizeScale(scaledVArray, 2, h/2, w/2, scalingFactor);
        scaledVArray = calculateIDCT(scaledVArray, 2, h/2, w/2);
        
        scaledUArray = expandInterpolateArray(scaledUArray, h, w);
        scaledVArray = expandInterpolateArray(scaledVArray, h, w);
        
        
      //Converts the 2D array into 1D array
        int finalY[] = new int[w*h];
        int finalU[] = new int[w*h];
        int finalV[] = new int[w*h];
        int finalCounter = 0;
        for (int index = 0; index < h; index++) {
        	for (int index2 = 0; index2 < w; index2++) {
        		finalY[finalCounter] = scaledYArray[index][index2];
        		finalU[finalCounter] = scaledUArray[index][index2];
        		finalV[finalCounter] = scaledVArray[index][index2];
        		finalCounter++;
        	}
        }
        
        int RGBY[] = new int[w*h];
        int RGBU[] = new int[w*h];
        int RGBV[] = new int[w*h];
        
        for (int index = 0; index < h * w; ++index){
        	RGBY[index] = (int)((1 * finalY[index]) + (0 * finalU[index]) + (1.13983 * finalV[index]));
        	RGBU[index] = (int)((1 * finalY[index]) + (-0.39465 * finalU[index]) + (-0.58060 * finalV[index]));
        	RGBV[index] = (int)((1 * finalY[index]) + (2.03211 * finalU[index]) + (0 * finalV[index]));
        }
        
        int finalRGB[] = new int[w*h*3];
        for (int index = 0; index < w*h; index++) {
        	int interval = 3*index;
        	finalRGB[interval] = RGBY[index];
        	finalRGB[interval+1] = RGBU[index];
        	finalRGB[interval+2] = RGBV[index];
        }
        
        
        if (isGrey) {
        	m_imgOutput = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        	WritableRaster raster = (WritableRaster) m_imgOutput.getData();
        	raster.setPixels(0, 0, w, h, finalY);
        	m_imgOutput.setData(raster);
        	m_panelImgOutput.setBufferedImage(m_imgOutput);
        } else {
        	m_imgOutput = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        	int[] raw = new int[finalRGB.length * 4 / 3];
        	for (int i = 0; i < finalRGB.length / 3; i++) {
        		raw[i] = 0xFF000000 | 
        				((finalRGB[3 * i + 0] & 0xFF) << 16) |
        				((finalRGB[3 * i + 1] & 0xFF) << 8) |
        				((finalRGB[3 * i + 2] & 0xFF));
        	}
        	m_imgOutput.setRGB(0, 0, w, h, raw, 0, w);
        	WritableRaster raster = (WritableRaster) m_imgOutput.getData();
        	raster.setPixels(0, 0, w, h, finalRGB);
        	m_imgOutput.setData(raster);
        	m_panelImgOutput.setBufferedImage(m_imgOutput);
        }
	}
	
}
	
	
	
	
	/*

    
    
    //Takes every other U&V value and puts it into a 2D array
    int newWidth = w/2;
    int newHeight = h/2;
    int[][] subSampleU = new int[newHeight][newWidth];
    int[][] subSampleV = new int[newHeight][newWidth];
    int rowCounter = -1;
    int columnCounter = 0;
    
    //For Testing
    int[][] testArray = { {200,202,189,188,189,175,175,175} , {200,203,198,188,189,182,178,175} , {203,200,200,195,200,187,185,175} , {200,200,200,200,197,187,187,187} , {200,205,200,200,195,188,187,175} , {200,200,200,200,200,190,187,175} , {205,200,199,200,191,187,187,175} , {210,200,200,200,188,185,187,186} , {200,202,189,188,189,175,175,175} , {200,203,198,188,189,182,178,175} , {203,200,200,195,200,187,185,175} , {200,200,200,200,197,187,187,187} , {200,205,200,200,195,188,187,175} , {200,200,200,200,200,190,187,175} , {205,200,199,200,191,187,187,175} , {210,200,200,200,188,185,187,186} };
    int testHeight = 16;
    int testWidth = 8;
    int[][] testSubSampleU = new int[testHeight/2][testWidth/2];
    /////////////////////////////////////////////////////////////////////
    
    
    //Chroma Subsample 4:2:0 of U Values
    for (int index = 0; index < testHeight; index += 2) {
    	rowCounter++;
    	columnCounter = 0;
    	for (int index2 = 0; index2 < testWidth; index2 += 2) {
    		testSubSampleU[rowCounter][columnCounter] = Math.round((testArray[index][index2] + testArray[index][index2+1] + testArray[index+1][index2] + testArray[index+1][index2+1]) / 4);
    		columnCounter++;
    	}
    
    
    //Convert 2D array into 1D array for Y Values
    
    
    //Converts the 2D array into 1D array
    int finalU[] = new int[w*h/4];
    int finalCounter = 0;
    for (int index = 0; index < newWidth; index++) {
    	for (int index2 = 0; index2 < newHeight; index2++) {
    		finalU[finalCounter] = subSampleU[index][index2];
    		finalCounter++;
    	}
    }
    //
    
    */
