package xyz.breadloaf.audioplayerroleplay.position;

import net.minecraft.core.Vec3i;

import java.util.concurrent.ConcurrentHashMap;

class PositionFile {
    ConcurrentHashMap<String, Vec3i> id_to_location = new ConcurrentHashMap<>();
}
