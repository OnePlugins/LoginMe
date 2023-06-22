package bxn4.oneplugins.loginme;

import org.bukkit.Bukkit;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class Functions {
    private final Path path = Paths.get("").toAbsolutePath();
    Map<String, Object> Config = new LinkedHashMap<>();
    Map<String, Object> JoinDisconnect = new LinkedHashMap<>();
    DumperOptions dumper = new DumperOptions();
    private final File OnePlugins = new File(path + "/plugins/OnePlugins");
    private final File LoginMe = new File(path + "/plugins/OnePlugins/LoginMe");
    private final File ConfigFile = new File(path + "/plugins/OnePlugins/LoginMe/config.yaml");
    private final File WelcomeFile = new File(path + "/plugins/OnePlugins/LoginMe/welcome.txt");
    private final File Database = new File(path + "/plugins/OnePlugins/LoginMe/playerDatabase.db");
    private final File JoinDisconnectFile = new File(path +  "/plugins/OnePlugins/LoginMe/joinDisconnect.yaml");
    public void CheckDir() throws IOException {
        if(!OnePlugins.exists()) {
            OnePlugins.mkdir();
        }
        if(!LoginMe.exists()) {
            SendMessage();
            LoginMe.mkdir();
        }
        if(!ConfigFile.exists()) {
            Config.put("lang", "en");
            Config.put("logout-time", 120);
            Config.put("min-pass-length", 6);
            Config.put("dont-allow-common-passwords", true);
            Config.put("enable-welcome-message", true);
            dumper.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(dumper);
            FileWriter writer = new FileWriter(ConfigFile);
            yaml.dump(Config, writer);
            writer.close();
        }
        if(!WelcomeFile.exists()) {
            FileWriter writer = new FileWriter(WelcomeFile);
            writer.write("Welcome §6[PLAYER]§r on §2{CHANGEME}§r server!");
            writer.close();
        }
        if(!Database.exists()) {
            CreateDatabase();
        }
        if(!JoinDisconnectFile.exists()) {
            JoinDisconnect.put("join", "§8[§2+§8]§7 [PLAYER]");
            JoinDisconnect.put("disconnect", "§8[§4-§8]§7 [PLAYER]");
            dumper.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(dumper);
            FileWriter writer = new FileWriter(JoinDisconnectFile);
            yaml.dump(JoinDisconnect, writer);
            writer.close();
        }
    }

    public void CreateDatabase() {
        String url = "jdbc:sqlite:" + path + "/plugins/OnePlugins/LoginMe/playerDatabase.db";
        String sqlCommand =
                "CREATE TABLE IF NOT EXISTS `PLAYERDATA` (\n" +
                "`uname` TEXT NOT NULL,\n" +
                "`passwd` TEXT NOT NULL,\n" +
                "`emailad` TEXT,\n" +
                "`backupcode` TEXT,\n" +
                "`emailconfirmationcode` TEXT,\n" +
                "`passwordresetcode` TEXT\n" +
                ");";
        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                Statement stmt = conn.createStatement();
                stmt.execute(sqlCommand);
                stmt.close();
                conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
        public void SendMessage() {
        Bukkit.getConsoleSender().sendMessage(
                "§a*======================================*§r\n" +
                   "§e  --- Thanks for using OnePlugins! ---§r\n" +
                   "  >> Plugin name: §dLoginMe§r\n" +
                   "              >>  Version: 1.1§r\n" +
                   "  §oBXn4§r\n" +
                   "§a*======================================*§r");
    }
}
