# AutoPickup
 Pick up the items you mine

Support Discord:
For any issues or questions feel free to join our Discord for quick support!
https://discord.gg/ncHH4FP

Other Support Methods:
GitHub and Spigot DM's (@MrButtersDev) are another good place for support. I check them almost daily. For quick and general support Discord is the best option. Although for documenting bugs feel free to create an issue here on GitHub.

[QUOTE]The below documentation was generated using ChatGPT by OpenAI. For further clarification join our discord for support or view an up-to-date "default" of the config.yml file on GitHub.[/QUOTE]

Configuration - config.yml
[QUOTE][/QUOTE][QUOTE]
The [ICODE]msgPrefix[/ICODE] field is a string that represents the message prefix for messages related to the AutoPickup feature. It is displayed in the chat with a specific color code formatting.

The [ICODE]msgAutoPickupEnable[/ICODE] and [ICODE]msgAutoPickupDisable[/ICODE] fields are strings that represent the messages displayed when the player enables or disables the AutoPickup feature, respectively. They are displayed in the chat with a specific color code formatting.

The [ICODE]msgAutoSmeltEnable[/ICODE] and [ICODE]msgAutoSmeltDisable[/ICODE] fields are strings that represent the messages displayed when the player enables or disables the AutoSmelt feature, respectively. They are displayed in the chat with a specific color code formatting.

The [ICODE]msgAutoMobDropsEnable[/ICODE] and [ICODE]msgAutoMobDropsDisable[/ICODE] fields are strings that represent the messages displayed when the player enables or disables the AutoMobDrops feature, respectively. They are displayed in the chat with a specific color code formatting.

The [ICODE]msgAutoEnable[/ICODE] field is a string that represents the message displayed when the AutoPickup feature is automatically enabled. It is displayed in the chat with a specific color code formatting. The [ICODE]doAutoEnableMSG[/ICODE] field is a boolean value that specifies whether the message defined in the [ICODE]msgAutoEnable[/ICODE] field should be displayed or not.

The [ICODE]msgEnabledJoinMSG[/ICODE] field is a string that represents the message displayed when a player joins the game and the AutoPickup feature is automatically enabled. It is displayed in the chat with a specific color code formatting. The [ICODE]doEnabledOnJoinMSG[/ICODE] field is a boolean value that specifies whether the message defined in the [ICODE]msgEnabledJoinMSG[/ICODE] field should be displayed or not.

The [ICODE]msgReload[/ICODE] field is a string that represents the message displayed when the configuration is reloaded. It is displayed in the chat with a specific color code formatting.

The [ICODE]msgFullInv[/ICODE] field is a string that represents the message displayed when the player's inventory is full. It is displayed in the chat with a specific color code formatting. The [ICODE]doFullInvMSG[/ICODE] field is a boolean value that specifies whether the message defined in the [ICODE]msgFullInv[/ICODE] field should be displayed or not.

The [ICODE]titlebar[/ICODE] section contains fields related to the display of a title bar message when the player's inventory is full. The [ICODE]doTitleBar[/ICODE] field is a boolean value that specifies whether the title bar message should be displayed or not. The [ICODE]line1[/ICODE] and [ICODE]line2[/ICODE] fields are strings that represent the two lines of the title bar message. They are displayed in the chat with a specific color code formatting.

The [ICODE]voidOnFullInv[/ICODE] field is a boolean value that specifies whether extra blocks broken by the player should be voided when the AutoPickup feature is enabled and the player's inventory is full.

The [ICODE]usingSilkSpawnerPlugin[/ICODE] field is a boolean value that specifies whether a Silk Spawner plugin is in use preventing players from receiving XP from breaking spawners. Defaulting to true, the plugin assumes your using a silk spawner plugin and will let that plugin manage XP drops for spawners.

The [ICODE]ignoreMobXPDrops[/ICODE] field is a boolean value that represents whether or not Auto Pickup should collect XP from mobs or allow it to drop naturally.

The [ICODE]msgNoperms[/ICODE] field is a string that represents the message displayed when a player does not have permission to run a command. It is displayed in the chat with a specific color code formatting.

The [ICODE]requirePerms[/ICODE] section contains fields that specify whether players require permission to run certain commands. The [ICODE]autopickup[/ICODE] field is a boolean value that specifies whether players require permission to run the [ICODE]/autopickup[/ICODE] command. The [ICODE]auto-reload[/ICODE] field is a boolean value that specifies whether players require permission to run the [ICODE]/auto reload[/ICODE] command.
[/QUOTE]

Blacklist's - blacklist.yml
[QUOTE][/QUOTE][QUOTE]
The [ICODE]doBlacklisted[/ICODE] field is a boolean value that specifies whether the blacklist feature for blocks should be enabled. If set to [ICODE]true[/ICODE], the blocks listed in the [ICODE]Blacklisted[/ICODE] field will be prevented from being picked up by the player.

The [ICODE]doBlacklistedEntities[/ICODE] field is a boolean value that specifies whether the blacklist feature for entities should be enabled. If set to [ICODE]true[/ICODE], the entities listed in the [ICODE]BlacklistedEntities[/ICODE] field will not drop items when killed by the player.

The [ICODE]Blacklisted[/ICODE] field is a list of block names that should be prevented from being picked up by the player when the blacklist feature is enabled.

The [ICODE]BlacklistedEntities[/ICODE] field is a list of entity names that should not drop items when killed by the player when the blacklist feature is enabled.

The [ICODE]BlacklistedWorlds[/ICODE] field is a list of world names in which the blacklist features should be applied.
[/QUOTE]

Placeholder Configuration - papi.yml
[QUOTE][/QUOTE][QUOTE]
The [ICODE]papi[/ICODE] section contains fields related to the PlaceholderAPI plugin. The enabled field is a subfield that has two subfields of its own, [ICODE]true[/ICODE] and [ICODE]false[/ICODE]. These subfields are strings that represent messages displayed when the AutoPickup plugin is enabled or disabled, respectively. They are displayed in the chat with a specific color code formatting.

Available PAPI Placeholders:

[code=Kotlin][>] %autopickup_autoenabled%
[>] %autopickup_dropsenabled%
[>] %autopickup_autosmeltenabled%[/code]
[/QUOTE]
