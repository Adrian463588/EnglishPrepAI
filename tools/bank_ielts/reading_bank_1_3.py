"""IELTS Academic Reading items Packs 1-3 (3 multi-paragraph passages x 13+13+14 = 40 Qs/pack = 120 items)."""

from pack_schema_helpers import make_q, rotated_mcq


def _tfng(statement, ans, exp, diff="B2"):
    return {"type": "CHOICE_SINGLE", "prompt": f"Do the following statements agree with the information in the text? (TRUE / FALSE / NOT GIVEN): '{statement}'", "opts": ["TRUE", "FALSE", "NOT GIVEN"], "ans_literal": ans, "exp": exp, "mod": "IELTS-MOD-02", "diff": diff}


def _ynng(claim, ans, exp, diff="B2"):
    return {"type": "CHOICE_SINGLE", "prompt": f"Do the following statements reflect the claims of the writer? (YES / NO / NOT GIVEN): '{claim}'", "opts": ["YES", "NO", "NOT GIVEN"], "ans_literal": ans, "exp": exp, "mod": "IELTS-MOD-08", "diff": diff}


def _rmc(prompt, opts, cidx, exp, mod="IELTS-MOD-08", diff="B2"):
    return {"type": "CHOICE_SINGLE", "prompt": prompt, "opts": opts, "cidx": cidx, "exp": exp, "mod": mod, "diff": diff}


def _rfill(prompt, ans, acc, exp, max_w=2, mod="IELTS-MOD-03", diff="B2"):
    return {"type": "FILL_BLANK", "prompt": prompt, "ans": ans, "acc": acc, "exp": exp, "maxWords": max_w, "mod": mod, "diff": diff}


def _rmulti(prompt, opts, ans, exp, mod="IELTS-MOD-08", diff="B2"):
    return {"type": "CHOICE_MULTI", "prompt": prompt, "opts": opts, "ans": ans, "exp": exp, "mod": mod, "diff": diff}


def _rmatch(prompt, pairs, ans, exp, mod="IELTS-MOD-03", diff="B2"):
    return {"type": "MATCHING", "prompt": prompt, "pairs": pairs, "ans": ans, "exp": exp, "mod": mod, "diff": diff}


def expand_ielts_reading(pack_num: int, passages_spec: list) -> tuple[list[dict], list[dict]]:
    passages = []
    questions = []
    q_idx = 41
    expected_counts = [13, 13, 14]
    assert len(passages_spec) == 3
    for p_ord, ((pid_sfx, title, body, qspecs), exp_cnt) in enumerate(zip(passages_spec, expected_counts), start=1):
        pid = f"IELTS0{pack_num}-{pid_sfx}"
        assert len(qspecs) == exp_cnt, f"{pid} expected {exp_cnt} Qs, got {len(qspecs)}"
        passages.append({"id": pid, "title": title, "body": body, "readingOrder": p_ord})
        for sub_i, spec in enumerate(qspecs, start=1):
            qid = f"IELTS-A-0{pack_num}-Q{q_idx:03d}"
            ttype = spec["type"]
            full_prompt = f"Reading Passage {p_ord} ('{title}' — Q{q_idx}): {spec['prompt']}"
            if ttype == "FILL_BLANK":
                questions.append(
                    make_q(
                        qid=qid,
                        section="READING",
                        task_type="FILL_BLANK",
                        prompt=full_prompt,
                        options=[],
                        answer=spec["ans"],
                        explanation=spec["exp"],
                        rationale_source=spec["mod"],
                        difficulty=spec["diff"],
                        passage_id=pid,
                        accepted_answers=spec["acc"],
                        extra={"maxWords": spec["maxWords"]},
                    ),
                )
            elif ttype == "CHOICE_MULTI":
                questions.append(
                    make_q(
                        qid=qid,
                        section="READING",
                        task_type="CHOICE_MULTI",
                        prompt=full_prompt,
                        options=spec["opts"],
                        answer=spec["ans"],
                        explanation=spec["exp"],
                        rationale_source=spec["mod"],
                        difficulty=spec["diff"],
                        passage_id=pid,
                    ),
                )
            elif ttype == "MATCHING":
                questions.append(
                    make_q(
                        qid=qid,
                        section="READING",
                        task_type="MATCHING",
                        prompt=full_prompt,
                        options=[],
                        answer=spec["ans"],
                        explanation=spec["exp"],
                        rationale_source=spec["mod"],
                        difficulty=spec["diff"],
                        passage_id=pid,
                        extra={"pairs": spec["pairs"]},
                    ),
                )
            elif "ans_literal" in spec:
                # Keep TRUE/FALSE/NOT GIVEN or YES/NO/NOT GIVEN standard exam order!
                questions.append(
                    make_q(
                        qid=qid,
                        section="READING",
                        task_type="CHOICE_SINGLE",
                        prompt=full_prompt,
                        options=spec["opts"],
                        answer=spec["ans_literal"],
                        explanation=spec["exp"],
                        rationale_source=spec["mod"],
                        difficulty=spec["diff"],
                        passage_id=pid,
                    ),
                )
            else:
                questions.append(
                    rotated_mcq(
                        qid=qid,
                        section="READING",
                        prompt=full_prompt,
                        options=spec["opts"],
                        correct_idx=spec["cidx"],
                        explanation=spec["exp"],
                        rationale_source=spec["mod"],
                        difficulty=spec["diff"],
                        seed=pack_num * 41 + q_idx,
                        passage_id=pid,
                    ),
                )
            q_idx += 1
    assert len(questions) == 40
    return passages, questions


