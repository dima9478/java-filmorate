-- film total info
SELECT f.*, r.name as rating, array_agg(g.name) as genres
FROM films f
         INNER JOIN ratings r USING (rating_id)
         LEFT JOIN film_genres fg USING (film_id)
         LEFT JOIN genres g USING (genre_id)
WHERE film_id = 1
GROUP BY film_id, r.name;

-- most popular films
SELECT f.*, COUNT(*) as likes_count
FROM films f
         LEFT JOIN film_likes USING (film_id)
GROUP BY film_id
ORDER BY likes_count DESC
LIMIT 10;

-- user total info
SELECT *,
       (SELECT array_agg(CASE WHEN user_id = u.user_id THEN friend_id ELSE user_id END)
        FROM friendships
        WHERE u.user_id = ANY (ARRAY [user_id, friend_id])
          AND confirmed) as friends
FROM users u
WHERE user_id = 1;


-- list of common friends
SELECT friend_id
FROM (SELECT CASE
                 WHEN user_id = 2 THEN friend_id
                 ELSE user_id
                 END AS friend_id
      FROM friendships
      WHERE 2 = ANY (ARRAY [user_id, friend_id])
        AND confirmed) AS f1
INTERSECT
SELECT friend_id
FROM (SELECT CASE
                 WHEN user_id = 3 THEN friend_id
                 ELSE user_id
                 END AS friend_id
      FROM friendships
      WHERE 3 = ANY (ARRAY [user_id, friend_id])
        AND confirmed) AS f2;

