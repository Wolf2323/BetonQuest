---
icon: material/puzzle-edit
---
@snippet:api-state:stable@

We implemented multiple different `org.bukkit.configuration.ConfigurationSection`'s and `Configuration`'s to make it 
easier to work with configurations and to implement some new features.

We have the following implementations:

## Decorator
Basically a wrapper for a `ConfigurationSection` simply delegating all calls to the wrapped section.
In that way, you can override specific methods without having to override all methods for custom implementations.
Mainly used for internal purposes, but can be used to create even more custom implementations.

## Handle
Based on `Decorator`. Handles any modifications to the configuration and wraps all values that are an instance
of `ConfigurationSection` with the same implementation. This should prevent any modifications to the configuration
that are not done through the handler. This is the implementation actually used to create more custom implementations.

## Unmodifiable
Based on `Handle`. Makes the configuration unmodifiable. This means no values can be added, removed or modified.
Also, no new Sections can be created.

## Fallback
This is a configuration build up from two configurations. One is the original where all modifications are done,
and the other one is the fallback configuration. The fallback configuration is used to look up values that are not
contained in the original configuration. This is useful for default values or global configurations.

## Multi
Based on `Handle`. Allows multiple configurations to be used as one.

