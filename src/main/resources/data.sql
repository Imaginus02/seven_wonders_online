-- SQL script to initialize card data
-- Now with min_player_count to specify how many players needed for this card
INSERT INTO cards (name, type, age, cost, coin_cost, min_player_count, incoming_links, outgoing_links, image) VALUES
-- Cards for 3+ players

    -- Brown Resource Cards
('Lumber Yard', 'BROWN', 'AGE_I', '{}', 0, 3, '[]', '[]', 'lumberyard.png'),
('Clay Pool', 'BROWN', 'AGE_I', '{}', 0, 3, '[]', '[]', 'claypool.png'),
('Stone Pit', 'BROWN', 'AGE_I', '{}', 0, 3, '[]', '[]', 'stonepit.png'),
('Ore Vein', 'BROWN', 'AGE_I', '{}', 0, 3, '[]', '[]', 'orevein.png'),
('Timber Yard', 'BROWN', 'AGE_I', '{}', 1, 3, '[]', '[]', 'timberyard.png'),
('Clay Pit', 'BROWN', 'AGE_I', '{}', 1, 3, '[]', '[]', 'claypit.png'),

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
('Workers Guild', 'VIOLET', 'AGE_III', '{"ORE":2,"WOOD":1,"STONE":1,"BRICK":1}', 0, 3, '[]', '[]', 'workersguild.png'),
('Craftsmens Guild', 'VIOLET', 'AGE_III', '{"STONE":2,"ORE":1}', 0, 3, '[]', '[]', 'craftsmensguild.png'),
('Magistrates Guild', 'VIOLET', 'AGE_III', '{"WOOD":3,"STONE":1,"TEXTILE":1}', 0, 3, '[]', '[]', 'magistratesguild.png'),
('Traders Guild', 'VIOLET', 'AGE_III', '{"TEXTILE":1,"GLASS":1,"PAPER":1}', 0, 3, '[]', '[]', 'tradersguild.png'),
('Spies Guild', 'VIOLET', 'AGE_III', '{"BRICK":2,"GLASS":1}', 0, 3, '[]', '[]', 'spiesguild.png'),
('Philosophers Guild', 'VIOLET', 'AGE_III', '{"BRICK":3,"TEXTILE":1,"PAPER":1}', 0, 3, '[]', '[]', 'philosophersguild.png'),
('Shipowners Guild', 'VIOLET', 'AGE_III', '{"WOOD":3,"GLASS":1,"PAPER":1}', 0, 3, '[]', '[]', 'shipownersguild.png'),
('Scientists Guild', 'VIOLET', 'AGE_III', '{"WOOD":2,"ORE":2,"PAPER":1}', 0, 3, '[]', '[]', 'scientistsguild.png'),
('Decorators Guild', 'VIOLET', 'AGE_III', '{"ORE":2,"STONE":1,"TEXTILE":1}', 0, 3, '[]', '[]', NULL),
('Builders Guild', 'VIOLET', 'AGE_III', '{"STONE":3,"BRICK":1,"GLASS":1}', 0, 3, '[]', '[]', 'buildersguild.png'),


-- Example of duplicate cards for different player counts
-- Lumber Yard can appear multiple times for different player counts
('Lumber Yard', 'BROWN', 'AGE_I', '{}', 0, 5, '[]', '[]', 'lumberyard.png'),
('Clay Pool', 'BROWN', 'AGE_I', '{}', 0, 5, '[]', '[]', 'claypool.png'),

('Chamber of Commerce', 'YELLOW', 'AGE_III', '{"BRICK":2,"PAPER":1}', 0, 4, '[]', '[]', 'chamberofcommerce.png');


-- EFFECTS TABLE - All card and wonder effects
-- Parameters encoding:
-- VP:n = Victory Points, COINS:n = Coins, MIL:n = Military
-- Resource: index:count (0=STONE,1=WOOD,2=ORE,3=BRICK,4=GLASS,5=PAPER,6=TEXTILE,7=MUTABLE_BASE,8=MUTABLE_ADVANCED)
-- Science: index:count (9=TABLET,10=COMPASS,11=GEAR,12=MUTABLE)
-- Price: side:price (0=LEFT,1=RIGHT,2=BOTH)
-- Special: keyword (VINEYARD, BUILD_FROM_DISCARD, etc.)
INSERT INTO effects (effect_id, description, timing, parameters) VALUES

