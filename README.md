# Navigation Compasses

![The logo for Navigation Compasses](src/main/resources/assets/navigation-compasses/icon.png)

> [!WARNING]
> This mod can cause extreme lag due to the nature of structure/biome location. This may be remedied in the future with async location but for now, be careful! 

## Usage

### For server owners

You can install the mod with Fabric API and it will work as intended. However, for the best experience I recommend you install [Polymer](https://modrinth.com/mod/polymer) and set up [resource pack hosting](https://polymer.pb4.eu/latest/user/resource-pack-hosting/).

There is a configuration file at `config/navigation-compasses.json` which lets you disable the compass items (ex. if you are running Terra you might want to disable biome locators due to the lag it causes), modify the search range (which will reduce the length lag spikes), and the search cooldown (in ticks, will probably reduce number of lag spikes).

It should work with any other structure/biome mods, though some may cause lag while locating depending on rarity.

Floodgate (Bedrock) players have special GUI handling, so it'll work great with them. (If you are concerned about item textures, you can try the ***heavily beta not-complete could-break no-warranty*** compatibility mod [Bedframe](https://github.com/sylvxa/bedframe), which adds Polymer textures to Geyser)

### For players

The **Biome Locator** does what it says on the tin, it opens a menu containing every biome and lets you select one to locate.

![The recipe of the Biome Locator, consisting of 4 saplings in the corners, 3 dirt blocks in the left, bottom, and right middle, a flower in the top middle, and a compass in the middle middle.](gallery/biome_recipe.png)

*The saplings can be any kind of sapling, the dirt can be any dirt-like block, and the flower can be any short flower.*

The **Structure Locator** works the same way but for structures.

![The recipe of the Structure Locator, consisting of 4 cobwebs in the corners, 3 cracked stone bricks in the left, bottom, and right middle, a map in the top middle, and a compass in the middle middle.](gallery/structure_recipe.png)

The locators add a locator bar waypoint and action bar message to help assist with navigation.

![A picture of the locator bar with the navigation helper elements](gallery/hotbar.png)

*These elements are faked through some packet trickery and are only visible to the player locating.*