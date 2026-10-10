package scalinghealthissues.compat;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;

import java.util.List;

public abstract class ModLoadedUtil {

    public static final String BLOODMOON_MODID = "bloodmoon";
    public static final String CHAMPIONS_MODID = "champions";
    public static final String DEFILED_LANDS_MODID = "defiledlands";
    public static final String FIRST_AID_MODID = "firstaid";
    public static final String INFERNAL_MOBS_MODID = "infernalmobs";
    public static final String LYCANITES_MOBS_MODID = "lycanitesmobs";
    public static final String RLMIXINS_MODID = "rlmixins";
    public static final String SRP_MODID = "srparasites";

    public static LoadedContainer CHAMPIONS = new LoadedContainer(CHAMPIONS_MODID);
    public static LoadedContainer FIRST_AID = new LoadedContainer(FIRST_AID_MODID);
    public static LoadedContainer INFERNAL_MOBS = new LoadedContainer(INFERNAL_MOBS_MODID);
    public static LoadedContainer RLMIXINS = new LoadedContainer(RLMIXINS_MODID);

    // Nischhelm style
    public static boolean versionInRange(LoadedContainer container, String version) {
        if (!container.isLoaded()) return false;
        VersionRange range;
        try {
            range = VersionRange.createFromVersionSpec(version);
        } catch (Exception e) {
            return false;
        }
        return range.containsVersion(container.getVersion());
    }

    public static class LoadedContainer{
        private Boolean isLoaded = null;
        private DefaultArtifactVersion version;
        private final String key;
        private String name;
        public List<String> authorList;
        private LoadedContainer(String key){
            this.key = key;
        }
        public boolean isLoaded(){
            if(this.isLoaded == null) isLoaded = Loader.isModLoaded(key);
            return isLoaded;
        }
        public DefaultArtifactVersion getVersion(){
            if(version == null) version = new DefaultArtifactVersion(Loader.instance().getIndexedModList().get(key).getVersion());
            return version;
        }
        public String getName(){
            if(name == null) name = Loader.instance().getIndexedModList().get(key).getName();
            return name;
        }
        public boolean containsAuthor(String author) {
            if(authorList == null) authorList = Loader.instance().getIndexedModList().get(key).getMetadata().authorList;
            return authorList.contains(author);
        }
    }
}
