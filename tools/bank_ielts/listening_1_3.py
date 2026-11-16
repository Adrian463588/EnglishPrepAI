"""IELTS Academic Listening items Packs 1-3 (4 parts x 10 Qs = 40/pack = 120 items)."""

from pack_schema_helpers import make_q, rotated_mcq


def expand_ielts_listening(pack_num: int, parts: list) -> list[dict]:
    items = []
    q_idx = 1
    assert len(parts) == 4, f"Pack {pack_num} needs 4 IELTS Listening parts"
    for part_num, (part_title, turns, qs) in enumerate(parts, start=1):
        assert len(qs) == 10, f"Pack {pack_num} Part {part_num} needs 10 Qs, got {len(qs)}"
        aid = f"IELTS0{pack_num}-LP{part_num}"
        for sub_i, spec in enumerate(qs, start=1):
            qid = f"IELTS-A-0{pack_num}-Q{q_idx:03d}"
            ttype = spec["type"]
            full_prompt = f"Listening Part {part_num} ({part_title} — Q{q_idx}): {spec['prompt']}"
            if ttype == "FILL_BLANK":
                items.append(
                    make_q(
                        qid=qid,
                        section="LISTENING",
                        task_type="FILL_BLANK",
                        prompt=full_prompt,
                        options=[],
                        answer=spec["ans"],
                        explanation=spec["exp"],
                        rationale_source=spec.get("mod", "IELTS-MOD-01"),
                        difficulty=spec.get("diff", "B2"),
                        audio_id=aid,
                        audio_turns=turns if sub_i == 1 else [],
                        accepted_answers=spec.get("acc", [spec["ans"]]),
                        extra={"maxWords": spec.get("maxWords", 2)},
                    ),
                )
            elif ttype == "CHOICE_MULTI":
                items.append(
                    make_q(
                        qid=qid,
                        section="LISTENING",
                        task_type="CHOICE_MULTI",
                        prompt=full_prompt,
                        options=spec["opts"],
                        answer=spec["ans"],
                        explanation=spec["exp"],
                        rationale_source=spec.get("mod", "IELTS-MOD-07"),
                        difficulty=spec.get("diff", "B2"),
                        audio_id=aid,
                        audio_turns=turns if sub_i == 1 else [],
                    ),
                )
            elif ttype == "MATCHING":
                items.append(
                    make_q(
                        qid=qid,
                        section="LISTENING",
                        task_type="MATCHING",
                        prompt=full_prompt,
                        options=[],
                        answer=spec["ans"],
                        explanation=spec["exp"],
                        rationale_source=spec.get("mod", "IELTS-MOD-07"),
                        difficulty=spec.get("diff", "B2"),
                        audio_id=aid,
                        audio_turns=turns if sub_i == 1 else [],
                        extra={"pairs": spec["pairs"]},
                    ),
                )
            else:
                items.append(
                    rotated_mcq(
                        qid=qid,
                        section="LISTENING",
                        prompt=full_prompt,
                        options=spec["opts"],
                        correct_idx=spec["cidx"],
                        explanation=spec["exp"],
                        rationale_source=spec.get("mod", "IELTS-MOD-01"),
                        difficulty=spec.get("diff", "B2"),
                        seed=pack_num * 37 + q_idx,
                        audio_id=aid,
                        audio_turns=turns if sub_i == 1 else [],
                    ),
                )
            q_idx += 1
    assert len(items) == 40
    return items


def _fb(prompt, ans, acc, exp, max_w=2, mod="IELTS-MOD-01", diff="B1"):
    return {"type": "FILL_BLANK", "prompt": prompt, "ans": ans, "acc": acc, "exp": exp, "maxWords": max_w, "mod": mod, "diff": diff}


def _mc(prompt, opts, cidx, exp, mod="IELTS-MOD-07", diff="B2"):
    return {"type": "CHOICE_SINGLE", "prompt": prompt, "opts": opts, "cidx": cidx, "exp": exp, "mod": mod, "diff": diff}


def _multi(prompt, opts, ans, exp, mod="IELTS-MOD-07", diff="B2"):
    return {"type": "CHOICE_MULTI", "prompt": prompt, "opts": opts, "ans": ans, "exp": exp, "mod": mod, "diff": diff}


def _match(prompt, pairs, ans, exp, mod="IELTS-MOD-07", diff="B2"):
    return {"type": "MATCHING", "prompt": prompt, "pairs": pairs, "ans": ans, "exp": exp, "mod": mod, "diff": diff}


