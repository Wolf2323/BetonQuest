---
icon: material/weather-night
tags:
  - Schedules
  - Time
  - Gametime
  - Automation
---

Schedules can also run based on Minecraft's ingame time instead of real-world server time. This is useful when events
should sync with the Minecraft day/night cycle, like spawning mobs at midnight or opening a shop at sunrise.

In this tutorial, a village guard announces the start of night and day. The schedule uses `gametime-cron` to run at
fixed Minecraft times, independent of the real-world clock.

<div class="grid" markdown>
!!! danger "Requirements"
    It is helpful to be familiar with actions and schedules before using gametime schedules.

    * [Actions Tutorial](../../../Tutorials/Getting-Started/Basics/Actions.md)
    * [Schedules](../../../Documentation/Advanced/Schedules.md)

!!! example "Related Docs"
    * [Gametime Schedules](../../../Documentation/Advanced/Schedules.md#gametime-schedules)
    * [Action NotifyAll](../../../Documentation/Reference/Actions-List.md#notifyall)
</div>

## 1. Understanding gametime schedules

Unlike [realtime schedules](../../../Documentation/Advanced/Schedules.md#realtime-schedules) that use the server's
real-world clock, gametime schedules use Minecraft's ingame time from a specific world.

Key differences:

- **Realtime schedules** run at a fixed server time (e.g., 10pm on your computer's clock).
- **Gametime schedules** run at a fixed Minecraft time (e.g., midnight in the game world).

A full Minecraft day lasts 20 minutes of real time (24,000 ticks). The ingame time can be changed by players sleeping
in beds, by using the `/time set` command, or by plugins.

## 2. Creating a gametime schedule

We create a simple schedule that announces night and day in the village. The schedule uses `gametime-cron` type with a
cron expression that maps to Minecraft hours and minutes.

=== "package.yml"

    ``` yaml linenums="1"
    schedules:
      announceNight: #(1)!
        type: gametime-cron #(2)!
        time: '0 18 * * *' #(3)!
        world: world #(4)!
        actions: announceNight #(5)!
      announceDay:
        type: gametime-cron
        time: '0 6 * * *'
        world: world
        actions: announceDay
    ```

    1. The schedule name must be unique inside the package.
    2. `gametime-cron` runs based on Minecraft ingame time using cron syntax.
    3. The cron expression `0 18 * * *` means every day at 18:00 (6pm) Minecraft time – sunset.
    4. `world` specifies which world's ingame time to use. This is required for gametime schedules.
    5. The action to run when the time is reached.

=== "actions.yml"

    ``` yaml linenums="1"
    actions:
      announceNight: "notifyall &9Night has fallen. Be careful out there! io:chat" #(1)!
      announceDay: "notifyall &6The sun rises. A new day begins! io:chat"
    ```

    1. `notifyall` is player independent and can be used directly from a schedule.

The schedule checks the world's ingame time every 10 ticks and runs the actions when the time matches the cron
expression.

!!! warning

    The `world` field is required for gametime schedules. The world must exist when BetonQuest loads the schedule.

## 3. Common gametime cron expressions

The cron expression uses the standard 5-field format: `minute hour day month weekday`.
For gametime schedules, only the minute and hour fields are relevant – the other fields are ignored.

| Expression        | Description                          |
|:------------------|:-------------------------------------|
| `0 0 * * *`       | Every day at midnight (00:00)        |
| `0 6 * * * *`     | Every day at 6 AM (sunrise)          |
| `0 12 * * *`      | Every day at noon (12:00)            |
| `0 18 * * *`      | Every day at 6 PM (sunset)           |
| `30 7 * * *`      | Every day at 7:30 AM                |
| `*/15 * * * *`    | Every 15 Minecraft minutes           |

!!! tip

    **[Crontab Guru](https://crontab.guru/)** is a great tool for learning and testing cron expressions.
    For gametime schedules, only the minute and hour fields matter.

## 4. Handling time jumps with catchup

When the ingame time jumps (e.g., a player sleeps in a bed and skips to morning), the scheduler can detect missed
executions. With catchup strategies and skip options, you can control what happens in these cases.

=== "package.yml"

    ``` yaml linenums="1"
    schedules:
      midnightEvent:
        type: gametime-cron
        time: '0 0 * * *'
        world: world
        actions: spawnMobs
        catchup: one #(1)!
        catchup_skip: -bed,-command #(2)!
    ```

    1. `catchup: one` means if the schedule was missed, run it once after detection.
    2. `catchup_skip` lists which time jump causes to ignore: `-bed` (sleeping), `-command` (`/time set`), `-plugin`.

In this example, if a player sleeps and skips to morning, the midnight event will not be caught up.
If the server was shut down during midnight, the event will run once on startup.

!!! note

    The catchup skip options work together with the catchup strategy.
    If catchup is set to `none`, the skip options have no effect.

You have now created schedules that run based on Minecraft ingame time. Use this pattern when events should sync with
the game world's day/night cycle, like mob spawning, shop opening hours, or time-based NPC dialogues.
