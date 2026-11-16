"""TOEFL iBT 2026 Reading items: Complete the Words (20/pack) & Read in Daily Life (15/pack) = 175 items."""

from pack_schema_helpers import make_q, rotated_mcq

# Complete the Words passages: 2 contexts x 10 stems per pack = 20 FILL_BLANK items per pack
_CW_DATA = {
    1: [
        (
            "CW1",
            "Complete the Words — Digital Soil Moisture Telemetry",
            "Precision irrigation engineers bury wireless tensiometers inside vegetable beds so farmers can moni___ [1] root moisture continuously. "
            "Instead of drenching entire clay fields uniformly every Tuesday, automated drip val___ [2] open only when local soil tension passes dry thresholds. "
            "Field measurements show this tar___ [3] scheduling slashes municipal groundwater draw by nearly a third while pre___ [4] deep nitrate leaching past sandy root zones. "
            "Compact solar cells mounted atop field posts re___ [5] battery packs smoothly even under misty spring clouds, and calibrated radios trans___ [6] hourly plots straight to greenhouse tablets. "
            "Critically, agricultural extension clinics warn novices against placing metal probes inside freshly disturbed trenches where loose air poc___ [7] distort resistance numbers; waiting forty-eight hours lets moist earth set___ [8] snug around ceramic sensor tips so winter readings remain con___ [9] across rocky slope mar___ [10].",
            [
                ("moni___ (track continuously)", "monitor", ["monitor"], "Verba dasar setelah modal 'can' -> 'monitor'."),
                ("val___ (mechanical flow gates)", "valves", ["valves"], "Nomina jamak cocok dengan verba jamak 'open' -> 'valves'."),
                ("tar___ (focused accurately on a spot)", "targeted", ["targeted"], "Adjektiva partisipial pasif menerangkan 'scheduling' -> 'targeted'."),
                ("pre___ (stopping from happening)", "preventing", ["preventing"], "Paralel setelah 'while' memerlukan gerund/participle '-ing' -> 'preventing'."),
                ("re___ (restore electrical power)", "recharge", ["recharge"], "Kata kerja dasar dengan subjek jamak 'solar cells' -> 'recharge'."),
                ("trans___ (send signals wirelessly)", "transmit", ["transmit"], "Verba jamak sesuai 'calibrated radios' -> 'transmit'."),
                ("poc___ (empty air spaces)", "pockets", ["pockets"], "Nomina jamak sesuai 'distort' (tanpa -s) -> 'pockets'."),
                ("set___ (sink gently down into place)", "settle", ["settle"], "Infinitif tanpa 'to' sesudah 'lets + object' -> 'settle'."),
                ("con___ (steady and uniform)", "consistent", ["consistent"], "Adjektiva sesudah linking verb 'remain' -> 'consistent'."),
                ("mar___ (outer edges of a slope)", "margins", ["margins"], "Nomina jamak 'margins' melengkapi frasa 'slope margins'."),
            ],
        ),
        (
            "CW2",
            "Complete the Words — Rare Book Archival Boxmaking",
            "Before brittle nineteenth-century atlases enter circulating university vaults, conservation binders encase each fragile vol___ [1] inside custom corrugated archival enclosures. "
            "Unbuffered acid-free rag board shields faded iron-gall bindings against abrupt indoor hu___ [2] swings whenever winter radiators blast dry corridor warmth. "
            "Apprentices score fold lines gently with smooth bone fol___ [3] rather than metal blades because steel burrs can tear soft archival board fib___ [4] cleanly apart. "
            "Rather than brushing wet adhesive anywhere near historic marbled endpapers, clever origami tuck flaps lock box walls tog___ [5] cleanly through friction slots alone. "
            "Each finished slipcase then rec___ [6] an external cotton ribbon tie plus a crisp acid-neutral spine label listing call numbers and last inspect___ [7] dates. "
            "Should any patron ever spot flaking gold leaf or detached hinges while consulting an old atlas, desk clerks gently relo___ [8] the enclosure onto red-tagged conservation carts so trained technicians can sta___ [9] loose sewing cords immediately before micro-tears widen dur___ [10] spring seminar crowds.",
            [
                ("vol___ (bound book or tome)", "volume", ["volume"], "Setelah determiner tunggal 'each fragile' dibutuhkan nomina tunggal -> 'volume'."),
                ("hu___ (air moisture level)", "humidity", ["humidity"], "Nomina tak terhitung sesuai konteks iklim penyimpanan -> 'humidity'."),
                ("fol___ (bookbinding smoothing tools)", "folders", ["folders"], "Istilah alat lipat penjilidan jamak -> 'bone folders'."),
                ("fib___ (tiny material threads)", "fibers", ["fibers", "fibres"], "Nomina jamak merujuk serat papan arsip -> 'fibers'/'fibres'."),
                ("tog___ (joined side by side)", "together", ["together"], "Adverbia pelengkap frasa kerja 'lock ... together' -> 'together'."),
                ("rec___ (is given/marked with)", "receives", ["receives"], "Verba tunggal sesuai subjek 'Each finished slipcase' -> 'receives'."),
                ("inspect___ (formal checkup event)", "inspection", ["inspection"], "Nomina pembentuk kata majemuk 'inspection dates' -> 'inspection'."),
                ("relo___ (move to another cart/spot)", "relocate", ["relocate"], "Verba jamak sesuai 'desk clerks' -> 'relocate'."),
                ("sta___ (make steady/secure)", "stabilize", ["stabilize", "stabilise"], "Infinitif dasar sesudah modal 'can' -> 'stabilize'."),
                ("dur___ (across a period of time)", "during", ["during"], "Preposisi waktu sebelum frasa nomina 'spring seminar crowds' -> 'during'."),
            ],
        ),
    ],
    2: [
        (
            "CW1",
            "Complete the Words — Campus Zero-Waste Dining Shift",
            "Starting September first, campus dining halls are phasing out single-use laminated soup cups across every daytime café counter. "
            "Students grabbing quick noon take___ [1] simply scan their digital meal pass to borrow durable stainless-steel bowls holding tight sili___ [2] lids. "
            "After finishing lunch anywhere across main campus plazas, diners drop rinsed containers into bright aqua return kio___ [3] stationed beside dormitory lobbies. "
            "Smart optical scanners inside each chute auto___ [4] credit your dining account within thirty seconds—eliminating bulky deposit refunds at busy cash reg___ [5]. "
            "Commercial dishwashers sterilize returned bowls at eighty-two degrees Celsius before stacking cleaned batches onto insulated cafeteria ser___ [6] racks. "
            "During pilot weeks across the science courtyard last spring, student volunteers rescued four thousand cardboard wrappers from dump___ [7] every single Monday through Friday. "
            "Only allergy-certified medical dining stations retain pre-sealed biodegradable tray packs, and sustainability coordinators invite residence teams to sub___ [8] crisp feedback notes whenever drop bins near remote athletic fiel___ [9] approach full capac___ [10].",
            [
                ("take___ (food carried out)", "takeout", ["takeout"], "Kata benda jamak/tak terhitung untuk makanan bawa pulang -> 'takeout'."),
                ("sili___ (flexible heat-safe rubber)", "silicone", ["silicone"], "Bahan penutup makanan fleksibel -> 'silicone'."),
                ("kio___ (self-service drop booths)", "kiosks", ["kiosks"], "Nomina jamak sesuai 'bright aqua return...' -> 'kiosks'."),
                ("auto___ (without manual typing)", "automatically", ["automatically"], "Adverbia memodifikasi kata kerja 'credit' -> 'automatically'."),
                ("reg___ (checkout payment machines)", "registers", ["registers"], "Kolokasi 'cash registers' -> 'registers'."),
                ("ser___ (food line stations)", "serving", ["serving"], "Partisipel modifier untuk 'racks' -> 'serving'."),
                ("dump___ (large outdoor trash bins)", "dumpsters", ["dumpsters"], "Nomina jamak tempat sampah besar -> 'dumpsters'."),
                ("sub___ (turn in feedback)", "submit", ["submit"], "Infinitif setelah 'invite ... to' -> 'submit'."),
                ("fiel___ (outdoor sports grounds)", "fields", ["fields"], "Frasa jamak 'athletic fields' -> 'fields'."),
                ("capac___ (maximum volume limit)", "capacity", ["capacity"], "Kolokasi baku 'full capacity' -> 'capacity'."),
            ],
        ),
        (
            "CW2",
            "Complete the Words — Restoring Luminous Paper Globes",
            "Historic classroom globes manufactured between 1870 and 1930 were not sculpted out of hollow modern plastics; artisans carefully pasted printed tapered paper gores over hollow spheres built from molded papier-mâché and crushed plaster. "
            "Over decades of eager student fingers spinning continents, greasy fingerprints darkened southern coastlines and acidic coal smoke yel___ [1] glossy shellac varnish coats. "
            "Before attempting any delicate wet cleaning step, map conservators thoroughly photo___ [2] faded imperial border lines under crisp oblique lighting. "
            "Soft natural-rubber dry erasers then lift loose attic soot gently without dis___ [3] sensitive water-soluble scarlet shipping lanes printed by hand watercolorists. "
            "Where careless storage cracked plaster shells along equatorial seams, specialists inject warm rice-starch adhesive mixed with tiny wood fibers to re___ [4] structural stiffness cleanly. "
            "Missing tiny islands rarely receive wholesale guesswork repainting; conservative ethics dictate matching neutral base tone closely so modern infill rem___ [5] visibly distinct upon close scholarly scru___ [6]. "
            "Once mounted quietly atop balanced brass meridian rings inside low-UV gallery vitr___ [7], restored globes guide campus geography seminars neatly through shifting carto___ [8] borders, maritime steam routes, and nineteenth-century ocean winds without risking physical fra___ [9] ever ag___ [10].",
            [
                ("yel___ (turned a dull amber tint)", "yellowed", ["yellowed"], "Kata kerja lampau paralel dengan 'darkened' -> 'yellowed'."),
                ("photo___ (capture lens records)", "photograph", ["photograph"], "Verba jamak sesuai subjek 'conservators' -> 'photograph'."),
                ("dis___ (melting pigments away)", "dissolving", ["dissolving"], "Gerund setelah preposisi 'without' -> 'dissolving'."),
                ("re___ (bring back strength)", "restore", ["restore"], "Infinitif setelah 'to' -> 'restore'."),
                ("rem___ (stays clearly visible)", "remains", ["remains"], "Verba tunggal sesuai 'modern infill' -> 'remains'."),
                ("scru___ (careful close inspection)", "scrutiny", ["scrutiny"], "Nomina kolokasi 'scholarly scrutiny' -> 'scrutiny'."),
                ("vitr___ (glass museum display cases)", "vitrines", ["vitrines"], "Nomina jamak kotak kaca museum -> 'vitrines'."),
                ("carto___ (relating to mapmaking)", "cartographic", ["cartographic"], "Adjektiva menerangkan 'borders' -> 'cartographic'."),
                ("fra___ (breaking into pieces)", "fracture", ["fracture"], "Nomina risiko retakan fisik -> 'fracture'."),
                ("ag___ (one more time)", "again", ["again"], "Adverbia penutup idiom 'ever again' -> 'again'."),
            ],
        ),
    ],
    3: [
        (
            "CW1",
            "Complete the Words — Nocturnal Pollinator Meadow Trials",
            "Daytime botanical studies long celebrated honeybees and bright bumblebees, inadvertently overshadowing unseen midnight visits by wild hawkmoths and tiny nocturnal beetles. "
            "Biologists installing weatherproof infrared time-lapse cameras across damp limestone meadows recently tracked astonishing nocturnal polli___ [1] networks operating between eleven PM and dawn. "
            "Unlike showy red blossoms tuned visually for sunny hummingbirds, pale night-blooming campion flowers release crisp sweet perf___ [2] waves precisely after twilight drops ambient temperatures. "
            "Hovering swiftly without touching sticky petals, long-tongued moths transfer sticky pollen grains across hillside ridges even when persistent coastal drizzle pins diurnal bees inside hives. "
            "Worryingly, researchers pairing illuminated highway streetlights beside half the experimental plots watched nocturnal fruit-set drop by nearly sixty percent compared with dark control mea___ [3]. "
            "Harsh blue-white glare disorients foraging moths upward away from blossoms toward fatal bulb spires—highlighting why rural townships switching toward warm shielded amber road lamps pro___ [4] unexpected harvest resilience across neighboring berry orch___ [5], clover seed fields, and wild meadow flora al___ [6]. "
            "Students analyzing hundreds of infrared frames must label each flight approach accurately inside open bio___ [7] datasets so global conservationists can model dark-sky buffer widths around vulnerable crop corr___ [8], while civic planners balance safe rural roadway visi___ [9] against fragile insect ecosy___ [10].",
            [
                ("polli___ (pollen transfer networks)", "pollination", ["pollination"], "Nomina pembentuk frasa 'pollination networks' -> 'pollination'."),
                ("perf___ (fragrant floral scents)", "perfume", ["perfume"], "Nomina penghasil frasa 'perfume waves' -> 'perfume'."),
                ("mea___ (grassy open fields)", "meadows", ["meadows"], "Nomina jamak sesuai 'dark control meadows' -> 'meadows'."),
                ("pro___ (supply/deliver cleanly)", "provide", ["provide", "promote"], "Verba jamak sesuai 'townships' -> 'provide'."),
                ("orch___ (fruit tree groves)", "orchards", ["orchards"], "Nomina jamak 'berry orchards' -> 'orchards'."),
                ("al___ (equally as well)", "alike", ["alike"], "Adverbia penutup daftar 'A, B, and C alike' -> 'alike'."),
                ("bio___ (living diversity databases)", "biodiversity", ["biodiversity"], "Nomina gabungan 'biodiversity datasets' -> 'biodiversity'."),
                ("corr___ (connected habitat strips)", "corridors", ["corridors"], "Nomina jamak 'crop corridors' -> 'corridors'."),
                ("visi___ (clear sight distance)", "visibility", ["visibility"], "Nomina keselamatan jalan -> 'visibility'."),
                ("ecosy___ (ecological communities)", "ecosystems", ["ecosystems"], "Nomina jamak sesuai konteks ekologi -> 'ecosystems'."),
            ],
        ),
        (
            "CW2",
            "Complete the Words — Subsea Fiber-Optic Seismic Listeners",
            "More than a million kilometers of slender glass cables currently thread across abyssal seabed plains, linking continental data centers with low-latency internet signals. "
            "Ocean geophysicists discovered an ingenious way to repurpose dark spare fiber strands lying quietly inside active coastal trunks without sinking costly marine seismometers from research vessels. "
            "By firing rapid laser pulses into shoreward cable terminals and listening to faint optical back-scatter ripples caused when submarine earth tremors stretch buried glass microseconds wide, scientists convert existing cables into dense chains of thousand-meter acoustic det___ [1]. "
            "This distributed acoustic sensing (DAS) cleanly captures passing fin-whale low calls, subtle undersea fault slips near offshore trenches, and approaching storm swells weeks before coastal gauges peak. "
            "Naturally, shore station hardware must isolate active commercial internet wavelengths safely inside armored repeaters so experimental seismic pulses never dis___ [2] civilian banking or university network packets. "
            "Initial offshore trials off central Oregon pinpointed unmapped buried fault fractures within mere meters, showing how repurposed telecom infrastructure delivers precious early warning data while slashing maritime vessel fuel exp___ [3] sharply during stormy Pacific win___ [4]. "
            "Graduate geoscience interns now process clean waterfall spectrograms daily inside quiet campus server rooms, writing filter routines that separate distant ferry thrusters neatly from genuine tectonic shock___ [5], slow subsea mudslides, and gentle thermal vents shimmering along volcanic mid-ocean rid___ [6] without ever dropping anchor cables across fragile deep coral gar___ [7]—proving scientific creativity thrives on collabo___ [8] partnerships uniting private ocean tele___ [9] operators with public climate resea___ [10].",
            [
                ("det___ (measuring sensors)", "detectors", ["detectors"], "Nomina jamak sesuai 'chains of ... acoustic detectors' -> 'detectors'."),
                ("dis___ (interrupt or interfere)", "disrupt", ["disrupt"], "Verba dasar setelah modal 'never' -> 'disrupt'."),
                ("exp___ (financial costs spent)", "expenses", ["expenses"], "Nomina jamak biaya bahan bakar kapal -> 'expenses'."),
                ("win___ (cold stormy seasons)", "winters", ["winters"], "Nomina jamak paralel dengan 'stormy Pacific winters' -> 'winters'."),
                ("shock___ (sharp earthquake waves)", "shockwaves", ["shockwaves"], "Nomina jamak gelombang kejut seismik -> 'shockwaves'."),
                ("rid___ (undersea mountain chains)", "ridges", ["ridges"], "Nomina jamak 'mid-ocean ridges' -> 'ridges'."),
                ("gar___ (living coral clusters)", "gardens", ["gardens"], "Kolokasi ekologi laut dalam 'deep coral gardens' -> 'gardens'."),
                ("collabo___ (cooperative shared)", "collaborative", ["collaborative"], "Adjektiva menerangkan 'partnerships' -> 'collaborative'."),
                ("tele___ (communications networks)", "telecom", ["telecom", "telecommunications"], "Singkatan baku industri kabel bawah laut -> 'telecom'."),
                ("resea___ (academic investigators)", "researchers", ["researchers"], "Nomina jamak mengacu peneliti publik -> 'researchers'."),
            ],
        ),
    ],
    4: [
        (
            "CW1",
            "Complete the Words — Dryland Fog-Harvesting Mesh Towers",
            "Along rain-parched Atacama and Namib coastal plateaus where rain gauges register barely ten millimeters annually, dense marine stratocumulus fog drifts reliably uphill off chilly ocean currents every pre-dawn morning. "
            "Lacking freshwater creeks or shallow wells, hillside agricultural cooperatives stretch fine UV-stabilized polypropylene Raschel nets vertically facing seaward winds. "
            "As microscopic windborne mist droplets sweep silently across braided green mesh filaments, droplets coalesce until gravity drags fresh water steadily downward into clean covered PVC gutters feeding drip-irrigated olive orchards. "
            "Engineers testing improved three-dimensional spacer weaves noticed twin benefits: coarse vertical harps shred heavy gale turbulence gently, boosting collection yields nearly forty percent over older flat sheets without clogging up with fine valley dust storms. "
            "Crucially, community bylaws assign rotating maintenance shifts evenly across participating households so torn grommets are mended before intense sun degrades polymer fibers, algae films get flushed out weekly from settling cisterns, and clean gravity pipes keep scarce orchard rations flowing equit___ [1] during peak summer heat. "
            "Wait—put blanks right inside text smoothly across 10 positions! Look cleanly at how the first blank appeared late: let's supply all 10 balanced blanks clearly throughout CW1 pack 4 below!",
            [],  # Replaced cleanly immediately in _CW4_1 below!
        ),
        (
            "CW2",
            "Complete the Words — Placeholder",
            "Placeholder",
            [],
        ),
    ],
    5: [
        ("CW1", "Placeholder", "Placeholder", []),
        ("CW2", "Placeholder", "Placeholder", []),
    ],
}

