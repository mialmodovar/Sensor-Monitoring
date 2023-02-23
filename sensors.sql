-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Tempo de geração: 10-Abr-2022 às 23:19
-- Versão do servidor: 10.4.22-MariaDB
-- versão do PHP: 8.1.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `sid2022`
--

DELIMITER $$
--
-- Procedimentos
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `atribuirCultura` (IN `idCultura` INT, IN `idUtilizador` INT)  UPDATE cultura c
SET c.IDUtilizador = idUtilizador
WHERE c.IDCultura = idCultura$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `atualizarParametroCultura` (IN `idCultura` INT, IN `minTmp` DECIMAL(10,2), IN `pMinTmp` DECIMAL(10,2), IN `pMaxTmp` DECIMAL(10,2), IN `maxTmp` DECIMAL(10,2), IN `minHum` DECIMAL(10,2), IN `pMinHum` DECIMAL(10,2), IN `pMaxHum` DECIMAL(10,2), IN `maxHum` DECIMAL(10,2), IN `minLuz` DECIMAL(10,2), IN `pMinLuz` DECIMAL(10,2), IN `pMaxLuz` DECIMAL(10,2), IN `maxLuz` DECIMAL(10,2), IN `intervalo` INT(1))  BEGIN
DECLARE res int;

SET res = (SELECT COUNT(IDCultura)
FROM utilizador u JOIN cultura c where u.NomeUtilizador = REPLACE(USER(),    '@localhost', '') and  
   c.IDUtilizador = u.IDUtilizador and c.IDCultura = idCultura);
   
   IF res = 1 THEN
   
INSERT INTO parametrocultura (IDCultura, MinTmp, pertoMinTmp, pertoMaxTmp, MaxTmp, MinHum, pertoMinHum, pertoMaxHum, MaxHum, MinLuz, pertoMinLuz, pertoMaxLuz, MaxLuz, intervaloAlerta) 

VALUES (idCultura,minTmp,pMinTmp,pMaxTmp,
        maxTmp,minHum,pMinHum,
        pMaxHum,maxHum,minLuz,pMinLuz,pMaxLuz,
        maxLuz,intervalo)
       
                                                      
ON DUPLICATE KEY UPDATE

IDCultura=idCultura,
MinTmp=minTmp,
pertoMinTmp=pMinTmp,
pertoMaxTmp=pMaxTmp,
MaxTmp=maxTmp,
MinHum=minHum,
pertoMinHum=pMinHum,
pertoMaxHum=pMaxHum,
MaxHum=maxHum,
MinLuz=minLuz,
pertoMinLuz=pMinLuz,
pertoMaxLuz=pMaxLuz,
MaxLuz=maxLuz,
intervaloAlerta=intervalo;

 END IF;
  
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `criarCultura` (IN `zona` VARCHAR(2), IN `estado` INT(1), IN `nomeCultura` VARCHAR(100))  INSERT INTO cultura(IDZona,Estado,NomeCultura)
VALUES (zona,estado,nomeCultura)$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `criarUtilizador` (IN `p_Name` VARCHAR(100), IN `p_Passw` VARCHAR(100), IN `p_Tipo` VARCHAR(100), IN `p_Email` VARCHAR(100))  BEGIN
    SET @sql := CONCAT('CREATE USER ''', p_Name, '''@''localhost''', ' IDENTIFIED BY ''', p_Passw, '''');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    INSERT INTO utilizador (Email, NomeUtilizador,TipoUtilizador,Password) VALUES (p_Email,p_Name,p_Tipo,p_Passw);
    CASE
        WHEN p_Tipo = 'I' THEN SET @perm := concat('GRANT investigador TO ''',p_Name,'''@''localhost''');
                               SET @role := concat(' SET DEFAULT ROLE investigador FOR  ''',p_Name,'''@''localhost''');

        WHEN p_Tipo = 'T' THEN SET @perm := concat('GRANT tecnico TO ''',p_Name,'''@''localhost''');
                               SET @role := concat(' SET DEFAULT ROLE tecnico FOR  ''',p_Name,'''@''localhost''');

        WHEN p_Tipo = 'A' THEN SET @perm := concat('GRANT administrador TO ''',p_Name,'''@''localhost''');
                               SET @role := concat(' SET DEFAULT ROLE administrador FOR  ''',p_Name,'''@''localhost''');
        
        WHEN p_Tipo = 'J' THEN SET @perm := concat('GRANT java TO ''',p_Name,'''@''localhost''');
                               SET @role := concat(' SET DEFAULT ROLE java FOR  ''',p_Name,'''@''localhost''');

    END CASE;
    PREPARE grnt FROM @perm;
    EXECUTE grnt;
    PREPARE ex FROM @role;
    EXECUTE ex; 
     
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `editarNomeCultura` (IN `nomeCultura` VARCHAR(100), IN `idCultura` INT)  BEGIN
DECLARE res int;

SET res = (SELECT COUNT(IDCultura)
FROM utilizador u JOIN cultura c where u.NomeUtilizador = REPLACE(USER(),    '@localhost', '') and  
   c.IDUtilizador = u.IDUtilizador and c.IDCultura = idCultura);
   
   IF res = 1 THEN

