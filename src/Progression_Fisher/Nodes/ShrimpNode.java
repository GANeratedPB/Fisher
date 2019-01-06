package Progression_Fisher.Nodes;

import Progression_Fisher.Fisher_Main;
import Progression_Fisher.Node;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.interactive.NPC;

public class ShrimpNode extends Node {
    Area shrimpArea = new Area(3247, 3161, 3241, 3143, 0);
    Area shopArea = new Area(3209, 3249, 3214, 3245, 0);

    public static final String DROP_ITEMS[] = {"Raw shrimps", "Raw anchovies"};

    public static final String SMALL_NET = "Small fishing net";
    public static String status = "inventory dropoff";

    public ShrimpNode(Fisher_Main c) {
        super(c);
    }

    @Override
    public boolean validate() {
        // fishing level is under 20
        return (c.getSkills().getRealLevel(Skill.FISHING) < 20);
    }

    @Override
    public int execute() {
        // powerfish shrimp until level 20
        switch (status) {
            case "inventory dropoff":
                // Empties inventory if not in south Lumbridge shrimp fishing area
                c.setStatus("Dropping off inventory");
                if (!shrimpArea.contains(c.getLocalPlayer()) && c.getInventory().emptySlotCount() == 27 && c.getInventory().contains(SMALL_NET)) {
                    // player not in shrimp area, has 27 empty spots, and has small net
                    status = "walk to shrimp";
                    break;

                } else if (shrimpArea.contains(c.getLocalPlayer())) {
                    // player IS in shrimp area
                    status = "fish shrimp";
                    break;
                } else {
                    c.getBank().openClosest();
                    c.sleep(500, 1500);
                    if (c.getBank().isOpen()) {
                        c.getBank().depositAllItems();
                        MethodProvider.sleep(700, 1000);
                        c.getBank().withdraw(SMALL_NET);
                        MethodProvider.sleep(600, 850);
                        c.getBank().close();
                    }
                    break;
                }

            case "walk to shrimp":
                c.setStatus("Walking to Shrimp");
                if (shrimpArea.contains(c.getLocalPlayer())) {
                    if (c.getInventory().isFull()) {
                        status = "drop shrimp";
                        break;
                    } else {
                        status = "fish shrimp";
                        break;
                    }

                } else {
                    c.walkToRand(shrimpArea, 3000, 5000);
                }
                break;

            case "fish shrimp":
                c.setStatus("Fishing Shrimp");
                if (shrimpArea.contains(c.getLocalPlayer()) && c.getInventory().isFull()) {
                    status = "drop shrimp";
                    break;
                } else {
                    NPC shrimpSpot = c.getNpcs().closest("Fishing spot");
                    if (shrimpSpot != null) {
                        shrimpSpot.interact("Net");
                        MethodProvider.sleep(1000, 1500);
                        MethodProvider.sleepUntil(() -> !c.getLocalPlayer().isAnimating(), 30000);
                    }
                    break;
                }

            case "drop shrimp":
                c.setStatus("Dropping Shrimp");
                if (shrimpArea.contains(c.getLocalPlayer()) && !c.getInventory().isFull()) {
                    status = "fish shrimp";
                    break;
                } else {
                    c.getInventory().dropAll("Raw shrimps", "Raw anchovies");
                    c.sleep(500, 800);
                    break;
                }

        }
        return 500;
    }
}
