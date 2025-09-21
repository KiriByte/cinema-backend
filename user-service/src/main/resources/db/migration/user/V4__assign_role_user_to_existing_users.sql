INSERT INTO user_service_schema.user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM user_service_schema.app_users u
         INNER JOIN user_service_schema.roles r ON r.name = 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1
                  FROM user_service_schema.user_roles ur
                  WHERE ur.user_id = u.user_id);