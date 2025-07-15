package app;

import controller.CanvasController;
import controller.ToolController;
import javax.swing.SwingUtilities;
import view.MainFrame;

public class Application {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CanvasController canvasController = new CanvasController();
            ToolController toolController = new ToolController(canvasController);

            MainFrame mainFrame = new MainFrame(canvasController, toolController);
            mainFrame.setVisible(true);
        });
    }
}