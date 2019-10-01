package ninjaphenix.userdefinedadditions.api.readers;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.impl.Marshaller;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import ninjaphenix.userdefinedadditions.api.ReaderManager;
import ninjaphenix.userdefinedadditions.api.readers.interfaces.Reader;

public class BlockSettingsReaderV0 implements Reader<Block.Settings>
{
    private static final BlockSettingsReaderV0 INSTANCE = new BlockSettingsReaderV0();

    public static Reader<Block.Settings> getInstance() { return INSTANCE; }

    @Override
    public Block.Settings read(JsonObject object)
    {
        final Marshaller marshaller = object.getMarshaller();
        final JsonObject materialObject = object.getObject("material");
        final JsonObject materialColorObject = object.getObject("material_color");
        Material material = null;
        if (materialObject != null)
        {
            ReaderReader.ReaderData data = ReaderReader.getInstance().read(materialObject);
            material = (Material) ReaderManager.getInstance().get(data.getType(), data.getVersion()).read(data.getData());
        }
        else return null;
        MaterialColor materialColor = material.getColor();
        if (materialColorObject != null)
        {
            ReaderReader.ReaderData data = ReaderReader.getInstance().read(materialColorObject);
            materialColor = (MaterialColor) ReaderManager.getInstance().get(data.getType(), data.getVersion()).read(data.getData());
        }
        final Float hardness = marshaller.marshall(Float.class, object.get("hardness"));
        final Float resistance = marshaller.marshall(Float.class, object.get("resistance"));
        final Float slipperiness = marshaller.marshall(Float.class, object.get("slipperiness"));
        final Integer lightLevel = marshaller.marshall(Integer.class, object.get("light_level"));
        final Boolean breakInstantly = marshaller.marshall(Boolean.class, object.get("break_instantly"));
        FabricBlockSettings settings = FabricBlockSettings.of(material, materialColor);
        if (breakInstantly) settings.breakInstantly();
        else
        {
            if (hardness != null && hardness >= 0.0F) settings.hardness(hardness);
            else settings.hardness(1.0F);
            if (resistance != null && resistance >= 0.0F) settings.resistance(resistance);
            else settings.resistance(1.0F);
        }
        if (slipperiness != null) settings.slipperiness(slipperiness);
        if (lightLevel != null && lightLevel >= 0 && lightLevel <= 15) settings.lightLevel(lightLevel);
        return settings.build();
    }
}