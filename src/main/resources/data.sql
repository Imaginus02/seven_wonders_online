-- SQL script to initialize card data
-- Now with min_player_count to specify how many players needed for this card
INSERT INTO cards (name, type, age, cost, coin_cost, min_player_count, incoming_links, outgoing_links, image) VALUES
-- Cards for 3+ players

    -- Brown Resource Cards
('Lumber Yard', 'BROWN', 'AGE_I', '{}', 0, 3, '[]', '[]', 'lumberyard.png'),
('Clay Pool', 'BROWN', 'AGE_I', '{}', 0, 3, '[]', '[]', 'claypool.png'),
('Stone Pit', 'BROWN', 'AGE_I', '{}', 0, 3, '[]', '[]', 'stonepit.png'),
('Ore Vein', 'BROWN', 'AGE_I', '{}', 0, 3, '[]', '[]', 'orevein.png'),
('Timber Yard', 'BROWN', 'AGE_I', '{}', 0, 3, '[]', '[]', 'timberyard.png'),
('CLay Pit', 'BROWN', 'AGE_I', '{}', 0, 3, '[]', '[]', 'claypit.png'),

('Sawmill', 'BROWN', 'AGE_II', '{}', 1, 3, '[]', '[]', 'sawmill.png'),
('Foundry', 'BROWN', 'AGE_II', '{}', 1, 3, '[]', '[]', 'foundry.png'),
('Quarry', 'BROWN', 'AGE_II', '{}', 1, 3, '[]', '[]', 'quarry.png'),
('Brickyard', 'BROWN', 'AGE_II', '{}', 1, 3, '[]', '[]', 'brickyard.png'),

    -- Grey Manufactured Goods Cards
('Glassworks', 'GREY', 'AGE_I', '{}', 0, 3, '[]', '[]', 'glassworks.png'),
('Press', 'GREY', 'AGE_I', '{}', 0, 3, '[]', '[]', 'press.png'),
('Loom', 'GREY', 'AGE_I', '{}', 0, 3, '[]', '[]', 'loom.png'),

('Glassworks', 'GREY', 'AGE_II', '{}', 0, 3, '[]', '[]', 'glassworks.png'),
('Press', 'GREY', 'AGE_II', '{}', 0, 3, '[]', '[]', 'press.png'),
('Loom', 'GREY', 'AGE_II', '{}', 0, 3, '[]', '[]', 'loom.png'),


    -- Blue Victory Point Cards
('Altar', 'BLUE', 'AGE_I', '{}', 0, 3, '[]', '[]', 'altar.png'),
('Theater', 'BLUE', 'AGE_I', '{}', 0, 3, '[]', '[]', 'theater.png'),
('Bath', 'BLUE', 'AGE_I', '{"STONE":1}', 0, 3, '[]', '[]', 'baths.png'),

('Courthouse', 'BLUE', 'AGE_II', '{"BRICK":2,"TEXTILE":1}', 0, 3, '[]', '[]', 'courthouse.png'),
('Temple', 'BLUE', 'AGE_II', '{"WOOD":1,"BRICK":1,"GLASS":1}', 0, 3, '[]', '[]', 'temple.png'),
('Statue', 'BLUE', 'AGE_II', '{"ORE":2,"WOOD":1}', 0, 3, '[]', '[]', 'statue.png'),
('Aqueduct', 'BLUE', 'AGE_II', '{"STONE":3}', 0, 3, '[]', '[]', 'aqueduct.png'),

('Gardens', 'BLUE', 'AGE_III', '{"BRICK":2,"WOOD":1}', 0, 3, '[]', '[]', 'gardens.png'),
('Senate', 'BLUE', 'AGE_III', '{"WOOD":2,"STONE":1,"ORE":1}', 0, 3, '[]', '[]', 'senate.png'),
('Town Hall', 'BLUE', 'AGE_III', '{"STONE":3,"GLASS":1}', 0, 3, '[]', '[]', 'townhall.png'),
('Pantheon', 'BLUE', 'AGE_III', '{"BRICK":2,"ORE":1,"GLASS":1,"TEXTILE":1,"PAPER":1}', 0, 3, '[]', '[]', 'pantheon.png'),
('Palace', 'BLUE', 'AGE_III', '{"WOOD":1,"STONE":1,"BRICK":1,"ORE":1,"PAPER":1,"TEXTILE":1,"GLASS":1}', 0, 3, '[]', '[]', 'palace.png'),

    -- Yellow Commerce Cards
('West Trading Post', 'YELLOW', 'AGE_I', '{}', 0, 3, '[]', '[]', 'westtradingpost.png'),
('East Trading Post', 'YELLOW', 'AGE_I', '{}', 0, 3, '[]', '[]', 'easttradingpost.png'),
('Marketplace', 'YELLOW', 'AGE_I', '{}', 0, 3, '[]', '[]', 'marketplace.png'),

('Caravansery', 'YELLOW', 'AGE_II', '{"WOOD":2}', 0, 3, '[]', '[]', 'caravansery.png'),
('Forum', 'YELLOW', 'AGE_II', '{"BRICK":2}', 0, 3, '[]', '[]', 'forum.png'),
('Vineyard', 'YELLOW', 'AGE_II', '{}', 0, 3, '[]', '[]', 'vineyard.png'),

('Lighthouse', 'YELLOW', 'AGE_III', '{"STONE":1,"GLASS":1}', 0, 3, '[]', '[]', 'lighthouse.png'),
('Haven', 'YELLOW', 'AGE_III', '{"WOOD":1,"ORE":1,"TEXTILE":1}', 0, 3, '[]', '[]', 'haven.png'),


    -- Red military Cards
