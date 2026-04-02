# Server Events

[![GitHub License](https://img.shields.io/github/license/sucj/serverevents)](https://github.com/sucj/serverevents?tab=MIT-1-ov-file#readme)
[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/sucj/serverevents/build.yml)](https://github.com/sucj/serverevents/actions/workflows/build.yml)
[![GitHub Release](https://img.shields.io/github/v/release/sucj/serverevents)](https://github.com/sucj/serverevents/releases/latest)

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/serverevents?logo=modrinth)](https://modrinth.com/mod/serverevents)

**Server Events** is a support library for Fabric server development, designed to enhance the Fabric API's limited event system. It offers a Bukkit-like event framework while adhering to Fabric's minimalist philosophy.

The mod doesn't wrap `CommandRegistrationCallback` and `DynamicRegistrySetupCallback` from Fabric API.

## Installation
1. Import this package to your project.
2. Add `serverevents` to your mod depends.

```groovy
repositories {
    maven "https://mvn.suc.icu"
}

dependencies {
    implementation "icu.suc:serverevents:<version>"
}
```

## Usage
**ServerEvents** provides a simple API for registering and processing events.

Here is an example of a player modifying broadcast information and giving an apple when joining:

```java
import net.fabricmc.api.ModInitializer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import icu.suc.serverevents.ServerEvents;

public class ExampleMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerEvents.Player.MODIFY_JOIN_MESSAGE.register((player, message) -> {
            player.getInventory().add(Items.APPLE.getDefaultInstance());
            return Component.literal("[+] ").append(player.getName());
        });
    }
}
``` 

## License

This project is licensed under the [MIT License](LICENSE) © 2025 sucj.