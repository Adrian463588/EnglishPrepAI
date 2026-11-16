"""TOEFL iBT 2026 Writing (10 Build-a-Sentence + 1 Email + 1 Discussion) & Speaking (7 Listen&Repeat + 4 Interview) = 115 items."""

from pack_schema_helpers import make_q

# Format per Build-a-Sentence: (context, [ordered chunks], exp) -> shuffles deterministically
def _sb(qid: str, idx: int, ctx: str, ordered: list[str], exp: str, seed: int) -> dict:
    assert len(set(ordered)) == len(ordered), f"{qid}: duplicate word tokens in {ordered}"
    # Deterministic non-identity permutation preserving clear difficulty
    n = len(ordered)
    shuffled = ordered[2:] + ordered[:2] if n >= 4 else ordered[::-1]
    if shuffled == ordered:
        shuffled = ordered[::-1]
    if seed % 2 == 1 and len(shuffled) >= 4:
        shuffled[0], shuffled[-2] = shuffled[-2], shuffled[0]
    return make_q(
        qid=qid,
        section="WRITING",
        task_type="SENTENCE_BUILD",
        prompt=f"Build a Sentence {idx}/10 — Context: {ctx} | Arrange into one grammatical response:",
        options=shuffled,
        answer="|".join(ordered),
        explanation=exp,
        rationale_source="IBT-MOD-05",
        difficulty="B2",
    )