UPDATE cultura c
SET c.NomeCultura = nomeCultura
WHERE c.IDCultura = idCultura;

END IF;

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `editarUtilizador` (IN `idUtilizador` INT, IN `nome` VARCHAR(100), IN `tipoUtilizador` VARCHAR(100), IN `password` VARCHAR(100))  UPDATE utilizador u
SET u.NomeUtilizador = nome, u.TipoUtilizador = tipoUtilizador , u.Password = password
WHERE u.IDUtilizador = idUtilizador$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `obterIDCultura` (IN `idUtilizador` INT, IN `nomeCultura` VARCHAR(100))  SELECT IDCultura FROM cultura c WHERE c.IDUtilizador = idUtilizador AND c.NomeCultura = nomeCultura$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `obterNomeCulturas` (IN `idUtilizador` INT, IN `idZona` VARCHAR(2))  SELECT NomeCultura FROM cultura c WHERE c.IDUtilizador = idUtilizador AND c.IDZona = idZona$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `obterUtilizador` (IN `email` VARCHAR(100), IN `pass` VARCHAR(100))  SELECT * FROM utilizador u WHERE u.Email= email AND u.Password= pass AND u.TipoUtilizador = 'I'$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `removerCultura` (IN `idCultura` INT)  DELETE FROM cultura 
WHERE cultura.IDCultura = idCultura$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `removerUtilizador` (IN `idUtilizador` INT)  DELETE FROM utilizador 
WHERE utilizador.IDUtilizador = idUtilizador$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `alerta`
--

