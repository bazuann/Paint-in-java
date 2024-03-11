import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class Tester extends JFrame {

    private JButton openButton;
    private DrawingPanel dp;
    private JPanel jpBottom;
    private JButton saveButton;
    private JButton eraseButton;
    private ArrayList<Point> points;
    private ArrayList<ArrayList<Point>> listOfLists;
    private ArrayList<ArrayList<Point>> eraserList;
    private int toolMode; // 0 for drawing, 1 for erasing

    Tester() {
        jpBottom = new JPanel();
        saveButton = new JButton("Save");
        saveButton.addActionListener(new SaveButtonListener());
        jpBottom.add(saveButton);

        openButton = new JButton("Open File");
        openButton.addActionListener(new OpenButtonListener());
        jpBottom.add(openButton);

        eraseButton = new JButton("Eraser/Pencil");
        eraseButton.addActionListener(new EraseButtonListener());
        jpBottom.add(eraseButton);

        listOfLists = new ArrayList<>();
        eraserList = new ArrayList<>();
        toolMode = 0; // Default to drawing mode

        dp = new DrawingPanel();
        dp.addMouseListener(new MyMouseListener());
        dp.addMouseMotionListener(new MyMouseMotionListener());
        this.setLayout(new BorderLayout());
        this.add(dp, BorderLayout.CENTER);
        this.add(jpBottom, BorderLayout.SOUTH);
        this.setSize(600, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        dp.setBackground(Color.white);
    }

    public static void main(String[] args) {
        Tester t = new Tester();
    }

    class DrawingPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (ArrayList<Point> list : listOfLists) {
                for (int i = 0; i < list.size() - 1; i++) {
                    g.setColor(Color.black);
                    g.drawLine(list.get(i).x, list.get(i).y, list.get(i + 1).x, list.get(i + 1).y);
                }
            }
            for (ArrayList<Point> list : eraserList) {
                for (Point point : list) {
                    g.setColor(getBackground());
                    g.fillRect(point.x, point.y, 10, 10);
                }
            }
        }
    }

    class EraseButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (toolMode == 0) {
                toolMode = 1;
                System.out.println("Eraser");
            } else {
                toolMode = 0;
                System.out.println("Pencil");
            }
        }
    }

    class OpenButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    FileInputStream fis = new FileInputStream(chooser.getSelectedFile());
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    listOfLists = (ArrayList<ArrayList<Point>>) ois.readObject();
                    eraserList = (ArrayList<ArrayList<Point>>) ois.readObject();
                    ois.close();
                    fis.close();
                    dp.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error reading file", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile());
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(listOfLists);
                    oos.writeObject(eraserList);
                    oos.flush();
                    oos.close();
                    fos.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error writing file", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    class MyMouseMotionListener implements MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (toolMode == 0) {
                listOfLists.get(listOfLists.size() - 1).add(new Point(e.getX(), e.getY()));
            } else {
                eraserList.get(eraserList.size() - 1).add(new Point(e.getX(), e.getY()));
            }
            dp.repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

    class MyMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (toolMode == 0) {
                listOfLists.add(new ArrayList<>());
            } else {
                eraserList.add(new ArrayList<>());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
}
