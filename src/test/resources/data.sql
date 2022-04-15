-- create authorities
 INSERT INTO authorities (authority) VALUES ('ROLE_USER'), ('ROLE_ADMIN');


-- create users
INSERT INTO user (email,password,first_name,last_name,balance,authority_id)
VALUES
 ('john.boyd@gmail.com', '$2a$10$ep//kwapcPwllF/btNb0kezT9u3P.7rd4qvS1ZDkhVyS8hXSgFvp.', 'John', 'Boyd', 1200, 1),
 ('jacob.boyd@gmail.com', '$2a$10$g7CQ9YN4PCNMapLVeSMiV.PWOJczSYtsxlleK9GG17zbbCfabncwa', 'Jacob', 'Boyd', 500000, 1),
 ('tenley.boyd@gmail.com', '$2a$10$NurB9uCyYg5U6T8ua.GQaOvocrc7/sEjlS04HDKMuQ5.kctk3jMmS', 'Tenley', 'Boyd', 0, 1),
 ('roger.boyd@gmail.com', '$2a$10$tarOw8cUCd97VfreFnr9fuzqAZUOuQnrdYYy7uQn5gsOsQ5LphSAy', 'Roger', 'Boyd', 1, 1),
 ('felicia.boyd@gmail.com', '$2a$10$MJXtw99t720QtWJ5IxMTi.g9L8zeEHlbbyBxauccUSOopt6EcdgDC', 'Felicia', 'Boyd', 10, 1),
 ('jonanathan.marrack@gmail.com', '$2a$10$14ALd9m3VkLEjksokzXJ3ukgilVQDSdmNyoJXOTmq49xSmb4FWh/K', 'Jonanathan', 'Marrack', 500, 1),
 ('tessa.carman@gmail.com', '$2a$10$5F8qXMNuKHiIXGG740yHq.OyhZkjAXRCJqg.NCS59kKwpOT4yiDqe', 'Tessa', 'Carman', 99999999999999, 1),
 ('peter.duncan@gmail.com', '$2a$10$l9VRMmB.UChzc56NZES7zeaa2Aec.dXWE6jVgt4R2y4SA6f2NlTcS', 'Peter', 'Duncan', 0, 1),
 ('foster.shepard@gmail.com', '$2a$10$1nigUZqq2HK1vm2f19jYW.4.S2WO9T7JHEw2e0z1KP9WjVfnOWDhW', 'Foster', 'Shepard', 600000, 1),
 ('tony.cooper@gmail.com', '$2a$10$R4QEX3gV.bfmVHIeL6sK6ubLSLiYYpq818gxvShL9Q5ynGwoMVaAC', 'Tony', 'Cooper', 100000, 1),
 ('lily.cooper@gmail.com', '$2a$10$SCZuBsEs0zgOQMWSrDgYUux5jDUVwk3OZKhnHp3Tfelbl02DTjF/q', 'Lily', 'Cooper', 10000, 1),
 ('sophia.zemicks@gmail.com', '$2a$10$lHjqTIa6i8/3n3Wvd20BceiQWpRApvVFpFp/rFHRYQlPyu5HVUhx.', 'Sophia', 'Zemicks', 0, 1),
 ('warren.zemicks@gmail.com', '$2a$10$ALfCNufG5Y82Snuk6Gp7EeFTevYQTycL7vFQ0r.g6Yzq2hP8M3rge', 'Warren', 'Zemicks', 0, 1),
 ('zach.zemicks@gmail.com', '$2a$10$Z0mvJVraoifnxwVyB2rtnu6NvwpDbAoH9atoa6dfVvs14iSfck0.W', 'Zach', 'Zemicks', 1234, 1),
 ('reginold.walker@gmail.com', '$2a$10$R6iHakWRoihLIFvIcYeQc.om.SQb0mgZ1m2H4rxv3OfNqCzJzAly6', 'Reginold', 'Walker', 456789, 1),
 ('jamie.peters@gmail.com', '$2a$10$8XwZtwGb6kWdQau0BjBw7uA4hXBJyr18WBw7Jj06FJCghpgA9tSE2', 'Jamie', 'Peters', 0, 1),
 ('ron.peters@gmail.com', '$2a$10$C4H2xKz8bRXl4cmxTHV91u5KzjSEH5r4tufH9sK0tbR57ivjrBphW', 'Ron', 'Peters', 0, 1),
 ('allison.boyd@gmail.com', '$2a$10$7euwEwKZgsFGHCFOofYBouNVe6rz40hD3IzXrbupf7KrjCiLpc2Ki', 'Allison', 'Boyd', 0, 1),
 ('brian.stelzer@gmail.com', '$2a$10$HHb2uH5WVPaIZm.cyFCJ6.WD7daD9fj775U..v2KfFfRqmZuxaHHO', 'Brian', 'Stelzer', 0, 1),
 ('shawna.stelzer@gmail.com', '$2a$10$dx5PHc/kr9YRBBTUP2KhbuRuzZc6etWFhvXkoGsEmENjt1lDJ0Dgq', 'Shawna', 'Stelzer', 0, 1),
 ('kendrik.stelzer@gmail.com', '$2a$10$sUNmE3i0oD.Qzq2s30/AU.uEOxbfL5SVEt.dSuaIP9tC23o0zR.4i', 'Kendrik', 'Stelzer', 0, 1),
 ('clive.ferguson@gmail.com', '$2a$10$V2ExbXjL855qy3Sppzf3ve2nm3aF1fKPrYeSsoxho5Nna99spZzNG', 'Clive', 'Ferguson', 0, 1),
 ('eric.cadigan@gmail.com', '$2a$10$a2S3R7cquVGhGty1VMmLQe4bc4eHacHzC.Jc0KWsPB74aOi54wHOC', 'Eric', 'Cadigan', 0, 1);

 -- create connections
 INSERT INTO user_connection_assoc  (user_id, connection_id)
 VALUES
 (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9),(1, 10), (1, 11),
 (2, 1), (2, 23), (2, 22), (2, 21), (2, 20),
 (3, 1),
 (4,1), (4,16),
 (5,1), (5,10), (5,20);

 -- create bank accounts
 INSERT INTO bank_account (iban, description, user_id)
 VALUES
 ('FR01234567890123456789012', 'HSBC', 1),
 ('FR01234567890123456741852', 'BNP', 1),
 ('FR01234567894561894777777', 'Societe generale', 1),
 ('FR01238915948312894785555', 'Banque populaire centre', 1),
 ('FR01123156418312894733333', 'La banque postale', 2),
 ('FR01121645485312894788888', 'CIC', 2),
 ('FR01129999999999999999999', 'CA', 4),
 ('FR01129999999999999999989', 'CA', 5);

 -- create bank transfers
 INSERT INTO bank_transfer (date,amount,description,bank_account_id)
 VALUES
 ('2022-04-01 05:40:56.000', 1000, "Credit MyBuddy account", 1),
 ('2022-04-02 06:40:56.000', 500, "Debit MyBuddy account", 2);

 -- create transactions
 INSERT INTO transaction (date,total_amount,description,fee_amount,payer_id,credit_id)
 VALUES
 ('2022-01-01 05:40:56.000', 5000, 'Cinema tickets', 250, 1, 2),
 ('2022-01-05 14:40:56.000', 2620, 'Restaurant', 131, 3, 1),
 ('2022-01-15 20:32:43.000', 9999999, 'Ferrari testarossa ', 500000, 1, 4),
 ('2022-02-02 12:32:43.000', 80, 'Croissant', 4, 4, 1),
 ('2022-02-03 23:32:43.000', 10000, 'Chaussure de course', 500, 2, 1),
 ('2022-02-04 13:12:13.000', 4725, 'Tournee au bar La Fabrique', 236, 1, 3),
 ('2022-02-04 13:32:43.000', 25664, 'Tableau de maitre', 1283, 2, 1),
 ('2022-02-05 13:33:41.000', 1234567891234, 'Detournement de fond', 61728394562, 1, 5),
 ('2022-03-05 15:33:41.000', 1500, 'Repas Mc do', 75, 4, 1),
 ('2022-03-05 15:33:41.000', 4548, 'Plein essence', 227, 3, 1);