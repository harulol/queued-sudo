<div align="center">

# QueuedSudo
Basically Essentials' `/sudo` but they can be queued.

![GitHub](https://img.shields.io/github/license/harulol/queued-sudo?color=red&style=for-the-badge) ![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/harulol/queued-sudo?color=yellow&include_prereleases&style=for-the-badge)
</div>

## Features
- Delaying `sudo`s until a player joins, with 3 types of executables. An executable is invoked once the player is teleported to a world, that belongs to a group, that has a queued executable for that player.
- `AwaitExecutable`s. A type of executable that waits some time before invocation.
- `DefaultExecutable`s. The default type of executable.
- `RepeatingExecutable`s. These are invoked a certain number of times with intervals between.
- CLI management.
- Customizable messages.
- GUIs to configure.

## Commands
The main and base command is `queuedsudo` with aliases. Commands documentation is available in-game.

Quick explanation of arguments and options: Arguments are flags that require a following value, options are just flags.
```
If "-s" is an argument:
-s "this value is necessary"

If "-s" is an option:
-s "this value is disregarded"
```
<details>
<summary>Subcommands</summary>

- `addworld`: Adds a world or multiple ones to an existing group.
- `create`: Creates a new world group, with a name and attached worlds.
- `delete`: Deletes an existing world group.
- `list`: Lists all registered groups.
- `reload`: Reloads the messages file.
- `removeworld`: Removes a world or multiple ones from an existing group.
- `rename`: Renames an existing group.
- `run`: Queues or runs an executable.
- `search`: Searches for a group.
</details>

## Permissions

| Permission | Default | Description |
|---|:---:|---|
| queued-sudo.rename | OP | Allows access to the "rename" subcommand. |
| queued-sudo.add-world | OP | Allows access to the "add-world" subcommand. |
| queued-sudo.remove-world | OP | Allows access to the "remove-world" subcommand. |
| queued-sudo.delete | OP | Allows access to the "delete" subcommand. |
| queued-sudo.list | OP | Allows access to the "list" subcommand. |
| queued-sudo.create | OP | Allows access to the "create" subcommand. |
| queued-sudo.main | OP | Allows access to the main command. |
| queued-sudo.exempt | OP | Prevents the holder from being queued sudo'd. |
| queued-sudo.reload | OP | Allows access to the "reload" subcommand. |
| queued-sudo.search | OP | Allows access to the "search" subcommand. |
| queued-sudo.* | None | Allows access to all other nodes in this plugin. |