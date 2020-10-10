package com.lothrazar.peacefulheights;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.lothrazar.peacefulheights.config.ConfigManager;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// TODO: The value here should match an entry in the META-INF/mods.toml file
// TODO: Also search and replace it in build.gradle
@Mod(PeacefulMod.MODID)
public class PeacefulMod {

  public static final String MODID = "peacefulheights";
  public static final Logger LOGGER = LogManager.getLogger();

  public PeacefulMod() {
    MinecraftForge.EVENT_BUS.register(this);
    ConfigManager.setup();
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
  }

  private void setup(final FMLCommonSetupEvent event) {
    //now all blocks/items exist 
    //    proxy.setup();
  }

  @SubscribeEvent
  public static void onFingerprintViolation(FMLFingerprintViolationEvent event) {
    // https://tutorials.darkhax.net/tutorials/jar_signing/
    String source = (event.getSource() == null) ? "" : event.getSource().getName() + " ";
    String msg = PeacefulMod.MODID + "Invalid fingerprint detected! The file " + source + "may have been tampered with. This version will NOT be supported by the author!";
    System.out.println(msg);
    LOGGER.info(msg);
  }
  //
  //  @SubscribeEvent
  //  public void onSummonAidEvent(ZombieEvent.SummonAidEvent event) {
  //    LOGGER.info("cancel ZombieEvent" + event.getEntity());
  //  }

  @SubscribeEvent
  public void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
    if (!(event.getEntity() instanceof LivingEntity)) {
      return;
    }
    if (event.getSpawner() != null) {
      return;//from spawner block: ignore
    }
    LivingEntity entity = (LivingEntity) event.getEntity();
    if (this.doDenySpawn(event, entity, event.getSpawnReason())) {
      event.setResult(Result.DENY);
      if (ConfigManager.PRINTLOGS.get())
        LOGGER.info("SpecialSpawn denied y=" + ((int) event.getY()) + event.getSpawnReason()
            + " " + entity.getType().getRegistryName());
    }
  }

  @SubscribeEvent
  public void onEntitySpawnEvent(LivingSpawnEvent.CheckSpawn event) {
    if (!(event.getEntity() instanceof LivingEntity)) {
      return;
    }
    LivingEntity entity = (LivingEntity) event.getEntity();
    if (this.doDenySpawn(event, entity, event.getSpawnReason())) {
      event.setResult(Result.DENY);
      if (ConfigManager.PRINTLOGS.get())
        LOGGER.info("CheckSpawn denied y=" + ((int) event.getY()) + event.getSpawnReason()
            + " " + entity.getType().getRegistryName());
    }
  }

  private boolean doDenySpawn(LivingSpawnEvent event, LivingEntity entity, SpawnReason spawnReason) {
    if (spawnReason != SpawnReason.NATURAL || entity instanceof PlayerEntity
        || entity.getType().getClassification() != EntityClassification.MONSTER) {
      //either its a player, or a non-monster, or an un-natrual spawn
      return false;//dont deny
    }
    //its a natural spawn
    //it is a monster
    //its NOT a player
    //. we might deny it
    if (event.getY() > ConfigManager.MAXHEIGHT.get()) {
      return true;//DENIED
    }
    return false;
  }
}
