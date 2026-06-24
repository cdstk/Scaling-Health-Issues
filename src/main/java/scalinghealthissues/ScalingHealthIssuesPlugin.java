package scalinghealthissues;

import fermiumbooter.FermiumRegistryAPI;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class ScalingHealthIssuesPlugin implements IFMLLoadingPlugin {

	public ScalingHealthIssuesPlugin() {
		MixinBootstrap.init();

		FermiumRegistryAPI.enqueueMixin(false, "mixins.scalinghealthissues.vanilla.json");

		FermiumRegistryAPI.enqueueMixin(true, "mixins.scalinghealthissues.scalinghealth.json");

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
	public void injectData(Map<String, Object> data) { }
	
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}