_PARTS_1_3 = {
    1: [
        ("Community Bike Repair & Tool Membership Inquiry", [
            ("W", "Good morning! I'd like to register for the community bike repair workshop this month."),
            ("M", "Happy to help! What's your surname for our sheet? Is it Patel or... wait let me type slowly."),
            ("W", "It's Alverez—A-L-V-E-R-E-Z. And I live along Willow Crescent, flat 14B."),
            ("M", "Got it, Alverez. Last spring we fit fifteen spots, sorry—with only six repair workstands open now, we strictly cap each evening session at twelve participants."),
            ("W", "Are Tuesday classes still held on Riverside Street?"),
            ("M", "Oh, we shifted out of Riverside Depot two weeks back—meet beside Harbor Annex in Hall Oak. Just remember bring clean rubber gloves for chain grease and lock helmets outside."),
            ("W", "Right! How much is the three-week tune-up fee including brake cables?"),
            ("M", "Tuition alone runs thirty-five pounds, though an optional seven-pound spare cable pack brings everything neatly to forty-two pounds—paid via bank card rather than cash at the gate."),
        ], [
            _fb("Spelling check (ONE WORD ONLY): Complete the surname: 'Applicant Surname: ___'", "Alverez", ["alverez"], "Wanita mengeja tegas: A-L-V-E-R-E-Z.", 1),
            _fb("Word/number (ONE WORD AND/OR A NUMBER): 'Address: Flat ___, Willow Crescent.'", "14B", ["14b"], "Nomor unit yang disebut adalah Flat 14B.", 1),
            _fb("Word/number (ONE WORD AND/OR A NUMBER): 'Maximum participants per session: ___'", "12", ["12", "twelve"], "Ia meralat 15 tahun lalu menjadi kapasitas kini maksimal 12 peserta.", 1),
            _mc("Why was group capacity capped lower this term?", ["Only six physical repair stands remain operational.", "Half the instructors moved to Riverside Depot.", "Fire wardens reduced seating across Willow Crescent.", "Only twelve rubber tool kits arrived downtown."], 0, "Ketersediaan enam standar kerja membatasi kelas maksimal 12 orang.", "IELTS-MOD-01", "B1"),
            _fb("NO MORE THAN TWO WORDS: 'Current venue name: Harbor ___'", "Annex", ["annex", "harbor annex"], "Lokasi pindah ke Harbor Annex meninggalkan Riverside Depot.", 2),
            _mc("Inside which room do repair benches stand?", ["Hall Oak.", "Riverside Lounge.", "Willow Room.", "Elm Basement."], 0, "Disebut bertemu di Harbor Annex bagian Hall Oak.", "IELTS-MOD-01", "B1"),
            _fb("NO MORE THAN TWO WORDS: 'Bring personal: clean rubber ___'", "gloves", ["gloves", "rubber gloves"], "Peserta diminta membawa sarung tangan karet bersih.", 2),
            _mc("Where should riders stow cycling helmets while inside the shop?", ["Locked neatly outside the workshop room.", "Hung atop upright brake workstands.", "Inside green plastic parts drawers.", "Stored beneath wooden reception stools."], 0, "Instruksi menyebut mengunci helm di luar ruang.", "IELTS-MOD-01", "B1"),
            _fb("A NUMBER ONLY: 'Combined total fee including optional cable kit (£): ___'", "42", ["42", "forty-two"], "Biaya kelas £35 + paket kabel £7 menjadi total £42.", 1),
            _mc("Which payment mode is accepted at the workshop check-in desk?", ["Bank payment card.", "Coins wrapped inside cash envelopes.", "Municipal library fee stamps.", "Bookstore voucher tokens."], 0, "Pembayaran diterima melalui bank card alih-alih tunai.", "IELTS-MOD-01", "B1"),
        ]),
        ("District Heating & Solar Eco-Center Tour Briefing", [
            ("W", "Welcome visitors to the Municipal Clean-Heat Energy Center! Please keep orange safety lanyards clipped visibly whenever walking past pipe galleries."),
            ("W", "In 2016 this former brick coal station reopened strictly as an automated heat-exchange hub feeding four thousand coastal apartments using deep saltwater heat pumps paired with rooftop thermal glass tubes."),
            ("W", "On your right, Gallery One houses insulated blue distribution valves built back in 2018; opposite sits Gallery Two storing solid sand-battery heat silos commissioned later in 2022, capable of banking surplus wind energy cleanly for eleven weeks."),
            ("W", "Watch closely past Hall Three—overhead steam vents stay closed weekdays until technicians test bronze check-gauges Fridays after four, so photography stays prohibited solely beside pressurized Pump Room Four while welcome everywhere else."),
        ], [
            _fb("ONE WORD ONLY: 'Wear visible ___ safety lanyards across pipe galleries.'", "orange", ["orange"], "Pemandu menginstruksikan lanyard pengaman berwarna oranye.", 1, "IELTS-MOD-07"),
            _mc("When did the old coal warehouse convert into a modern heat-exchange facility?", ["In 2016.", "In 2018.", "In 2022.", "Last Friday afternoon."], 0, "Peralihan fungsi gedung stasiun batu bara tua berlangsung tahun 2016."),
            _fb("NUMBER ONLY: 'Approximate apartment homes heated downtown: ___'", "4000", ["4000", "4,000", "four thousand"], "Menyuplai panas bagi sekitar 4.000 apartemen pesisir.", 2),
            _mc("What two renewable sources combine cleanly to warm municipal apartment water loops?", ["Deep sea heat pumps paired with rooftop thermal glass solar panels.", "Wood-pellet ovens paired with diesel harbor barges.", "Coal dust gasifiers paired with shallow tidal dams.", "Natural gas turbines paired cleanly with mountain glaciers."], 0, "Pompa panas air laut + panel surya termal atap."),
            _match("Match each tour Wing to its main equipment:", [
                {"left": "Gallery One", "rightOptions": ["Insulated 2018 distribution valves", "2022 sand-battery heat silos", "Restricted high-pressure pump hall"]},
                {"left": "Gallery Two", "rightOptions": ["Insulated 2018 distribution valves", "2022 sand-battery heat silos", "Restricted high-pressure pump hall"]},
                {"left": "Pump Room Four", "rightOptions": ["Insulated 2018 distribution valves", "2022 sand-battery heat silos", "Restricted high-pressure pump hall"]},
            ], "Gallery One::Insulated 2018 distribution valves|Gallery Two::2022 sand-battery heat silos|Pump Room Four::Restricted high-pressure pump hall", "Urutan tur: Gallery One (katup 2018), Gallery Two (baterai pasir 2022), Pump Room Four (ruang pompa bertekanan)."),
            _fb("NO MORE THAN TWO WORDS: 'Surplus sand-battery heat lasts up to ___'", "11 weeks", ["11 weeks", "eleven weeks"], "Menyimpan surplus energi panas angin hingga sebelas minggu.", 2, "IELTS-MOD-07"),
            _mc("When do maintenance crews run bronze check-gauge tests on overhead steam vents?", ["On Fridays after 4:00 PM.", "Every weekday noon cleanly.", "Tuesday mornings at nine.", "Strictly on winter holidays."], 0, "Pengujian berlangsung hari Jumat setelah pukul empat sore."),
            _mc("Where inside the Eco-Center are visitor cameras temporarily banned?", ["Inside pressurized Pump Room Four only.", "Throughout all public entrance brick halls.", "Looking upward toward rooftop solar panels.", "Beside sand silos in Gallery Two."], 0, "Fotografi dilarang khusus di dekat ruang pompa bertekanan Room Four."),
            _fb("ONE WORD ONLY: 'Battery thermal storage inside Gallery Two relies on solid ___.'", "sand", ["sand"], "Silo penyimpanan panas menggunakan media pasir ('sand-battery').", 1, "IELTS-MOD-07"),
            _mc("What overall purpose drives this Municipal Clean-Heat briefing?", ["Orienting tour guests safely across historic conversion milestones and active equipment rooms.", "Recruiting apprentice diesel boiler stokers immediately.", "Selling individual homeowner coal stoves cheaply.", "Advertising weekend harbor ferries outside Hall Three."], 0, "Pengarahan orientasi keselamatan dan teknologi transisi panas gedung."),
        ]),
        ("Solar Water-Heater Student Research Review", [
            ("M", "Dr. Nair, Maya and I brought our rooftop solar hot-water prototype logs—did the delayed city rooftop permit stall testing like last month?"),
            ("W", "No, the building office actually cleared your permit Monday—wait, what held up last week's thermal efficiency data instead?"),
            ("F" if False else "W2" if False else "W", "Wait—let me confirm cleanly: earlier copper pipe solder joints cracked twice under freeze expansion AND our infrared thermometer sensor spat wild noisy spikes until swapping probes Tuesday."),
            ("M", "True—once we re-soldered the copper manifold and fitted fresh platinum thermistors, water gain stabilized sharply around sixty-four percent! Now should we drop cold December trials from Chapter Three or highlight cloudy recovery too?"),
            ("W", "Keep overcast cold metrics prominently right beside sunny averages—urban housing boards care far more about January reliability than sunny August peaks."),
        ], [
            _multi("Choose TWO verified causes that delayed the solar prototype trial last week:",
                   ["Cracked copper manifold solder joints", "Delayed municipal rooftop permit", "Faulty infrared temperature sensor", "Missing roof solar collector glass"],
                   "Cracked copper manifold solder joints|Faulty infrared temperature sensor",
                   "Izin atap sudah terbit Senin; kendala nyata berasal dari sambungan tembaga retak dan sensor inframerah rusak."),
            _mc("When did municipal building clerks officially clear their rooftop testing permit?", ["Earlier on Monday.", "Two winters prior.", "Waitlist paused permanently.", "Thursday evening past midnight."], 0, "Izin atap sebenarnya sudah diterbitkan hari Senin."),
            _fb("NUMBER ONLY (%): 'Steady thermal capture gain once sensor swaps finished: ___%'", "64", ["64", "sixty-four"], "Setelah diperbaiki, tangkapan panas stabil sekitar 64%.", 1, "IELTS-MOD-07"),
            _mc("What upgraded sensor cured unstable noisy readings on Tuesday?", ["Swapping onto platinum thermistor probes.", "Gluing cardboard shades over mercury tubes.", "Doubling copper pipe wall paint thickness.", "Cleaning glass panels using tap vinegar."], 0, "Mereka mengganti sensor ke termistor platina."),
            _mc("What draft thesis edit does the student initially consider cutting?", ["Omitting sluggish overcast December readings from Chapter Three.", "Expanding bibliography footnotes across ten extra books.", "Adding brightly painted yellow roof schematics.", "Shortening abstract words down to twenty."], 0, "Mahasiswa mempertimbangkan membuang uji Desember berawan karena angkanya rendah."),
            _mc("Why does Dr. Nair insist retaining gloomy winter data visibly in the report?", ["Urban building managers judge viability primarily by overcast winter dependability.", "Sunny August benchmarks contain illegal metric errors.", "Chapter Three lacks sufficient quantitative tables otherwise.", "Platinum probes stop recording strictly during warm July sun."], 0, "Dewan perumahan kota menilai kehandalan musim dingin berawan jauh lebih krusial."),
            _match("Match each component issue with its corrective step:", [
                {"left": "Copper manifold joints", "rightOptions": ["Re-soldered against freezing expansion", "Replaced with platinum probe", "Cleared Monday by office"]},
                {"left": "Temperature readout spikes", "rightOptions": ["Re-soldered against freezing expansion", "Replaced with platinum probe", "Cleared Monday by office"]},
                {"left": "Rooftop testing permit", "rightOptions": ["Re-soldered against freezing expansion", "Replaced with platinum probe", "Cleared Monday by office"]},
            ], "Copper manifold joints::Re-soldered against freezing expansion|Temperature readout spikes::Replaced with platinum probe|Rooftop testing permit::Cleared Monday by office", "Ringkasan kronologis perbaikan sistem serta status izin."),
            _mc("How does Dr. Nair assess their engineering troubleshooting response overall?", ["Constructively appreciative of verifying hardware before trusting cold averages.", "Disappointed they missed July hurricane testing cycles.", "Alarmed copper pipes violated municipal fire setbacks.", "Skeptical solar pipes can heat housing water anywhere."], 0, "Apresiatif atas ketelitian mengisolasi masalah perangkat keras serta mempertahankan data lapangan jujur."),
            _fb("ONE WORD ONLY: 'What structural liquid hazard fractured the initial copper joints? ___ expansion.'", "freeze", ["freeze"], "Ekspansi pembekuan air mematahkan sambungan las tembaga.", 1, "IELTS-MOD-07"),
            _mc("Which chapter will display overcast winter figures directly beside sunny runs?", ["Chapter Three.", "Chapter One.", "Appendix B.", "Bibliography Index."], 0, "Mahasiswa menyebut data Desember tersebut di Chapter Three."),
        ]),
        ("University Lecture — Deep Geothermal District Heating Networks", [
            ("W", "Today's Energy Systems lecture contrasts backyard residential ground loops with full city-scale deep geothermal district heating networks powering thousands of homes continuously."),
            ("W", "Unlike shallow trenches pulling gentle twelve-degree subsoil heat, deep geothermal wells drill roughly two kilometers downward straight through dense shale caps until tapping natural hot salt water—brine—trapped inside porous sandstone rock strata near ninety degrees Celsius."),
            ("W", "A production pump lifts scalding brine upward through insulated steel pipes into a city substation titanium heat-exchanger, warming separate clean municipal freshwater radiating gently underneath neighborhood street mains without smelly dissolved brine ever touching home taps."),
            ("W", "Crucially, cooled spent brine gets pumped continuously back down a companion injection well half a kilometer away—preserving deep underground pore pressure and preventing land sinking above historic neighborhoods for decades."),
        ], [
            _fb("NUMBER ONLY (km): 'Approximate vertical depth drilled to reach deep urban geothermal brine: ___ kilometers'", "2", ["2", "two"], "Sumur pengeboran mencapai kedalaman sekitar dua kilometer.", 1, "IELTS-MOD-07"),
            _fb("ONE WORD ONLY: 'Natural hot subterranean brine sits inside permeable ___ strata.'", "sandstone", ["sandstone"], "Dosen menyebut lapisan batuan berpori 'porous sandstone strata'.", 1, "IELTS-MOD-07"),
            _fb("ONE WORD ONLY: 'Impermeable geological rock layer sealing deep hot brine below: dense ___ caps.'", "shale", ["shale"], "Penutup geologis kedap di atas lapisan reservoar disebut 'shale caps'.", 1, "IELTS-MOD-07"),
            _fb("NUMBER ONLY (°C): 'Typical subterranean brine extraction temperature noted: ___ degrees Celsius.'", "90", ["90", "ninety"], "Suhu air garam panas alami mendekati 90 °C.", 1, "IELTS-MOD-07"),
            _mc("What metal composes the core city substation heat exchanger transferring warmth safely?", ["Corrosion-resistant titanium plates.", "Porous soft terracotta bricks.", "Copper alloy bicycle tubing.", "Unlined grey Portland concrete."], 0, "Penukar panas menggunakan pelat titanium tahan korosi garam."),
            _mc("Why doesn't mineral-salty deep brine corrode apartment bathroom pipes indoors?", ["Heat passes indirectly inside closed titanium exchangers into clean municipal freshwater mains.", "Brine boils completely dry before climbing above five hundred meters.", "Municipal homes run purely dry air blowers instead of hydronic pipes.", "Filter salt baskets clear seawater completely downstream."], 0, "Air geothermal tidak bercampur langsung; panasnya ditransfer ke loop air bersih terpisah."),
            _fb("ONE WORD ONLY: 'Spent chilled brine returns underground via a twin ___ well.'", "injection", ["injection"], "Air dingin dipompa kembali melalui sumur injeksi pendamping ('injection well').", 1, "IELTS-MOD-07"),
            _mc("What twin environmental harms does continuous brine reinjection actively prevent?", ["Losing deep aquifer pore pressure AND causing gradual ground subsidence overhead.", "Heating nearby winter coastal rivers above boiling temperatures.", "Flooding municipal transit subway roofs daily.", "Dissolving porous backyard limestone garden walls."], 0, "Re-injeksi menjaga tekanan pori reservoar serta mencegah penurunan muka tanah."),
            _fb("NO MORE THAN TWO WORDS: 'Rough spacing keeping chilled return water clear of hot production draw: ___ kilometer'", "half a", ["half a", "0.5"], "Sumur injeksi ditempatkan sekitar setengah kilometer jauhnya.", 2, "IELTS-MOD-07"),
            _mc("How does deep geothermal district warming fundamentally differ from shallow suburban loops?", ["It taps hot deep sandstone reservoirs directly rather than low-grade shallow garden warmth.", "It burns volcanic surface oil alongside wind generators.", "Shallow trenches pump brine down two-kilometer boreholes.", "Residential boilers discard brine straight into rivers."], 0, "Memanfaatkan panas alami suhu tinggi di kedalaman 2 km alih-alih suhu tanah permukaan 12 °C."),
        ]),
    ],
    2: [
        ("Coastal Eco-Volunteer Registration Phone Call", [
            ("M", "Hello, Harbour Conservation Society! Speaking with Liam—how can I assist you?"),
            ("W", "Hi Liam, I'd love joining next Saturday's oyster-shell bag placement along Mudflat Point. What equipment is provided?"),
            ("M", "Welcome aboard! Let me enter your name cleanly: is that Hannah Brooks, phone double-zero-four-seven-eight-five?"),
            ("W", "Actually double-zero-four-seven-nine-five! Also, I own tall rubber waders—should I bring spare gardening shovels too?"),
            ("M", "Leave shovels home—our barge carries stacked stainless tongs and woven coir mesh bags already. Bring polarized sunglasses plus at least two liters of drinking water since Mudflat pier taps stayed capped until November."),
            ("W", "Check! Do morning minivans still pick up volunteers at Central Rail Station at seven-thirty?"),
            ("M", "Slightly earlier due to low spring tide windows: climb aboard outside Station Gate North promptly at seven-ten, and lunch wraps are vegetarian millet bowls unless noting dietary requests beforehand."),
        ], [
            _fb("NUMBER ONLY: 'Corrected telephone ending for Hannah Brooks: 0047___'", "95", ["95"], "Ia meralat 85 menjadi 95 ('double-zero-four-seven-nine-five').", 1),
            _fb("NO MORE THAN TWO WORDS: 'Restoration site name: ___ Point'", "Mudflat", ["mudflat", "mudflat point"], "Lokasi pemasangan kantong cangkang adalah Mudflat Point.", 2),
            _fb("ONE WORD ONLY: 'Natural biodegradable mesh bag fiber provided on barge: ___'", "coir", ["coir"], "Karung jaring alami yang dipakai berbahan serat sabut kelapa ('coir').", 1),
            _mc("Which personal item does Liam confirm Hannah does NOT need to pack from home?", ["Heavy steel digging shovels.", "Tall waterproof rubber boots/waders.", "Polarized sunny glasses.", "Reusable two-liter hydration bottles."], 0, "Sekop disediakan penuh di tongkang; peserta tidak perlu membawa dari rumah."),
            _fb("NUMBER ONLY (liters): 'Minimum recommended personal drinking water per volunteer: ___ liters'", "2", ["2", "two"], "Minimal membawa dua liter air minum pribadi.", 1),
            _mc("Why must volunteers haul personal hydration supplies along Mudflat pier?", ["Drinking water pier faucets stay shut off until November.", "Brackish oyster barge tanks spoil bottled milk quickly.", "Hot sun cracks glazed clay coffee flasks immediately.", "Local bylaws ban plastic bottle tops past Station Gate North."], 0, "Keran air minum dermaga ditutup hingga bulan November."),
            _fb("TIME ONLY: 'Adjusted Central Station Gate North minivan pickup time: ___'", "7:10", ["7:10", "7.10", "seven-ten"], "Dipercepat dari 07.30 menjadi tepat 07.10 menyesuaikan jadwal surut."),
            _mc("What dictates pulling pickup twenty minutes earlier Saturday?", ["Timing the low spring tidal window safely before water returns.", "Saturday train repairs blocking Central Rail platforms.", "Heavy afternoon coastal thunderstorm alerts.", "Bakery truck schedules downtown."], 0, "Jadwal dimajukan demi mengejar jendela air surut terendah."),
            _fb("ONE WORD ONLY: 'Default volunteer lunch bowls feature grain: ___'", "millet", ["millet"], "Makan siang bawaan standar berupa mangkuk vegetarian berbahan millet.", 1),
            _mc("What pickup entrance must Hannah wait beside at Central Station?", ["Station Gate North.", "Harbor South Ferry Arch.", "Willow Crescent Gate.", "East Stadium Terminal."], 0, "Minivan menunggu di luar Station Gate North."),
        ]),
        ("City Wetland Park & Bird Hide Audio Orientation", [
            ("M", "Good morning cyclists and birdwatchers joining our Reed-Marsh Wetland Park launch!"),
            ("M", "First opened in 2019 atop abandoned industrial freight sidings, this forty-hectare basin cleans storm runoff cleanly before it enters East Estuary while welcoming public trail walks."),
            ("M", "Observe trail color markers carefully: the Blue Boardwalk spans level timber decks accessible cleanly to wheelchairs leading past open heron rookeries; the Yellow Spur dips across stepping logs where sturdy ankle boots are safest."),
            ("M", "Between March first and June fifteenth, Northern Marsh gates lock strictly shut to shelter nesting lapwings cleanly away from stray dogs—meanwhile South Wooden Hide stays unlocked dawn-to-dusk year-round carrying complimentary tripod binoculars for quiet observation."),
        ], [
            _fb("NUMBER ONLY: 'Year Wetland Park officially opened atop old rail sidings: ___'", "2019", ["2019"], "Taman lahan basah dibuka pada tahun 2019.", 1, "IELTS-MOD-07"),
            _fb("NUMBER ONLY (hectares): 'Total surface park area treating urban storms: ___ hectares'", "40", ["40", "forty"], "Luas cekungan taman mencapai 40 hektar.", 1, "IELTS-MOD-07"),
            _match("Match each park feature or trail color to its rule:", [
                {"left": "Blue Boardwalk", "rightOptions": ["Step-free accessible timber path", "Log stepping trail needing boots", "Seasonal spring bird lockup"]},
                {"left": "Yellow Spur", "rightOptions": ["Step-free accessible timber path", "Log stepping trail needing boots", "Seasonal spring bird lockup"]},
                {"left": "Northern Marsh", "rightOptions": ["Step-free accessible timber path", "Log stepping trail needing boots", "Seasonal spring bird lockup"]},
            ], "Blue Boardwalk::Step-free accessible timber path|Yellow Spur::Log stepping trail needing boots|Northern Marsh::Seasonal spring bird lockup", "Blue=akses kursi roda; Yellow=batang pijakan butuh sepatu bot; Northern Marsh=ditutup Maret–Juni."),
            _mc("What former land use occupied this clean basin site before transformation?", ["Abandoned industrial rail freight sidings.", "Deep concrete municipal car washes.", "Medieval lime brick factories.", "Commercial salmon drying sheds."], 0, "Sebelumnya merupakan lahan rel barang industri terbengkalai."),
            _fb("ONE WORD ONLY: 'Ground-nesting species protected by March–June Northern gate closures: ___'", "lapwings", ["lapwings"], "Penutupan musim semi melindungi burung lapwing yang bersarang di tanah.", 1, "IELTS-MOD-07"),
            _mc("Which amenity sits unlocked every morning until sunset inside South Wooden Hide?", ["Shared mounted tripod viewing binoculars free for quiet visitors.", "Coin espresso vending machines beside duck ponds.", "Heated limestone benches powered by biogas.", "Lending shelves stocked with watercolor easels."], 0, "Tersedia teropong tripod gratis di South Wooden Hide sepanjang tahun."),
            _fb("NO MORE THAN TWO WORDS: 'Recipient body receiving cleansed storm outflows: East ___'", "Estuary", ["estuary", "east estuary"], "Air limpasan bersih dialirkan menuju East Estuary.", 2, "IELTS-MOD-07"),
            _mc("When are Northern Marsh trails open freely for ordinary pedestrian hiking?", ["Outside March 1 through June 15 nesting closures.", "Solely Monday rainy evenings before midnight.", "Throughout spring bird hatching weeks only.", "Strictly while Yellow Spur logs submerge."], 0, "Di luar periode penguncian 1 Maret–15 Juni."),
            _mc("Why are dogs excluded strictly near spring marsh gates?", ["Preventing disturbance to vulnerable ground-nesting wetland birds.", "Wooden boardwalk varnish peels cleanly under dog claws.", "Marsh crabs pinch unleashed pets regularly.", "Park water tastes too salty for domestic dogs."], 0, "Menjaga ketenangan burung yang bersarang di permukaan rawa."),
            _mc("What accessibility trait distinguishes the Blue Boardwalk clearly?", ["Smooth level plank decking usable smoothly by mobility wheelchairs.", "Narrow cable suspension ropes spanning flood canals.", "Bright neon floodlights glowing through night skies.", "Audio megaphone warnings alerting passing ferries."], 0, "Dek kayu rata bebas tangga ramah kursi roda."),
        ]),
        ("Clinical Acoustics Noise-Trial Literature Review", [
            ("W", "Oliver, how's our literature synthesis on hospital ICU night-noise abatement coming ahead of Friday's seminar?"),
            ("M", "Wait—look closely at Ward Seven data! I originally assumed blinking overhead monitor bells accounted for most patient sleep waking, yet acoustic logs point to something surprising."),
            ("W", "Don't tell me cleaning hallway squeegee carts or squeaky linen trolleys woke sleepers even louder?"),
            ("M", "Spot on! Unlubricated polyurethane trolley casters clattering down tiled vinyl joints spike sudden sixty-eight-decibel transients—whereas quiet nursing whisper tones barely hit forty! Wait: Dr. Morales questioned whether sample night spans lasted long enough though."),
            ("W", "Right—let's exclude single-weekend pilot charts and weigh multi-month sleep-tracker studies heaviest inside Table Two so reviewers don't tear into weak short samples."),
        ], [
            _multi("Choose TWO unexpected acoustic insights highlighted by Oliver in ICU hallway logs:",
                   ["Hard cart wheels crossing floor seams generated sharp noise spikes", "Clinical monitor alarms remained completely mute", "Soft nursing staff voices stayed relatively quiet (~40 dB)", "ICU sleep trackers recorded exclusively morning daylight"],
                   "Hard cart wheels crossing floor seams generated sharp noise spikes|Soft nursing staff voices stayed relatively quiet (~40 dB)",
                   "Roda troli di sambungan lantai memicu lonjakan ~68 dB, sedangkan suara percakapan staf rendah (~40 dB)."),
            _mc("What mistaken assumption did Oliver hold at the start of reading Ward Seven logs?", ["He guessed clinical medical alarms dominated night sleep disruption.", "He assumed ICU corridors used quiet plush wool carpet.", "He thought polyurethane trolley wheels made zero sound cleanly.", "He blamed outdoor diesel buses passing hospital wards."], 0, "Ia semula menyangka alarm monitor pasien yang paling sering membangunkan."),
            _fb("NUMBER ONLY (dB): 'Sharp acoustic peak produced by unlubricated wheel carts rolling seams: ___ decibels'", "68", ["68", "sixty-eight"], "Lonjakan mendadak troli mencapai 68 desibel.", 1),
            _mc("What methodological critique did Dr. Morales raise against minor early studies?", ["Short single-weekend observation windows lacked statistical weight.", "Sixty-eight decibel sensors drained ward wall batteries fast.", "ICU nursing journals prohibited formatting comparative Table Two.", "Polyurethane wheels slipped across smooth hospital tile."], 0, "Studi percontohan satu akhir pekan dinilai durasinya terlalu singkat."),
            _mc("How will the research pair address Dr. Morales's sample-duration criticism in Table Two?", ["Prioritizing longer multi-month objective sleep-tracker trials over brief weekend samples.", "Erasing decibel measurement numbers entirely from text.", "Adding noisy cafeteria data cleanly beside Ward Seven logs.", "Asking nurses to skip overnight trolley linen swaps silently."], 0, "Memfokuskan bobot analisis pada studi multi-bulan berdurasi panjang."),
            _match("Match each ICU sound factor with its log value or decision:", [
                {"left": "Trolley wheels on seams", "rightOptions": ["Spikes up to 68 dB", "Stays mild near 40 dB", "Excluded for short duration"]},
                {"left": "Staff quiet speech", "rightOptions": ["Spikes up to 68 dB", "Stays mild near 40 dB", "Excluded for short duration"]},
                {"left": "Single-weekend pilots", "rightOptions": ["Spikes up to 68 dB", "Stays mild near 40 dB", "Excluded for short duration"]},
            ], "Trolley wheels on seams::Spikes up to 68 dB|Staff quiet speech::Stays mild near 40 dB|Single-weekend pilots::Excluded for short duration", "Kecocokan hasil pengamatan suara ICU serta keputusan metodologis."),
            _fb("ONE WORD ONLY: 'Wheel treads rattling vinyl corridor seams were made of ___'", "polyurethane", ["polyurethane"], "Bahan roda troli yang disebut adalah poliuretan ('polyurethane').", 1),
            _mc("When is Oliver and his partner's research seminar presentation scheduled?", ["On Friday.", "Tuesday afternoon.", "Last November Monday.", "Next spring vacation."], 0, "Mereka mempresentasikan tinjauan pustaka ini di seminar hari Jumat."),
            _mc("What practical engineering fix naturally follows from Ward Seven's finding?", ["Fitting softer lubricated casters and smoothing vinyl floor joints cleanly.", "Silencing critical patient heart telemetry monitors entirely.", "Replacing ceramic kitchen tiles outdoors along walkways.", "Banning nighttime hospital laundry delivery completely."], 0, "Pelumasan serta roda lunak/lantai mulus langsung menekan lonjakan bising 68 dB."),
            _fb("NUMBER ONLY (dB): 'Approximate level of gentle corridor nursing conversation: ___ decibels'", "40", ["40", "forty"], "Suara perawat hanya sekitar 40 dB.", 1),
        ]),
        ("University Lecture — Eutrophication & Estuarine Seagrass Recovery", [
            ("W", "Why did sprawling underwater eelgrass meadows across sheltered coastal bays die off rapidly during post-war suburban growth even where ships rarely dropped anchor?"),
            ("W", "Trace upstream storm pipes: soluble turf fertilizers and septic nitrate surges fuel explosive green planktonic surface algal blooms, shading crystal-clear shallows into soupy opaque darkness so bottom grass photosynthesis stalls."),
            ("W", "Even worse, when billions of surface algae cells die, sink, and rot along calm bay bottoms, greedy bacterial decay strips dissolved oxygen clean down into lifeless 'hypoxic' dead zones suffocating slow seafloor scallops."),
            ("W", "Encouragingly, once bay municipalities upgraded sewage plants to biological denitrification and cut summer lawn nitrogen runoff, satellite water-transparency monitors charted sunlit clarity bouncing back—followed closely by resurgent crab nurseries weaving through freshly recovered green meadows within seven years."),
        ], [
            _mc("What chain reaction initially starves submerged coastal seagrass meadows of energy?", ["Runoff fertilizer nutrients fuel dense surface algae blooms blocking sunlight from reaching bottom blades.", "Commercial container anchors shred deep oceanic seabed mud hourly.", "Cold winter tides strip salt water completely clean out of harbors.", "Hungry coastal seagulls devour underwater green stems daily."], 0, "Ledakan alga permukaan akibat limbah nutrien menutupi sinar matahari ke dasar laut."),
            _fb("ONE WORD ONLY: 'Biological term for dangerously oxygen-starved benthic water zones: ___'", "hypoxic", ["hypoxic"], "Istilah zona perairan miskin oksigen adalah 'hypoxic'.", 1, "IELTS-MOD-07"),
            _mc("How specifically do dying surface algae plumes drain deep bottom-water oxygen?", ["Bottom bacteria devour huge dissolved oxygen reserves while decomposing sunken dead algae.", "Living green algae exhale toxic carbon steam directly into scallops.", "Surface scum blocks coastal storm breezes from lifting heavy salt mud.", "Submerged eelgrass breathes underwater oxygen exclusively during sunny noons."], 0, "Bakteri pengurai alga mati menghabiskan oksigen terlarut di dasar laut."),
            _fb("ONE WORD ONLY: 'Filter shellfish harmed swiftly when bottom dead zones expand: ___'", "scallops", ["scallops"], "Kuliah mencontohkan kerang 'scallops' yang tercekik di zona mati dasar.", 1, "IELTS-MOD-07"),
            _fb("NO MORE THAN TWO WORDS: 'Key sewage upgrade removing nitrogen cleanly before estuary discharge: biological ___'", "denitrification", ["denitrification", "biological denitrification"], "Teknologi instalasi air limbah disebut 'biological denitrification'.", 2, "IELTS-MOD-07"),
            _fb("NUMBER ONLY (years): 'Recovery span observed for crab-sheltering seagrass beds after nutrient cuts: within ___ years'", "7", ["7", "seven"], "Pemulihan padang lamun & kepiting tercatat dalam kurun tujuh tahun.", 1, "IELTS-MOD-07"),
            _mc("What satellite telemetry metric confirmed improving water health upstream of eelgrass growth?", ["Measured increases in water light clarity / transparency.", "Rising hot mid-August seawater salt percentages.", "Higher surface algae bloom thickness indices.", "Sinking estuarine sediment sand depth counts."], 0, "Pemantau satelit melacak kenaikan kejernihan/transparansi air."),
            _mc("Why is the recovery example included at the conclusion of this lecture?", ["To demonstrate degraded coastal marine ecosystems can rebound measurably once nutrient pollution drops.", "To discourage municipal spending on household wastewater engineering.", "To argue anchor chains outperform biological denitrification effortlessly.", "To suggest crabs prefer muddy bare silt over sunlit green meadows."], 0, "Membuktikan kerusakan eutrofikasi dapat pulih bila beban nutrien dipangkas."),
            _fb("ONE WORD ONLY: 'Major non-point upland contributor linked to suburban lawns: septic ___ surges'", "nitrate", ["nitrate"], "Disebut limpasan nitrat pupuk halaman dan septik ('septic nitrate').", 1, "IELTS-MOD-07"),
            _mc("Which organism category flourished alongside rejuvenated seagrass nurseries after water cleared?", ["Juvenile coastal crabs.", "Subsurface oil worms.", "Deep oceanic sperm whales.", "Urban brick streetcar bugs."], 0, "Teks menyebut kembalinya pembibitan kepiting ('resurgent crab nurseries')."),
        ]),
    ],
    3: [
        ("Art Foundry Bronze Casting Studio Sign-Up", [
            ("M", "Afternoon, Fine Arts Metals Desk! Calling about the Saturday community sculpture pouring demonstration?"),
            ("W", "Yes! Look at our waiver board: is your contact email registered neatly under 't-k-a-p-u-r' at mail-dot-edu, or Kapur with double-P?"),
            ("M", "Single P—T-K-A-P-U-R! Wait—how long do beginner sand-mold reliefs take from pouring molten bronze through grinding seams?"),
            ("W", "Safety briefings start promptly at nine-fifteen, molten pours run noon till one-thirty, and cool breakouts finish at three-forty-five—wait, sorry, afternoon cleanup pushes wrap-up smoothly until four-fifteen."),
            ("M", "Awesome! Should I wear regular canvas sneakers alongside old denim jeans?"),
            ("W", "Old heavy non-frayed jeans work great, yet canvas melts fast if molten droplets bounce! Wear sturdy leather lace-up boots strictly, plus borrow our green chrome-leather aprons free at lockers."),
            ("M", "Smart warning! What's the supply deposit covering raw silicone bronze ingots?"),
            ("W", "Forty-eight dollars covers bronze billets plus silica sand—payable only at Bursar Desk Three inside South Commons, not inside the foundry room."),
        ], [
            _fb("SPELLING (ONE WORD ONLY): 'Participant surname spelling check: ___'", "Kapur", ["kapur"], "Ia mengonfirmasi satu huruf P: K-A-P-U-R.", 1),
            _mc("When does the mandatory molten safety orientation begin Saturday?", ["Promptly at 9:15 AM.", "Right at noon during pours.", "At 3:45 PM before cleanup.", "Immediately past 4:15 PM."], 0, "Pengarahan keselamatan dimulai tepat pukul 09.15."),
            _fb("TIME ONLY: 'Corrected final afternoon workshop wrap-up finish time: ___ PM'", "4:15", ["4:15", "4.15", "four-fifteen"], "Semula disebut 3:45 lalu diralat menjadi 4:15 karena bersih-bersih sore.", 1),
            _mc("Why are ordinary canvas sneakers prohibited inside the pouring room?", ["Synthetic or canvas fabric fails to shield feet safely against hot molten metal splashes.", "Canvas laces catch electric sander cords cleanly.", "Silica dust stains bright blue canvas permanently.", "Steel lockers lack shelves sized for loose sneakers."], 0, "Sepatu kulit tertutup wajib karena kanvas cepat meleleh jika terkena percikan logam cair."),
            _fb("ONE WORD ONLY: 'Heavy natural leg attire cleared safely for pouring: old ___ jeans.'", "denim", ["denim"], "Celana jins denim tua yang tidak robek diperbolehkan.", 1),
            _fb("NO MORE THAN TWO WORDS: 'Protective workshop apron color lent free by foundry: ___ leather'", "green chrome", ["green", "green chrome", "chrome"], "Celemek pelindung berwarna hijau kulit krom disediakan gratis.", 2),
            _fb("NUMBER ONLY ($): 'All-inclusive bronze ingot & silica supply fee: $___'", "48", ["48", "forty-eight"], "Biaya material tercatat $48.", 1),
            _fb("ONE WORD ONLY: 'Sand molding medium paired cleanly with raw bronze billets: ___ sand.'", "silica", ["silica"], "Material cetakan menggunakan pasir silika ('silica sand').", 1),
            _mc("Where must participants settle the forty-eight-dollar foundry deposit?", ["At Bursar Desk Three inside South Commons.", "Directly inside the hot furnace foundry bay.", "Online via mobile QR scanner Sunday.", "Inside Art Library Room 204."], 0, "Pembayaran hanya diterima di Bursar Desk Three di South Commons."),
            _fb("NO MORE THAN TWO WORDS: 'Protective footgear required instead of canvas: ___ boots'", "leather lace-up", ["leather", "leather boots", "leather lace-up"], "Harus mengenakan sepatu bot kulit bertali yang kokoh.", 2),
        ]),
        ("Regional Cross-Laminated Timber Archive Orientation", [
            ("W", "Welcome to the Structural Timber Demonstration Gallery! Every soaring wall beam here showcases regional Cross-Laminated Timber—engineered spruce boards stacked cleanly at perpendicular ninety-degree grain angles and pressed flat with benign formaldehyde-free polyurethane glue."),
            ("W", "Inspect sample cube Exhibit A right beside Reception: crossing perpendicular wood layers stops seasonal swelling shrinkages cleanly while boosting dimensional strength close to poured concrete at just one-fifth total crane weight!"),
            ("W", "Inside Exhibit B along North Hall, touch our charred black spruce beam corner from a standard two-hour furnace burn test: thick slow-charring outer timber insulates untouched cool structural inner core wood safely without snapping suddenly like exposed steel girders."),
            ("W", "Before exiting via the rooftop timber skywalk, stow loose umbrellas neatly beside umbrella rack C—dry rain drips mark porous acoustic spruce slabs visibly."),
        ], [
            _fb("NUMBER ONLY (degrees): 'Angle separating stacked spruce plank layers inside CLT boards: ___ degrees'", "90", ["90", "ninety"], "Lapisan kayu disusun saling tegak lurus 90 derajat.", 1, "IELTS-MOD-07"),
            _fb("ONE WORD ONLY: 'Type of wood species laminated inside these demonstration girders: ___'", "spruce", ["spruce"], "Kayu yang dipakai dalam contoh adalah pohon cemara spruce.", 1, "IELTS-MOD-07"),
            _mc("Approximately how heavy is Cross-Laminated Timber compared with equivalent-strength reinforced concrete?", ["Roughly one-fifth (20%) of concrete crane weight.", "Nearly three times heavier per beam meter.", "Identical tonnage per structural floor slab.", "Half heavy strictly above forty degrees."], 0, "Bobot sekitar seperlima (1/5) dari struktur beton setara."),
            _fb("ONE WORD ONLY: 'Harmful solvent chemical deliberately absent from modern structural adhesive: ___'", "formaldehyde", ["formaldehyde"], "Perekat poliuretan modern bebas dari emisi formaldehida.", 1, "IELTS-MOD-07"),
            _mc("What surprising fire-safety behavior demonstrates cleanly in Exhibit B's burned beam?", ["A steady insulating charcoal crust shields cool internal structural wood reliably across prolonged heat.", "Burnt spruce drips molten sticky flame straight onto steel beams below.", "Exposed structural girders buckle instantaneously before ninety seconds.", "White mineral paint explodes neatly under two-hour furnace tests."], 0, "Lapisan arang luar bertindak sebagai isolator menjaga inti kayu tetap kokoh."),
            _match("Match each Gallery landmark with what visitors see or do there:", [
                {"left": "Reception Exhibit A", "rightOptions": ["Compare weight vs concrete cube", "Inspect 2-hour charred fire beam", "Store wet umbrellas away safely"]},
                {"left": "North Hall Exhibit B", "rightOptions": ["Compare weight vs concrete cube", "Inspect 2-hour charred fire beam", "Store wet umbrellas away safely"]},
                {"left": "Exit Rack C", "rightOptions": ["Compare weight vs concrete cube", "Inspect 2-hour charred fire beam", "Store wet umbrellas away safely"]},
            ], "Reception Exhibit A::Compare weight vs concrete cube|North Hall Exhibit B::Inspect 2-hour charred fire beam|Exit Rack C::Store wet umbrellas away safely", "Pemetaan tiap pos pameran dan lokasi aturan payung."),
            _fb("NUMBER ONLY (hours): 'Standard furnace fire resistance burn length shown in Exhibit B: ___ hours'", "2", ["2", "two"], "Durasi uji bakar standar mencapai dua jam.", 1, "IELTS-MOD-07"),
            _mc("Why must wet umbrellas stay tucked outside before crossing acoustic spruce skywalks?", ["Stray water drops leave dark visible stains across porous untreated ceiling panels.", "Damp spokes snag crane anchor pins overhead.", "Indoor air heaters evaporate wet umbrellas into loud fog.", "Security metal scanners block aluminum handles."], 0, "Tetesan air hujan meninggalkan noda pada lempeng kayu akustik berpori."),
            _mc("What geometric property eliminates crooked seasonal wood warping inside engineered slabs?", ["Stacking alternating boards perpendicularly counteracts lengthwise shrinkage across grains.", "Soaking raw logs five weeks in oily lake silt.", "Wrapping green timber tightly in shiny tin foil.", "Freezing wet boards overnight below minus twenty."], 0, "Susunan serat silang 90° saling mengunci kembang-susut kayu."),
            _mc("What core engineering benefit drives skyscraper designers testing tall timber grids cleanly?", ["Lighter dead structural weight reduces foundation concrete demand dramatically.", "Trees grow overnight beside suburban steel yards cleanly.", "Charcoal beams absorb downtown noisy sirens automatically.", "Wooden floors never require electrical conduits."], 0, "Pengurangan bobot bangunan signifikan memperingan kebutuhan pondasi besar."),
        ]),
        ("Cover Crops & No-Till Soil Seminar Evaluation", [
            ("W", "Mark, before Dr. Reyes grades our agricultural field review tomorrow, double-check Section Four comparing winter crimson clover against bare autumn plowed wheat plots."),
            ("M", "Wait—look carefully at Plot Nine moisture! Didn't our first draft state cover crops sucked dry topsoil water away right before April corn planting?"),
            ("W", "Hold on—re-check Professor Alvarez's corrected appendix chart! Heavy spring rain bounced fast off bare crusted plots, whereas matted clover roots held spongy rainwater reserves twenty-two percent higher down at fifteen centimeters!"),
            ("M", "Good catch—I almost misread reversed bar legends on Plot Nine! Plus notice earthworm burrow counts tripled under no-till rye stubble compared with twice-harrowed control strips."),
            ("W", "Right! Let's replace that old rough draft paragraph swiftly and cite both moisture depth AND worm infiltration velocity tables explicitly."),
        ], [
            _multi("Choose TWO documented gains measured under matted cover-crop strips vs bare plowed soil:",
                   ["Subsurface soil moisture ran 22% higher at fifteen centimeters", "Bare plowed soil soaked rain three times faster", "Earthworm burrow density tripled inside undisturbed stubble", "Winter frost killed all planted corn shoots early"],
                   "Subsurface soil moisture ran 22% higher at fifteen centimeters|Earthworm burrow density tripled inside undisturbed stubble",
                   "Kelembapan kedalaman 15 cm meningkat 22% dan jumlah liang cacing tanah meningkat tiga kali lipat."),
            _mc("What embarrassing chart-reading slip did the woman catch inside Mark's initial draft?", ["Mark accidentally inverted legend colors, claiming clover dried soil instead of storing moisture.", "Plot Nine temperatures read strictly in Kelvin backwards.", "Crimson clover plots grew toxic highway weeds instead.", "Section Four omitted autumn wheat seed prices cleanly."], 0, "Mark salah baca legenda grafik sehingga membalik kesimpulan kelembapan tanah."),
            _fb("NUMBER ONLY (%): 'Moisture boost recorded at 15 cm beneath crimson clover mulch: ___%'", "22", ["22", "twenty-two"], "Simpanan kelembapan di kedalaman 15 cm lebih tinggi 22%.", 1),
            _fb("ONE WORD ONLY: 'Soil organism whose burrow numbers tripled under unplowed stubble: ___'", "earthworm", ["earthworm", "earthworms"], "Populasi liang cacing tanah ('earthworm') meningkat tiga kali lipat.", 1),
            _mc("What caused rainwater to run wasted off unplanted autumn bare soil?", ["Rain crusted smooth plowed bare earth so showers sheeted off instead of soaking deep.", "Deep thick earthworm pits flooded corn tractors immediately.", "Cold steel harrows cooled raindrops solid above dirt.", "Clover roots drank water away from neighboring barns."], 0, "Tanah gundul berkerak keras membuat air hujan meluncur sebagai limpasan permukaan."),
            _match("Match each test soil condition with its observed physical behavior:", [
                {"left": "Bare plowed crust", "rightOptions": ["Sheds spring rain fast offline", "Stores moisture 22% higher", "Triples earthworm vertical tunnels"]},
                {"left": "Matted clover roots", "rightOptions": ["Sheds spring rain fast offline", "Stores moisture 22% higher", "Triples earthworm vertical tunnels"]},
                {"left": "No-till rye stubble", "rightOptions": ["Sheds spring rain fast offline", "Stores moisture 22% higher", "Triples earthworm vertical tunnels"]},
            ], "Bare plowed crust::Sheds spring rain fast offline|Matted clover roots::Stores moisture 22% higher|No-till rye stubble::Triples earthworm vertical tunnels", "Rangkuman perbandingan perilaku tiga perlakuan tanah lapangan."),
            _mc("When will Dr. Reyes evaluate their finalized seminar field report?", ["Tomorrow.", "Late next semester.", "Strictly every Tuesday noon.", "Yesterday before lunch."], 0, "Penilaian laporan oleh Dr. Reyes dijadwalkan besok."),
            _fb("ONE WORD ONLY: 'Legume cover species tested alongside winter rye in Plot Nine: crimson ___'", "clover", ["clover"], "Jenis tanaman penutup tanah yang diuji adalah crimson clover.", 1),
            _mc("What paired metrics do the two students agree to highlight cleanly in revised Section Four?", ["Fifteen-centimeter moisture depth alongside worm-burrow water infiltration velocity.", "Spring diesel tractor oil receipts alongside seed retail marks.", "Historical clay pottery dates alongside autumn market inflation.", "Nitrogen aerosol clouds alongside dry salt desert dunes."], 0, "Mereka sepakat menonjolkan data kelembapan 15 cm dan kecepatan infiltrasi liang cacing."),
            _mc("How does the woman respond upon spotting Mark's chart-reading error?", ["Constructively fixing the draft paragraph together before grading tomorrow.", "Scolding Mark publicly inside seminar classrooms.", "Submitting both contradictory drafts unchanged simultaneously.", "Dropping Plot Nine entirely from agronomy reviews."], 0, "Respons kolaboratif langsung mengganti paragraf salah dengan kutipan data presisi."),
        ]),
        ("University Lecture — How Printing Woodblocks Transitioned Into Movable Metal Type", [
            ("M", "Many textbooks start printing history starkly inside 1450s Mainz, Germany—skipping centuries of ingenious East Asian woodblock, ceramic, and bronze cast printing breakthroughs that solved ink, paper, and alignment hurdles long beforehand."),
            ("M", "Hand-carving whole text pages backwards across pearwood panels proved brisk for crisp multi-thousand Buddhist scripture editions—yet changing one mispelled date meant slicing entire boards anew."),
            ("M", "Bi Sheng's eleventh-century Song baked-clay movable sorts cracked modularity first before thirteenth-century Korean royal artisans perfected durable sand-cast bronze type held steady over beeswax-pine-resin beds."),
            ("M", "Crucially, Gutenberg's genuine European breakthrough combined an adjustable precision hand-mold casting crisp uniform lead-tin-antimony alloy letters smoothly alongside oil-varnish viscous linseed ink—essential because watery Asian carbon inks beaded unevenly off slick metal rather than clinging crisp to damp rag paper."),
        ], [
            _mc("What core historical limitation restricts classic carved whole-page woodblock printing?", ["Correcting tiny single character typos required recarving entire wooden page panels.", "Pearwood grain absorbed watery ink too cleanly for legible scripture print.", "Paper pulp tore instantaneously inside Chinese wooden presses.", "Carvers could never print more than twelve copies per carved board."], 0, "Kesalahan satu aksara pada blok utuh memaksa pengukiran ulang papan baru."),
            _fb("ONE WORD ONLY: 'Hard fine-grained timber favored historically for crisp page woodblocks: ___ wood.'", "pearwood", ["pearwood", "pear"], "Papan ukir berbutir halus sering menggunakan kayu pir ('pearwood').", 1, "IELTS-MOD-07"),
            _mc("Which two pioneering materials preceded European lead letters in East Asian movable-type workshops?", ["Eleventh-century Chinese baked clay followed by thirteenth-century Korean cast bronze.", "Carved soft ocean chalk followed cleanly by bamboo bark strips.", "Hand-cut granite cubes followed smoothly by melted amber wax.", "Wrought iron wire hooks welded atop copper sheets."], 0, "Tanah liat bakar era Song (Bi Sheng abad ke-11) dan perunggu cor Korea abad ke-13."),
            _fb("NO MORE THAN TWO WORDS: 'Adhesive mix steadying uneven baked metal sorts inside Korean frames: beeswax and ___ resin'", "pine", ["pine", "pine resin"], "Alas campuran lilin lebah dan getah pinus menahan huruf logam agar rata.", 2, "IELTS-MOD-07"),
            _fb("ONE WORD ONLY: 'Base botanical pressing oil carrying Gutenberg's tacky viscous metal ink cleanly: ___ oil.'", "linseed", ["linseed"], "Tinta kental berbahan minyak biji rami ('linseed oil varnish') menempel merata di logam.", 1, "IELTS-MOD-07"),
            _mc("Why did traditional soot-and-water calligraphy ink fail cleanly on smooth cast-metal typefaces?", ["Watery ink pulled into patchy wet beads atop metal rather than spreading crisp film coats.", "Carbon water boiled violently against cold alloy bronze.", "Water inks rusted lead antimony punches dry in seconds.", "Damp rag paper rejected all black pigments automatically."], 0, "Tinta berbasis air menggumpal putus-putus di permukaan logam licin alih-alih melapisi tipis merata."),
            _fb("ONE WORD ONLY: 'Key mechanical invention enabling Gutenberg to cast thousands of identical uniform shafts rapidly: adjustable ___.'", "hand-mold", ["hand-mold", "mold"], "Cetakan tangan fleksibel ('adjustable hand-mold') memastikan ukuran batang huruf seragam.", 2, "IELTS-MOD-07"),
            _mc("What three metals joined in Gutenberg's crisp precision casting alloy?", ["Lead, tin, and antimony.", "Gold, pure silver, and iron.", "Zinc, bright copper, and nickel.", "Liquid mercury, bronze, and lime."], 0, "Paduan timah hitam (lead), timah putih (tin), dan antimon (antimony)."),
            _mc("Why did Korea and China historically retain carved woodblock halls even after inventing movable metal sorts?", ["Thousands of distinct characters made massive metal sorting trays unwieldy compared with fast compact alphabets.", "Korean artisans forgot completely how to heat clay foundry ovens.", "Lead-tin alloys were outlawed across eastern harbor cities.", "Woodblocks produced shiny photographic three-color murals."], 0, "Ribuan karakter non-alfabetis membuat penataan baki huruf terpisah lebih lambat dibanding papan ukir untuk naskah panjang."),
            _mc("How does the lecturer frame technological history throughout the talk?", ["As cumulative global problem-solving across cultures rather than a lone sudden European miracle.", "Purely as proof that metal alloys always eclipse wooden crafts cleanly.", "Dismissing clay movable fonts as complete medieval failures.", "Claiming ancient scrolls never spread cleanly beyond Mainz."], 0, "Menggarisbawahi evolusi kumulatif lintas peradaban Tiongkok, Korea, dan Eropa."),
        ]),
    ],
}


def build_ielts_listening_1_3(pack_num: int) -> list[dict]:
    return expand_ielts_listening(pack_num, _PARTS_1_3[pack_num])
