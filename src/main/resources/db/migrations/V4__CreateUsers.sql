CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,

                       email VARCHAR(100) NOT NULL,
                       password VARCHAR(255) NOT NULL,

                       role VARCHAR(50) NOT NULL,

                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                       is_active BOOLEAN NOT NULL DEFAULT TRUE,

                       employee_id BIGINT NOT NULL,

                       CONSTRAINT fk_users_employee FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE UNIQUE INDEX ux_users_email ON users(email);

CREATE UNIQUE INDEX ux_users_employee_id ON users(employee_id);
