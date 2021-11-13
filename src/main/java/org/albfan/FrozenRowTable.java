package org.albfan;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

public class FrozenRowTable {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int columnCount = 20;
                Vector<String> columnNames = new Vector<>();
                for (int i = 0; i < columnCount; i++) {
                    columnNames.add("col"+i);
                }

                Vector<Vector<String>> data = new Vector<>();
                for (int i = 0; i < 100; i++) {
                    Vector<String> row = new Vector<>();
                    for (int j = 0; j < columnNames.size(); j++) {
                        row.add(""+i+j);
                    }
                    data.add(row);
                }

                JTable table = new JTable(data, columnNames);
                FrozenRowTablePane frame = new FrozenRowTablePane(table, 3, true, false, false);
                frame.setSize(300, 150);
                frame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
                frame.setVisible(true);
            }
        });
    }
}
