package view;

import controller.CanvasController;
import controller.ToolController;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class MainFrame extends JFrame {

    private Toolbar toolbar;
    private CanvasView canvasView;

    public MainFrame(CanvasController canvasController, ToolController toolController) {
        super("UML Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        toolbar = new Toolbar(toolController);
        canvasView = new CanvasView(canvasController);

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(canvasView, BorderLayout.CENTER);

        setJMenuBar(createMenuBar(canvasController));
    }

    private JMenuBar createMenuBar(CanvasController canvasController) {
        JMenuBar menuBar = new JMenuBar();

        // 建立 Edit 選單
        JMenu editMenu = new JMenu("Edit");
        JMenuItem labelItem = new JMenuItem("Label");
        labelItem.addActionListener(e -> canvasController.customizeLabelStyle(this));
        editMenu.add(labelItem);

        JMenuItem groupItem = new JMenuItem("Group");
        groupItem.addActionListener((ActionEvent e) -> canvasController.groupSelectedObjects());

        JMenuItem ungroupItem = new JMenuItem("UnGroup");
        ungroupItem.addActionListener((ActionEvent e) -> canvasController.ungroupSelectedObject());

        editMenu.add(groupItem);
        editMenu.add(ungroupItem);

        menuBar.add(editMenu);
        return menuBar;
    }
}
