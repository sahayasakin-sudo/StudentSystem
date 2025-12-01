import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class StudentManagementGUI {

    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;

    public StudentManagementGUI() {
        initUI();
    }

    private void initUI() {
        frame = new JFrame("Student Management (GUI)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 450);
        frame.setLocationRelativeTo(null);

        model = new DefaultTableModel(new Object[] { "ID", "Name" }, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(table);

        JPanel buttons = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton removeBtn = new JButton("Remove");
        JButton clearBtn = new JButton("Clear All");

        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(removeBtn);
        buttons.add(clearBtn);

        frame.setLayout(new BorderLayout(8, 8));
        frame.add(createTopPanel(), BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(buttons, BorderLayout.SOUTH);

        // Button actions
        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        removeBtn.addActionListener(e -> onRemove());
        clearBtn.addActionListener(e -> onClearAll());

        // Double-click to edit
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) onEdit();
            }
        });

        // Sample data
        model.addRow(new Object[] { 1, "Alice" });
        model.addRow(new Object[] { 2, "Bob" });

        frame.setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Student Management System", JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        p.add(title, BorderLayout.CENTER);
        return p;
    }

    private void onAdd() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        Object[] message = {
                "ID (integer):", idField,
                "Name:", nameField
        };
        int option = JOptionPane.showConfirmDialog(frame, message, "Add Student", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                if (name.isEmpty()) throw new IllegalArgumentException("Name empty");

                // Check duplicate ID
                for (int i = 0; i < model.getRowCount(); i++) {
                    Integer existing = (Integer) model.getValueAt(i, 0);
                    if (existing != null && existing == id) {
                        JOptionPane.showMessageDialog(frame, "ID already exists.", "Input error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                model.addRow(new Object[] { id, name });
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "ID must be an integer.", "Input error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, "Name cannot be empty.", "Input error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Select a student to edit.", "No selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Object curId = model.getValueAt(row, 0);
        Object curName = model.getValueAt(row, 1);

        JTextField idField = new JTextField(String.valueOf(curId));
        JTextField nameField = new JTextField(String.valueOf(curName));
        Object[] message = {
                "ID (integer):", idField,
                "Name:", nameField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Edit Student", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                if (name.isEmpty()) throw new IllegalArgumentException("Name empty");

                // If ID changed, check duplicate
                if (id != (Integer) curId) {
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if (i == row) continue;
                        Integer existing = (Integer) model.getValueAt(i, 0);
                        if (existing != null && existing == id) {
                            JOptionPane.showMessageDialog(frame, "ID already exists.", "Input error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }

                model.setValueAt(id, row, 0);
                model.setValueAt(name, row, 1);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "ID must be an integer.", "Input error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, "Name cannot be empty.", "Input error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onRemove() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Select a student to remove.", "No selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(frame, "Remove selected student?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(row);
        }
    }

    private void onClearAll() {
        int confirm = JOptionPane.showConfirmDialog(frame, "Remove ALL students?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.setRowCount(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentManagementGUI::new);
    }
}
