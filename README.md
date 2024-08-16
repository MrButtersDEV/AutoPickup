# AutoPickup
Pick up the items you mine

### Support Discord:
For any issues or questions, feel free to join our Discord for quick support!  
[https://discord.gg/ncHH4FP](https://discord.gg/ncHH4FP)

### Other Support Methods:
GitHub and Spigot DMs (`@MrButtersDev`) are another good place for support. I check them almost daily. For quick and general support, Discord is the best option. However, for documenting bugs, feel free to create an issue on GitHub.

> The below documentation was generated using ChatGPT by OpenAI. For further clarification, join our Discord for support or view an up-to-date "default" of the `config.yml` file on GitHub.

## Configuration - `config.yml`
> The `msgPrefix` field is a string that represents the message prefix for messages related to the AutoPickup feature. It is displayed in the chat with specific color code formatting.
> 
> The `msgAutoPickupEnable` and `msgAutoPickupDisable` fields are strings that represent the messages displayed when the player enables or disables the AutoPickup feature, respectively. They are displayed in the chat with specific color code formatting.
> 
> The `msgAutoSmeltEnable` and `msgAutoSmeltDisable` fields are strings that represent the messages displayed when the player enables or disables the AutoSmelt feature, respectively. They are displayed in the chat with specific color code formatting.
> 
> The `msgAutoMobDropsEnable` and `msgAutoMobDropsDisable` fields are strings that represent the messages displayed when the player enables or disables the AutoMobDrops feature, respectively. They are displayed in the chat with specific color code formatting.
> 
> The `msgAutoEnable` field is a string that represents the message displayed when the AutoPickup feature is automatically enabled. It is displayed in the chat with specific color code formatting. The `doAutoEnableMSG` field is a boolean value that specifies whether the message defined in the `msgAutoEnable` field should be displayed or not.
> 
> The `msgEnabledJoinMSG` field is a string that represents the message displayed when a player joins the game and the AutoPickup feature is automatically enabled. It is displayed in the chat with specific color code formatting. The `doEnabledOnJoinMSG` field is a boolean value that specifies whether the message defined in the `msgEnabledJoinMSG` field should be displayed or not.
> 
> The `msgReload` field is a string that represents the message displayed when the configuration is reloaded. It is displayed in the chat with specific color code formatting.
> 
> The `msgFullInv` field is a string that represents the message displayed when the player's inventory is full. It is displayed in the chat with specific color code formatting. The `doFullInvMSG` field is a boolean value that specifies whether the message defined in the `msgFullInv` field should be displayed or not.
> 
> The `titlebar` section contains fields related to the display of a title bar message when the player's inventory is full. The `doTitleBar` field is a boolean value that specifies whether the title bar message should be displayed or not. The `line1` and `line2` fields are strings that represent the two lines of the title bar message. They are displayed in the chat with specific color code formatting.
> 
> The `voidOnFullInv` field is a boolean value that specifies whether extra blocks broken by the player should be voided when the AutoPickup feature is enabled and the player's inventory is full.
> 
> The `usingSilkSpawnerPlugin` field is a boolean value that specifies whether a Silk Spawner plugin is in use, preventing players from receiving XP from breaking spawners. Defaulting to true, the plugin assumes you're using a silk spawner plugin and will let that plugin manage XP drops for spawners.
> 
> The `ignoreMobXPDrops` field is a boolean value that represents whether or not Auto Pickup should collect XP from mobs or allow it to drop naturally.
> 
> The `msgNoperms` field is a string that represents the message displayed when a player does not have permission to run a command. It is displayed in the chat with specific color code formatting.
> 
> The `requirePerms` section contains fields that specify whether players require permission to run certain commands. The `autopickup` field is a boolean value that specifies whether players require permission to run the `/autopickup` command. The `auto-reload` field is a boolean value that specifies whether players require permission to run the `/auto reload` command.

## Blacklists - `blacklist.yml`
> The `doBlacklisted` field is a boolean value that specifies whether the blacklist feature for blocks should be enabled. If set to `true`, the blocks listed in the `Blacklisted` field will be prevented from being picked up by the player.
> 
> The `doBlacklistedEntities` field is a boolean value that specifies whether the blacklist feature for entities should be enabled. If set to `true`, the entities listed in the `BlacklistedEntities` field will not drop items when killed by the player.
> 
> The `Blacklisted` field is a list of block names that should be prevented from being picked up by the player when the blacklist feature is enabled.
> 
> The `BlacklistedEntities` field is a list of entity names that should not drop items when killed by the player when the blacklist feature is enabled.
> 
> The `BlacklistedWorlds` field is a list of world names in which the blacklist features should be applied.

## Placeholder Configuration - `papi.yml`
> The `papi` section contains fields related to the PlaceholderAPI plugin. The `enabled` field is a subfield that has two subfields of its own, `true` and `false`. These subfields are strings that represent messages displayed when the AutoPickup plugin is enabled or disabled, respectively. They are displayed in the chat with specific color code formatting.
> 
> **Available PAPI Placeholders:**
> 
> ```kotlin
> %autopickup_autoenabled%
> %autopickup_dropsenabled%
> %autopickup_autosmeltenabled%
> ```