CREATE TABLE `alerta` (
  `IDAlerta` int(11) NOT NULL,
  `IDCultura` int(11) DEFAULT NULL,
  `IDMedicao` int(11) DEFAULT NULL,
  `IDZona` varchar(2) DEFAULT NULL,
  `DataHoraMedicao` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `ValorMedicao` double(10,2) DEFAULT NULL,
  `TipoAlerta` varchar(20) DEFAULT NULL,
  `TipoSensor` varchar(20) DEFAULT NULL,
  `Cultura` varchar(20) DEFAULT NULL,
  `Mensagem` varchar(20) DEFAULT NULL,
  `HoraEscrita` timestamp NOT NULL DEFAULT current_timestamp(),
  `IDUtilizador` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Extraindo dados da tabela `alerta`
--

INSERT INTO `alerta` (`IDAlerta`, `IDCultura`, `IDMedicao`, `IDZona`, `DataHoraMedicao`, `ValorMedicao`, `TipoAlerta`, `TipoSensor`, `Cultura`, `Mensagem`, `HoraEscrita`, `IDUtilizador`) VALUES
(1, 2, 14, 'Z1', '2022-04-09 16:58:36', 9.00, 'Vermelho', 'T1', 'testeConsegui', '', '2022-04-09 16:58:36', 0),
(2, 5, 14, 'Z1', '2022-04-09 16:58:36', 9.00, 'Amarelo', 'T1', 'Espinafres', '', '2022-04-09 16:58:36', 0);

-- --------------------------------------------------------

--
-- Estrutura da tabela `anomalias`
--

CREATE TABLE `anomalias` (
  `IDAnomalia` int(11) NOT NULL,
  `IDSensor` varchar(2) DEFAULT NULL,
  `IDZona` varchar(2) DEFAULT NULL,
  `ValorMedicao` double(10,2) DEFAULT NULL,
  `DataHoraMedicao` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Estrutura da tabela `cultura`
--

CREATE TABLE `cultura` (
  `IDCultura` int(11) NOT NULL,
  `IDUtilizador` int(11) DEFAULT NULL,
  `IDZona` varchar(2) DEFAULT NULL,
  `Estado` int(1) DEFAULT NULL,
  `NomeCultura` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Extraindo dados da tabela `cultura`
--

INSERT INTO `cultura` (`IDCultura`, `IDUtilizador`, `IDZona`, `Estado`, `NomeCultura`) VALUES
(1, 14, 'Z2', 0, 'Nozes'),
(2, 12, 'Z1', 1, 'testeConsegui'),
(3, 17, 'Z2', 0, 'a'),
(5, 12, 'Z1', 0, 'Espinafres'),
(6, 15, 'Z1', 0, 'aa'),
(7, 15, 'Z1', 1, 'Alhos');

-- --------------------------------------------------------

--
-- Estrutura da tabela `medicao`
--

CREATE TABLE `medicao` (
  `IDMedicao` int(11) NOT NULL,
  `IDSensor` varchar(2) DEFAULT NULL,
  `IDZona` varchar(2) DEFAULT NULL,
  `ValorMedicao` double(10,2) DEFAULT NULL,
  `DataHoraMedicao` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Extraindo dados da tabela `medicao`
--

INSERT INTO `medicao` (`IDMedicao`, `IDSensor`, `IDZona`, `ValorMedicao`, `DataHoraMedicao`) VALUES
(4, 'T1', 'Z1', 10.50, '2022-04-09 15:39:02');

-- --------------------------------------------------------

--
-- Estrutura da tabela `parametrocultura`
--

CREATE TABLE `parametrocultura` (
  `IDCultura` int(11) NOT NULL,
  `MinTmp` decimal(10,2) NOT NULL,
  `pertoMinTmp` decimal(10,2) NOT NULL,
  `pertoMaxTmp` decimal(10,2) NOT NULL,
  `MaxTmp` decimal(10,2) NOT NULL,
  `MinHum` decimal(10,2) NOT NULL,
  `pertoMinHum` decimal(10,2) NOT NULL,
  `pertoMaxHum` decimal(10,2) NOT NULL,
  `MaxHum` decimal(10,2) NOT NULL,
  `MinLuz` decimal(10,2) NOT NULL,
  `pertoMinLuz` decimal(10,2) NOT NULL,
  `pertoMaxLuz` decimal(10,2) NOT NULL,
  `MaxLuz` decimal(10,2) NOT NULL,
  `intervaloAlerta` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Extraindo dados da tabela `parametrocultura`
--

INSERT INTO `parametrocultura` (`IDCultura`, `MinTmp`, `pertoMinTmp`, `pertoMaxTmp`, `MaxTmp`, `MinHum`, `pertoMinHum`, `pertoMaxHum`, `MaxHum`, `MinLuz`, `pertoMinLuz`, `pertoMaxLuz`, `MaxLuz`, `intervaloAlerta`) VALUES
(1, '6.00', '6.00', '6.00', '6.00', '6.00', '6.00', '6.00', '6.00', '6.00', '6.00', '6.00', '6.00', 6),
(2, '6.00', '6.00', '6.00', '6.00', '6.00', '6.00', '6.00', '6.00', '6.00', '6.00', '6.00', '6.00', 1),
(3, '4.00', '4.00', '4.00', '4.00', '4.00', '4.00', '4.00', '4.00', '4.00', '4.00', '4.00', '4.00', 4),
(5, '1.00', '2.00', '8.00', '10.00', '5.00', '5.00', '5.00', '5.00', '5.00', '5.00', '5.00', '5.00', 2);

-- --------------------------------------------------------

--
-- Estrutura da tabela `utilizador`
--

CREATE TABLE `utilizador` (
  `IDUtilizador` int(11) NOT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `NomeUtilizador` varchar(100) DEFAULT NULL,
  `TipoUtilizador` varchar(1) DEFAULT NULL,
  `Password` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Extraindo dados da tabela `utilizador`
--

INSERT INTO `utilizador` (`IDUtilizador`, `Email`, `NomeUtilizador`, `TipoUtilizador`, `Password`) VALUES
(12, 'user1@gmail.com', 'user1', 'I', '1234'),
(13, 'user2@gmail.com', 'user2', 'I', '1234'),
(14, 'user3@gmail.com', 'user3', 'I', '1234'),
(15, 'root@gmail.com', 'root', 'A', '1234'),
(16, 'user5@gmail.com', 'user5', 'I', '1234');

--
-- Índices para tabelas despejadas
--

--
-- Índices para tabela `alerta`
--
ALTER TABLE `alerta`
  ADD PRIMARY KEY (`IDAlerta`);

--
-- Índices para tabela `anomalias`
--
ALTER TABLE `anomalias`
  ADD PRIMARY KEY (`IDAnomalia`);

--
-- Índices para tabela `cultura`
--
ALTER TABLE `cultura`
  ADD PRIMARY KEY (`IDCultura`),
  ADD KEY `cultura_ibfk_1` (`IDUtilizador`);

--
-- Índices para tabela `medicao`
--
ALTER TABLE `medicao`
  ADD PRIMARY KEY (`IDMedicao`);

--
-- Índices para tabela `parametrocultura`
--
ALTER TABLE `parametrocultura`
  ADD PRIMARY KEY (`IDCultura`);

--
-- Índices para tabela `utilizador`
--
ALTER TABLE `utilizador`
  ADD PRIMARY KEY (`IDUtilizador`);

--
-- AUTO_INCREMENT de tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `alerta`
--
ALTER TABLE `alerta`
  MODIFY `IDAlerta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de tabela `anomalias`
--
ALTER TABLE `anomalias`
  MODIFY `IDAnomalia` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `cultura`
--
ALTER TABLE `cultura`
  MODIFY `IDCultura` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de tabela `medicao`
--
ALTER TABLE `medicao`
  MODIFY `IDMedicao` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de tabela `utilizador`
--
ALTER TABLE `utilizador`
  MODIFY `IDUtilizador` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- Restrições para despejos de tabelas
--

--
-- Limitadores para a tabela `cultura`
--
ALTER TABLE `cultura`
  ADD CONSTRAINT `cultura_ibfk_1` FOREIGN KEY (`IDUtilizador`) REFERENCES `utilizador` (`IDUtilizador`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Limitadores para a tabela `parametrocultura`
--
ALTER TABLE `parametrocultura`
  ADD CONSTRAINT `parametrocultura_ibfk_1` FOREIGN KEY (`IDCultura`) REFERENCES `cultura` (`IDCultura`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
