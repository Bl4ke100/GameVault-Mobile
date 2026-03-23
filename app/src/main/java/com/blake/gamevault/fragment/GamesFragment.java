package com.blake.gamevault.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blake.gamevault.R;
import com.blake.gamevault.adapter.ListingAdapter;
import com.blake.gamevault.databinding.FragmentGamesBinding;
import com.blake.gamevault.model.Game;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;


public class GamesFragment extends Fragment {

    private FragmentGamesBinding binding;
    private ListingAdapter adapter;
    private String catId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            catId = getArguments().getString("catId");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGamesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerGameView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        Game game1 = new Game("g1", "The Witcher 3: Wild Hunt", "A story-driven, next-generation open world role-playing game.", 12000.0, 2015, "cat5", "dev14", "pub14", "witcher3_poster.jpg", List.of("w3_img1.jpg"), 120, true);
//        Game game2 = new Game("g2", "Cyberpunk 2077", "An open-world, action-adventure RPG set in Night City.", 18000.0, 2020, "cat5", "dev14", "pub14", "cp2077_poster.jpg", List.of("cp_img1.jpg"), 85, true);
//        Game game3 = new Game("g3", "The Elder Scrolls V: Skyrim", "Epic fantasy open-world RPG.", 12000.0, 2011, "cat5", "dev6", "pub6", "skyrim_poster.jpg", List.of("skyrim_img1.jpg"), 200, true);
//        Game game4 = new Game("g4", "Fallout 4", "Post-apocalyptic RPG.", 6000.0, 2015, "cat5", "dev6", "pub6", "fallout4_poster.jpg", List.of("fo4_img1.jpg"), 150, true);
//        Game game5 = new Game("g5", "Final Fantasy VII Remake", "A spectacular reimagining.", 21000.0, 2020, "cat5", "dev4", "pub8", "ff7r_poster.jpg", List.of("ff7_img1.jpg"), 40, true);
//        Game game6 = new Game("g6", "Dragon Quest XI: Echoes of an Elusive Age", "A grand RPG adventure.", 12000.0, 2017, "cat5", "dev4", "pub8", "dq11_poster.jpg", List.of("dq_img1.jpg"), 30, true);
//        Game game7 = new Game("g7", "Mass Effect Legendary Edition", "Relive the cinematic sci-fi saga.", 18000.0, 2021, "cat5", "dev5", "pub5", "mass_effect_poster.jpg", List.of("me_img1.jpg"), 90, true);
//        Game game8 = new Game("g8", "Dragon Age: Inquisition", "Lead an epic adventure to save the world.", 12000.0, 2014, "cat5", "dev5", "pub5", "dai_poster.jpg", List.of("dai_img1.jpg"), 45, true);
//        Game game9 = new Game("g9", "Assassin's Creed Valhalla", "Lead epic Viking raids.", 18000.0, 2020, "cat5", "dev2", "pub2", "acv_poster.jpg", List.of("acv_img1.jpg"), 110, true);
//        Game game10 = new Game("g10", "Yakuza: Like a Dragon", "Dynamic turn-based RPG.", 18000.0, 2020, "cat5", "dev11", "pub11", "yakuza_poster.jpg", List.of("yak_img1.jpg"), 60, true);
//
//        Game game11 = new Game("g11", "Uncharted 4: A Thief's End", "Nathan Drake's final adventure.", 12000.0, 2016, "cat1", "dev10", "pub4", "uc4_poster.jpg", List.of("uc4_1.jpg"), 80, true);
//        Game game12 = new Game("g12", "God of War Ragnarök", "Kratos and Atreus journey through the Nine Realms.", 21000.0, 2022, "cat1", "dev8", "pub4", "gow_poster.jpg", List.of("gow_1.jpg"), 150, true);
//        Game game13 = new Game("g13", "The Last of Us Part I", "A brutal journey across a post-pandemic US.", 21000.0, 2022, "cat1", "dev10", "pub4", "tlou_poster.jpg", List.of("tlou_1.jpg"), 90, true);
//        Game game14 = new Game("g14", "Red Dead Redemption 2", "Epic tale of life in America's unforgiving heartland.", 18000.0, 2018, "cat1", "dev1", "pub1", "rdr2_poster.jpg", List.of("rdr2_1.jpg"), 200, true);
//        Game game15 = new Game("g15", "Grand Theft Auto V", "Explore the sprawling world of Los Santos.", 9000.0, 2013, "cat1", "dev1", "pub1", "gta5_poster.jpg", List.of("gta5_1.jpg"), 300, true);
//        Game game16 = new Game("g16", "Ghost of Tsushima", "Forge a new path to protect Tsushima.", 18000.0, 2020, "cat1", "dev12", "pub4", "got_poster.jpg", List.of("got_1.jpg"), 110, true);
//        Game game17 = new Game("g17", "Marvel's Spider-Man Remastered", "Be greater in Marvel's New York.", 18000.0, 2020, "cat1", "dev8", "pub4", "spidey_poster.jpg", List.of("spidey_1.jpg"), 140, true);
//        Game game18 = new Game("g18", "Horizon Zero Dawn", "Hunt mechanized creatures in a post-apocalyptic world.", 12000.0, 2017, "cat1", "dev8", "pub4", "hzd_poster.jpg", List.of("hzd_1.jpg"), 70, true);
//        Game game19 = new Game("g19", "Assassin's Creed Mirage", "Experience the story of Basim.", 15000.0, 2023, "cat1", "dev2", "pub2", "acm_poster.jpg", List.of("acm_1.jpg"), 100, true);
//        Game game20 = new Game("g20", "Watch Dogs: Legion", "Build a resistance to take back near-future London.", 12000.0, 2020, "cat1", "dev2", "pub2", "wdl_poster.jpg", List.of("wdl_1.jpg"), 45, true);
//
//        Game game21 = new Game("g21", "Call of Duty: Modern Warfare III", "Captain Price faces the ultimate threat.", 21000.0, 2023, "cat3", "dev3", "pub3", "mw3_poster.jpg", List.of("mw3_1.jpg"), 250, true);
//        Game game22 = new Game("g22", "Doom Eternal", "Slay demons across dimensions.", 12000.0, 2020, "cat3", "dev6", "pub6", "doom_poster.jpg", List.of("doom_1.jpg"), 60, true);
//        Game game23 = new Game("g23", "Battlefield 2042", "FPS marking the return to the iconic all-out warfare.", 18000.0, 2021, "cat3", "dev5", "pub5", "bf2042_poster.jpg", List.of("bf_1.jpg"), 120, true);
//        Game game24 = new Game("g24", "Tom Clancy's Rainbow Six Siege", "Intense, highly tactical team-based shooter.", 6000.0, 2015, "cat3", "dev2", "pub2", "r6s_poster.jpg", List.of("r6s_1.jpg"), 180, true);
//        Game game25 = new Game("g25", "Overwatch 2", "A team-based, free-to-play hero shooter.", 0.0, 2022, "cat3", "dev3", "pub3", "ow2_poster.jpg", List.of("ow2_1.jpg"), 999, true);
//        Game game26 = new Game("g26", "Destiny 2", "Action MMO with a single evolving world.", 0.0, 2017, "cat3", "dev8", "pub4", "destiny2_poster.jpg", List.of("d2_1.jpg"), 999, true);
//        Game game27 = new Game("g27", "Borderlands 3", "The original shooter-looter returns.", 15000.0, 2019, "cat3", "dev15", "pub12", "bl3_poster.jpg", List.of("bl3_1.jpg"), 80, true);
//        Game game28 = new Game("g28", "Far Cry 6", "Join a modern-day guerrilla revolution.", 18000.0, 2021, "cat3", "dev2", "pub2", "fc6_poster.jpg", List.of("fc6_1.jpg"), 90, true);
//        Game game29 = new Game("g29", "Wolfenstein II: The New Colossus", "Fight back against the war machine.", 12000.0, 2017, "cat3", "dev6", "pub6", "wolf2_poster.jpg", List.of("wolf_1.jpg"), 50, true);
//        Game game30 = new Game("g30", "Cyberpunk 2077: Phantom Liberty", "Spy-thriller expansion.", 9000.0, 2023, "cat3", "dev14", "pub14", "cppl_poster.jpg", List.of("cppl_1.jpg"), 100, true);
//
//        Game game31 = new Game("g31", "Microsoft Flight Simulator", "Test your piloting skills.", 18000.0, 2020, "cat9", "dev16", "pub15", "mfs_poster.jpg", List.of("mfs_1.jpg"), 75, true);
//        Game game32 = new Game("g32", "The Sims 4", "Play with life.", 0.0, 2014, "cat9", "dev5", "pub5", "sims4_poster.jpg", List.of("sims4_1.jpg"), 999, true);
//        Game game33 = new Game("g33", "Cities: Skylines II", "Build a metropolis.", 15000.0, 2023, "cat9", "dev17", "pub16", "cs2_poster.jpg", List.of("cs2_1.jpg"), 85, true);
//        Game game34 = new Game("g34", "Planet Coaster", "Build your dream theme park.", 12000.0, 2016, "cat9", "dev18", "pub17", "planco_poster.jpg", List.of("planco_1.jpg"), 40, true);
//        Game game35 = new Game("g35", "Euro Truck Simulator 2", "Travel across Europe.", 6000.0, 2012, "cat9", "dev19", "pub18", "ets2_poster.jpg", List.of("ets2_1.jpg"), 110, true);
//        Game game36 = new Game("g36", "Animal Crossing: New Horizons", "Create your paradise.", 18000.0, 2020, "cat9", "dev7", "pub7", "acnh_poster.jpg", List.of("acnh_1.jpg"), 200, true);
//        Game game37 = new Game("g37", "Farming Simulator 22", "Take on the role of a modern farmer.", 12000.0, 2021, "cat9", "dev20", "pub19", "fs22_poster.jpg", List.of("fs22_1.jpg"), 90, true);
//        Game game38 = new Game("g38", "Elite Dangerous", "Take control of your own starship.", 9000.0, 2014, "cat9", "dev18", "pub17", "ed_poster.jpg", List.of("ed_1.jpg"), 50, true);
//        Game game39 = new Game("g39", "Kerbal Space Program", "Build spaceships, fly them.", 12000.0, 2015, "cat9", "dev21", "pub12", "ksp_poster.jpg", List.of("ksp_1.jpg"), 60, true);
//        Game game40 = new Game("g40", "Stardew Valley", "Inherit your grandfather's old farm plot.", 4500.0, 2016, "cat9", "dev22", "pub20", "sdv_poster.jpg", List.of("sdv_1.jpg"), 300, true);
//
//        Game game41 = new Game("g41", "Call of Duty: Warzone", "Massive free-to-play combat arena.", 0.0, 2020, "cat2", "dev3", "pub3", "wz_poster.jpg", List.of("wz_1.jpg"), 999, true);
//        Game game42 = new Game("g42", "Apex Legends", "Hero shooter battle royale by Respawn.", 0.0, 2019, "cat2", "dev5", "pub5", "apex_poster.jpg", List.of("apex_1.jpg"), 999, true);
//        Game game43 = new Game("g43", "Fortnite", "Drop in and compete to be the last one standing.", 0.0, 2017, "cat2", "dev23", "pub21", "fn_poster.jpg", List.of("fn_1.jpg"), 999, true);
//        Game game44 = new Game("g44", "PUBG: BATTLEGROUNDS", "The original battle royale experience.", 0.0, 2017, "cat2", "dev24", "pub22", "pubg_poster.jpg", List.of("pubg_1.jpg"), 999, true);
//        Game game45 = new Game("g45", "Fall Guys", "Massive multiplayer party knockout game.", 0.0, 2020, "cat2", "dev23", "pub21", "fg_poster.jpg", List.of("fg_1.jpg"), 999, true);
//        Game game46 = new Game("g46", "Naraka: Bladepoint", "60-player melee-focused action battle royale.", 0.0, 2021, "cat2", "dev25", "pub23", "naraka_poster.jpg", List.of("naraka_1.jpg"), 999, true);
//        Game game47 = new Game("g47", "Hunt: Showdown", "PvPvE competitive multiplayer bounty hunting.", 12000.0, 2019, "cat2", "dev26", "pub24", "hunt_poster.jpg", List.of("hunt_1.jpg"), 60, true);
//        Game game48 = new Game("g48", "Realm Royale Reforged", "Class-based fantasy battle royale.", 0.0, 2018, "cat2", "dev27", "pub25", "realm_poster.jpg", List.of("realm_1.jpg"), 999, true);
//        Game game49 = new Game("g49", "CRSED: F.O.A.D.", "Brutal MMO last-man-standing shooter.", 0.0, 2019, "cat2", "dev28", "pub26", "crsed_poster.jpg", List.of("crsed_1.jpg"), 999, true);
//        Game game50 = new Game("g50", "Super Animal Royale", "Frenzied, 2D 64-player battle royale.", 0.0, 2021, "cat2", "dev29", "pub27", "sar_poster.jpg", List.of("sar_1.jpg"), 999, true);
//
//        Game game51 = new Game("g51", "Minecraft", "Place blocks and go on adventures.", 9000.0, 2011, "cat6", "dev30", "pub15", "mc_poster.jpg", List.of("mc_1.jpg"), 500, true);
//        Game game52 = new Game("g52", "Rust", "The only aim in Rust is to survive.", 12000.0, 2018, "cat6", "dev31", "pub28", "rust_poster.jpg", List.of("rust_1.jpg"), 120, true);
//        Game game53 = new Game("g53", "ARK: Survival Ascended", "Stranded on the shores of a mysterious island.", 13500.0, 2023, "cat6", "dev32", "pub29", "ark_poster.jpg", List.of("ark_1.jpg"), 80, true);
//        Game game54 = new Game("g54", "The Forest", "Survive against a society of cannibalistic mutants.", 6000.0, 2018, "cat6", "dev33", "pub30", "forest_poster.jpg", List.of("forest_1.jpg"), 60, true);
//        Game game55 = new Game("g55", "Subnautica", "Descend into the depths of an alien underwater world.", 9000.0, 2018, "cat6", "dev34", "pub31", "sub_poster.jpg", List.of("sub_1.jpg"), 70, true);
//        Game game56 = new Game("g56", "Don't Starve Together", "Uncompromising wilderness survival multiplayer.", 4500.0, 2016, "cat6", "dev35", "pub32", "dst_poster.jpg", List.of("dst_1.jpg"), 150, true);
//        Game game57 = new Game("g57", "DayZ", "Unforgiving, authentic open world sandbox survival.", 13500.0, 2018, "cat6", "dev36", "pub33", "dayz_poster.jpg", List.of("dayz_1.jpg"), 90, true);
//        Game game58 = new Game("g58", "Valheim", "A brutal exploration and survival game for 1-10 players.", 6000.0, 2021, "cat6", "dev37", "pub34", "val_poster.jpg", List.of("val_1.jpg"), 110, true);
//        Game game59 = new Game("g59", "Terraria", "Dig, fight, explore, build.", 3000.0, 2011, "cat6", "dev38", "pub35", "terraria_poster.jpg", List.of("terra_1.jpg"), 400, true);
//        Game game60 = new Game("g60", "State of Decay 2", "Open-world survival fantasy set just after the zombie apocalypse.", 9000.0, 2020, "cat6", "dev39", "pub15", "sod2_poster.jpg", List.of("sod2_1.jpg"), 50, true);
//
//        Game game61 = new Game("g61", "Resident Evil 4 Remake", "Survival is just the beginning.", 18000.0, 2023, "cat4", "dev9", "pub9", "re4_poster.jpg", List.of("re4_1.jpg"), 130, true);
//        Game game62 = new Game("g62", "Resident Evil Village", "Experience survival horror like never before.", 12000.0, 2021, "cat4", "dev9", "pub9", "rev_poster.jpg", List.of("rev_1.jpg"), 80, true);
//        Game game63 = new Game("g63", "Silent Hill 2 Remake", "Investigate a town shrouded in fog and monsters.", 21000.0, 2024, "cat4", "dev40", "pub13", "sh2_poster.jpg", List.of("sh2_1.jpg"), 95, true);
//        Game game64 = new Game("g64", "Outlast", "Hell is an experiment you can't survive.", 6000.0, 2013, "cat4", "dev41", "pub36", "outlast_poster.jpg", List.of("outlast_1.jpg"), 40, true);
//        Game game65 = new Game("g65", "Amnesia: The Dark Descent", "A chilling descent into madness.", 6000.0, 2010, "cat4", "dev42", "pub37", "amnesia_poster.jpg", List.of("amnesia_1.jpg"), 50, true);
//        Game game66 = new Game("g66", "Dead Space", "Sci-fi survival horror classic completely rebuilt.", 18000.0, 2023, "cat4", "dev5", "pub5", "ds_poster.jpg", List.of("ds_1.jpg"), 110, true);
//        Game game67 = new Game("g67", "The Evil Within 2", "Lose yourself in a nightmare to save your daughter.", 12000.0, 2017, "cat4", "dev6", "pub6", "tew2_poster.jpg", List.of("tew2_1.jpg"), 35, true);
//        Game game68 = new Game("g68", "Alien: Isolation", "Discover the true meaning of fear.", 12000.0, 2014, "cat4", "dev11", "pub11", "alien_poster.jpg", List.of("alien_1.jpg"), 60, true);
//        Game game69 = new Game("g69", "Until Dawn", "Eight friends are trapped together.", 6000.0, 2015, "cat4", "dev8", "pub4", "ud_poster.jpg", List.of("ud_1.jpg"), 55, true);
//        Game game70 = new Game("g70", "Phasmophobia", "4 player online co-op psychological horror.", 4500.0, 2020, "cat4", "dev43", "pub38", "phasmo_poster.jpg", List.of("phasmo_1.jpg"), 150, true);
//
//        Game game71 = new Game("g71", "EA SPORTS FC 24", "The next chapter in The World's Game.", 21000.0, 2023, "cat7", "dev5", "pub5", "fc24_poster.jpg", List.of("fc24_1.jpg"), 300, true);
//        Game game72 = new Game("g72", "Forza Horizon 5", "Explore the vibrant landscapes of Mexico.", 18000.0, 2021, "cat7", "dev44", "pub15", "fh5_poster.jpg", List.of("fh5_1.jpg"), 180, true);
//        Game game73 = new Game("g73", "NBA 2K24", "Experience hoops culture in MyCAREER.", 21000.0, 2023, "cat7", "dev45", "pub12", "nba24_poster.jpg", List.of("nba24_1.jpg"), 200, true);
//        Game game74 = new Game("g74", "Gran Turismo 7", "The Real Driving Simulator.", 21000.0, 2022, "cat7", "dev8", "pub4", "gt7_poster.jpg", List.of("gt7_1.jpg"), 140, true);
//        Game game75 = new Game("g75", "F1 23", "Official video game of the 2023 FIA Formula One.", 21000.0, 2023, "cat7", "dev5", "pub5", "f123_poster.jpg", List.of("f1_1.jpg"), 90, true);
//        Game game76 = new Game("g76", "Madden NFL 24", "Experience the newest iteration of FieldSENSE.", 21000.0, 2023, "cat7", "dev5", "pub5", "madden_poster.jpg", List.of("madden_1.jpg"), 80, true);
//        Game game77 = new Game("g77", "Rocket League", "High-powered hybrid of arcade-style soccer.", 0.0, 2015, "cat7", "dev23", "pub21", "rl_poster.jpg", List.of("rl_1.jpg"), 999, true);
//        Game game78 = new Game("g78", "Need for Speed Unbound", "Race to the top, don't flop.", 21000.0, 2022, "cat7", "dev5", "pub5", "nfsu_poster.jpg", List.of("nfs_1.jpg"), 70, true);
//        Game game79 = new Game("g79", "DiRT Rally 2.0", "Carve your way through iconic rally locations.", 6000.0, 2019, "cat7", "dev5", "pub5", "dirt_poster.jpg", List.of("dirt_1.jpg"), 40, true);
//        Game game80 = new Game("g80", "WWE 2K23", "Even Stronger.", 18000.0, 2023, "cat7", "dev45", "pub12", "wwe_poster.jpg", List.of("wwe_1.jpg"), 85, true);
//
//        Game game81 = new Game("g81", "Sid Meier's Civilization VI", "Build an empire to stand the test of time.", 18000.0, 2016, "cat8", "dev46", "pub12", "civ6_poster.jpg", List.of("civ6_1.jpg"), 100, true);
//        Game game82 = new Game("g82", "Age of Empires IV", "Make History your story.", 12000.0, 2021, "cat8", "dev47", "pub15", "aoe4_poster.jpg", List.of("aoe4_1.jpg"), 70, true);
//        Game game83 = new Game("g83", "StarCraft II", "Command the terrans, protoss, and zerg.", 0.0, 2010, "cat8", "dev3", "pub3", "sc2_poster.jpg", List.of("sc2_1.jpg"), 999, true);
//        Game game84 = new Game("g84", "XCOM 2", "Earth has changed. The aliens rule.", 15000.0, 2016, "cat8", "dev46", "pub12", "xcom2_poster.jpg", List.of("xcom2_1.jpg"), 50, true);
//        Game game85 = new Game("g85", "Crusader Kings III", "Guide a royal dynasty through the centuries.", 15000.0, 2020, "cat8", "dev48", "pub16", "ck3_poster.jpg", List.of("ck3_1.jpg"), 80, true);
//        Game game86 = new Game("g86", "Total War: WARHAMMER III", "Rally your forces.", 18000.0, 2022, "cat8", "dev11", "pub11", "tw3_poster.jpg", List.of("tw3_1.jpg"), 90, true);
//        Game game87 = new Game("g87", "Stellaris", "Explore a vast galaxy full of wonder.", 12000.0, 2016, "cat8", "dev48", "pub16", "stellaris_poster.jpg", List.of("stell_1.jpg"), 60, true);
//        Game game88 = new Game("g88", "Hearts of Iron IV", "Take command of any nation in World War II.", 12000.0, 2016, "cat8", "dev48", "pub16", "hoi4_poster.jpg", List.of("hoi4_1.jpg"), 65, true);
//        Game game89 = new Game("g89", "Company of Heroes 3", "Heart-pounding combat.", 18000.0, 2023, "cat8", "dev11", "pub11", "coh3_poster.jpg", List.of("coh3_1.jpg"), 55, true);
//        Game game90 = new Game("g90", "Mario + Rabbids Kingdom Battle", "Two worlds collide.", 12000.0, 2017, "cat8", "dev2", "pub2", "mario_rabbids_poster.jpg", List.of("mrkb_1.jpg"), 70, true);
//
//        Game game91 = new Game("g91", "Persona 5 Royal", "Don the mask and join the Phantom Thieves of Hearts.", 18000.0, 2019, "cat5", "dev11", "pub11", "p5r_poster.jpg", List.of("p5r_1.jpg"), 100, true);
//        Game game92 = new Game("g92", "Elden Ring", "Rise, Tarnished.", 18000.0, 2022, "cat5", "dev49", "pub39", "elden_poster.jpg", List.of("elden_1.jpg"), 250, true);
//        Game game93 = new Game("g93", "Baldur's Gate 3", "Gather your party.", 18000.0, 2023, "cat5", "dev50", "pub40", "bg3_poster.jpg", List.of("bg3_1.jpg"), 300, true);
//        Game game94 = new Game("g94", "The Outer Worlds", "A single-player sci-fi RPG.", 9000.0, 2019, "cat5", "dev51", "pub12", "tow_poster.jpg", List.of("tow_1.jpg"), 60, true);
//        Game game95 = new Game("g95", "Horizon Forbidden West", "Brave an expansive open world.", 21000.0, 2022, "cat5", "dev8", "pub4", "hfw_poster.jpg", List.of("hfw_1.jpg"), 140, true);
//        Game game96 = new Game("g96", "Bloodborne", "Face your fears.", 6000.0, 2015, "cat5", "dev49", "pub4", "bb_poster.jpg", List.of("bb_1.jpg"), 80, true);
//        Game game97 = new Game("g97", "Monster Hunter: World", "Slay ferocious monsters.", 9000.0, 2018, "cat5", "dev9", "pub9", "mhw_poster.jpg", List.of("mhw_1.jpg"), 110, true);
//        Game game98 = new Game("g98", "NieR:Automata", "Battle to reclaim the machine-driven dystopia.", 12000.0, 2017, "cat5", "dev4", "pub8", "nier_poster.jpg", List.of("nier_1.jpg"), 70, true);
//        Game game99 = new Game("g99", "Diablo IV", "The ultimate action RPG experience.", 21000.0, 2023, "cat5", "dev3", "pub3", "d4_poster.jpg", List.of("d4_1.jpg"), 200, true);
//        Game game100 = new Game("g100", "Kingdom Hearts III", "Follow Sora and his friends.", 18000.0, 2019, "cat5", "dev4", "pub8", "kh3_poster.jpg", List.of("kh3_1.jpg"), 50, true);
//
//        List<Game> gameList = List.of(
//                game1, game2, game3, game4, game5, game6, game7, game8, game9, game10,
//                game11, game12, game13, game14, game15, game16, game17, game18, game19, game20,
//                game21, game22, game23, game24, game25, game26, game27, game28, game29, game30,
//                game31, game32, game33, game34, game35, game36, game37, game38, game39, game40,
//                game41, game42, game43, game44, game45, game46, game47, game48, game49, game50,
//                game51, game52, game53, game54, game55, game56, game57, game58, game59, game60,
//                game61, game62, game63, game64, game65, game66, game67, game68, game69, game70,
//                game71, game72, game73, game74, game75, game76, game77, game78, game79, game80,
//                game81, game82, game83, game84, game85, game86, game87, game88, game89, game90,
//                game91, game92, game93, game94, game95, game96, game97, game98, game99, game100
//        );
//        WriteBatch batch = db.batch();
//
//        for (Game g : gameList) {
//            DocumentReference ref = db.collection("games").document();
//            batch.set(ref, g);
//        }
//
//        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(getContext(), "Games added", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getContext(), "Games could not be added", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        if (catId != null){
            db.collection("games")
                    .whereEqualTo("categoryId", catId)
                    .orderBy("title", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(ds -> {
                        if (!ds.isEmpty()) {
                            List<Game> games = ds.toObjects(Game.class);

                            adapter = new ListingAdapter(games, game -> {



                            });

                            binding.recyclerGameView.setAdapter(adapter);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "Error"+e.getMessage());
                            Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            db.collection("games")
                    .orderBy("title", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(ds -> {
                        if (!ds.isEmpty()) {
                            List<Game> games = ds.toObjects(Game.class);

                            adapter = new ListingAdapter(games, game -> {

                            });

                            binding.recyclerGameView.setAdapter(adapter);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "Error"+e.getMessage());
                            Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}