# Clean full definitions for Pack 4 and Pack 5 Complete the Words:
_CW_DATA[4] = [
    (
        "CW1",
        "Complete the Words — Dryland Fog-Harvesting Mesh Towers",
        "Along rain-parched coastal plateaus where rain gauges measure barely ten millimeters annually, dense marine fog dri___ [1] reliably uphill off chilly ocean currents every morning. "
        "Lacking flowing rivers, hillside villages stretch fine polypropylene mesh screens ver___ [2] across windy ridge crests. "
        "As microscopic mist drops pass through braided plastic fil___ [3], tiny droplets merge until gravity pulls fresh clear water into cov___ [4] storage gutters feeding hillside olive orchards. "
        "Field trials comparing novel 3D harp weaves against flat older sheets revealed forty-percent higher capture yields alongside lower tear ris___ [5] during severe mountain gusts. "
        "Community cooperatives organize rotating weekly shifts to flush settling tanks, tighten anchor cab___ [6], and mend tiny sun-worn seams promptly before summer dust clo___ [7] mesh pores. "
        "Independent water testing confirms atmospheric fog condensate tastes markedly fresher than brackish shallow bore wells, cutting expensive bottled water deliv___ [8] for rural schoolhouses. "
        "Hydrologists caution against assuming fog nets alone satisfy heavy urban industry, yet as localized household supple___ [9] in arid coastal valleys they offer durable low-energy drought resi___ [10].",
        [
            ("dri___ (moves gently in currents)", "drifts", ["drifts"], "Verba tunggal sesuai 'dense marine fog' -> 'drifts'."),
            ("ver___ (upright from ground up)", "vertically", ["vertically"], "Adverbia memodifikasi kata kerja 'stretch' -> 'vertically'."),
            ("fil___ (thin thread strands)", "filaments", ["filaments"], "Nomina jamak serat jaring penangkap kabut -> 'filaments'."),
            ("cov___ (shielded from evaporation)", "covered", ["covered"], "Adjektiva partisipial pasif menerangkan 'storage gutters' -> 'covered'."),
            ("ris___ (chances of breakage)", "risks", ["risks"], "Nomina jamak sejajar 'higher yields' -> 'risks'."),
            ("cab___ (supporting metal wire lines)", "cables", ["cables"], "Nomina jamak kawat penahan tiang -> 'cables'."),
            ("clo___ (blocks small openings)", "clogs", ["clogs"], "Verba tunggal sesuai 'summer dust' -> 'clogs'."),
            ("deliv___ (paid transport shipments)", "deliveries", ["deliveries"], "Nomina jamak kiriman truk air -> 'deliveries'."),
            ("supple___ (helpful added supply)", "supplements", ["supplements"], "Nomina jamak pelengkap pasokan air -> 'supplements'."),
            ("resi___ (ability to endure drought)", "resilience", ["resilience"], "Nomina abstrak ketahanan kekeringan -> 'resilience'."),
        ],
    ),
    (
        "CW2",
        "Complete the Words — Bio-Based Acoustic Ceiling Foam",
        "Traditional open-cell polyurethane wall panels deaden noisy office chatter well, yet rely entirely on petroleum feedstocks that crumble into persistent microplastic dust after renovation. "
        "Materials chemists at coastal agricultural labs have started growing pale featherlight acoustic panels directly from crop husk waste bound safely together with fungal mycelium networks. "
        "After sterilizing discarded oat hulls inside aluminum molds, technicians inoculate the damp mix with benign oyster-mushroom rootlets that weave microscopic hollow tubes within five quiet dark days. "
        "Gently oven-drying finished bricks stops live fungal growth completely, leaving porous fibrous interiors remarkably skilled at scat___ [1] harsh mid-range vocal echoes across busy university study commons. "
        "Independent lab burns further showed natural mineral ash crusts retard flame spread smoothly without relying on brominated chemical addi___ [2]. "
        "Acoustic testers measuring standard reverberation decay across identical small conference rooms found mushroom-hull tiles matched commercial fiberglass absorption cur___ [3] closely between five hundred and four thousand hertz. "
        "Equally encouraging for campus sustainability officers, obsolete boards removed during hallway upgrades can simply be chip___ [4] and composted safely onto landscaped shrub beds instead of crowding sealed municipal land___ [5]. "
        "Pilot installations inside two library group booths drawn up last semester sparked virtually zero chemical off-gassing odors, keeping allergic student readers comfortable even when summer humidity clim___ [6] steeply. "
        "Engineers currently focus on trimming mold-press drying elec___ [7] and applying translucent plant-wax coatings around vulnerable lower corners so passing backpack zip___ [8] never fray delicate mycelium faces dur___ [9] hurried semester fin___ [10].",
        [
            ("scat___ (dispersing sound waves)", "scattering", ["scattering"], "Gerund setelah preposisi 'at' -> 'scattering'."),
            ("addi___ (chemical supplements added)", "additives", ["additives"], "Nomina jamak bahan kimia tambahan -> 'additives'."),
            ("cur___ (plotted measurement lines)", "curves", ["curves"], "Nomina jamak kurva daya serap frekuensi -> 'curves'."),
            ("chip___ (shredded into small pieces)", "chipped", ["chipped"], "Past participle pasif setelah 'be' -> 'chipped'."),
            ("land___ (waste burial sites)", "landfills", ["landfills"], "Nomina jamak tempat pembuangan akhir -> 'landfills'."),
            ("clim___ (rises upward steeply)", "climbs", ["climbs"], "Verba tunggal sesuai 'summer humidity' -> 'climbs'."),
            ("elec___ (power energy needed)", "electricity", ["electricity"], "Nomina daya listrik pengeringan -> 'electricity'."),
            ("zip___ (metal backpack fasteners)", "zippers", ["zippers"], "Nomina jamak ritsleting tas mahasiswa -> 'zippers'."),
            ("dur___ (across a time period)", "during", ["during"], "Preposisi waktu ujian akhir -> 'during'."),
            ("fin___ (end-of-term exam period)", "finals", ["finals"], "Nomina majemuk 'semester finals' -> 'finals'."),
        ],
    ),
]

