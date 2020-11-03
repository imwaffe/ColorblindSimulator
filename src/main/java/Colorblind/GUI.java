package Colorblind;

import ImageTools.ImageScaler;
import ImageTools.ImagesList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class GUI extends JPanel {
    private static final String TITLE = "Colorblind Simulator";

    private final JFrame frame = new JFrame(TITLE);
    private static final JMenuItem menuFilterNormal = new JMenuItem("Normal vision");
    private static final JMenuItem menuFilterProtan = new JMenuItem("Protanopy");
    private static final JMenuItem menuFilterDeutan = new JMenuItem("Deuteranopy");
    private static final JMenuItem menuFilterTritan = new JMenuItem("Tritanopy");
    private static final JMenuItem menuFilterGray = new JMenuItem("Greyscale");
    private final JMenuItem menuFileOpen = new JMenuItem("Open");
    private final JMenuItem menuFileSave = new JMenuItem("Save as ...");

    public enum FilterType {
        PROTAN (" [ PROTANOPY ] ", menuFilterProtan),
        DEUTAN (" [ DEUTERANOPY ] ", menuFilterDeutan),
        TRITAN (" [ TRITANOPY ] ", menuFilterTritan),
        GRAYSCALE (" [ GRAYSCALE ] ", menuFilterGray),
        NORMAL ("", menuFilterNormal);

        protected final String title;
        protected final JMenuItem menu;
        FilterType(String title, JMenuItem menu) {
            this.title = title;
            this.menu = menu;
        }
    };

    public GUI(){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);

        JMenuBar bar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenu menuFilter = new JMenu("Simulate");

        menuFile.add(menuFileOpen);
        //menuFile.add(menuFileSave);

        menuFilter.add(menuFilterNormal);
        menuFilter.add(menuFilterProtan);
        menuFilter.add(menuFilterDeutan);
        menuFilter.add(menuFilterTritan);
        menuFilter.add(menuFilterGray);

        bar.add(menuFile);
        bar.add(menuFilter);

        frame.getContentPane().add(BorderLayout.NORTH, bar);
        frame.setVisible(true);
    }

    public void addFilterListener(FilterType filter, Runnable action){
        filter.menu.addActionListener(e -> {
            frame.setTitle(TITLE + filter.title);
            action.run();
        });
    }

    public void addOpenFileListener(Runnable action){
        menuFileOpen.addActionListener(e -> {
            action.run();
        });
    }

    public static File[] fileLoader(JPanel parent){
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filesFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(filesFilter);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.showOpenDialog(parent);
        return fileChooser.getSelectedFiles();
    }

    public void drawPicture(BufferedImage img){
        img = ImageScaler.resizeImage(img,new Dimension(1000,1000));
        try{
            frame.getContentPane().remove(1);
        }catch (ArrayIndexOutOfBoundsException event){};
        frame.setSize(img.getWidth(),img.getHeight());
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        frame.revalidate();
        frame.repaint();
    }
    public void drawPicture(BufferedImage img, Simulator simulator){
        drawPicture(simulator.filter(img));
    }

    public JFrame getFrame(){
        return this.frame;
    }
}
