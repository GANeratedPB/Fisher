package Progression_Fisher.Nodes;

import Progression_Fisher.Fighter_main;
import Progression_Fisher.Node;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;

public class FlyNode extends Node {
    Area flyArea = new Area(3109, 3434, 3103, 3424, 0);
    Area fishingStoreArea = new Area(3016, 3229, 3012, 3223, 0);
    Area chickenArea = new Area(3020, 3296, 3014, 3282, 0);

    public static String status = "Check Weapons";

    public static final String WOODEN_SHIELD = "Wooden shield";
    public static final String BRONZE_SWORD = "Bronze sword";
    public static final String ROD = "Fly fishing rod";
    public static final String FEATHERS = "Feather";

    public FlyNode(Fighter_main c) {
        super(c);
    }

    @Override
    public boolean validate() {
        return (c.getSkills().getRealLevel(Skill.FISHING) >= 20);
    }

    @Override
    public int execute() {
        switch (status) {
            case "Check Weapons":
                c.log("Checking for weapon and shield");
                if (!c.getEquipment().isSlotEmpty(EquipmentSlot.WEAPON.getSlot()) && !c.getEquipment().isSlotEmpty(EquipmentSlot.SHIELD.getSlot())) {
                    if (c.getInventory().count(FEATHERS) > 1000) {
                        status = "Get Flyrod";
                        break;
                    } else {
                        status = "Kill Chickens";
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
                        c.sleep(500, 1000);
                        c.getBank().withdraw(BRONZE_SWORD);
                        c.sleep(200, 400);
                        c.getBank().close();
                    }
                    break;
                }
            case "Get Fly rod":
                c.log("Getting Fly Fishing Rod");
                c.getBank().openClosest();
                c.sleep(1000, 2500);
                if (c.getInventory().contains(ROD)) {
                    if (c.getInventory().count(FEATHERS) > 1000) {
                        status = "Fly Fish";
                        break;
                    } else {
                        status = "Check Weapons";
                        break;

                    }

                } else if (c.getBank().contains(ROD)) {
                    c.getBank().openClosest();
                    c.sleep(500, 800);
                    c.getBank().withdraw(ROD);
                    c.sleep(500, 800);
                    c.getBank().close();
                } else {
                    if (c.getInventory().count("Coins") < 5) {
                        c.getBank().openClosest();
                        c.sleep(500, 800);
                        c.getBank().withdraw("Coins", 5);
                        c.sleep(500, 800);
                        c.getBank().close();
                    } else if (!fishingStoreArea.contains(c.getLocalPlayer())) {
                        c.getWalking().walk(fishingStoreArea.getRandomTile());
                        c.sleep(1500, 2500);
                    } else {
                        NPC storeKeeper = c.getNpcs().closest("Gerrant");
                        if (storeKeeper != null) {
                            storeKeeper.interact("Trade");
                            c.sleep(1000, 1500);
                            WidgetChild rodWidg = c.getWidgets().getWidgetChild(300, 16, 3);
                            if (rodWidg != null && rodWidg.isVisible()) {
                                rodWidg.interact("Buy 1");
                            }
                        }
                    }

                }
                break;

            case "Kill Chickens":
                if (c.getInventory().count(FEATHERS) > 1000) {
                    status = "Get Fly rod";
                } else if (!chickenArea.contains(c.getLocalPlayer())) {
                    c.getWalking().walk(chickenArea.getRandomTile());
                    c.sleep(500, 800);
                } else {
                    if (c.getLocalPlayer().isInCombat()) {
                        // do nothing
                    } else {
                        NPC chicken = c.getNpcs().closest("Chicken");
                        if (chicken != null) {
                            chicken.interact("Attack");
                            c.sleep(1000, 1500);

                        }


                    }
                }


        }
        return 500;
    }
}
