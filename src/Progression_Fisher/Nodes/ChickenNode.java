package Progression_Fisher.Nodes;

import Progression_Fisher.Fighter_main;
import Progression_Fisher.Node;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;

public class ChickenNode extends Node {
    public static String status = "check weapons";
    public static final String FEATHERS = "Feather";
    public static final String WOODEN_SHIELD = "Wooden shield";
    public static final String BRONZE_SWORD = "Bronze sword";

    Area chickenArea = new Area(3020, 3296, 3014, 3282, 0);

    public ChickenNode(Fighter_main main) {
        super(main);
    }

    @Override
    public boolean validate() {
        // fishing lvl is greater than or equal to 20 and less than 1k feathers in inventory
        return (c.getSkills().getRealLevel(Skill.FISHING) >= 20 && c.getInventory().count(FEATHERS) <= 200);
    }

    @Override
    public int execute() {
        switch (status) {
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
