package com.jishnu;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class WatermarkImage  {

	public static void main(String[] args) throws NullPointerException {

		System.out.println("argumenst passed: " + args.length);
		if (args.length != 3) {
			System.out
					.println("usage example WatermarkImage <Input Directory> <\"Watermark Text\"> <Output Directory>");
			System.exit(0);
		}
		String inDir = args[0];
		String waterMarkText = args[1];
		String outDir = args[2];
		System.out.println("indir :" + inDir + " waterMarktest: "
				+ waterMarkText + " outDir: " + outDir);

		File inDirFiles = new File(inDir);
		File outDirFiles = new File(outDir);
		outDirFiles.mkdirs();
		if (!inDirFiles.isDirectory() || !outDirFiles.isDirectory()) {
			System.out
					.println("Frst and Third argument needs to be a directory");
			System.exit(0);
		}
		File[] inFilesArray = inDirFiles.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				if (name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith("jpeg")) {
					return true;
				}
				return false;
			}
		});
		for (int i = 0; i < inFilesArray.length; i++) {
			System.out.println("in file: " + inFilesArray[i].getName()
					+ " out Dir: " + outDirFiles.getPath()
					+ System.getProperty("file.separator")
					+ inFilesArray[i].getName());
			createWaterMark(
					inFilesArray[i],
					new File(outDirFiles.getPath()
							+ System.getProperty("file.separator")
							+ inFilesArray[i].getName()), waterMarkText);
		}

	}

	public static boolean createWaterMark(File inFile, File OutFile,
			String watermark) {

		ImageIcon icon = new ImageIcon(inFile.getPath());

		// create BufferedImage object of same width and height as of original
		// image
		BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(),
				icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);

		// create graphics object and add original image to it
		Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();

		graphics.drawImage(icon.getImage(), 0, 0, null);

		graphics.setColor(Color.red);
		//graphics.setFont(new Font("Arial", Font.ITALIC, 30));
		Font font = Font.decode("Arial");
		
		
		graphics.setRenderingHint(
			    RenderingHints.KEY_TEXT_ANTIALIASING, 
			    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Map<TextAttribute, Object> atts = new HashMap<TextAttribute, Object>();
		
		atts.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
		atts.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
		atts.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		
		Rectangle2D rect =null;
		FontMetrics fm = null;
		int optimal_width=(int) (icon.getIconWidth()*.50);
		int optimal_height=(int) (icon.getIconHeight()*.50);
		int size=100;
		
		while(true){	
		atts.put(TextAttribute.SIZE, size);
		font = font.deriveFont(atts);
		graphics.setFont(font);
		fm = graphics.getFontMetrics();
		rect = fm.getStringBounds(watermark, graphics);
		if((optimal_width+rect.getWidth()>icon.getIconWidth()) 
				|| (optimal_height+rect.getHeight()> icon.getIconHeight())){
			
			size--;
		}
		else{
			
			
			break;
		}
		
		//}while((icon.getIconWidth()<(rect.getWidth()*2)) || (icon.getIconHeight() < (rect.getHeight()*2)));
		}
		
		
		Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .6f);
		graphics.setComposite(c);
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.fill3DRect(
				(int) (icon.getIconWidth() - rect.getWidth() - fm.getAscent()-10),
				(int) (icon.getIconHeight() - rect.getHeight() - fm.getAscent()),
				(int) rect.getWidth() + 10, (int) rect.getHeight() + 2, true);
		// set font for the watermark text
		graphics.setColor(Color.red);

		// add the watermark text
		graphics.drawString(watermark,
				(int) (icon.getIconWidth() - rect.getWidth() - fm.getAscent()),
				(int) (icon.getIconHeight() - rect.getHeight()));
		graphics.dispose();

		try {
			ImageIO.write(bufferedImage, "jpg", OutFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(OutFile.getPath() + " created successfully!");
		return true;
	}

}