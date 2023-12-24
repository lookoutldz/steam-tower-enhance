CREATE DATABASE IF NOT EXISTS steamtower DEFAULT CHARACTER SET utf8mb4;

USE steamtower;

CREATE TABLE IF NOT EXISTS `player` (
    `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `steamid` VARCHAR(255) NOT NULL,
    `communityvisibilitystate` INT UNSIGNED,
    `profilestate` INT UNSIGNED,
    `personaname` VARCHAR(255),
    `lastlogoff` INT UNSIGNED,
    `commentpermission` INT UNSIGNED,
    `profileurl` VARCHAR(255),
    `avatar` VARCHAR(255),
    `avatarmedium` VARCHAR(255),
    `avatarfull` VARCHAR(255),
    `avatarhash` VARCHAR(255),
    `personastate` INT UNSIGNED,
    `realname` VARCHAR(255),
    `primaryclanid` VARCHAR(255),
    `timecreated` INT UNSIGNED,
    `personastateflags` INT UNSIGNED,
    `gameextrainfo` VARCHAR(255),
    `gameserverip` VARCHAR(255),
    `gameid` INT UNSIGNED,
    `loccountrycode` VARCHAR(255),
    `locstatecode` VARCHAR(255),
    `loccityid` VARCHAR(255),
    `steamlevel` INT UNSIGNED,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `delete_time` TIMESTAMP DEFAULT NULL,
    `deleted` BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE UNIQUE INDEX `uk_steamid_on_player` ON player(`steamid`);


CREATE TABLE IF NOT EXISTS `app` (
    `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `appid` INT UNSIGNED NOT NULL,
    `appname` VARCHAR(255),
    `chname` VARCHAR(255),
    `img_icon_url` VARCHAR(255),
    `img_logo_url` VARCHAR(255),
    `screenshot_url` TEXT,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `delete_time` TIMESTAMP DEFAULT NULL,
    `deleted` BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE UNIQUE INDEX `uk_appid_on_app` ON app(`appid`);

CREATE TABLE game_schema (
    `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `appid` INT NOT NULL,
    `achname` VARCHAR(255) NOT NULL,
    `defaultvalue` INT UNSIGNED,
    `displayName` TEXT,
    `hidden` INT UNSIGNED,
    `description` TEXT,
    `icon` VARCHAR(255),
    `icongray` VARCHAR(255),
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `delete_time` TIMESTAMP DEFAULT NULL,
    `deleted` BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE UNIQUE INDEX `uk_appid_achname_on_game_schema` ON game_schema (`appid`, `achname`);

CREATE TABLE IF NOT EXISTS `owned_game` (
    `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `steamid` VARCHAR(255) NOT NULL,
    `appid` INT NOT NULL,
    `appname` VARCHAR(255),
    `playtime_2week` INT UNSIGNED,
    `playtime_forever` INT UNSIGNED,
    `img_icon_url` VARCHAR(255),
    `img_logo_url` VARCHAR(255),
    `has_community_visible_state` TINYINT (1),
    `has_leaderboards` TINYINT (1),
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `delete_time` TIMESTAMP DEFAULT NULL,
    `deleted` BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE UNIQUE INDEX `uk_steamid_appid_on_owned_game` ON owned_game (`steamid`, `appid`);

CREATE TABLE player_achi (
    `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `steamid` VARCHAR(255) NOT NULL,
    `appid` INT NOT NULL,
    `achname` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `achieved` INT UNSIGNED,
    `unlocktime` INT UNSIGNED,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `delete_time` TIMESTAMP DEFAULT NULL,
    `deleted` BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE UNIQUE INDEX `uk_steamid_appid_achname_on_player_achi` ON player_achi (`steamid`, `appid`, `achname`);

CREATE TABLE friend (
    `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `steamid` VARCHAR(255) NOT NULL,
    `friendsteamid` VARCHAR(255) NOT NULL,
    `relationship` VARCHAR(255),
    `friend_since` INT UNSIGNED,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `delete_time` TIMESTAMP DEFAULT NULL,
    `deleted` BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE UNIQUE INDEX `uk_steamid_friendsteamid_on_friend` ON friend (`steamid`, `friendsteamid`);

-- CREATE INDEX unlocktime ON player_achi(unlocktime);
-- CREATE INDEX achieved ON player_achi(achieved);
-- CREATE INDEX achi_appid ON player_achi(appid);
-- CREATE INDEX game_appid ON owned_game(appid);
-- CREATE INDEX schema_appid ON game_schema(appid);
--
-- CREATE INDEX game_steamid ON owned_game(steamid);
-- CREATE INDEX achi_steamid ON player_achi(steamid);