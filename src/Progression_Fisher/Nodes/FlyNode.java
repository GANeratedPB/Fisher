package Progression_Fisher.Nodes;

import Progression_Fisher.Fighter_main;
import Progression_Fisher.Node;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;

public class FlyNode extends Node {
    Area flyArea = new Area(3109, 3434, 3105, 3431, 0);
    Area fishingStoreArea = new Area(3016, 3229, 3012, 3223, 0);
    Area chickenArea = new Area(3020, 3296, 3014, 3282, 0);

    public static String status = "get fly rod";

    public static final String WOODEN_SHIELD = "Wooden shield";
    public static final String BRONZE_SWORD = "Bronze sword";
    public static final String ROD = "Fly fishing rod";
    public static final String FEATHERS = "Feather";

    public FlyNode(Fighter_main c) {
        super(c);
    }


    @Override
    public boolean validate() {
        // check that fish lvl is equal to or greater than 20 and one has 1000 feathers
        return (c.getSkills().getRealLevel(Skill.FISHING) >= 20 && c.getInventory().count(FEATHERS) > 200);
    }

    @Override
    public int execute() {
        switch (status) {
            case "get fly rod":
                c.setStatus("Getting fly fishing rod");
                if (c.getInventory().contains(ROD)) {
                    status = "fly fish";
                    break;
                } else if (c.getInventory().count("Coins") < 5) {
                    c.getBank().openClosest();
                    MethodProvider.sleep(500, 800);
                    if (c.getBank().isOpen() && c.getBank().contains(ROD)) {
                        c.getBank().withdraw(ROD);
                        MethodProvider.sleep(500, 800);
                        c.getBank().close();
                    } else if (c.getBank().isOpen()) {
                        c.getBank().withdraw("Coins", 5);
                        c.sleep(500, 800);
                        c.getBank().close();

                    }
                    break;
                } else {
                    c.walkTo(fishingStoreArea, 3000, 5000);

                    NPC storeKeeper = c.getNpcs().closest("Gerrant");
                    if (storeKeeper != null) {
                        storeKeeper.interact("Trade");
                        MethodProvider.sleep(1000, 1500);
                        WidgetChild rodWidg = c.getWidgets().getWidgetChild(300, 16, 3);
                        if (rodWidg != null && rodWidg.isVisible()) {
                            rodWidg.interact("Buy 1");
                        }
                    }
                }

                break;

            case "fly fish":
                c.setStatus("Fly Fishing");
                if (flyArea.contains(c.getLocalPlayer()) && c.getInventory().isFull()) {
                    status = "bank fish";
                    break;
                } else if (!flyArea.contains(c.getLocalPlayer())) {
                    c.walkTo(flyArea, 3000, 5000);

                } else {
                    NPC flySpot = c.getNpcs().closest("Rod Fishing spot");
                    if (flySpot != null && flySpot.distance(flyArea.getNearestTile(c.getLocalPlayer())) < 5) {
                        flySpot.interact("Lure");
                        c.sleep(1000, 1500);
                        c.sleepUntil(() -> !c.getLocalPlayer().isAnimating(), 30000);
                    }

                }

                break;
            case "bank fish":
                c.setStatus("Dropping fish");
                if (!flyArea.contains(c.getLocalPlayer()) && !c.getInventory().isFull()) {
                    status = "fly fish";
                    break;
                }
                c.setStatus("Inventory is full, banking fish");
                c.getBank().openClosest();
                c.sleep(1500, 3000);
                if (c.getBank().isOpen()) {
                    c.getBank().depositAllExcept(FEATHERS, ROD);
                    c.sleep(500, 1500);
                    c.getBank().close();
                }
                break;
        }
        return 500;
    }
}
