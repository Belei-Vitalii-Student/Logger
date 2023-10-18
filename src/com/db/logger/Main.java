package com.db.logger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class Main {

    public static void main(String[] args) throws Exception {

//        Завдання: створити за допомогою програмного коду або програми журнал обліку змін, які відбуваються у базі даних.
//
//        Обрана мова програмування: Java
//        Обрана база даних: PostgreSQL
//
//        Для перевірки змін в базі даних за допомогою програми використовую додаток pgAdmin 4
//
//        Дії:
//          1. Встановити підключення з базою даних, а саме з базою даних logger та створити необхідні таблиці (users, logs)
//          2. Відкрити вікно програми та натиснути кнопу "New User" для додавання нового користувача
//          3. Ввести дані в поля (name, age, favorite color)
//          4. Переглянути таблицю Users та таблицю Logs (дані повинні оновитись)
//          5. Змінити будь-яке поле недавно створеного користувача просто двічі натиснувши на нього та вписавши нове
//          6. Переглянути таблицю Users та таблицю Logs (дані повинні оновитись)
//          7. Для видалення користувача вибираємо рядок в якому користувач записаний та натискаємо на кнопку Del (на клавіатурі)
//          8. Переглянути таблицю Users та таблицю Logs (дані повинні оновитись)

        PostgresqlDB db = new PostgresqlDB();


        JFrame frame = new JFrame("Logger");
        JTabbedPane tabbedPane = new JTabbedPane();

        JButton addButton = new JButton("New user");

        JTable logsTable = createLogsTable(db.getLogs());
        JTable usersTable = createUsersTable(db.getAllUsers(), db, logsTable, addButton);

        JScrollPane logsPane = new JScrollPane(logsTable);
        JScrollPane usersPane = new JScrollPane(usersTable);
        JPanel panel = new JPanel();
        panel.add(addButton);

        tabbedPane.addTab("Users", usersPane);
        tabbedPane.addTab("Logs", logsPane);

        frame.add(tabbedPane);
        frame.add(panel, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    private static JTable createUsersTable(List<User> users, PostgresqlDB db, JTable logsTable, JButton addButton) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"id", "name", "age", "favorite color"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };

        for (User user : users) {
            model.addRow(new Object[]{user.getId(), user.getName(), user.getAge(), user.getFavoriteColor()});
        }

        JTable table = new JTable(model);


        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showAddDataDialog(db);
                    refreshUsersTable(table, db.getAllUsers());
                    refreshLogsTable(logsTable, db.getLogs());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        table.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0 && e.getKeyCode() == KeyEvent.VK_DELETE) {
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    Integer id = (Integer) model.getValueAt(selectedRow, 0); // Get the ID from the selected row
                    model.removeRow(selectedRow);
                    try {
                        db.deleteUser(id);
                        List<Log> newLogs = db.getLogs();
                        refreshLogsTable(logsTable, newLogs);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    if (column == 1) {
                        try {
                            int id = (int) model.getValueAt(row, 0);
                            String newValue = (String) model.getValueAt(row, column);
                            db.changeUserName(id, newValue);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (column == 2) {
                        try {
                            int id = (int) model.getValueAt(row, 0);
                            int newValue = 0;
                            try {
                                newValue = Integer.parseInt((String) model.getValueAt(row, column));
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null, "Please enter a valid age (an integer).");
                                return;
                            }
                            db.changeUserAge(id, newValue);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (column == 3) {
                        try {
                            int id = (int) model.getValueAt(row, 0);
                            String newValue = (String) model.getValueAt(row, column);
                            db.changeUserFavoriteColor(id, newValue);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    try {
                        List<Log> newLogs = db.getLogs();
                        refreshLogsTable(logsTable, newLogs);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        return table;
    }

    private static JTable createLogsTable(List<Log> logs) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"id", "action", "date", "description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Log log : logs) {
            model.addRow(new Object[]{log.getId(), log.getAction(), log.getDate(), log.getDescription()});
        }
        JTable table = new JTable(model);
        return table;
    }

    private static void refreshLogsTable(JTable table, List<Log> updatedLogs) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        // Clear the existing data in the model
        model.setRowCount(0);

        // Add the updated data to the model
        for (Log log : updatedLogs) {
            model.addRow(new Object[]{log.getId(), log.getAction(), log.getDate(), log.getDescription()});
        }

        // Refresh the JTable to display the updated data
        table.setModel(model);
    }

    private static void refreshUsersTable(JTable table, List<User> updatedUsers) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        // Clear the existing data in the model
        model.setRowCount(0);

        // Add the updated data to the model
        for (User user : updatedUsers) {
            model.addRow(new Object[]{user.getId(), user.getName(), user.getAge(), user.getFavoriteColor()});
        }

        // Refresh the JTable to display the updated data
        table.setModel(model);
    }

    private static void showAddDataDialog(PostgresqlDB db) throws SQLException {
        JTextField nameField = new JTextField(20);
        JTextField ageField = new JTextField(5);
        JTextField colorField = new JTextField(10);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Favorite Color:"));
        panel.add(colorField);

        int result = JOptionPane.showConfirmDialog(null, panel, "New User", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            Integer age = Integer.parseInt(ageField.getText());
            String color = colorField.getText();

            db.addUser(name, age, color);
        }
    }
}