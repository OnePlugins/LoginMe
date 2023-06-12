package bxn4.oneplugins.loginme;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class LoginMe extends JavaPlugin implements CommandExecutor, Listener {
    //region variables
    String registerText = "";
    String passwordGenerated = "";
    String passwordGenerateUsage = "";
    String backupCodeCreated = "";
    String backupCodeWarning = "";
    String backupCodeUse = "";
    String backupCodeWrong = "";
    String passwordUpdated = "";
    String codeDeleted = "";
    String registeredText = "";
    String addEmail = "";
    String updateEmail = "";
    String registerExpired = "";
    String passwordsDoesNotMatch = "";
    String weakPassword = "";
    String shortPassword = "";
    String loginText = "";
    String loggedIn = "";
    String loginExpired = "";
    String wrongPassword = "";
    String reconnect = "";
    String logout = "";
    String cantLogin = "";
    String minPassLengthError = "";
    String cantUseThisCommand = "";
    String loginToUseChat = "";
    String lang = "";
    int logoutTime = 0;
    int minPassLength = 0;
    boolean dontAllowCommonPasswords = true;
    boolean enableWelcomeMessage = true;
    String welcomeMessage = "";
    String loggedInTitle = "";
    String loggedInSubTitle = "";
    String joinMessage = "";
    String disconnectMessage = "";
    private final Path path = Paths.get("").toAbsolutePath();
    private HashMap<String, String> signedInPlayers = new HashMap<>();
    private HashMap<String, Integer> backupCodeCommand = new HashMap<>();
    private List<String> canBackupPlayers = new ArrayList<>();
    PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255);
    PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 255);
    PotionEffect JUMP = new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 250);
    PotionEffect SLOW_DIGGING = new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 255);
    PotionEffect DAMAGE_RESISTANCE = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 1);
    private ArrayList<String> weakPasswords = new ArrayList<String>();
    File loginMe = new File(path + "/plugins/OnePlugins/LoginMe");
    File config = new File(path + "/plugins/OnePlugins/LoginMe/config.yaml");
    File welcome = new File(path + "/plugins/OnePlugins/LoginMe/welcome.txt");
    File database = new File(path + "/plugins/OnePlugins/LoginMe/playerDatabase.db");
    File joinDisconnect = new File(path + "/plugins/OnePlugins/LoginMe/joinDisconnect.yaml");
    private final String databaseUrl = "jdbc:sqlite:" + path + "/plugins/OnePlugins/LoginMe/playerDatabase.db";
    Connection conn = null;
    private final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!?@#$%&*()_-";
    private final String charactersBackup = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    //endregion

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        if (!loginMe.exists() || !config.exists() || !welcome.exists() || !database.exists() || !joinDisconnect.exists()) {
            Functions functions = new Functions();
            try {
                functions.CheckDir();
            } catch (IOException e) {
            }
        }
        if(welcome.exists()) {
            StringBuilder builder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(welcome), UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                    builder.append(System.lineSeparator());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            welcomeMessage = builder.toString();
        }
        if (joinDisconnect.exists()) {
            Yaml yaml = new Yaml();
            try {
                FileReader reader = new FileReader(path + "/plugins/OnePlugins/LoginMe/joinDisconnect.yaml");
                Map<String, Object> data = yaml.load(reader);
                reader.close();
                joinMessage = data.get("join").toString();
                disconnectMessage = data.get("disconnect").toString();
            } catch (IOException e) {
            }
        }
        if (config.exists()) {
            Yaml yaml = new Yaml();
            try {
                FileReader reader = new FileReader(path + "/plugins/OnePlugins/LoginMe/config.yaml");
                Map<String, Object> data = yaml.load(reader);
                reader.close();
                lang = data.get("lang").toString();
                logoutTime = Integer.parseInt(data.get("logout-time").toString()) * 20;
                minPassLength = Integer.parseInt(data.get("min-pass-length").toString());
                dontAllowCommonPasswords = Boolean.parseBoolean(data.get("dont-allow-common-passwords").toString());
                enableWelcomeMessage = Boolean.parseBoolean(data.get("enable-welcome-message").toString());
                if (dontAllowCommonPasswords) {
                    weakPasswords = new ArrayList<String>(Arrays.asList(
                            "123456", "password", "12345678", "1234", "pussy", "12345", "dragon", "qwerty", "696969", "mustang", "letmein",
                            "baseball", "master", "michael", "football", "shadow", "monkey", "abc123", "pass", "fuckme", "6969", "jordan", "harley", "ranger",
                            "iwantu", "jennifer", "hunter", "fuck", "2000", "test", "batman", "trustno1", "thomas", "tigger", "robert", "access", "love", "buster",
                            "1234567", "soccer", "hockey", "killer", "george", "sexy", "andrew", "charlie", "superman", "asshole", "fuckyou", "dallas", "jessica",
                            "panties", "pepper", "1111", "austin", "william", "daniel", "golfer", "summer", "heather", "hammer", "yankees", "joshua", "maggie",
                            "biteme", "enter", "ashley", "thunder", "cowboy", "silver", "richard", "fucker", "orange", "merlin", "michelle", "corvette", "bigdog",
                            "cheese", "matthew", "121212", "patrick", "martin", "freedom", "ginger", "blowjob", "nicole", "sparky", "yellow", "camaro", "secret",
                            "dick", "falcon", "taylor", "111111", "131313", "123123", "bitch", "hello", "scooter", "please", "porsche", "guitar", "chelsea", "black",
                            "diamond", "nascar", "jackson", "cameron", "654321", "computer", "amanda", "wizard", "xxxxxxxx", "money", "phoenix", "mickey", "bailey",
                            "knight", "iceman", "tigers", "purple", "andrea", "horny", "dakota", "aaaaaa", "player", "sunshine", "morgan", "starwars", "boomer",
                            "cowboys", "edward", "charles", "girls", "booboo", "coffee", "xxxxxx", "bulldog", "ncc1701", "rabbit", "peanut", "john", "johnny",
                            "gandalf", "spanky", "winter", "brandy", "compaq", "carlos", "tennis", "james", "mike", "brandon", "fender", "anthony", "blowme",
                            "ferrari", "cookie", "chicken", "maverick", "chicago", "joseph", "diablo", "sexsex", "hardcore", "666666", "willie", "welcome",
                            "chris", "panther", "yamaha", "justin", "banana", "driver", "marine", "angels", "fishing", "david", "maddog", "hooters", "wilson",
                            "butthead", "dennis", "fucking", "captain", "bigdick", "chester", "smokey", "xavier", "steven", "viking", "snoopy", "blue", "eagles",
                            "winner", "samantha", "house", "miller", "flower", "jack", "firebird", "butter", "united", "turtle", "steelers", "tiffany", "zxcvbn",
                            "tomcat", "golf", "bond007", "bear", "tiger", "doctor", "gateway", "gators", "angel", "junior", "thx1138", "porno", "badboy", "debbie",
                            "spider", "melissa", "booger", "1212", "flyers", "fish", "porn", "matrix", "teens", "scooby", "jason", "walter", "cumshot", "boston",
                            "braves", "yankee", "lover", "barney", "victor", "tucker", "princess", "mercedes", "5150", "doggie", "zzzzzz", "gunner", "horney",
                            "bubba", "2112", "fred", "johnson", "xxxxx", "tits", "member", "boobs", "donald", "bigdaddy", "bronco", "penis", "voyager", "rangers",
                            "birdie", "trouble", "white", "topgun", "bigtits", "bitches", "green", "super", "qazwsx", "magic", "lakers", "rachel", "slayer", "scott",
                            "2222", "asdf", "video", "london", "7777", "marlboro", "srinivas", "internet", "action", "carter", "jasper", "monster", "teresa", "jeremy",
                            "11111111", "bill", "crystal", "peter", "pussies", "cock", "beer", "rocket", "theman", "oliver", "prince", "beach", "amateur", "7777777",
                            "muffin", "redsox", "star", "testing", "shannon", "murphy", "frank", "hannah", "dave", "eagle1", "11111", "mother", "nathan", "raiders",
                            "steve", "forever", "angela", "viper", "ou812", "jake", "lovers", "suckit", "gregory", "buddy", "whatever", "young", "nicholas", "lucky",
                            "helpme", "jackie", "monica", "midnight", "college", "baby", "cunt", "brian", "mark", "startrek", "sierra", "leather", "232323", "4444",
                            "beavis", "bigcock", "happy", "sophie", "ladies", "naughty", "giants", "booty", "blonde", "fucked", "golden", "0", "fire", "sandra", "pookie",
                            "packers", "einstein", "dolphins", "chevy", "winston", "warrior", "sammy", "slut", "8675309", "zxcvbnm", "nipples", "power", "victoria",
                            "asdfgh", "vagina", "toyota", "travis", "hotdog", "paris", "rock", "xxxx", "extreme", "redskins", "erotic", "dirty", "ford", "freddy",
                            "arsenal", "access14", "wolf", "nipple", "iloveyou", "alex", "florida", "eric", "legend", "movie", "success", "rosebud", "jaguar", "great",
                            "cool", "cooper", "1313", "scorpio", "mountain", "madison", "987654", "brazil", "lauren", "japan", "naked", "squirt", "stars", "apple", "alexis",
                            "aaaa", "bonnie", "peaches", "jasmine", "kevin", "matt", "qwertyui", "danielle", "beaver", "4321", "4128", "runner", "swimming", "dolphin",
                            "gordon", "casper", "stupid", "shit", "saturn", "gemini", "apples", "august", "3333", "canada", "blazer", "cumming", "hunting", "kitty",
                            "rainbow", "112233", "arthur", "cream", "calvin", "shaved", "surfer", "samson", "kelly", "paul", "mine", "king", "racing", "5555", "eagle",
                            "hentai", "newyork", "little", "redwings", "smith", "sticky", "cocacola", "animal", "broncos", "private", "skippy", "marvin", "blondes",
                            "enjoy", "girl", "apollo", "parker", "qwert", "time", "sydney", "women", "voodoo", "magnum", "juice", "abgrtyu", "777777", "dreams",
                            "maxwell", "music", "rush2112", "russia", "scorpion", "rebecca", "tester", "mistress", "phantom", "billy", "6666", "albert", "minecraft"
                    ));     // Source: https://github.com/danielmiessler/SecLists/blob/master/Passwords/500-worst-passwords.txt
                }
                if (minPassLength < 6) {
                    Bukkit.getConsoleSender().sendMessage(minPassLengthError);
                    minPassLength = 6;
                }
                switch (lang) {
                    case "en":
                        registerText = "§7Please register with the §l§a/register <password> <password> §r§7command!§r";
                        passwordGenerated = "§8[§2>>§8] §7The generated password is: §2[PASSWORD]\n§cDon't forget to save it!§r";
                        passwordGenerateUsage = "§7Usage: §l§a/password <length>§r§7 (max 32)";
                        backupCodeCreated = "§8[§2>>§8] §7Your backup code is: §2[BACKUP]\n§cDon't forget to save it! §7Type: §l§a/help loginbackup §r§7to see the avaliable commands.§r";
                        backupCodeWarning = "§8[§4!!§8] §7Your code §cwill be visible! §7Please type the command again to show your code.";
                        backupCodeUse = "§8[§2>>§8] §7Code used! Type: §l§a/loginbackup setpass <password> §r§7to set your password.";
                        backupCodeWrong = "Wrong code!";
                        passwordUpdated = "Password updated! Now you can login with the new password.";
                        codeDeleted = " Please create a new code because the previous one has been deleted.";
                        registeredText = "§8[§2>>§8] §7Successful registration!";
                        addEmail = "§7Are you scared about forgetting your password? Use §l§a/loginbackup email <email> §r§7anytime to bind an email address to your account, or use §l§a/loginbackup create §r§7anytime to create a backup code.";
                        updateEmail = "§7You already have connected an email address. Use §l§a/loginbackup newmail <email> §r§7anytime to bind an email address to your account, or use §l§a/loginbackup create §r§7anytime to create a backup code.";
                        registerExpired = "Your register time has expired!";
                        passwordsDoesNotMatch = "§8[§2>>§8] §7The passwords doesn't match.";
                        weakPassword = "§8[§2>>§8] §7The password what you entered is not secure. Please use another password for your safety.";
                        shortPassword = "§8[§2>>§8] §7The password is too short. Minimum length is: " + minPassLength;
                        loginText = "§7Please login with the §l§a/login <password> §r§7command!§r";
                        loggedIn = "§8[§2>>§8] §7Successful login!";
                        loggedInTitle = "§a Hello ";
                        loggedInSubTitle = "§7Enjoy your stay!";
                        loginExpired = "Your login time has expired!";
                        wrongPassword = "§8[§2>>§8] §7Bad password, try again!";
                        reconnect = "§8[§2>>§8] §7Successfully reconnected! If you want to sign out instantly, please use §l§a/logout §r§7command!§r";
                        logout = "See-ya!";
                        cantLogin = "Can't join to the server, because you connected from another place. Please wait few minutes, before you try again.";
                        minPassLengthError = "§7[§5LoginMe§7] §4!! Bad config file !!§r\n§7[§5LoginMe§7] §cThe minimum password length should greater than 6!§r";
                        cantUseThisCommand = "§8[§2>>§8] §7Please login to use this command.";
                        loginToUseChat = "§8[§4>>§8] §cPlease login to use chat!§r";
                        break;
                    case "hu":
                        registerText = "§7Kérlek regisztrálj a §l§a/register <jelszó> <jelszó> §r§7paranccsal!§r";
                        passwordGenerated = "§8[§2>>§8] §7A generált jelszó: §2[PASSWORD]\n§cNe felejtsd el lementeni!§r";
                        passwordGenerateUsage = "§7Használat: §l§a/password <hosszúság>§r§7 (max 32)";
                        backupCodeCreated = "§8[§2>>§8] §7A visszaállító kódod: §2[BACKUP]\n§cNe felejtsd el lementeni! §7Az elérhető parancsokért használd a: §l§a/help loginbackup §r§7parancsot.;§r";
                        backupCodeWarning = "§8[§4!!§8] §7A kód §clátható lesz! §7Kérlek írd be újra a parancsot, hogy látható legyen kódod.";
                        backupCodeUse = "§8[§2>>§8] §7Kód felhasználva! Az új jelszó beállításához használd a: §l§a/loginbackup setpass <jelszó> §r§7parancsot.";
                        backupCodeWrong = "Helytelen kód!";
                        passwordUpdated = "A jelszó frissült! Mostmár beléphetsz az új jelszóval.";
                        codeDeleted = " Kérlek hozz létre új kódot, mert az előző törlése került.";
                        registeredText = "§8[§2>>§8] §7Sikeres regisztráció!";
                        addEmail = "§7Aggódsz, hogy elfelejted a jelszavad? Használd bármikor az §l§a/loginbackup email <email> §r§7parancsot, hogy email címet csatolj a fiókodhoz, vagy használd a §l§a/loginbackup create §r§7parancsot, hogy visszaállító kódot hozz létre.";
                        registerExpired = "Lejárt a regisztrációra alkalmas időd!";
                        passwordsDoesNotMatch = "§8[§2>>§8] §7A jelszavak nem egyeznek.";
                        weakPassword = "§8[§2>>§8] §7Ez a jelszó nem biztonságos. Kérlek használj másik jelszót.";
                        shortPassword = "§8[§2>>§8] §7A jelszavad túl rövid. A minimális hosszúság: " + minPassLength;
                        loginText = "§7Kérlek jelentkezz be a §l§a/login <jelszó> §r§7paranccsal!§r";
                        loggedIn = "§8[§2>>§8] §7Sikeres bejelentkezés!";
                        loggedInTitle = "§a Üdv ";
                        loggedInSubTitle = "§7Érezd jól magad!";
                        loginExpired = "Lejárt a belépésre alkalmas időd!";
                        wrongPassword = "§8[§2>>§8] §7Helytelen jelszó, próbáld újra!";
                        reconnect = "§8[§2>>§8] §7Sikeresen újracsatlakoztál! Ha azonnal kiszeretnél jelentkezni, akkor használd a §l§a/logout §r§7parancsot.§r";
                        logout = "Várunk vissza!";
                        cantLogin = "Nem sikerült csatlakozni a szerverre, mert más helyről csatlakoztál. Kérlek várj pár percet mielőtt újra megpróbálsz csatlakozni.";
                        minPassLengthError = "§7[§5LoginMe§7] §4!! Helytelen konfiguracios fajl !!§r\n§7[§5LoginMe§7] §cA minimalis jelszo hosszusag nem lehet kevesbb, mint 6 karakter!§r";
                        cantUseThisCommand = "§8[§2>>§8] §7Kérlek jelentkezz be, hogy használd a parancsot.";
                        loginToUseChat = "§8[§4>>§8] §cJelentkezz be, hogy használd a chatet!§r";
                        break;
                    case "lt":
                        // Lithuanian translate by: Mafris
                        registerText = "§7Prađome uţsiregistruoti su §l§a/register<slaptaţodis> <slaptaţodis> §r§7komandŕ!§r";
                        passwordGenerated = "§7Sugeneruotas slaptažodis yra: §2[PASSWORD]\n§cNepamirškite jo išsaugoti!§r";
                        registeredText = "§8[§2>>§8] §7Registracija sëkminga!";
                        addEmail = "§7Ar jűs bijote pamirđti savo slaptaţodá? Naudokite §l§a/addemail <email'as> §r§7betkada, kad susietumete savo email'o adresa á jűsř paskyra."; // NEED BACKUP TRANSLATE
                        // NEED BACKUPCREATED TRANSLATE
                        // CONFIRM BACKUP TRANSLATE
                        registerExpired = "Tavo registracijos laikas pasibaigë!";
                        passwordsDoesNotMatch = "§8[§2>>§8] §7Slaptaţodţiai nesutampa.";
                        weakPassword = "§8[§2>>§8] §7Slaptaţodis kurá jűs ávedëte nëra saugus. Prađome naudoti kitŕ slaptaţodá jűsř saugumui.";
                        shortPassword = "§8[§2>>§8] §7Slaptaţodis yra per trumpas. Minimalus ilgis yra: " + minPassLength;
                        loginText = "§7Prađome prisijungti su §l§a/login <slaptaţodţis> §r§7komandŕ!§r";
                        loggedIn = "§8[§2>>§8] §7Sëkmingai prisijungëte!";
                        loggedInTitle = "§a Sveiki ";
                        loggedInSubTitle = "§7Gerai praleiskit laikŕ!";
                        loginExpired = "Jűsř prisijungimo laikas pasibaigë!";
                        wrongPassword = "§8[§2>>§8] §7Neteisingas slaptazodis, bandykite iđ naujo!";
                        reconnect = "§8[§2>>§8] §7Sëkmingai iđ naujo prisijungëte! Jeigu norite atsijungti iđ karto, prađome naudoti §l§a/logout §r§7komandŕ!!§r";
                        logout = "Iki kito karto!";
                        cantLogin = "Negalite prisijungti prie serverio, nes esate prisijunge iđ kitos vietos. Prađome palaukti kelias minutës, kad galëtumete bandyti iđ naujo.";
                        minPassLengthError = "§7[§5LoginMe§7] §4!! Blogas konfiguracijos failas !!§r\n§7[§5LoginMe§7] §cMinimalus slaptaţodţio ilgis turi bűti didesnis nei 6!§r";
                        cantUseThisCommand = "§8[§2>>§8] §7Prađome prisijungti, kad galëtumete naudoti điŕ komandŕ.§r";
                        loginToUseChat = "§8[§4>>§8] §cPrađome prisijungti, kad galëtume naudoti chat'ŕ!§r";
                        break;
                    case "pl":
                        // Polish translate by: ??
                    case "de":
                        // German translate by: ??
                    case "tr":
                        // Turkish translate by: ??
                    default:
                        Bukkit.getConsoleSender().sendMessage("§7[§5LoginMe§7] §4!! Bad config file !!§r\n§7[§5LoginMe§7] §cLanguage: " + lang + " is not supported. Supported languages: en, hu, lt§r");
                        registerText = "§7Please register with the §l§a/register <password> <password> §r§7command!§r";
                        passwordGenerated = "§8[§2>>§8] §7The generated password is: §2[PASSWORD]\n§cDon't forget to save it!§r";
                        passwordGenerateUsage = "§7To use this command type: §l§a/password <length>§r§7 (max 32)";
                        backupCodeCreated = "§8[§2>>§8] §7Your backup code is: §2[BACKUP]\n§cDon't forget to save it! §7Type: §l§a/help loginbackup §r§7to see the avaliable commands.§r";
                        backupCodeWarning = "§8[§4!!§8] §7Your code §cwill be visible! §7Please type the command again to show your code.";
                        backupCodeUse = "§8[§2>>§8] §7Code used! Type: §l§a/loginbackup setpass <password> §r§7to set your password.";
                        backupCodeWrong = "Wrong code!";
                        passwordUpdated = "Password updated! Now you can login with the new password.";
                        codeDeleted = " Please create a new code because the previous one has been deleted.";
                        registeredText = "§8[§2>>§8] §7Successful registration!";
                        addEmail = "§7Are you scared about forgetting your password? Use §l§a/loginbackup email <email> §r§7anytime to bind an email address to your account, or use §l§a/loginbackup create §r§7anytime to create a backup code.";
                        registerExpired = "Your register time has expired!";
                        passwordsDoesNotMatch = "§8[§2>>§8] §7The passwords doesn't match.";
                        weakPassword = "§8[§2>>§8] §7The password what you entered is not secure. Please use another password for your safety.";
                        shortPassword = "§8[§2>>§8] §7The password is too short. Minimum length is: " + minPassLength;
                        loginText = "§7Please login with the §l§a/login <password> §r§7command!§r";
                        loggedIn = "§8[§2>>§8] §7Successful login!";
                        loggedInTitle = "§a Hello ";
                        loggedInSubTitle = "§7Enjoy your stay!";
                        loginExpired = "Your login time has expired!";
                        wrongPassword = "§8[§2>>§8] §7Bad password, try again!";
                        reconnect = "§8[§2>>§8] §7Successfully reconnected! If you want to sign out instantly, please use §l§a/logout §r§7command!§r";
                        logout = "See-ya!";
                        cantLogin = "Can't join to the server, because you connected from another place. Please wait few minutes, before you try again.";
                        minPassLengthError = "§7[§5LoginMe§7] §4!! Bad config file !!§r\n§7[§5LoginMe§7] §cThe minimum password length should greater than 6!§r";
                        cantUseThisCommand = "§8[§2>>§8] §7Please login to use this command.";
                        loginToUseChat = "§8[§4>>§8] §cPlease login to use chat!§r";
                        break;
                }
                connect();
            } catch (IOException e) {
            }
        }
    }

    public Connection connect() {
        try {
            conn = DriverManager.getConnection(databaseUrl);
        } catch (SQLException e) {
        }
        return conn;
    }

    @Override
    public void onDisable() {
        signedInPlayers.clear();
        try {
            Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            conn.close();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
        Player player = event.getPlayer();
        String playerName = player.getName();
        if (signedInPlayers.containsKey(playerName)) {
            String ipAddress = player.getAddress().getAddress().toString();
            String ipAddressin = "";
            for (Map.Entry<String, String> check : signedInPlayers.entrySet()) {
                ipAddressin = check.getValue();
            }
            if (ipAddress.equals(ipAddressin)) {
                player.sendMessage(reconnect);
            }
        } else {
            String welcomeMessageNew = welcomeMessage.replace("[PLAYER]", playerName);
            player.sendMessage(welcomeMessageNew);
            player.setGameMode(GameMode.ADVENTURE);
            player.addPotionEffect(SLOW);
            player.addPotionEffect(BLINDNESS);
            player.addPotionEffect(JUMP);
            player.addPotionEffect(SLOW_DIGGING);
            player.setNoDamageTicks(Integer.MAX_VALUE);
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA-512");
            } catch (NoSuchAlgorithmException e) {
            }
            byte[] hash = md.digest(playerName.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            String username = sb.toString();
            String sqlCommand = "SELECT CASE WHEN EXISTS ( SELECT * FROM `playerdata` WHERE uname ='" + username + "' ) THEN 'TRUE' ELSE 'FALSE' END AS 'EXISTS';";
            try {
                Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlCommand);
                if (rs.next()) {
                    String exists = rs.getString("EXISTS");
                    if (exists.equals("TRUE")) {
                        player.sendMessage(loginText);
                        new BukkitRunnable() {
                            int i = 0;

                            @Override
                            public void run() {
                                i++;
                                if(signedInPlayers.containsKey(playerName)) {
                                    cancel();
                                }
                                else {
                                    player.sendMessage(loginText);
                                }
                                if (i > 4 && !signedInPlayers.containsKey(playerName)) {
                                    player.kickPlayer(loginExpired);
                                    cancel();
                                }
                            }
                        }.runTaskTimer(this, 800, 800); // 20s
                    } else {
                        player.sendMessage(registerText);
                        new BukkitRunnable() {
                            int i = 0;

                            @Override
                            public void run() {
                                i++;
                                if (signedInPlayers.containsKey(playerName)) {
                                    cancel();
                                }
                                else {
                                    player.sendMessage(registerText);
                                }
                                if (i > 4 && !signedInPlayers.containsKey(playerName)) {
                                    player.kickPlayer(registerExpired);
                                    cancel();
                                }
                            }
                        }.runTaskTimer(this, 800, 800);
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        event.setQuitMessage("");
        Player player = event.getPlayer();
        String playerName = player.getName();
        canBackupPlayers.remove(playerName);
        if(signedInPlayers.containsKey(playerName)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (Bukkit.getServer().getPlayer(playerName) == null) {
                        signedInPlayers.remove(playerName);
                        String disconnectMessageNew = disconnectMessage.replace("[PLAYER]", playerName);
                        Bukkit.broadcastMessage(disconnectMessageNew);
                        cancel();
                    }
                    else {
                        cancel();
                    }
                }
            }.runTaskLater(this, logoutTime);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;
        String playerName = player.getName();
        switch (command.getName()) {
            case "logout":
                String disconnectMessageNew = disconnectMessage.replace("[PLAYER]", playerName);
                Bukkit.broadcastMessage(disconnectMessageNew);
                signedInPlayers.remove(playerName);
                player.kickPlayer(logout);
                break;
            case "password":
                if(args[0].equals("")) {
                    player.sendMessage(passwordGenerateUsage);
                }
                else {
                    try {
                        if (args.length != 0) {
                            int length = Integer.parseInt(args[0]);
                            String password = "";
                            Random random = new Random();
                            if (length < 33) {
                                for (int i = 0; i < length; i++) {
                                    int index = random.nextInt(characters.length());
                                    char passwordChar = characters.charAt(index);
                                    password += passwordChar;
                                }
                                String passwordNew = passwordGenerated.replace("[PASSWORD]", password);
                                player.sendMessage(passwordNew);
                            }
                        }
                        else {
                            player.sendMessage(passwordGenerateUsage);
                        }
                    }
                    catch (NumberFormatException e) {
                        player.sendMessage(passwordGenerateUsage);
                    }
                }
                break;
            case "changepass":
                if(signedInPlayers.containsKey(playerName)) {
                    String passwd1 = args[0];
                    MessageDigest md = null;
                    try {
                        md = MessageDigest.getInstance("SHA-512");
                    } catch (NoSuchAlgorithmException e) {
                    }
                    byte[] hash = md.digest(passwd1.getBytes());
                    StringBuilder sb = new StringBuilder();
                    for (byte b : hash) {
                        sb.append(String.format("%02x", b));
                    }
                    String passwd = sb.toString();
                    hash = md.digest(playerName.getBytes());
                    sb = new StringBuilder();
                    for (byte b : hash) {
                        sb.append(String.format("%02x", b));
                    }
                    String username = sb.toString();
                    String sqlCommand = "SELECT MAX(CASE WHEN uname = '" + username + "' AND passwd = '" + passwd + "' THEN 'TRUE' ELSE 'FALSE' END) AS 'MATCH' FROM playerdata;";
                    try {
                        Connection conn = this.connect();
                        Statement stmt = conn.createStatement();
                        stmt.execute(sqlCommand);
                        ResultSet rs = stmt.executeQuery(sqlCommand);
                        if (rs.next()) {
                            String match = rs.getString("MATCH");
                            stmt.close();
                            conn.close();
                            rs.close();
                            if (match.equals("TRUE")) {
                                String password = args[1];
                                if (password.length() >= minPassLength) {
                                    if (weakPasswords.contains(password) && dontAllowCommonPasswords || password.equals(playerName)) {
                                        player.sendMessage(weakPassword);
                                    } else {
                                        md = null;
                                        try {
                                            md = MessageDigest.getInstance("SHA-512");
                                        } catch (NoSuchAlgorithmException e) {
                                        }
                                        hash = md.digest(args[1].getBytes());
                                        sb = new StringBuilder();
                                        for (byte b : hash) {
                                            sb.append(String.format("%02x", b));
                                        }
                                        password = sb.toString();
                                        sqlCommand = "UPDATE playerdata SET passwd = '" + password + "' WHERE uname = '" + username + "';";
                                        try {
                                            conn = this.connect();
                                            stmt = conn.createStatement();
                                            stmt.execute(sqlCommand);
                                            stmt.close();
                                            conn.close();
                                        } catch (SQLException e) {
                                        }
                                        player.kickPlayer(passwordUpdated);
                                    }
                                }
                                else {
                                    player.sendMessage(shortPassword);
                                }
                            }
                            else {
                                player.sendMessage(wrongPassword);
                            }
                        }
                    }
                    catch (SQLException e) {

                    }
                }
                break;
            case "loginbackup":
                if(args[0].equals("create")) {
                    if (signedInPlayers.containsKey(playerName)) {
                        if(backupCodeCommand.containsKey(playerName) && backupCodeCommand.containsValue(1)) {
                            MessageDigest md = null;
                            try {
                                md = MessageDigest.getInstance("SHA-512");
                            } catch (NoSuchAlgorithmException e) {
                            }
                            byte[] hash = md.digest(playerName.getBytes());
                            StringBuilder sb = new StringBuilder();
                            for (byte b : hash) {
                                sb.append(String.format("%02x", b));
                            }
                            String username = sb.toString();
                            String backup = "";
                            Random random2 = new Random();
                            for (int i = 0; i < 8; i++) {
                                int index = random2.nextInt(charactersBackup.length());
                                char backupChar = charactersBackup.charAt(index);
                                backup += backupChar;
                            }
                            String backupNew = backupCodeCreated.replace("[BACKUP]", backup);
                            player.sendMessage(backupNew);
                            backupCodeCommand.remove(playerName);
                            md = null;
                            try {
                                md = MessageDigest.getInstance("SHA-512");
                            } catch (NoSuchAlgorithmException e) {
                            }
                            hash = md.digest(backup.getBytes());
                            sb = new StringBuilder();
                            for (byte b : hash) {
                                sb.append(String.format("%02x", b));
                            }
                            String backupCode = sb.toString();
                            String sqlCommand = "UPDATE playerdata SET backupcode = '" + backupCode + "' WHERE uname = '" + username + "';";
                            try {
                                Connection conn = this.connect();
                                Statement stmt = conn.createStatement();
                                stmt.execute(sqlCommand);
                                stmt.close();
                                conn.close();
                            }
                            catch (SQLException e) {
                            }
                        }
                        else {
                            if (!backupCodeCommand.containsKey(playerName)) {
                                backupCodeCommand.put(playerName, 1);
                            }
                            player.sendMessage(backupCodeWarning);
                        }
                    }
                }
                if(args[0].equals("code")) {
                    if (!signedInPlayers.containsKey(playerName)) {
                        MessageDigest md = null;
                        try {
                            md = MessageDigest.getInstance("SHA-512");
                        } catch (NoSuchAlgorithmException e) {
                        }
                        byte[] hash = md.digest(playerName.getBytes());
                        StringBuilder sb = new StringBuilder();
                        for (byte b : hash) {
                            sb.append(String.format("%02x", b));
                        }
                        String username = sb.toString();
                        md = null;
                        try {
                            md = MessageDigest.getInstance("SHA-512");
                        } catch (NoSuchAlgorithmException e) {
                        }
                        hash = md.digest(args[1].getBytes());
                        sb = new StringBuilder();
                        for (byte b : hash) {
                            sb.append(String.format("%02x", b));
                        }
                        String backupEnter = sb.toString();
                        String sqlCommand = "SELECT MAX(CASE WHEN uname = '" + username + "' AND backupcode = '" + backupEnter + "' THEN 'TRUE' ELSE 'FALSE' END) AS 'CORRECTCODE' FROM playerdata;";
                        try {
                            Connection conn = this.connect();
                            Statement stmt = conn.createStatement();
                            ResultSet rs = stmt.executeQuery(sqlCommand);
                            if (rs.next()) {
                                String correct = rs.getString("CORRECTCODE");
                                conn.close();
                                stmt.close();
                                rs.close();
                                if (correct.equals("TRUE")) {
                                    player.sendMessage(backupCodeUse);
                                    canBackupPlayers.add(playerName);
                                }
                                else {
                                    player.kickPlayer(backupCodeWrong);
                                }
                            }
                        } catch (SQLException e) {
                        }
                    }
                }
                if (args[0].equals("setpass")) {
                    if (!signedInPlayers.containsKey(playerName)) {
                        MessageDigest md = null;
                        try {
                            md = MessageDigest.getInstance("SHA-512");
                        } catch (NoSuchAlgorithmException e) {
                        }
                        byte[] hash = md.digest(playerName.getBytes());
                        StringBuilder sb = new StringBuilder();
                        for (byte b : hash) {
                            sb.append(String.format("%02x", b));
                        }
                        String username = sb.toString();
                        String passwordNew = args[1];
                        if (canBackupPlayers.contains(playerName)) {
                            if (passwordNew.length() >= minPassLength) {
                                if (weakPasswords.contains(passwordNew) && dontAllowCommonPasswords || passwordNew.equals(playerName)) {
                                    player.sendMessage(weakPassword);
                                } else {
                                    md = null;
                                    try {
                                        md = MessageDigest.getInstance("SHA-512");
                                    } catch (NoSuchAlgorithmException e) {
                                    }
                                    hash = md.digest(args[1].getBytes());
                                    sb = new StringBuilder();
                                    for (byte b : hash) {
                                        sb.append(String.format("%02x", b));
                                    }
                                    passwordNew = sb.toString();
                                    String sqlCommand = "UPDATE playerdata SET passwd = '" + passwordNew + "' WHERE uname = '" + username + "';";
                                    try {
                                        Connection conn = this.connect();
                                        Statement stmt = conn.createStatement();
                                        stmt.execute(sqlCommand);
                                        stmt.close();
                                        conn.close();
                                    } catch (SQLException e) {
                                    }
                                    canBackupPlayers.remove(playerName);
                                    sqlCommand = "UPDATE playerdata SET backupcode = '' WHERE uname = '" + username + "';";
                                    try {
                                        Connection conn = this.connect();
                                        Statement stmt = conn.createStatement();
                                        stmt.execute(sqlCommand);
                                        stmt.close();
                                        conn.close();
                                    } catch (SQLException e) {
                                    }
                                    player.kickPlayer(passwordUpdated + codeDeleted);
                                }
                            }
                            else {
                                player.sendMessage(shortPassword);
                            }
                        }
                    }
                }
                if(args[0].equals("email")) {
                    String emailAddress = args[1];
                    MessageDigest md = null;
                    try {
                        md = MessageDigest.getInstance("SHA-512");
                    } catch (NoSuchAlgorithmException e) {
                    }
                    byte[] hash = md.digest(playerName.getBytes());
                    StringBuilder sb = new StringBuilder();
                    for (byte b : hash) {
                        sb.append(String.format("%02x", b));
                    }
                    String username = sb.toString();
                    String sqlCommand = "SELECT MAX(CASE WHEN uname = '" + username + "' AND email = '' THEN 'TRUE' ELSE 'FALSE' END) AS 'HAVEEMAIL' FROM playerdata;";
                    try {
                        Connection conn = this.connect();
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sqlCommand);
                        if (rs.next()) {
                            String haveemail = rs.getString("HAVEEMAIL");
                            conn.close();
                            stmt.close();
                            rs.close();
                            if (haveemail.equals("TRUE")) {
                                player.sendMessage(updateEmail);
                            }
                            else {

                            }
                        }
                    } catch (SQLException e) {
                    }
                }
            if(args[0].equals("newmail")) {
                String emailAddress = args[1];
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("SHA-512");
                } catch (NoSuchAlgorithmException e) {
                }
                byte[] hash = md.digest(playerName.getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte b : hash) {
                    sb.append(String.format("%02x", b));
                }
                String username = sb.toString();
                break;
            }
            case "login":
                if (!signedInPlayers.containsKey(playerName)) {
                    String passwd1 = args[0];
                    MessageDigest md = null;
                    try {
                        md = MessageDigest.getInstance("SHA-512");
                    } catch (NoSuchAlgorithmException e) {
                    }
                    byte[] hash = md.digest(passwd1.getBytes());
                    StringBuilder sb = new StringBuilder();
                    for (byte b : hash) {
                        sb.append(String.format("%02x", b));
                    }
                    String passwd = sb.toString();
                    hash = md.digest(playerName.getBytes());
                    sb = new StringBuilder();
                    for (byte b : hash) {
                        sb.append(String.format("%02x", b));
                    }
                    String username = sb.toString();
                    String sqlCommand = "SELECT MAX(CASE WHEN uname = '" + username + "' AND passwd = '" + passwd + "' THEN 'TRUE' ELSE 'FALSE' END) AS 'MATCH' FROM playerdata;";
                    try {
                        Connection conn = this.connect();
                        Statement stmt = conn.createStatement();
                        stmt.execute(sqlCommand);
                        ResultSet rs = stmt.executeQuery(sqlCommand);
                        if (rs.next()) {
                            String match = rs.getString("MATCH");
                            if (match.equals("TRUE")) {
                                String joinMessageNew = joinMessage.replace("[PLAYER]", playerName);
                                Bukkit.broadcastMessage(joinMessageNew);
                                player.sendMessage(loggedIn);
                                player.setGameMode(GameMode.SURVIVAL);
                                player.sendTitle(loggedInTitle + playerName + "!", loggedInSubTitle, 5, 70, 10);
                                player.playSound(player.getLocation(), "block.note_block.pling", SoundCategory.MASTER, 1.0f, 1.0f);
                                signedInPlayers.put(playerName, player.getAddress().getAddress().toString());
                                player.removePotionEffect(SLOW.getType());
                                player.removePotionEffect(BLINDNESS.getType());
                                player.removePotionEffect(JUMP.getType());
                                player.removePotionEffect(SLOW_DIGGING.getType());
                                player.setNoDamageTicks(200);
                                player.addPotionEffect(DAMAGE_RESISTANCE);
                            }
                            else {
                                player.sendMessage(wrongPassword);
                            }
                            rs.close();
                            stmt.close();
                            conn.close();
                        }
                    } catch (SQLException e) {
                    }
                }
                break;
        }
        if(command.getName().equals("register") || command.getName().equals("reg")) {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA-512");
            } catch (NoSuchAlgorithmException e) {
            }
            byte[] hash = md.digest(playerName.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            String username = sb.toString();
            String sqlCommand = "SELECT CASE WHEN EXISTS ( SELECT * FROM `playerdata` WHERE uname ='" + username + "' ) THEN 'TRUE' ELSE 'FALSE' END AS 'EXISTS';";
            try {
                Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlCommand);
                if (rs.next()) {
                    String exists = rs.getString("EXISTS");
                    stmt.close();
                    conn.close();
                    if (exists.equals("FALSE")) {
                        if (!signedInPlayers.containsKey(playerName)) {
                            String passwd1 = args[0];
                            String passwd2 = args[1];
                            if (passwd1.length() >= minPassLength) {
                                if (passwd1.equals(passwd2)) {
                                    if (weakPasswords.contains(passwd1) && dontAllowCommonPasswords) {
                                        player.sendMessage(weakPassword);
                                    }
                                    if(passwd1.equals(playerName)) {
                                        player.sendMessage(weakPassword);
                                    }
                                    else {
                                        md = null;
                                        try {
                                            md = MessageDigest.getInstance("SHA-512");
                                        } catch (NoSuchAlgorithmException e) {
                                        }
                                        hash = md.digest(passwd1.getBytes());
                                        sb = new StringBuilder();
                                        for (byte b : hash) {
                                            sb.append(String.format("%02x", b));
                                        }
                                        String passwd = sb.toString();
                                        sqlCommand = "INSERT INTO PLAYERDATA (uname, passwd) " +
                                                "VALUES ('" + username + "', '" + passwd + "')";
                                        try {
                                            conn = this.connect();
                                            stmt = conn.createStatement();
                                            stmt.execute(sqlCommand);
                                            rs.close();
                                            stmt.close();
                                            conn.close();
                                            String joinMessageNew = joinMessage.replace("[PLAYER]", playerName);
                                            Bukkit.broadcastMessage(joinMessageNew);
                                            player.sendMessage(registeredText);
                                            player.setGameMode(GameMode.SURVIVAL);
                                            player.sendMessage(addEmail);
                                            player.sendTitle(loggedInTitle + playerName + "!", loggedInSubTitle, 5, 70, 10);
                                            player.playSound(player.getLocation(), "block.note_block.pling", SoundCategory.MASTER, 1.0f, 1.0f);
                                            signedInPlayers.put(playerName, player.getAddress().getAddress().toString());
                                            player.removePotionEffect(SLOW.getType());
                                            player.removePotionEffect(BLINDNESS.getType());
                                            player.removePotionEffect(JUMP.getType());
                                            player.removePotionEffect(SLOW_DIGGING.getType());
                                            player.setNoDamageTicks(200);
                                            player.addPotionEffect(DAMAGE_RESISTANCE);
                                        } catch (SQLException e) {
                                        }
                                    }
                                } else {
                                    player.sendMessage(passwordsDoesNotMatch);
                                }
                            } else {
                                player.sendMessage(shortPassword);
                            }
                        }
                    }
                }
            }
            catch (SQLException e) {
            }
        }
        return true;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        String command = event.getMessage();
        if (!signedInPlayers.containsKey(playerName)) {
            if (!command.startsWith("/login") && !command.startsWith("/logout") && !command.startsWith("/register") && !command.startsWith("/reg") && !command.startsWith("/password") && !command.startsWith("/loginbackup") && !command.startsWith("/help")) {
                event.setCancelled(true);
                player.sendMessage(cantUseThisCommand);
            }
        }
    }

    @EventHandler
    public void onPlayerSendMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        if(!signedInPlayers.containsKey(playerName))
        {
            player.sendMessage(loginToUseChat);
            event.setMessage("");
            event.setCancelled(true);
        }
    }
}
