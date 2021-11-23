CREATE TABLE IF NOT EXISTS `players` (
    `id` INT PRIMARY KEY,
    `nickname` VARCHAR(60),
    `createdAt` DATE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS `logs` (
    `id` INT PRIMARY KEY,
    `userId` FOREIGN KEY REFERENCES players(id) ON CASCADE DELETE,
    `points` DOUBLE,
    `reason` VARCHAR(255),
    `createdAt` DATE DEFAULT now()
);