_CW_DATA[5] = [
    (
        "CW1",
        "Complete the Words — Urban Nighttime Thermal Satellite Maps",
        "Conventional weather forecasts issue one broad daytime temperature reading calibrated neatly inside breezy shaded airport meadows, overlooking sweltering nighttime brickwork traps inside crowded downtown wards. "
        "Environmental geographers now fuse high-resolution thermal infrared satellite passes with street-level bicycle thermometer loops to build fine block-by-block cooling priority maps. "
        "Crucially, infrared imagery flags neighborhoods where dark gravel roofs and dense truck corridors keep bedrooms hovering four degrees warmer well past mid___ [1], raising overnight heat-stress risks sharply for elderly tenants lacking air-condi___ [2] units. "
        "City arborists compare those exact glowing thermal pockets straight against legacy municipal tree censuses, exposing historic zoning gaps where narrow concrete sidewalks starved mature saplings of unpaved root vol___ [3]. "
        "Equipped with verifiable block metrics rather than anecdotal complaints, public-works crews dig wider interconnected soil trenches, plant drought-hardy shade elm clo___ [4], and spray light-reflecting mineral sealants across municipal parking bays. "
        "Follow-up thermal flights captured two summers later confirmed sunlit roof peaks cooled by nearly seven degrees, demonstrating how transparent earth-observation telemetry accelerates tangible neighborhood climate eq___ [5]. "
        "Local secondary science teachers also integrate simplified ward heat maps directly into tenth-grade geometry projects so students walk safe shaded blocks measuring real asphalt al___ [6], calculating surface canopy percentages cleanly, and drafting civic garden peti___ [7] backed by crisp quantitative charts rather than vague opi___ [8] during annual town-hall bud___ [9] hearings each Oct___ [10].",
        [
            ("mid___ (twelve o'clock at night)", "midnight", ["midnight"], "Nomina waktu malam tepat -> 'midnight'."),
            ("air-condi___ (cooling room machines)", "air-conditioning", ["air-conditioning"], "Kata majemuk pengondisi udara -> 'air-conditioning'."),
            ("vol___ (cubic soil space)", "volume", ["volume"], "Kolokasi ilmu kehutanan kota 'soil root volume' -> 'volume'."),
            ("clo___ (tightly spaced groups)", "clusters", ["clusters"], "Nomina jamak rumpun pohon peneduh -> 'clusters'."),
            ("eq___ (fair equal distribution)", "equity", ["equity"], "Istilah tata kota 'climate equity' -> 'equity'."),
            ("al___ (surface solar reflectivity)", "albedo", ["albedo"], "Istilah fisika iklim pantulan cahaya -> 'albedo'."),
            ("peti___ (formal citizen requests)", "petitions", ["petitions"], "Nomina jamak usulan warga -> 'petitions'."),
            ("opi___ (subjective unproven views)", "opinions", ["opinions"], "Nomina jamak kontras data kuantitatif -> 'opinions'."),
            ("bud___ (city financial spending plans)", "budget", ["budget"], "Nomina gabungan 'budget hearings' -> 'budget'."),
            ("Oct___ (tenth calendar month)", "October", ["October"], "Nama bulan tepat -> 'October'."),
        ],
    ),
    (
        "CW2",
        "Complete the Words — Bilingual Botanical Herbarium Digitization",
        "Tucked inside oak cabinets across regional museums sit millions of fragile flattened plant sheets collected over two centuries—often labeled strictly in faded cursive Latin alongside half-remembered local regional names. "
        "Natural-history archivists partnering with linguistics students recently launched massive open-access imaging lines capturing high-pixel scans plus spoken community interviews beside preserved specimens. "
        "Whenever skilled botanical volunteers photograph dried mountain fern stems above tiny color calibration scales, optical software extracts stamped collection years and latitude digits into clean searchable spread___ [1]. "
        "Even more vital, bilingual language keepers verify indigenous upland folk names describing seasonal flowering windows, edible tuber prep___ [2], and natural textile dye recipes missing completely from brief nineteenth-century collectors' field tags. "
        "Ecologists tracking subtle global climate shifts rely heavily on timestamped dried herbarium blossoms to prove whether certain alpine wildflowers now open buds nearly two weeks ear___ [3] than specimens pressed back in 1895. "
        "Because fragile paper sheets can stay quietly inside cold low-humidity vaults once crisp digital scans live online, curators spare priceless specimens endless rough finger-handling while opening rich heritage files freely to rural highland school___ [4] and overseas taxonomists ali___ [5]. "
        "Students transcribing difficult nineteenth-century penmanship consult shared cursive guides so confusing archaic town spel___ [6] link reliably back to modern topographic coor___ [7], eliminating duplicate county counts cleanly before peer reviewers ver___ [8] public biogeography atl___ [9] each Dece___ [10].",
        [
            ("spread___ (tabular data files)", "spreadsheets", ["spreadsheets"], "Nomina jamak file tabel terstruktur -> 'spreadsheets'."),
            ("prep___ (food processing methods)", "preparation", ["preparation"], "Nomina cara pengolahan umbi -> 'preparation'."),
            ("ear___ (sooner in the spring)", "earlier", ["earlier"], "Komparatif sebelum 'than' -> 'earlier'."),
            ("school___ (youth pupils)", "schoolchildren", ["schoolchildren", "schoolrooms", "schools"], "Nomina subjek pembaca sekolah pedesaan -> 'schoolchildren'."),
            ("ali___ (both groups equally)", "alike", ["alike"], "Adverbia baku 'A and B alike' -> 'alike'."),
            ("spel___ (word letterings)", "spellings", ["spellings"], "Nomina jamak variasi ejaan kota tua -> 'spellings'."),
            ("coor___ (latitude/longitude numbers)", "coordinates", ["coordinates"], "Nomina jamak titik lokasi peta -> 'coordinates'."),
            ("ver___ (check accuracy of)", "verify", ["verify"], "Verba jamak sesuai 'peer reviewers' -> 'verify'."),
            ("atl___ (bound geographic books/maps)", "atlases", ["atlases"], "Nomina jamak buku peta sebaran -> 'atlases'."),
            ("Dece___ (twelfth calendar month)", "December", ["December"], "Nama bulan tepat -> 'December'."),
        ],
    ),
]

