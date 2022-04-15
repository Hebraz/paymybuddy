DROP DATABASE IF EXISTS paymybuddy ;
CREATE DATABASE paymybuddy;
USE paymybuddy;

-- paymybuddy.`authorities` definition

CREATE TABLE `authorities` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `authority` VARCHAR(50) NOT NULL UNIQUE,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- paymybuddy.`user` definition

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL UNIQUE,
  `password` varchar(100),
  `enabled` TINYINT NOT NULL DEFAULT 1,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `balance`  BIGINT NOT NULL DEFAULT '0', -- unit is cent
  `authority_id`  int(11)  NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `user_authorities_fk`  FOREIGN KEY (`authority_id`) REFERENCES `authorities` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- paymybuddy.bank_account definition

CREATE TABLE `bank_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `iban` varchar(50) NOT NULL UNIQUE,
  `description` varchar(100) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_bank_account_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- paymybuddy.bank_transfer definition

CREATE TABLE `bank_transfer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` timestamp NOT NULL,
  `amount` BIGINT NOT NULL DEFAULT '0', -- unit is cent
  `description` varchar(100) DEFAULT NULL,
  `bank_account_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `bank_account_bank_transfer_fk` (`bank_account_id`),
  CONSTRAINT `bank_account_bank_transfer_fk` FOREIGN KEY (`bank_account_id`) REFERENCES `bank_account` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- paymybuddy.`transaction` definition

CREATE TABLE `transaction` (
  `id_transaction` int(11) NOT NULL AUTO_INCREMENT,
  `date` timestamp NOT NULL,
  `total_amount` BIGINT NOT NULL DEFAULT '0', -- unit is cent
  `description` varchar(100) DEFAULT NULL,
  `fee_amount` BIGINT NOT NULL, -- unit is cent
  `payer_id` int(11) DEFAULT NULL,
  `credit_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_transaction`),
  KEY `user_transaction_fk` (`payer_id`),
  KEY `user_transaction_fk1` (`credit_id`),
  CONSTRAINT `user_transaction_fk` FOREIGN KEY (`payer_id`) REFERENCES `user` (`id`) ON DELETE SET NULL,
  CONSTRAINT `user_transaction_fk1` FOREIGN KEY (`credit_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- paymybuddy.user_connection_assoc definition

CREATE TABLE `user_connection_assoc` (
  `user_id` int(11) NOT NULL,
  `connection_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`connection_id`),
  KEY `user_user_connection_assoc_fk` (`connection_id`),
  CONSTRAINT `user_user_connection_assoc_fk` FOREIGN KEY (`connection_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ,
  CONSTRAINT `user_user_connection_assoc_fk1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;