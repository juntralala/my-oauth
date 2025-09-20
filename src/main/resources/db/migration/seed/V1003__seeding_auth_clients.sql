INSERT INTO auth_clients VALUES (
    uuid(),
    "client_id",
    "http://localhost/callback",
    "profile",
    "client_secret",
    true,
    now(),
    now(),
    null
);

INSERT INTO auth_clients_grant_types VALUES (
    (SELECT id FROM auth_clients WHERE client_id = "client_id" LIMIT 1),
    (SELECT id FROM grant_types WHERE name = "code" LIMIT 1),
    now()
);