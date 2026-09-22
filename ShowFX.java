import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
 
public class ShowFX extends Application {
    private MyShow flaeche;
    private Canvas canvas;
    private AnimationTimer prozess;
    @Override
    public void start(Stage stage) {
        canvas = new Canvas(400, 400);// Zeichenfläche
        flaeche = new Light1();// Standardmäßig Light1 anzeigen
        ComboBox<String> S1 = new ComboBox<>();// Auswahlfeld
        S1.getItems().addAll("1", "2", "0");
        S1.setValue("1");
        S1.setOnAction(e -> {
            switch (S1.getValue()) {
                case "0": flaeche = new Light0(); break;
                case "2": flaeche = new Light2(); break;
                default: flaeche = new Light1(); break;
            }
        });
        BorderPane root = new BorderPane();// Layout
        root.setCenter(canvas);
        Pane buttonPanel = new Pane();
        buttonPanel.getChildren().add(S1);
        root.setBottom(buttonPanel);
        root.setStyle("-fx-background-color: black;");
        canvas.setStyle("-fx-background-color: black;");// Schwarzer Hintergrund
        Scene scene = new Scene(root, 400, 454);// Scene
        stage.setTitle("Show");
        stage.setScene(scene);
        prozess = new AnimationTimer() {// Animation starten
            @Override
            public void handle(long now) {
                GraphicsContext g = canvas.getGraphicsContext2D();
                //~ g.setFill(Color.BLACK);// Hintergrund löschen
                //~ g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                flaeche.paint(g, canvas);// aktuelle Animation zeichnen
            }
        };
        prozess.start();
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
 
/*
 * Entspricht der ursprünglichen abstrakten
 * AWT-Klasse MyShow.
 */
abstract class MyShow {
    private int cor = 0;
    private int cog = 0;
    private int cob = 0;
    public int getCor() {return cor;}
    public int getCog() {return cog;}
    public int getCob() {return cob;}
    public void setCor(int xCor) {cor = xCor;}
    public void setCog(int xCog) {cog = xCog;}
    public void setCob(int xCob) {cob = xCob;}
    public int varCor(int xCor) {return cor += xCor;}
    public int varCog(int xCog) {return cog += xCog;}
    public int varCob(int xCob) {return cob += xCob;}
    abstract public void paint(GraphicsContext g, Canvas canvas);
}
 
/*
 * Entspricht Light0 aus der AWT-Version.
 */
class Light0 extends MyShow {
    private int c = 0;
    private int y1 = 0;
    @Override
    public void paint(GraphicsContext g, Canvas canvas) {
        int x = (int) canvas.getWidth();
        int y = (int) canvas.getHeight();
        double helligkeit = c * 255.0 / 255.0;
        g.setStroke(Color.color(helligkeit, helligkeit, helligkeit));
        g.strokeLine(0, y1, x, y1);
        if (y1 >= 2 * y) {
            y1 = 0;
            if (c == 1) c = 0; else c = 1;
        } else {y1++;}
    }
}
 
/*
 * Entspricht Light1 aus der AWT-Version.
 */
class Light1 extends MyShow {
    private int c;
    private int x1 = 0;
    private int y1 = 0;
    public Light1() { c = 1; x1 = -1; y1 = -1; }
    public Light1(int xX1, int xY1) { c = 1; x1 = xX1; y1 = xY1; }
    @Override
    public void paint(GraphicsContext g, Canvas canvas) {
        if (getCob() >= 255) c = -1;
        if (getCob() <= 0) c = 1;
        varCob(c);
        g.setStroke(Color.rgb(getCor(), getCog(), getCob() ));
        int x = (int) canvas.getWidth();
        int y = (int) canvas.getHeight();
        if (x1 > x || x1 < 0 || y1 > y || y1 < 0) { x1 = x / 2; y1 = y / 2; }
        x1 += ((int) Math.floor(Math.random() * 5)) - 2;
        y1 += ((int) Math.floor(Math.random() * 5)) - 2;
        g.strokeLine( x1, y1, x / 2, y / 2 );
    }
}
 
/*
 * Entspricht Light2 aus der AWT-Version.
 */
class Light2 extends MyShow {
    private int c = 1;
    private int i;
    public Light2() { c = 1; }
    @Override
    public void paint(GraphicsContext g, Canvas canvas) {
        int x = (int) canvas.getWidth();
        int y = (int) canvas.getHeight();
        if (getCob() >= 255) c = -1;
        if (getCob() <= 0) c = 1;
        varCob(c);
        if (++i >= x) i = 0;
        g.setStroke(Color.rgb( getCor(), getCog(), getCob() ));
        g.strokeLine(i, 0, x - i, y);
        g.strokeLine(0, y - i, x, i);
    }
}
/*
Die wichtigsten Entsprechungen sind:
AWT                 JavaFX
Frame               Stage
Component           Canvas bzw. JavaFX-Node
Panel               Pane
Choice              ComboBox
BorderLayout        BorderPane
Graphics            GraphicsContext
Color               javafx.scene.paint.Color
paint(Graphics g)   paint(GraphicsContext g, Canvas canvas)
repaint()           Zeichnen über AnimationTimer
Thread + sleep(5)   AnimationTimer
WindowAdapter       JavaFX Stage-Lifecycle
setVisible(true)    stage.show()
 
Ein wichtiger Unterschied bei der Animation
In der AWT-Version passiert das hier: while (true) { repaint(); Thread.sleep(5);}
Das ist in JavaFX nicht empfehlenswert, weil man GUI-Operationen nicht einfach 
aus einem eigenen Thread heraus durchführen sollte.
Daher übernimmt in dieser Version: AnimationTimer die Aufgabe des bisherigen Threads. 
handle() wird von JavaFX regelmäßig auf dem JavaFX Application Thread aufgerufen. 
 
Außerdem existiert ein kleiner Unterschied bei paint(): 
In AWT konnte Graphics direkt auf den Hintergrund bzw. die Komponente zeichnen. 
Beim JavaFX-Canvas muss der vorherige Inhalt explizit gelöscht werden. Deshalb:
g.setFill(Color.BLACK);
g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); 
*/