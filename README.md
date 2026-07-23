# AnarchyRuleEngine

Configurable rule engine for Paper 1.16.5 servers. It lets server developers describe item-interaction rules in YAML using reusable conditions and actions instead of hard-coding every mechanic in a listener.

## Requirements

- Java 8 or newer
- Paper 1.16.5
- WorldGuard 7.0.5 or a compatible 7.x build

## Build

```bash
mvn clean package
```

The compiled plugin will be created at:

```text
target/AnarchyRuleEngine-1.0.0.jar
```

## Installation

1. Install Paper 1.16.5.
2. Install WorldEdit and WorldGuard 7.x.
3. Put `AnarchyRuleEngine-1.0.0.jar` into the server's `plugins` directory.
4. Start the server once to create `config.yml`.
5. Configure custom items and rules, then run `/are reload`.

## Commands

| Command | Description | Permission |
|---|---|---|
| `/are help` | Shows available commands | — |
| `/are version` | Shows plugin version and platform | — |
| `/are reload` | Atomically reloads items and rules | `anarchyruleengine.command.reload` |
| `/are give <player> <itemId> [amount]` | Gives a configured custom item | `anarchyruleengine.command.give` |
| `/are rules` | Lists loaded rules | `anarchyruleengine.command.inspect` |
| `/are inspect <ruleId>` | Describes a loaded rule | `anarchyruleengine.command.inspect` |
| `/are debug [player] <on\|off\|status>` | Controls rule execution tracing | `anarchyruleengine.command.debug` |

## Built-in conditions

- `always`
- `item`
- `material`
- `interaction-action`
- `permission`
- `world`
- `region`
- `y-min` / `y-max`
- `cooldown`
- `on-cooldown`

## Built-in actions

- `message`
- `cancel`
- `cooldown`
- `remove-item`

## Example rule

```yaml
rules:
  plast-use:
    enabled: true
    priority: 100
    stop-processing: true

    conditions:
      item: plast
      interaction-action:
        - RIGHT_CLICK_AIR
        - RIGHT_CLICK_BLOCK
      cooldown: plast

    actions:
      - type: cooldown
        seconds: 20

      - type: message
        message: "&aПласт использован!"

      - type: remove-item
        amount: 1

      - type: cancel
```

Rules are evaluated from highest to lowest priority. When a matched rule has `stop-processing: true`, lower-priority rules are not evaluated.

## Reload safety

`/are reload` validates the new item and rule configuration before replacing the active state. If validation fails, the previous working configuration remains active.

## Debugging

Enable tracing for a player:

```text
/are debug <player> on
```

The player will see which rules were checked, which conditions passed or failed, which actions executed, and where an execution error occurred.

## Version

Current stable release: **1.0.0**.
