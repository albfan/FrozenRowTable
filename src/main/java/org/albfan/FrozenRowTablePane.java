package org.albfan;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

/**
 * @version 1.0 03/05/99
 */
public class FrozenRowTablePane extends JFrame {
    JTable fixedTable, newTable;
    JScrollPane scroll, fixedScroll;

    boolean autoresize;
    boolean useDummyBar;

    public FrozenRowTablePane(final JTable table, final int fixedRows, final boolean top, final boolean autoresize, final boolean useDummyBar) {
        super();
        this.autoresize = autoresize;
        this.useDummyBar = useDummyBar;

        AbstractTableModel model = new AbstractTableModel() {
            public int getColumnCount() {
                return table.getColumnCount();
            }

            public int getRowCount() {
                return table.getRowCount() - fixedRows;
            }

            public String getColumnName(int col) {
                return table.getColumnName(col);
            }

            public Object getValueAt(int row, int col) {
                return table.getValueAt(row, col);
            }

            public void setValueAt(Object obj, int row, int col) {
                table.setValueAt(obj, getRowPos(row), col);
            }

            public int getRowPos(int row) {
                return top ? row + fixedRows: row;
            }

            public boolean CellEditable(int row, int col) {
                return true;
            }
        };

        AbstractTableModel fixedModel = new AbstractTableModel() {
            public int getColumnCount() {
                return table.getColumnCount();
            }

            public int getRowCount() {
                return fixedRows;
            }

            public Object getValueAt(int row, int col) {
                return table.getValueAt(getRowPos(row), col);
            }

            public int getRowPos(int row) {
                return top ? row: row + table.getRowCount() - fixedRows;
            }
        };

        DefaultTableColumnModel cm = new DefaultTableColumnModel();
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn tc = new TableColumn();
            tc.setHeaderValue(table.getColumnName(i));
            cm.addColumn(tc);
        }

        newTable = new JTable(model);
        newTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        fixedTable = new JTable(fixedModel, cm);
        fixedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        if (!this.autoresize) {
            newTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            fixedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        createScrolls(top);

        drawEmptyFixedVerticalBar();

        syncHorizontalBars(scroll, fixedScroll);

        scroll.setPreferredSize(new Dimension(400, 100));
        //TODO: Calculate correctly dimension
        int fixedHeaderHeight = fixedTable.getTableHeader().getPreferredSize().height;
        int fixedHeight = fixedTable.getRowHeight() * fixedRows + fixedTable.getIntercellSpacing().height * (fixedRows +1)+ (top ? fixedHeaderHeight: 0);

        fixedScroll.setPreferredSize(new Dimension(400, fixedHeight));
        getContentPane().add(scroll, BorderLayout.CENTER);
        String position = top ? BorderLayout.NORTH: BorderLayout.SOUTH;
        getContentPane().add(fixedScroll, position);
    }

    private void drawEmptyFixedVerticalBar() {
        //TODO: It should be draw only when vertical scroll on scrollable table is shown
        fixedScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JScrollBar bar = fixedScroll.getVerticalScrollBar();
        JScrollBar dummyBar = new JScrollBar() {
            public void paint(Graphics g) {
            }
        };
        dummyBar.setPreferredSize(bar.getPreferredSize());
        //TODO: Until correct height is calculated, is better to have a vertical bar to see all
        //TODO: arrows are repainted when mouse is on top
        if (useDummyBar) {
            fixedScroll.setVerticalScrollBar(dummyBar);
        }
    }

    private void createScrolls(boolean top) {
        //Hide the columnHeader of fixed
        if (top) {
            fixedScroll = new JScrollPane(fixedTable);
            scroll = new JScrollPane(newTable) {
                public void setColumnHeaderView(Component view) {
                } // work around
            };;
            //scroll.setColumnHeader(null);
        } else {
            fixedScroll = new JScrollPane(fixedTable) {
                public void setColumnHeaderView(Component view) {
                } // work around
            };
            scroll = new JScrollPane(newTable);
            //fixedScroll.setColumnHeader(null);
        }
        if (top) {
            fixedScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        } else {
            scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        }
    }

    private void syncHorizontalBars(JScrollPane scroll, JScrollPane fixedScroll) {
        final JScrollBar bar1 = scroll.getHorizontalScrollBar();
        final JScrollBar bar2 = fixedScroll.getHorizontalScrollBar();
        bar2.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                bar1.setValue(e.getValue());
            }
        });
        bar1.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                bar2.setValue(e.getValue());
            }
        });
    }
}