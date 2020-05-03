package lesson8;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

import static java.lang.Thread.*;

public class SaveTottoro extends JFrame {

    private JButton [][] map;
    private final ImageIcon TOTTORO = new ImageIcon("src/lesson8/res/tottoro.png");
    private final ImageIcon EMPTY = new ImageIcon("src/lesson8/gui/res/empty.jpg");
    private JButton previousTot;
    private JButton currentTot;
    private JButton home;

    private JMenu createFileMenu() {
        JMenu file = new JMenu("Файл");
        JMenuItem open = new JMenuItem("Новая игра");
        JMenuItem exit = new JMenuItem("Выход");
        exit.addActionListener(actionEvent -> {
            dispose();
        });
        open.addActionListener(actionEvent -> {
            try {
                new SaveTottoro("Спаси Тотторо от Коронавируса!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dispose();
        });
        file.add(open);
        file.addSeparator();
        file.add(exit);
        return file;
    }
    private Boolean isInfected(JButton virus, JButton tottoro) {
        if (virus.equals(tottoro)) {
            return true;
        }
        return false;
    }
    private Boolean isArrested(JButton police, JButton tottoro) {
        if (police.equals(tottoro)) {
            return true;
        }
        return false;
    }
    private Boolean isHome(JButton tottoro, JButton home) {
        if (tottoro.equals(home)) {
            return true;
        }
        return false;
    }
    private JPanel getMap() {
        map = new JButton[5][5];
        JPanel panel = new JPanel(new GridLayout(5,5));

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                map[i][j] = new JButton();
                map[i][j].setIcon(EMPTY);

                // Навешимаем обработчик событий, передвигаем Тотторо
                // TODO: переработать этот блок - неправильная отрисовка,
                //  когда заболел. Проблема в переназначении previousTot

                    JButton tmp = map[i][j];
                    tmp.addActionListener(actionEvent -> {
                        tmp.setIcon(TOTTORO);
                        tmp.setEnabled(true);
                        previousTot.setIcon(EMPTY);
                        previousTot = tmp;
                        currentTot = tmp;
                    });
                panel.add(map[i][j]);
            }
        }
        currentTot = previousTot = map[4][0];
        currentTot.setIcon(TOTTORO);
        home = map[0][4];
        home.setBackground(Color.GREEN);
        home.setText("ДОМИК");
        return panel;
    }
    private void initMap() throws InterruptedException {
        // Обратный отсчет перед началом игры
        ImageIcon START3 = new ImageIcon("src/lesson8/res/3.png");
        ImageIcon START2 = new ImageIcon("src/lesson8/res/2.png");
        ImageIcon START1 = new ImageIcon("src/lesson8/res/1.png");
        map[2][2].setIcon(START3);
        sleep(500);
        map[2][2].setIcon(START2);
        sleep(500);
        map[2][2].setIcon(START1);
        sleep(500);
        map[2][2].setIcon(EMPTY);

        // Инициализация положения вируса
        JButton currentVirus = map[0][0];
        JButton currentPolice = map[0][0];
        int virusPreviousX = 5;
        int policePreviousY = 5;
        while (!isInfected(currentVirus, currentTot) ||
               !isArrested(currentPolice, currentTot) ||
               isHome(currentTot, home)) {
            Random rand = new Random();
            int virusNextY = rand.nextInt(5);
            int policeNextX = rand.nextInt(5);

            // Проверка на два одинаковых virusNextY подряд
            if (virusNextY == virusPreviousX || policeNextX == policePreviousY) {
                continue;
            }
            virusPreviousX = virusNextY;
            policePreviousY = policeNextX;
            JButton previousVirus = currentVirus = map[0][virusNextY];
            JButton previousPolice = currentPolice = map[policeNextX][4];

            // Один проход
            for (int i = 0, j = 4; i < 5; i++, j--) {
                if (i == 4 || j == -1) {
                    Thread.sleep(300);
                    currentVirus.setIcon(EMPTY);
                    currentPolice.setIcon(EMPTY);
                } else {
                    ImageIcon virus = new ImageIcon("src/lesson8/res/c1.png");
                    ImageIcon police = new ImageIcon("src/lesson8/res/police.jpg");
                    previousVirus.setIcon(virus);
                    previousPolice.setIcon(police);
                    Thread.sleep(300);
                    previousVirus.setIcon(EMPTY);
                    previousPolice.setIcon(EMPTY);
                    currentVirus = map[i+1][virusNextY];
                    currentPolice = map[policeNextX][j-1];

                    if (isHome(currentTot, home)) {
                        System.out.println("Victory");
                        for (JButton[] line: map
                        ) {
                            for (JButton button: line
                            ) { button.setEnabled(false);
                                button.setText("!!!WIN!!!");
                            }
                        }
                        ImageIcon totWin = new ImageIcon("src/lesson8/res/Tot-Win.png");
                        home.setDisabledIcon(totWin);
                        return;
                    }

                    if (isArrested(currentPolice, currentTot)) {
                        for (JButton[] line: map
                        ) {
                            for (JButton button: line
                            ) { button.setEnabled(false);
                                button.setText("ARRESTED");
                            }
                        }
                        ImageIcon arrested = new ImageIcon("src/lesson8/res/tottoroArrested.png");
                        currentPolice.setDisabledIcon(arrested);
                        return;
                    }
                    if (isInfected(currentVirus, currentTot)) {
                        for (JButton[] line: map
                        ) {
                            for (JButton button: line
                            ) { button.setEnabled(false);
                                button.setText("!!DIED!!");
                            }
                        }
                        ImageIcon dead = new ImageIcon("src/lesson8/res/Tot-Dead.png");
                        currentVirus.setDisabledIcon(dead);
                        return;
                    }


                    currentVirus.setIcon(virus);
                    previousVirus = currentVirus;
                    currentPolice.setIcon(police);
                    previousPolice = currentPolice;
                }
            }
        }
    }

    private SaveTottoro(String title) throws HeadlessException, InterruptedException {
        super(title);
        JMenuBar bar = new JMenuBar();
        bar.add(createFileMenu());
        setJMenuBar(bar);
        add(getMap());
        setSize(500,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        initMap();
    }

    public static void main(String[] args) throws InterruptedException {
        new SaveTottoro("Тотторо гуляет по Москве");
    }
}