# Read in Daily Life: 5 concise authentic functional texts x 3 Qs = 15 items per pack
_DAILY_LIFE = {
    1: [
        ("DL1", "Campus Notice: Makerspace Laser Cutter Access",
         "To: Engineering & Design Students | Subject: Makerspace Room 104 Laser Access\n"
         "Technicians installed upgraded exhaust filters in Room 104 over spring break. Starting Monday, March 9, students may reserve 45-minute laser cutting slots online only after completing the updated 10-minute ventilation safety quiz. Last semester's badges remain active for 3D printers, while laser access pauses automatically until the quiz clears on your portal. Drop-in Q&A runs Tuesday and Thursday at 1:00 PM.",
         [
             ("What must a student with an active fall semester badge complete before booking laser time?", ["Complete the new 10-minute ventilation safety quiz online.", "Attend a mandatory two-hour Tuesday 3D printer briefing.", "Renew payment for spring lab materials at the dean's office.", "Bring spare replacement exhaust filters to Room 104."], 0, "IBT-MOD-01", "B1", "Akses pemotong laser tertahan hingga kuis ventilasi 10 menit terselesaikan."),
             ("Which workshop machine remains unaffected for students who hold last semester's badges?", ["3D printers.", "Industrial metal lathes.", "Exhaust duct fans.", "Laser cutters."], 0, "IBT-MOD-01", "B1", "Pengumuman menyatakan 'Previous/Last semester's badges remain active for 3D printers'."),
             ("When can students drop in for live questions about the new filter rules?", ["Tuesday and Thursday afternoons at 1:00 PM.", "Monday mornings right after 8:00 AM.", "Friday evenings during spring break.", "Any weekend hour once filters run."], 0, "IBT-MOD-01", "B1", "Sesi drop-in berlangsung Selasa dan Kamis pukul 13.00."),
         ]),
        ("DL2", "Campus Email: Shuttle Loop Detour Advisory",
         "From: Campus Transit Operations | Re: Route B Blue Line Detour (Nov 18–20)\n"
         "Due to underground chilled-water pipe welding beside East Stadium, Route B shuttles will skip the East Stadium Gate stop from 7:00 AM Monday through 6:00 PM Wednesday. Riders heading to Kinesiology Hall should alight one stop earlier at Botanical Arch or transfer free onto electric micro-shuttle Gold-2 (running every 8 minutes from West Commons). Route A Orange Line runs on normal timetable.",
         [
             ("Why will Route B skip East Stadium Gate through Wednesday evening?", ["Construction crews are welding chilled-water pipes near East Stadium.", "Athletic tournaments closed West Commons parking garages.", "Electric micro-shuttle batteries overheated on Monday morning.", "Route A Orange Line took over all campus loops permanently."], 0, "IBT-MOD-01", "B1", "Pekerjaan pengelasan pipa air dingin menutup perhentian East Stadium sementara."),
             ("Which alternative shuttle transfers riders free from West Commons every eight minutes?", ["Electric micro-shuttle Gold-2.", "Route A Orange Line.", "Express midnight library van.", "City Municipal Bus 404."], 0, "IBT-MOD-01", "B1", "Email menyebut micro-shuttle Gold-2 berangkat tiap 8 menit dari West Commons."),
             ("At what day and time does normal East Stadium Gate boarding resume?", ["After 6:00 PM Wednesday, Nov 20 (or Thursday morning).", "At noon Monday, Nov 18.", "Only during Thanksgiving holiday weeks.", "Immediately after 7:00 AM Tuesday."], 0, "IBT-MOD-01", "B2", "Pengalihan berakhir Rabu pukul 18.00."),
         ]),
        ("DL3", "Receipt & Policy Summary: Campus Co-op Textbook Return",
         "University Book Co-op Receipt #88412 | Sold: Sep 2 | Item: Applied Hydrology 3rd Ed (Shrink-Wrapped New)\n"
         "Return Policy: Unopened texts in original intact shrink wrap receive full refunds through Sep 16 with original paper or app receipt. Opened loose-leaf packets and scratched access-code cards are strictly non-returnable unless defective from the publisher. Buyback trade-in window opens Dec 8–18 at 40% list credit.",
         [
             ("Under what condition does this hydrology textbook qualify for a 100% cash/card refund?", ["The plastic shrink wrap stays unbroken and it returns with receipt by Sept 16.", "Students solve only Chapters 1–3 cleanly without ink stains.", "The professor cancels the course during December finals.", "Access codes are activated online before Sept 16."], 0, "IBT-MOD-01", "B1", "Pengembalian penuh berlaku hingga 16 Sept jika segel plastik utuh + struk asli."),
             ("Which purchase format is explicitly excluded from regular student returns once opened?", ["Loose-leaf unbound packets and scratched digital access codes.", "Hardcover reference atlases bought on Dec 8.", "Unopened physics lab safety goggles.", "Discounted used stationery binders."], 0, "IBT-MOD-01", "B2", "Paket lepas atau kartu kode akses tergores tidak dapat dikembalikan."),
             ("When does the end-of-semester buyback period run?", ["Between December 8 and December 18.", "Strictly prior to September 16.", "On alternate Fridays throughout October.", "Forty days after publisher receipt."], 0, "IBT-MOD-01", "B1", "Jendela buyback dibuka 8–18 Desember dengan nilai kredit 40%."),
         ]),
        ("DL4", "Dormitory Utility Alert: Cold Water Brief Shutoff",
         "Residence Facilities Bulletin | North & Oak Halls\n"
         "Crews will test main backflow valves in North and Oak Halls this Thursday, Oct 14, between 1:30 PM and 3:30 PM. Cold water taps and third-floor laundry washers will pause during that two-hour maintenance window; toilets will remain flushable using reserve cisterns only once. South Wing and dining commons remain unaffected. Flush faucets thirty seconds once pressure returns if air sputters.",
         [
             ("Which buildings lose cold tap pressure for two hours on Thursday afternoon?", ["North Hall and Oak Hall.", "South Wing and Campus Dining Commons.", "All off-campus graduate apartments.", "Main Library basement restrooms only."], 0, "IBT-MOD-01", "B1", "Pengumuman menargetkan North Hall dan Oak Hall pukul 13.30–15.30."),
             ("What should residents expect immediately after valves reopen at 3:30 PM?", ["Possible trapped air sputtering briefly, cleared by running taps 30 seconds.", "Hot laundry water doubling normal temperature permanently.", "Third-floor washers automatically restarting unfinished cycles.", "Clean water remaining unavailable until Friday afternoon."], 0, "IBT-MOD-01", "B2", "Penduduk diminta membuka keran 30 detik jika air mendesis akibat udara terjebak."),
             ("How long will maintenance interrupt laundry washer operation?", ["Two hours (1:30 PM to 3:30 PM).", "Thirty seconds total.", "Forty-eight continuous hours.", "Thursday morning before noon."], 0, "IBT-MOD-01", "B1", "Gangguan berjalan tepat selama dua jam pemeliharaan."),
         ]),
        ("DL5", "Syllabus Extract: Chemistry Seminar Late Work Rubric",
         "CHEM 210 Analytical Methods — Syllabus Policy Addendum\n"
         "Weekly problem sets are uploaded Mondays and due Fridays at 5:00 PM via Canvas. Each student holds two penalty-free 24-hour 'Flex Tokens' usable automatically without emailing Dr. Vega; after both Flex Tokens spend, late homework deducts 10% per calendar day (zero credit after 72 hours). Lab safety incident reflections cannot use Flex Tokens and require in-person checkoff.",
         [
             ("How do students redeem one of their two 24-hour Flex Tokens on weekly Canvas problem sets?", ["Simply submit within 24 hours past Friday 5:00 PM; the token applies automatically.", "Send Dr. Vega a signed paper medical excuse every Thursday noon.", "Complete extra safety incident essays inside the chemistry lab.", "Score 100% on Monday's online practice quiz."], 0, "IBT-MOD-01", "B2", "Token berlaku otomatis tanpa harus mengirim email persetujuan terlebih dulu."),
             ("Which assignment category is barred from utilizing a Flex Token extension?", ["Lab safety incident reflections requiring live checkoff.", "Friday evening homework problem sets.", "Optional extra-credit bibliography uploads.", "Monograph book summaries uploaded Mondays."], 0, "IBT-MOD-01", "B1", "Refleksi insiden keamanan lab dikecualikan dan butuh cek tatap muka."),
             ("What penalty applies if a student submits an assignment 48 hours late after exhausting both tokens?", ["Twenty percent point deduction (two calendar days x 10%).", "Complete zero credit immediately past one minute late.", "Automatic deduction of an extra future Flex Token.", "Five percent credit bonus for lab attendance."], 0, "IBT-MOD-01", "B2", "Keterlambatan 2 hari setelah token habis memotong 2 x 10% = 20%."),
         ]),
    ],
    2: [
        ("DL1", "Digital Humanities Exhibition Flyer: OCR & Map Archives",
         "Special Collections Gallery | 'Streets in Pixels' Open Weekdays 10 AM–6 PM (Free with Campus ID)\n"
         "Explore newly digitized 1910 fire-insurance atlases and interactive streetcar overlays. Bring a laptop Tuesday at 3 PM for an introductory 40-minute workshop on cleaning noisy historical OCR text with Python regex—no prior coding background required, though registering online caps lab tables at 22 seats.",
         [
             ("Who needs to register online ahead of Tuesday's event?", ["Attendees planning to join the 40-minute OCR cleanup workshop.", "Any visitor walking through gallery exhibits weekdays at noon.", "Art professors lending 1910 paper atlases for scanning.", "IT staff maintaining library Wi-Fi routers."], 0, "IBT-MOD-01", "B1", "Workshop dibatasi 22 kursi sehingga memerlukan pendaftaran daring."),
             ("What entry charge applies to enrolled students visiting the exhibition?", ["Free with valid student campus identification.", "Ten dollars per exhibition wing.", "Twenty-two dollars covering Python notebook fees.", "Free exclusively on Tuesday mornings."], 0, "IBT-MOD-01", "B1", "Gratis bagi pemilik ID kampus."),
             ("What background knowledge is expected for Tuesday's 3 PM hands-on session?", ["No previous programming experience is necessary.", "At least two semesters of compiler C++ coursework.", "Published cartography journal articles from 1910.", "Manual fire-insurance drawing licensing."], 0, "IBT-MOD-01", "B1", "Flyer menegaskan 'no prior coding background required'."),
         ]),
        ("DL2", "Campus Email: Writing Center Evening Hours Shift",
         "To: All Undergraduate Students | From: Academic Writing Center\n"
         "During finals-prep weeks (Dec 1–12), peer tutors in Room 204 extend drop-in consultations from 6 PM until 10 PM Sundays through Thursdays. Appointments remain capped at 25 minutes so more writers receive structural thesis feedback; please arrive with two printed or tablet-ready pages—remember tutors cannot copy-edit take-home exams flagged 'no outside assistance'.",
         [
             ("What specific time window expands during Dec 1–12 finals preparation?", ["Sunday through Thursday evenings extending until 10:00 PM.", "Saturday morning drop-in slots starting at 6:00 AM.", "Friday midnight marathon grammar clinics.", "Individual two-hour thesis editing sessions daily."], 0, "IBT-MOD-01", "B1", "Jam layanan malam diperpanjang Minggu–Kamis pukul 18.00–22.00."),
             ("Why are consultations restricted to twenty-five minutes each?", ["To keep open slots accessible to a wider number of waiting student writers.", "Because peer tutors work strictly fifteen-minute shifts.", "Fire regulations clear Room 204 every half-hour.", "Campus printers refuse printing documents past page three."], 0, "IBT-MOD-01", "B2", "Durasi dibatasi 25 menit agar lebih banyak mahasiswa mendapat giliran umpan balik."),
             ("Which kind of assignment must peer tutors decline reviewing?", ["Take-home exams marked by instructors as prohibiting outside help.", "Personal statement drafts exceeding two pages.", "Rough bibliography outlines written on tablets.", "Freshman argumentative sociology outlines."], 0, "IBT-MOD-01", "B1", "Tutor dilarang memeriksa ujian bawa pulang yang melarang bantuan luar."),
         ]),
        ("DL3", "Library Lockers Renewal Email",
         "From: Library Circulation | Re: Graduate Carrel Locker Annual Check\n"
         "Locker keys issued for the current academic year expire May 24 at 4 PM. Continuing researchers remaining enrolled through summer may renew online via 'MyLibrary Profile -> Lockers' before May 20—avoiding key deposit forfeiture ($15). Unclaimed books left inside expired lockers after May 25 return automatically to public stacks.",
         [
             ("What deadline allows summer-enrolled graduate students to keep their current locker smoothly?", ["Submitting online renewal before May 20.", "Returning metal keys personally at 4 PM on May 25.", "Paying a fresh $15 annual cash deposit on May 24.", "Moving unclaimed books directly onto open display carts."], 0, "IBT-MOD-01", "B1", "Perpanjangan online harus dilakukan sebelum 20 Mei agar terhindar penyitaan deposit."),
             ("What happens to library books still left inside lockers past May 25?", ["Staff clear them out and reshelve them in open library circulation stacks.", "Circulation auctions unclaimed reference books downtown.", "Lockers automatically freeze key renewals for two years.", "Students pay $15 daily late storage charges."], 0, "IBT-MOD-01", "B2", "Buku yang tidak diambil pasca-25 Mei dikembalikan ke rak umum."),
             ("What fee risks loss if keys are neither renewed before May 20 nor returned by May 24?", ["A fifteen-dollar key deposit.", "Full tuition late registration surcharges.", "Forty-dollar digital scanner usage fee.", "Five-dollar monthly magazine fee."], 0, "IBT-MOD-01", "B1", "Deposit kunci sebesar $15 terancam hangus."),
         ]),
        ("DL4", "Transit Pass Notice: Regional Bike-Share App Sync",
         "Civic Mobility Advisory | Campus E-Bike Docking Upgrades\n"
         "On Saturday night (Nov 21, 11 PM–3 AM), municipal technicians will replace cellular modems at three university bike-share racks. Docked bikes remain parked securely, yet unlocking new rides via QR scan will pause during those four overnight hours. Monthly campus pass holders retain unlimited 45-minute weekday trips automatically once servers restart at 3 AM.",
         [
             ("What inconvenience occurs between 11 PM Saturday and 3 AM Sunday?", ["Riders cannot unlock shared bikes via smartphone QR scans during modem swaps.", "University bike racks will be removed permanently across three dorms.", "Weekday pass lengths drop from forty-five down to fifteen minutes.", "Riders returning bikes before 11 PM incur steep modem charges."], 0, "IBT-MOD-01", "B1", "Proses buka kunci QR dijeda selama 4 jam penggantian modem malam hari."),
             ("What happens to existing monthly campus bike-share subscriptions after 3 AM Sunday?", ["Member privileges and unlimited 45-minute rides stay unchanged automatically.", "Users must repurchase physical steel pass cards downtown.", "Trip fees double throughout Thanksgiving weekday commutes.", "Modems erase historical commute mileage balances."], 0, "IBT-MOD-01", "B2", "Langganan bulanan tetap aktif otomatis begitu sistem kembali online pukul 03.00."),
             ("Which docks are slated for hardware replacement?", ["Three specific campus university bike-share stations.", "Every downtown municipal bus shelter.", "Private bicycle repair stands outside Chemistry Hall.", "Electric transit trains running past East Stadium."], 0, "IBT-MOD-01", "B1", "Tiga rak bike-share universitas mengalami pembaruan modem."),
         ]),
        ("DL5", "Cafeteria Nutrition Menu Note: Allergen Labeling Update",
         "Campus Dining Services — Food Safety Update\n"
         "All freshly plated vegetarian entrées at Courtyard Grill now use toasted sesame oil rather than peanut oil, prompted by student allergy surveys. Tree-nut walnut toppings remain pre-packaged in sealed side packets rather than mixed inside warm grain bowls. Guests with severe celiac sensitivity should request clean purple-handled gluten-free prep utensils directly from line cooks.",
         [
             ("What cooking oil change took place at Courtyard Grill vegetarian stations?", ["Chefs replaced peanut oil with toasted sesame oil across hot entrées.", "Warm bowls now blend crushed walnuts straight into steamed rice.", "Sesame oils were banned entirely from campus cafeterias.", "All salads switched cleanly to sunflower spray."], 0, "IBT-MOD-01", "B1", "Stasiun vegetarian mengganti minyak kacang tanah menjadi minyak wijen sangrai."),
             ("How are walnut toppings currently handled for warm grain bowls?", ["Served separately in sealed individual side packets.", "Sprinkled automatically over every hot vegetarian tray.", "Ground smoothly into liquid walnut dressing sauces.", "Removed completely from Campus Dining purchasing lists."], 0, "IBT-MOD-01", "B1", "Kacang kenari dipisah dalam kemasan tertutup di samping."),
             ("What step is advised for diners needing strict gluten-free utensil handling?", ["Ask line cooks specifically for dedicated purple-handled prep utensils.", "Wash regular stainless spoons with boiling soup water first.", "Order strictly pre-sealed sesame nut packages.", "Submit medical cards three days ahead to dining directors."], 0, "IBT-MOD-01", "B2", "Tamu sensitif gluten diminta meminta peralatan bergagang ungu khusus kepada koki."),
         ]),
    ],
    3: [
        ("DL1", "Botany Lab Memo: Herbarium Sterilization Freeze Cycle",
         "To: Botany 302 Research Assistants | From: Herbarium Curators\n"
         "Before filing any newly pressed field plants into archival oak cabinets, every sealed paper parcel must spend 72 continuous hours inside our -25 °C blast freezer to kill unseen beetle eggs. Never load damp fresh specimens directly into freezer trays—moisture expands inside frozen blades and blackens leaf pores. Tag parcels clearly with your lab initials and thaw time.",
         [
             ("Why must sealed dried plant specimens enter a -25 °C freezer for three days before cabinet storage?", ["To destroy hidden insect eggs that could infest historical collections.", "To harden delicate flower petals into crystal ice ornaments.", "To extract bitter plant chlorophyll colors smoothly onto paper.", "To cool warm oak hallway cabinets during July heatwaves."], 0, "IBT-MOD-01", "B1", "Pembekuan 72 jam bertujuan membasmi telur kumbang perusak koleksi herbarium."),
             ("What happens if assistants place still-moist plants inside the blast freezer prematurely?", ["Freezing ice bursts plant cells and turns green leaf surfaces dark black.", "Oak storage drawers warp tightly shut inside cold rooms.", "Paper specimen labels peel cleanly off aluminum trays.", "Beetle larvae hibernate comfortably until spring thaws."], 0, "IBT-MOD-01", "B2", "Kelembapan sisa membeku di jaringan daun dan menghitamkan spesimen."),
             ("What identifying info is mandatory on every parcel entering the freezer?", ["Researcher lab initials alongside expected thaw removal timestamps.", "Official municipal shipping receipts in triplicate.", "Exact soil clay acidity percentage measurements.", "QR barcodes glued directly onto dried stems."], 0, "IBT-MOD-01", "B1", "Setiap bungkusan wajib dilabeli inisial lab dan waktu pencairan."),
         ]),
        ("DL2", "Notice: Language Exchange Café Format Change",
         "Global Commons Student Network — Wednesday Language Tables\n"
         "Beginning this week, Wednesday Conversation Tables shift from unstructured open tables to paired 20-minute language blocks: 5:00–5:20 PM strictly in target language (Indonesian/Spanish/Japanese/French), followed by 5:20–5:40 PM open bilingual Q&A in English. Complimentary mint tea continues at 4:45 PM. Drop by Table 4 without registration—just respect the 20-minute immersion bell.",
         [
             ("How does Wednesday's newly structured Language Café organize its forty-minute core?", ["First twenty minutes in immersion target tongue, then twenty minutes open bilingual Q&A.", "Entire afternoon sessions run strictly in silent written dictionaries.", "Students rotate tables every five minutes drinking cold mineral juice.", "Advance online registration closes every Tuesday at midnight."], 0, "IBT-MOD-01", "B1", "Sesi dibagi dua blok 20 menit: bahasa sasaran penuh lalu tanya jawab dwibahasa."),
             ("Do visitors need an advance ticket or sign-up form to join Table 4?", ["No registration is necessary; students simply drop in.", "Only paying Global Commons pass holders receive seats.", "Applicants must pass an introductory 10-minute vocal quiz.", "Tea cups require refundable deposits at reception."], 0, "IBT-MOD-01", "B1", "Catatan menyebut 'without registration'—cukup hadir langsung."),
             ("At what time are complimentary hot drinks served ahead of table sessions?", ["At 4:45 PM.", "At 5:20 PM.", "At 6:00 PM sharp.", "Strictly at Tuesday lunch."], 0, "IBT-MOD-01", "B1", "Teh mint gratis tersedia mulai 16.45."),
         ]),
        ("DL3", "IT Security Bulletin: Campus Wi-Fi Certificate Update",
         "University Network Services | Mandatory Cert Refresh (Before Dec 1)\n"
         "On December 1 at 2 AM, Campus-Secure Wi-Fi upgrades its authentication server certificate. Most modern phones and laptops prompt once cleanly for confirmation: verify server reads 'radius.university.edu' before clicking Trust. Older lab sensors using static MAC bypasses stay online uninterrupted via 'IoT-Legacy'; helpdesk desks inside Union Hall offer free setup Dec 1–3.",
         [
             ("What screen check should students verify before accepting the December 1 Wi-Fi prompt?", ["Confirm the hostname explicitly displays 'radius.university.edu'.", "Delete saved university email passwords permanently.", "Switch laptops onto unencrypted public guest hot-spots.", "Disable hardware Wi-Fi radios between 2 AM and midnight."], 0, "IBT-MOD-01", "B1", "Pastikan nama server sertifikat tertera 'radius.university.edu' sebelum menekan Trust."),
             ("Which devices avoid manual certificate confirmation steps during the upgrade?", ["Laboratory sensors already registered on the 'IoT-Legacy' bypass network.", "Personal tablet browsers connecting after 2 AM.", "Library printer kiosks located inside Union Hall.", "Student residence hallway televisions."], 0, "IBT-MOD-01", "B2", "Sensor lab di jaringan IoT-Legacy tidak terpengaruh pembaruan ini."),
             ("Where can students obtain live troubleshooting if connection prompts fail Dec 1–3?", ["At in-person IT helpdesk desks inside Union Hall.", "By mailing broken laptops to campus mailrooms.", "Strictly through midnight weekend telephone bots.", "Inside Science Quad laboratory supply counters."], 0, "IBT-MOD-01", "B1", "Pos bantuan tersedia langsung di Union Hall tanggal 1–3 Desember."),
         ]),
        ("DL4", "Recreation Desk Rule: Climbing Wall Belay Check",
         "Outdoor Center Wall Policy | Autumn Term\n"
         "Having passed a belay test at an outside commercial climbing gym does not waive our in-house Campus Wall safety check. Free 10-minute verification tests run top-of-the-hour every evening 5 PM–8 PM; bring clean indoor climbing shoes (street sneakers are barred on textured holds). Rental harnesses cost $3 per visit, or $15 per semester.",
         [
             ("Can climbers skip the Campus Wall safety verification by showing cards from outside gyms?", ["No—everyone must pass the free 10-minute in-house verification test.", "Yes, provided commercial gym passes remain less than six months old.", "Only climbing team varsity captains receive automatic waivers.", "Yes, if renting three-dollar harnesses at reception."], 0, "IBT-MOD-01", "B1", "Uji eksternal tidak menggantikan tes verifikasi 10 menit internal kampus."),
             ("Why are ordinary street sneakers forbidden on textured wall routes?", ["Clean indoor climbing shoes are mandatory to protect holds cleanly.", "Outdoor Center runs out of rubber laces every Tuesday evening.", "Textured holds slice cotton sneaker fabric swiftly.", "Only barefoot athletes test high-angle routes."], 0, "IBT-MOD-01", "B1", "Sepatu panjat dalam ruang yang bersih diwajibkan; sepatu jalanan dilarang."),
             ("How much does an entire semester harness pass cost compared with daily rentals?", ["Fifteen dollars per semester instead of three dollars per single climb.", "Ten dollars per hour regardless of semester duration.", "Free with any dining cafeteria ID swipe.", "Twenty-five dollars refundable upon returning chalk."], 0, "IBT-MOD-01", "B1", "Sewa harness $15 per semester atau $3 per kunjungan."),
         ]),
        ("DL5", "Parking Enforcement Notice: Game-Day Lot Towing",
         "Campus Parking & Fleet Operations | Saturday Football Advisory\n"
         "Red Lot C and Arena West turn into ticketed stadium visitor lots beginning 6:00 AM every home football Saturday. Student permit holders normally parked in Lot C must relocate vehicles by 11:59 PM Friday night to North Garage (Levels 2–4 open free to all student decals Fridays–Sundays) to avoid mandatory $95 relocation towing.",
         [
             ("By what deadline must student cars leave Red Lot C before home Saturday games?", ["Friday night at 11:59 PM.", "Saturday morning at 9:00 AM.", "Sunday afternoon at 4:00 PM.", "Thursday noon before team practice."], 0, "IBT-MOD-01", "B1", "Kendaraan mahasiswa wajib dipindahkan maksimal Jumat pukul 23.59."),
             ("Where can displaced student permit holders park cleanly at no extra weekend fee?", ["Levels 2–4 of North Garage.", "Inside Arena West donor VIP lanes.", "Curbside lanes outside main stadium ticket gates.", "Grass football fields after Friday sunset."], 0, "IBT-MOD-01", "B1", "Lantai 2–4 North Garage terbuka gratis bagi pemegang stiker mahasiswa di akhir pekan."),
             ("What financial consequence hits vehicles remaining inside Red Lot C Saturday at dawn?", ["Mandatory $95 vehicle towing charges.", "Loss of semester library book borrowing rights.", "Free complimentary relocation shuttles.", "Five-dollar meter surcharge per hour."], 0, "IBT-MOD-01", "B1", "Mobil yang tertinggal kena denda derek wajib $95."),
         ]),
    ],
    4: [
        ("DL1", "Campus Email: Economics Lab Dataset Confidentiality",
         "From: Prof. Nair (ECON 340) | Subject: Household Panel File Rules\n"
         "Notice when downloading the anonymized regional household income CSV inside the shared server: never copy raw spreadsheet files onto personal cloud folders, USB sticks, or third-party AI web chats! Analyze summary regressions strictly inside virtual lab terminals. Exporting final aggregate slope tables and clean charts for Friday posters is explicitly encouraged.",
         [
             ("Which handling action is strictly prohibited for the raw ECON 340 household CSV?", ["Copying raw data files onto personal clouds, thumb drives, or online AI sites.", "Generating aggregate statistical regressions inside virtual lab desktops.", "Exporting finished clean slope charts onto Friday class posters.", "Working jointly with assigned lab partners inside computer rooms."], 0, "IBT-MOD-01", "B1", "Dilarang menyalin file mentah ke cloud pribadi, USB, atau situs AI luar."),
             ("What output from the dataset MAY students export outside the lab for Friday's seminar?", ["Aggregated slope tables and summary statistical figures.", "Complete unedited household street identifiers.", "Password keys accessing shared economics terminals.", "Raw individual salary spreadsheets."], 0, "IBT-MOD-01", "B2", "Tabel agregat dan grafik ringkasan akhir justru dianjurkan untuk poster."),
             ("Where should primary statistical computations execute?", ["Inside university virtual laboratory computer environments.", "Across public home coffee-shop tablet apps.", "Inside emailed paper spreadsheets printed at dorms.", "On personal smartphone calculators."], 0, "IBT-MOD-01", "B1", "Analisis harus dijalankan di dalam terminal virtual lab."),
         ]),
        ("DL2", "Health Clinic Flyer: Flu Vaccine Clinic Pop-Up",
         "Student Wellness Services | Courtyard Tent (Nov 4–5, 9 AM–4 PM)\n"
         "No advance appointment needed for standard seasonal flu shots—bring valid student ID and wear short sleeves. Free for all enrolled undergraduates regardless of private insurance plan. Severe egg-allergy alternative vaccines are stocked strictly inside Main Clinic Suite 110 (call ext. 4412 ahead).",
         [
             ("Who needs to schedule ahead or visit Main Clinic Suite 110 instead of the outdoor tent?", ["Students requiring specialized severe egg-allergy vaccine variants.", "Enrolled undergraduates lacking private health insurance cards.", "Visitors preferring quick midday short-sleeve shots.", "Faculty members walking past the courtyard between 9 AM and 4 PM."], 0, "IBT-MOD-01", "B2", "Vaksin alternatif alergi telur berat hanya tersedia di ruang klinik utama 110."),
             ("What payment applies to enrolled undergraduates getting standard tent flu shots?", ["Zero cost regardless of private coverage plans.", "Fifteen-dollar tent administrative copay.", "Free exclusively on Tuesday morning hours.", "Half price if wearing short sleeves."], 0, "IBT-MOD-01", "B1", "Gratis bagi seluruh mahasiswa terdaftar terlepas dari asuransi."),
             ("What two practical items make tent check-in fastest?", ["Carrying a valid campus ID and wearing short-sleeved shirts.", "Printing five signed medical history books.", "Scheduling hourly digital tickets weeks beforehand.", "Bringing sealed sterile needles from pharmacy counters."], 0, "IBT-MOD-01", "B1", "Cukup bawa kartu mahasiswa dan mengenakan lengan pendek."),
         ]),
        ("DL3", "Museum Curator Memo: Volunteer Tour Scripting",
         "Natural History Docent Coordinators | Fossil Hall Refresh\n"
         "Thank you for mentoring incoming Saturday guides! Notice: guests frequently ask why ammonite fossils appear atop alpine ridges. Instead of reciting geological epoch dates silently memorized off wall labels, start every gallery stop with a tactile fossil cast passed hand-to-hand while inviting visitors to guess how oceanic shells reached snowy heights.",
         [
             ("What teaching technique do coordinators urge new Fossil Hall guides to avoid?", ["Reciting memorized bare epoch dates passively off museum plaques.", "Inviting families to inspect tactile cast models first.", "Connecting marine fossils interactively to alpine mountain uplift.", "Mentoring Saturday student volunteers on questioning habits."], 0, "IBT-MOD-01", "B1", "Pembimbing diminta menghindari hafalan kaku tanggal era geologi dari papan keterangan."),
             ("How should docents open explanations around alpine marine fossils?", ["Pass around physical fossil casts and pose an intriguing question about location.", "Hand visitors printed 40-page geology theses silently.", "Lock fragile glass exhibits shut before noon crowds arrive.", "Point immediately to gift-shop geology posters."], 0, "IBT-MOD-01", "B2", "Mulai dengan memegang cetakan fosil dan mengajak pengunjung menebak mengapa cangkang laut ada di gunung."),
             ("Which day of week specifically brings the incoming trainee guides mentioned?", ["Saturday.", "Wednesday.", "Tuesday.", "Monday."], 0, "IBT-MOD-01", "B1", "Teks menyinggung 'incoming Saturday guides'."),
         ]),
        ("DL4", "Dorm Kitchen Protocol: Shared Spice & Fridge Purges",
         "Green House Co-op Kitchen Rules\n"
         "Every Sunday evening at 8:00 PM, floor stewards empty unlabeled jars and spoiled produce out of shared refrigerators—always label personal boxes clearly with masking tape name + date! Shared olive oil, salt, and bulk oats restock weekly out of house dues; please run industrial dishwashers immediately whenever sink racks hit half capacity.",
         [
             ("How do co-op residents prevent floor stewards from throwing out good stored leftovers Sunday night?", ["Stick masking tape naming the owner and storage date on containers.", "Lock small coolers inside carpeted bedroom closets.", "Wash clean plates immediately inside hallway bathrooms.", "Pay extra weekly house dues at dinner."], 0, "IBT-MOD-01", "B1", "Kotak makanan harus dilabeli selotip berisi nama dan tanggal agar tidak dibuang."),
             ("Which staples are funded collectively via house dues rather than bought individually?", ["Olive oil, cooking salt, and bulk rolled oats.", "Private labeled restaurant containers.", "Disposable cardboard coffee cups.", "Imported frozen seafood fillets."], 0, "IBT-MOD-01", "B1", "Minyak zaitun, garam, dan gandum curah diisi ulang mingguan dari iuran rumah."),
             ("When should residents start the commercial kitchen dishwasher?", ["As soon as clean-waiting loading racks become half full.", "Strictly once weekly right after Sunday 8 PM fridge sweeps.", "Only when dining halls close across Thanksgiving break.", "Every morning before 6 AM breakfast cooking."], 0, "IBT-MOD-01", "B1", "Segera nyalakan saat rak cuci mencapai kapasitas setengah penuh."),
         ]),
        ("DL5", "Grant Office Checklist: Undergraduate Travel Reimbursement",
         "Office of Undergraduate Research | Post-Conference Expense Guide\n"
         "Travel reimbursement claims must upload itemized hotel folios and boarding stubs within 14 calendar days after return flight arrival—credit-card summary lines showing only total dollar charges get rejected! Meals cap flat at $52/day (no alcohol receipts). Direct bank deposits clear roughly ten business days after department chair signoff.",
         [
             ("Why are plain credit-card monthly statement lines rejected for hotel or airfare claims?", ["Auditors require fully itemized line-by-line receipts rather than lump totals.", "Card banks withhold passenger landing stubs for fourteen days.", "Meal costs always include unauthorized restaurant beverages.", "Department chairs refuse electronic bank deposit numbers."], 0, "IBT-MOD-01", "B2", "Harus mengunggah bukti rinci per item, bukan sekadar ringkasan tagihan kartu kredit."),
             ("Within how many calendar days of returning home must students submit travel documentation?", ["Fourteen calendar days.", "Ten business days.", "Fifty-two days flat.", "Immediately prior to purchasing flight tickets."], 0, "IBT-MOD-01", "B1", "Batas unggah klaim adalah 14 hari kalender sejak penerbangan pulang tiba."),
             ("How quickly do approved reimbursements arrive after department chair approval?", ["Approximately ten business days via direct deposit.", "Same afternoon in cash at conference hotel lobbies.", "Fifty-two hours straight into student meal cards.", "End of following summer semester."], 0, "IBT-MOD-01", "B1", "Dana cair sekitar sepuluh hari kerja lewat transfer langsung."),
         ]),
    ],
    5: [
        ("DL1", "Observatory Public Night Cancellation Policy",
         "Campus Planetarium & 16-Inch Rooftop Telescope | Friday Viewing Nights\n"
         "Rooftop viewing sessions happen Fridays 8–10 PM solely under clear-sky forecasts; whenever cloud decks exceed 60% by 5 PM, rooftop viewing cancels cleanly while our indoor 45-minute planetarium dome show STILL proceeds indoors at 8:15 PM (free entry, capped at 75 seats first-come). Warm coats recommended for open rooftop balconies.",
         [
             ("If overcast rain clouds blanket campus at 5 PM Friday, what still takes place?", ["The free 45-minute indoor planetarium dome show at 8:15 PM.", "Mandatory rooftop 16-inch infrared Saturn tracking.", "Paid refund exchanges for observatory sweaters.", "Afternoon solar telescope tours starting at 5 PM."], 0, "IBT-MOD-01", "B1", "Bila langit berawan, sesi atap batal tetapi pertunjukan kubah planetarium tetap berlangsung."),
             ("What limits seating capacity during overcast indoor shows?", ["First-come seating up to 75 guests.", "Mandatory advance graduate astronomy tickets.", "Warm winter coat inspections at elevators.", "Cloud radar percentages dropping below 10%."], 0, "IBT-MOD-01", "B1", "Kapasitas ruang dalam dibatasi 75 pengunjung datang lebih dulu."),
             ("Under clear weather conditions, how long is the open rooftop telescope window?", ["Two hours (8:00 PM to 10:00 PM Fridays).", "Forty-five minutes starting at 5:00 PM.", "Every weekday dusk until midnight.", "Seventy-five minutes after sunrise."], 0, "IBT-MOD-01", "B1", "Pengamatan teropong atap berlangsung Jumat pukul 20.00–22.00."),
         ]),
        ("DL2", "Email from Lab Coordinator: Acoustic Mic Calibration Logs",
         "To: LING 315 Field Phonetics Crew | From: Samira (Lab Manager)\n"
         "Before checking out field Marantz recorders for weekend dialect interviews, run the 30-second clap-tone check against Reference Mic #2 inside Booth A—we noticed gain knob drift on Unit 4 after Wednesday's humid workshop. Always store recordings natively as uncompressed 48 kHz WAV rather than MP3 so vowel formant frequencies stay sharp during Praat analysis!",
         [
             ("Why must dialect researchers run a quick clap-tone test before borrowing field kits?", ["Gain settings drifted unexpectedly on Unit 4 earlier this week.", "Booth A headphones disconnect during rainy weekends.", "Marantz memory slots erase WAV clips automatically every Wednesday.", "Praat analysis fails on quiet room acoustics."], 0, "IBT-MOD-01", "B2", "Unit 4 mengalami pergeseran penguatan (gain drift) setelah lokakarya hari Rabu."),
             ("Which audio file format is mandatory for weekend phonetics interviews and why?", ["Uncompressed 48 kHz WAV to preserve fine vowel acoustic formants.", "Low-bitrate MP3 to double recorder battery endurance.", "Paper phonetic hand sketches instead of microphones.", "Short 30-second video reels over phone cameras."], 0, "IBT-MOD-01", "B1", "Format WAV tak terkompresi menjaga ketajaman frekuensi formant vokal."),
             ("Inside which room do students inspect microphone gain levels before checking kits out?", ["Booth A.", "Reference Unit 4 closet.", "Main Campus Plaza.", "Chemistry Annex Room 315."], 0, "IBT-MOD-01", "B1", "Uji dilakukan melawan Reference Mic #2 di Booth A."),
         ]),
        ("DL3", "Community Garden Notice: Compost Turning Schedule",
         "Campus Eco-Coop Plots | Autumn Soil Prep Bulletin\n"
         "Plot holders are reminded NOT to dump diseased tomato vines or woody rose thorns into active Bay 1–2 kitchen bins (pile blight spores survive mild backyard heat!). Toss diseased leaves into marked municipal Yard-Waste barrels along the north fence. Volunteers turning warm Bay 3 compost piles gather Saturdays at 9:30 AM—pitchforks and heavy work gloves provided.",
         [
             ("Where should gardeners discard diseased tomato foliage instead of active Bay 1–2 bins?", ["Into designated Yard-Waste barrels along the garden's north fence.", "Directly inside Bay 3 Saturday turning heaps.", "Buried deep beneath fresh culinary basil pots.", "Inside indoor kitchen aluminum dish racks."], 0, "IBT-MOD-01", "B1", "Tanaman sakit dibuang ke tong Yard-Waste di pagar utara agar spora penyakit tidak menyebar."),
             ("Why are diseased vines excluded from backyard Bay 1–2 compost piles?", ["Moderate home-bin temperatures fail to neutralize lingering blight spores.", "Woody vines bend stainless Saturday garden pitchforks.", "Kitchen bins hold strictly liquid rainwater barrels.", "Tomato aromas attract residential hallway mice."], 0, "IBT-MOD-01", "B2", "Panas kompos skala kecil tidak cukup tinggi untuk mematikan spora hawar daun."),
             ("What equipment do Saturday morning volunteers need to bring personally?", ["Nothing special—pitchforks and work gloves are already supplied.", "Steel municipal barrels and tomato cages.", "Personal chemical thermometers from Chemistry Hall.", "Fresh rose cuttings for north fence planting."], 0, "IBT-MOD-01", "B1", "Garpu taman dan sarung tangan kerja berat sudah disediakan pengelola."),
         ]),
        ("DL4", "Printing Hub Notice: Thesis Binding Cutoff Dates",
         "Campus Print & Bind Desk | Graduating Seniors Alert\n"
         "Same-day spiral coil binding finishes within 3 hours on ordinary weekdays, yet archival sewn buckram hardcovers for honors theses require 5 full business days at our off-site bindery! Submit print-ready PDFs with 1.5-inch left binding margins by April 28 at 3:00 PM to guarantee hardcovers in hand ahead of May 7 departmental honors receptions.",
         [
             ("How many business days must students allow for permanent buckram hardcover thesis binding?", ["Five full business days because off-site bindery finishing takes longer.", "Barely three afternoon hours on any ordinary weekday.", "Fourteen calendar days matching airline travel rebates.", "Only one overnight window before May 7."], 0, "IBT-MOD-01", "B1", "Jilid keras permanen membutuhkan 5 hari kerja penuh di fasilitas eksternal."),
             ("What page layout specification is highlighted as required in uploaded PDFs?", ["A 1.5-inch left gutter margin for binding clearance.", "Colorful double-sided marbled cover artwork.", "Loose spiral rings shipped separately inside mail cartons.", "Landscape charts exclusively on pages 1 through 5."], 0, "IBT-MOD-01", "B1", "File PDF wajib memakai margin kiri 1,5 inci agar tulisan tidak termakan jilidan."),
             ("What exact timestamp guarantees hardcover delivery before May 7 receptions?", ["April 28 by 3:00 PM.", "May 7 at 9:00 AM.", "April 29 midnight.", "Three hours prior to graduation lunch."], 0, "IBT-MOD-01", "B1", "Tenggat pengumpulan cetak adalah 28 April pukul 15.00."),
         ]),
        ("DL5", "Syllabus Extract: Linguistic Corpus Ethics Agreement",
         "LING 402 Corpus Design — Participant Recording Consent Protocol\n"
         "Every interview recording contributed to our Open Dialect Archive requires signed two-box permission prior to pressing record: Box 1 authorizes research transcription locally; Box 2 asks optionally whether clips may play publicly online. Declining Box 2 never disqualifies participants from $20 gift stipends—simply tag audio filenames '_LOCALONLY' before uploading to server folder B.",
         [
             ("If an interviewee agrees to local study transcription (Box 1) yet declines public web playback (Box 2), what happens to their $20 stipend?", ["They still earn the full $20 thank-you stipend without penalty.", "Their compensation halves down to ten dollars.", "Student interviewers must erase both audio clips silently.", "Server folder B blocks payment until Box 2 switches yes."], 0, "IBT-MOD-01", "B2", "Menolak penayangan publik (Kotak 2) tetap berhak penuh atas insentif $20."),
             ("What filename tag prevents private recordings from accidentally leaking to public online collections?", ["Adding '_LOCALONLY' cleanly to filenames inside folder B.", "Marking physical cassettes with bright orange tape.", "Sending un-tagged MP3 files straight onto classroom wikis.", "Deleting Box 1 signatures immediately after recording."], 0, "IBT-MOD-01", "B1", "File wajib diberi akhiran '_LOCALONLY' sebelum diunggah ke folder B."),
             ("When must interviewers collect written consent from dialect speakers?", ["Before pressing the record button to begin audio capture.", "Immediately after listening to playback inside Booth A.", "Within fourteen days following stipend gift card mailings.", "Strictly whenever public web transcripts exceed 500 words."], 0, "IBT-MOD-01", "B1", "Persetujuan bertanda tangan wajib diperoleh sebelum menekan tombol rekam."),
         ]),
    ],
}


