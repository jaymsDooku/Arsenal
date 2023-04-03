package dev.jayms.arsenal.artillery.impl.field;

import dev.jayms.arsenal.Arsenal;
import dev.jayms.arsenal.artillery.Artillery;
import dev.jayms.arsenal.artillery.ArtilleryManager;
import dev.jayms.arsenal.artillery.ArtilleryMissileState;
import dev.jayms.arsenal.artillery.event.MissileImpactEvent;
import dev.jayms.arsenal.artillery.impl.GenericMissileFinishState;
import dev.jayms.arsenal.artillery.impl.trebuchet.Trebuchet;
import dev.jayms.arsenal.artillery.shooter.PlayerShooter;
import dev.jayms.arsenal.artillery.shooter.Shooter;
import dev.jayms.arsenal.util.LocationTools;
import isaac.bastion.Bastion;
import isaac.bastion.BastionBlock;
import isaac.bastion.manager.BastionBlockManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import vg.civcraft.mc.citadel.ReinforcementLogic;
import vg.civcraft.mc.citadel.model.Reinforcement;

import java.util.*;

public class FieldArtilleryMissileFinishState extends GenericMissileFinishState<FieldArtilleryMissile> {

}