-- BROWN CARDS (Resources) - IMMEDIATE
('LUMBER_YARD_WOOD_1', 'Lumber Yard: +1 Wood', 'IMMEDIATE', '1:1'),
('CLAY_POOL_BRICK_1', 'Clay Pool: +1 Brick', 'IMMEDIATE', '3:1'),
('STONE_PIT_STONE_1', 'Stone Pit: +1 Stone', 'IMMEDIATE', '0:1'),
('ORE_VEIN_ORE_1', 'Ore Vein: +1 Ore', 'IMMEDIATE', '2:1'),
('TIMBER_YARD_STONE_WOOD_1', 'Timber Yard: +1 Stone/Wood', 'IMMEDIATE', '0_1:1'),
('CLAY_PIT_ORE_BRICK_1', 'Clay Pit: +1 Ore/Brick', 'IMMEDIATE', '2_3:1'),
('SAWMILL_WOOD_2', 'Sawmill: +2 Wood', 'IMMEDIATE', '1:2'),
('FOUNDRY_ORE_2', 'Foundry: +2 Ore', 'IMMEDIATE', '2:2'),
('QUARRY_STONE_2', 'Quarry: +2 Stone', 'IMMEDIATE', '0:2'),
('BRICKYARD_BRICK_2', 'Brickyard: +2 Brick', 'IMMEDIATE', '3:2'),

-- GREY CARDS (Manufactured Goods) - IMMEDIATE
('GLASSWORKS_GLASS_1', 'Glassworks: +1 Glass', 'IMMEDIATE', '4:1'),
('PRESS_PAPER_1', 'Press: +1 Paper', 'IMMEDIATE', '5:1'),
('LOOM_TEXTILE_1', 'Loom: +1 Textile', 'IMMEDIATE', '6:1'),

-- BLUE CARDS (Victory Points) - IMMEDIATE
('ALTAR_VP_2', 'Altar: +2 VP', 'IMMEDIATE', 'VP:2'),
('THEATER_VP_2', 'Theater: +2 VP', 'IMMEDIATE', 'VP:2'),
('BATH_VP_3', 'Bath: +3 VP', 'IMMEDIATE', 'VP:3'),
('COURTHOUSE_VP_4', 'Courthouse: +4 VP', 'IMMEDIATE', 'VP:4'),
('TEMPLE_VP_3', 'Temple: +3 VP', 'IMMEDIATE', 'VP:3'),
('STATUE_VP_4', 'Statue: +4 VP', 'IMMEDIATE', 'VP:4'),
('AQUEDUCT_VP_5', 'Aqueduct: +5 VP', 'IMMEDIATE', 'VP:5'),
('GARDENS_VP_5', 'Gardens: +5 VP', 'IMMEDIATE', 'VP:5'),
('SENATE_VP_6', 'Senate: +6 VP', 'IMMEDIATE', 'VP:6'),
('TOWN_HALL_VP_6', 'Town Hall: +6 VP', 'IMMEDIATE', 'VP:6'),
('PANTHEON_VP_7', 'Pantheon: +7 VP', 'IMMEDIATE', 'VP:7'),
('PALACE_VP_8', 'Palace: +8 VP', 'IMMEDIATE', 'VP:8'),

-- YELLOW CARDS (Trading) - IMMEDIATE & END_OF_TURN
('WEST_TRADING_POST', 'West Trading Post: left base cost 1', 'IMMEDIATE', 'PRICE:0:1'),
('EAST_TRADING_POST', 'East Trading Post: right base cost 1', 'IMMEDIATE', 'PRICE:1:1'),
('MARKETPLACE', 'Marketplace: advanced cost 1', 'IMMEDIATE', 'PRICE:2:1'),
('CARAVANSERY_MUTABLE_BASE_1', 'Caravansery: +1 Mutable Base', 'IMMEDIATE', '7:1'),
('FORUM_MUTABLE_ADVANCED_1', 'Forum: +1 Mutable Advanced', 'IMMEDIATE', '8:1'),
('VINEYARD_COINS_BROWN', 'Vineyard: +1 coin per brown card (neighbors + self)', 'END_OF_TURN', 'VINEYARD'),
('LIGHTHOUSE_COINS_YELLOW', 'Lighthouse: +1 coin per yellow card', 'END_OF_TURN', 'LIGHTHOUSE'),
('HAVEN_COINS_BROWN', 'Haven: +1 coin per brown card', 'END_OF_TURN', 'HAVEN'),
('CHAMBER_OF_COMMERCE_COINS_YELLOW', 'Chamber of Commerce: +2 coins per yellow card', 'END_OF_TURN', 'CHAMBER_OF_COMMERCE'),

