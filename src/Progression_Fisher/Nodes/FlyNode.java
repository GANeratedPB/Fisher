package Progression_Fisher.Nodes;

import Progression_Fisher.Fighter_main;
import Progression_Fisher.Node;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;

public class FlyNode extends Node {
    Area flyArea = new Area(3109, 3434, 3103, 3424, 0);
    Area fishingStoreArea = new Area(3016, 3229, 3012, 3223, 0);
    Area chickenArea = new Area(3020, 3296, 3014, 3282, 0);

    public static String status = "Check weapons";

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
        return (c.getSkills().getRealLevel(Skill.FISHING) >= 20 && c.getInventory().count(FEATHERS)>1000);
    }

    @Override
    public int execute() {
        switch (status) {
            case "Check weapons":
                c.log("Checking for weapon and shield");
                if (!c.getEquipment().isSlotEmpty(EquipmentSlot.WEAPON.getSlot()) && !c.getEquipment().isSlotEmpty(EquipmentSlot.SHIELD.getSlot())) {
                    MethodProvider.log("Found equipment");
                    if (c.getInventory().count(FEATHERS) > 1000) {
                        status = "Get fly rod";
                        break;
                    } else {
                        status = "Kill chickens";
                        break;
                    }

                } else if (c.getEquipment().isSlotEmpty(EquipmentSlot.SHIELD.getSlot())) {
                    c.log("Getting Shield");
                    if (c.getInventory().contains(WOODEN_SHIELD)) {
                        c.getInventory().interact(WOODEN_SHIELD, "Wield");
                    } else {
                        c.getBank().openClosest();
                        c.sleep(500, 1000);
                        c.getBank().withdraw(WOODEN_SHIELD);
                        c.sleep(200, 400);
                        c.getBank().close();
                    }
                    break;
                } else {
                    if (c.getInventory().contains(BRONZE_SWORD)) {
                        c.getInventory().interact(BRONZE_SWORD, "Wield");
                    } else {
                        c.getBank().openClosest();
                        MethodProvider.sleep(500, 1000);
                        c.getBank().withdraw(BRONZE_SWORD);
                        MethodProvider.sleep(200, 400);
                        c.getBank().close();
                    }
                    break;
                }
            case "Get fly rod":
                c.log("Getting Fly Fishing Rod");
                c.getBank().openClosest();
                MethodProvider.sleep(1000, 2500);
                if (c.getInventory().contains(ROD)) {
                    if (c.getInventory().count(FEATHERS) > 1000) {
                        status = "Fly fish";
                        break;
                    } else {
                        status = "Check weapons";
                        break;
                    }

                } else if (c.getBank().contains(ROD)) {
                    c.getBank().openClosest();
                    MethodProvider.sleep(500, 800);
                    c.getBank().withdraw(ROD);
                    MethodProvider.sleep(500, 800);
                    c.getBank().close();
                } else {
                    if (c.getInventory().count("Coins") < 5) {
                        c.getBank().openClosest();
                        MethodProvider.sleep(500, 800);
                        c.getBank().withdraw("Coins", 5);
                        MethodProvider.sleep(500, 800);
                        c.getBank().close();
                    } else if (!fishingStoreArea.contains(c.getLocalPlayer())) {
                        c.walkTo(fishingStoreArea, 3000, 5000);
                    } else {
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

                }
                break;

            case "Kill chickens":
                c.log("Killing chickens for feathers");
                if (c.getInventory().count(FEATHERS) > 1000) {
                    System.out.println("here");
                    status = "Get fly rod";
                } else if (!chickenArea.contains(c.getLocalPlayer())) {
                    c.getWalking().walk(chickenArea.getRandomTile());
                    MethodProvider.sleep(500, 800);
                } else {
                    if (c.getLocalPlayer().isInCombat()) {
                        // do nothing
                    } else if (chickenArea.contains(c.getGroundItems().closest(FEATHERS))) {
                        if (c.getGroundItems().closest(FEATHERS) != null) {
                            c.getGroundItems().closest(FEATHERS).interact("Take");
                            MethodProvider.sleep(1000, 1500);
                            MethodProvider.sleepUntil(() -> !c.getLocalPlayer().isAnimating(), 6000);
                        }
                    } else {
                        NPC chicken = c.getNpcs().closest("Chicken");
                        if (chicken != null) {
                            chicken.interact("Attack");
                            MethodProvider.sleep(1000, 1500);

                        }


                    }
                }

            case "Fly fish":
                c.log("Fly Fishing");
                if (flyArea.contains(c.getLocalPlayer()) && c.getInventory().isFull()) {
                    status = "Bank fish";
                    break;
                } else if (!flyArea.contains(c.getLocalPlayer())) {
                    c.walkTo(flyArea, 3000, 5000);

                } else {
                    NPC flySpot = c.getNpcs().closest("Rod Fishing spot");
                    if (flySpot != null) {
                        flySpot.interact("Lure");
                        c.sleep(1000, 1500);
                        c.sleepUntil(() -> !c.getLocalPlayer().isAnimating(), 30000);
                    }

                }

                break;
            case "Bank fish":
                c.log("Dropping fish");
                if (!flyArea.contains(c.getLocalPlayer()) && !c.getInventory().isFull()) {
                    status = "Fly fish";
                    break;
                }
                c.log("Inventory is full, banking fish");
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
