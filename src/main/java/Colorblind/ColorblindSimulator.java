package Colorblind;

import ImageTools.ImagesList;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

enum Simulation {
    normal, deutan, protan, tritan, grayscale
}

public class ColorblindSimulator {

    private static final int    LEFT_KEY = 37;
    private static final int    RIGHT_KEY = 39;

    private BufferedImage inputImg;
    public void setInputImg(BufferedImage inputImg){
        this.inputImg = inputImg;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        GUI gui = new GUI();
        Simulator simulator = new Simulator();
        ImagesList imgList = new ImagesList();

        AtomicReference<BufferedImage> inputImg = new AtomicReference<BufferedImage>();
        AtomicReference<BufferedImage> filteredImg = new AtomicReference<BufferedImage>();

        gui.addOpenFileListener(() -> {
            File[] ff = GUI.fileLoader(gui);
            imgList.addFiles(ff);
            simulator.simulate(Simulation.normal);
            inputImg.set(imgList.nextBuffImg());
            gui.drawPicture(inputImg.get());
        });

        gui.addFilterListener(GUI.FilterType.PROTAN, () -> {
            simulator.simulate(Simulation.protan);
            filteredImg.set(showImage(gui, simulator.filter(inputImg.get())));
        });
        gui.addFilterListener(GUI.FilterType.DEUTAN, () -> {
            simulator.simulate(Simulation.deutan);
            filteredImg.set(showImage(gui, simulator.filter(inputImg.get())));
        });
        gui.addFilterListener(GUI.FilterType.TRITAN, () -> {
            simulator.simulate(Simulation.tritan);
            filteredImg.set(showImage(gui, simulator.filter(inputImg.get())));
        });
        gui.addFilterListener(GUI.FilterType.GRAYSCALE, () -> {
            simulator.simulate(Simulation.grayscale);
            filteredImg.set(showImage(gui, simulator.filter(inputImg.get())));
        });
        gui.addFilterListener(GUI.FilterType.NORMAL, () -> {
            simulator.simulate(Simulation.normal);
            filteredImg.set(showImage(gui, inputImg.get()));
        });

        gui.getFrame().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'x' || e.getKeyChar() == 'X') {
                    gui.drawPicture(inputImg.get());
                }else if(e.getKeyCode()==RIGHT_KEY){
                    if(imgList.hasNext()) {
                        inputImg.set(imgList.nextBuffImg());
                        if(simulator.getSimulationType()==Simulation.normal)
                            filteredImg.set(inputImg.get());
                        else
                            filteredImg.set(simulator.filter(inputImg.get()));
                        gui.drawPicture(filteredImg.get());
                    }
                }else if(e.getKeyCode()==LEFT_KEY){
                    if(imgList.hasPrevious()) {
                        inputImg.set(imgList.prevBuffImg());
                        if(simulator.getSimulationType()==Simulation.normal)
                            filteredImg.set(inputImg.get());
                        else
                            filteredImg.set(simulator.filter(inputImg.get()));
                        gui.drawPicture(filteredImg.get());
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyChar()=='x' || e.getKeyChar()=='X') {
                    if(simulator.getSimulationType()==Simulation.normal)
                        gui.drawPicture(inputImg.get());
                    else
                        gui.drawPicture(filteredImg.get());
                }
            }
        });

    }

    private static BufferedImage showImage(GUI gui, BufferedImage img){
        gui.drawPicture(img);
        return img;
    }
}
