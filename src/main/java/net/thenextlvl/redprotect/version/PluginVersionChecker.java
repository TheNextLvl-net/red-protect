package net.thenextlvl.redprotect.version;

import core.paper.version.PaperModrinthVersionChecker;
import core.version.SemanticVersion;
import net.thenextlvl.redprotect.RedProtect;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PluginVersionChecker extends PaperModrinthVersionChecker<SemanticVersion> {
    public PluginVersionChecker(RedProtect plugin) {
        super(plugin, "loXscTUV");
    }

    @Override
    public @Nullable SemanticVersion parseVersion(String version) {
        return SemanticVersion.parse(version);
    }
}
