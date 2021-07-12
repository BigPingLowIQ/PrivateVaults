# PrivateVaults

## About me

Hello, I go by the name BigPingLowIQ. I am new to the programming scene and in a week of researching and learning how to make spigot plugins I came up with 
this simple but usefull plugin. 


## Description

This is an open source minecraft spigot plugin for creating private vaults. Each vault has a custom command name, title, size and permission. 
Simple to use and add new vaults using a couple commands.

## Usage

| Command | Usage |
| --- | --- |
| `/open [<vault_name>]` | `Open a vault of the specified <vault_name>` |
| `/open [<vault_name>] [<player_name>]` | `Open a players vault. Player must be online.` |
| `/privatevaultsadd [<vault_name>] [<title>] [<size>]` | `Adds a new vault. Use underscores "_" in the title to add spaces between words.` |
| `/privatevaultsremove [<vault_name>]` | `Removes a vault if it exisits.` |
| `/privatevaultsreset [<vault_name>]` | `Resets all inventory data of an exisiting vault. Can't be undone.` |
### Additional usage info
##### - `<vault_name> can't have special characters.`
##### - `<size> must be any of the following numbers: 9, 18, 27, 36, 45, 54`

## Permissions

| Permission | Usage |
| --- | --- |
| `privatevaults.*` | `Gives permission to all commands.` |
| `privatevaults.open` | `Gives permission to use /open command.` |
| `privatevaults.open.invsee` | `Gives permission to open other players vaults. Player must be online. Note. Anyone with this permission can open any of his own vaults.`|
| `privatevaults.vault.* ` | `Gives permmission to open any of the exisiting vaults.` |
| `privatevaults.vault.<vault_name>` | `Gives permission to open a specified vault.` |
| `privatevaults.admin.add` | `Gives permission to /privatevaultsadd command.` |
| `privatevaults.admin.remove` | `Gives permission to /privatevaultsremove command.` |
| `privatevaults.admin.reset` | `Gives permission to /privatevaultsreset command.` |

## Future ideas/updates

- Opening offline player vaults.
- Deleting individual player vaults.
- Automatic update check. (Added)

