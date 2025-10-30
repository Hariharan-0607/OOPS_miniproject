import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame implements ActionListener {
    JTextField idField, exam1Field, exam2Field, exam3Field, assignmentField, semField;
    JLabel internalLabel, finalLabel, gradeLabel, resultInternal, resultFinal, resultGrade;
    JButton submitButton;

    public Client() {
        setTitle("🎓 Internal Marks Calculator");
        setSize(600, 420);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        int row = 0;

        // Left-aligned inputs
        addLeftAlignedLabel(panel, c, "Student ID:", row, labelFont);
        idField = addTextField(panel, c, row++, fieldFont);

        addLeftAlignedLabel(panel, c, "Exam 1 (out of 50):", row, labelFont);
        exam1Field = addTextField(panel, c, row++, fieldFont);

        addLeftAlignedLabel(panel, c, "Exam 2 (out of 25):", row, labelFont);
        exam2Field = addTextField(panel, c, row++, fieldFont);

        addLeftAlignedLabel(panel, c, "Exam 3 (out of 50):", row, labelFont);
        exam3Field = addTextField(panel, c, row++, fieldFont);

        addLeftAlignedLabel(panel, c, "Assignment (out of 50):", row, labelFont);
        assignmentField = addTextField(panel, c, row++, fieldFont);

        addLeftAlignedLabel(panel, c, "Semester Exam (out of 100):", row, labelFont);
        semField = addTextField(panel, c, row++, fieldFont);

        // Button in center
        submitButton = new JButton("Calculate & Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 15));
        submitButton.setBackground(new Color(70, 130, 180));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(this);
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, c);
        row++;

        // --- Right-aligned Output Section ---
        internalLabel = new JLabel("Internal (out of 40):", SwingConstants.RIGHT);
        finalLabel = new JLabel("Final (out of 100):", SwingConstants.RIGHT);
        gradeLabel = new JLabel("Grade:", SwingConstants.RIGHT);

        resultInternal = new JLabel("—", SwingConstants.RIGHT);
        resultFinal = new JLabel("—", SwingConstants.RIGHT);
        resultGrade = new JLabel("—", SwingConstants.RIGHT);

        internalLabel.setFont(labelFont);
        finalLabel.setFont(labelFont);
        gradeLabel.setFont(labelFont);

        resultInternal.setFont(fieldFont);
        resultFinal.setFont(fieldFont);
        resultGrade.setFont(fieldFont);

        // Output rows (aligned to right side)
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.EAST;

        c.gridx = 0; c.gridy = row;
        panel.add(internalLabel, c);
        c.gridx = 1;
        panel.add(resultInternal, c);
        row++;

        c.gridx = 0; c.gridy = row;
        panel.add(finalLabel, c);
        c.gridx = 1;
        panel.add(resultFinal, c);
        row++;

        c.gridx = 0; c.gridy = row;
        panel.add(gradeLabel, c);
        c.gridx = 1;
        panel.add(resultGrade, c);

        add(panel);
        setVisible(true);
    }

    private void addLeftAlignedLabel(JPanel panel, GridBagConstraints c, String text, int row, Font font) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(font);
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0.4;
        panel.add(label, c);
    }

    private JTextField addTextField(JPanel panel, GridBagConstraints c, int row, Font font) {
        JTextField field = new JTextField(10);
        field.setFont(font);
        c.gridx = 1;
        c.gridy = row;
        c.weightx = 0.6;
        panel.add(field, c);
        return field;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String data = String.join(",",
                idField.getText(),
                exam1Field.getText(),
                exam2Field.getText(),
                exam3Field.getText(),
                assignmentField.getText(),
                semField.getText()
            );

            Socket socket = new Socket("localhost", 5000);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            out.writeUTF(data);
            String result = in.readUTF();
            socket.close();

            // Split and update right-aligned result labels
            String[] parts = result.split("\n");
            resultInternal.setText(parts[0].replace("Internal:", "").trim());
            resultFinal.setText(parts[1].replace("Final:", "").trim());
            resultGrade.setText(parts[2].replace("Grade:", "").trim());

        } catch (Exception ex) {
            resultInternal.setText("⚠️ Error");
            resultFinal.setText("");
            resultGrade.setText("");
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