_READING_1_3 = {
    1: [
        # P1 (13)
        ("P1", "Passive Solar Retrofits in Cold-Climate Apartments",
         "Paragraph A: Many European apartment blocks constructed between 1950 and 1975 lose over half their winter warmth through thin uninsulated concrete facades and single-pane steel windows. Rather than demolishing sound structural concrete frames—which squanders huge embodied construction carbon—municipal engineers wrap occupied exteriors in prefabricated timber-and-mineral-wool cladding panels while residents stay indoors.\n\n"
         "Paragraph B: Monitoring across three pilot estates in Graz documented average space-heating drops reaching seventy-eight percent following exterior cladding paired with mechanical heat-recovery ventilation. Crucially, indoor living rooms remained steadily comfortable throughout harsh January cold snaps without running emergency electric coil heaters.\n\n"
         "Paragraph C: Deep retrofit crews assemble laser-scanned wall panels in sheltered regional factories down to two-millimeter precision, bolting window cassettes seamlessly outside old concrete edges with minimal sidewalk scaffolding noise. Total crane installation per four-story apartment block wraps inside barely sixteen days.\n\n"
         "Paragraph D: Despite proven thermal efficiency, municipal housing trusts regularly run into financing traps called the 'split-incentive' hurdle: private or public landlords shoulder steep upfront loan costs immediately whereas tenants enjoy markedly lower monthly gas bills afterward—forcing cities like Vienna to design clean long-term utility-shared repayment tariffs.\n\n"
         "Paragraph E: Post-occupancy health clinics additionally recorded sharper evening sleep stability and steep drops in bedroom wall condensation mold once triple-glazed warm frames pushed interior corner surfaces cleanly above dew-point moisture thresholds.",
         [
             _tfng("Deep prefabricated timber-panel wrapping requires emptying apartment buildings of all residents during installation.", "FALSE", "Paragraf A menegaskan panel dipasang dari bagian luar saat penghuni tetap tinggal di dalam ('while tenants remain inside').", "B1"),
             _tfng("Demolishing structurally sound 1960s apartment shells wastes substantial embedded embodied carbon.", "TRUE", "Paragraf A menyebut pembongkaran kerangka beton memboroskan karbon struktural tertanam.", "B1"),
             _tfng("Vienna retrofit teams achieved slightly lower winter gas reductions than Graz pilot blocks.", "NOT GIVEN", "Teks hanya menyebut penghematan 78% di Graz; Vienna disebut dalam konteks skema tarif pembiayaan, bukan angka penurunan gas.", "B2"),
             _tfng("Factory timber panels are measured with millimeter optical laser scans prior to craning on site.", "TRUE", "Paragraf C menyatakan fabrikasi mengikuti pemindaian laser presisi dua milimeter.", "B1"),
             _tfng("Adding mechanical heat-recovery ventilation inside Graz blocks eliminated mold risk alongside heating drops.", "TRUE", "Paragraf B & E mencatat ventilasi pemulihan panas + bingkai hangat menghilangkan kondensasi jamur serta menghemat 78% panas.", "B2"),
             _tfng("Every prefabricated retrofit crane assembly finishes under sixteen rainy autumn hours.", "FALSE", "Paragraf C menyebut durasi sekitar 16 hari ('sixteen days'), bukan jam.", "B1"),
             _tfng("Private utility banks waive interest rates completely on Graz cladding mortgages.", "NOT GIVEN", "Teks tidak menyebut pembebasan bunga pinjaman bank swasta di Graz.", "B2"),
             _rfill("ONE WORD AND/OR A NUMBER: 'Graz monitoring showed annual space-heating demand fell by ___ percent.'", "78", ["78", "seventy-eight"], "Paragraf B mencatat penurunan kebutuhan pemanasan ruang sebesar 78%.", 2),
             _rfill("ONE WORD ONLY: 'Natural insulating filler paired inside prefabricated timber frames: mineral ___'", "wool", ["wool"], "Paragraf A menyebut 'timber-and-mineral-wool cladding panels'.", 1),
             _rfill("NO MORE THAN TWO WORDS: 'Main economic barrier separating landlord loan outlay from tenant bill savings: split-___ barrier'", "incentive", ["incentive"], "Paragraf D menamai hambatan tersebut sebagai 'split-incentive'.", 1),
             _rfill("NUMBER ONLY (days): 'Typical exterior facade assembly timeline per four-story housing block: ___ days'", "16", ["16", "sixteen"], "Pemasangan derek per blok memakan waktu 16 hari.", 1),
             _rmc("How did interior bedroom air quality improve after triple-glazed frame installation?", ["Warmer interior corner surfaces stopped damp vapor from condensing into mold.", "Bedroom fans blew salty coastal wind straight indoors daily.", "Residents turned radiator dials down permanently below ten degrees.", "Brick seams absorbed dusty hallway smells automatically."], 0, "Paragraf E menjelaskan permukaan sudut hangat berada di atas titik embun sehingga mencegah jamur."),
             _rmatch("Match paragraphs A, C, and D cleanly with their central themes:", [
                 {"left": "Paragraph A", "rightOptions": ["Saving structural carbon via exterior wraps", "Precision off-site factory panel speed", "Overcoming landlord-tenant financing mismatch"]},
                 {"left": "Paragraph C", "rightOptions": ["Saving structural carbon via exterior wraps", "Precision off-site factory panel speed", "Overcoming landlord-tenant financing mismatch"]},
                 {"left": "Paragraph D", "rightOptions": ["Saving structural carbon via exterior wraps", "Precision off-site factory panel speed", "Overcoming landlord-tenant financing mismatch"]},
             ], "Paragraph A::Saving structural carbon via exterior wraps|Paragraph C::Precision off-site factory panel speed|Paragraph D::Overcoming landlord-tenant financing mismatch", "Ringkasan kesesuaian ide utama Paragraf A, C, dan D."),
         ]),
        # P2 (13)
        ("P2", "Night Cooling Strategies Across Dense Mediterranean Cities",
         "Paragraph A: Historic compact limestone quarters in Seville, Cordoba, and Fez stayed surprisingly liveable across sizzling forty-degree July afternoons long before mechanical compressors arrived, harnessing clever street proportions paired with thick thermal-inertia courtyards.\n\n"
         "Paragraph B: Modern microclimate sensors mounted along narrow two-meter winding alleys record daytime air resting six degrees cooler than exposed wide suburban boulevards merely three blocks away, because tall chalk-whitewashed upper balconies shield lower walls steadily from direct solar angles.\n\n"
         "Paragraph C: Inside traditional dwellings, central shaded wells open vertically skyward; as cool dense night air pools steadily onto damp patio stone fountains until dawn, warm indoor daytime convection vents out overhead before shutters latch firmly shut ahead of noon glare.\n\n"
         "Paragraph D: When mid-twentieth-century planners demolished narrow winding market lanes in favor of sun-baked wide glass strip corridors, outdoor courtyard cooling loops broke swiftly—forcing households onto costly peak afternoon wall-mounted condensers whose outward exhaust fans further baked neighboring alleys.\n\n"
         "Paragraph E: Contemporary architectural simulators modeled merging lightweight retractable fabric street awnings ('toldos') with permeable limestone pavers, restoring nearly three degrees of ambient afternoon relief along open commercial squares.",
         [
             _rmatch("Choose the best heading for Paragraphs A–D:", [
                 {"left": "Paragraph A", "rightOptions": ["Pre-electric thermal comfort overview", "Measurable shade drops in narrow alleys", "Daily vertical courtyard airflow cycle", "Heat penalties of modern glass corridors"]},
                 {"left": "Paragraph B", "rightOptions": ["Pre-electric thermal comfort overview", "Measurable shade drops in narrow alleys", "Daily vertical courtyard airflow cycle", "Heat penalties of modern glass corridors"]},
                 {"left": "Paragraph C", "rightOptions": ["Pre-electric thermal comfort overview", "Measurable shade drops in narrow alleys", "Daily vertical courtyard airflow cycle", "Heat penalties of modern glass corridors"]},
                 {"left": "Paragraph D", "rightOptions": ["Pre-electric thermal comfort overview", "Measurable shade drops in narrow alleys", "Daily vertical courtyard airflow cycle", "Heat penalties of modern glass corridors"]},
             ], "Paragraph A::Pre-electric thermal comfort overview|Paragraph B::Measurable shade drops in narrow alleys|Paragraph C::Daily vertical courtyard airflow cycle|Paragraph D::Heat penalties of modern glass corridors", "Setiap heading merangkum topik utama paragraf masing-masing secara utuh."),
             _tfng("Sensors placed in narrow whitewashed alleys measured daytime warmth six degrees hotter than wide boulevards.", "FALSE", "Paragraf B menyebut gang sempit justru 6 derajat LEBIH SEJUK ('six degrees cooler') dibanding bulevar terbuka.", "B1"),
             _tfng("Mechanical window air conditioners intensify surrounding street warmth due to outdoor fan exhaust.", "TRUE", "Paragraf D mencatat kipas buangan AC memperpanas gang di sekitarnya ('exhaust fans further baked neighboring alleys').", "B1"),
             _tfng("Retractable toldo fabric shades outlast solid limestone marble arches over fifty rainy winters.", "NOT GIVEN", "Paragraf E hanya membahas efektivitas pendinginan toldo sekitar 3 derajat, tanpa membandingkan umur pakainya dengan marmer.", "B2"),
             _rfill("ONE WORD ONLY: 'White outer coatings reflecting midday solar shafts off narrow walls: chalk-___ coatings'", "whitewashed", ["whitewashed", "lime"], "Paragraf B menyebut 'chalk-whitewashed upper balconies'.", 1),
             _rfill("NUMBER ONLY (m): 'Approximate width cited for cool winding shaded alleys downtown: ___ meters'", "2", ["2", "two"], "Gang sempit dicatat selebar sekitar 2 meter.", 1),
             _rfill("ONE WORD ONLY: 'Spanish textile overhead shade system modeled cleanly over modern town squares: ___'", "toldos", ["toldos"], "Paragraf E menyebut kanopi kain tarik 'toldos'.", 1),
             _rfill("NUMBER ONLY (°C): 'Simulated ambient cooling restoration along wide plazas using permeable stone + awnings: nearly ___ degrees'", "3", ["3", "three"], "Simulasi mengembalikan penurunan suhu hampir 3 derajat.", 1),
             _rmulti("Choose TWO traditional building behaviors operating across daily summer cycles in Paragraph C:",
                    ["Closing window shutters prior to scorching midday sun", "Running rooftop electric water spray fans overnight", "Allowing chilly night breezes to sink down open courtyard fountains", "Removing whitewashed limestone awnings monthly"],
                    "Closing window shutters prior to scorching midday sun|Allowing chilly night breezes to sink down open courtyard fountains",
                    "Udara malam sejuk mengendap ke pelataran dalam lalu penutup jendela dirapatkan sebelum tengah hari terik."),
             _rmc("What dual urban failure followed replacing narrow medieval lanes with wide glass boulevards?", ["Loss of street self-shading paired with heat-dumping mechanical AC compressors.", "Frequent winter river ice jams flooding dry lime fountains.", "Collapse of historic stone wells under morning commuters.", "Lack of whitewash chalk quarry workers downtown."], 0, "Gang kehilangan peneduh diri sekaligus dibanjiri buangan panas AC mekanis.", "IELTS-MOD-08", "B2"),
             _tfng("Traditional courtyards in Seville kept limestone cool chiefly by running continuous electric floor turbines overnight.", "FALSE", "Paragraf A dan C menegaskan desain tradisional bekerja jauh sebelum kompresor mekanis ada, lewat konveksi alami serta pelataran air malam hari.", "B1"),
             _rfill("ONE WORD ONLY: 'Architectural urban material helping pavers cut plaza daytime temperatures when paired with canopy toldos: permeable ___'", "limestone", ["limestone"], "Paragraf E menyebut kombinasi toldos dengan 'permeable limestone pavers'.", 1),
             _rmc("Why do overhanging upper white balconies keep two-meter ground alleys substantially cooler than sunlit suburbs?", ["Elevated projections intercept steep midday sunlight while bright surfaces reflect radiation skyward.", "Balcony timber fans push river mist into basement kitchens continuously.", "Tall glass balconies absorb street traffic fumes quietly.", "Narrow lanes forbid daytime neighborhood strolling completely."], 0, "Balkon atas meneduhi dinding bawah dari sinar langsung sekaligus memantulkan radiasi.", "IELTS-MOD-08", "B2"),
         ]),
        # P3 (14)
        ("P3", "Re-Examining the Myth of Passive Consumer Energy Feedback",
         "Paragraph A: Early optimistic utility pilots predicted installing glossy digital electricity meters inside kitchen hallways would cut household power usage twenty percent almost effortlessly once families viewed live kilowatt digits.\n\n"
         "Paragraph B: Yet longitudinal multi-year social studies across Leeds and Seattle uncovered a sobering 'novelty fade': households checked blinking kitchen displays closely throughout month one before drifting straight back toward prior laundry and oven routines as colored bars blended cleanly into daily wallpaper.\n\n"
         "Paragraph C: Behavioral researcher Dr. Helen Kwak showed raw hourly kilowatt graphs confuse busy parents—asking folks to juggle abstract physics units against invisible nighttime tariff curves works poorly compared with actionable clear appliance nudges ('Shifting dishwasher start past 9 PM saves £14 quarterly').\n\n"
         "Paragraph D: Similarly, social economist Tomas Silva demonstrated neighbor norm bars carry real friction risks: while above-average consumers trimmed minor habits downward, ultra-frugal pensioners spotting green 'well below street average' smiley icons quietly crept usage UP slightly—a 'boomerang effect' erasing aggregate neighborhood reductions!\n\n"
         "Paragraph E: Modern responsive energy tools bypass human willpower exhaustion altogether through automated smart-tariff scheduling—charging home storage batteries cleanly overnight only during wind peaks while letting residents keep full simple override dials.",
         [
             _ynng("Simply placing digital kilowatt screens in hallways consistently sustains twenty-percent long-term savings on its own.", "NO", "Paragraf B membuktikan adanya 'novelty fade' di mana kebiasaan pengguna kembali seperti semula sesudah bulan pertama.", "B2"),
             _ynng("Concrete appliance-level cost feedback engages tired families more effectively than raw kilowatt-hour bar charts.", "YES", "Paragraf C menegaskan riset Dr. Kwak bahwa pesan tindakan langsung (hemat £14) jauh lebih efektif daripada angka kilowatt abstrak.", "B1"),
             _ynng("Showing neighborhood utility benchmarks never accidentally increases low-usage pensioners' bills.", "NO", "Paragraf D mencatat 'boomerang effect' di mana rumah tangga hemat justru sedikit menaikkan konsumsi setelah melihat dirinya jauh di bawah rata-rata.", "B2"),
             _ynng("Seattle families tested glossy kitchen monitors longer than households living in Leeds.", "NOT GIVEN", "Paragraf B menyebut kedua kota (Leeds dan Seattle) tetapi tidak membandingkan durasi pengujian di antara keduanya.", "B2"),
             _ynng("Automating battery charging via wind-peak pricing minimizes mental fatigue compared with manual switch-watching.", "YES", "Paragraf E mendukung penjadwalan tarif otomatis agar pengguna tidak mengalami 'willpower exhaustion'.", "B2"),
             _rmatch("Match each researcher cleanly to their empirical energy finding:", [
                 {"left": "Dr. Helen Kwak", "rightOptions": ["Specific euro/pound task advice beats abstract kWh numbers", "Social average rankings triggered low-user boomerang rebounds"]},
                 {"left": "Tomas Silva", "rightOptions": ["Specific euro/pound task advice beats abstract kWh numbers", "Social average rankings triggered low-user boomerang rebounds"]},
             ], "Dr. Helen Kwak::Specific euro/pound task advice beats abstract kWh numbers|Tomas Silva::Social average rankings triggered low-user boomerang rebounds", "Pemetaan temuan Kwak (paragraf C) vs Silva (paragraf D)."),
             _rfill("NO MORE THAN TWO WORDS: 'Sociological decline when early excitement over new wall monitors wears flat: novelty ___'", "fade", ["fade"], "Paragraf B menyebut istilah tersebut 'novelty fade'.", 1),
             _rfill("ONE WORD ONLY: 'Unexpected reverse uptick seen among frugal households seeing low rankings: ___ effect'", "boomerang", ["boomerang"], "Paragraf D menamainya 'boomerang effect'.", 1),
             _rfill("NUMBER ONLY (£): 'Specific concrete quarterly savings cited cleanly for delayed dishwasher starts: £___'", "14", ["14", "fourteen"], "Contoh hemat konkret Dr. Kwak adalah £14 per kuartal.", 1),
             _rfill("NO MORE THAN TWO WORDS: 'Energy automation storage devices charged neatly during surplus wind spikes: ___ batteries'", "home storage", ["storage", "home storage"], "Paragraf E menyebut pengisian 'home storage batteries'.", 2),
             _rmc("Why did blinking color power screens fade into household obscurity after barely thirty days?", ["Constant manual unit monitoring lacks specific behavioral guidance and competes with busy routines.", "Electric screens overheated kitchen drywall paint swiftly.", "Municipal privacy boards unplugged neighborhood wireless networks.", "Green bars drained kitchen breaker panels noticeably."], 0, "Grafik pasif tidak mengarahkan aksi spesifik dan kalah oleh rutinitas keseharian."),
             _rmc("Which group drove the unexpected upward consumption nudge in Tomas Silva's social ranking trial?", ["Very low-consumption thrifty households realizing they sat far below neighborhood norms.", "Luxury home owners owning heated winter swimming garages.", "Industrial office cafes operating twenty-four espresso stations.", "Rental students lacking automated tariff batteries."], 0, "Rumah tangga yang sangat hemat melihat ikon senyum jauh di bawah rata-rata lalu agak melonggarkan pemakaian."),
             _rmc("How do responsive smart systems avoid repeating the 'novelty fade' mistake cleanly?", ["They automate overnight wind-tariff charging silently in background while preserving manual override ease.", "They sound loud piercing bells whenever television screens draw excess wattage.", "They fine families monthly unless reading five kilowatt tutorials.", "They erase household utility display apps altogether."], 0, "Sistem otomatis bekerja diam-diam mengikuti tarif angin murah sambil menyisakan kontrol manual."),
             _rmc("What best reflects the writer's overall evaluation of smart home energy engagement?", ["Actionable concrete nudges and user-respecting automation outperform passive bare numbers.", "Manual meter reading remains the single trustworthy method nationwide.", "Behavioral science offers zero insights into utility planning.", "Visual screens hurt neighborhood conservation goals uniformly."], 0, "Penulis menyimpulkan kombinasi arahan praktis dan otomasi cerdas jauh mengungguli tampilan data mentah pasif."),
         ]),
    ],
    2: [
        # P1 (13)
        ("P1", "Restoring Urban Oyster Reefs Inside Polluted Shipping Harbors",
         "Paragraph A: Long before industrial steel docks ringed New York Harbor, sprawling natural Eastern oyster beds stretched across hundreds of square kilometers of Hudson estuary shallows, anchoring rich mud habitats while clarifying turbid tidal plumes effortlessly.\n\n"
         "Paragraph B: A single mature oyster pumps nearly two hundred liters of estuary silt and algae daily past feathery gills, binding harmless mud pellets down safely onto reef floors so bright sunshine reaches shallow eelgrass beds.\n\n"
         "Paragraph C: Centuries of unchecked commercial harvest dredging alongside untreated city canal waste decimated native beds—not simply clearing delicious shellfish tables, yet erasing natural rough limestone breakwaters that buffered shorelines gently against Atlantic storms.\n\n"
         "Paragraph D: Recently, coastal non-profit educators paired local vocational high-school marine carpentry students together with seafood wholesalers to cure mountains of discarded restaurant half-shells cleanly on sunny harbor islands for twelve months—removing lingering kitchen smells and drying pathogens safely before dropping stuffed gabion cages onto muddy harbor pilings.\n\n"
         "Paragraph E: Remarkably, wild tiny harbor oyster larvae—still drifting sparingly from hidden survivors under crumbling docks—settled greedily atop cured recycled cages once clean calcium surfaces reappeared without dirty silt choking gills.",
         [
             _tfng("A solitary adult Eastern oyster can filter nearly two hundred liters of muddy water every day.", "TRUE", "Paragraf B menyebut 'nearly two hundred liters ... daily'.", "B1"),
             _tfng("Historical harbor reefs sheltered shallow coasts gently by breaking rough Atlantic wave energy.", "TRUE", "Paragraf C menegaskan terumbu kapur meredam ombak badai Atlantik.", "B1"),
             _tfng("Marine wholesalers pay high cash bounties per kilogram directly to vocational school carpenters.", "NOT GIVEN", "Paragraf D menyebut kerja sama sekolah dan distributor makanan laut menyumbang cangkang bekas, tanpa menyebut pembayaran imbalan uang ke siswa.", "B2"),
             _tfng("Recycled restaurant oyster shells drop straight off kitchen trucks wet into polluted bay mud on day one.", "FALSE", "Paragraf D menjelaskan cangkang dijemur/disterilkan 12 bulan di pulau pelabuhan lebih dulu.", "B1"),
             _tfng("Living harbor oyster larvae perished totally across the entire Hudson River prior to student restoration.", "FALSE", "Paragraf E menyebut larva liar masih mengalir tipis dari populasi penyintas di kolong dermaga tua.", "B2"),
             _tfng("Cleaned gabion cages attract free-swimming baby oysters strictly through injected artificial coral perfumes.", "FALSE", "Paragraf E menyatakan larva menempel alami begitu permukaan kalsium bersih tersedia.", "B2"),
             _tfng("Curing discarded culinary shells outdoors eliminates lingering odor alongside unwanted microbes.", "TRUE", "Paragraf D menyebut penjemuran 12 bulan menghilangkan bau dapur dan patogen.", "B1"),
             _rfill("NUMBER ONLY (months): 'Time discarded half-shells cure outdoors under sun before harbor placement: ___ months'", "12", ["12", "twelve"], "Cangkang dikeringkan alami selama dua belas bulan.", 1),
             _rfill("ONE WORD ONLY: 'Fine benthic sea plants gaining clear sunlight once oysters capture suspended algae: ___ beds'", "eelgrass", ["eelgrass"], "Paragraf B menyebut 'shallow eelgrass beds'.", 1),
             _rfill("ONE WORD ONLY: 'Structured stone-and-wire enclosures loaded tightly with cured shell mounds: ___ cages'", "gabion", ["gabion"], "Paragraf D menggunakan keranjang kawat batu 'gabion cages'.", 1),
             _rfill("ONE WORD ONLY: 'Water-pumping filtering shellfish organs handling suspended bay mud: feathery ___'", "gills", ["gills"], "Paragraf B menyebut penyaringan melalui insang berbulu ('feathery gills').", 1),
             _rmatch("Match paragraphs cleanly to their focal process stage:", [
                 {"left": "Paragraph B", "rightOptions": ["Natural clarity & filtration physiology", "Historical reef depletion causes", "Vocational shell-curing workflow"]},
                 {"left": "Paragraph C", "rightOptions": ["Natural clarity & filtration physiology", "Historical reef depletion causes", "Vocational shell-curing workflow"]},
                 {"left": "Paragraph D", "rightOptions": ["Natural clarity & filtration physiology", "Historical reef depletion causes", "Vocational shell-curing workflow"]},
             ], "Paragraph B::Natural clarity & filtration physiology|Paragraph C::Historical reef depletion causes|Paragraph D::Vocational shell-curing workflow", "Kesesuaian ide utama tiap paragraf."),
             _rmc("Why do project organizers cure shells outdoors for a year rather than importing quarried limestone gravel?", ["Recycling abundant kitchen calcium provides ideal larval chemistry while diverting restaurant shell waste cleanly.", "Limestone gravel dissolves completely inside fresh ocean currents overnight.", "Quarry trucks refuse driving onto sunny island harbors.", "Students prefer scraping rough limestone edges indoors daily."], 0, "Cangkang restoran melimpah, bebas limbah ke TPA, serta berkimia kalsium alami bagi larva tiram."),
         ]),
        # P2 (13)
        ("P2", "Quiet Rubberized Asphalt vs Concrete Road Noise",
         "Paragraph A: Commuter towns sprawling alongside suburban expressways commonly pour tall four-meter concrete noise barriers—deflecting mid-range horn chatter smoothly upward while spending fortunes blocking backyard valley greenery views completely.\n\n"
         "Paragraph B: Wait—where does persistent sixty-to-ninety kilometer highway rush actually generate noisy roar? Above barely forty kilometers per hour, modern passenger sedans emit far louder aerodynamic tire-pavement slapping than silent mufflers under engine hoods!\n\n"
         "Paragraph C: Highway material labs in Arizona and Netherlands test blending tiny ground-up scrap rubber tire crumb gently into porous 'open-graded friction asphalt' mats containing eighteen-percent interconnected air voids directly inside the top three pavement centimeters.\n\n"
         "Paragraph D: When rubberized tires press cleanly across porous open-graded grooves, trapped high-pressure air pockets squeeze smoothly downward inside interconnected mineral voids instead of whistling explosively outward—trimming roadside decibels cleanly by four to five decibels (feeling perceptually almost half as loud to human ears!) while draining dangerous highway rain puddles instantly.\n\n"
         "Paragraph E: Note one temperate engineering caveat: in harsh freezing northern zones, salted snowplow scraping gradually grinds fine porous lips away unless winter crews ease steel blade downward weight.",
         [
             _rmatch("Choose the best summary heading for Paragraphs A, C, and D:", [
                 {"left": "Paragraph A", "rightOptions": ["Costly drawbacks of tall visual barrier walls", "Recycled porous pavement composition", "Acoustic & rain-drainage mechanism"]},
                 {"left": "Paragraph C", "rightOptions": ["Costly drawbacks of tall visual barrier walls", "Recycled porous pavement composition", "Acoustic & rain-drainage mechanism"]},
                 {"left": "Paragraph D", "rightOptions": ["Costly drawbacks of tall visual barrier walls", "Recycled porous pavement composition", "Acoustic & rain-drainage mechanism"]},
             ], "Paragraph A::Costly drawbacks of tall visual barrier walls|Paragraph C::Recycled porous pavement composition|Paragraph D::Acoustic & rain-drainage mechanism", "Pemetaan topik masing-masing paragraf tentang aspal karet berpori."),
             _tfng("At highway speeds exceeding forty kilometers hourly, vehicle engine exhaust creates louder total road din than tire contact.", "FALSE", "Paragraf B menyebut di atas 40 km/jam suara benturan ban terhadap permukaan jalan lebih mendominasi daripada mesin.", "B2"),
             _tfng("Open-graded porous asphalt blends incorporate small ground rubber fragments from discarded scrap tires.", "TRUE", "Paragraf C memakai serbuk karet ban bekas daur ulang ('ground-up scrap rubber tire crumb').", "B1"),
             _tfng("Dropping roadside noise roughly four to five decibels makes traffic sound nearly twice as quiet to everyday listeners.", "TRUE", "Paragraf D mencatat penurunan 4–5 dB terasa hampir setengah kali lebih senyap bagi pendengaran manusia.", "B2"),
             _tfng("Arizona DOT banned all rubberized highway overlays immediately after heavy monsoon storms in 2020.", "NOT GIVEN", "Arizona disebut melakukan uji coba; pelarangan tahun 2020 tidak pernah disebut.", "B2"),
             _rfill("NUMBER ONLY (%): 'Target internal air-void percentage mixed inside porous upper pavement mats: ___%'", "18", ["18", "eighteen"], "Kadar rongga udara internal mencapai 18%.", 1),
             _rfill("NUMBER ONLY (cm): 'Approximate depth thickness of quiet open-graded asphalt wearing top layers: ___ centimeters'", "3", ["3", "three"], "Lapisan atas berpori setebal tiga sentimeter.", 1),
             _rfill("NUMBER ONLY (km/h): 'Approximate vehicle speed threshold above which tire noise dominates car engines: ___ km/h'", "40", ["40", "forty"], "Ambang kecepatan di atas 40 km/jam.", 1),
             _rfill("ONE WORD ONLY: 'Hazardous winter maintenance trucks requiring gentler blade adjustments over open-pore asphalt: ___'", "snowplows", ["snowplow", "snowplows"], "Paragraf E menyebut bajak salju bergaram mengikis bibir pori ('salted snowplow scraping').", 1),
             _rmulti("Choose TWO verified benefits produced concurrently by porous rubberized surface layers:",
                    ["Immediate evacuation of blinding roadway rainwater puddles", "Complete elimination of suburban street tree maintenance", "Measured 4–5 decibel cuts directly at moving wheel sources", "Lifetime immunity against heavy snowplow metal blades"],
                    "Immediate evacuation of blinding roadway rainwater puddles|Measured 4–5 decibel cuts directly at moving wheel sources",
                    "Keunggulan ganda: meredam 4–5 dB bising langsung di sumber roda sekaligus mengalirkan genangan air hujan."),
             _rmc("Why is eliminating tire noise directly on the road surface often superior to erecting concrete highway walls?", ["Tall walls only shelter immediate backyards unevenly while blocking scenic sunlight and greenery.", "Porous pavement absorbs overhead highway jetliner roar automatically.", "Rubber crumb asphalt cleans engine oil exhaust inside car mufflers.", "Concrete walls break easily under passenger car tires."], 0, "Mengatasi sumber bising di jalan lebih efektif tanpa menutup pemandangan dan menghabiskan biaya dinding tinggi."),
             _tfng("Highway noise barriers taller than four meters typically open wide unobstructed views across valleys.", "FALSE", "Paragraf A mencatat dinding beton empat meter justru memblokir panorama lembah warga ('blocking backyard valley greenery views completely').", "B1"),
             _rfill("NO MORE THAN TWO WORDS: 'Standard civil engineering label for open-void crumb pavement mats: open-graded ___ asphalt'", "friction", ["friction"], "Paragraf C menyebut istilah teknis 'open-graded friction asphalt'.", 1),
         ]),
        # P3 (14)
        ("P3", "Do Automated Handwriting Algorithms Flatten Classroom Cognition?",
         "Paragraph A: Several university boards eager to accelerate lecture note taking swiftly replaced spiral notebooks cleanly with silent glass tablet keyboards across first-year humanities halls—citing brisk typing speeds nearing sixty words per minute compared with barely twenty-two slow longhand pen words.\n\n"
         "Paragraph B: Controlled memory trials conducted by Mueller and Oppenheimer quietly punctured simple speed optimism: although frantic laptop typists recorded nearly verbatim lecture slide transcripts cleanly word-for-word, cursive pen note-takers scored measurably sharper on conceptual synthesis questions weeks later.\n\n"
         "Paragraph C: Crucially, the cognitive gap did NOT stem from glowing distraction web pop-ups alone—even offline air-gapped laptops locked inside blank word-processing windows still lagged handwriting in conceptual transfer tests! Why? Slow sluggish pen speed forces busy listeners actively to filter, condense, and paraphrase big claims mentally into personal sketchy summaries while listening.\n\n"
         "Paragraph D: Notice also a balancing educational caution voiced cleanly by technology theorist Dr. Naomi Chen: students facing fine-motor dysgraphia or chronic wrist fatigue gain essential equal fluency exclusively via keyboards—making rigid blanket classroom laptop bans discriminatory unless balanced smoothly beside thoughtful pedagogical prompts.\n\n"
         "Paragraph E: Wise seminar leaders therefore preserve keyboard access cleanly for accessibility while coaching laptop users gently to map conceptual flowcharts and close word-for-word transcription crutches.",
         [
             _ynng("Typing faster verbatim transcripts guarantees stronger long-term conceptual comprehension than slower handwriting.", "NO", "Paragraf B membuktikan pengetik cepat verbatim justru tertinggal pada pertanyaan sintesis konsep jangka panjang.", "B1"),
             _ynng("Switching off Wi-Fi distracting notifications completely closes the cognitive memory gap between typing and handwriting.", "NO", "Paragraf C menegaskan laptop offline tanpa distraksi tetap tertinggal karena pengetikan cepat mendorong transkripsi mekanis tanpa parafrase aktif.", "B2"),
             _ynng("Enforcing rigid sweeping bans against student keyboards harms equitable access for writers with motor challenges.", "YES", "Paragraf D mendukung pandangan Dr. Chen bahwa larangan total laptop diskriminatif bagi mahasiswa disgrafia atau cedera motorik halus.", "B1"),
             _ynng("Dr. Naomi Chen recommends handwritten ink pens exclusively inside European high schools.", "NOT GIVEN", "Dr. Chen memperingatkan bahaya larangan laptop, bukan menganjurkan pena eksklusif di SMA Eropa.", "B2"),
             _ynng("Encouraging laptop learners to synthesize ideas conceptually instead of typing word-for-word helps close cognitive losses.", "YES", "Paragraf E menganjurkan pelatihan pemetaan konsep alih-alih mengetik kata per kata.", "B2"),
             _rmatch("Match researchers accurately to their cited contribution in Paragraphs B and D:", [
                 {"left": "Mueller and Oppenheimer", "rightOptions": ["Verbatim typing reduces active mental paraphrasing", "Blanket laptop bans penalize students with motor fatigue"]},
                 {"left": "Dr. Naomi Chen", "rightOptions": ["Verbatim typing reduces active mental paraphrasing", "Blanket laptop bans penalize students with motor fatigue"]},
             ], "Mueller and Oppenheimer::Verbatim typing reduces active mental paraphrasing|Dr. Naomi Chen::Blanket laptop bans penalize students with motor fatigue", "Pemetaan studi Mueller/Oppenheimer vs perspektif aksesibilitas Chen."),
             _rfill("NUMBER ONLY (wpm): 'Approximate fast student lecture keyboard speed cited: ___ words per minute'", "60", ["60", "sixty"], "Paragraf A mencatat kecepatan ketik mendekati 60 kata per menit.", 1),
             _rfill("NUMBER ONLY (wpm): 'Typical slower manual pen handwriting rate per minute: barely ___'", "22", ["22", "twenty-two"], "Kecepatan tulis tangan sekitar 22 kata per menit.", 1),
             _rfill("ONE WORD ONLY: 'Generative mental skill forced upon pen users due to slow physical speed: active ___'", "paraphrasing", ["paraphrase", "paraphrasing", "condense"], "Tulis tangan memaksa penyaringan dan parafrase aktif.", 1),
             _rfill("ONE WORD ONLY: 'Fine-motor handwriting difficulty cited as benefiting genuinely from digital keyboards: ___'", "dysgraphia", ["dysgraphia"], "Paragraf D menyebut kondisi 'fine-motor dysgraphia'.", 1),
             _rmc("Why did air-gapped laptops without internet still produce weaker synthesis recall in Paragraph C?", ["Because near-effortless transcription velocity allowed minds to copy words passively without semantic filtering.", "Offline laptop keyboards glowed harshly causing immediate visual sleepiness.", "Pen paper ink emitted calming floral odors boosting hippocampal recall.", "Laptop batteries slowed screen scrolling every five minutes."], 0, "Kecepatan mengetik tinggi memungkinkan penyalinan pasif tanpa pemrosesan makna aktif."),
             _rmc("How should seminar instructors resolve the tension between deep synthesis and disability inclusion cleanly?", ["Keep laptop access open for accommodation while guiding learners away from blind transcription toward mapping.", "Collect all laptops at doorboxes except Fridays.", "Require blind typing exams strictly in noisy dining cafeterias.", "Grade handwritten calligraphy neatness exclusively."], 0, "Menjaga akses inklusif sekaligus membiasakan pembuatan bagan konsep daripada menyalin mentah."),
             _rmc("What evidence proved web pop-up distractions weren't the sole mechanism behind shallow laptop memory?", ["Controlled sessions using isolated offline computers locked in blank word processors showed matching gaps.", "All students deleted social apps permanently prior to semester surveys.", "Spiral notebooks carried colorful cartoons distracting cursive writers too.", "Lecture speakers banned overhead visual projection slides."], 0, "Uji laptop offline terisolasi tetap menampilkan ketertinggalan sintesis."),
             _rmc("Which summary best describes Paragraph E's final synthesis?", ["Tool modality matters less than deliberate active cognitive synthesis supported by inclusive classroom policy.", "Mechanical laptops will completely destroy traditional university learning forever.", "Only twenty-two words per minute should ever be graded cleanly.", "Students must carry twin wooden tablets plus spiral paper."], 0, "Kesimpulan memadukan sintesis aktif dengan kebijakan kelas inklusif."),
         ]),
    ],
    3: [
        # P1 (13)
        ("P1", "Timber Rafts and Shifting River Log Drives in Nineteenth-Century Forestry",
         "Paragraph A: Long before diesel logging freighters or muddy mountain switchback truck roads existed, felling tall temperate white pines across upper watersheds posed a giant haulage riddle: heavy waterlogged hardwoods sank swiftly whereas buoyant winter pine trunks floated effortlessly.\n\n"
         "Paragraph B: Northern lumber camps felled thousands of winter pines atop thick frozen river valleys until April snowmelt swollen streams unlocked natural liquid highways—launching dramatic roaring spring 'log drives' steering massive rafts straight down twisting gorges toward valley sawmills.\n\n"
         "Paragraph C: Navigating churning rapids demanded lethal physical agility from nimble 'river pigs' balancing atop rolling wet trunks using iron-spiked cork boots and long hooked pike poles—breaking terrifying upstream 'logjams' packed twenty meters high against narrow rocky bends with dangerous dynamite charges.\n\n"
         "Paragraph D: Beyond human perils, scouring timber torrents battered gravel trout redds clean away alongside ripping protective riparian willow roots—scars still visibly straightening eroded valley channels a full century later.\n\n"
         "Paragraph E: Today's watershed restoration teams deliberately helicopter large unanchored nurse tree trunks carefully BACK into cold mountain bends—not as fuel timber, but to rebuild complex underwater log pools sheltering fragile juvenile salmon.",
         [
             _tfng("Buoyant winter pine logs floated well on spring meltwaters unlike dense green hardwood trunks.", "TRUE", "Paragraf A menjelaskan kayu keras berat tenggelam sedangkan batang pinus ringan mengapung alami.", "B1"),
             _tfng("Lumber workers felled and dragged heavy timber chiefly during dry scorched August river lows.", "FALSE", "Paragraf B mencatat penebangan dikerjakan saat musim dingin di lembah beku lalu dihanyutkan tepat saat banjir salju mencair di bulan April.", "B1"),
             _tfng("Workers wore iron-spiked cork footwear to maintain grip atop wet spinning river trunks.", "TRUE", "Paragraf C menyebut sepatu berpaku besi ('iron-spiked cork boots').", "B1"),
             _tfng("Modern salmon-stream restoration helicopters reintroduce deliberate underwater wooden trunks to foster deep quiet fish pools.", "TRUE", "Paragraf E menguraikan penempatan ulang batang pohon besar guna membangun kolam perlindungan anak salmon.", "B1"),
             _tfng("Dynamic spring logjams generated slightly more sawmill timber income than straight railway shipping.", "NOT GIVEN", "Teks tidak membandingkan pendapatan ekonomi log drive vs jalur kereta api.", "B2"),
             _tfng("Historical spring timber drives left lasting straightening damage along once-meandering rocky channels.", "TRUE", "Paragraf D menegaskan bekas gerusan masih meluruskan alur lembah bahkan seabad kemudian.", "B2"),
             _tfng("Early logging companies routinely paid cash bonuses whenever trout spawning gravels doubled.", "NOT GIVEN", "Bonus perusahaan kayu untuk ikan trout tidak disebut di bacaan.", "B1"),
             _rfill("ONE WORD ONLY: 'Seasonal mountain event powering wild April log drives: spring ___'", "snowmelt", ["snowmelt"], "Paragraf B menyebut 'April snowmelt swollen streams'.", 1),
             _rfill("ONE WORD ONLY: 'Explosive material occasionally triggered to clear jammed twenty-meter river bottlenecks: ___'", "dynamite", ["dynamite"], "Paragraf C menyebut penggunaan bahan peledak dinamit ('dynamite charges').", 1),
             _rfill("NO MORE THAN TWO WORDS: 'Long hooked wooden tools river workers swung to direct rolling pine trunks: ___ poles'", "pike", ["pike"], "Disebut 'hooked pike poles'.", 1),
             _rfill("ONE WORD ONLY: 'Sensitive underwater gravelly fish nests wiped away by scouring torrents: trout ___'", "redds", ["redds"], "Paragraf D memakai istilah ekologi 'trout redds'.", 1),
             _rmatch("Match each chronological era cleanly to its river engineering treatment:", [
                 {"left": "Nineteenth-century winter drives", "rightOptions": ["Dragged straight heavy trunks ripping bends clean", "Hauled large nurse logs gently back by air"]},
                 {"left": "Modern salmon habitat recovery", "rightOptions": ["Dragged straight heavy trunks ripping bends clean", "Hauled large nurse logs gently back by air"]},
             ], "Nineteenth-century winter drives::Dragged straight heavy trunks ripping bends clean|Modern salmon habitat recovery::Hauled large nurse logs gently back by air", "Kontras historis perusakan alur vs pemulihan modern memakai helikopter."),
             _rmc("Why do modern biologists intentionally place large fallen wood back inside mountain rivers cleaned by ancestors?", ["Anchored trunks carve cool sheltered deep eddies hiding young salmon safely.", "Floating log rafts generate renewable hydroelectric current downstream.", "Wooden dams drain polluted highway runoff safely away.", "Trout devour rotten soft cedar bark across winter months."], 0, "Batang pohon besar menciptakan struktur kolam tenang pelindung ikan muda."),
         ]),
        # P2 (13)
        ("P2", "Can Passive Clay Coolers Outlast Electric Cold Chains for Rural Clinics?",
         "Paragraph A: Storing heat-sensitive childhood vaccines and rural farm greens reliably inside off-grid dry equatorial towns faces a stark bottleneck: dusty diesel generators cough steadily to breakdowns whenever expensive fuel deliveries stall past washed-out dirt tracks.\n\n"
         "Paragraph B: Engineers revived an ancient Saharan clay principle inside rugged modern terracotta 'zeer' evaporative pot-in-pot coolers—nesting an inner porous red-clay vessel smoothly inside a wider glazed outer pot bridged evenly with damp washed river sand.\n\n"
         "Paragraph C: As parched desert gusts sweep over moist perimeter sand seams, liquid evaporation draws intense latent heat rapidly straight out through microscopic terracotta pores—dropping internal chamber baskets fifteen full degrees below forty-degree afternoon veranda shade with zero batteries or compressors.\n\n"
         "Paragraph D: Field audits conducted by agricultural relief NGOs in arid northern Nigeria tracked crisp Roma tomatoes lasting three unbroken weeks inside zero-watt sand pots versus barely three mushy days piled in woven bamboo market baskets.\n\n"
         "Paragraph E: Note cleanly one decisive physical climate ceiling: evaporative coolers shine solely across arid breezy valleys under forty percent relative humidity; during steamy coastal monsoon weeks near ninety percent air saturation, slow damp evaporation stalls completely flat.",
         [
             _rmatch("Match Paragraphs B, C, D, and E to their core subject headings:", [
                 {"left": "Paragraph B", "rightOptions": ["Double-walled clay and damp sand construction", "Evaporative latent heat extraction mechanics", "Measured tomato shelf-life trial gains", "High-humidity monsoon operating limits"]},
                 {"left": "Paragraph C", "rightOptions": ["Double-walled clay and damp sand construction", "Evaporative latent heat extraction mechanics", "Measured tomato shelf-life trial gains", "High-humidity monsoon operating limits"]},
                 {"left": "Paragraph D", "rightOptions": ["Double-walled clay and damp sand construction", "Evaporative latent heat extraction mechanics", "Measured tomato shelf-life trial gains", "High-humidity monsoon operating limits"]},
                 {"left": "Paragraph E", "rightOptions": ["Double-walled clay and damp sand construction", "Evaporative latent heat extraction mechanics", "Measured tomato shelf-life trial gains", "High-humidity monsoon operating limits"]},
             ], "Paragraph B::Double-walled clay and damp sand construction|Paragraph C::Evaporative latent heat extraction mechanics|Paragraph D::Measured tomato shelf-life trial gains|Paragraph E::High-humidity monsoon operating limits", "Kesesuaian inti setiap paragraf mengenai pendingin tanah liat evaporatif."),
             _tfng("Dry outer terracotta shells prevent evaporation inside nested double-wall zeer units.", "FALSE", "Justru penguapan menembus pori tanah liat melalui pasir basah yang menarik panas laten keluar.", "B2"),
             _tfng("Trials in northern Nigeria preserved fresh tomatoes roughly seven times longer than bare woven baskets.", "TRUE", "Tiga minggu (21 hari) dibanding tiga hari (~7 kali lipat lebih awet).", "B1"),
             _tfng("Evaporative clay coolers drop temperatures sharply well across rainy tropical rainforests near ninety percent humidity.", "FALSE", "Paragraf E memperingatkan pada kelembapan tinggi ~90% penguapan melambat dan gagal mendinginkan.", "B1"),
             _tfng("Northern Nigerian clinic nurses preferred wooden tomato baskets strictly over electricity.", "NOT GIVEN", "Preferensi perawat klinik terhadap keranjang kayu dibanding listrik tidak dibahas.", "B2"),
             _rfill("ONE WORD ONLY: 'Porous moist mineral medium packed firmly separating twin nested pots: washed river ___'", "sand", ["sand"], "Paragraf B menyebut 'damp washed river sand'.", 1),
             _rfill("NUMBER ONLY (°C): 'Internal cooling drop achievable below 40°C dry shade: up to ___ degrees'", "15", ["15", "fifteen"], "Suhu turun hingga 15 derajat di bawah udara teduh luar.", 1),
             _rfill("NUMBER ONLY (weeks): 'Preserved market freshness recorded for tomatoes inside zeer chambers: ___ weeks'", "3", ["3", "three"], "Paragraf D mencatat tomat bertahan tiga minggu.", 1),
             _rfill("NUMBER ONLY (%): 'Ideal upper ambient dry-air humidity limit where zeer coolers perform strongly: below ___%'", "40", ["40", "forty"], "Bekerja optimal di daerah kering berkelembapan di bawah 40%.", 1),
             _rmulti("Choose TWO environmental prerequisites necessary for passive pot-in-pot units to chill cleanly:",
                    ["Low dry ambient air relative humidity", "Sealed electric lithium cooling fans underneath baskets", "Gentle continuous dry outdoor wind movement", "Flooded wet coastal monsoon rainfall daily"],
                    "Low dry ambient air relative humidity|Gentle continuous dry outdoor wind movement",
                    "Pendinginan evaporatif butuh udara kering berkelembapan rendah serta hembusan angin stabil."),
             _rmc("Why does continuous water topping along the mid-sand ring stay essential daily?", ["Cooling stops entirely once moisture inside sand seams dries dry.", "Dry sand shrinks smoothly cracking porcelain handles nightly.", "Bacteria drink water bubbles out of tomato skins.", "Diesel engines overheat inside moist sand walls."], 0, "Pasir harus tetap basah agar proses penguapan penarik panas tidak terhenti."),
             _tfng("Glazed non-porous outer vessels hold moist washed river sand around smaller porous clay containers.", "TRUE", "Paragraf B menerangkan wadah dalam dari tanah liat berpori diletakkan di dalam pot luar berlapis glasir berisi pasir basah.", "B1"),
             _rmc("What explains why rural clinic vaccine storage struggles with diesel chillers in Paragraph A?", ["Uncertain fuel transport over ruined dirt tracks plus mechanical breakdown risk.", "Clay vessels cost twenty times more than metal turbines.", "Vaccines lose potency rapidly when chilled gently indoors.", "Bamboo baskets overheat glass solar reflectors daily."], 0, "Paragraf A menyoroti gangguan pasokan solar di jalan tanah berlumpur dan kerusakan generator diesel.", "IELTS-MOD-08", "B1"),
         ]),
        # P3 (14)
        ("P3", "Do Automated Handwriting vs Early Reading Phonics Debates Miss Dyslexic Variety?",
         "Paragraph A: Bitter decades of polarized primary literacy policy swings pitched rigid phonics syllable-drills starkly against immersive unstructured picture-story reading—often leaving perplexed classroom teachers choosing sides like wartime trenches.\n\n"
         "Paragraph B: Large systematic neuroscience meta-analyses across diverse languages cleanly settled one opening question: explicit instruction linking spoken letter-sound phonemes reliably accelerates decoding fluency across both typical learners and struggling readers far more predictably than guessing words strictly from nearby picture clues.\n\n"
         "Paragraph C: Yet educational neuroscientist Dr. Priya Subramanian emphasizes fluency does not end cleanly inside isolated consonant flashcards! Once beginners decode print automatically, deep reading comprehension hinges sharply upon rich spoken oral vocabulary, complex background world knowledge, and varied story exposure.\n\n"
         "Paragraph D: Furthermore, brain imaging studies examining developmental dyslexia demonstrate phonological processing variations vary noticeably in degree—meaning stubborn fast-paced timed choral drills alone frustrate sensitive learners unless paired gently with multisensory tactile letter-tracing and patient individualized pacing.\n\n"
         "Paragraph E: Balanced structured literacy merges joyful high-interest read-aloud curiosity smoothly atop crystal-clear systematic sound-letter foundations rather than sacrificing either pillar.",
         [
             _ynng("Guessing unfamiliar text words strictly from nearby illustration context matches systematic phoneme decoding reliability.", "NO", "Paragraf B menegaskan instruksi eksplisit fonem huruf-bunyi jauh lebih andal dibanding menebak dari gambar.", "B1"),
             _ynng("Mastering accurate phonics word decoding automatically guarantees deep literary comprehension regardless of spoken vocabulary.", "NO", "Paragraf C menyoroti Dr. Subramanian bahwa pemahaman mendalam tetap menuntut kosakata lisan kaya dan pengetahuan latar.", "B2"),
             _ynng("Uniform ultra-fast timed sound drills suit every student carrying developmental dyslexia comfortably.", "NO", "Paragraf D mencatat latihan cepat seragam memicu frustrasi dan butuh tempo individual serta metode multisensori.", "B2"),
             _ynng("Phonemic neural imaging scans cost significantly more across Asian universities than American primary districts.", "NOT GIVEN", "Biaya pemindaian otak antarbenua tidak pernah dibahas.", "B2"),
             _ynng("Modern 'structured literacy' unites systematic sound instruction thoughtfully beside rich storytelling meaning.", "YES", "Paragraf E mendukung penggabungan fondasi fonem sistematis dengan keingintahuan cerita bermakna.", "B1"),
             _rmatch("Match each literacy perspective cleanly to its classroom focus:", [
                 {"left": "Explicit phoneme decoding", "rightOptions": ["Unlocks reliable word recognition early", "Supplies semantics beyond simple sounding-out", "Accommodates individual neural rhythm softly"]},
                 {"left": "Oral vocabulary & knowledge", "rightOptions": ["Unlocks reliable word recognition early", "Supplies semantics beyond simple sounding-out", "Accommodates individual neural rhythm softly"]},
                 {"left": "Multisensory individual pacing", "rightOptions": ["Unlocks reliable word recognition early", "Supplies semantics beyond simple sounding-out", "Accommodates individual neural rhythm softly"]},
             ], "Explicit phoneme decoding::Unlocks reliable word recognition early|Oral vocabulary & knowledge::Supplies semantics beyond simple sounding-out|Multisensory individual pacing::Accommodates individual neural rhythm softly", "Integrasi tiga komponen literasi terstruktur sesuai paragraf B–D."),
             _rfill("ONE WORD ONLY: 'Meta-analyses confirm linking spoken units cleanly speeds decoding: letter-sound ___'", "phonemes", ["phoneme", "phonemes"], "Paragraf B menyebut 'spoken letter-sound phonemes'.", 1),
             _rfill("ONE WORD ONLY: 'Additional sensory learning path paired with timing flexibility for dyslexic learners: ___ tracing'", "tactile", ["tactile"], "Paragraf D memakai istilah 'multisensory tactile letter-tracing'.", 1),
             _rfill("ONE WORD ONLY: 'Integrative classroom term merging clear phonics beside lively rich books: ___ literacy'", "structured", ["structured"], "Paragraf E menyebut pendekatan 'Balanced structured literacy'.", 1),
             _rfill("ONE WORD ONLY: 'Core cognitive reading skill unlocked once mechanical word-decoding reaches automatic speed: deep ___'", "comprehension", ["comprehension"], "Paragraf C menekankan pemahaman bacaan mendalam ('deep reading comprehension').", 1),
             _rmc("Why does the writer criticize historic literacy curriculum cycles in Paragraph A?", ["Binary pendulum debates forced artificial either-or trade-offs instead of synthesizing valid findings.", "Teachers uniformly refused reading illustrated novels aloud inside primary desks.", "Neuroscience brain imagers falsified childhood vocabulary flashcard outcomes.", "Phonics drills banned paper spiral pencils completely."], 0, "Perdebatan hitam-putih memaksa guru memilih satu kutub secara kaku alih-alih mengintegrasikannya."),
             _rmc("According to Dr. Priya Subramanian, what risks bottlenecking fourth-grade reading growth even in skilled phonetic decoders?", ["Shallow everyday oral vocabulary and narrow background knowledge across diverse topics.", "Excessive bright cursive wall calligraphy posters.", "Skipping fifty-word timed reading speed bells.", "Reading fascinating picture storybooks cleanly at dusk."], 0, "Kurangnya kosakata lisan serta pengetahuan dunia membatasi pemahaman tingkat lanjut."),
             _rmc("What evidence supports rejecting purely fast-paced one-size-fits-all phonics timelines?", ["Neuroimaging reveals varied phonological processing profiles requiring adaptive tactile support.", "Choral classrooms fail strictly during noisy winter snowstorms.", "Children prefer watching uncaptioned wildlife videos exclusively.", "Flashcard ink turns dull grey after twenty daily touches."], 0, "Pencitraan otak menunjukkan keragaman variasi fonologis antarindividu."),
             _rmc("What best reflects the author's concluding educational prescription?", ["Anchor sound-letter mechanics cleanly first while nurturing expressive oral stories and flexible pacing.", "Eliminate phonemic instruction entirely until high school seminar classes.", "Mandate computerized reading timers across kindergarten classrooms.", "Discard rich storybooks whenever phonetic spelling checklists appear."], 0, "Kesimpulan menyatukan fondasi fonem jelas + literasi kaya makna + fleksibilitas individual."),
         ]),
    ],
}


def build_ielts_reading_1_3(pack_num: int) -> tuple[list[dict], list[dict]]:
    return expand_ielts_reading(pack_num, _READING_1_3[pack_num])
