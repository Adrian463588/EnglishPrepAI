"""IELTS Academic Writing (2 tasks/pack) & Speaking (3 parts/pack) across 5 packs = 25 items."""

from pack_schema_helpers import make_q

_WRITING_SPEAKING = {
    1: {
        "w1": "Writing Task 1 (Academic Report — At least 150 words, 20 min): The table below compares municipal household waste outcomes (Landfilled, Recycled, Composted, Incinerated with Heat Recovery) across three medium-sized cities in 2010 and 2024. City A cut landfilling from 68% down to 22% while doubling recycling (24%->51%) and lifting composting (8%->27%). City B cut landfilling slowly (75%->55%, recycling 18%->31%, incineration 7%->14%). City C reached near-zero landfill (52%->9%, recycling 44%, composting 19%, incineration 28%). Summarize the information by selecting and reporting the main features, and make comparisons where relevant.",
        "w2": "Writing Task 2 (Academic Essay — At least 250 words, 40 min): Many municipal libraries now replace large quiet printed reference shelves with open communal maker workshops, digital editing desks, and toddler storytelling corners. To what extent do you think the benefits of this shift outweigh the drawbacks for lifelong learners and community reading culture?",
        "s1": ("Examiner Part 1 (Neighborhood & Daily Habits — 4–5 min):", "Good afternoon. Let's talk about your neighborhood. How long have you lived there, what kind of local spot helps you focus or unwind, and has public transport near your street changed recently?"),
        "s2": "Speaking Part 2 (Cue Card — 1 min prep, 2 min talk): Describe a skill you taught yourself using offline books or printed guides without enrolling in a paid course. You should say: what the skill was, why you decided to learn it independently, what obstacles slowed you down initially, and explain how practicing it changed your daily confidence.",
        "s3": ("Examiner Part 3 (Independent Learning & Technology — 4–5 min):", "You mentioned learning from structured written manuals. Do you think modern short video tutorials make deep conceptual mastery easier or shallower compared with working slowly through printed textbooks, and how might universities measure real competence beyond memorized facts?"),
    },
    2: {
        "w1": "Writing Task 1 (Process Diagram — At least 150 words, 20 min): The diagram illustrates closed-loop urban greywater purification inside a multi-story apartment complex. Sink and shower water passes first through a coarse mesh screen (removing hair/lint), enters an aerated bio-filter tank (microbes break down soap organics), moves through a subsurface gravel reed-bed planter outside, gets sanitized under UV light indoors, and finally feeds non-potable toilet cisterns and rooftop drip irrigation. Summarize the stages clearly in logical sequence.",
        "w2": "Writing Task 2 (Academic Essay — At least 250 words, 40 min): In numerous university towns, fast automated delivery robots and high-speed bike lanes now compete for narrow historic pedestrian sidewalks. Should municipal governments restrict sidewalk commercial automation heavily to safeguard unhurried pedestrian sociability, or integrate dedicated micro-lanes proactively? Discuss both views and give your opinion.",
        "s1": ("Examiner Part 1 (Punctuality & Calendars — 4–5 min):", "Hello, nice to meet you. Let's discuss time habits. Do you track tasks on paper notebooks or phone apps, how important is arriving ten minutes early in your culture, and when did waiting patiently actually help you?"),
        "s2": "Speaking Part 2 (Cue Card — 1 min prep, 2 min talk): Describe an old building or public bridge in your city whose architecture or history caught your attention. You should say: where it stands, what materials or proportions make it memorable, who uses it day-to-day today, and explain why preserving structures like it matters to residents.",
        "s3": ("Examiner Part 3 (Heritage vs Urban Density — 4–5 min):", "Old stone architecture often stands on valuable central land. How can city planners balance historic character with urgent affordable housing density, and should taxpayers fund structural retrofits of privately owned heritage facades?"),
    },
    3: {
        "w1": "Writing Task 1 (Line Graph — At least 150 words, 20 min): The chart tracks seasonal hospital admissions for heat stress versus winter respiratory infections across two coastal regions between 2005 and 2025. Region North recorded mild heat admissions rising from 180 to 460 per 100k during July–August while winter cases fell steadily (1,240->810). Region South experienced sharp late-summer spikes doubling from 620 to 1,390 per 100k alongside flat modest winter peaks (~490). Compare key trends and contrasts clearly.",
        "w2": "Writing Task 2 (Academic Essay — At least 250 words, 40 min): Public universities in several nations publish all taxpayer-funded empirical datasets immediately online, whereas private firms guard laboratory measurements behind intellectual-property walls. Does mandatory immediate open data sharing advance collective science faster, or undermine commercial incentives needed to develop practical inventions? Give reasons and concrete examples.",
        "s1": ("Examiner Part 1 (Cooking & Shared Meals — 4–5 min):", "Let's chat about cooking and food. What simple home dish do you enjoy preparing most often, did you learn recipes by watching relatives or reading instructions, and why do shared evening meals matter inside busy households?"),
        "s2": "Speaking Part 2 (Cue Card — 1 min prep, 2 min talk): Describe a science experiment, museum exhibit, or outdoor demonstration that explained a surprising natural mechanism clearly. You should say: where you encountered it, what visible change occurred, what prior misconception it corrected, and explain why hands-on illustration stays memorable.",
        "s3": ("Examiner Part 3 (Public Science Literacy — 4–5 min):", "Interactive exhibitions clarify complex concepts quickly, yet public distrust of empirical findings still flares on social platforms. What stops adults from revising wrong assumptions when confronted with clear data, and how should schools teach experimental skepticism gently?"),
    },
    4: {
        "w1": "Writing Task 1 (Map Comparison — At least 150 words, 20 min): Two campus maps show Riverside University Docklands in 1995 versus today. In 1995, bare gravel parking lots flanked the west riverbank beside an inactive timber coal jetty, one narrow stone footpath, and open reed marshes north of the boathouse. Today, the gravel lots became a terraced flood-sponge meadow park, the coal jetty rebuilt into a solar passenger ferry dock linked by a curved timber footbridge to a Marine Ecology Lab replacing one marsh edge while restoring wetlands eastward. Report major spatial transformations clearly.",
        "w2": "Writing Task 2 (Academic Essay — At least 250 words, 40 min): Some municipal transit agencies eliminate off-peak bus fares completely for residents while raising central street parking charges steeply. Do the social and environmental benefits of zero-fare public buses justify higher charges on private inner-city motorists? Evaluate advantages, unintended side effects, and state your own position.",
        "s1": ("Examiner Part 1 (Weather & Clothing — 4–5 min):", "Welcome. Let's talk about local weather and routines. Which season feels easiest for outdoor study in your town, how does heavy sudden rain affect weekend travel plans there, and do people dress mainly for comfort or formal tradition?"),
        "s2": "Speaking Part 2 (Cue Card — 1 min prep, 2 min talk): Describe a difficult group decision or committee choice you helped resolve peacefully without formal authority. You should say: what the disagreement involved, why each side hesitated initially, what practical compromise you proposed, and explain what you learned about listening carefully.",
        "s3": ("Examiner Part 3 (Consensus & Civic Participation — 4–5 min):", "Smaller circles often settle disagreements face-to-face, yet citywide referendums can harden opposing camps. Why do technical compromises work smoothly in workshops yet stall on televised public panels, and should citizens' assemblies help set local environmental budgets?"),
    },
    5: {
        "w1": "Writing Task 1 (Bar Chart + Pie Pair — At least 150 words, 20 min): The bar chart compares weekly adult time spent reading printed books, long-form digital articles, and short social clips across four age brackets (18–24, 25–39, 40–59, 60+), showing short-clip dominance in 18–24 (14.5 hrs/wk vs 1.8 hrs print) shifting markedly toward printed/long-form reading among 60+ (8.2 hrs print, 2.1 hrs short clips). The companion pie summarizes preferred audio formats during commutes (Podcasts 48%, Audiobooks 31%, Radio News 21%). Summarize core patterns and age contrasts.",
        "w2": "Writing Task 2 (Academic Essay — At least 250 words, 40 min): Artificial intelligence speech translators increasingly let travelers hold fluid basic conversations abroad without studying foreign grammar rules beforehand. Will effortless real-time translation enrich cross-cultural empathy overall, or discourage the patient cognitive effort required to grasp foreign idioms and social nuance deeply? Present a reasoned argument.",
        "s1": ("Examiner Part 1 (Languages & Memory — 4–5 min):", "Good to see you today. Let's reflect on languages and memory. Which English phrase or idiom puzzled you at first before making total sense, do you remember spoken words better by hearing stories or writing notes, and what quiet sound relaxes your mind after long reading?"),
        "s2": "Speaking Part 2 (Cue Card — 1 min prep, 2 min talk): Describe a long-term goal or academic milestone that took months of steady invisible effort before paying off noticeably. You should say: what milestone you aimed for, how you tracked tiny daily increments, when motivation dipped mid-way, and explain what kept your habit steady until completion.",
        "s3": ("Examiner Part 3 (Deep Focus in an Automated Era — 4–5 min):", "Instant algorithmic helpers finish summaries and translations within seconds today. How might relying on effortless digital shortcuts alter long-term memory formation among adolescents, and what deliberate offline intellectual challenges still justify slow unassisted human struggle?"),
    },
}


