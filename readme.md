# AudioPlayer Roleplay Addon

Roleplay/Minigame additions to the [AudioPlayer](https://modrinth.com/mod/audioplayer) mod.


Requires [AudioPlayer](https://modrinth.com/mod/audioplayer)
and [Simple Voice Chat](https://modrinth.com/mod/simple-voice-chat).

# Features

Features are defined as modules, that can be disabled or enabled in the server config, 
modules can be stacked in any valid combination, currently there are 4 modules.

# Commands

- `/roleplay info` - Shows all information for all modules on the held item 

Every module has the following commands:
- `/roleplay <module name> remove` - removes the module from the held item
- `/roleplay <module name> info` - provides a small info blurb about the module and held item (if present)
- `/roleplay <module name>` - the same as info

## Static Playback

This module makes the audio static, removing 3D audio in the range.

This will be automatically upgraded from AudioPlayer v1 static playback, starting with AudioPlayer 2.1.x.

Commands:
- `/roleplay static apply` - Apply the static playback module to the item

## Randomized Playback

This module allows you to have multiple sounds on one item, and the sound that is played is picked randomly.

This will be automatically upgraded from AudioPlayer v1 randomized playback, starting with AudioPlayer 2.1.x.

Commands:
- `/roleplay randomized append` - Adds a sound to this item, additionally enabling randomized playback if needed
- `/roleplay randomized enable` - Enables randomized playback, but does not add another sound

## Regions

This module allows you to override the default spherical area for playback, 
and replace it with a custom volume defined by two points.

You can also save a region as a "Named Region", and use it across multiple.

By default, a region can be used from anywhere, 
however there is a config option in the regions config file to allow a server owner to limit this.

Commands
- `/roleplay regions apply <pos1> <pos2>` - Sets the region of this item to be between the supplied coordinates
- `/roleplay regions apply <region>` - Sets the region to the supplied named region
- `/roleplay regions named set <region> <pos1> <pos2>` - Creates or edits an existing named region (takes place instantly)
- `/roleplay regions named list` - Lists all named regions on the server

## Custom Volume Category

This module allows you to create custom volume sliders in Voice chat, and assign items to them.

The categories (volume sliders) must be explicitly created from a config located in `<world_folder>/audioplayer_rp/custom_volume_category/categories.properties`.
This config can be reloaded while the server is running, and icons can be assigned in the config as well.

- `/roleplay volume apply <category>` - sets the category of this item to the one provided
- `/roleplay volume reload` - reloads the config to load all categories
