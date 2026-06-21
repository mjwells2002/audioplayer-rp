Requires Simple Voicechat >2.6.20 

Requires Audioplayer >2.3.0
- Added randomized audio playlists feature
- Added region modes
  - CLIP - the same as how regions used to work, a rectangle that clips a much larger radius sphere that contains it
  - CLIP_MANUAL_RANGE - like clip, expect the range is not automatically set to cover the entire region, you can set it manually
  - FALLOFF - a new mode where audio plays static (with no 3D audio direction) inside the region, and then with locationality pointing towards the region within the items range of the region
- Tweak commands that accept audio ids as an argument to also accept filenames, with a search in the command suggestions
- Add ability to bulk import a folder of files into a playlist for randomized audio
