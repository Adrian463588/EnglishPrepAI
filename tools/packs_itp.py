"""5 original sample packages for TOEFL ITP Level 1 (ITP-L1-01..05, 12 items each = 60 items)."""


def _q(qid, sec, ttype, prompt, opts, ans, exp, src, diff="B2", passage_id=None, audio_id=None, audio_turns=None):
    return {
        "id": qid,
        "section": sec,
        "taskType": ttype,
        "prompt": prompt,
        "options": opts,
        "answer": ans,
        "acceptedAnswers": [ans] if ans else [],
        "explanation": exp,
        "rationaleSource": src,
        "difficulty": diff,
        "points": 1,
        "license": "Original",
        "reviewStatus": "VERIFIED_ORIGINAL",
        "passageId": passage_id,
        "audioAssetId": audio_id,
        "audioTurns": audio_turns or [],
    }


ITP_PACKS = [
    {
        "packId": "ITP-L1-01",
        "examId": "ITP_L1",
        "edition": "2026.1",
        "title": "ITP Paket 1 — Campus Routines & Natural Systems",
        "theme": "Diagnostic foundations across campus dialogues, passive solar biology, and clause grammar.",
        "status": "SAMPLE",
        "blueprintCount": 140,
        "durationMinutes": 20,
        "blueprintDurationMinutes": 115,
        "license": "Original",
        "provenance": "EnglishPrepAI-Original-2026",
        "passages": [
            {
                "id": "ITP01-P1",
                "title": "How Desert Succulents Conserve Water",
                "body": (
                    "Unlike temperate woodland trees that open leaf pores throughout daylight hours, desert succulents "
                    "rely on Crassulacean acid metabolism to curb moisture loss. At night, when desert air cools and "
                    "humidity rises slightly, these plants open tiny stomata to absorb carbon dioxide, storing it as malic "
                    "acid inside vacuole chambers. Once sunlight returns, stomata seal shut while photosynthesis converts "
                    "the stored carbon into sugars. Because waxy cuticles and swollen stems simultaneously limit evaporation, "
                    "succulents endure multi-month droughts without shedding vital tissue."
                ),
                "readingOrder": 1
            },
            {
                "id": "ITP01-P2",
                "title": "Early Marine Chronometers",
                "body": (
                    "Before the 1760s, ocean navigators determined latitude easily from solar elevation, yet establishing "
                    "longitude remained dangerously imprecise. Without knowing the exact time at a reference port, a clock "
                    "error of merely four minutes shifted a ship's calculated position by sixty nautical miles near the equator. "
                    "English carpenter John Harrison countered temperature swings and rolling decks by pairing bimetallic "
                    "strips with low-friction bearings in his fourth sea watch, H4. Trial voyages across the Atlantic proved "
                    "its accuracy within a single nautical mile, revolutionizing maritime cartography."
                ),
                "readingOrder": 2
            }
        ],
        "questions": [
            _q("ITP-L1-01-Q01", "LISTENING", "CHOICE_SINGLE",
               "Listen to the short dialogue. What does the woman mean?",
               ["She cannot lend the recorder because another classmate has it.", "She forgot to bring batteries for the recorder.", "She will meet the man at the media counter at five.", "She plans to sell her audio recorder tomorrow."],
               "She cannot lend the recorder because another classmate has it.",
               "Pembicara kedua menyebut 'Maya borrowed it for her linguistics interview', artinya alat sedang dipinjam orang lain.",
               "ITP-MOD-01", "B1", audio_id="ITP01-A01",
               audio_turns=[("M", "Could I borrow your digital voice recorder for this afternoon?"), ("W", "I wish I could help, but Maya borrowed it for her linguistics interview."), ("N", "What does the woman mean?")]),
            _q("ITP-L1-01-Q02", "LISTENING", "CHOICE_SINGLE",
               "Listen to the conversation between two students. What does the man imply?",
               ["He thinks the seminar room feels unusually cold.", "He wants to cancel today's afternoon seminar.", "He prefers sitting beside the open window.", "He already turned off the air conditioner."],
               "He thinks the seminar room feels unusually cold.",
               "'Did someone turn this room into an icebox?' adalah ungkapan hiperbolis bahwa ruangan terasa sangat dingin.",
               "ITP-MOD-01", "B1", audio_id="ITP01-A02",
               audio_turns=[("W", "Should we review the graphs before Professor Lin arrives?"), ("M", "Sure, though did someone turn this room into an icebox?"), ("N", "What does the man imply?")]),
            _q("ITP-L1-01-Q03", "LISTENING", "CHOICE_SINGLE",
               "Listen to the advisor dialogue. Why does the student visit the registrar office?",
               ["To clarify whether an elective fulfills her science requirement.", "To withdraw from all spring courses.", "To request an extension on tuition payment.", "To replace a lost dormitory key."],
               "To clarify whether an elective fulfills her science requirement.",
               "Mahasiswa menanyakan apakah mata kuliah Geology 105 dapat dihitung sebagai kredit laboratorium sains.",
               "ITP-MOD-02", "B2", audio_id="ITP01-A03",
               audio_turns=[("W", "Hello, I noticed Geology 105 has a field component. Does it count toward my natural science lab credit?"), ("M", "Let me check the catalog code. Yes, any four-credit course ending in five qualifies."), ("N", "Why did the student visit the office?")]),
            _q("ITP-L1-01-Q04", "LISTENING", "CHOICE_SINGLE",
               "Listen to the mini-lecture excerpt. According to the speaker, what keeps adobe walls cool during afternoon heat?",
               ["Thick earthen walls absorb heat slowly and release it at night.", "Reflective metal sheets block all infrared rays.", "Underground pipes pump chilled groundwater.", "High ceilings push humid air through roof vents."],
               "Thick earthen walls absorb heat slowly and release it at night.",
               "Pembicara menjelaskan prinsip thermal mass: dinding tanah liat tebal menyerap panas lambat di siang hari lalu melepasnya saat malam.",
               "ITP-MOD-02", "B2", audio_id="ITP01-A04",
               audio_turns=[("M", "In arid regions, thick adobe walls utilize thermal mass. They absorb daytime heat gradually, keeping interior rooms cool until nightfall releases that stored warmth outside."), ("N", "How do adobe walls moderate indoor temperatures?")]),
            _q("ITP-L1-01-Q05", "STRUCTURE", "CHOICE_SINGLE",
               "Rarely ___ a volcanic eruption monitored from so many underwater sensors simultaneously.",
               ["was", "has", "were", "have"],
               "was",
               "Adverb negatif 'Rarely' di awal kalimat memicu inversi auxiliary 'was' + subjek tunggal 'a volcanic eruption' + past participle 'monitored'.",
               "ITP-MOD-03", "B2"),
            _q("ITP-L1-01-Q06", "STRUCTURE", "CHOICE_SINGLE",
               "The university's archive of nineteenth-century maps, along with several brass compasses, ___ stored in climate-controlled drawers.",
               ["is", "are", "were", "have"],
               "is",
               "Inti subjek adalah kata benda tunggal 'archive'; frasa 'along with...' tidak menjadikan subjek jamak.",
               "ITP-MOD-03", "B1"),
            _q("ITP-L1-01-Q07", "STRUCTURE", "ERROR_IDENTIFICATION",
               "Migratory whales [A] rely on low-frequency calls [B] to coordinate feeding, [C] avoid reefs, and [D] finding distant mates.",
               ["[A] rely on", "[B] to coordinate", "[C] avoid reefs", "[D] finding distant mates"],
               "[D] finding distant mates",
               "Paralelisme sesudah 'to' menuntut bentuk dasar 'find distant mates' sejajar dengan 'coordinate' dan 'avoid'.",
               "ITP-MOD-04", "B2"),
            _q("ITP-L1-01-Q08", "STRUCTURE", "ERROR_IDENTIFICATION",
               "Although [A] the bridge looks [B] similarly to Victorian steel spans, [C] its core cables use [D] modern carbon composites.",
               ["[A] the bridge looks", "[B] similarly to", "[C] its core cables", "[D] modern carbon composites"],
               "[B] similarly to",
               "Linking verb 'looks' diikuti adjektiva 'similar to', bukan adverb 'similarly'.",
               "ITP-MOD-04", "B2"),
            _q("ITP-L1-01-Q09", "READING", "CHOICE_SINGLE",
               "Based on Passage 1 ('How Desert Succulents Conserve Water'), why do succulents open their stomata at night?",
               ["Cooler night air lowers the amount of water lost through evaporation.", "Photosynthesis occurs faster in darkness than in sunlight.", "Night winds supply higher nitrogen levels to roots.", "Insects pollinate stomata only after sunset."],
               "Cooler night air lowers the amount of water lost through evaporation.",
               "Paragraf menjelaskan pembukaan stomata terjadi saat udara malam lebih dingin dan lembap untuk menekan kehilangan air.",
               "ITP-MOD-05", "B2", passage_id="ITP01-P1"),
            _q("ITP-L1-01-Q10", "READING", "CHOICE_SINGLE",
               "In Passage 1, the word 'curb' in line 2 is closest in meaning to:",
               ["restrain", "measure", "accelerate", "reverse"],
               "restrain",
               "Dalam konteks 'curb moisture loss', 'curb' berarti menahan atau membatasi ('restrain').",
               "ITP-MOD-05", "B2", passage_id="ITP01-P1"),
            _q("ITP-L1-01-Q11", "READING", "CHOICE_SINGLE",
               "In Passage 2 ('Early Marine Chronometers'), what does the pronoun 'its' in the final sentence refer to?",
               ["H4", "the equator", "latitude", "cartography"],
               "H4",
               "'its accuracy within a single nautical mile' merujuk pada jam laut keempat karya Harrison, yakni H4.",
               "ITP-MOD-05", "B2", passage_id="ITP01-P2"),
            _q("ITP-L1-01-Q12", "READING", "CHOICE_SINGLE",
               "According to Passage 2, why was an accurate clock essential for measuring longitude at sea?",
               ["Small timing errors translated into large directional distance errors.", "Clocks powered ship compasses near magnetic poles.", "Captains needed alarms to wake harbor pilots.", "Solar angles disappeared completely near the equator."],
               "Small timing errors translated into large directional distance errors.",
               "Teks menyebutkan kesalahan 4 menit menggeser posisi hingga 60 mil laut.",
               "ITP-MOD-05", "B2", passage_id="ITP01-P2"),
        ]
    },

    {
        "packId": "ITP-L1-02",
        "examId": "ITP_L1",
        "edition": "2026.1",
        "title": "ITP Paket 2 — History, Architecture & Urban Ecology",
        "theme": "Idiomatic campus exchanges, participle clauses, and industrial urban waterways.",
        "status": "SAMPLE",
        "blueprintCount": 140,
        "durationMinutes": 20,
        "blueprintDurationMinutes": 115,
        "license": "Original",
        "provenance": "EnglishPrepAI-Original-2026",
        "passages": [
            {
                "id": "ITP02-P1",
                "title": "Daylighting Buried Urban Streams",
                "body": (
                    "During rapid nineteenth-century industrialization, dozens of cities enclosed small creeks inside brick "
                    "culverts to clear land for factories and roads. While convenient initially, buried pipes accelerated "
                    "stormwater runoff, triggering severe downstream flash floods whenever intense rain overwhelmed drain "
                    "capacity. Urban planners now practice 'daylighting'—uncovering buried channels and restoring vegetated "
                    "floodplains. Once reopened, meandering banks slow peak discharge, filter road pollutants through root "
                    "systems, and cool adjacent neighborhoods by up to two degrees Celsius."
                ),
                "readingOrder": 1
            },
            {
                "id": "ITP02-P2",
                "title": "Roman Hydraulic Concrete",
                "body": (
                    "Modern Portland cement piers immersed in seawater frequently crack within decades as saltwater corrodes "
                    "embedded steel reinforcement. By contrast, harbor breakwaters constructed along the Mediterranean two "
                    "millennia ago remain intact despite relentless wave impact. Materials analyses indicate Roman builders "
                    "mixed slaked lime with volcanic pozzolana ash, creating mineral crystals that actually interlock tighter "
                    "when exposed to seawater percolation. Instead of weakening over time, microscopic fractures self-heal "
                    "as aluminum-silicate compounds precipitate inside pores."
                ),
                "readingOrder": 2
            }
        ],
        "questions": [
            _q("ITP-L1-02-Q01", "LISTENING", "CHOICE_SINGLE",
               "Listen to the conversation. What does the man mean?",
               ["He thinks the essay outline needs clearer topic sentences.", "He has already finished grading all student outlines.", "He disagrees with every point in the woman's essay.", "He plans to skip the writing center workshop."],
               "He thinks the essay outline needs clearer topic sentences.",
               "Pria menyarankan penajaman topic sentences sebelum menambahkan lebih banyak kutipan.",
               "ITP-MOD-01", "B2", audio_id="ITP02-A01",
               audio_turns=[("W", "Should I add three more quotations to my history essay?"), ("M", "Before adding sources, I'd sharpen your topic sentences so each paragraph's point stands out."), ("N", "What does the man suggest?")]),
            _q("ITP-L1-02-Q02", "LISTENING", "CHOICE_SINGLE",
               "Listen to the dialogue. What can be inferred about the campus bookstore?",
               ["It ran out of the biology lab manual temporarily.", "It closed permanently last Friday.", "It only accepts cash payments for manuals.", "It moved into the chemistry annex."],
               "It ran out of the biology lab manual temporarily.",
               "Wanita menyebut stok manual habis sementara ('sold out until Thursday').",
               "ITP-MOD-01", "B1", audio_id="ITP02-A02",
               audio_turns=[("M", "Did you grab the new biology lab manual yet?"), ("W", "No luck—the bookstore sold out this morning, though a shipment arrives Thursday."), ("N", "What is true about the lab manual?")]),
            _q("ITP-L1-02-Q03", "LISTENING", "CHOICE_SINGLE",
               "Listen to the student-librarian conversation. What does the librarian offer to do?",
               ["Place a hold on a microfilm reel from another branch.", "Scan the entire newspaper archive immediately.", "Extend the library closing hour until midnight.", "Waive all late fees from previous semesters."],
               "Place a hold on a microfilm reel from another branch.",
               "Pustakawan menawarkan pemesanan silang antar-cabang ('request the reel from our downtown branch').",
               "ITP-MOD-02", "B2", audio_id="ITP02-A03",
               audio_turns=[("M", "I can't find the 1924 city newspaper reel on the third floor."), ("W", "That reel is archived at our downtown branch. I can place a transfer hold so it arrives here by noon tomorrow."), ("N", "What does the librarian offer to do?")]),
            _q("ITP-L1-02-Q04", "LISTENING", "CHOICE_SINGLE",
               "Listen to the archaeology talk. Why did scribes reuse ancient parchment leaves?",
               ["Prepared animal skin was costly and scarce.", "Ink faded completely within three days.", "Laws prohibited keeping older religious texts.", "Wood pulp paper had already replaced parchment."],
               "Prepared animal skin was costly and scarce.",
               "Pembicara menjelaskan kelangkaan dan tingginya biaya pembuatan perkamen mendorong praktik palimpsest.",
               "ITP-MOD-02", "B2", audio_id="ITP02-A04",
               audio_turns=[("W", "Because prepared animal skin parchment was expensive and scarce, medieval scribes sometimes scraped off older writing to copy new manuscripts—creating layered documents called palimpsests."), ("N", "Why were parchment sheets reused?")]),
            _q("ITP-L1-02-Q05", "STRUCTURE", "CHOICE_SINGLE",
               "___ along shallow tidal flats, mangrove roots trap floating silt and protect shorelines from storm surges.",
               ["Growing", "Grown they", "To grow it", "Having grew"],
               "Growing",
               "Reduced active participle clause ('Growing along shallow tidal flats...') menjelaskan subjek 'mangrove roots'.",
               "ITP-MOD-03", "B2"),
            _q("ITP-L1-02-Q06", "STRUCTURE", "CHOICE_SINGLE",
               "Architects designed the reading hall with north-facing skylights ___ direct glare across study tables.",
               ["to minimize", "minimize", "minimizing it", "for minimize"],
               "to minimize",
               "Infinitive of purpose 'to minimize' menyatakan tujuan perancangan jendela atap.",
               "ITP-MOD-03", "B1"),
            _q("ITP-L1-02-Q07", "STRUCTURE", "ERROR_IDENTIFICATION",
               "Between 1910 and 1930, [A] the number of municipal libraries [B] across the Midwest [C] have tripled despite [D] tight budgets.",
               ["[A] the number of", "[B] across", "[C] have tripled", "[D] tight budgets"],
               "[C] have tripled",
               "Rentang waktu lampau spesifik ('Between 1910 and 1930') serta subjek tunggal 'the number' menuntut simple past 'tripled', bukan 'have tripled'.",
               "ITP-MOD-04", "B2"),
            _q("ITP-L1-02-Q08", "STRUCTURE", "ERROR_IDENTIFICATION",
               "No [A] other playwright of that era [B] wrote dialogue [C] as realistic and witty [D] like Maria Alvarez did.",
               ["[A] other", "[B] wrote", "[C] as realistic and witty", "[D] like Maria Alvarez did"],
               "[D] like Maria Alvarez did",
               "Pasangan perbandingan setara 'as ... as' menuntut 'as Maria Alvarez did', bukan 'like'.",
               "ITP-MOD-04", "B2"),
            _q("ITP-L1-02-Q09", "READING", "CHOICE_SINGLE",
               "What is the main topic of Passage 1 ('Daylighting Buried Urban Streams')?",
               ["Reopening enclosed city streams to reduce floods and improve local ecology", "Building deeper brick sewers beneath historic factory districts", "Replacing coastal ports with artificial drainage canals", "Stopping all rainfall runoff in residential streets"],
               "Reopening enclosed city streams to reduce floods and improve local ecology",
               "Seluruh bacaan membandingkan masalah pipa bawah tanah dengan manfaat pembukaan kembali aliran air ('daylighting').",
               "ITP-MOD-05", "B2", passage_id="ITP02-P1"),
            _q("ITP-L1-02-Q10", "READING", "CHOICE_SINGLE",
               "In Passage 1, what drawback of nineteenth-century culverts is mentioned?",
               ["They speeded up water flow, worsening downstream flooding.", "They dissolved rapidly in clean rainwater.", "They cooled surrounding streets excessively.", "They prevented factories from obtaining electricity."],
               "They speeded up water flow, worsening downstream flooding.",
               "Kalimat kedua menyebut 'buried pipes accelerated stormwater runoff, triggering severe downstream flash floods'.",
               "ITP-MOD-05", "B2", passage_id="ITP02-P1"),
            _q("ITP-L1-02-Q11", "READING", "CHOICE_SINGLE",
               "According to Passage 2 ('Roman Hydraulic Concrete'), how does seawater affect Roman piers?",
               ["It reacts with volcanic ash minerals to seal tiny cracks.", "It rusts internal steel rods within twenty years.", "It dissolves slaked lime immediately upon contact.", "It turns breakwaters into porous limestone caves."],
               "It reacts with volcanic ash minerals to seal tiny cracks.",
               "Teks menerangkan reaksi air laut dan abu pozzolana membentuk kristal pengunci yang menyembuhkan retakan mikro.",
               "ITP-MOD-05", "B2", passage_id="ITP02-P2"),
            _q("ITP-L1-02-Q12", "READING", "CHOICE_SINGLE",
               "In Passage 2, the word 'intact' is closest in meaning to:",
               ["undamaged", "submerged", "hollow", "abandoned"],
               "undamaged",
               "'Intact' berarti utuh dan tidak rusak meskipun diterjang ombak selama dua milenium.",
               "ITP-MOD-05", "B1", passage_id="ITP02-P2"),
        ]
    },

    {
        "packId": "ITP-L1-03",
        "examId": "ITP_L1",
        "edition": "2026.1",
        "title": "ITP Paket 3 — Engineering, Acoustics & Scientific Prose",
        "theme": "Negative inversions, paired conjunctions, acoustic engineering, and glacial geology.",
        "status": "SAMPLE",
        "blueprintCount": 140,
        "durationMinutes": 20,
        "blueprintDurationMinutes": 115,
        "license": "Original",
        "provenance": "EnglishPrepAI-Original-2026",
        "passages": [
            {
                "id": "ITP03-P1",
                "title": "Acoustic Shaping in Concert Halls",
                "body": (
                    "Listeners often assume auditorium acoustics depend primarily on speaker power, yet unamplified symphony "
                    "halls rely on subtle wall geometry. Flat, parallel concrete panels bounce sound waves back and forth in "
                    "harsh 'flutter echoes' that blur musical articulation. By angling timber side balconies and adding "
                    "stepped wooden diffusers, acousticians scatter early reflections laterally toward listeners' ears within "
                    "thirty milliseconds of the direct sound. This brief lateral delay creates an enveloping spatial warmth "
                    "without muddying rapid violin passages."
                ),
                "readingOrder": 1
            },
            {
                "id": "ITP03-P2",
                "title": "Glacial Varves as Climate Calendars",
                "body": (
                    "Each spring, meltwater streams carry coarse pale sand into lakes bordering retreating glaciers, while "
                    "calm winter ice cover lets fine dark clay settle slowly to the lakebed. Together, one pale summer band "
                    "and one dark winter band form an annual couplet known as a varve. By extracting sediment cores and "
                    "counting thousands of varves under microscopes, paleoclimatologists reconstruct year-by-year shifts in "
                    "melt intensity—achieving seasonal precision rarely possible with radiometric dating alone."
                ),
                "readingOrder": 2
            }
        ],
        "questions": [
            _q("ITP-L1-03-Q01", "LISTENING", "CHOICE_SINGLE",
               "Listen to the short exchange. What does the woman imply?",
               ["She disagrees that the lab assignment was effortless.", "She finished the assignment in half an hour.", "She plans to drop the robotics class.", "She lost her lab instructions yesterday."],
               "She disagrees that the lab assignment was effortless.",
               "Ungkapan 'Easy for you to say—I spent four hours calibrating the sensor!' menandakan tugas terasa sulit baginya.",
               "ITP-MOD-01", "B2", audio_id="ITP03-A01",
               audio_turns=[("M", "That robotics calibration exercise was a breeze."), ("W", "Easy for you to say! It took me four hours just to steady the sensor."), ("N", "What does the woman mean?")]),
            _q("ITP-L1-03-Q02", "LISTENING", "CHOICE_SINGLE",
               "Listen to the dialogue. What will the man probably do next?",
               ["Upload his slides to the shared folder before class.", "Print fifty color copies for every student.", "Ask the professor to postpone the presentation.", "Buy a new USB adapter from the bookstore."],
               "Upload his slides to the shared folder before class.",
               "Wanita mengingatkan proyektor tidak memakai USB langsung, sehingga pria setuju mengunggah ke folder kelas.",
               "ITP-MOD-01", "B1", audio_id="ITP03-A02",
               audio_turns=[("M", "I saved my presentation on this flash drive for our 2 PM talk."), ("W", "Good thing I saw you—the podium computer only reads files from the shared course folder."), ("M", "Thanks, I'll upload it right away."), ("N", "What will the man probably do next?")]),
            _q("ITP-L1-03-Q03", "LISTENING", "CHOICE_SINGLE",
               "Listen to the longer dialogue. Why is the student excited about the observatory tour?",
               ["Students will view Jupiter through the restored brass refractor.", "Attendance grants automatic exemption from the final exam.", "The observatory sits inside the student union roof.", "Tour guides will distribute free telescopes."],
               "Students will view Jupiter through the restored brass refractor.",
               "Percakapan menyebut teleskop refraktor perunggu yang baru direstorasi akan dipakai mengamati Jupiter.",
               "ITP-MOD-02", "B2", audio_id="ITP03-A03",
               audio_turns=[("W", "Are you signing up for Friday's observatory open house?"), ("M", "Definitely. Now that the 1912 brass refractor has been restored, they're aiming it at Jupiter all evening."), ("N", "Why is the man eager to attend?")]),
            _q("ITP-L1-03-Q04", "LISTENING", "CHOICE_SINGLE",
               "Listen to the geology lecture clip. Why are varves useful to researchers?",
               ["Each light-and-dark sediment pair marks one exact year.", "They contain intact dinosaur fossils in every layer.", "They form exclusively in tropical coral lagoons.", "They reveal future earthquake dates directly."],
               "Each light-and-dark sediment pair marks one exact year.",
               "Pembicara mendefinisikan varve sebagai pasangan lapisan musiman yang terbentuk satu pasangan per tahun.",
               "ITP-MOD-02", "B2", audio_id="ITP03-A04",
               audio_turns=[("M", "Because fast spring melt deposits coarse pale silt while quiet winter water drops fine dark clay, each light-and-dark layer pair in a glacial lake records one exact calendar year."), ("N", "What does one pair of sediment layers represent?")]),
            _q("ITP-L1-03-Q05", "STRUCTURE", "CHOICE_SINGLE",
               "Not only ___ wind shear near runways, but Doppler radar also estimates rainfall intensity.",
               ["does it detect", "it detects", "detects it", "is detected"],
               "does it detect",
               "'Not only' di awal kalimat memicu inversi auxiliary 'does' + subject 'it' + bare verb 'detect'.",
               "ITP-MOD-03", "B2"),
            _q("ITP-L1-03-Q06", "STRUCTURE", "CHOICE_SINGLE",
               "The telescope mirror, ___ curvature was polished over eighteen months, resolves faint moons clearly.",
               ["whose", "which", "that", "whom"],
               "whose",
               "Possessive relative pronoun 'whose' menghubungkan 'mirror' dengan 'curvature' ('the mirror's curvature').",
               "ITP-MOD-03", "B2"),
            _q("ITP-L1-03-Q07", "STRUCTURE", "ERROR_IDENTIFICATION",
               "Despite [A] of heavy fog along the coast, [B] the ferry docked [C] safely within [D] five minutes of schedule.",
               ["[A] of heavy fog", "[B] the ferry docked", "[C] safely", "[D] five minutes of schedule"],
               "[A] of heavy fog",
               "'Despite' tidak memakai preposisi 'of' (gunakan 'Despite heavy fog' atau 'In spite of heavy fog').",
               "ITP-MOD-04", "B1"),
            _q("ITP-L1-03-Q08", "STRUCTURE", "ERROR_IDENTIFICATION",
               "Every battery cell [A] inside the prototype pack [B] undergo thermal cycling [C] prior to [D] final certification.",
               ["[A] inside", "[B] undergo", "[C] prior to", "[D] final certification"],
               "[B] undergo",
               "Kata penentu 'Every' mewajibkan subjek tunggal sehingga kata kerjanya harus 'undergoes'.",
               "ITP-MOD-04", "B2"),
            _q("ITP-L1-03-Q09", "READING", "CHOICE_SINGLE",
               "According to Passage 1 ('Acoustic Shaping in Concert Halls'), what problem do parallel flat concrete walls cause?",
               ["Harsh flutter echoes that blur rapid notes", "Weak bass frequencies below twenty hertz", "Excessive absorption of violin timber resonance", "Delay exceeding three full seconds"],
               "Harsh flutter echoes that blur rapid notes",
               "Kalimat kedua menyatakan dinding beton datar paralel memantulkan suara bolak-balik menjadi 'flutter echoes' yang mengaburkan artikulasi.",
               "ITP-MOD-05", "B2", passage_id="ITP03-P1"),
            _q("ITP-L1-03-Q10", "READING", "CHOICE_SINGLE",
               "In Passage 1, the word 'laterally' is closest in meaning to:",
               ["from the sides", "from directly overhead", "beneath the floor", "after the concert"],
               "from the sides",
               "'Laterally' merujuk arah samping ('side balconies' ke telinga pendengar).",
               "ITP-MOD-05", "B2", passage_id="ITP03-P1"),
            _q("ITP-L1-03-Q11", "READING", "CHOICE_SINGLE",
               "In Passage 2 ('Glacial Varves as Climate Calendars'), when is the fine dark clay layer deposited?",
               ["During calm winter ice cover", "During rapid spring snowmelt", "During autumn windstorms", "During volcanic eruptions"],
               "During calm winter ice cover",
               "Teks menyebut 'calm winter ice cover lets fine dark clay settle slowly'.",
               "ITP-MOD-05", "B1", passage_id="ITP03-P2"),
            _q("ITP-L1-03-Q12", "READING", "CHOICE_SINGLE",
               "What advantage of varves over radiometric dating is noted in Passage 2?",
               ["Annual and seasonal resolution", "Lower laboratory microscope cost", "Applicability to ocean lava flows", "Elimination of lake core drilling"],
               "Annual and seasonal resolution",
               "Kalimat terakhir menyebut 'seasonal precision rarely possible with radiometric dating alone'.",
               "ITP-MOD-05", "B2", passage_id="ITP03-P2"),
        ]
    },

    {
        "packId": "ITP-L1-04",
        "examId": "ITP_L1",
        "edition": "2026.1",
        "title": "ITP Paket 4 — Economics, Marine Biology & Culture",
        "theme": "Double negatives, appositives, marine kelp ecosystems, and historic paper currency.",
        "status": "SAMPLE",
        "blueprintCount": 140,
        "durationMinutes": 20,
        "blueprintDurationMinutes": 115,
        "license": "Original",
        "provenance": "EnglishPrepAI-Original-2026",
        "passages": [
            {
                "id": "ITP04-P1",
                "title": "Sea Otters and Kelp Forest Stability",
                "body": (
                    "Along rocky North Pacific coastlines, towering kelp forests shelter juvenile fish and dampen destructive "
                    "ocean swells. Yet kelp canopies can vanish within months when purple sea urchins multiply unchecked and "
                    "graze kelp holdfasts down to bare stone—forming 'urchin barrens'. Sea otters act as a keystone predator "
                    "in this chain: lacking thick blubber, otters sustain an elevated metabolic rate by consuming roughly "
                    "a quarter of their body weight daily, largely in urchins and shellfish. Wherever otter populations "
                    "rebound, urchin grazing declines and dense kelp forests rapidly regenerate."
                ),
                "readingOrder": 1
            },
            {
                "id": "ITP04-P2",
                "title": "Cotton-Linen Durability in Banknotes",
                "body": (
                    "Ordinary office paper tears after a few dozen folds because wood pulp contains short cellulose fibers and "
                    "lignin that turns brittle with oxidation. Conversely, central banks print traditional banknotes on blends "
                    "of long-staple cotton and linen rag fibers. Because cotton-linen sheets weave long, flexible fibers under "
                    "high pressure without acid sizing, banknotes withstand thousands of creases, accidental laundry cycles, "
                    "and humid climates far longer than wood-derived paper."
                ),
                "readingOrder": 2
            }
        ],
        "questions": [
            _q("ITP-L1-04-Q01", "LISTENING", "CHOICE_SINGLE",
               "Listen to the dialogue. What does the woman mean?",
               ["She was surprised the economic policy talk had empty seats.", "She expected the auditorium to hold twice as many students.", "She disliked the speaker's examples on trade tariffs.", "She arrived twenty minutes after the talk ended."],
               "She was surprised the economic policy talk had empty seats.",
               "Pria berkata ia mengira ruangan akan penuh; wanita menjawab 'I couldn't believe how many empty rows there were either!' (sama-sama terkejut).",
               "ITP-MOD-01", "B2", audio_id="ITP04-A01",
               audio_turns=[("M", "I thought the economic policy lecture would be standing room only."), ("W", "Me too—I couldn't believe how many empty rows were left in the back!"), ("N", "What does the woman mean?")]),
            _q("ITP-L1-04-Q02", "LISTENING", "CHOICE_SINGLE",
               "Listen to the conversation. What does the man decide to do?",
               ["Ask the lab coordinator to check the microscope lens.", "Skip tomorrow's biology lab entirely.", "Buy a replacement glass slide set tonight.", "Write his report using someone else's data."],
               "Ask the lab coordinator to check the microscope lens.",
               "Wanita menyarankan menghubungi koordinator lab sebelum menunda eksperimen.",
               "ITP-MOD-01", "B1", audio_id="ITP04-A02",
               audio_turns=[("M", "My microscope image stays blurry even after adjusting the fine focus knob."), ("W", "Why don't you have the lab coordinator clean the objective lens before you restart?"), ("M", "Good idea, I'll take it to her desk now."), ("N", "What will the man do?")]),
            _q("ITP-L1-04-Q03", "LISTENING", "CHOICE_SINGLE",
               "Listen to the student conversation. What is the deadline change mentioned?",
               ["The draft review shifted from Wednesday to Friday afternoon.", "The final portfolio is canceled this semester.", "Group presentations moved up to Monday morning.", "Online submissions closed two days early."],
               "The draft review shifted from Wednesday to Friday afternoon.",
               "Pembicara mengonfirmasi email dosen yang menggeser tenggat draf ke Jumat sore.",
               "ITP-MOD-02", "B2", audio_id="ITP04-A03",
               audio_turns=[("W", "Did you read Professor Gomez's announcement about the economics draft?"), ("M", "Yes, she pushed the peer-review deadline from Wednesday back to Friday at four."), ("N", "What changed about the draft deadline?")]),
            _q("ITP-L1-04-Q04", "LISTENING", "CHOICE_SINGLE",
               "Listen to the marine biology segment. Why must sea otters eat so frequently?",
               ["They lack an insulating blubber layer and burn calories rapidly.", "Sea urchins contain almost no water.", "Cold ocean currents carry them away from kelp beds.", "Otters hibernate throughout December."],
               "They lack an insulating blubber layer and burn calories rapidly.",
               "Pembicara menyatakan berang-berang laut tidak memiliki lapisan lemak tebal (*blubber*) sehingga membutuhkan metabolisme tinggi.",
               "ITP-MOD-02", "B2", audio_id="ITP04-A04",
               audio_turns=[("W", "Unlike seals, sea otters lack thick blubber to trap body heat in frigid Pacific water, so they rely on dense fur and eat nearly twenty-five percent of their body weight every day."), ("N", "Why do sea otters consume so much food daily?")]),
            _q("ITP-L1-04-Q05", "STRUCTURE", "CHOICE_SINGLE",
               "Dr. Lin, ___ research on coastal estuaries spans three decades, received the national ecology prize.",
               ["whose", "who", "which", "whom"],
               "whose",
               "Klausa kepemilikan non-restriktif ('Dr. Lin's research') memakai 'whose'.",
               "ITP-MOD-03", "B2"),
            _q("ITP-L1-04-Q06", "STRUCTURE", "CHOICE_SINGLE",
               "Only after inspecting all thirty bridge joints ___ the county reopen the river crossing.",
               ["did", "had", "was", "were"],
               "did",
               "'Only after + prepositional phrase' di awal kalimat mewajibkan inversi auxiliary 'did' sebelum subjek 'the county' dan verb 'reopen'.",
               "ITP-MOD-03", "C1"),
            _q("ITP-L1-04-Q07", "STRUCTURE", "ERROR_IDENTIFICATION",
               "Because kelp [A] absorbs dissolved carbon rapidly, [B] marine botanists view [C] it as an [D] extreme valuable coastal habitat.",
               ["[A] absorbs", "[B] marine botanists", "[C] it as an", "[D] extreme valuable"],
               "[D] extreme valuable",
               "Untuk memodifikasi adjektiva 'valuable', diperlukan adverb 'extremely', bukan adjektiva 'extreme'.",
               "ITP-MOD-04", "B2"),
            _q("ITP-L1-04-Q08", "STRUCTURE", "ERROR_IDENTIFICATION",
               "The newly [A] minted coins weigh [B] slightly heavier [C] than those produced [D] in the previous decade.",
               ["[A] minted coins", "[B] slightly heavier", "[C] than those", "[D] in the previous decade"],
               "[B] slightly heavier",
               "Kata kerja 'weigh' diikuti keterangan ukuran atau adverb/nominal; perbandingan yang tepat: 'are slightly heavier than' atau 'weigh slightly more than'.",
               "ITP-MOD-04", "C1"),
            _q("ITP-L1-04-Q09", "READING", "CHOICE_SINGLE",
               "In Passage 1 ('Sea Otters and Kelp Forest Stability'), what happens when sea otters disappear from a coastal zone?",
               ["Sea urchins multiply and strip kelp forests down to bare rock.", "Juvenile fish rapidly replace kelp holdfasts.", "Ocean swells flatten rocky shorelines completely.", "Kelp canopies grow twice as thick without grazing."],
               "Sea urchins multiply and strip kelp forests down to bare rock.",
               "Teks menjelaskan ledakan populasi bulu babi memakan pangkal kelp hingga menyisakan 'urchin barrens'.",
               "ITP-MOD-05", "B2", passage_id="ITP04-P1"),
            _q("ITP-L1-04-Q10", "READING", "CHOICE_SINGLE",
               "In Passage 1, the word 'dampen' is closest in meaning to:",
               ["weaken", "soak", "amplify", "freeze"],
               "weaken",
               "Dalam konteks 'dampen destructive ocean swells', 'dampen' bermakna meredam/melemahkan kekuatan ombak.",
               "ITP-MOD-05", "B2", passage_id="ITP04-P1"),
            _q("ITP-L1-04-Q11", "READING", "CHOICE_SINGLE",
               "According to Passage 2 ('Cotton-Linen Durability in Banknotes'), why does ordinary office paper turn brittle over time?",
               ["It is made from wood pulp containing short fibers and lignin.", "It is washed inside high-temperature machines.", "It lacks metallic security threads.", "It uses too much long-staple cotton."],
               "It is made from wood pulp containing short fibers and lignin.",
               "Kalimat pertama menyebut 'wood pulp contains short cellulose fibers and lignin that turns brittle with oxidation'.",
               "ITP-MOD-05", "B1", passage_id="ITP04-P2"),
            _q("ITP-L1-04-Q12", "READING", "CHOICE_SINGLE",
               "What does Passage 2 imply about banknote paper compared with office paper?",
               ["Acid-free long fibers make banknotes far more resistant to repeated folding.", "Banknotes tear faster when exposed to humid air.", "Office paper costs central banks more to weave.", "Lignin increases the lifespan of currency bills."],
               "Acid-free long fibers make banknotes far more resistant to repeated folding.",
               "Teks mengkontraskan daya tahan lipatan ribuan kali dari campuran kapas-linen bebas asam.",
               "ITP-MOD-05", "B2", passage_id="ITP04-P2"),
        ]
    },

    {
        "packId": "ITP-L1-05",
        "examId": "ITP_L1",
        "edition": "2026.1",
        "title": "ITP Paket 5 — Astronomy, Linguistics & Public Policy",
        "theme": "Integrated diagnostic sample covering exoplanet spectroscopy, writing systems, and subjunctives.",
        "status": "SAMPLE",
        "blueprintCount": 140,
        "durationMinutes": 20,
        "blueprintDurationMinutes": 115,
        "license": "Original",
        "provenance": "EnglishPrepAI-Original-2026",
        "passages": [
            {
                "id": "ITP05-P1",
                "title": "Reading Starlight Through Exoplanet Atmospheres",
                "body": (
                    "Most planets orbiting distant stars lie too close to their host star's glare to be photographed directly. "
                    "Instead, astronomers study exoplanet air using transit spectroscopy. Whenever an orbiting planet crosses "
                    "directly in front of its star, a tiny fraction of starlight skims through the planet's thin atmospheric "
                    "rim before reaching space telescopes. Because specific gases—such as water vapor, methane, or sodium—"
                    "absorb narrow wavelengths of light, dark gaps in the recorded spectrum act like chemical fingerprints "
                    "revealing what gases surround a world trillions of kilometers away."
                ),
                "readingOrder": 1
            },
            {
                "id": "ITP05-P2",
                "title": "How Sequoyah Engineered the Cherokee Syllabary",
                "body": (
                    "In the early 1800s, Cherokee silversmith Sequoyah recognized that written symbols enabled rapid long-distance "
                    "communication, yet English spelling poorly matched Cherokee speech sounds. Working over twelve years "
                    "without formal linguistic training, he listened closely to everyday conversation and distilled the entire "
                    "spoken language into eighty-five distinct syllable characters. Because each symbol stood consistently for "
                    "one syllable rather than ambiguous silent letters, Cherokee learners often mastered reading and writing "
                    "their own language within weeks."
                ),
                "readingOrder": 2
            }
        ],
        "questions": [
            _q("ITP-L1-05-Q01", "LISTENING", "CHOICE_SINGLE",
               "Listen to the short exchange. What does the man mean?",
               ["He doubts the library printing queue will clear quickly.", "He already picked up his printed thesis.", "He thinks the color printer is out of paper.", "He wants to switch topics for his presentation."],
               "He doubts the library printing queue will clear quickly.",
               "Ungkapan 'Don't hold your breath—there are twenty people ahead of us' menandakan antrean akan lama.",
               "ITP-MOD-01", "B2", audio_id="ITP05-A01",
               audio_turns=[("W", "Do you think our posters will finish printing before noon?"), ("M", "Don't hold your breath—there are still twenty jobs ahead of us in the queue."), ("N", "What does the man imply?")]),
            _q("ITP-L1-05-Q02", "LISTENING", "CHOICE_SINGLE",
               "Listen to the conversation. What does the woman imply about Dr. Tanaka's statistics course?",
               ["It is challenging but clearly organized.", "It requires no prior algebra knowledge.", "It has been moved to an online-only format.", "Few students enjoy Dr. Tanaka's office hours."],
               "It is challenging but clearly organized.",
               "'Tough grading, sure, but her lecture notes are crystal clear' menunjukkan mata kuliah sulit namun sangat terstruktur.",
               "ITP-MOD-01", "B2", audio_id="ITP05-A02",
               audio_turns=[("M", "Everyone says Dr. Tanaka's applied statistics class is brutal."), ("W", "Her problem sets are tough, no doubt, yet her examples are the clearest I've ever seen."), ("N", "What is the woman's opinion of the course?")]),
            _q("ITP-L1-05-Q03", "LISTENING", "CHOICE_SINGLE",
               "Listen to the campus announcement dialogue. What must students do to join the weekend field trip?",
               ["Submit the signed safety waiver by Thursday noon.", "Pay fifty dollars directly to the bus driver.", "Bring personal geological hammers from home.", "Pass an oral quiz before boarding."],
               "Submit the signed safety waiver by Thursday noon.",
               "Asisten dosen mengingatkan formulir persetujuan keselamatan bertanda tangan wajib dikumpulkan sebelum Kamis siang.",
               "ITP-MOD-02", "B1", audio_id="ITP05-A03",
               audio_turns=[("M", "Are we still leaving at seven Saturday for the limestone quarry trip?"), ("W", "Yes, just remember no one boards the van unless the signed safety waiver reaches my mailbox by Thursday noon."), ("N", "What requirement must students fulfill?")]),
            _q("ITP-L1-05-Q04", "LISTENING", "CHOICE_SINGLE",
               "Listen to the astronomy talk. What limits direct photography of most exoplanets?",
               ["Bright glare from their host stars hides faint planets.", "Space telescopes cannot operate outside Earth's clouds.", "Exoplanets reflect zero light from nearby stars.", "Cosmic dust blocks every radio signal from space."],
               "Bright glare from their host stars hides faint planets.",
               "Pembicara menjelaskan silau bintang induk mengaburkan planet di sekitarnya sehingga astronom memakai spektroskopi transit.",
               "ITP-MOD-02", "B2", audio_id="ITP05-A04",
               audio_turns=[("M", "Taking a direct photograph of an exoplanet is like spotting a firefly beside a lighthouse. Because host-star glare overwhelms faint planetary light, astronomers watch tiny dips and chemical absorptions during planetary transits."), ("N", "Why is direct imaging of exoplanets difficult?")]),
            _q("ITP-L1-05-Q05", "STRUCTURE", "CHOICE_SINGLE",
               "Public health officials recommended that the municipal reservoir ___ tested twice weekly after heavy rains.",
               ["be", "is", "was", "will be"],
               "be",
               "Kata kerja mandative subjunctive setelah 'recommended that ...' menggunakan bare infinitive 'be'.",
               "ITP-MOD-03", "B2"),
            _q("ITP-L1-05-Q06", "STRUCTURE", "CHOICE_SINGLE",
               "___ its low density, aerogel supports thousands of times its own weight without collapsing.",
               ["Despite", "Although", "Because", "Unless"],
               "Despite",
               "Diikuti noun phrase ('its low density') dan menyatakan kontras -> preposisi 'Despite'.",
               "ITP-MOD-03", "B2"),
            _q("ITP-L1-05-Q07", "STRUCTURE", "ERROR_IDENTIFICATION",
               "Neither the [A] chief meteorologist [B] nor her junior forecasters [C] was able to predict [D] the sudden hailstorm.",
               ["[A] chief meteorologist", "[B] nor", "[C] was able", "[D] the sudden hailstorm"],
               "[C] was able",
               "Pada 'Neither A nor B', kata kerja mengikuti subjek terdekat ('her junior forecasters' jamak) sehingga harus 'were able'.",
               "ITP-MOD-04", "B2"),
            _q("ITP-L1-05-Q08", "STRUCTURE", "ERROR_IDENTIFICATION",
               "The [A] newly discovered manuscripts [B] provide [C] valuable insights [D] onto seventeenth-century maritime trade.",
               ["[A] newly discovered", "[B] provide", "[C] valuable insights", "[D] onto"],
               "[D] onto",
               "Kolokasi preposisi baku untuk 'insights' adalah 'insights into', bukan 'onto'.",
               "ITP-MOD-04", "B2"),
            _q("ITP-L1-05-Q09", "READING", "CHOICE_SINGLE",
               "According to Passage 1 ('Reading Starlight Through Exoplanet Atmospheres'), how do astronomers identify specific atmospheric gases?",
               ["By matching missing light wavelengths to known gas absorption signatures", "By collecting dust samples with orbiting probes", "By measuring the planet's color in visible photographs", "By timing solar flares between adjacent galaxies"],
               "By matching missing light wavelengths to known gas absorption signatures",
               "Teks menyebut celah gelap pada panjang gelombang tertentu bertindak sebagai sidik jari kimia tiap gas.",
               "ITP-MOD-05", "B2", passage_id="ITP05-P1"),
            _q("ITP-L1-05-Q10", "READING", "CHOICE_SINGLE",
               "In Passage 1, the word 'skims' is closest in meaning to:",
               ["passes lightly across", "bounces violently back", "freezes inside", "blocks completely"],
               "passes lightly across",
               "'Skims through the planet's thin atmospheric rim' bermakna melintas tipis menyapu lapisan luar atmosfer.",
               "ITP-MOD-05", "B2", passage_id="ITP05-P1"),
            _q("ITP-L1-05-Q11", "READING", "CHOICE_SINGLE",
               "According to Passage 2 ('How Sequoyah Engineered the Cherokee Syllabary'), why could learners master the system quickly?",
               ["Each written character corresponded reliably to one spoken syllable.", "It borrowed English spelling conventions directly.", "Sequoyah taught every household in person.", "It reduced Cherokee speech to ten numerical digits."],
               "Each written character corresponded reliably to one spoken syllable.",
               "Paragraf menjelaskan konsistensi satu simbol per suku kata tanpa huruf bisu mempercepat penguasaan baca-tulis.",
               "ITP-MOD-05", "B2", passage_id="ITP05-P2"),
            _q("ITP-L1-05-Q12", "READING", "CHOICE_SINGLE",
               "In Passage 2, the word 'distilled' is closest in meaning to:",
               ["condensed", "translated", "erased", "complicated"],
               "condensed",
               "Menyaring bahasa lisan ke dalam 85 karakter suku kata berarti merangkum/menyuling ('condensed').",
               "ITP-MOD-05", "B2", passage_id="ITP05-P2"),
        ]
    }
]
