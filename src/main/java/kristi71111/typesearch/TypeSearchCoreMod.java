package kristi71111.typesearch;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Optional;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("TypeSearch")
public class TypeSearchCoreMod implements IFMLLoadingPlugin {
    public static final Logger LOGGER = LogManager.getLogger("TypeSearch");

    public TypeSearchCoreMod(){
        //This is fairly hacky but we must do it so we can edit the class and there's probably a better way of doing this that I atm don't know about
        findAndLoadAsCoremod("com/pixelmonmod/pixelmon/comm/packetHandlers/clientStorage/newStorage/pc/ServerQueryPC.class");
    }
    public void findAndLoadAsCoremod(String classReference) {
        try {
            FMLLog.log.info("Trying to load {} ...", classReference);
            Path modsFolder = Paths.get("mods");
            if (!Files.exists(modsFolder)) {
                FMLLog.log.error("The mods folder couldn't be found.\nFolder: {}", modsFolder.toString());
                return;
            }
            PathMatcher jarMatcher = modsFolder.getFileSystem().getPathMatcher("glob:*.jar");
            Optional<Path> coremodJar = Files.list(modsFolder).filter(jar -> jarMatcher.matches(jar.getFileName())).filter(jar -> {
                try (FileSystem zip = FileSystems.newFileSystem(jar, null)){
                    return Files.exists(zip.getPath(classReference));
                } catch (IOException ex) {
                    return false;
                }
            }).findFirst();
            if (!coremodJar.isPresent()) {
                FMLLog.log.error("{} jar cannot be found, the program will not continue to load this coremod.", classReference);
                return;
            }
            Path coremod = coremodJar.get();
            if (!CoreModManager.getReparseableCoremods().contains(coremod.getFileName().toString())) {
                Launch.classLoader.addURL(coremod.toUri().toURL());
                CoreModManager.getReparseableCoremods().add(coremod.getFileName().toString());
            }
            FMLLog.log.info("Loaded {}!", coremod);
        } catch (Exception e) {
            try {
                FMLLog.log.error("There was a problem trying to find {}, the program will not continue to load this coremod.", classReference, e);
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartedEvent event) {
        LOGGER.info("TypeSearch enabled!");
    }
}
