<h1>
LoginMe
</h1>
<center><img src="https://github.com/OnePlugins/LoginMe/assets/78733248/59edfa69-edca-4193-954d-61cb7c56e450" width="300px"><h4>A simple login plugin</h4></center>
<p align="center">
  --> <a href="#description">Description</a> ·
  <a href="#features">Features</a> ·
  <a href="#commands">Commands</a> ·
  <a href="#configuration">Configuration</a> ·
  <a href="#download">Download</a> ·
  <a href="#support-me">Support me</a> <--
</p>

## Description
A simple login security plugin to make your server safe.
With this plugin players can protect their accounts with password.

### Features:
 - Allows users to rejoin the server without password for X seconds.
 - Custom join / welcome message.
 - Easy configuration
 - Multiple languages support.

## Commands
| Command | Description |
| --- | --- |
| `/login` | Login into the server with password. |
| `/register, /reg` | Register into the server |
| `/logout` | Logout from the server |
| `/password` | Generate a random password |
| `/registerplayer` | Registering a player by an admin. The allowPlayerLogin parameter allows player to login. If it set to false, then the player can login by an admin. |
| `/loginplayer` | Allows to login the player by an admin. |
| `/loginbackup` | Simple code, to reset your password. |
  
## Usages
| Command | How To Use |
| --- | --- |
| `/login` | /login [password] |
| `/register, /reg` | /register [password] [password] |
| `/logout` | /logout |
| `/password` | /password <length> |
| `/registerplayer` | /registerplayer <player> [password] allowPlayerLogin:<true/false> unRegister:[y:<year> m:<month> d:<day> h:<hour> mn:<minute>] |
| `/loginplayer` | /loginplayer <player> [password] |
| `/loginbackup` | /loginbackup help |
  
## Configuration
```yaml
lang:
```
Change plugin language
```yaml
logout-time:
```
 When the player disconnects from the server, the player can rejoin X seconds without a password.
```yaml
min-pass-length:
```
The minimum password length.
```yaml
dont-allow-common-passwords:
```
This should always set to "true"! Protecting users to don't enter common / weak passwords from this list: 
https://github.com/danielmiessler/SecLists/blob/master/Passwords/500-worst-passwords.txt
```yaml
enable-welcome-message:
```
Simple welcome message, when the player join.

## Download
|           LoginMe           |
|-----------------------------|
| Version: 1.0 SPIGOT                |
| MC version: 1.19.4          |
| Supported languages:  |
| en, hu, lt |

<p>Download the latest version: <a href="https://github.com/OnePlugins/LoginMe/releases/">here</a></p>

## Support me!
<a href="https://www.buymeacoffee.com/bence912" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/purple_img.png" alt="Buy Me A Coffee">