-- RED CARDS (Military) - IMMEDIATE
('GUARD_TOWER_MILITARY_1', 'Guard Tower: +1 Military', 'IMMEDIATE', 'MIL:1'),
('BARRACKS_MILITARY_1', 'Barracks: +1 Military', 'IMMEDIATE', 'MIL:1'),
('STOCKADE_MILITARY_1', 'Stockade: +1 Military', 'IMMEDIATE', 'MIL:1'),
('STABLES_MILITARY_2', 'Stables: +2 Military', 'IMMEDIATE', 'MIL:2'),
('ARCHERY_RANGE_MILITARY_2', 'Archery Range: +2 Military', 'IMMEDIATE', 'MIL:2'),
('WALLS_MILITARY_2', 'Walls: +2 Military', 'IMMEDIATE', 'MIL:2'),
('ARSENAL_MILITARY_3', 'Arsenal: +3 Military', 'IMMEDIATE', 'MIL:3'),
('SIEGE_WORKSHOP_MILITARY_3', 'Siege Workshop: +3 Military', 'IMMEDIATE', 'MIL:3'),
('FORTIFICATIONS_MILITARY_3', 'Fortifications: +3 Military', 'IMMEDIATE', 'MIL:3'),

-- GREEN CARDS (Science) - IMMEDIATE
('SCRIPTORIUM_TABLET_1', 'Scriptorium: +1 Tablet', 'IMMEDIATE', '9:1'),
('APOTHECARY_COMPASS_1', 'Apothecary: +1 Compass', 'IMMEDIATE', '10:1'),
('WORKSHOP_GEAR_1', 'Workshop: +1 Gear', 'IMMEDIATE', '11:1'),
('DISPENSARY_COMPASS_1', 'Dispensary: +1 Compass', 'IMMEDIATE', '10:1'),
('LABORATORY_GEAR_1', 'Laboratory: +1 Gear', 'IMMEDIATE', '11:1'),
('LIBRARY_TABLET_1', 'Library: +1 Tablet', 'IMMEDIATE', '9:1'),
('SCHOOL_TABLET_1', 'School: +1 Tablet', 'IMMEDIATE', '9:1'),
('UNIVERSITY_TABLET_1', 'University: +1 Tablet', 'IMMEDIATE', '9:1'),
('STUDY_GEAR_1', 'Study: +1 Gear', 'IMMEDIATE', '11:1'),
('LODGE_COMPASS_1', 'Lodge: +1 Compass', 'IMMEDIATE', '10:1'),
('ACADEMY_COMPASS_1', 'Academy: +1 Compass', 'IMMEDIATE', '10:1'),
('OBSERVATORY_GEAR_1', 'Observatory: +1 Gear', 'IMMEDIATE', '11:1'),

-- VIOLET CARDS (Guilds) - END_OF_GAME
('WORKERS_GUILD_VP_BROWN', 'Workers Guild: +1 VP per brown card (neighbors)', 'END_OF_GAME', 'WORKERS_GUILD'),
('CRAFTSMENS_GUILD_VP_GREY', 'Craftsmens Guild: +2 VP per grey card (neighbors)', 'END_OF_GAME', 'CRAFTSMENS_GUILD'),
('MAGISTRATES_GUILD_VP_BLUE', 'Magistrates Guild: +1 VP per blue card (neighbors)', 'END_OF_GAME', 'MAGISTRATES_GUILD'),
('TRADERS_GUILD_VP_YELLOW', 'Traders Guild: +1 VP per yellow card (neighbors)', 'END_OF_GAME', 'TRADERS_GUILD'),
('SPIES_GUILD_VP_RED', 'Spies Guild: +1 VP per red card (neighbors)', 'END_OF_GAME', 'SPIES_GUILD'),
('PHILOSOPHERS_GUILD_VP_GREEN', 'Philosophers Guild: +1 VP per green card (neighbors)', 'END_OF_GAME', 'PHILOSOPHERS_GUILD'),
('SHIPOWNERS_GUILD_VP_BROWN_GREY_VIOLET', 'Shipowners Guild: +1 VP per brown+grey+violet card (self)', 'END_OF_GAME', 'SHIPOWNERS_GUILD'),
('SCIENTISTS_GUILD_MUTABLE', 'Scientists Guild: +1 Mutable Science', 'IMMEDIATE', '12:1'),
('DECORATORS_GUILD_VP_WONDER', 'Decorators Guild: +7 VP if wonder fully built', 'END_OF_GAME', 'DECORATORS_GUILD'),
('BUILDERS_GUILD_VP_WONDER', 'Builders Guild: +1 VP per wonder stage (self + neighbors)', 'END_OF_GAME', 'BUILDERS_GUILD'),

