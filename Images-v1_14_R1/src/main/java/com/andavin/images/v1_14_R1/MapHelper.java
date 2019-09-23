package com.andavin.images.v1_14_R1;

import com.andavin.reflect.Reflection;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @since September 20, 2019
 * @author Andavin
 */
class MapHelper extends com.andavin.images.MapHelper {

    private static final int DEFAULT_STARTING_ID = 8000;
    private static final Map<UUID, AtomicInteger> MAP_IDS = new HashMap<>(4);
    private static final Field ENTITY_ID = Reflection.findField(Entity.class, "id");

    @Override
    protected MapView getWorldMap(int id) {
        return Bukkit.getMap(id);
    }

    @Override
    protected int nextMapId(org.bukkit.World world) {
        return MAP_IDS.computeIfAbsent(world.getUID(), __ ->
                new AtomicInteger(DEFAULT_STARTING_ID)).getAndIncrement();
    }

    @Override
    protected void createMap(int frameId, int mapId, Player player, Location location, BlockFace direction, byte[] pixels) {

    }

    @Override
    protected void destroyMap(Player player, int[] frameIds) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(frameIds));
    }

    @Override
    protected byte[] createPixels(BufferedImage image) {

        int pixelCount = image.getWidth() * image.getHeight();
        int[] pixels = new int[pixelCount];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        byte[] colors = new byte[pixelCount];
        for (int i = 0; i < pixelCount; i++) {
            colors[i] = MapPalette.matchColor(new Color(pixels[i], true));
        }

        return colors;
    }
}
