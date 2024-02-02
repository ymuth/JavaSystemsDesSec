# COM2008 group work

## Requirements
- Java 17
- Maven 3.6.3

## SQL table definitions and initial value insertions

### How to add Manager (after creating db)
`INSERT INTO UserRoles VALUES('userID of Manager', '2');`

**Create all the tables and insert initial values**

### Addresses
```
DROP TABLE IF EXISTS Addresses;     
CREATE TABLE Addresses (
  addressID varchar(10) NOT NULL,   
  roadName varchar(45) DEFAULT NULL,
  cityName varchar(45) DEFAULT NULL,
  houseNumber int DEFAULT NULL,
  postCode varchar(7) DEFAULT NULL,
  PRIMARY KEY (addressID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### Users
```
DROP TABLE IF EXISTS Users;
CREATE TABLE Users (
  userID int NOT NULL AUTO_INCREMENT,
  email varchar(45) NOT NULL,
  password varchar(64) NOT NULL,
  forename varchar(15) DEFAULT NULL,
  surname varchar(15) DEFAULT NULL,
  addressID varchar(10) DEFAULT NULL,
  PRIMARY KEY (userID),
  UNIQUE KEY userID_UNIQUE (userID),
  KEY addressID_idx (addressID),
  CONSTRAINT fk_Users_Addresses FOREIGN KEY (addressID) REFERENCES Addresses (addressID) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```

### BankDetails
```
DROP TABLE IF EXISTS BankDetails;
CREATE TABLE BankDetails (
  cardNumber varchar(45) NOT NULL,
  cardName varchar(45) NOT NULL,
  expiryDate varchar(45) NOT NULL,
  securityCode varchar(45) NOT NULL,
  PRIMARY KEY (cardNumber)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### Roles + insert initial values
```
DROP TABLE IF EXISTS Roles;
CREATE TABLE Roles (
  roleID int NOT NULL,
  roleName varchar(15) DEFAULT NULL,
  PRIMARY KEY (roleID),
  UNIQUE KEY roleID_UNIQUE (roleID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES Roles WRITE;
INSERT INTO Roles VALUES (0,'Customer'),(1,'Admin'),(2,'Manager');
UNLOCK TABLES;
```

### Categories + insert initial values
```
DROP TABLE IF EXISTS Categories;
CREATE TABLE Categories (
  categoryID int NOT NULL,
  categoryNames varchar(255) NOT NULL,
  PRIMARY KEY (categoryID),
  UNIQUE KEY catergoryID_UNIQUE (categoryID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES Categories WRITE;
INSERT INTO Categories VALUES (0,'Locomotives'),(1,'RollingStocks'),(2,'Controllers'),(3,'TrackPieces'),(4,'TrainSets'),(5,'TrackPacks');
UNLOCK TABLES;
```

### UserBankDetails
```
DROP TABLE IF EXISTS UserBankDetails;
CREATE TABLE UserBankDetails (
  userID int NOT NULL,
  cardNumber varchar(45) NOT NULL,
  PRIMARY KEY (userID,cardNumber),
  KEY fk_UserBankDetails_BankDetails_idx (cardNumber),
  CONSTRAINT fk_UserBankDetails_BankDetails FOREIGN KEY (cardNumber) REFERENCES BankDetails (cardNumber) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_UserBankDetails_Users FOREIGN KEY (userID) REFERENCES Users (userID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### UserRoles
```
DROP TABLE IF EXISTS UserRoles;
CREATE TABLE UserRoles (
  userID int NOT NULL,
  roleID int NOT NULL,
  PRIMARY KEY (userID,roleID),
  KEY roleID_idx (roleID),
  CONSTRAINT fk_UserRoles_Roles FOREIGN KEY (roleID) REFERENCES Roles (roleID),
  CONSTRAINT fk_UserRoles_Users FOREIGN KEY (userID) REFERENCES Users (userID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### Orders
```
DROP TABLE IF EXISTS Orders;
CREATE TABLE Orders (
  orderNo int NOT NULL AUTO_INCREMENT,
  userID int NOT NULL,
  date bigint NOT NULL,
  orderQueueNo int DEFAULT NULL,
  status enum('PENDING','CONFIRMED','FULFILLED') NOT NULL DEFAULT 'PENDING',
  PRIMARY KEY (orderNo),
  UNIQUE KEY OrderNo_UNIQUE (orderNo),
  KEY fk_Orders_Users (userID),
  CONSTRAINT fk_Orders_Users FOREIGN KEY (userID) REFERENCES Users (userID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### Products
```
DROP TABLE IF EXISTS Products;
CREATE TABLE Products (
  productCode varchar(255) NOT NULL,
  productBrand varchar(255) NOT NULL,
  productName varchar(255) NOT NULL,
  gauge varchar(45) NOT NULL,
  cost decimal(16,2) NOT NULL,
  stock int NOT NULL,
  categoryID int NOT NULL,
  eraCode varchar(45) DEFAULT NULL,
  dccCode varchar(45) DEFAULT NULL,
  digital tinyint(1) DEFAULT NULL,
  packageContent varchar(255) DEFAULT NULL,
  PRIMARY KEY (productCode),
  KEY fk_Products_Category_idx (categoryID),
  CONSTRAINT fk_Products_Categories FOREIGN KEY (categoryID) REFERENCES Categories (categoryID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### OrderLines
```
DROP TABLE IF EXISTS OrderLines;
CREATE TABLE OrderLines (
  orderNo int NOT NULL,
  orderLineNo int NOT NULL,
  productCode varchar(45) NOT NULL,
  quantity int DEFAULT NULL,
  totalCost decimal(16,2) DEFAULT NULL,
  PRIMARY KEY (orderNo,orderLineNo)
  KEY fk_OrderLines_Products_idx (productCode),
  CONSTRAINT fk_OrderLines_Orders FOREIGN KEY (orderNo) REFERENCES Orders (orderNo) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_OrderLines_Products FOREIGN KEY (productCode) REFERENCES Products (productCode) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```
