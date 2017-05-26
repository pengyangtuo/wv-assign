SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema wave_employee
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `wave_employee` ;
CREATE SCHEMA IF NOT EXISTS `wave_employee` DEFAULT CHARACTER SET latin1 ;
USE `wave_employee` ;

-- -----------------------------------------------------
-- Table `wave_employee`.`pay_group`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `wave_employee`.`pay_group` ;

CREATE TABLE IF NOT EXISTS `wave_employee`.`pay_group` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `rate` DOUBLE NOT NULL,
  `last_modified_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `wave_employee`.`employee`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `wave_employee`.`employee` ;

CREATE TABLE IF NOT EXISTS `wave_employee`.`employee` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `pay_group_id` INT NOT NULL,
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_employee_pay_group1_idx` (`pay_group_id` ASC),
  CONSTRAINT `fk_employee_pay_group1`
    FOREIGN KEY (`pay_group_id`)
    REFERENCES `wave_employee`.`pay_group` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `wave_employee`.`report`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `wave_employee`.`report` ;

CREATE TABLE IF NOT EXISTS `wave_employee`.`report` (
  `id` INT NOT NULL,
  `uploaded_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `location` VARCHAR(255) NULL,
  `desc` VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  INDEX `uploaded_time_index` (`uploaded_time` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `wave_employee`.`report_record`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `wave_employee`.`report_record` ;

CREATE TABLE IF NOT EXISTS `wave_employee`.`report_record` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `report_id` INT NOT NULL,
  `employee_id` INT NOT NULL,
  `hours` DOUBLE NOT NULL,
  `rate` DOUBLE NOT NULL,
  `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_report_record_report_idx` (`report_id` ASC),
  INDEX `record_report_id_index` (`report_id` ASC),
  INDEX `record_employee_id_index` (`employee_id` ASC),
  INDEX `date_index` (`date` ASC),
  CONSTRAINT `fk_report_record_report`
    FOREIGN KEY (`report_id`)
    REFERENCES `wave_employee`.`report` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `wave_employee`.`report_summary`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `wave_employee`.`report_summary` ;

CREATE TABLE IF NOT EXISTS `wave_employee`.`report_summary` (
  `employee_id` VARCHAR(45) NOT NULL,
  `pay_period_start` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `pay_period_end` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `amount_paid` DOUBLE NOT NULL,
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`employee_id`, `pay_period_start`, `pay_period_end`))
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -------------------------------------------------------
-- Initial Dump
-- -------------------------------------------------------
INSERT INTO `wave_employee`.`pay_group` VALUES (1, "A", 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP );
INSERT INTO `wave_employee`.`pay_group` VALUES (2, "B", 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP );

INSERT INTO `wave_employee`.`employee` VALUES (1, "Tim", "Peng", 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP );
INSERT INTO `wave_employee`.`employee` VALUES (2, "Tom", "Tuo", 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP );
INSERT INTO `wave_employee`.`employee` VALUES (3, "Yang", "Tuo", 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP );
INSERT INTO `wave_employee`.`employee` VALUES (4, "Morning", "Good", 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP );