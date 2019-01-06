package Progression_Fisher.Nodes;

import Progression_Fisher.Fisher_Main;
import Progression_Fisher.Node;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;

public class FlyNode extends Node {
    Area flyArea = new Area(3109, 3434, 3099, 3424, 0);
    Area fishingStoreArea = new Area(3016, 3229, 3012, 3223, 0);
    Area chickenArea = new Area(3020, 3296, 3014, 3282, 0);

    public static String status = "get fly rod";

    public static final String WOODEN_SHIELD = "Wooden shield";
    public static final String BRONZE_SWORD = "Bronze sword";
    public static final String ROD = "Fly fishing rod";
    public static final String FEATHERS = "Feather";

    public FlyNode(Fisher_Main c) {
        super(c);
    }


    @Override
    public boolean validate() {
        // check that fish lvl is equal to or greater than 20 and one has 1000 feathers
        int numFeathers = c.getInventory().count(FEATHERS);
        return (c.getSkills().getRealLevel(Skill.FISHING) >= 20 && !c.isCollectFeathers());
    }

    @Override
    public int execute() {
        if (c.getInventory().count(FEATHERS) < 50) {
            c.setCollectFeathers(true);
        }
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
                    c.walkToRand(fishingStoreArea, 3000, 5000);

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
                if (c.getInventory().isFull()) {
                    status = "bank fish";
                    break;
                } else if (!flyArea.contains(c.getLocalPlayer())) {
                    c.walkToCenter(flyArea, 3000, 5000);

                } else {
                    NPC flySpot = c.getNpcs().closest("Rod Fishing spot");
                    if (flySpot != null && flySpot.interact("Lure")) {
                        c.sleepUntil(() -> {
                            c.sleep(1000, 1500);
                            return (!c.getLocalPlayer().isMoving() || !c.getLocalPlayer().isAnimating());
                        }, 20000);
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
