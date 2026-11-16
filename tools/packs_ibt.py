"""5 original sample packages for TOEFL iBT 2026 (IBT26-01..05, 12 items each = 60 items)."""


def _q(qid, sec, ttype, prompt, opts, ans, exp, src, diff="B2", passage_id=None, audio_id=None, audio_turns=None, accepted=None, extra=None):
    item = {
        "id": qid,
        "section": sec,
        "taskType": ttype,
        "prompt": prompt,
        "options": opts,
        "answer": ans,
        "acceptedAnswers": accepted if accepted is not None else ([ans] if ans else []),
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
    if extra:
        item.update(extra)
    return item


IBT_PACKS = [
    {
        "packId": "IBT26-01",
        "examId": "IBT_2026",
        "edition": "2026.1",
        "title": "iBT 2026 Paket 1 — Campus Operations & Environmental Science",
        "theme": "Complete the Words, campus notices, biochar soil trials, email & interview tasks.",
        "status": "SAMPLE",
        "blueprintCount": 120,
        "durationMinutes": 25,
        "blueprintDurationMinutes": 90,
        "license": "Original",
        "provenance": "EnglishPrepAI-Original-2026",
        "passages": [
            {
                "id": "IBT01-P1",
                "title": "Daily Life Notice: Campus Makerspace Safety Upgrade",
                "body": (
                    "To: Engineering & Design Students\n"
                    "Subject: Laser Cutter Access Starting Monday, March 9\n\n"
                    "During spring break, technicians installed upgraded exhaust filters in Makerspace Room 104. "
                    "Effective Monday, students may reserve 45-minute cutting slots online only after passing the updated "
                    "ten-minute ventilation quiz. Previous semester badges will remain valid for 3D printers, but laser "
                    "cutter access pauses automatically until the new quiz appears on your student ID profile. "
                    "Drop-in orientation runs Tuesday and Thursday at 1:00 PM."
                ),
                "readingOrder": 1
            },
            {
                "id": "IBT01-P2",
                "title": "Biochar Amendments in Sandy Agricultural Soils",
                "body": (
                    "Farmers cultivating sandy coastal plains face rapid nutrient leaching whenever heavy rain flushes "
                    "nitrogen past shallow crop roots. Agronomists have increasingly tested biochar—charcoal produced by "
                    "heating crop husks in low-oxygen kilns—as a porous soil amendment. Under scanning electron microscopes, "
                    "biochar granules exhibit honeycomb micro-channels that hold both moisture and positively charged ammonium "
                    "ions. In three-year field trials, plots blended with five percent biochar retained twenty-eight percent "
                    "more plant-available water during dry spells without altering soil acidity dramatically."
                ),
                "readingOrder": 2
            }
        ],
        "questions": [
            _q("IBT26-01-Q01", "READING", "FILL_BLANK",
               "Complete the Word: 'Library staff will arch___ fragile 19th-century maps inside humidity-controlled vaults.' (Write the full verb matching 'arch___')",
               [], "archive",
               "Kata kerja 'archive' melengkapi potongan 'arch___' agar sesuai konteks penyimpanan peta tua di brankas khusus.",
               "IBT-MOD-01", "B2", accepted=["archive"]),
            _q("IBT26-01-Q02", "READING", "CHOICE_SINGLE",
               "Based on the Makerspace Notice, what must a student with last semester's badge do to use the laser cutter?",
               ["Pass the updated ten-minute ventilation quiz before booking a slot.", "Re-take the full 3D printer orientation course.", "Request a paper form from the engineering dean.", "Wait until next autumn semester to reserve Room 104."],
               "Pass the updated ten-minute ventilation quiz before booking a slot.",
               "Pengumuman menjelaskan badge lama tetap berlaku untuk printer 3D, namun akses laser cutter tertahan sampai kuis ventilasi baru selesai.",
               "IBT-MOD-01", "B1", passage_id="IBT01-P1"),
            _q("IBT26-01-Q03", "READING", "CHOICE_SINGLE",
               "In Passage 2 ('Biochar Amendments'), why does the author mention honeycomb micro-channels?",
               ["To explain the physical structure enabling biochar to retain water and nutrients", "To criticize high costs of scanning electron microscopes", "To show biochar dissolves sandy clay within three seasons", "To argue kilns release excessive oxygen into coastal plains"],
               "To explain the physical structure enabling biochar to retain water and nutrients",
               "Struktur pori mikro seperti sarang lebah menjelaskan mekanisme fisik daya ikat air dan ion amonium.",
               "IBT-MOD-02", "B2", passage_id="IBT01-P2"),
            _q("IBT26-01-Q04", "LISTENING", "CHOICE_SINGLE",
               "Listen and Choose a Response: Select the most natural reply to the speaker.",
               ["Actually, the bookstore moved next to the dining commons last week.", "Yes, I read three chapters of economics yesterday.", "No, I rarely carry heavy winter coats in July.", "Thank you, the printer cartridge arrived on Tuesday."],
               "Actually, the bookstore moved next to the dining commons last week.",
               "Pembicara menanyakan arah ke toko buku kampus; balasan paling tepat memberi informasi lokasi terbaru toko buku.",
               "IBT-MOD-03", "B1", audio_id="IBT01-A01",
               audio_turns=[("W", "Excuse me, isn't the campus bookstore still on the ground floor of Miller Hall?")]),
            _q("IBT26-01-Q05", "LISTENING", "CHOICE_SINGLE",
               "Listen to the campus shuttle announcement. Which stop is temporarily suspended today?",
               ["The East Stadium loop stop until 5 PM", "The Main Library gate all weekend", "The Science Quad terminal next Monday", "All evening routes after 9 PM"],
               "The East Stadium loop stop until 5 PM",
               "Pengumuman menyebut perhentian East Stadium ditutup hingga pukul 17.00 akibat perbaikan pipa air.",
               "IBT-MOD-03", "B1", audio_id="IBT01-A02",
               audio_turns=[("M", "Attention campus riders: because crews are repairing a water main near East Stadium, Green Route buses will skip the stadium loop until 5 PM today. Please board at Oak Commons instead."), ("N", "Which stop is temporarily suspended?")]),
            _q("IBT26-01-Q06", "LISTENING", "CHOICE_SINGLE",
               "Listen to the short environmental science talk. What key advantage of biochar kilns is emphasized?",
               ["Heating husks without abundant oxygen preserves porous carbon structures instead of turning biomass purely into ash.", "Open bonfires produce stronger soil granules than sealed kilns.", "Biochar eliminates the need for rainfall across desert regions.", "Crop husks must be soaked in seawater before heating."],
               "Heating husks without abundant oxygen preserves porous carbon structures instead of turning biomass purely into ash.",
               "Pembicara mengontraskan pirolisis rendah oksigen (mempertahankan pori karbon) dengan pembakaran terbuka yang menyisakan abu.",
               "IBT-MOD-04", "B2", audio_id="IBT01-A03",
               audio_turns=[("W", "When agricultural husks burn in open air, almost everything turns to fine ash. By restricting oxygen inside a pyrolysis kiln, however, heat drives off volatile gases while locking carbon into a rigid porous sponge."), ("N", "What distinguishes low-oxygen kiln heating?")]),
            _q("IBT26-01-Q07", "WRITING", "SENTENCE_BUILD",
               "Build a grammatical question from the word tiles: 'Could you tell me ___?'",
               ["where", "the", "chemistry", "tutoring", "center", "moved"],
               "where|the|chemistry|tutoring|center|moved",
               "Embedded question sesudah 'Could you tell me' mengikuti pola Subjek + Kata Kerja ('where the chemistry tutoring center moved').",
               "IBT-MOD-05", "B2"),
            _q("IBT26-01-Q08", "WRITING", "WRITING_RESPONSE",
               "Write an Email (60–100 words): You missed Friday's mandatory lab safety orientation due to a documented clinic visit. Email your lab supervisor (Dr. Rivera) to apologize briefly, explain the situation, and request one alternative completion option before next week's lab.",
               [], "",
               "Email iBT 2026 dinilai dari kejelasan tujuan, nada profesional, pemenuhan ketiga poin prompt, dan ketepatan tata bahasa.",
               "IBT-MOD-05", "B2",
               extra={"minWords": 50, "recommendedWords": 80, "rubricCriteria": ["Clear greeting & purpose", "All 3 prompt points covered", "Polite academic tone", "Clean sentence mechanics"]}),
            _q("IBT26-01-Q09", "WRITING", "WRITING_RESPONSE",
               "Write for an Academic Discussion (100–140 words): Professor Kim asks: 'Should universities require all undergraduate students, regardless of major, to complete at least one environmental literacy course?' Maya argues yes because climate decisions affect every profession; Liam argues electives should remain flexible so technical majors graduate on time. State your view and engage their points.",
               [], "",
               "Diskusi akademik menuntut posisi tegas, respons langsung ke poin Maya/Liam, alasan mendalam, dan contoh spesifik.",
               "IBT-MOD-05", "B2",
               extra={"minWords": 95, "recommendedWords": 120, "rubricCriteria": ["Clear stance", "Engages Maya/Liam", "Original reasoning + example", "Cohesive academic prose"]}),
            _q("IBT26-01-Q10", "SPEAKING", "LISTEN_REPEAT",
               "Listen and Repeat: Play the audio prompt once, then record yourself repeating the exact sentence clearly with natural phrasing.",
               [], "Please submit your revised field report before noon on Friday.",
               "Perhatikan kejelasan konsonan akhir ('submit', 'revised', 'report') dan jeda alami sebelum 'before noon'.",
               "IBT-MOD-06", "B1", audio_id="IBT01-A04",
               audio_turns=[("W", "Please submit your revised field report before noon on Friday.")],
               extra={"referenceText": "Please submit your revised field report before noon on Friday."}),
            _q("IBT26-01-Q11", "SPEAKING", "SPEAKING_RESPONSE",
               "Take an Interview (Question 1 · ~45s): 'When learning a new technical skill, do you prefer following structured video demonstrations or experimenting directly with hands-on projects? Give one reason and an example.'",
               [], "",
               "Pakai pola Direct Answer -> Reason -> Concrete Example; jaga tempo stabil tanpa jeda panjang.",
               "IBT-MOD-06", "B2",
               extra={"prepSeconds": 0, "speakSeconds": 45, "rubricCriteria": ["Direct opinion", "Supporting reason + example", "Intelligibility & pacing", "Grammatical accuracy"]}),
            _q("IBT26-01-Q12", "SPEAKING", "SPEAKING_RESPONSE",
               "Take an Interview (Question 2 · ~45s): 'Some student clubs restrict meetings to members in a single major, while others stay open to everyone. Which policy benefits campus life more, and why?'",
               [], "",
               "Bandingkan kedua sisi secara singkat lalu tekankan satu manfaat utama dengan contoh kegiatan nyata.",
               "IBT-MOD-06", "B2",
               extra={"prepSeconds": 0, "speakSeconds": 45, "rubricCriteria": ["Clear comparison", "Coherent elaboration", "Natural lexical range", "Fluency"]}),
        ]
    },

    {
        "packId": "IBT26-02",
        "examId": "IBT_2026",
        "edition": "2026.1",
        "title": "iBT 2026 Paket 2 — Digital Humanities & Urban Transit",
        "theme": "Morphological word completion, transit policy announcements, OCR archives, and discussion writing.",
        "status": "SAMPLE",
        "blueprintCount": 120,
        "durationMinutes": 25,
        "blueprintDurationMinutes": 90,
        "license": "Original",
        "provenance": "EnglishPrepAI-Original-2026",
        "passages": [
            {
                "id": "IBT02-P1",
                "title": "Daily Life Email: City Bike-Share Semester Pass",
                "body": (
                    "Dear Commuter Pass Holder,\n"
                    "Effective April 1, your Student Transit Card unlocks unlimited 30-minute rides on CivicCycle electric "
                    "bikes at no extra charge. Trips exceeding 30 minutes incur a $0.15 per-minute fee unless the bike is "
                    "docked for at least two minutes before starting a fresh ride. To link your transit ID, tap 'University "
                    "Benefit' inside the station kiosk before your first unlock; customer service cannot apply the waiver "
                    "retroactively to past trips."
                ),
                "readingOrder": 1
            },
            {
                "id": "IBT02-P2",
                "title": "Optical Character Recognition on Nineteenth-Century Newspapers",
                "body": (
                    "Historians digitizing nineteenth-century gazettes rely heavily on Optical Character Recognition (OCR) "
                    "software to convert scanned column images into searchable text. Yet early rotary presses produced "
                    "uneven ink smudges, broken serifs, and tightly packed typefaces that cause generic OCR engines to "
                    "misread 'f' as long 's' or fuse adjacent words. Rather than manually retyping millions of pages, "
                    "computational linguists now train post-correction transformers on historical spelling dictionaries—cutting "
                    "character error rates below two percent across fragile regional archives."
                ),
                "readingOrder": 2
            }
        ],
        "questions": [
            _q("IBT26-02-Q01", "READING", "FILL_BLANK",
               "Complete the Word: 'Automated scanners now pre_____ fragile leather bindings from harsh direct light.' (Write the full verb matching 'pre_____')",
               [], "preserve",
               "'Preserve' melengkapi bentuk kata kerja dasar yang sesuai dengan makna melindungi jilid buku rapuh.",
               "IBT-MOD-01", "B2", accepted=["preserve"]),
            _q("IBT26-02-Q02", "READING", "CHOICE_SINGLE",
               "According to the Bike-Share Email, how can a rider take a 50-minute journey without paying extra per-minute fees?",
               ["Dock the bike after 30 minutes, wait at least two minutes, then start a second ride.", "Call customer service afterward to waive the 20-minute surcharge.", "Unlock a non-electric bike instead of an electric model.", "Activate the 'University Benefit' option after finishing the trip."],
               "Dock the bike after 30 minutes, wait at least two minutes, then start a second ride.",
               "Email menyatakan perjalanan >30 menit dikenai biaya kecuali sepeda dikembalikan minimal 2 menit sebelum memulai sesi baru.",
               "IBT-MOD-01", "B2", passage_id="IBT02-P1"),
            _q("IBT26-02-Q03", "READING", "CHOICE_SINGLE",
               "In Passage 2 ('OCR on Newspapers'), why did older OCR engines perform poorly on nineteenth-century gazettes?",
               ["Smudged ink and closely set historic typefaces confused character recognition.", "Microfilm reels were recorded exclusively in handwritten cursive.", "Modern keyboards lack nineteenth-century punctuation marks.", "Newspapers printed all columns in color ink."],
               "Smudged ink and closely set historic typefaces confused character recognition.",
               "Teks menunjuk noda tinta, serif patah, dan huruf rapat pada mesin cetak putar awal sebagai sumber kesalahan.",
               "IBT-MOD-02", "B2", passage_id="IBT02-P2"),
            _q("IBT26-02-Q04", "LISTENING", "CHOICE_SINGLE",
               "Listen and Choose a Response: Select the most appropriate reply.",
               ["Let's reschedule to Thursday at ten so both teams can join.", "The conference room holds roughly forty wooden chairs.", "I took the commuter rail back home last Friday.", "Yes, the coffee machine has already been unplugged."],
               "Let's reschedule to Thursday at ten so both teams can join.",
               "Pembicara mengeluhkan bentrok jadwal jam 2 siang; respons paling tepat menawarkan penjadwalan ulang yang masuk akal.",
               "IBT-MOD-03", "B1", audio_id="IBT02-A01",
               audio_turns=[("M", "Half the design team has an exam during our two o'clock project check-in.")]),
            _q("IBT26-02-Q05", "LISTENING", "CHOICE_SINGLE",
               "Listen to the conversation. What does the librarian suggest for finding regional census tables?",
               ["Use the digitized archive index before requesting physical microfilm.", "Wait until next month for the printed annex catalog.", "Search only local newspaper headlines from 1950.", "Visit the city hall basement without an appointment."],
               "Use the digitized archive index before requesting physical microfilm.",
               "Pustakawan menjelaskan indeks digital memetakan nomor gulungan mikrofilm secara langsung.",
               "IBT-MOD-03", "B2", audio_id="IBT02-A02",
               audio_turns=[("W", "Do I have to scroll through twenty microfilm reels just to find one county census table?"), ("M", "Not at all—open the digitized archive index on Terminal 4 first; it lists the exact reel and frame number."), ("N", "What does the staff member advise?")]),
            _q("IBT26-02-Q06", "LISTENING", "CHOICE_SINGLE",
               "Listen to the urban planning talk. What changed after Oak Avenue removed curbside parking for dedicated bus lanes?",
               ["Bus travel times dropped by nearly a third while total corridor shopper counts rose.", "Traffic delays tripled across adjacent residential alleys.", "Local bakery sales vanished completely within sixty days.", "Buses stopped running after seven o'clock each evening."],
               "Bus travel times dropped by nearly a third while total corridor shopper counts rose.",
               "Pembicara melaporkan penurunan waktu tempuh bus hampir sepertiga serta peningkatan jumlah pengunjung koridor.",
               "IBT-MOD-04", "B2", audio_id="IBT02-A03",
               audio_turns=[("M", "Merchants initially worried that converting Oak Avenue parking bays into dedicated bus lanes would hurt retail sales. Instead, bus travel times fell by thirty-one percent, and pedestrian foot traffic to shops actually climbed twelve percent."), ("N", "What happened after bus lanes replaced curbside parking?")]),
            _q("IBT26-02-Q07", "WRITING", "SENTENCE_BUILD",
               "Build a grammatical sentence from the tiles: 'The historian wondered ___.'",
               ["how", "many", "issues", "survived", "the", "1906", "fire"],
               "how|many|issues|survived|the|1906|fire",
               "Susunan yang benar adalah 'how many issues survived the 1906 fire'.",
               "IBT-MOD-05", "B2"),
            _q("IBT26-02-Q08", "WRITING", "WRITING_RESPONSE",
               "Write an Email (60–100 words): You are coordinating a student urban-design exhibit and need permission from the Facilities Manager (Mr. Okafor) to display three lightweight poster boards in the Atrium Lobby next Friday. State event timing, confirm no wall tape will be used, and ask for approval.",
               [], "",
               "Pastikan detail waktu, jaminan tidak merusak dinding atrium, dan permintaan konfirmasi tertulis tercakup ringkas.",
               "IBT-MOD-05", "B2",
               extra={"minWords": 50, "recommendedWords": 80, "rubricCriteria": ["Clear subject & purpose", "Time + zero-damage assurance", "Polite closing CTA", "Accurate grammar"]}),
            _q("IBT26-02-Q09", "WRITING", "WRITING_RESPONSE",
               "Write for an Academic Discussion (100–140 words): Dr. Alvarez asks: 'Should cities convert downtown streets into car-free pedestrian zones even if delivery trucks face stricter morning schedules?' Elena supports the shift for cleaner air and small cafes; Marcus warns elder commuters rely on drop-off lanes. Contribute your perspective.",
               [], "",
               "Sebutkan sudut pandang Elena/Marcus lalu bangun solusi atau posisi yang terstruktur dan spesifik.",
               "IBT-MOD-05", "B2",
               extra={"minWords": 95, "recommendedWords": 120, "rubricCriteria": ["Addresses both peers' trade-offs", "Clear stance", "Concrete urban example", "Cohesion"]}),
            _q("IBT26-02-Q10", "SPEAKING", "LISTEN_REPEAT",
               "Listen and Repeat: Play the audio prompt once, then record yourself repeating the sentence clearly.",
               [], "Express buses depart every twelve minutes from the northern transit hub.",
               "Latih pengucapan tepat angka 'twelve' dan artikulasi 'northern transit hub'.",
               "IBT-MOD-06", "B1", audio_id="IBT02-A04",
               audio_turns=[("M", "Express buses depart every twelve minutes from the northern transit hub.")],
               extra={"referenceText": "Express buses depart every twelve minutes from the northern transit hub."}),
            _q("IBT26-02-Q11", "SPEAKING", "SPEAKING_RESPONSE",
               "Take an Interview (Question 1 · ~45s): 'When researching a historical topic, do you think digitized searchable archives make physical library visits unnecessary? Explain your reasoning.'",
               [], "",
               "Nyatakan sikap jelas, jelaskan satu kelebihan arsip digital sekaligus satu nilai unik kunjungan fisik.",
               "IBT-MOD-06", "B2",
               extra={"prepSeconds": 0, "speakSeconds": 45, "rubricCriteria": ["Balanced thesis", "Concrete example", "Fluid delivery", "Vocabulary precision"]}),
            _q("IBT26-02-Q12", "SPEAKING", "SPEAKING_RESPONSE",
               "Take an Interview (Question 2 · ~45s): 'Would you prefer living in a compact walkable neighborhood with small apartments or a quieter suburb requiring a daily commute? Why?'",
               [], "",
               "Fokus pada satu alasan utama (waktu komuter atau ketenangan) + contoh rutinitas harian.",
               "IBT-MOD-06", "B1",
               extra={"prepSeconds": 0, "speakSeconds": 45, "rubricCriteria": ["Direct answer", "Practical reason", "Smooth pacing", "Grammar control"]}),
        ]
    },

    {
        "packId": "IBT26-03",
        "examId": "IBT_2026",
        "edition": "2026.1",
        "title": "iBT 2026 Paket 3 — Biological Systems & Academic Seminars",
        "theme": "Mycorrhizal fungi, lab safety scheduling, sentence construction, and research seminar talks.",
        "status": "SAMPLE",
        "blueprintCount": 120,
        "durationMinutes": 25,
        "blueprintDurationMinutes": 90,
        "license": "Original",
        "provenance": "EnglishPrepAI-Original-2026",
        "passages": [
            {
                "id": "IBT03-P1",
                "title": "Daily Life Portal Message: Campus Clinic Flu Immunization",
                "body": (
                    "Student Health Services Notice:\n"
                    "Walk-in seasonal immunizations run Wednesday through Friday from 9:00 AM to 4:00 PM inside Pavilion B. "
                    "Bring your physical or mobile Student ID; insurance co-pays are waived for enrolled undergraduates. "
                    "Students with severe egg allergies should check the 'Alternative Form' box during pre-screening at the "
                    "reception tablet so nurses prepare a cell-based dose."
                ),
                "readingOrder": 1
            },
            {
                "id": "IBT03-P2",
                "title": "Mycorrhizal Carbon Exchange Beneath Boreal Forests",
                "body": (
                    "Beneath northern pine forests, threadlike fungal filaments known as mycorrhizae wrap around tree roots "
                    "to form a mutualistic trade network. Because fungi lack chlorophyll, they depend on trees for up to "
                    "twenty percent of the sugars synthesized via sunlight. In return, microscopic fungal hyphae penetrate "
                    "soil crevices far narrower than root hairs, mining bound phosphorus and nitrogen that trees cannot "
                    "extract alone. Isotopic tracer experiments demonstrate that shaded understory saplings even receive "
                    "emergency carbon pulses transferred from sunlit canopy trees through shared fungal threads."
                ),
                "readingOrder": 2
            }
        ],
        "questions": [
            _q("IBT26-03-Q01", "READING", "FILL_BLANK",
               "Complete the Word: 'Symbiotic fungi sign_______ expand the nutrient-absorbing surface area of pine roots.' (Write the full adverb matching 'sign_______')",
               [], "significantly",
               "Adverb 'significantly' memodifikasi kata kerja 'expand' ('meningkatkan secara signifikan').",
               "IBT-MOD-01", "B2", accepted=["significantly"]),
            _q("IBT26-03-Q02", "READING", "CHOICE_SINGLE",
               "According to the Campus Clinic Notice, how do students with severe egg allergies receive an appropriate vaccine?",
               ["By selecting the 'Alternative Form' option during tablet check-in at reception", "By emailing Pavilion B three weeks prior to Wednesday", "By paying an extra laboratory fee at the pharmacy", "By visiting only on Monday mornings"],
               "By selecting the 'Alternative Form' option during tablet check-in at reception",
               "Catatan menyatakan penderita alergi telur cukup menandai kotak 'Alternative Form' saat pra-skrining di tablet resepsionis.",
               "IBT-MOD-01", "B1", passage_id="IBT03-P1"),
            _q("IBT26-03-Q03", "READING", "CHOICE_SINGLE",
               "In Passage 2 ('Mycorrhizal Carbon Exchange'), what evidence supports the claim that canopy trees subsidize shaded saplings?",
               ["Isotopic tracer tests tracking carbon movement along shared fungal filaments", "Measurements of needle color along sunlit forest edges", "Counts of fallen pine cones during snowy winters", "Records of commercial fertilizer spread in boreal plots"],
               "Isotopic tracer tests tracking carbon movement along shared fungal filaments",
               "Kalimat terakhir menyebut eksperimen pelacak isotop membuktikan aliran karbon dari pohon kanopi ke anakan ternaungi.",
               "IBT-MOD-02", "B2", passage_id="IBT03-P2"),
            _q("IBT26-03-Q04", "LISTENING", "CHOICE_SINGLE",
               "Listen and Choose a Response: Select the most helpful reply.",
               ["Check the reserve shelf behind the circulation desk—the professor placed two copies there.", "No, the biology building closes at six on Saturdays.", "I bought three highlighters and a blue binder this morning.", "Yes, genetics homework usually takes an hour."],
               "Check the reserve shelf behind the circulation desk—the professor placed two copies there.",
               "Pembicara mencari buku ajar yang habis dipinjam; memberi tahu adanya salinan cadangan di rak reserve adalah balasan paling relevan.",
               "IBT-MOD-03", "B1", audio_id="IBT03-A01",
               audio_turns=[("W", "All regular copies of the genetics textbook are checked out, and I need Chapter 4 tonight!")]),
            _q("IBT26-03-Q05", "LISTENING", "CHOICE_SINGLE",
               "Listen to the conversation. What does the teaching assistant recommend before running the PCR machine?",
               ["Verify pipette calibration and label every sample tube clearly.", "Double the sample volume inside each micro-tube.", "Skip the control tube to save reagents.", "Turn off the cooling fan prior to loading tubes."],
               "Verify pipette calibration and label every sample tube clearly.",
               "Asisten laboratorium menekankan pengecekan kalibrasi pipet dan pelabelan tabung untuk menghindari kontaminasi silang.",
               "IBT-MOD-03", "B2", audio_id="IBT03-A02",
               audio_turns=[("M", "My last PCR run gave blank bands again."), ("W", "Before loading today's tray, double-check pipette calibration and label each cap clearly so control tubes aren't swapped."), ("N", "What does the TA advise doing first?")]),
            _q("IBT26-03-Q06", "LISTENING", "CHOICE_SINGLE",
               "Listen to the biology lecture clip. Why are fungal hyphae more effective than tree roots at gathering phosphorus?",
               ["Hyphae are far thinner, allowing them to squeeze into tiny mineral pores.", "Hyphae perform faster photosynthesis underground.", "Tree roots repel dissolved water molecules.", "Fungi convert granite stones directly into nitrogen gas."],
               "Hyphae are far thinner, allowing them to squeeze into tiny mineral pores.",
               "Pembicara menyoroti diameter hifa yang jauh lebih halus dibanding rambut akar sehingga sanggup menembus celah tanah sempit.",
               "IBT-MOD-04", "B2", audio_id="IBT03-A03",
               audio_turns=[("W", "A single tree root hair looks huge next to a mycorrhizal filament. Because fungal hyphae are roughly one-tenth as wide, they thread into microscopic soil pores where phosphorus clings tightly to mineral grains."), ("N", "Why can fungal filaments access phosphorus trees miss?")]),
            _q("IBT26-03-Q07", "WRITING", "SENTENCE_BUILD",
               "Build a grammatical sentence from the tiles: 'Researchers tested ___.'",
               ["whether", "shaded", "seedlings", "received", "extra", "sugars"],
               "whether|shaded|seedlings|received|extra|sugars",
               "Klausa nomina 'whether shaded seedlings received extra sugars' berfungsi sebagai objek langsung kata kerja 'tested'.",
               "IBT-MOD-05", "B2"),
            _q("IBT26-03-Q08", "WRITING", "WRITING_RESPONSE",
               "Write an Email (60–100 words): You noticed a scheduling clash between your Botany field trip and an honors colloquium on April 14. Email your Botany instructor (Prof. Nair) politely asking if you may complete an independent specimen log instead of attending the field trip.",
               [], "",
               "Cantumkan tanggal bentrok (April 14), alasan akademis singkat, dan usulan tugas pengganti konkret.",
               "IBT-MOD-05", "B2",
               extra={"minWords": 50, "recommendedWords": 80, "rubricCriteria": ["Clear purpose & date", "Concrete alternative proposal", "Respectful academic register", "Grammar precision"]}),
            _q("IBT26-03-Q09", "WRITING", "WRITING_RESPONSE",
               "Write for an Academic Discussion (100–140 words): Dr. Sato asks: 'Should undergraduate biology programs replace some dissection labs with interactive 3D simulations?' Sofia supports simulations for repeatability and ethical benefits; Noah believes real tissue texture teaches essential clinical judgment. State and defend your view.",
               [], "",
               "Pertimbangkan kedua argumen Sofia & Noah lalu jelaskan pendekatan terbaik beserta alasan praktis.",
               "IBT-MOD-05", "B2",
               extra={"minWords": 95, "recommendedWords": 120, "rubricCriteria": ["Addresses peers directly", "Well-developed argument", "Domain vocabulary", "Cohesive structure"]}),
            _q("IBT26-03-Q10", "SPEAKING", "LISTEN_REPEAT",
               "Listen and Repeat: Play the audio prompt, then record yourself repeating it clearly.",
               [], "Please wear protective eyewear whenever entering the microscopy suite.",
               "Jaga penekanan di 'protective eyewear' dan bunyi akhir 'microscopy suite'.",
               "IBT-MOD-06", "B1", audio_id="IBT03-A04",
               audio_turns=[("W", "Please wear protective eyewear whenever entering the microscopy suite.")],
               extra={"referenceText": "Please wear protective eyewear whenever entering the microscopy suite."}),
            _q("IBT26-03-Q11", "SPEAKING", "SPEAKING_RESPONSE",
               "Take an Interview (Question 1 · ~45s): 'Do you learn science concepts more effectively through solo reading or small-group lab discussions? Give one specific example.'",
               [], "",
               "Mulai dengan preferensi tegas, beri alasan kognitif/praktis, lalu sertakan contoh proyek kelas.",
               "IBT-MOD-06", "B2",
               extra={"prepSeconds": 0, "speakSeconds": 45, "rubricCriteria": ["Clear stance", "Specific example", "Natural pacing", "Accuracy"]}),
            _q("IBT26-03-Q12", "SPEAKING", "SPEAKING_RESPONSE",
               "Take an Interview (Question 2 · ~45s): 'Should universities fund long-term basic ecological research even when commercial applications remain uncertain? Why or why not?'",
               [], "",
               "Hubungkan penelitian dasar dengan ketahanan ekosistem jangka panjang atau temuan tak terduga.",
               "IBT-MOD-06", "C1",
               extra={"prepSeconds": 0, "speakSeconds": 45, "rubricCriteria": ["Analytical depth", "Clear signposting", "Vocabulary range", "Fluency"]}),
        ]
    },

    {
        "packId": "IBT26-04",
        "examId": "IBT_2026",
        "edition": "2026.1",
        "title": "iBT 2026 Paket 4 — Economics, Daily Life & Technical Interviews",
        "theme": "Behavioral nudges, refund policies, paragraph ordering, and professional email tasks.",
        "status": "SAMPLE",
        "blueprintCount": 120,
        "durationMinutes": 25,
        "blueprintDurationMinutes": 90,
        "license": "Original",
        "provenance": "EnglishPrepAI-Original-2026",
        "passages": [
            {
                "id": "IBT04-P1",
                "title": "Daily Life Policy: Campus Conference Registration Refunds",
                "body": (
                    "Annual Undergraduate Research Symposium — Fee Policy:\n"
                    "Presenters pay a $25 registration fee covering lunch and printed abstracts. Cancellations submitted "
                    "via the portal at least ten calendar days prior to May 2 receive a full refund minus a $3 processing "
                    "charge. Within ten days of the event, refunds are unavailable, though presenters may transfer their "
                    "slot to a co-author without penalty by emailing symposium@campus.edu."
                ),
                "readingOrder": 1
            },
            {
                "id": "IBT04-P2",
                "title": "Default Choices in Household Energy Conservation",
                "body": (
                    "Conventional public-utility campaigns often assume consumers adopt cleaner electricity plans purely after "
                    "reading cost spreadsheets. Field experiments across mid-sized towns reveal a stronger behavioral lever: "
                    "default enrollment. When municipalities set modest time-of-use conservation plans as the automatic "
                    "option while letting households opt out via a single postcard, participation climbed from eighteen percent "
                    "to over seventy-four percent. Economists attribute this gap to status-quo bias rather than indifference, "
                    "since exit rates remained below five percent even one year later."
                ),
                "readingOrder": 2
            }
        ],
        "questions": [
            _q("IBT26-04-Q01", "READING", "FILL_BLANK",
               "Complete the Word: 'Households could easily opt out without paying any admin________ penalty.' (Write the full adjective matching 'admin________')",
               [], "administrative",
               "Adjektiva 'administrative' memodifikasi nomina 'penalty' (denda administratif).",
               "IBT-MOD-01", "B2", accepted=["administrative"]),
            _q("IBT26-04-Q02", "READING", "CHOICE_SINGLE",
               "If a presenter cancels 4 days before May 2, what option avoids losing the registration value?",
               ["Transferring the presentation slot to a co-author by email", "Requesting a full $25 refund on the portal", "Paying a $3 late cancellation charge", "Submitting abstract edits after May 2"],
               "Transferring the presentation slot to a co-author by email",
               "Dalam rentang kurang dari 10 hari, pengembalian dana tidak tersedia, namun pengalihan ke rekan penulis melalui email bebas penalti.",
               "IBT-MOD-01", "B2", passage_id="IBT04-P1"),
            _q("IBT26-04-Q03", "READING", "REORDER",
               "Order these 4 research steps from Passage 2 logically (1 = earliest step, 4 = conclusion):",
               ["Utility mails default enrollment notice with simple opt-out card", "Households remain enrolled unless opting out", "Participation exceeds 74% compared with 18% opt-in rate", "Economists verify low 1-year exit rates reflect status-quo bias"],
               "Utility mails default enrollment notice with simple opt-out card|Households remain enrolled unless opting out|Participation exceeds 74% compared with 18% opt-in rate|Economists verify low 1-year exit rates reflect status-quo bias",
               "Urutan kausal bergerak dari intervensi default -> perilaku rumah tangga -> hasil partisipasi -> interpretasi ekonomi setahun kemudian.",
               "IBT-MOD-02", "B2", passage_id="IBT04-P2"),
            _q("IBT26-04-Q04", "LISTENING", "CHOICE_SINGLE",
               "Listen and Choose a Response: Select the most appropriate reply.",
               ["Yes, please attach the latest budget spreadsheet so I can review it beforehand.", "The elevator on the west side stops at floor four.", "I ordered two boxes of envelopes yesterday afternoon.", "No, the stadium lights stay off until Friday."],
               "Yes, please attach the latest budget spreadsheet so I can review it beforehand.",
               "Menjawab tawaran pengiriman agenda dan angka anggaran rapat secara konstruktif.",
               "IBT-MOD-03", "B1", audio_id="IBT04-A01",
               audio_turns=[("M", "Would you like me to send over the club budget draft before tomorrow's finance meeting?")]),
            _q("IBT26-04-Q05", "LISTENING", "CHOICE_SINGLE",
               "Listen to the conversation. Why is the student asking about work-study hours?",
               ["She wants to check if adding a second campus job exceeds weekly limits.", "She forgot how to submit her bank account form.", "She plans to graduate two semesters early.", "She needs permission to miss economics class."],
               "She wants to check if adding a second campus job exceeds weekly limits.",
               "Mahasiswa memastikan total jam kerja gabungan antara perpustakaan dan lab komputer tidak melampaui batas mingguan.",
               "IBT-MOD-03", "B2", audio_id="IBT04-A02",
               audio_turns=[("W", "I currently work eight hours weekly at the library, and the economics lab offered me ten hours. Does campus policy allow eighteen total hours?"), ("M", "During regular terms the cap is twenty hours combined, so eighteen stays safely within guidelines."), ("N", "Why did the student inquire about hours?")]),
            _q("IBT26-04-Q06", "LISTENING", "CHOICE_SINGLE",
               "Listen to the economics talk. Why did default enrollment raise participation dramatically without coercion?",
               ["Many residents stick with pre-selected options when switching feels mildly effortful.", "Utilities charged triple fees to anyone returning opt-out postcards.", "Residents had no legal right to switch back later.", "Advertisements ran continuously on local television."],
               "Many residents stick with pre-selected options when switching feels mildly effortful.",
               "Pembicara menjelaskan bias status-quo: masyarakat cenderung bertahan pada opsi bawaan saat opsi keluar tetap bebas dan mudah.",
               "IBT-MOD-04", "B2", audio_id="IBT04-A03",
               audio_turns=[("M", "Why did switching from opt-in to opt-out quadruple clean-energy enrollment even though leaving took one stamp? Behavioral economists point to friction and status-quo bias—people stick with sensible defaults when no penalty blocks opting out."), ("N", "What drives high retention in default programs?")]),
            _q("IBT26-04-Q07", "WRITING", "SENTENCE_BUILD",
               "Build a grammatical sentence from the tiles: 'Economists measured ___.'",
               ["how", "quickly", "households", "adjusted", "peak", "usage"],
               "how|quickly|households|adjusted|peak|usage",
               "Susunan tepat: 'how quickly households adjusted peak usage'.",
               "IBT-MOD-05", "B2"),
            _q("IBT26-04-Q08", "WRITING", "WRITING_RESPONSE",
               "Write an Email (60–100 words): You are treasurer of the Economics Society and accidentally submitted an outdated receipt PDF in your reimbursement request to Student Activities (Ms. Brooks). Apologize concisely, explain that the corrected receipt is attached, and confirm the final total ($48.50).",
               [], "",
               "Email harus jelas menyebutkan kesalahan dokumen lama, lampiran baru, dan angka pasti $48.50.",
               "IBT-MOD-05", "B2",
               extra={"minWords": 50, "recommendedWords": 75, "rubricCriteria": ["States exact correction ($48.50)", "Concise professional apology", "Clear follow-up", "Grammar accuracy"]}),
            _q("IBT26-04-Q09", "WRITING", "WRITING_RESPONSE",
               "Write for an Academic Discussion (100–140 words): Prof. Chen asks: 'Should undergraduate economics courses focus more on real-world policy case studies even if it leaves less time for abstract mathematical proofs?' Clara favors case studies for career readiness; Ben argues mathematical foundations prevent shallow analysis. Share your stance.",
               [], "",
               "Integrasikan kedua poin Clara dan Ben sambil merumuskan keseimbangan atau prioritas yang didukung alasan.",
               "IBT-MOD-05", "B2",
               extra={"minWords": 95, "recommendedWords": 120, "rubricCriteria": ["Engages both classmates", "Coherent stance", "Relevant illustration", "Academic vocabulary"]}),
            _q("IBT26-04-Q10", "SPEAKING", "LISTEN_REPEAT",
               "Listen and Repeat: Play the audio prompt once, then record yourself clearly.",
               [], "Quarterly reimbursement forms must include itemized receipts and advisor signatures.",
               "Pertahankan kejelasan gugus konsonan pada 'Quarterly', 'reimbursement', dan 'itemized receipts'.",
               "IBT-MOD-06", "B2", audio_id="IBT04-A04",
               audio_turns=[("M", "Quarterly reimbursement forms must include itemized receipts and advisor signatures.")],
               extra={"referenceText": "Quarterly reimbursement forms must include itemized receipts and advisor signatures."}),
            _q("IBT26-04-Q11", "SPEAKING", "SPEAKING_RESPONSE",
               "Take an Interview (Question 1 · ~45s): 'When managing personal finances as a student, is a fixed weekly budget or tracking every transaction in an app more helpful for you? Explain why.'",
               [], "",
               "Pilih satu pendekatan, jelaskan alasannya secara praktis, dan beri satu contoh kebiasaan pribadi.",
               "IBT-MOD-06", "B1",
               extra={"prepSeconds": 0, "speakSeconds": 45, "rubricCriteria": ["Direct choice", "Concrete reason", "Clear pronunciation", "Coherence"]}),
            _q("IBT26-04-Q12", "SPEAKING", "SPEAKING_RESPONSE",
               "Take an Interview (Question 2 · ~45s): 'Do automatic reminders help people build sustainable habits, or do too many notifications eventually get ignored? Explain with an example.'",
               [], "",
               "Beri perspektif bernuansa tentang manfaat awal vs kejenuhan notifikasi disertai contoh nyata.",
               "IBT-MOD-06", "B2",
               extra={"prepSeconds": 0, "speakSeconds": 45, "rubricCriteria": ["Nuanced stance", "Concrete illustration", "Smooth flow", "Lexical range"]}),
        ]
    },

    {
        "packId": "IBT26-05",
        "examId": "IBT_2026",
        "edition": "2026.1",
        "title": "iBT 2026 Paket 5 — Earth & Cognitive Sciences Simulation",
        "theme": "Sleep memory consolidation, dormitory lab advisories, comprehensive 2026 task mix.",
        "status": "SAMPLE",
        "blueprintCount": 120,
        "durationMinutes": 25,
        "blueprintDurationMinutes": 90,
        "license": "Original",
        "provenance": "EnglishPrepAI-Original-2026",
        "passages": [
            {
                "id": "IBT05-P1",
                "title": "Daily Life Notice: Geology Specimen Cabinet Relocation",
                "body": (
                    "Department Announcement:\n"
                    "Mineral reference trays normally stored in Hall 210 are moving to Earth Sciences Room 118 between "
                    "May 4 and May 6. During those three days, students studying for mineralogy lab practicals may inspect "
                    "high-resolution 3D scans via the course portal or check out portable sample kits for 2-hour library "
                    "use with a faculty signature."
                ),
                "readingOrder": 1
            },
            {
                "id": "IBT05-P2",
                "title": "Slow-Wave Sleep and Declarative Memory Transfer",
                "body": (
                    "Neuroscientists long wondered why short nightly study reviews consistently outperform marathon all-night "
                    "cramming before exams. Polysomnography studies show that during deep slow-wave sleep, brief electrical "
                    "bursts called sleep spindles coordinate synchronized firing between the hippocampus—which holds fragile "
                    "newly learned facts—and the neocortex, where durable long-term memories reside. When volunteers stay "
                    "awake through the night after memorizing vocabulary pairs, this overnight transfer fails to occur, "
                    "leaving recall accuracy nearly forty percent lower two days later even after recovery sleep."
                ),
                "readingOrder": 2
            }
        ],
        "questions": [
            _q("IBT26-05-Q01", "READING", "FILL_BLANK",
               "Complete the Word: 'Repeated sleep spindles help cons_______ fragile facts into durable cortical memory.' (Write the full verb matching 'cons_______')",
               [], "consolidate",
               "'Consolidate' berarti menguatkan/memadatkan ingatan baru menjadi memori jangka panjang.",
               "IBT-MOD-01", "B2", accepted=["consolidate"]),
            _q("IBT26-05-Q02", "READING", "CHOICE_SINGLE",
               "During May 4–6, how can a student physically examine mineral samples while Hall 210 cabinets are moving?",
               ["Borrow a portable sample kit for two hours in the library with faculty approval.", "Enter Hall 210 after 6 PM when movers leave.", "Pick up trays directly from Room 118 without permission.", "Wait until May 10 because no kits or scans exist."],
               "Borrow a portable sample kit for two hours in the library with faculty approval.",
               "Pengumuman menyebut mahasiswa bisa meminjam kit sampel portabel selama 2 jam di perpustakaan dengan tanda tangan dosen.",
               "IBT-MOD-01", "B1", passage_id="IBT05-P1"),
            _q("IBT26-05-Q03", "READING", "CHOICE_SINGLE",
               "According to Passage 2 ('Slow-Wave Sleep'), what happens if learners miss sleep on the night immediately following study?",
               ["Recovery sleep two nights later cannot fully restore the lost consolidation transfer.", "The hippocampus erases all prior childhood memories.", "Sleep spindles double in speed during daytime classes.", "All-night cramming produces higher retention after 48 hours."],
               "Recovery sleep two nights later cannot fully restore the lost consolidation transfer.",
               "Kalimat terakhir menegaskan akurasi mengingat tetap ~40% lebih rendah dua hari kemudian meski sudah mengganti tidur.",
               "IBT-MOD-02", "B2", passage_id="IBT05-P2"),
            _q("IBT26-05-Q04", "LISTENING", "CHOICE_SINGLE",
               "Listen and Choose a Response: Select the most appropriate reply.",
               ["Sure, let's split the slides so you cover methods and I present results.", "I left my umbrella inside the campus cafeteria.", "Actually, psychology exams always include fifty desks.", "No, the fountain near West Gate runs every spring."],
               "Sure, let's split the slides so you cover methods and I present results.",
               "Pembicara menawarkan pembagian bagian presentasi; membagi porsi metode dan hasil adalah respons paling logis.",
               "IBT-MOD-03", "B1", audio_id="IBT05-A01",
               audio_turns=[("W", "Want to divide Monday's cognitive science slides before we rehearse tonight?")]),
            _q("IBT26-05-Q05", "LISTENING", "CHOICE_SINGLE",
               "Listen to the conversation. What misconception does the professor correct about memory?",
               ["Rereading notes feels productive yet yields weaker recall than active self-testing.", "Highlighting every sentence boosts long-term recall best.", "Listening to music eliminates the need for sleep.", "Flashcards work only for mathematics formulas."],
               "Rereading notes feels productive yet yields weaker recall than active self-testing.",
               "Dosen menjelaskan ilusi kelancaran dari membaca ulang pasif dibanding kekuatan *retrieval practice*.",
               "IBT-MOD-03", "B2", audio_id="IBT05-A02",
               audio_turns=[("M", "I reread my neuroscience chapters four times, yet froze on the quiz."), ("W", "Passive rereading creates familiarity without retrieval practice. Testing yourself from memory—even briefly—builds stronger recall pathways."), ("N", "What advice does the professor give?")]),
            _q("IBT26-05-Q06", "LISTENING", "CHOICE_SINGLE",
               "Listen to the geology talk. How do quartz crystals record past earthquakes along fault zones?",
               ["Trace heat fractures and fluid inclusions capture brief high-temperature slip events.", "Quartz dissolves completely during minor earth tremors.", "Earthquakes turn quartz into soft liquid clay.", "Crystals emit audible chimes before faults shift."],
               "Trace heat fractures and fluid inclusions capture brief high-temperature slip events.",
               "Pembicara menjelaskan retakan termal dan inklusi fluida pada kuarsa merekam riwayat gesekan panas gempa purba.",
               "IBT-MOD-04", "B2", audio_id="IBT05-A03",
               audio_turns=[("M", "When fault lines grind suddenly during an earthquake, frictional heat flashes along mineral veins. Microscopic fluid inclusions sealed inside fractured quartz crystals act like tiny thermometers preserving evidence of ancient slip events."), ("N", "How do quartz crystals preserve earthquake evidence?")]),
            _q("IBT26-05-Q07", "WRITING", "SENTENCE_BUILD",
               "Build a grammatical sentence from the tiles: 'Neuroscientists investigated ___.'",
               ["why", "sleep", "spindles", "strengthen", "overnight", "memory"],
               "why|sleep|spindles|strengthen|overnight|memory",
               "Susunan klausa objek yang tepat adalah 'why sleep spindles strengthen overnight memory'.",
               "IBT-MOD-05", "B2"),
            _q("IBT26-05-Q08", "WRITING", "WRITING_RESPONSE",
               "Write an Email (60–100 words): You need access to the Psychology Quiet Testing Booth on Wednesday from 2:00 to 3:30 PM to pilot-test an offline reading comprehension task with 4 volunteers. Email Lab Coordinator Ms. Park stating date/time, equipment needed (none—students bring tablets), and requesting booking confirmation.",
               [], "",
               "Email efektif mencakup waktu persis, jumlah peserta, kebutuhan peralatan, dan permintaan konfirmasi.",
               "IBT-MOD-05", "B2",
               extra={"minWords": 50, "recommendedWords": 80, "rubricCriteria": ["Clear reservation details", "All prompt items covered", "Concise tone", "Clean grammar"]}),
            _q("IBT26-05-Q09", "WRITING", "WRITING_RESPONSE",
               "Write for an Academic Discussion (100–140 words): Dr. Morales asks: 'Should courses replace traditional two-hour final exams with multiple smaller cumulative quizzes throughout the term?' Dana argues smaller quizzes lower cramming stress and boost sleep; Trevor counters that comprehensive finals test deep synthesis across topics. Give your reasoned contribution.",
               [], "",
               "Tanggapi argumen Dana dan Trevor dengan perspektif terstruktur serta contoh pelaksanaan konkret.",
               "IBT-MOD-05", "B2",
               extra={"minWords": 95, "recommendedWords": 120, "rubricCriteria": ["Engages Dana/Trevor", "Clear thesis", "Practical rationale", "Academic cohesion"]}),
            _q("IBT26-05-Q10", "SPEAKING", "LISTEN_REPEAT",
               "Listen and Repeat: Play the audio prompt once, then record yourself accurately.",
               [], "Consistent nightly rest supports faster recall during complex problem solving.",
               "Jaga artikulasi 'consistent nightly rest' serta jeda singkat sebelum 'during complex'.",
               "IBT-MOD-06", "B2", audio_id="IBT05-A04",
               audio_turns=[("W", "Consistent nightly rest supports faster recall during complex problem solving.")],
               extra={"referenceText": "Consistent nightly rest supports faster recall during complex problem solving."}),
            _q("IBT26-05-Q11", "SPEAKING", "SPEAKING_RESPONSE",
               "Take an Interview (Question 1 · ~45s): 'Do you study more effectively in short focused intervals across several days or in longer uninterrupted blocks? Explain what works for you.'",
               [], "",
               "Gunakan contoh jadwal belajar nyata untuk memperkuat alasan Anda.",
               "IBT-MOD-06", "B1",
               extra={"prepSeconds": 0, "speakSeconds": 45, "rubricCriteria": ["Direct answer", "Real study example", "Fluency", "Grammar"]}),
            _q("IBT26-05-Q12", "SPEAKING", "SPEAKING_RESPONSE",
               "Take an Interview (Question 2 · ~45s): 'Should universities offer quiet, device-free study halls without Wi-Fi alongside modern digital labs? Why?'",
               [], "",
               "Jelaskan dampak distraksi digital vs kebutuhan akses sumber daya dengan alasan seimbang.",
               "IBT-MOD-06", "B2",
               extra={"prepSeconds": 0, "speakSeconds": 45, "rubricCriteria": ["Clear stance", "Cause-effect reasoning", "Vocabulary", "Delivery"]}),
        ]
    }
]
