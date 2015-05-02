package de.quellcodecounter.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.UIManager;

import org.jdesktop.swingx.image.ColorTintFilter;
import org.jdesktop.swingx.image.StackBlurFilter;

import de.quellcodecounter.scanner.ProjectScanner;

public class DistributionBar extends JComponent implements MouseMotionListener {

	public SortedMap<String, Integer> parts = new TreeMap<String, Integer>(); 
	
	private static final long serialVersionUID = -2658718126478584418L;

	public DistributionBar() {
		setMinimumSize(new Dimension(0, 28));
		
		addMouseMotionListener(this);
	}

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(0, 14);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(0, 24);
    }
	
    private int getPartSum() {
    	int all = 0;
    	
    	Iterator<Entry<String, Integer>> it = parts.entrySet().iterator();
	    while (it.hasNext()) all += it.next().getValue();
    	
    	return all;
    }
    
	@Override
	public void paintComponent(Graphics graphics) {
		if (parts.isEmpty()) {
			graphics.setColor(UIManager.getColor("Panel.background"));
			graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
		} else {
			float sum = getPartSum();
			
			Iterator<Entry<String, Integer>> it = parts.entrySet().iterator();
			float last = 0;
		    while (it.hasNext()) {
		    	Entry<String, Integer> entry = it.next();
		    	
		    	if (ProjectScanner.FILETYPE_COLORS.containsKey(entry.getKey()))
		    		graphics.setColor(ProjectScanner.FILETYPE_COLORS.get(entry.getKey()));
		    	else
		    		graphics.setColor(ProjectScanner.FILETYPE_COLORS.get(null));
		    		
		    	float begin = last;
		    	float end = begin + this.getWidth() * (entry.getValue() / sum);
		    	last = (int)end;
				graphics.fillRect((int) begin, 0, (int) end, this.getHeight());
				
				graphics.setColor(Color.RED);
				float x = begin + ((end-begin) - graphics.getFontMetrics().stringWidth(entry.getKey()))/2;
				float y = getHeight() - graphics.getFontMetrics().getHeight()/2;
				gaussianText(graphics, entry.getKey(), (int)x, (int)y, (end-begin));
		    }
		}
		

	}

	private void gaussianText(Graphics gg1, String txt, int x, int y, float w) {
		if (gg1.getFontMetrics().stringWidth(txt) > w) return;
		
		BufferedImage g2 = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics gg2 = g2.getGraphics();
		gg2.setColor(Color.WHITE);
		gg2.drawString(txt, x, y);
		
		new StackBlurFilter(4).filter(g2, g2);
		new ColorTintFilter(Color.WHITE, 1f).filter(g2, g2);
		
		for (int i = 0; i < 10; i++)
			gg1.drawImage(g2, 0, 0, null);
		
		gg1.setColor(Color.BLACK);
		gg1.drawString(txt, x, y);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (parts.isEmpty()) {
			setToolTipText("null");
		} else {
			float sum = getPartSum();
			
			Iterator<Entry<String, Integer>> it = parts.entrySet().iterator();
			float last = 0;
		    while (it.hasNext()) {
		    	Entry<String, Integer> entry = it.next();
		    		
		    	float begin = last;
		    	float end = begin + this.getWidth() * (entry.getValue() / sum);
		    	last = (int)end;
		    	
		    	if (e.getX() >= begin && e.getX() <= end) {
		    		setToolTipText(entry.getKey());
		    		return;
		    	}
		    }
		}
	}
}
