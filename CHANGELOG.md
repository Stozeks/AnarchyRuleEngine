# Changelog

## 1.0.0 — 2026-07-23

First stable release.

### Added

- Configurable custom-item registry.
- Priority-based rule engine with `stop-processing` support.
- Extensible action and condition registries.
- Built-in item, material, interaction, permission, world, region, Y-level and cooldown conditions.
- Built-in message, cancel, cooldown and item-removal actions.
- Placeholder resolution for player, world, position and cooldown values.
- Atomic `/are reload` for both items and rules.
- Strict YAML validation and useful loading errors.
- `/are give`, `/are rules`, `/are inspect`, `/are debug` and `/are version`.
- Per-player execution tracing with condition/action details.
- Tab completion for commands, players, items and rule IDs.
- Runtime isolation for failing conditions and actions.

### Fixed

- Reload now applies item changes instead of only replacing rules.
- Failed reloads preserve the previous working state.
- Region checks use the interaction location when a block is clicked.
- Invalid item materials fail with an explicit configuration error.
- Execution results retain every matched rule and runtime error details.