-- WONDER EFFECTS - Alexandria A
('ALEXANDRIA_A_STAGE_1_VP_3', 'Alexandria A Stage 1: +3 VP', 'IMMEDIATE', 'VP:3'),
('ALEXANDRIA_A_STAGE_2_MUTABLE_BASE_1', 'Alexandria A Stage 2: +1 Mutable Base', 'IMMEDIATE', '7:1'),
('ALEXANDRIA_A_STAGE_3_VP_7', 'Alexandria A Stage 3: +7 VP', 'IMMEDIATE', 'VP:7'),

-- WONDER EFFECTS - Alexandria B
('ALEXANDRIA_B_STAGE_1_MUTABLE_BASE_1', 'Alexandria B Stage 1: +1 Mutable Base', 'IMMEDIATE', '7:1'),
('ALEXANDRIA_B_STAGE_2_MUTABLE_ADVANCED_1', 'Alexandria B Stage 2: +1 Mutable Advanced', 'IMMEDIATE', '8:1'),
('ALEXANDRIA_B_STAGE_3_VP_7', 'Alexandria B Stage 3: +7 VP', 'IMMEDIATE', 'VP:7'),

-- WONDER EFFECTS - Babylon A
('BABYLON_A_STAGE_1_VP_3', 'Babylon A Stage 1: +3 VP', 'IMMEDIATE', 'VP:3'),
('BABYLON_A_STAGE_2_MUTABLE_SCIENCE_1', 'Babylon A Stage 2: +1 Mutable Science', 'IMMEDIATE', '12:1'),
('BABYLON_A_STAGE_3_VP_7', 'Babylon A Stage 3: +7 VP', 'IMMEDIATE', 'VP:7'),

-- WONDER EFFECTS - Babylon B
('BABYLON_B_STAGE_1_VP_3', 'Babylon B Stage 1: +3 VP', 'IMMEDIATE', 'VP:3'),
('BABYLON_B_STAGE_2_PLAY_LAST_CARDS', 'Babylon B Stage 2: Play last 2 cards of age', 'END_OF_AGE_BEFORE_DISCARD', 'BABYLON_B_PLAY_LAST'),
('BABYLON_B_STAGE_3_MUTABLE_SCIENCE_1', 'Babylon B Stage 3: +1 Mutable Science', 'IMMEDIATE', '12:1'),

-- WONDER EFFECTS - Ephesos A
('EPHESOS_A_STAGE_1_VP_3', 'Ephesos A Stage 1: +3 VP', 'IMMEDIATE', 'VP:3'),
('EPHESOS_A_STAGE_2_COINS_9', 'Ephesos A Stage 2: +9 Coins', 'IMMEDIATE', 'COINS:9'),
('EPHESOS_A_STAGE_3_VP_7', 'Ephesos A Stage 3: +7 VP', 'IMMEDIATE', 'VP:7'),

-- WONDER EFFECTS - Ephesos B
('EPHESOS_B_STAGE_1_VP_2_COINS_4_MILITARY_1', 'Ephesos B Stage 1: +2 VP, +4 Coins, +1 Military', 'IMMEDIATE', 'VP:2|COINS:4|MIL:1'),
('EPHESOS_B_STAGE_2_VP_3_COINS_4_MILITARY_1', 'Ephesos B Stage 2: +3 VP, +4 Coins, +1 Military', 'IMMEDIATE', 'VP:3|COINS:4|MIL:1'),
('EPHESOS_B_STAGE_3_VP_5_COINS_4_MILITARY_1', 'Ephesos B Stage 3: +5 VP, +4 Coins, +1 Military', 'IMMEDIATE', 'VP:5|COINS:4|MIL:1'),

