package view;

import controller.ToolController;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Toolbar extends JPanel {
    private JButton rectBtn;
    private JButton ovalBtn;
    private JButton associationBtn;
    private JButton generalizationBtn;
    private JButton compositionBtn;
    private JButton selectBtn;

    public Toolbar(ToolController toolController) {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        rectBtn = new JButton("Rect");
        ovalBtn = new JButton("Oval");
        associationBtn = new JButton("Association");
        generalizationBtn = new JButton("Generalization");
        compositionBtn = new JButton("Composition");
        selectBtn = new JButton("Select");

        rectBtn.addActionListener(e -> toolController.selectTool(ToolController.ToolType.RECT));
        ovalBtn.addActionListener(e -> toolController.selectTool(ToolController.ToolType.OVAL));
        associationBtn.addActionListener(e -> toolController.selectTool(ToolController.ToolType.ASSOCIATION));
        generalizationBtn.addActionListener(e -> toolController.selectTool(ToolController.ToolType.GENERALIZATION));
        compositionBtn.addActionListener(e -> toolController.selectTool(ToolController.ToolType.COMPOSITION));  // 如果你是用 DEPENDENCY 表示 composition 請保持，否則改成 COMPOSITION
        selectBtn.addActionListener(e -> toolController.selectTool(ToolController.ToolType.SELECT));

        add(rectBtn);
        add(ovalBtn);
        add(associationBtn);
        add(generalizationBtn);
        add(compositionBtn);
        add(selectBtn);
    }
}