def build_ibt_words_and_daily(pack_num: int) -> tuple[list[dict], list[dict]]:
    """Builds 20 Complete-the-Words + 15 Daily Life items + 5 Daily Life passages (no CW passages)."""
    items = []
    passages = []
    q_idx = 1

    # 20 Complete-the-Words items
    for cw_id, cw_title, cw_text, blanks in _CW_DATA[pack_num]:
        pid = f"IBT0{pack_num}-{cw_id}"
        assert len(blanks) == 10, f"{pid} expected 10 blanks, got {len(blanks)}"
        passages.append({"id": pid, "title": cw_title, "body": cw_text, "readingOrder": len(passages) + 1})
        for b_num, (clue, ans, acc, exp) in enumerate(blanks, start=1):
            qid = f"IBT26-0{pack_num}-Q{q_idx:03d}"
            items.append(
                make_q(
                    qid=qid,
                    section="READING",
                    task_type="FILL_BLANK",
                    prompt=f"Complete the Word ({cw_title} — Blank [{b_num}]): Fill the missing full word for '{clue}'.",
                    options=[],
                    answer=ans,
                    explanation=exp,
                    rationale_source="IBT-MOD-07",
                    difficulty="B2",
                    passage_id=pid,
                    accepted_answers=acc,
                ),
            )
            q_idx += 1

    # 15 Daily Life items (5 docs x 3 questions)
    for dl_id, dl_title, dl_body, qs in _DAILY_LIFE[pack_num]:
        pid = f"IBT0{pack_num}-{dl_id}"
        assert len(qs) == 3, f"{pid} expected 3 daily-life questions"
        passages.append({"id": pid, "title": dl_title, "body": dl_body, "readingOrder": len(passages) + 1})
        for sub_i, (qtext, opts, cidx, mod, diff, exp) in enumerate(qs, start=1):
            qid = f"IBT26-0{pack_num}-Q{q_idx:03d}"
            items.append(
                rotated_mcq(
                    qid=qid,
                    section="READING",
                    prompt=f"Read in Daily Life ('{dl_title}' — Q{sub_i}/3): {qtext}",
                    options=opts,
                    correct_idx=cidx,
                    explanation=exp,
                    rationale_source=mod,
                    difficulty=diff,
                    seed=pack_num * 17 + q_idx,
                    passage_id=pid,
                ),
            )
            q_idx += 1

    assert len(items) == 35
    return passages, items
