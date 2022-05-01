package com.kamiskidder.shgr.module.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kamiskidder.shgr.event.client.PacketEvent;
import com.kamiskidder.shgr.manager.RotateManager;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.client.Timer;
import com.kamiskidder.shgr.util.entity.EntityUtil;
import com.kamiskidder.shgr.util.player.BlockUtil;
import com.kamiskidder.shgr.util.player.CrystalUtil;
import com.kamiskidder.shgr.util.player.InventoryUtil;
import com.kamiskidder.shgr.util.player.PlayerUtil;
import com.kamiskidder.shgr.util.render.GeometryMasks;
import com.kamiskidder.shgr.util.render.RenderUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoCrystal extends Module {
    public Setting<Boolean> place = register(new Setting("Place", true));
    public Setting<Float> placeDelay = register(new Setting("Place Delay", 0.0f, 10.0f, 0.0f));
    public Setting<Float> placeRange = register(new Setting("Place Range", 5.0f, 12.0f, 0.5f));
    public Setting<Float> placeWallRange = register(new Setting("Place Wall Range", 3.0f, 12.0f, 0.5f));
    public Setting<Float> placeMinDamage = register(new Setting("Place Min Damage", 5.0f, 20.0f, 0.1f));
    public Setting<Float> placeMaxSelf = register(new Setting("Place Max Self", 5.0f, 20.0f, 0.1f));
    public Setting<String> placeMode = register(new Setting("Place Mode", "Packet", new String[]{"Vanilla", "Packet"}));
    public Setting<Boolean> silentSwitch = register(new Setting("Silent Switch", false));
    public Setting<String> placeSortMode = register(new Setting("Place Sort Mode", "Safest", new String[]{"Nearest", "Safest"}));
    public Setting<Boolean> ignorePlace = register(new Setting("Ignore Place", false));
    public Setting<Boolean> opPlace = register(new Setting("1.13 Place", false));

    public Setting<Boolean> explode = register(new Setting("Explode", true));
    public Setting<Boolean> predict = register(new Setting("Predict", true));
    public Setting<Float> explodeDelay = register(new Setting("Explode Delay", 0.0f, 10.0f, 0.0f));
    public Setting<Float> explodeRange = register(new Setting("Explode Range", 5.0f, 12.0f, 0.5f));
    public Setting<Float> explodeWallRange = register(new Setting("Explode Wall Range", 3.0f, 12.0f, 0.5f));
    public Setting<Float> explodeMinDamage = register(new Setting("Explode Min Damage", 5.0f, 20.0f, 0.1f));
    public Setting<Float> explodeMaxSelf = register(new Setting("Explode Max Self", 5.0f, 20.0f, 0.1f));
    public Setting<String> explodeSortMode = register(new Setting("Explode Sort Mode", "Safest", new String[]{"Nearest", "Safest"}));
    public Setting<String> swingArm = register(new Setting("Swing Arm", "Main Hand", new String[]{"Main Hand", "Off Hand", "None"}));

    public Setting<Boolean> rotate = register(new Setting("Rotate", false));
    public Setting<Boolean> soundSync = register(new Setting("Sound Sync", true));
    public Setting<Boolean> placer = register(new Setting("Placer", false, v -> soundSync.getValue()));
    public Setting<String> targetMode = register(new Setting("Target Mode", "Nearest", new String[]{"Nearest", "Weakest"}));
    public Setting<Float> targetRange = register(new Setting("Target Range", 13.0f, 30.0f, 0.5f));
    public Setting<Boolean> focusTarget = register(new Setting("Focus Target", true));

    public Setting<Boolean> render = register(new Setting("Render", true));
    public Setting<Color> color = register(new Setting("Color", new Color(230, 0, 100, 100)));
    public Setting<Float> thickness = register(new Setting("Thickness", 1.0f, 3.0f, 0.1f));
    public Setting<Color> otColor = register(new Setting("Outline Color", new Color(255, 10, 100, 70)));
    public Setting<Boolean> fade = register(new Setting("Fade", true));
    public Setting<Float> speed = register(new Setting("Speed", 100.0f, 150.0f, 1.0f));

    private List<RenderPos> renderPositions;
    private EntityPlayer target = null;
    private Timer placeTimer, explodeTimer;
    private BlockPos lastPlace = null;

    public AutoCrystal() {
        super("AutoCrystal", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        placeTimer = new Timer();
        explodeTimer = new Timer();
        renderPositions = new ArrayList<>();
    }

    @Override
    public void onDisable() {
        RotateManager.reset();
        lastPlace = null;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            if (target == null) return;

            SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            if (explode.getValue() && predict.getValue() && packet.getType() == 51) {
                Vec3d pos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());

                double range = PlayerUtil.getDistance(pos);
                if (range > explodeRange.getValue())
                    return;
                if (!PlayerUtil.canBlockBeSeen(new BlockPos(pos)) && range > explodeWallRange.getValue())
                    return;

                double damage = CrystalUtil.calculateDamage(pos, target);
                if (damage < explodeMinDamage.getValue())
                    return;
                double selfDamage = CrystalUtil.calculateDamage(pos, mc.player);
                if (selfDamage > explodeMaxSelf.getValue())
                    return;
                if (selfDamage > damage)
                    return;

                CPacketUseEntity useEntity = new CPacketUseEntity();
                useEntity.entityId = packet.getEntityID();
                useEntity.action = CPacketUseEntity.Action.ATTACK;
                mc.player.connection.sendPacket(useEntity);
                swing();

                if (rotate.getValue()) RotateManager.lookAtPos(new BlockPos(pos).add(0, -1, 0));
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS
                    && packet.getSound().equals(SoundEvents.ENTITY_GENERIC_EXPLODE) && soundSync.getValue()) {
                for (EntityEnderCrystal crystal : mc.world.getEntities(EntityEnderCrystal.class, e -> true)) {
                    if (packet.getX() == crystal.posX && packet.getY() == crystal.posY && packet.getZ() == crystal.posZ) {
                        crystal.setDead();
                        if (placer.getValue()) placeCrystal();
                    }
                }
            }
        }
    }

    @Override
    public void onRender3D() {
        try {
            if (render.getValue()) {
                if (fade.getValue()) {
                    Iterator<RenderPos> iterator = renderPositions.iterator();
                    while (iterator.hasNext()) {
                        RenderPos pos = iterator.next();
                        renderBox(pos.pos, pos.color, pos.otColor);
                        pos.color = modifyColor(pos.color);
                        pos.otColor = modifyColor(pos.otColor);
                        if (isDead(pos.color) && isDead(pos.otColor)) {
                            iterator.remove();
                        }
                    }
                } else {
                    if (lastPlace != null) {
                        renderBox(lastPlace, color.getValue(), otColor.getValue());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isDead(Color c) {
        return c.getAlpha() == 0;
    }

    private Color modifyColor(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();
        a -= speed.getValue() * 0.01;
        if (a < 0) a = 0;
        return new Color(r, g, b, a);
    }

    private void renderBox(BlockPos pos, Color color, Color outLineColor) {
        RenderUtil.drawBox(pos, 1.0F, color, GeometryMasks.Quad.ALL);
        RenderUtil.drawBoundingBox(pos, 1.0, thickness.getValue().floatValue(), outLineColor);
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;

        if (place.getValue() && placeTimer.passedD(placeDelay.getValue())) {
            placeTimer.reset();
            placeCrystal();
        }

        if (explode.getValue() && explodeTimer.passedD(explodeDelay.getValue())) {
            explodeTimer.reset();
            explodeCrystal();
        }
    }

    private void placeCrystal() {
        int crystal = mc.player.getHeldItemOffhand().item == Items.END_CRYSTAL ? 999 : InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
        if (crystal == -1) return;
        EnumHand hand = crystal == 999 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

        EntityPlayer target = findTarget();
        if (target == null) return;

        List<BlockPos> placeablePositions = CrystalUtil.placePostions(placeRange.getValue(), opPlace.getValue());
        List<PlacePosition> attachablePositions = new ArrayList<>();
        double maxDamage = 0;

        for (BlockPos pos : placeablePositions) {
            double range = PlayerUtil.getDistance(pos);
            if (range > placeRange.getValue())
                continue;
            if (!PlayerUtil.canBlockBeSeen(pos) && range > placeWallRange.getValue())
                continue;

            // Check is there crystal
            List<EntityEnderCrystal> crystalList = mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(pos))
                    .stream().filter(e -> !EntityUtil.getEntityPos(e).equals(pos.add(0, -1, 0))).collect(Collectors.toList());
            if (!crystalList.isEmpty())
                continue;

            Vec3d crystalPos = new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            double damage = CrystalUtil.calculateDamage(crystalPos, target);
            if (damage < placeMinDamage.getValue())
                continue;
            double selfDamage = CrystalUtil.calculateDamage(crystalPos, mc.player);
            if (selfDamage > placeMaxSelf.getValue())
                continue;
            if (selfDamage > damage)
                continue;

            if (damage > maxDamage)
                maxDamage = damage;

            attachablePositions.add(new PlacePosition(pos, damage, selfDamage));
        }

        double finalMaxDamage = maxDamage;
        Stream<PlacePosition> positionStream = attachablePositions.stream().filter(p -> p.damage >= finalMaxDamage);
        PlacePosition best = null;

        if (placeSortMode.getValue().equals("Nearest")) {
            best = positionStream.min(Comparator.comparingDouble(p -> PlayerUtil.getDistance(p.pos))).orElse(null);
        } else if (placeSortMode.getValue().equals("Safest")) {
            best = positionStream.min(Comparator.comparingDouble(p -> p.selfDamage)).orElse(null);
        }

        if (best == null) return;

        BlockPos bestPosition = best.pos;

        if (!ignorePlace.getValue() &&
                !mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(bestPosition)).isEmpty())
            return;

        if (crystal != 999) {
            if (silentSwitch.getValue()) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(crystal));
            } else {
                mc.player.inventory.currentItem = crystal;
                mc.playerController.updateController();
            }
        }

        if (placeMode.getValue().equalsIgnoreCase("Vanilla")) {
            mc.playerController.processRightClick(mc.player, mc.world, hand);
        } else {
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(bestPosition, BlockUtil.getDirection(bestPosition), hand, 0.f, 0f, 0f));
        }

        if (silentSwitch.getValue()) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
        }

        if (rotate.getValue()) RotateManager.lookAtPos(bestPosition);

        //weird
        if (fade.getValue()) {
            try {
                boolean found = false;
                for (int i = 0; i < renderPositions.size(); i++) {
                    if (renderPositions.get(i).pos.equals(bestPosition)) {
                        renderPositions.get(i).color = color.getValue();
                        renderPositions.get(i).otColor = otColor.getValue();
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    renderPositions.add(new RenderPos(bestPosition, color.getValue(), otColor.getValue()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            lastPlace = bestPosition;
        }
    }

    private void explodeCrystal() {
        EntityPlayer target = findTarget();
        if (target == null) return;

        List<BreakableCrystal> breakableCrystals = new ArrayList<>();
        double maxDamage = 0;
        for (EntityEnderCrystal crystal : mc.world.getEntities(EntityEnderCrystal.class, e -> true)) {
            double range = PlayerUtil.getDistance(crystal);
            if (range > explodeRange.getValue())
                continue;
            if (!PlayerUtil.canSeeEntity(crystal) && range > explodeWallRange.getValue())
                continue;

            double damage = CrystalUtil.calculateDamage(crystal.getPositionVector(), target);
            if (damage < explodeMinDamage.getValue())
                continue;
            double selfDamage = CrystalUtil.calculateDamage(crystal.getPositionVector(), mc.player);
            if (selfDamage > explodeMaxSelf.getValue())
                continue;
            if (selfDamage > damage)
                continue;

            if (damage > maxDamage)
                maxDamage = damage;

            breakableCrystals.add(new BreakableCrystal(crystal, damage, selfDamage));
        }

        double finalMaxDamage = maxDamage;
        Stream<BreakableCrystal> crystalStream = breakableCrystals.stream().filter(c -> c.damage <= finalMaxDamage);
        BreakableCrystal best = null;

        if (explodeSortMode.getValue().equals("Nearest")) {
            best = crystalStream.min(Comparator.comparing(c -> PlayerUtil.getDistance(c.crystal))).orElse(null);
        } else if (explodeSortMode.getValue().equals("Safest")) {
            best = crystalStream.min(Comparator.comparing(c -> c.selfDamage)).orElse(null);
        }

        if (best == null) return;

        Entity crystal = best.crystal;
        mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        swing();

        if (rotate.getValue()) RotateManager.lookAtPos(new BlockPos(crystal).add(0, -1, 0));
    }

    private void swing() {
        if (swingArm.getValue().equalsIgnoreCase("None")) {
            mc.player.connection.sendPacket(new CPacketAnimation(swingArm.getValue().equalsIgnoreCase("Main Hand") ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
        }
    }

    private EntityPlayer findTarget() {
        if (focusTarget.getValue() && target != null && !target.isDead && PlayerUtil.getDistance(target) < targetRange.getValue())
            return target;

        Stream<EntityPlayer> players = EntityUtil.getPlayers().stream().filter(e -> PlayerUtil.getDistance(e) < targetRange.getValue());

        if (targetMode.getValue().equalsIgnoreCase("Nearest")) {
            target = players.min(Comparator.comparing(PlayerUtil::getDistance)).orElse(null);
        } else if (targetMode.getValue().equalsIgnoreCase("Weakest")) {
            target = players.min(Comparator.comparing(EntityUtil::getPlayerHealth)).orElse(null);
        }

        return target;
    }

    private class PlacePosition {
        public BlockPos pos;
        public double damage;
        public double selfDamage;

        public PlacePosition(BlockPos pos, double damage, double selfDamage) {
            this.pos = pos;
            this.damage = damage;
            this.selfDamage = selfDamage;
        }
    }

    private class BreakableCrystal {
        public Entity crystal;
        public double damage;
        public double selfDamage;

        public BreakableCrystal(Entity crystal, double damage, double selfDamage) {
            this.crystal = crystal;
            this.damage = damage;
            this.selfDamage = selfDamage;
        }
    }

    private class RenderPos {
        public BlockPos pos;
        public Color color, otColor;

        public RenderPos(BlockPos pos, Color color, Color otColor) {
            this.pos = pos;
            this.color = color;
            this.otColor = otColor;
        }
    }
}
