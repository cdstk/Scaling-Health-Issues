package scalinghealthissues;

import fermiumbooter.FermiumRegistryAPI;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import scalinghealthissues.compat.ModLoadedUtil;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class ScalingHealthIssuesPlugin implements IFMLLoadingPlugin {

	public ScalingHealthIssuesPlugin() {
		FermiumRegistryAPI.enqueueMixin(false, "mixins.scalinghealthissues.vanilla.json");

		FermiumRegistryAPI.enqueueMixin(true, "mixins.scalinghealthissues.scalinghealth.json");

		FermiumRegistryAPI.enqueueMixin(true, "mixins.scalinghealthissues.firstaid.json", () -> FermiumRegistryAPI.isModPresent(ModLoadedUtil.FIRST_AID_MODID));

//		if(FermiumRegistryAPI.isModPresent("fermiummixins")) {
//			// Older patch, uses redirect so no chaining
//			FermiumRegistryAPI.removeMixin("mixins.fermiummixins.late.champions.deathmessage.json");
//		}
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[0];
	}
	
	@Override
	public String getModContainerClass()
	{
		return null;
	}
	
	@Override
	public String getSetupClass()
	{
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) {
		if (Boolean.FALSE.equals(data.get("runtimeDeobfuscationEnabled"))) {
			MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
			CoreModManager.getReparseableCoremods().removeIf(s -> StringUtils.containsIgnoreCase(s, "fermiumbooter"));
		}
	}
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}