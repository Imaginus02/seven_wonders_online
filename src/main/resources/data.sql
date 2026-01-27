-- SQL script to initialize card data
INSERT INTO cards (name, type, age, cost, coin_cost, incoming_links, outgoing_links, effect) VALUES
('Lumber Yard', 'BROWN', 'AGE_I', '{}', 0, '[]', '[]', 'PRODUCE_WOOD'),
('Clay Pool', 'BROWN', 'AGE_I', '{"clay":1}', 0, '[]', '[]', 'PRODUCE_CLAY'),
('Stone Pit', 'BROWN', 'AGE_I', '{"stone":1}', 0, '[]', '[]', 'PRODUCE_STONE'),
('Ore Vein', 'BROWN', 'AGE_I', '{"ore":1}', 0, '[]', '[]', 'PRODUCE_ORE'),
('Glassworks', 'GREY', 'AGE_I', '{"glass":1}', 0, '[]', '[]', 'PRODUCE_GLASS'),
('Press', 'GREY', 'AGE_I', '{"papyrus":1}', 0, '[]', '[]', 'PRODUCE_PAPYRUS');