_BUILD_SENTENCE = {
    1: [
        ("Did the librarian say where old harbor atlases moved?", ["She", "explained", "where", "rare", "maps", "were", "stored."], "Embedded clause memakai urutan subjek + kata kerja ('where rare maps were stored')."),
        ("Why did irrigation engineers postpone burying soil probes?", ["They", "waited", "until", "damp", "earth", "settled", "snugly."], "Klausa waktu menggunakan 'until + S + V' secara urut."),
        ("Would Maya prefer borrowing dry-field boots before sunrise?", ["Ask", "whether", "she", "already", "packed", "spare", "boots."], "Indirect yes/no question memakai 'whether + S + packed...'."),
        ("How long does archival box board resist winter heat cycles?", ["Acid-free", "rag", "enclosures", "protect", "bindings", "for", "decades."], "Kalimat deklaratif S + V + O + keterangan waktu."),
        ("When will shuttle Gold-2 resume running beside East Stadium?", ["Transit", "staff", "confirmed", "Wednesday", "evening", "at", "six."], "Objek langsung mengikuti kata kerja lampau 'confirmed'."),
        ("What should students finish prior to laser cutter booking?", ["Everyone", "must", "complete", "that", "ten-minute", "ventilation", "quiz."], "Modal 'must' langsung diikuti bare verb 'complete' + objek."),
        ("Why do sandy plots benefit from adding five percent biochar?", ["Porous", "granules", "retain", "more", "available", "plant", "moisture."], "Frasa subjek jamak 'Porous granules' berpasangan dengan verba jamak 'retain'."),
        ("Did astronomers confirm whether dark clouds blocked Kepler mirrors?", ["Dr. Lin", "checked", "how", "thick", "haze", "dimmed", "readings."], "Embedded question menggunakan 'how + noun phrase + verb'."),
        ("How can small coastal farms keep harvested berries cool quickly?", ["Solar", "units", "chill", "fruit", "crates", "before", "noon."], "S + V + O + prepositional time phrase."),
        ("Did the professor mention if Flex Tokens cover lab checkoffs?", ["No,", "safety", "reflections", "always", "require", "live", "attendance."], "Adverb frekuensi 'always' ditempatkan tepat sebelum kata kerja utama 'require'."),
    ],
    2: [
        ("Could you verify why our OCR workshop seats filled fast?", ["Two", "guests", "canceled", "because", "fieldwork", "ran", "late."], "Klausa sebab-akibat dengan konjungsi 'because + S + V'."),
        ("Why did hard cement crack nineteenth-century soft salmon bricks?", ["Rigid", "mortar", "transferred", "freeze", "stress", "directly", "outward."], "Subjek + kata kerja transitif + objek + adverbia arah."),
        ("When do Writing Center peer consultations stay open late?", ["Evening", "sessions", "extend", "until", "ten", "this", "month."], "Pola subjek jamak + verba + batas waktu."),
        ("What does the curator want students to avoid on wooden book cradles?", ["Remember", "never", "to", "touch", "delicate", "bindings", "carelessly."], "Verba 'remember + never + to-infinitive' untuk larangan tindakan."),
        ("How can microfilm users escape harsh overhead eye glare?", ["Try", "switching", "on", "reverse", "polarity", "mode", "first."], "Imperatif 'Try + gerund' diikuti partikel dan objek."),
        ("Why did medieval harbor vaults stay dry despite high ocean tides?", ["Self-healing", "lime", "clasts", "sealed", "tiny", "hairline", "cracks."], "Subjek + kata kerja lampau + objek langsung."),
        ("Can campus commuters still unlock shared bikes past midnight Saturday?", ["Cellular", "swaps", "pause", "QR", "locks", "until", "dawn."], "Subjek + verba + objek + preposisi waktu."),
        ("Why were antique glass vault tiles glowing faintly violet today?", ["Long", "sunlight", "exposure", "altered", "clear", "manganese", "salts."], "Kalimat aktif lampau berobjek tunggal."),
        ("How should Courtyard Grill guests request gluten-free utensils cleanly?", ["Just", "ask", "line", "cooks", "for", "purple", "tools."], "Imperatif sopan dengan preposisi pasangan 'ask someone for something'."),
        ("Why do roadside bioswales keep muddy runoff away from rivers?", ["Porous", "roots", "absorb", "rainwater", "before", "curb", "floods."], "Klausa kompleks menghubungkan subjek vegetasi dengan efek serapan."),
    ],
    3: [
        ("Why did Lena warn assistants against freezing fresh damp leaves?", ["Trapped", "ice", "bursts", "green", "leaf", "cells", "black."], "Hasil kausatif menggambarkan perubahan warna akibat sel pecah."),
        ("Can climbers skip our ten-minute knot check showing gym cards?", ["Every", "newcomer", "still", "takes", "an", "in-house", "check."], "Subjek tunggal dengan verba berimbuhan -s."),
        ("How do subterranean fungi trade scarce minerals cleanly with firs?", ["Slender", "hyphae", "forage", "inside", "tiny", "rock", "pores."], "Verba intransitif diikuti preposisi arah 'inside'."),
        ("When must game-day cars leave Red Lot C Friday night?", ["Move", "permits", "before", "midnight", "to", "North", "Garage."], "Imperatif instruksi pemindahan izin parkir tepat waktu."),
        ("Why do twilight hatchetfish glow faintly across silver bellies below?", ["Matching", "downwelling", "light", "erases", "dark", "upward", "shadows."], "Gerund phrase sebagai subjek tunggal berpasangan dengan 'erases'."),
        ("Could someone check if Unit 2 Wi-Fi trusted campus servers?", ["Click", "Trust", "only", "after", "reading", "server", "names."], "Urutan imperatif bersyarat sebelum menekan tombol aman."),
        ("Why are ion beams favored over rigid diamond tools last?", ["Focused", "argon", "removes", "bumps", "without", "contact", "ripples."], "Preposisi 'without' diikuti nomina hasil efek samping."),
        ("Where do Wednesday Global Commons guests drink mint tea?", ["Grab", "free", "cups", "near", "Union", "Table", "Four."], "Kalimat ajakan tempat dengan frasa lokasi spesifik."),
        ("How do clover root nodules convert airborne nitrogen into fertilizer?", ["Housed", "microbes", "fix", "nitrogen", "inside", "pink", "pockets."], "Partisipial pembuka menerangkan bakteri simbiosis dalam bintil akar."),
        ("What makes nocturnal hawkmoths vulnerable beside bright white lamps?", ["Harsh", "glare", "pulls", "insects", "away", "from", "petals."], "Struktur aktif menjelaskan mekanisme gangguan polinasi malam."),
    ],
    4: [
        ("Can ECON 340 partners paste raw household spreadsheets onto websites?", ["Only", "summarized", "charts", "may", "appear", "outside", "clusters."], "Subjek terbatas 'Only summarized charts' dengan modal izin 'may appear'."),
        ("Why did finance clerks bounce her hotel expense report yesterday?", ["Card", "slips", "omitted", "itemized", "nightly", "room", "rates."], "Keterangan sebab penolakan tagihan tanpa rincian harian."),
        ("How can docents hook young kids right inside Fossil Hall?", ["Let", "visitors", "hold", "tactile", "ammonite", "casts", "first."], "Causative 'Let + object + bare infinitive' membuka interaksi."),
        ("Where should Green House cooks stick food storage identification names?", ["Press", "dated", "tape", "firmly", "onto", "every", "box."], "Imperatif instruksi pelabelan kotak makanan."),
        ("What protects seaside fog nets against ripping on sharp shackles?", ["Rubber", "sleeves", "cushion", "taut", "corner", "wire", "eyelets."], "Subjek benda jamak + verba + objek jelas."),
        ("Why did shoppers actually purchase more from tiny jam displays?", ["Smaller", "menus", "prevent", "mental", "fatigue", "and", "regret."], "Kalimat ringkas psikologi perilaku tentang beban pilihan."),
        ("How do farm chillers stop ripe tropical mangoes spoiling early?", ["Solar", "cooling", "slows", "fungal", "growth", "soon", "afterward."], "Gerund subjek tunggal + verba tunggal 'slows'."),
        ("What acoustic hint guided Upper Paleolithic ochre cave wall artists?", ["Vibrant", "echoes", "amplified", "ancient", "chants", "inside", "chambers."], "Struktur lampau menerangkan peran gema dalam pemilihan ruang."),
        ("Why won't dry mycelium bricks crumble inside damp autumn flowerbeds?", ["Natural", "microbes", "compost", "retired", "bumpers", "within", "days."], "Kalimat aktif menyoroti penguraian organik aman."),
        ("Who qualifies automatically for free flu shots in Courtyard Canopy?", ["All", "enrolled", "undergraduates", "carrying", "valid", "student", "ID."], "Frasa nomina panjang dengan partisipial aktif menerangkan persyaratan."),
    ],
    5: [
        ("What happens at five PM Friday if thick storm clouds gather?", ["Indoor", "planetarium", "dome", "shows", "still", "start", "safely."], "Subjek majemuk jamak dengan adverb 'still' dan 'safely'."),
        ("Why must LING 315 dialect interviews avoid compact MP3 encoding?", ["Lossy", "compression", "blurs", "tiny", "vowel", "formant", "peaks."], "Penjelasan teknis mengapa rekaman tak terkompresi wajib."),
        ("Where do Eco-Coop gardeners dump spotted withered tomato stalks?", ["Place", "diseased", "vines", "inside", "north", "fence", "barrels."], "Instruksi jelas mencegah spora menyebar ke kompos utama."),
        ("Can seniors get archival buckram hardcovers bound before graduation week?", ["Order", "sewn", "covers", "at", "least", "five", "weekdays", "early."], "Imperatif tenggat lima hari kerja dengan penekanan kuantitatif."),
        ("What tag shields confidential family dialect recordings inside Folder B?", ["Append", "private", "local", "markers", "onto", "file", "names."], "Instruksi penamaan berkas etika penelitian."),
        ("Why did dense red brick corridors hold warmth well past midnight?", ["Heavy", "masonry", "walls", "release", "stored", "heat", "slowly."], "Kalimat sebab-akibat fisika massa termal bangunan."),
        ("How do herbarium transcribers treat faint faded mountain village labels?", ["Wrap", "uncertain", "names", "inside", "clear", "square", "brackets."], "Kaidah pencatatan ketidakpastian paleografi pada data lapangan."),
        ("What happens neurologically during eight hours of deep night sleep?", ["Hippocampal", "replays", "anchor", "fresh", "words", "into", "memory."], "Pola proses konsolidasi memori jangka panjang."),
        ("Why do bare seaweed condiment sachets soften quickly in rainy climates?", ["Plant", "wax", "seals", "out", "humid", "coastal", "steam."], "Kalimat ringkas menjelaskan peran lapisan lilin pelindung kelembapan."),
        ("What quiet maritime adjustment trims both ship fuel and underwater roar?", ["Slowing", "two", "knots", "cuts", "cavitation", "noise", "steeply."], "Gerund frasa subjek + verba tunggal 'cuts'."),
    ],
}

