package Progression_Fisher.Nodes;

import Progression_Fisher.Fisher_Main;
import Progression_Fisher.Node;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;

public class ChickenNode extends Node {
    public static String status = "check inventory";
    public static final String FEATHERS = "Feather";
    public static final String WOODEN_SHIELD = "Wooden shield";
    public static final String BRONZE_SWORD = "Bronze sword";

    Area chickenArea = new Area(3020, 3296, 3014, 3282, 0);

    public ChickenNode(Fisher_Main main) {
        super(main);
    }

    @Override
    public boolean validate() {
        // fishing lvl is greater than or equal to 20
        return (c.getSkills().getRealLevel(Skill.FISHING) >= 20 && c.isCollectFeathers());
    }

    @Override
    public int execute() {
        if (c.getInventory().count(FEATHERS) > 500) {
            c.setCollectFeathers(false);
        }
        switch (status) {
            case "check inventory":
                c.setStatus("Checking to bank all fish");
                if (!c.getInventory().contains("Raw trout", "Raw Salmon")) {
                    status = "check weapons";
                    break;
                } else {
                    c.getBank().openClosest();
                    MethodProvider.sleep(500, 1000);
                    if (c.getBank().isOpen()) {
                        c.getBank().depositAllExcept(FEATHERS);
                        c.getBank().close();
                    }
                    break;
                }
            case "check weapons":
                c.setStatus("Checking for weapon and shield");
                if (!c.getEquipment().isSlotEmpty(EquipmentSlot.WEAPON.getSlot()) && !c.getEquipment().isSlotEmpty(EquipmentSlot.SHIELD.getSlot())) {
                    c.setStatus("Found equipment");
                    status = "kill chickens";
                    break;
                } else if (c.getEquipment().isSlotEmpty(EquipmentSlot.WEAPON.getSlot())) {
                    if (c.getInventory().contains(BRONZE_SWORD)) {
                        c.getInventory().interact(BRONZE_SWORD, "Wield");
                    } else {
                        c.getBank().openClosest();
                        MethodProvider.sleep(500, 1000);
                        c.getBank().depositAllItems();
                        MethodProvider.sleep(500, 1000);
                        c.getBank().withdraw(BRONZE_SWORD);
                        MethodProvider.sleep(200, 400);
                        c.getBank().close();
                    }
                    break;
                } else {
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
                }
            case "kill chickens":
                c.setStatus("Killing chickens for feathers");
                if (!chickenArea.contains(c.getLocalPlayer())) {
                    c.getWalking().walk(chickenArea.getRandomTile());
                    MethodProvider.sleep(500, 800);
                } else {
                    if (c.getLocalPlayer().isInCombat()) {
                        // do nothing
                    } else if (chickenArea.contains(c.getGroundItems().closest(FEATHERS))) {
                        GroundItem feather = c.getGroundItems().closest(FEATHERS);
                        if (feather != null) {
                            feather.interact("Take");
                            c.sleepUntil(() -> !feather.exists(), 3000);
                        }
                    } else {
                        NPC chicken = c.getNpcs().closest("Chicken");
                        if (chicken != null) {
                            chicken.interact("Attack");
                            MethodProvider.sleep(1000, 1500);

                        }


                    }
                }
        }
        return 500;
    }
}
