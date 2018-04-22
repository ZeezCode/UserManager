package com.aidanmurphey.usermanager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Group {
    private FileConfiguration config;
    private String name;

    /**
     * Gets an instance of the Group class for a specific group
     * Returns null if no group exists with the given name
     * @param name Name of the group
     * @return Group Instance of the group
     */
    public static Group getGroup(String name) {
        FileConfiguration config = UserManager.getPlugin().getConfig();
        ConfigurationSection groupSection = config.getConfigurationSection("groups." + name);

        //If section is found, return new instance of the group. Otherwise, return null
        return groupSection != null ? new Group(name, config): null;
    }

    /**
     * Returns a list of all of the permissions of a group and its parents (using recursion)
     * @param group Instance of group
     * @return List<String> All of the permissions of a group and the permissions it gets through its parent
     */
    public static List<String> getPermissions(Group group) {
        List<String> permissions = UserManager.getPlugin().getConfig().getStringList("groups." + group + ".permissions");

        Group inherits = group.getInherits();
        if (inherits == null)
            return permissions; //return strictly own's permissions
        else {
            permissions.addAll(getPermissions(inherits)); //add parent's permissions to current permissions
            return permissions; //return collective permissions
        }
    }

    /**
     * Returns the default group for new users
     * @return Group The default group for new users
     */
    public static Group getDefaultGroup() {
        ConfigurationSection configurationSection =
                UserManager.getPlugin().getConfig().getConfigurationSection("groups");

        String foundName = configurationSection.getKeys(false).stream()
                .filter(groupName -> configurationSection.getBoolean(groupName + ".default")).findFirst().orElse(null);

        return Group.getGroup(foundName);
    }

    /**
     * Creates a new instance of the Group class
     * @param name
     */
    private Group(String name, FileConfiguration config) {
        this.name = name;
        this.config = config;
    }

    /**
     * Returns the name of the group
     * @return String Name of group
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether or not the group is the default group
     * @return boolean Whether or not the group is the default group
     */
    public boolean isDefault() {
        return config.getBoolean("groups." + name + ".default");
    }

    /**
     * Returns whether or not a group has access to a permission
     * @param permission Permission node to be queried
     * @return boolean Whether or not group has access to permission
     */
    public boolean hasPermission(String permission) {
        List<String> permissions = Group.getPermissions(this);

        return permissions.contains(permission);
    }

    /**
     * Returns the group this group inherits permissions from
     * @return Group The group this group inherits permissions from
     */
    public Group getInherits() {
        String groupName = config.getString("groups." + name + ".inherits");

        return groupName != null ? Group.getGroup(groupName) : null;
    }

    /**
     * Returns whether or not the group has permission to build
     * @return boolean Whether or not the group has permission to build
     */
    public boolean canBuild() {
        return config.getBoolean("groups" + name + ".info.canBuild");
    }

    /**
     * Returns the prefix of a group
     * @return String The prefix of the group
     */
    public String getPrefix() {
        return config.getString("groups." + name + ".info.prefix");
    }

    /**
     * Returns the suffix of a group
     * @return String The suffix of the group
     */
    public String getSuffix() {
        return config.getString("groups." + name + ".info.suffix");
    }
}
