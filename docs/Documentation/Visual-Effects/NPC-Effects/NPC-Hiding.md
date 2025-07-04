---
icon: fontawesome/solid/person-through-window
---

# NPC Hiding

## Usage
Hide NPCs if specified conditions are met!
You can do that by adding a `hide_npcs` section in your package. 
It allows you to assign conditions to specific BQ NpcIDs like so:

=== "Citizens"
    ```YAML title="Example"
    npcs:
      innkeeper: citizens 0
    conditions:
      hidden: tag innkeeperIsTired
    hide_npcs:
      innkeeper: hidden
    ```
    @snippet:integrations:protocollib@
=== "MythicMobs"
    ```YAML title="Example"
    npcs:
      innkeeper: mythicmobs UUID 60b0144d-2c55-457a-aeb8-15fbf244f3b7
    conditions:
      hidden: tag innkeeperIsTired
    hide_npcs:
      innkeeper: hidden
    ```
    @snippet:integrations:protocollib@
=== "FancyNpcs"
    ```YAML title="Example"
    npcs:
      innkeeper: FancyNpcs dc8f2889-ed79-455e-944b-115dae978737
    conditions:
      hidden: tag innkeeperIsTired
    hide_npcs:
      innkeeper: hidden
    ```
=== "ZNPCsPlus"
    ```YAML title="Example"
    npcs:
      innkeeper: ZNPCsPlus innkeeper
    conditions:
      hidden: tag innkeeperIsTired
    hide_npcs:
      innkeeper: hidden
    ```
    
Where NpcID is declared when you register the NPC which is described [here](../../Features/NPCs.md#provided-integrations).

The interval the conditions are checked in can be configured with the [`npc_update_interval`](../../Configuration/Plugin-Config.md#npc-npc-settings) setting.

### Force Visibility Update
You can run the `updatevisibility` event to manually update the visibility. This is useful for performance optimizations
if used with the [npc hider interval](../../Configuration/Plugin-Config.md#npc-npc-settings) set to high values.
