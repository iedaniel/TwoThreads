package com.twothreads;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Timer;

public class App {
    private JPanel mainPanel;
    private JLabel curMinLabel;
    private JTextArea inputArea;

    List<Integer> numbersList = new ArrayList<>();

    String[] numbers0To9 = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
    String[] numbers11To19 = {"eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "sevenTeen", "eighteen", "nineteen"};
    String[] tens = {"ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};
    String hundred = "hundred";
    String thousand = "thousand";

    public App() {
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    inputArea.setText("");
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    InputStream is = new ByteArrayInputStream(inputArea.getText().getBytes(StandardCharsets.UTF_8));
                    Scanner sc = new Scanner(is);
                    while (sc.hasNextLine()) {
                        String[] input = sc.next().split(" ");

                        int res = 0;
                        for (int i = 0; i < input.length; ++i) {
                            for (int j = 0; j < numbers0To9.length; ++j) {
                                if (input[i].equals(numbers0To9[j])) {
                                    if (i + 1 < input.length && input[i + 1].equals(hundred)) {
                                        res += j * 100;
                                        ++i;
                                    } else if (i + 1 < input.length && input[i + 1].equals(thousand)) {
                                        res += j * 1000;
                                        ++i;
                                    } else {
                                        res += j;
                                    }
                                }
                            }
                            for (int j = 0; j < numbers11To19.length; ++j) {
                                if (input[i].equals(numbers11To19[j])) {
                                    res += 11 + j;
                                }
                            }
                            for (int j = 0; j < tens.length; ++j) {
                                if (input[i].equals(tens[j])) {
                                    res += ++j * 10;
                                }
                            }
                        }
                        synchronized (numbersList) {
                            numbersList.add(res);
                        }
                    }
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (numbersList) {
                            if (numbersList.isEmpty()) {
                                curMinLabel.setText("На данный момент список пуст...");
                            } else {
                                Integer min = numbersList.stream().min(Integer::compareTo).get();
                                numbersList.remove(min);
                                String txt = "";
                                for (Integer num: numbersList) {
                                    txt += "\n" + num;
                                }

                                txt += "\n\n Минимальное число: " + min;
                                curMinLabel.setText("<html>" + txt.replaceAll("\n", "<br>") + "</html>");
                            }

                        }
                    }
                }, 0, 5000);
            }
        }).start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