# Fix Build-a-Sentence #4 in Pack 2 cleanly (replacing the 'Wait: check grammar!' self-note):
_BUILD_SENTENCE[2][3] = (
    "What does the curator want students to remember near cradles?",
    ["Always", "handle", "fragile", "edges", "cleanly", "without", "flash."],
    "Imperatif baku 'Always + V1 + object + manner' mengingatkan etiket penangan arsip.",
)

_WRITING_PROMPTS = {
    1: (
        "Email — Field Lab Equipment Schedule Conflict\nWrite an email (70–120 words) to Prof. Vega explaining why your biology lab group needs to move Wednesday's greenhouse soil-sensor setup to Thursday at 2:00 PM, referencing one weather/schedule reason and asking politely for confirmation.",
        "Academic Discussion — Campus Energy Priorities\nDr. Santos asks: 'Should universities invest scarce sustainability grants first in smart solar microgrid batteries or deep-retrofit thermal window insulation across older classrooms?' Student A argues batteries slash blackout risks; Student B favors insulation cutting heating bills daily. Contribute a clear 100–130 word post weighing both trade-offs.",
    ),
    2: (
        "Email — Special Collections Print Permission Request\nWrite a formal email (70–120 words) to Archivist Patel requesting permission to include two 1904 waterfront elevation figures from Folio 7 in your undergraduate senior architecture monograph, noting exact citation credit and asking for format guidelines.",
        "Academic Discussion — Preserving Industrial Neighborhood Character\nProf. Lim asks: 'When waterfront districts build modern commuter bikeways beside 19th-century brick warehouses, should councils enforce strict exterior facade preservation or allow flexible adaptive glass extensions?' Student A defends brick identity; Student B prefers sunny adaptive skylights. Write a 100–130 word scholarly response.",
    ),
    3: (
        "Email — Acoustic Lab Rehearsal Booking Extension\nWrite an email (70–120 words) to Studio Manager Kim asking to extend your acoustics quartet test inside Room 208 by forty minutes this Friday so students can log both draped and un-draped decay curves cleanly, noting you will restore curtains afterward.",
        "Academic Discussion — Reducing Nighttime Streetlight Impacts\nDr. Flores asks: 'To protect nocturnal moth pollination around suburban berry farms, should rural counties dim highway streetlamps after midnight or switch solely to downward shielded warm-amber LEDs?' Engage thoughtfully with both safety and ecology across 100–130 words.",
    ),
    4: (
        "Email — Resolving Reimbursement Folio Documentation\nWrite a polite email (70–120 words) to the Travel Grants Desk attaching your newly acquired zero-balance hotel folio (replacing the earlier card total) and asking whether your undergraduate conference travel claim can enter this Friday's payout cycle.",
        "Academic Discussion — Designing Opt-Out Environmental Programs\nProf. Nair asks: 'Do opt-out municipal composting programs—where households enroll by default unless opting out—cross an ethical boundary compared with voluntary signup flyers?' Evaluate autonomy vs real-world behavioral friction cleanly in 100–130 words.",
    ),
    5: (
        "Email — Department Honors Hardcover Extension Notice\nWrite a concise email (70–120 words) to Honors Chair Alvarez explaining that because buckram off-site bindery runs five weekdays past April 28, you will submit a heavyweight black-spiral reading copy before May 2 while the hardcover arrives next Tuesday.",
        "Academic Discussion — Public Funding for Endangered Language Archives\nDr. Sato asks: 'Should national science foundations reserve strict funds for preserving small oral dialect lexicons alongside marine and ecological research?' Discuss the scientific and cultural link cleanly across 100–130 words.",
    ),
}

