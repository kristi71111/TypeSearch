package kristi71111.typesearch.mixins;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PokemonStorage;
import com.pixelmonmod.pixelmon.comm.packetHandlers.clientStorage.newStorage.pc.ServerQueryPC;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.BaseStats;
import com.pixelmonmod.pixelmon.enums.EnumType;
import org.apache.commons.lang3.text.WordUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(value = ServerQueryPC.class)
public abstract class MixinServerQueryPC {
    /**
     * @author kristi71111
     * @reason Add option to search @type
     */
    @Inject(method = "search", at = @At("HEAD"), remap = false, cancellable = true)
    private static void search(PokemonStorage storage, String query, CallbackInfoReturnable<List<Pokemon>> cir) {
        //Length check so that we don't check the @ before it can even possibly match anything at all.
        if (query.length() > 3 && Character.toString(query.charAt(0)).equals("@")) {
            final String getType = WordUtils.capitalize(query.substring(1));
            List<Pokemon> matches = new ArrayList<>();
            final EnumType searchedType = EnumType.parseOrNull(getType);
            if (searchedType == null) {
                cir.setReturnValue(Collections.emptyList());
            }
            Pokemon[] all = storage.getAll();
            for (int i = 0, allLength = all.length; i < allLength; i++) {
                Pokemon pokemon = all[i];
                if (pokemon == null || pokemon.isEgg()) {
                    continue;
                }
                BaseStats stats = pokemon.getBaseStats();
                EnumType type1 = stats.getType1();
                EnumType type2 = stats.getType2();
                if (type1.equals(searchedType) || (type2 != null && type2.equals(searchedType))) {
                    matches.add(pokemon);
                }
            }
            cir.setReturnValue(matches);
        }
    }
}

