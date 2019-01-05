package Progression_Fisher;

import Progression_Fisher.Nodes.ChickenNode;
import Progression_Fisher.Nodes.FlyNode;
import Progression_Fisher.Nodes.ShrimpNode;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@ScriptManifest(name = "GanFigher", version = 0.1, description = "nodes", category = Category.FISHING, author = "GANerated")
public class Fighter_main extends AbstractScript {
    // times in milliseconds
    private long START_TIME = 0L;
    private long BREAK_TIME = 0L;
    private long PLAY_TIME = 0L;

    private long elapsedPlayTime = 0L;

    private String state = "Play";
    private String status = "";

    private Node[] nodes;


    @Override
    public void onStart() {

        START_TIME = System.currentTimeMillis();
        PLAY_TIME = updatedTime(15, 50);
        BREAK_TIME = updatedTime(5, 25);
        nodes = new Node[]{
                new ShrimpNode(this),
                new ChickenNode(this),
                new FlyNode(this)
        };
    }

    @Override
    public int onLoop() {


        switch (state) {
            case "Play":
                elapsedPlayTime = Calculations.elapsed(START_TIME);
                // Once play time is up
                if (elapsedPlayTime > PLAY_TIME) {
                    PLAY_TIME = updatedTime(15, 50);
                    START_TIME = System.currentTimeMillis();
                    state = "Logout";
                }
                for (Node node : nodes) {
                    if (node.validate()) {
                        return node.execute();
                    }
                }
                break;

            case "Break":
                long elapsedBreakTime = Calculations.elapsed(START_TIME);
                if (elapsedBreakTime > BREAK_TIME) {
                    BREAK_TIME = updatedTime(5, 25);
                    START_TIME = System.currentTimeMillis();
                    state = "Login";
                }
                break;

            case "Logout":
                disableEvent(RandomEvent.LOGIN);
                getTabs().logout();
                state = "Break";
                break;

            case "Login":
                enableEvent(RandomEvent.LOGIN);
                state = "Play";
                break;

        }

        return 1000;
    }

    @Override
    public void onPaint(Graphics g) {
        super.onPaint(g);
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.PLAIN, 12));

        g.drawString("Time till Break : " + Timer.formatTime(PLAY_TIME - elapsedPlayTime), 10, 35);
        g.drawString("Current Status: " + status, 10, 55);
    }

    private void disableEvent(RandomEvent event) {
        getRandomManager().disableSolver(event);
    }

    private void enableEvent(RandomEvent event) {
        getRandomManager().enableSolver(event);
    }

    private long updatedTime(int min, int max) {
        /*
        min: minimum number of MINUTES
        max: maximum number of MINUTES
         */
        return TimeUnit.MINUTES.toMillis(Calculations.random(min, max));

    }

    public void walkTo(Area area, int minSleep, int maxSleep) {
        if (getWalking().walk(area.getRandomTile())) {
            sleepUntil(() -> {
                sleep(1000);
                return getLocalPlayer().isStandingStill() || !getLocalPlayer().isAnimating();
            }, Calculations.random(minSleep, maxSleep));
        }

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
