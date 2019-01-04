package Progression_Fisher.Nodes;

import Progression_Fisher.Fighter_main;
import Progression_Fisher.Node;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.interactive.NPC;

public class ShrimpNode extends Node {
    Area shrimpArea = new Area(3247, 3161, 3241, 3143, 0);
    Area shopArea = new Area(3209, 3249, 3214, 3245, 0);

    public static final String SMALL_NET = "Small fishing net";
    public static String status = "Tutorial Island Dropoff";

    public ShrimpNode(Fighter_main c) {
        super(c);
    }

    @Override
    public boolean validate() {
        return (c.getSkills().getRealLevel(Skill.FISHING) < 20);
    }

    @Override
    public int execute() {
        switch (status) {
            case "Tutorial Island Dropoff":
                c.log("Dropping off inventory");
                if (c.getInventory().emptySlotCount()>10 && c.getInventory().contains(SMALL_NET)) {
                    status = "Walk to Shrimp";
                    break;
                }
                c.getBank().openClosest();
                MethodProvider.sleep(400, 700);
                if (c.getBank().isOpen()) {
                    c.getBank().depositAllExcept(SMALL_NET);
                    c.sleep(500,700);
                    c.getBank().close();
                }
                break;

            case "Walk to Shrimp":
                c.log("Walking to Shrimp");
                if (shrimpArea.contains(c.getLocalPlayer())) {
                    status = "Fish Shrimp";
                    break;
                }
                c.getWalking().walk(shrimpArea.getRandomTile());
                MethodProvider.sleep(500, 800);
                break;

            case "Fish Shrimp":
                c.log("Fishing Shrimp");
                if (shrimpArea.contains(c.getLocalPlayer()) && c.getInventory().isFull() && c.getInventory().count("Coins") > 5) {
                    status = "Drop Shrimp";
                    break;
                } else if (c.getInventory().isFull()) {
                    status = "Sell Shrimp";
                }
                NPC shrimpSpot = c.getNpcs().closest("Fishing spot");
                if (shrimpSpot != null) {
                    shrimpSpot.interact("Net");
                    c.sleep(1000, 1500);
                    c.sleepUntil(() -> !c.getLocalPlayer().isAnimating(), 30000);
                }
                break;

            case "Drop Shrimp":
                c.log("Dropping Shrimp");
                if (shrimpArea.contains(c.getLocalPlayer()) && !c.getInventory().isFull()) {
                    status = "Fish Shrimp";
                    break;
                }
                c.log("Inventory is full, dropping shrimp");
                c.getInventory().dropAll("Raw shrimps", "Raw anchovies");
                c.sleep(500, 600);
                break;

            case "Sell Shrimp":
                c.log("Selling Shrimp");
                if (c.getInventory().count("Coins") > 25 || !c.getInventory().isFull()) {
                    status = "Walk to Shrimp";
                    break;
                }
                c.getWalking().walk(shopArea.getRandomTile());
                c.sleep(400, 600);
                NPC shopKeeper = c.getNpcs().closest("Shop keeper");
                if (shopKeeper != null) {
                    shopKeeper.interact("Trade");
                    c.sleep(1000, 1500);
                    c.getInventory().interact("Raw shrimps", "Sell 50");
                    c.sleep(500, 1500);
                    c.getInventory().interact("Raw anchovies", "Sell 50");
                    c.sleep(500, 1500);
                }
                break;
        }
        return 500;
    }
}