('Guard Tower', 'RED', 'AGE_I', '{"BRICK":1}', 0, 3, '[]', '[]', 'guardtower.png'),
('Barracks', 'RED', 'AGE_I', '{"ORE":1}', 0, 3, '[]', '[]', 'barracks.png'),
('Stockade', 'RED', 'AGE_I', '{"WOOD":1}', 0, 3, '[]', '[]', 'stockade.png'),

('Stables', 'RED', 'AGE_II', '{"WOOD":1,"ORE":1,"BRICK":1}', 0, 3, '[]', '[]', 'stables.png'),
('Archery Range', 'RED', 'AGE_II', '{"WOOD":2,"ORE":1}', 0, 3, '[]', '[]', 'archeryrange.png'),
('Walls', 'RED', 'AGE_II', '{"STONE":3}', 0, 3, '[]', '[]', 'walls.png'),

('Arsenal', 'RED', 'AGE_III', '{"WOOD":2,"ORE":1,"TEXTILE":1}', 0, 3, '[]', '[]', 'arsenal.png'),
('Siege Workshop', 'RED', 'AGE_III', '{"BRICK":3,"STONE":1}', 0, 3, '[]', '[]', 'siegeworkshop.png'),
('Fortifications', 'RED', 'AGE_III', '{"ORE":3,"BRICK":1}', 0, 3, '[]', '[]', 'fortifications.png'),


    -- Green Science Cards
('Scriptorium', 'GREEN', 'AGE_I', '{"PAPER":1}', 0, 3, '[]', '[]', 'scriptorium.png'),
('Apothecary', 'GREEN', 'AGE_I', '{"TEXTILE":1}', 0, 3, '[]', '[]', 'apothecary.png'),
('Workshop', 'GREEN', 'AGE_I', '{"GLASS":1}', 0, 3, '[]', '[]', 'workshop.png'),

('Dispensary', 'GREEN', 'AGE_II', '{"ORE":2,"GLASS":1}', 0, 3, '[]', '[]', 'dispensary.png'),
('Laboratory', 'GREEN', 'AGE_II', '{"BRICK":2,"PAPER":1}', 0, 3, '[]', '[]', 'laboratory.png'),
('Library', 'GREEN', 'AGE_II', '{"STONE":2,"TEXTILE":1}', 0, 3, '[]', '[]', 'library.png'),
('School', 'GREEN', 'AGE_II', '{"WOOD":1,"PAPER":1}', 0, 3, '[]', '[]', 'school.png'),

('University', 'GREEN', 'AGE_III', '{"WOOD":2,"GLASS":1,"PAPER":1}', 0, 3, '[]', '[]', 'university.png'),
('Study', 'GREEN', 'AGE_III', '{"WOOD":1,"TEXTILE":1,"PAPER":1}', 0, 3, '[]', '[]', 'study.png'),
('Lodge', 'GREEN', 'AGE_III', '{"BRICK":2,"TEXTILE":1,"PAPER":1}', 0, 3, '[]', '[]', 'lodge.png'),
('Academy', 'GREEN', 'AGE_III', '{"STONE":3,"GLASS":1}', 0, 3, '[]', '[]', 'academy.png'),
('Observatory', 'GREEN', 'AGE_III', '{"ORE":2,"GLASS":1,"TEXTILE":1}', 0, 3, '[]', '[]', 'observatory.png'),


    -- Purple Guild Cards
('Workers Guild', 'PURPLE', 'AGE_III', '{"ORE":2,"WOOD":1,"STONE":1,"BRICK":1}', 0, 3, '[]', '[]', 'workersguild.png'),
('Craftsmens Guild', 'PURPLE', 'AGE_III', '{"STONE":2,"ORE":1}', 0, 3, '[]', '[]', 'craftsmensguild.png'),
('Magistrates Guild', 'PURPLE', 'AGE_III', '{"WOOD":3,"STONE":1,"TEXTILE":1}', 0, 3, '[]', '[]', 'magistratesguild.png'),
('Traders Guild', 'PURPLE', 'AGE_III', '{"TEXTILE":1,"GLASS":1,"PAPER":1}', 0, 3, '[]', '[]', 'tradersguild.png'),
('Spies Guild', 'PURPLE', 'AGE_III', '{"BRICK":2,"GLASS":1}', 0, 3, '[]', '[]', 'spiesguild.png'),
('Philosophers Guild', 'PURPLE', 'AGE_III', '{"BRICK":3,"TEXTILE":1,"PAPER":1}', 0, 3, '[]', '[]', 'philosophersguild.png'),
('Shipowners Guild', 'PURPLE', 'AGE_III', '{"WOOD":3,"GLASS":1,"PAPER":1}', 0, 3, '[]', '[]', 'shipownersguild.png'),
('Scientists Guild', 'PURPLE', 'AGE_III', '{"WOOD":2,"ORE":2,"PAPER":1}', 0, 3, '[]', '[]', 'scientistsguild.png'),
('Decorators Guild', 'PURPLE', 'AGE_III', '{"ORE":2,"STONE":1,"TEXTILE":1}', 0, 3, '[]', '[]', NULL),
('Builders Guild', 'PURPLE', 'AGE_III', '{"STONE":3,"BRICK":1,"GLASS":1}', 0, 3, '[]', '[]', 'buildersguild.png'),


-- Example of duplicate cards for different player counts
-- Lumber Yard can appear multiple times for different player counts
('Lumber Yard', 'BROWN', 'AGE_I', '{}', 0, 5, '[]', '[]', 'lumberyard.png'),
('Clay Pool', 'BROWN', 'AGE_I', '{}', 0, 5, '[]', '[]', 'claypool.png'),

('Chamber of Commerce', 'YELLOW', 'AGE_III', '{"BRICK":2,"PAPER":1}', 0, 4, '[]', '[]', 'chamberofcommerce.png');
