package kristi71111.typesearch.mixins;

import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PokemonStorage;
import com.pixelmonmod.pixelmon.comm.packetHandlers.clientStorage.newStorage.pc.ServerQueryPCPacket;
import org.apache.commons.lang3.text.WordUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(value = ServerQueryPCPacket.class)
public abstract class ServerQueryPCPacketMixin {
    /**
     * @author kristi71111
     * @reason Add option to search @type
     */
    @Inject(method = "search", at = @At("HEAD"), remap = false, cancellable = true)
    public void search(PokemonStorage storage, String query, CallbackInfoReturnable<List<Pokemon>> cir) {
        //Length check so that we don't check the @ before it can even possibly match anything at all.
        if (query.length() > 3 && Character.toString(query.charAt(0)).equals("@")) {
            final String getType = WordUtils.capitalize(query.substring(1));
            final Element searchedElement = Element.parseOrNull(getType);
            if (searchedElement == null) {
                cir.setReturnValue(Collections.emptyList());
            }
            final List<Pokemon> matches = new ArrayList<>();
            final Pokemon[] pokemon = storage.getAll();
            for (int i = 0; i < pokemon.length; ++i) {
                Pokemon pokemonActual = pokemon[i];
                if (pokemonActual == null || pokemonActual.isEgg()) {
                    continue;
                }
                for (Element element : pokemonActual.getForm().getTypes()) {
                    if (element.getName().equals(getType)) {
                        matches.add(pokemonActual);
                        break;
                    }
                }
            }
            cir.setReturnValue(matches);
        }
    }
}
