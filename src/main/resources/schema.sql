CREATE TABLE IF NOT EXISTS mpa_ratings (
                                           id INTEGER PRIMARY KEY,
                                           name VARCHAR NOT NULL UNIQUE,
                                           description VARCHAR
);

CREATE TABLE IF NOT EXISTS genres (
                                      id INTEGER PRIMARY KEY,
                                      name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS films (
                                     id INTEGER PRIMARY KEY,
                                     name VARCHAR NOT NULL,
                                     description VARCHAR,
                                     release_date DATE,
                                     duration INTEGER NOT NULL,
                                     mpa_rating_id INTEGER NOT NULL,
                                     CONSTRAINT fk_mpa_rating FOREIGN KEY (mpa_rating_id) REFERENCES mpa_ratings(id)
    );

CREATE TABLE IF NOT EXISTS film_genres (
                                           film_id INTEGER NOT NULL,
                                           genre_id INTEGER NOT NULL,
                                           CONSTRAINT fk_film FOREIGN KEY (film_id) REFERENCES films(id),
    CONSTRAINT fk_genre FOREIGN KEY (genre_id) REFERENCES genres(id),
    PRIMARY KEY (film_id, genre_id)
    );

CREATE TABLE IF NOT EXISTS users (
                                     id INTEGER PRIMARY KEY,
                                     email VARCHAR NOT NULL UNIQUE,
                                     login VARCHAR NOT NULL UNIQUE,
                                     name VARCHAR,
                                     birthday DATE
);

CREATE TABLE IF NOT EXISTS film_likes (
                                          film_id INTEGER NOT NULL,
                                          user_id INTEGER NOT NULL,
                                          CONSTRAINT fk_film_like FOREIGN KEY (film_id) REFERENCES films(id),
    CONSTRAINT fk_user_like FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (film_id, user_id)
    );

CREATE TABLE IF NOT EXISTS user_friends (
                                            user_id INTEGER NOT NULL,
                                            friend_id INTEGER NOT NULL,
                                            status VARCHAR NOT NULL DEFAULT 'unconfirmed',
                                            CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_friend FOREIGN KEY (friend_id) REFERENCES users(id),
    PRIMARY KEY (user_id, friend_id)
    );
