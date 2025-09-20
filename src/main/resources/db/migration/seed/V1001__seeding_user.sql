INSERT INTO users VALUES
(
    uuid(),
    "dummy",
    "Dummy",
    "$2a$12$.OPt1No8kXcx8ApdTKlbfeZ51y1cHqrfPMTfkwIyt2rSCWHs1URoG",
    null,
    (SELECT id FROM roles WHERE name = "ADMIN"),
    now(),
    now(),
    null
);