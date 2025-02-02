DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS friendships CASCADE;
DROP TABLE IF EXISTS mpa_rating CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS Likes CASCADE;
-- Таблица для рейтингов MPA
CREATE TABLE IF NOT EXISTS mpa_rating
(
    mpa_raiting_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name           VARCHAR(255) NOT NULL
);

-- Таблица для пользователей
CREATE TABLE IF NOT EXISTS users
(
    user_id  INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    login    VARCHAR(255) NOT NULL UNIQUE,
    email    VARCHAR(255) NOT NULL UNIQUE,
    birthday DATE         NOT NULL,
    name     VARCHAR(255) NOT NULL
);

-- Таблица для фильмов
CREATE TABLE IF NOT EXISTS films
(
    film_id        INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    description    TEXT         NOT NULL,
    release_date   DATE         NOT NULL,
    duration       INT          NOT NULL CHECK (duration > 0),
    mpa_raiting_id INT          NOT NULL,
    FOREIGN KEY (mpa_raiting_id) REFERENCES mpa_rating (mpa_raiting_id) ON DELETE CASCADE
);

-- Таблица для жанров
CREATE TABLE IF NOT EXISTS genres
(
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     VARCHAR(255) NOT NULL
);

-- Таблица для дружбы между пользователями
CREATE TABLE IF NOT EXISTS friendships
(
    user_id   INT NOT NULL,
    friend_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, friend_id)
);

-- Таблица для связки фильмов и жанров
CREATE TABLE IF NOT EXISTS film_genres
(
    film_genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id       INT NOT NULL,
    genre_id      INT NOT NULL,
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres (genre_id) ON DELETE CASCADE
);

-- Таблица для лайков фильмов
CREATE TABLE IF NOT EXISTS Likes
(
    likes_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id  INT NOT NULL,
    film_id  INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE
);