_LISTEN_REPEAT = {
    1: [
        "All chemistry safety forms are due on Tuesday before noon.",
        "Desert succulents store carbon inside cellular vacuoles overnight.",
        "Route B express buses bypass the south stadium during pipe welding.",
        "Please leave your library ID at the third-floor scanning counter.",
        "Biochar granules hold rainwater smoothly inside sandy farm plots.",
        "Review Chapter Four graphs ahead of Friday's afternoon advising hour.",
        "The marine chronometer lost only five seconds across eighty ocean days.",
    ],
    2: [
        "Personal tablet snapshots without flash are welcome inside Special Collections.",
        "Drop rinsed stainless takeout bowls beside dorm aqua chutes before nine.",
        "Lime mortar lets historic brick seams release dampness cleanly.",
        "Evening writing consultations run twenty-five minutes through December twelfth.",
        "Daylight vault prisms bent overhead street rays deep into basements.",
        "Remember to renew library carrel locker keys online before May twentieth.",
        "Roadside sandy bioswales capture oily runoff prior to creek flooding.",
    ],
    3: [
        "Every newly pressed wildflower parcel spends three full days in freezing storage.",
        "Passive dark-fiber lasers detect subtle ocean quakes without disturbing traffic.",
        "Language Café starts twenty minutes strictly in target languages at five.",
        "Always swap into clean indoor climbing shoes before testing wall routes.",
        "Bright blue streetlamps disorient nocturnal moths hovering over meadows.",
        "Move parked cars from Red Lot C into North Garage Friday before midnight.",
        "Leghemoglobin shields root microbes softly against oxygen damage indoors.",
    ],
    4: [
        "Never copy raw household panel files onto personal cloud drives.",
        "Attach itemized hotel folios rather than monthly card totals alone.",
        "Courtyard flu shots require no appointment for enrolled undergraduates.",
        "Wrap labeled masking tape securely on shared dorm fridge tubs weekly.",
        "Smaller product displays help hesitant buyers avoid choice fatigue.",
        "Solar farm chillers drop fresh mango heat smoothly before port transit.",
        "Balloon burst echoes reveal vivid acoustic signatures in dark rock caves.",
    ],
    5: [
        "Planetarium dome shows run indoors at eight-fifteen when clouds block telescopes.",
        "Configure field linguistics recorders cleanly to uncompressed WAV format.",
        "Toss diseased tomato stems straight into north fence disposal barrels.",
        "Submit thesis PDF files early with generous one-point-five-inch margins.",
        "Tag private oral interview recordings clearly before server backup.",
        "Deep slow-wave sleep locks afternoon study concepts smoothly into memory.",
        "Slowing cargo vessels two knots lowers ocean whale noise significantly.",
    ],
}

