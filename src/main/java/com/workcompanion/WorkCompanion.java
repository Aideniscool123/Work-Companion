package com.workcompanion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.prefs.Preferences;

public final class WorkCompanion {
    private static final Color[] COLORS = {new Color(246,176,119),new Color(168,205,255),new Color(177,224,165),new Color(224,184,246),new Color(255,204,112),new Color(239,151,167),new Color(139,213,204),new Color(196,190,175),new Color(255,226,143),new Color(163,174,242)};
    private static final String[] NAMES = {"Mochi","Puddle","Sprout","Mallow","Toast","Peach","Boba","Pebble","Sunny","Pip"};
    private final Preferences prefs = Preferences.userNodeForPackage(WorkCompanion.class);
    private JFrame petFrame; private Pet pet; private JWindow hover; private int character; private int size; private Timer animation;
    private Point dragOffset; private boolean dragging;

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new WorkCompanion().start()); }
    private void start() {
        character=prefs.getInt("character",0); size=prefs.getInt("size",112);
        pet=new Pet(); petFrame=new JFrame(); petFrame.setUndecorated(true); petFrame.setAlwaysOnTop(true); petFrame.setType(Window.Type.UTILITY); petFrame.setBackground(new Color(0,0,0,0)); petFrame.setContentPane(pet); petFrame.setSize(size,size+10);
        Dimension screen=Toolkit.getDefaultToolkit().getScreenSize(); int x=prefs.getInt("x",screen.width-size-35), y=prefs.getInt("y",screen.height-size-100); petFrame.setLocation(Math.max(0,Math.min(x,screen.width-size)),Math.max(0,Math.min(y,screen.height-size)));
        pet.addMouseListener(new MouseAdapter(){ public void mouseEntered(MouseEvent e){showHover();} public void mouseExited(MouseEvent e){hideHoverLater();} public void mousePressed(MouseEvent e){dragOffset=e.getPoint();dragging=true;} public void mouseReleased(MouseEvent e){dragging=false;savePosition();} });
        pet.addMouseMotionListener(new MouseMotionAdapter(){ public void mouseDragged(MouseEvent e){Point p=petFrame.getLocation();petFrame.setLocation(p.x+e.getX()-dragOffset.x,p.y+e.getY()-dragOffset.y); if(hover!=null&&hover.isVisible()) positionHover();} });
        petFrame.setVisible(true); animation=new Timer(45,e->{pet.tick++;pet.repaint();});animation.start();
    }
    private void savePosition(){prefs.putInt("x",petFrame.getX());prefs.putInt("y",petFrame.getY());}
    private void showHover(){if(hover==null){hover=new JWindow(petFrame);hover.setAlwaysOnTop(true);JPanel panel=new JPanel(new FlowLayout(FlowLayout.CENTER,4,5));panel.setBackground(new Color(255,255,255,245));panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(228,222,238),1,true),new EmptyBorder(0,4,0,4)));JButton b=new JButton("⚙  Settings");b.setFont(new Font("Segoe UI",Font.PLAIN,12));b.setForeground(new Color(62,57,75));b.setFocusPainted(false);b.setBorderPainted(false);b.setContentAreaFilled(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.addActionListener(e->settings());panel.add(b);hover.setContentPane(panel);hover.pack();hover.addMouseListener(new MouseAdapter(){public void mouseEntered(MouseEvent e){if(hideTimer!=null)hideTimer.stop();}public void mouseExited(MouseEvent e){hideHoverLater();}});}positionHover();hover.setVisible(true);}
    private Timer hideTimer; private void hideHoverLater(){if(hideTimer!=null)hideTimer.stop();hideTimer=new Timer(350,e->{if(hover!=null&&!hover.getBounds().contains(MouseInfo.getPointerInfo().getLocation()))hover.setVisible(false);});hideTimer.setRepeats(false);hideTimer.start();}
    private void positionHover(){if(hover==null)return;int w=hover.getWidth(),h=hover.getHeight();int x=petFrame.getX()+(petFrame.getWidth()-w)/2;int y=petFrame.getY()+petFrame.getHeight()-7;Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();if(y+h>screen.height)y=petFrame.getY()-h+8;hover.setLocation(Math.max(0,Math.min(x,screen.width-w)),Math.max(0,y));}
    private void settings(){if(hover!=null)hover.setVisible(false);String[] options=new String[NAMES.length];for(int i=0;i<NAMES.length;i++)options[i]=NAMES[i];JComboBox<String> choices=new JComboBox<>(options);choices.setSelectedIndex(character);JSlider scale=new JSlider(76,168,size);scale.setMajorTickSpacing(23);scale.setPaintTicks(true);scale.setPaintLabels(true);scale.setSnapToTicks(true);JPanel content=new JPanel();content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));content.setBorder(new EmptyBorder(14,16,8,16));content.add(new JLabel("Character"));content.add(Box.createVerticalStrut(5));content.add(choices);content.add(Box.createVerticalStrut(14));content.add(new JLabel("Size"));content.add(scale);int result=JOptionPane.showConfirmDialog(petFrame,content,"Your little companion",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);if(result==JOptionPane.OK_OPTION){character=choices.getSelectedIndex();size=scale.getValue();prefs.putInt("character",character);prefs.putInt("size",size);petFrame.setSize(size,size+10);pet.revalidate();pet.repaint();savePosition();}}

    private final class Pet extends JPanel {long tick=0;Pet(){setOpaque(false);}void tick(){tick++;}protected void paintComponent(Graphics raw){super.paintComponent(raw);Graphics2D g=(Graphics2D)raw.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);double s=Math.min(getWidth(),getHeight()-8)/112.0;g.translate((getWidth()-112*s)/2,5+(Math.sin(tick*.075)*2));g.scale(s,s);float blink=(tick%115>108)?0.13f:1f;Color c=COLORS[character];g.setColor(new Color(45,42,60,28));g.fill(new Ellipse2D.Double(19,91,74,12));
            // Small ears and a soft, rounded body
            g.setColor(c.darker());switch(character%5){case 0->{g.fillRoundRect(25,16,18,31,10,10);g.fillRoundRect(69,16,18,31,10,10);}case 1->{g.fill(new Ellipse2D.Double(21,20,23,23));g.fill(new Ellipse2D.Double(68,20,23,23));}case 2->{Path2D leaf=new Path2D.Double();leaf.moveTo(32,37);leaf.curveTo(12,6,39,6,46,31);leaf.closePath();g.fill(leaf);leaf.reset();leaf.moveTo(80,37);leaf.curveTo(102,4,74,7,66,31);leaf.closePath();g.fill(leaf);}case 3->{g.fillRoundRect(25,14,18,34,8,8);g.fillRoundRect(69,14,18,34,8,8);}default->{g.fill(new Ellipse2D.Double(25,19,18,18));g.fill(new Ellipse2D.Double(69,19,18,18));}}
            g.setColor(c);g.fill(new Ellipse2D.Double(16,27,80,69));g.setColor(new Color(255,255,255,50));g.fill(new Ellipse2D.Double(26,33,50,29));g.setColor(new Color(80,63,68));g.fill(new Ellipse2D.Double(37,57,5,5*blink));g.fill(new Ellipse2D.Double(69,57,5,5*blink));g.setStroke(new BasicStroke(1.8f));g.draw(new Arc2D.Double(51,62,11,8,205,130,Arc2D.OPEN));g.setColor(new Color(255,150,160,125));g.fill(new Ellipse2D.Double(28,64,10,5));g.fill(new Ellipse2D.Double(75,64,10,5));
            // Character-specific accessory marks
            g.setColor(new Color(255,255,255,205));switch(character){case 0->g.fill(new Ellipse2D.Double(50,35,12,8));case 1->g.fill(new Ellipse2D.Double(49,77,16,9));case 2->{g.setColor(new Color(70,155,89));g.fill(new Ellipse2D.Double(48,31,16,8));}case 3->{g.fill(new Ellipse2D.Double(48,37,16,8));}case 4->{g.fillRoundRect(41,34,32,9,5,5);}case 5->{g.fill(new Ellipse2D.Double(50,33,12,8));g.fill(new Ellipse2D.Double(32,25,7,7));}case 6->{g.setColor(new Color(85,66,59));g.fillRoundRect(48,32,16,6,4,4);}case 7->{g.fill(new Ellipse2D.Double(39,40,8,8));g.fill(new Ellipse2D.Double(67,47,6,6));}case 8->{g.setColor(new Color(250,191,70));g.fill(new Arc2D.Double(36,30,40,24,0,180,Arc2D.PIE));}case 9->{g.fill(new Ellipse2D.Double(50,34,12,8));g.setColor(new Color(255,190,80));g.fill(new Polygon(new int[]{55,61,55},new int[]{41,49,49},3));}}
            g.dispose();}}
}