-- WONDER EFFECTS - Gizah A
('GIZAH_A_STAGE_1_VP_3', 'Gizah A Stage 1: +3 VP', 'IMMEDIATE', 'VP:3'),
('GIZAH_A_STAGE_2_VP_5', 'Gizah A Stage 2: +5 VP', 'IMMEDIATE', 'VP:5'),
('GIZAH_A_STAGE_3_VP_7', 'Gizah A Stage 3: +7 VP', 'IMMEDIATE', 'VP:7'),

-- WONDER EFFECTS - Gizah B
('GIZAH_B_STAGE_1_VP_3', 'Gizah B Stage 1: +3 VP', 'IMMEDIATE', 'VP:3'),
('GIZAH_B_STAGE_2_VP_5', 'Gizah B Stage 2: +5 VP', 'IMMEDIATE', 'VP:5'),
('GIZAH_B_STAGE_3_VP_5', 'Gizah B Stage 3: +5 VP', 'IMMEDIATE', 'VP:5'),
('GIZAH_B_STAGE_4_VP_7', 'Gizah B Stage 4: +7 VP', 'IMMEDIATE', 'VP:7'),

-- WONDER EFFECTS - Halikarnassos A
('HALIKARNASSOS_A_STAGE_1_VP_3', 'Halikarnassos A Stage 1: +3 VP', 'IMMEDIATE', 'VP:3'),
('HALIKARNASSOS_A_STAGE_2_BUILD_DISCARD', 'Halikarnassos A Stage 2: Build card from discard', 'END_OF_TURN', 'BUILD_FROM_DISCARD'),
('HALIKARNASSOS_A_STAGE_3_VP_7', 'Halikarnassos A Stage 3: +7 VP', 'IMMEDIATE', 'VP:7'),

-- WONDER EFFECTS - Halikarnassos B
('HALIKARNASSOS_B_STAGE_1_BUILD_DISCARD', 'Halikarnassos B Stage 1: Build card from discard + 2 VP', 'END_OF_TURN', 'BUILD_FROM_DISCARD|VP:2'),
('HALIKARNASSOS_B_STAGE_2_BUILD_DISCARD', 'Halikarnassos B Stage 2: Build card from discard + 1 VP', 'END_OF_TURN', 'BUILD_FROM_DISCARD|VP:1'),
('HALIKARNASSOS_B_STAGE_3_BUILD_DISCARD', 'Halikarnassos B Stage 3: Build card from discard', 'END_OF_TURN', 'BUILD_FROM_DISCARD'),

-- WONDER EFFECTS - Olympia A
('OLYMPIA_A_STAGE_1_VP_3', 'Olympia A Stage 1: +3 VP', 'IMMEDIATE', 'VP:3'),
('OLYMPIA_A_STAGE_2_FIRST_CARD_FREE', 'Olympia A Stage 2: First card each age is free', 'DEFERRED', 'FIRST_CARD_FREE'),
('OLYMPIA_A_STAGE_3_VP_7', 'Olympia A Stage 3: +7 VP', 'IMMEDIATE', 'VP:7'),

-- WONDER EFFECTS - Olympia B
('OLYMPIA_B_STAGE_1_BASE_PRICE_1', 'Olympia B Stage 1: base resources cost 1 (both sides)', 'IMMEDIATE', 'PRICE:2:1'),
('OLYMPIA_B_STAGE_2_VP_5', 'Olympia B Stage 2: +5 VP', 'IMMEDIATE', 'VP:5'),
('OLYMPIA_B_STAGE_3_COPY_VIOLET', 'Olympia B Stage 3: Copy violet card from neighbor at end', 'END_OF_GAME', 'COPY_VIOLET'),