_INTERVIEWS = {
    1: [
        "Interviewer Q1: Welcome! To begin, what kind of off-campus field site or study space helps you concentrate best on tough assignments, and why?",
        "Interviewer Q2: Many science classes require collaborative lab partners while some students prefer solo work. Which style suits you better?",
        "Interviewer Q3: If a field sensor malfunctions the evening before a major deadline, how would you troubleshoot or adjust your team schedule?",
        "Interviewer Q4: Looking broader, should university campuses prioritize funding energy-saving smart buildings or green courtyard landscapes?",
    ],
    2: [
        "Interviewer Q1: Tell me about an older historical landmark or public library building in your town that left an impression on you.",
        "Interviewer Q2: Do you enjoy studying with digital searchable archives on tablets, or do you retain information better from printed paper pages?",
        "Interviewer Q3: Imagine you joined a neighborhood committee deciding how to turn an empty paved alley into green civic space. What would you suggest?",
        "Interviewer Q4: As cities modernize quickly, how closely should councils balance tearing down outdated concrete infrastructure against conserving architectural craft?",
    ],
    3: [
        "Interviewer Q1: Let's talk about practical hobbies or student clubs: what hands-on skill outside coursework has taught you patience recently?",
        "Interviewer Q2: Have you ever participated in a bilingual or international language table? What surprised you most about practicing casually?",
        "Interviewer Q3: Suppose you organize an outdoor evening stargazing event that threatens to rain out. How would you pivot smoothly to keep guests engaged?",
        "Interviewer Q4: Do you think public parks near suburbs should limit bright white LED streetlights overnight to aid local wildlife and dark skies?",
    ],
    4: [
        "Interviewer Q1: When purchasing electronics or groceries in a store with twenty nearly identical options, how do you pick quickly without second-guessing?",
        "Interviewer Q2: Tell me about a financial or scheduling habit you adjusted during college to save time or avoid waste.",
        "Interviewer Q3: If you managed a crowded college dorm kitchen where food kept spoiling anonymously, what humane fairness rule would you enforce?",
        "Interviewer Q4: Should public programs use automatic opt-out defaults—for example in waste composting or retirement savings—or keep everything strictly opt-in?",
    ],
    5: [
        "Interviewer Q1: Describe an evening routine that genuinely helps you unwind and sleep solidly after intense study days.",
        "Interviewer Q2: Have you ever memorized complex vocabulary faster by spacing short sessions across days instead of marathon late-night cramming?",
        "Interviewer Q3: Suppose you interview an elderly grandparent about neighborhood changes and notice they feel nervous beside a microphone. How would you build comfort?",
        "Interviewer Q4: Why do you think national museums still allocate funding to digitize tiny regional dialects alongside global engineering grants?",
    ],
}


