package Colorblind.Controller;

import Colorblind.GUI.GUI;
import Colorblind.Simulator;
import ImageTools.ImagesList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

class ChangePicture extends Thread {
    private static final int DURATION_MIN = 500;
    private static final int DURATION_MAX = 1000;
    private static final int RUNS_MIN = 10;
    private static final int RUNS_MAX = 20;

    private BufferedImage original;
    private BufferedImage filtered;
    private final GUI gui;
    private boolean state = true;
    private int runs = 0;
    private Runnable actionAfter = () -> {};

    public ChangePicture(BufferedImage original, BufferedImage filtered, GUI gui) {
        this.gui = gui;
        this.original = original;
        this.filtered = filtered;
    }

    @Override
    public void run() {
        runs = RUNS_MIN + (int) (Math.random()*(RUNS_MAX-RUNS_MIN));
        int remaining = runs;

        while(remaining>=0) {
            if (state)
                gui.drawPicture(original);
            else
                gui.drawPicture(filtered);
            state = !state;
            try {
                sleep((long) (DURATION_MIN + (Math.random()*(DURATION_MAX-DURATION_MIN))));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            remaining--;
        }
        actionAfter.run();
    }

    public int getRuns(){
        return runs;
    }

    public void executeAfter(Runnable actionAfter){
        this.actionAfter = actionAfter;
    }

    public void changePictures(BufferedImage original, BufferedImage filtered){
        this.original = original;
        this.filtered = filtered;
    }
}

public class RunTest {
    public static void runTest(GUI gui, Simulator simulator, ImagesList imgList) {
        imgList.reset();
        AtomicReference<BufferedImage> inputImg = new AtomicReference<>(imgList.nextBuffImg());
        ChangePicture cp = new ChangePicture(inputImg.get(), simulator.filter(inputImg.get()), gui);

        ArrayList<Integer> actual = new ArrayList<>(), given = new ArrayList<>();
        ArrayList<String> imageName = new ArrayList<>();

        cp.executeAfter(() -> {
            String input;
            do {
                input = JOptionPane.showInputDialog(gui.getFrame(),
                        "How many times do the image's colors changed?",
                        "Test",
                        JOptionPane.QUESTION_MESSAGE);
            }while(input==null || !input.matches("^[0-9]+$") || input.isEmpty());

            JOptionPane.showMessageDialog(gui.getFrame(),
                    "Your guess: "+input+"\nActual: "+cp.getRuns(),
                    "Answer",
                    JOptionPane.INFORMATION_MESSAGE);

            actual.add(cp.getRuns());
            given.add(Integer.parseInt(input));
            imageName.add(imgList.getCurrentFileName());

            if(imgList.hasNext()){
                inputImg.set(imgList.nextBuffImg());
                cp.changePictures(inputImg.get(), simulator.filter(inputImg.get()));
                cp.run();
            }else{
                gui.drawPicture(inputImg.get(), simulator);
                JOptionPane.showMessageDialog(gui.getFrame(),
                        "Test completed!",
                        "Test end",
                        JOptionPane.INFORMATION_MESSAGE);
                CSVWriter(actual,given,imageName);
            }
        });

        cp.start();
    }

    private static void CSVWriter(ArrayList<Integer> actual, ArrayList<Integer> given, ArrayList<String> imageName){
        if(actual.size() != given.size() || actual.size() != imageName.size())
            throw new IllegalArgumentException("All vectors must have the same length");
        File dir = new File("./test_results/");
        dir.mkdirs();
        try(CSVPrinter printer = new CSVPrinter(new FileWriter(new File(dir,System.currentTimeMillis()+".csv")), CSVFormat.EXCEL)){
            printer.printRecord("image","actual","given","error");
            for(int i=0; i<actual.size(); i++){
                float error = (float)Math.abs(actual.get(i)-given.get(i))/(float)actual.get(i);
                printer.printRecord(imageName.get(i),actual.get(i),given.get(i),error);
            }
            actual.clear();
            given.clear();
            imageName.clear();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