def build_ielts_writing_and_speaking(pack_num: int) -> list[dict]:
    ws = _WRITING_SPEAKING[pack_num]
    s1_label, s1_line = ws["s1"]
    s3_label, s3_line = ws["s3"]
    return [
        make_q(
            qid=f"IELTS-A-0{pack_num}-Q081",
            section="WRITING",
            task_type="WRITING_RESPONSE",
            prompt=ws["w1"],
            options=[],
            answer="",
            explanation="Band 7+ Task 1 wajib menyertakan Overview tren utama tanpa opini pribadi, membandingkan angka/tahapan ekstrem secara akurat.",
            rationale_source="IELTS-MOD-04",
            difficulty="B2",
            extra={
                "minWords": 150,
                "recommendedWords": 180,
                "rubricCriteria": ["Clear Overview statement", "Accurate data/stage selection", "Trend comparisons & passive/sequence connectors", "Lexical precision"],
            },
        ),
        make_q(
            qid=f"IELTS-A-0{pack_num}-Q082",
            section="WRITING",
            task_type="WRITING_RESPONSE",
            prompt=ws["w2"],
            options=[],
            answer="",
            explanation="Band 7+ Task 2 menuntut tesis tegas konsisten di pembuka hingga penutup, pengembangan paragraf tubuh bertumpu mekanisme & contoh, serta kendali klausa kompleks.",
            rationale_source="IELTS-MOD-05",
            difficulty="C1",
            extra={
                "minWords": 250,
                "recommendedWords": 290,
                "rubricCriteria": ["Full prompt coverage & clear position", "Coherent paragraphing", "Precise collocations", "Complex grammar range & accuracy"],
            },
        ),
        make_q(
            qid=f"IELTS-A-0{pack_num}-Q083",
            section="SPEAKING",
            task_type="SPEAKING_RESPONSE",
            prompt=f"{s1_label} {s1_line}",
            options=[],
            answer="",
            explanation="Part 1 idealnya berdurasi ~25–35 detik per pertanyaan gabungan dengan ekstensi 2–3 kalimat alami dan intonasi santai tanpa menghafal templat.",
            rationale_source="IELTS-MOD-06",
            difficulty="B1",
            audio_id=f"IELTS0{pack_num}-SP1",
            audio_turns=[("W", s1_line)],
            extra={
                "prepSeconds": 5,
                "speakSeconds": 45,
                "rubricCriteria": ["Natural conversational extension", "Everyday topic collocations", "Clear stress & rhythm", "Self-correction control"],
            },
        ),
        make_q(
            qid=f"IELTS-A-0{pack_num}-Q084",
            section="SPEAKING",
            task_type="SPEAKING_RESPONSE",
            prompt=ws["s2"],
            options=[],
            answer="",
            explanation="Gunakan 60 detik persiapan untuk mencatat 4–5 kata kunci linimasa (Konteks -> Tantangan -> Aksi -> Dampak), lalu bicaralah lancar hingga 2 menit penuh.",
            rationale_source="IELTS-MOD-06",
            difficulty="B2",
            audio_id=f"IELTS0{pack_num}-SP2",
            audio_turns=[("W", "Here is your Part 2 topic card. You now have one minute to prepare notes before speaking cleanly for up to two minutes.")],
            extra={
                "prepSeconds": 60,
                "speakSeconds": 120,
                "rubricCriteria": ["Full 2-minute sustained narrative", "Discourse markers across stages", "Vivid specific vocabulary", "Tenses accuracy across past & present reflection"],
            },
        ),
        make_q(
            qid=f"IELTS-A-0{pack_num}-Q085",
            section="SPEAKING",
            task_type="SPEAKING_RESPONSE",
            prompt=f"{s3_label} {s3_line}",
            options=[],
            answer="",
            explanation="Part 3 menuntut abstraksi gagasan tingkat masyarakat: pakai hedging ('on balance', 'tends to'), kontras dua sisi, dan alasan sebab-akibat analitis.",
            rationale_source="IELTS-MOD-10",
            difficulty="C1",
            audio_id=f"IELTS0{pack_num}-SP3",
            audio_turns=[("W", s3_line)],
            extra={
                "prepSeconds": 10,
                "speakSeconds": 60,
                "rubricCriteria": ["Abstract analytical reasoning", "Hedging & nuanced stance", "Complex clause variety", "Natural pronunciation under cognitive load"],
            },
        ),
    ]
