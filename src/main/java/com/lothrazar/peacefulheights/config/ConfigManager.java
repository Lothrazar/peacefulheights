package com.lothrazar.peacefulheights.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.lothrazar.peacefulheights.PeacefulMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.loading.FMLPaths;

public class ConfigManager {

  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
  private static ForgeConfigSpec CFG;
  public static IntValue MAXHEIGHT;
  public static BooleanValue PRINTLOGS;
  static {
    initConfig();
  }

  private static void initConfig() {
    BUILDER.comment("These only apply to spawns that are both Natural and Hostile.  Natrual spawns means from regular world gen, does not include spawn eggs, spawner blocks, etc.").push(PeacefulMod.MODID);
    String category = "hostile.";
    MAXHEIGHT = BUILDER.comment("Hostile mobs > this value will be denied").defineInRange(category + "maxHeight", 63, 0, 256);
    PRINTLOGS = BUILDER.comment("logging").define("logActivity", false);
    BUILDER.pop();
    CFG = BUILDER.build();
  }

  public static void setup() {
    final CommentedFileConfig configData = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(PeacefulMod.MODID + ".toml"))
        .sync()
        .autosave()
        .writingMode(WritingMode.REPLACE)
        .build();
    configData.load();
    CFG.setConfig(configData);
  }
}