-- WONDER EFFECTS - Rhodes A
('RHODES_A_STAGE_1_VP_3', 'Rhodes A Stage 1: +3 VP', 'IMMEDIATE', 'VP:3'),
('RHODES_A_STAGE_2_MILITARY_2', 'Rhodes A Stage 2: +2 Military', 'IMMEDIATE', 'MIL:2'),
('RHODES_A_STAGE_3_VP_7', 'Rhodes A Stage 3: +7 VP', 'IMMEDIATE', 'VP:7'),

-- WONDER EFFECTS - Rhodes B
('RHODES_B_STAGE_1_VP_3_COINS_3_MILITARY_1', 'Rhodes B Stage 1: +3 VP, +3 Coins, +1 Military', 'IMMEDIATE', 'VP:3|COINS:3|MIL:1'),
('RHODES_B_STAGE_2_VP_4_COINS_4_MILITARY_1', 'Rhodes B Stage 2: +4 VP, +4 Coins, +1 Military', 'IMMEDIATE', 'VP:4|COINS:4|MIL:1');


-- Test Users (passwords: alice="password1", bob="password2", charlie="password3")
-- Using {noop} prefix for plain text passwords (for testing only)
INSERT INTO users (username, password, role) VALUES
('alice', '{noop}password1', 'ROLE_USER'),
('bob', '{noop}password2', 'ROLE_USER'),
('charlie', '{noop}password3', 'ROLE_USER'),
('admin', '{noop}demo', 'ROLE_ADMIN');

INSERT INTO wonders (name, face, starting_resources, stage_costs, number_of_stages, image) VALUES
-- Alexandria A
('Alexandria', 'A', '{"GLASS":1}', '[{"STONE":2},{"ORE":2},{"GLASS":2}]', 3, 'alexandriaA.png'),
-- Alexandria B
('Alexandria', 'B', '{"GLASS":1}', '[{"BRICK":2},{"WOOD":2},{"STONE":3}]', 3, 'alexandriaB.png'),

-- Babylon A
('Babylon', 'A', '{"BRICK":1}', '[{"BRICK":2},{"WOOD":3},{"BRICK":4}]', 3, 'babylonA.png'),
-- Babylon B
('Babylon', 'B', '{"BRICK":1}', '[{"BRICK":1,"TEXTILE":1},{"WOOD":2,"GLASS":1},{"BRICK":3,"PAPER":1}]', 3, 'babylonB.png'),

-- Ephesos A
('Ephesos', 'A', '{"PAPER":1}', '[{"STONE":2},{"WOOD":2},{"PAPER":2}]', 3, 'ephesosA.png'),
-- Ephesos B
('Ephesos', 'B', '{"PAPER":1}', '[{"STONE":2},{"WOOD":2},{"PAPER":1,"GLASS":1,"TEXTILE":1}]', 3, 'ephesosB.png'),

-- Gizah A
('Gizah', 'A', '{"STONE":1}', '[{"STONE":2},{"WOOD":3},{"STONE":4}]', 3, 'gizahA.png'),
-- Gizah B
('Gizah', 'B', '{"STONE":1}', '[{"WOOD":2},{"STONE":3},{"BRICK":3},{"STONE":4,"PAPER":1}]', 4, 'gizahB.png'),

-- Halikarnassos A
('Halikarnassos', 'A', '{"TEXTILE":1}', '[{"BRICK":2},{"ORE":3},{"TEXTILE":2}]', 3, 'halikarnassusA.png'),
-- Halikarnassos B
('Halikarnassos', 'B', '{"TEXTILE":1}', '[{"ORE":2},{"BRICK":3},{"PAPER":1,"GLASS":1,"TEXTILE":1}]', 3, 'halikarnassusB.png'),

-- Olympia A
('Olympia', 'A', '{"WOOD":1}', '[{"WOOD":2},{"STONE":2},{"ORE":2}]', 3, 'olympiaA.png'),
-- Olympia B
('Olympia', 'B', '{"WOOD":1}', '[{"WOOD":2},{"STONE":2},{"ORE":2,"TEXTILE":1}]', 3, 'olympiaB.png'),

-- Rhodes A
('Rhodes', 'A', '{"ORE":1}', '[{"WOOD":2},{"BRICK":3},{"ORE":4}]', 3, 'rhodosA.png'),
-- Rhodes B
('Rhodes', 'B', '{"ORE":1}', '[{"STONE":3},{"ORE":4}]', 2, 'rhodosB.png');
