-- Musical 테이블 생성
CREATE TABLE Musical
(
    id            VARCHAR(10) PRIMARY KEY,
    title         VARCHAR(50) NOT NULL,
    theater       VARCHAR(50),
    posterImage   VARCHAR(255),
    stDate        DATE,
    edDate        DATE,
    viewAge       VARCHAR(30),
    runningTime   VARCHAR(10),
    mainCharacter VARCHAR(20)
);

-- Actor 테이블 생성
CREATE TABLE Actor
(
    id           VARCHAR(6) PRIMARY KEY,
    name         VARCHAR(20) NOT NULL,
    profileImage VARCHAR(255)
);

-- Casting 테이블 생성
CREATE TABLE Casting
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    musical_id VARCHAR(10),
    actor_id   VARCHAR(6),
    role       VARCHAR(20) NOT NULL,
    FOREIGN KEY (musical_id) REFERENCES Musical (id),
    FOREIGN KEY (actor_id) REFERENCES Actor (id)
);