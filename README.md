# java-filmorate
Template repository for Filmorate project.

Table films {
  id integer [primary key]
  name varchar [not null, note: 'Название фильма']
  description varchar [note: 'Описание фильма, максимум 200 символов']
  release_date date [note: 'Дата релиза фильма']
  duration integer [not null, note: 'Продолжительность фильма в минутах']
  mpa_rating_id integer [not null, ref: > mpa_ratings.id, note: 'Ссылка на таблицу рейтингов MPA']
}

Table genres {
  id integer [primary key]
  name varchar [not null, unique, note: 'Название жанра']
}

Table film_genres {
  film_id integer [not null, ref: > films.id]
  genre_id integer [not null, ref: > genres.id]
}

Table mpa_ratings {
  id integer [primary key]
  name varchar [not null, unique, note: 'Код рейтинга (G, PG, PG-13, R, NC-17)']
  description varchar [note: 'Описание возрастного ограничения']
}

Table users {
  id integer [primary key]
  email varchar [not null, unique, note: 'Электронная почта пользователя']
  login varchar [not null, unique, note: 'Логин пользователя']
  name varchar [note: 'Имя пользователя']
  birthday date [note: 'Дата рождения пользователя']
}

Table film_likes {
  film_id integer [not null, ref: > films.id]
  user_id integer [not null, ref: > users.id]
}

Table user_friends {
  user_id integer [not null, ref: > users.id]
  friend_id integer [not null, ref: > users.id]
  status varchar [not null, default: 'unconfirmed', note: 'Статус дружбы: confirmed или unconfirmed']
}