def build_ibt_writing_and_speaking(pack_num: int) -> list[dict]:
    """Builds 12 Writing (Q098..Q109) + 11 Speaking (Q110..Q120) = 23 items for pack 1..5."""
    items = []
    q_idx = 98

    # 10 Build a Sentence
    sb_list = _BUILD_SENTENCE[pack_num]
    assert len(sb_list) == 10
    for idx, (ctx, ordered, exp) in enumerate(sb_list, start=1):
        qid = f"IBT26-0{pack_num}-Q{q_idx:03d}"
        items.append(_sb(qid, idx, ctx, ordered, exp, pack_num * 31 + idx))
        q_idx += 1

    # 2 Writing Responses (Email + Academic Discussion)
    email_prompt, disc_prompt = _WRITING_PROMPTS[pack_num]
    items.append(
        make_q(
            qid=f"IBT26-0{pack_num}-Q{q_idx:03d}",
            section="WRITING",
            task_type="WRITING_RESPONSE",
            prompt=email_prompt,
            options=[],
            answer="",
            explanation="Cek ulang struktur email: salam sopan -> alasan langsung di kalimat pertama -> detail spesifik & ajakan konfirmasi -> penutup rapi.",
            rationale_source="IBT-MOD-09",
            difficulty="B2",
            extra={
                "minWords": 70,
                "recommendedWords": 95,
                "rubricCriteria": ["Clear communicative purpose", "Specific schedule/reason details", "Polite academic email register", "Grammatical accuracy"],
            },
        ),
    )
    q_idx += 1
    items.append(
        make_q(
            qid=f"IBT26-0{pack_num}-Q{q_idx:03d}",
            section="WRITING",
            task_type="WRITING_RESPONSE",
            prompt=disc_prompt,
            options=[],
            answer="",
            explanation="Nyatakan posisi jelas, rujuk argumen Student A/B dengan kata sendiri, serta tambahkan alasan mekanisme dan satu contoh konkret (100–130 kata).",
            rationale_source="IBT-MOD-05",
            difficulty="B2",
            extra={
                "minWords": 100,
                "recommendedWords": 125,
                "rubricCriteria": ["Direct response engaging peers", "Cohesive idea development", "Syntactic variety", "Accurate lexical control"],
            },
        ),
    )
    q_idx += 1

    # 7 Listen and Repeat
    lr_list = _LISTEN_REPEAT[pack_num]
    assert len(lr_list) == 7
    for idx, sentence in enumerate(lr_list, start=1):
        qid = f"IBT26-0{pack_num}-Q{q_idx:03d}"
        aid = f"IBT0{pack_num}-SR{idx:02d}"
        items.append(
            make_q(
                qid=qid,
                section="SPEAKING",
                task_type="LISTEN_REPEAT",
                prompt=f"Listen and Repeat ({qid}): Listen cleanly to the spoken sentence ({idx}/7) and repeat it aloud with natural rhythm and clear articulation.",
                options=[],
                answer="",
                explanation=f"Latih pengelompokan jeda alami (chunking) dan akurasi pelafalan kalimat referensi: '{sentence}'",
                rationale_source="IBT-MOD-10",
                difficulty="B2",
                audio_id=aid,
                audio_turns=[("W" if idx % 2 == 1 else "M", sentence)],
                extra={
                    "prepSeconds": 0,
                    "speakSeconds": 12,
                    "referenceText": sentence,
                    "rubricCriteria": ["Word accuracy", "Natural chunk phrasing", "Vowel & final-consonant clarity"],
                },
            ),
        )
        q_idx += 1

    # 4 Take an Interview
    iv_list = _INTERVIEWS[pack_num]
    assert len(iv_list) == 4
    for idx, qprompt in enumerate(iv_list, start=1):
        qid = f"IBT26-0{pack_num}-Q{q_idx:03d}"
        aid = f"IBT0{pack_num}-SI{idx:02d}"
        items.append(
            make_q(
                qid=qid,
                section="SPEAKING",
                task_type="SPEAKING_RESPONSE",
                prompt=f"Take an Interview {idx}/4: {qprompt}",
                options=[],
                answer="",
                explanation="Jawab langsung posisi/pilihan Anda dalam 1 kalimat pembuka, ikuti 1–2 alasan mekanisme nyata, dan tutup dengan contoh singkat terfokus (45 detik).",
                rationale_source="IBT-MOD-06",
                difficulty="B2",
                audio_id=aid,
                audio_turns=[("M" if idx % 2 == 1 else "W", qprompt.split(": ", 1)[1])],
                extra={
                    "prepSeconds": 10,
                    "speakSeconds": 45,
                    "rubricCriteria": ["Prompt relevance", "Spontaneous fluency (~130-160 WPM)", "Supporting detail & clarity", "Sentence variety"],
                },
            ),
        )
        q_idx += 1

    assert len(items) == 23
    return items
