package xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback;

import de.maxhenkel.audioplayer.api.events.GetSoundIdEvent;

import static xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedPlayback.RANDOM_PLAYBACK_MODULE;

public class RandomizedPlaybackHooks {

    public static void onGetSoundId(GetSoundIdEvent event) {
        RandomizedSoundModule rngModule = event.getData().getModule(RANDOM_PLAYBACK_MODULE).orElse(null);
        if (rngModule != null) {
            event.setSoundId(rngModule.getUUID());
        }
    }

}

