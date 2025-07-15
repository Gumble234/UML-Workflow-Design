package view;

import model.GraphicObject;
import model.GraphicObject.ShapeType;
import javax.swing.*;
import java.awt.*;

public class LabelStyleDialog extends JDialog {
    private final JTextField nameField = new JTextField(15);
    private final JComboBox<String> shapeBox = new JComboBox<>(new String[]{"RECT", "OVAL"});
    private final JComboBox<String> colorBox = new JComboBox<>(new String[]{"LIGHT_GRAY", "YELLOW", "PINK"});
    private final JComboBox<Integer> fontSizeBox = new JComboBox<>(new Integer[]{10, 12, 14, 16, 18, 20});

    private boolean confirmed = false;

    public LabelStyleDialog(JFrame parent, GraphicObject obj) {
        super(parent, "Custom Label Style", true);

        nameField.setText(obj.getLabel());
        shapeBox.setSelectedItem(obj.getLabelShape().name());
        colorBox.setSelectedItem("LIGHT_GRAY");
        fontSizeBox.setSelectedItem(obj.getLabelFontSize());

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Label Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Label Shape:"));
        panel.add(shapeBox);
        panel.add(new JLabel("Label Color:"));
        panel.add(colorBox);
        panel.add(new JLabel("Font Size:"));
        panel.add(fontSizeBox);

        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");

        ok.addActionListener(e -> {
            confirmed = true;
            setVisible(false);
        });
        cancel.addActionListener(e -> setVisible(false));

        JPanel btnPanel = new JPanel();
        btnPanel.add(ok);
        btnPanel.add(cancel);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getLabelName() {
        return nameField.getText();
    }

    public ShapeType getSelectedShape() {
        return ShapeType.valueOf((String) shapeBox.getSelectedItem());
    }

    public Color getSelectedColor() {
        return switch ((String) colorBox.getSelectedItem()) {
            case "YELLOW" -> Color.YELLOW;
            case "PINK" -> Color.PINK;
            default -> Color.LIGHT_GRAY;
        };
    }

    public int getSelectedFontSize() {
        return (Integer) fontSizeBox.getSelectedItem();
    }
}
