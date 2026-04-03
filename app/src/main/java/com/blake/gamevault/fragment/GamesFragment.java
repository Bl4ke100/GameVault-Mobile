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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GamesFragment extends Fragment {

    private FragmentGamesBinding binding;
    private ListingAdapter adapter;
    private String catId;

    private List<Game> fullGameList = new ArrayList<>();
    private List<Game> displayedGameList = new ArrayList<>();


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
//        // Category 1: Action-Adventure
//        Game game1 = new Game("g1", "Grand Theft Auto V", "Explore the sprawling open world of Los Santos and Blaine County.", 9000.0, 2013, "cat1", "dev1", "pub1", "gta5_poster.jpg", List.of("gta5_1.jpg"), 100, true, 4.8f);
//        Game game2 = new Game("g2", "Red Dead Redemption 2", "An epic tale of life in America’s unforgiving heartland.", 18000.0, 2018, "cat1", "dev1", "pub1", "rdr2_poster.jpg", List.of("rdr2_1.jpg"), 150, true, 4.9f);
//        Game game3 = new Game("g3", "Ghost of Tsushima", "Forge a new path to protect Tsushima in this open-world adventure.", 18000.0, 2020, "cat1", "dev12", "pub4", "got_poster.jpg", List.of("got_1.jpg"), 90, true, 4.8f);
//        Game game4 = new Game("g4", "The Last of Us Part I", "Experience the emotional storytelling and unforgettable characters.", 21000.0, 2022, "cat1", "dev10", "pub4", "tlou_poster.jpg", List.of("tlou_1.jpg"), 80, true, 4.9f);
//        Game game5 = new Game("g5", "Uncharted 4: A Thief's End", "Nathan Drake's final globe-trotting adventure.", 12000.0, 2016, "cat1", "dev10", "pub4", "uc4_poster.jpg", List.of("uc4_1.jpg"), 60, true, 4.7f);
//        Game game6 = new Game("g6", "Marvel's Spider-Man", "Swing through the streets of Marvel's New York.", 15000.0, 2018, "cat1", "dev8", "pub4", "spidey_poster.jpg", List.of("spidey_1.jpg"), 70, true, 4.8f);
//        Game game7 = new Game("g7", "Death Stranding", "Deliver hope to a fractured world as Sam Bridges.", 15000.0, 2019, "cat1", "dev13", "pub4", "ds_poster.jpg", List.of("ds_1.jpg"), 90, true, 4.4f);
//        Game game8 = new Game("g8", "Watch Dogs", "Hack the city of Chicago as Aiden Pearce.", 6000.0, 2014, "cat1", "dev2", "pub2", "wd_poster.jpg", List.of("wd_1.jpg"), 40, true, 3.8f);
//        Game game9 = new Game("g9", "Assassin's Creed Mirage", "Experience the story of Basim in 9th-century Baghdad.", 15000.0, 2023, "cat1", "dev2", "pub2", "acm_poster.jpg", List.of("acm_1.jpg"), 60, true, 4.0f);
//        Game game10 = new Game("g10", "The Legend of Zelda: Tears of the Kingdom", "An epic adventure across the land and skies of Hyrule.", 21000.0, 2023, "cat1", "dev7", "pub7", "totk_poster.jpg", List.of("totk_1.jpg"), 150, true, 4.9f);
//
//        // Category 2: Battle Royale
//        Game game11 = new Game("g11", "Call of Duty: Warzone", "A massive free-to-play combat arena.", 0.0, 2020, "cat2", "dev3", "pub3", "wz_poster.jpg", List.of("wz_1.jpg"), 120, true, 4.2f);
//        Game game12 = new Game("g12", "Apex Legends", "Conquer with character in this free-to-play Hero shooter.", 0.0, 2019, "cat2", "dev5", "pub5", "apex_poster.jpg", List.of("apex_1.jpg"), 80, true, 4.5f);
//        Game game13 = new Game("g13", "Hyper Scape", "A futuristic, fast-paced urban battle royale.", 0.0, 2020, "cat2", "dev2", "pub2", "hyper_poster.jpg", List.of("hyper_1.jpg"), 40, true, 3.5f);
//        Game game14 = new Game("g14", "Tetris 99", "99 players. One winner. A massive puzzle battle.", 0.0, 2019, "cat2", "dev7", "pub7", "t99_poster.jpg", List.of("t99_1.jpg"), 2, true, 4.6f);
//        Game game15 = new Game("g15", "Super Mario Bros. 35", "Classic Mario gameplay turned into a 35-player battle royale.", 0.0, 2020, "cat2", "dev7", "pub7", "smb35_poster.jpg", List.of("smb35_1.jpg"), 5, true, 4.4f);
//        Game game16 = new Game("g16", "Pac-Man 99", "A chaotic 99-player retro arcade battle royale.", 0.0, 2021, "cat2", "dev7", "pub7", "pac99_poster.jpg", List.of("pac99_1.jpg"), 2, true, 4.3f);
//        Game game17 = new Game("g17", "F-Zero 99", "Classic high-speed racing meets 99-player survival.", 0.0, 2023, "cat2", "dev7", "pub7", "fzero_poster.jpg", List.of("fzero_1.jpg"), 3, true, 4.5f);
//        Game game18 = new Game("g18", "Final Fantasy VII: The First Soldier", "A high-octane battle royale set in Midgar.", 0.0, 2021, "cat2", "dev4", "pub8", "ff7fs_poster.jpg", List.of("ff7fs_1.jpg"), 10, true, 3.7f);
//        Game game19 = new Game("g19", "Battlefield V: Firestorm", "Battle royale completely reimagined for Battlefield.", 15000.0, 2019, "cat2", "dev5", "pub5", "bfv_poster.jpg", List.of("bfv_1.jpg"), 90, true, 3.8f);
//        Game game20 = new Game("g20", "Call of Duty: Mobile - BR", "The iconic battle royale experience on mobile.", 0.0, 2019, "cat2", "dev3", "pub3", "codm_poster.jpg", List.of("codm_1.jpg"), 15, true, 4.6f);
//
//        // Category 3: Shooting
//        Game game21 = new Game("g21", "Call of Duty: Modern Warfare II", "Drop into a global conflict with Task Force 141.", 21000.0, 2022, "cat3", "dev3", "pub3", "mw2_poster.jpg", List.of("mw2_1.jpg"), 150, true, 4.1f);
//        Game game22 = new Game("g22", "DOOM Eternal", "Become the Slayer and conquer demons across dimensions.", 15000.0, 2020, "cat3", "dev6", "pub6", "doom_poster.jpg", List.of("doom_1.jpg"), 60, true, 4.8f);
//        Game game23 = new Game("g23", "Far Cry 6", "Join a modern-day guerrilla revolution to liberate Yara.", 18000.0, 2021, "cat3", "dev2", "pub2", "fc6_poster.jpg", List.of("fc6_1.jpg"), 80, true, 4.0f);
//        Game game24 = new Game("g24", "Battlefield 2042", "A first-person shooter marking the return to all-out warfare.", 18000.0, 2021, "cat3", "dev5", "pub5", "bf2042_poster.jpg", List.of("bf_1.jpg"), 90, true, 3.6f);
//        Game game25 = new Game("g25", "Tom Clancy's Rainbow Six Siege", "Master the art of destruction and gadgetry in 5v5 action.", 9000.0, 2015, "cat3", "dev2", "pub2", "r6s_poster.jpg", List.of("r6s_1.jpg"), 70, true, 4.5f);
//        Game game26 = new Game("g26", "Wolfenstein II: The New Colossus", "An exhilarating adventure to fight the Nazi war machine.", 12000.0, 2017, "cat3", "dev6", "pub6", "wolf2_poster.jpg", List.of("wolf_1.jpg"), 50, true, 4.6f);
//        Game game27 = new Game("g27", "Splatoon 3", "Ink up the Splatlands in this 4v4 multiplayer shooter.", 18000.0, 2022, "cat3", "dev7", "pub7", "splat3_poster.jpg", List.of("splat_1.jpg"), 20, true, 4.7f);
//        Game game28 = new Game("g28", "The Division 2", "Lead a team of elite agents into a post-pandemic Washington DC.", 12000.0, 2019, "cat3", "dev2", "pub2", "div2_poster.jpg", List.of("div2_1.jpg"), 90, true, 4.2f);
//        Game game29 = new Game("g29", "Star Wars Battlefront II", "Embark on an endless Star Wars action experience.", 15000.0, 2017, "cat3", "dev5", "pub5", "swbf2_poster.jpg", List.of("swbf2_1.jpg"), 80, true, 4.1f);
//        Game game30 = new Game("g30", "Titanfall 2", "Call down your Titan and get ready for an exhilarating shooter.", 9000.0, 2016, "cat3", "dev5", "pub5", "tf2_poster.jpg", List.of("tf2_1.jpg"), 45, true, 4.9f);
//
//        // Category 4: Horror
//        Game game31 = new Game("g31", "Resident Evil 4 Remake", "Survival is just the beginning in this modernized horror classic.", 18000.0, 2023, "cat4", "dev9", "pub9", "re4_poster.jpg", List.of("re4_1.jpg"), 80, true, 4.9f);
//        Game game32 = new Game("g32", "Resident Evil Village", "Experience survival horror like never before.", 15000.0, 2021, "cat4", "dev9", "pub9", "rev_poster.jpg", List.of("rev_1.jpg"), 50, true, 4.7f);
//        Game game33 = new Game("g33", "The Evil Within 2", "Descend into a terrifying nightmare to save your daughter.", 12000.0, 2017, "cat4", "dev6", "pub6", "tew2_poster.jpg", List.of("tew2_1.jpg"), 40, true, 4.5f);
//        Game game34 = new Game("g34", "Dead Space", "A sci-fi survival horror classic rebuilt from the ground up.", 18000.0, 2023, "cat4", "dev5", "pub5", "ds_poster.jpg", List.of("ds_1.jpg"), 60, true, 4.8f);
//        Game game35 = new Game("g35", "Alien: Isolation", "Discover the true meaning of fear in a deadly game of survival.", 12000.0, 2014, "cat4", "dev11", "pub11", "alien_poster.jpg", List.of("alien_1.jpg"), 40, true, 4.7f);
//        Game game36 = new Game("g36", "Until Dawn", "Eight friends are trapped together in a remote mountain retreat.", 9000.0, 2015, "cat4", "dev8", "pub4", "ud_poster.jpg", List.of("ud_1.jpg"), 50, true, 4.4f);
//        Game game37 = new Game("g37", "Bloodborne", "Face your fears as you search for answers in the ancient city of Yharnam.", 12000.0, 2015, "cat4", "dev8", "pub4", "bb_poster.jpg", List.of("bb_1.jpg"), 60, true, 4.9f);
//        Game game38 = new Game("g38", "Resident Evil 7: Biohazard", "Fear and isolation seep through the walls of an abandoned farmhouse.", 12000.0, 2017, "cat4", "dev9", "pub9", "re7_poster.jpg", List.of("re7_1.jpg"), 40, true, 4.6f);
//        Game game39 = new Game("g39", "ZombiU", "London is falling. How long will you survive the undead?", 6000.0, 2012, "cat4", "dev2", "pub2", "zombi_poster.jpg", List.of("zombi_1.jpg"), 30, true, 3.8f);
//        Game game40 = new Game("g40", "The Last of Us Part II", "A harrowing journey of survival and retribution.", 21000.0, 2020, "cat4", "dev10", "pub4", "tlou2_poster.jpg", List.of("tlou2_1.jpg"), 90, true, 4.8f);
//
//        // Category 5: RPG
//        Game game41 = new Game("g41", "The Witcher 3: Wild Hunt", "A story-driven, next-generation open-world role-playing game.", 12000.0, 2015, "cat5", "dev14", "pub14", "witcher3_poster.jpg", List.of("w3_img1.jpg"), 120, true, 4.9f);
//        Game game42 = new Game("g42", "Cyberpunk 2077", "An open-world, action-adventure RPG set in the megalopolis of Night City.", 18000.0, 2020, "cat5", "dev14", "pub14", "cp2077_poster.jpg", List.of("cp_img1.jpg"), 100, true, 4.5f);
//        Game game43 = new Game("g43", "The Elder Scrolls V: Skyrim", "An epic fantasy open-world RPG.", 12000.0, 2011, "cat5", "dev6", "pub6", "skyrim_poster.jpg", List.of("skyrim_img1.jpg"), 150, true, 4.8f);
//        Game game44 = new Game("g44", "Fallout 4", "Explore a massive post-apocalyptic open world.", 9000.0, 2015, "cat5", "dev6", "pub6", "fallout4_poster.jpg", List.of("fo4_img1.jpg"), 100, true, 4.4f);
//        Game game45 = new Game("g45", "Final Fantasy VII Remake", "A spectacular reimagining of one of the most visionary games ever.", 21000.0, 2020, "cat5", "dev4", "pub8", "ff7r_poster.jpg", List.of("ff7_img1.jpg"), 80, true, 4.7f);
//        Game game46 = new Game("g46", "Mass Effect Legendary Edition", "Relive the cinematic saga of Commander Shepard.", 18000.0, 2021, "cat5", "dev5", "pub5", "mass_effect_poster.jpg", List.of("me_img1.jpg"), 120, true, 4.8f);
//        Game game47 = new Game("g47", "Dragon Age: Inquisition", "Lead a team of heroes in a perilous journey through Thedas.", 12000.0, 2014, "cat5", "dev5", "pub5", "dai_poster.jpg", List.of("dai_img1.jpg"), 80, true, 4.3f);
//        Game game48 = new Game("g48", "Persona 5 Royal", "Don the mask and join the Phantom Thieves of Hearts.", 18000.0, 2019, "cat5", "dev11", "pub11", "p5r_poster.jpg", List.of("p5r_1.jpg"), 120, true, 4.9f);
//        Game game49 = new Game("g49", "Yakuza: Like a Dragon", "A dynamic turn-based RPG set in modern-day Japan.", 18000.0, 2020, "cat5", "dev11", "pub11", "yakuza_poster.jpg", List.of("yak_img1.jpg"), 60, true, 4.6f);
//        Game game50 = new Game("g50", "Starfield", "Create any character you want and explore the stars with unparalleled freedom.", 21000.0, 2023, "cat5", "dev6", "pub6", "starfield_poster.jpg", List.of("sf_img1.jpg"), 140, true, 3.9f);
//
//        // Category 6: Survival
//        Game game51 = new Game("g51", "Fallout 76", "Work together, or not, to survive in a post-nuclear wasteland.", 12000.0, 2018, "cat6", "dev6", "pub6", "fo76_poster.jpg", List.of("fo76_1.jpg"), 90, true, 3.8f);
//        Game game52 = new Game("g52", "Days Gone", "Ride and fight into a deadly, post-pandemic America.", 15000.0, 2019, "cat6", "dev8", "pub4", "days_poster.jpg", List.of("days_1.jpg"), 70, true, 4.3f);
//        Game game53 = new Game("g53", "I Am Alive", "Survive the ruined streets of Chicago after a devastating event.", 6000.0, 2012, "cat6", "dev2", "pub2", "iamalive_poster.jpg", List.of("iaa_1.jpg"), 15, true, 3.5f);
//        Game game54 = new Game("g54", "Metal Gear Survive", "A spin-off survival action game set in an alternate universe.", 9000.0, 2018, "cat6", "dev13", "pub13", "mgs_poster.jpg", List.of("mgs_1.jpg"), 40, true, 2.9f);
//        Game game55 = new Game("g55", "Resident Evil 0", "Survive the origins of the nightmare.", 6000.0, 2002, "cat6", "dev9", "pub9", "re0_poster.jpg", List.of("re0_1.jpg"), 20, true, 4.0f);
//        Game game56 = new Game("g56", "Resident Evil (1996)", "The survival horror classic that started it all.", 3000.0, 1996, "cat6", "dev9", "pub9", "re1_poster.jpg", List.of("re1_1.jpg"), 5, true, 4.6f);
//        Game game57 = new Game("g57", "Tokyo Jungle", "Fight for survival as an animal in a post-apocalyptic Tokyo.", 6000.0, 2012, "cat6", "dev8", "pub4", "tokyo_poster.jpg", List.of("tj_1.jpg"), 10, true, 4.1f);
//        Game game58 = new Game("g58", "Far Cry Primal", "Survive the perilous Stone Age.", 12000.0, 2016, "cat6", "dev2", "pub2", "fcp_poster.jpg", List.of("fcp_1.jpg"), 50, true, 4.0f);
//        Game game59 = new Game("g59", "Dead Rising", "Survive a shopping mall swarming with the undead.", 6000.0, 2006, "cat6", "dev9", "pub9", "dr1_poster.jpg", List.of("dr1_1.jpg"), 15, true, 4.5f);
//        Game game60 = new Game("g60", "Dead Rising 2", "Tape it or die in Fortune City.", 6000.0, 2010, "cat6", "dev9", "pub9", "dr2_poster.jpg", List.of("dr2_1.jpg"), 20, true, 4.4f);
//
//        // Category 7: Sports & Racing
//        Game game61 = new Game("g61", "EA SPORTS FC 24", "The next chapter in The World's Game.", 21000.0, 2023, "cat7", "dev5", "pub5", "fc24_poster.jpg", List.of("fc24_1.jpg"), 100, true, 3.9f);
//        Game game62 = new Game("g62", "Madden NFL 24", "Experience the newest iteration of FieldSENSE.", 21000.0, 2023, "cat7", "dev5", "pub5", "madden_poster.jpg", List.of("madden_1.jpg"), 80, true, 3.5f);
//        Game game63 = new Game("g63", "F1 23", "Be the last to brake in the official video game of the 2023 FIA Formula One.", 21000.0, 2023, "cat7", "dev5", "pub5", "f123_poster.jpg", List.of("f1_1.jpg"), 90, true, 4.4f);
//        Game game64 = new Game("g64", "Need for Speed Unbound", "Race to the top, don't flop. Outsmart the cops.", 21000.0, 2022, "cat7", "dev5", "pub5", "nfsu_poster.jpg", List.of("nfs_1.jpg"), 60, true, 4.1f);
//        Game game65 = new Game("g65", "Gran Turismo 7", "The ultimate Real Driving Simulator.", 21000.0, 2022, "cat7", "dev8", "pub4", "gt7_poster.jpg", List.of("gt7_1.jpg"), 120, true, 4.6f);
//        Game game66 = new Game("g66", "Mario Kart 8 Deluxe", "Race and battle your friends in the definitive version of Mario Kart 8.", 18000.0, 2017, "cat7", "dev7", "pub7", "mk8_poster.jpg", List.of("mk8_1.jpg"), 15, true, 4.9f);
//        Game game67 = new Game("g67", "The Crew Motorfest", "Grab your ticket to the ultimate car festival in Hawaii.", 21000.0, 2023, "cat7", "dev2", "pub2", "crew_poster.jpg", List.of("crew_1.jpg"), 85, true, 4.3f);
//        Game game68 = new Game("g68", "Riders Republic", "Jump into the massive multiplayer playground of extreme sports.", 15000.0, 2021, "cat7", "dev2", "pub2", "riders_poster.jpg", List.of("riders_1.jpg"), 60, true, 4.2f);
//        Game game69 = new Game("g69", "FIFA 23", "The culmination of EA's legendary football franchise.", 18000.0, 2022, "cat7", "dev5", "pub5", "fifa23_poster.jpg", List.of("fifa_1.jpg"), 100, true, 4.0f);
//        Game game70 = new Game("g70", "Mario Strikers: Battle League", "Strike, tackle, and score in 5-on-5 soccer with no rules.", 18000.0, 2022, "cat7", "dev7", "pub7", "strikers_poster.jpg", List.of("strikers_1.jpg"), 10, true, 3.8f);
//
//        // Category 8: Strategy
//        Game game71 = new Game("g71", "Mario + Rabbids Kingdom Battle", "Team up with Mario and the Rabbids in turn-based combat.", 12000.0, 2017, "cat8", "dev2", "pub2", "mario_rabbids_poster.jpg", List.of("mrkb_1.jpg"), 15, true, 4.5f);
//        Game game72 = new Game("g72", "Anno 1800", "Lead the Industrial Revolution in this city-building RTS.", 18000.0, 2019, "cat8", "dev2", "pub2", "anno_poster.jpg", List.of("anno_1.jpg"), 50, true, 4.6f);
//        Game game73 = new Game("g73", "Fire Emblem: Three Houses", "Shape the future of a continent on the verge of war.", 18000.0, 2019, "cat8", "dev7", "pub7", "fe3h_poster.jpg", List.of("fe3h_1.jpg"), 80, true, 4.8f);
//        Game game74 = new Game("g74", "Pikmin 4", "Command a capable crop of tiny, plant-like creatures.", 18000.0, 2023, "cat8", "dev7", "pub7", "pikmin_poster.jpg", List.of("pikmin_1.jpg"), 20, true, 4.7f);
//        Game game75 = new Game("g75", "Company of Heroes 3", "The ultimate package of action, tactics, and strategy.", 18000.0, 2023, "cat8", "dev11", "pub11", "coh3_poster.jpg", List.of("coh3_1.jpg"), 60, true, 4.1f);
//        Game game76 = new Game("g76", "Total War: WARHAMMER III", "Rally your forces and step into the Realm of Chaos.", 18000.0, 2022, "cat8", "dev11", "pub11", "tw3_poster.jpg", List.of("tw3_1.jpg"), 120, true, 4.4f);
//        Game game77 = new Game("g77", "Humankind", "Rewrite the entire narrative of human history.", 15000.0, 2021, "cat8", "dev11", "pub11", "human_poster.jpg", List.of("human_1.jpg"), 40, true, 3.9f);
//        Game game78 = new Game("g78", "Triangle Strategy", "A tactical RPG where your choices impact the story.", 18000.0, 2022, "cat8", "dev4", "pub8", "tri_poster.jpg", List.of("tri_1.jpg"), 60, true, 4.4f);
//        Game game79 = new Game("g79", "Command & Conquer Remastered Collection", "The genre-defining RTS games completely rebuilt.", 9000.0, 2020, "cat8", "dev5", "pub5", "cnc_poster.jpg", List.of("cnc_1.jpg"), 35, true, 4.6f);
//        Game game80 = new Game("g80", "Valkyria Chronicles 4", "A unique mix of turn-based strategy, RPG, and real-time 3rd person shooter.", 12000.0, 2018, "cat8", "dev11", "pub11", "vc4_poster.jpg", List.of("vc4_1.jpg"), 45, true, 4.5f);
//
//        // Category 9: Simulation
//        Game game81 = new Game("g81", "The Sims 4", "Play with life and create your own unique world.", 0.0, 2014, "cat9", "dev5", "pub5", "sims4_poster.jpg", List.of("sims4_1.jpg"), 60, true, 4.2f);
//        Game game82 = new Game("g82", "Animal Crossing: New Horizons", "Escape to a deserted island and create your own paradise.", 18000.0, 2020, "cat9", "dev7", "pub7", "acnh_poster.jpg", List.of("acnh_1.jpg"), 20, true, 4.8f);
//        Game game83 = new Game("g83", "Nintendogs + Cats", "Interact with adorable puppies and kittens.", 12000.0, 2011, "cat9", "dev7", "pub7", "nint_poster.jpg", List.of("nint_1.jpg"), 5, true, 4.5f);
//        Game game84 = new Game("g84", "SimCity", "Build, manage, and evolve your dream city.", 9000.0, 2013, "cat9", "dev5", "pub5", "simcity_poster.jpg", List.of("simcity_1.jpg"), 15, true, 3.4f);
//        Game game85 = new Game("g85", "Star Wars: Squadrons", "Master the art of starfighter combat in the authentic piloting experience.", 12000.0, 2020, "cat9", "dev5", "pub5", "squad_poster.jpg", List.of("squad_1.jpg"), 40, true, 4.2f);
//        Game game86 = new Game("g86", "Football Manager 2024", "Step into the shoes of a real manager and write your own football story.", 18000.0, 2023, "cat9", "dev11", "pub11", "fm24_poster.jpg", List.of("fm24_1.jpg"), 15, true, 4.7f);
//        Game game87 = new Game("g87", "Two Point Campus", "Build the university of your dreams in this management sim.", 12000.0, 2022, "cat9", "dev11", "pub11", "tpc_poster.jpg", List.of("tpc_1.jpg"), 20, true, 4.3f);
//        Game game88 = new Game("g88", "Two Point Hospital", "Design stunning hospitals and cure peculiar illnesses.", 12000.0, 2018, "cat9", "dev11", "pub11", "tph_poster.jpg", List.of("tph_1.jpg"), 15, true, 4.4f);
//        Game game89 = new Game("g89", "Silent Hunter 5: Battle of the Atlantic", "Take command of a German U-boat in this immersive naval simulator.", 6000.0, 2010, "cat9", "dev2", "pub2", "sh5_poster.jpg", List.of("sh5_1.jpg"), 10, true, 3.5f);
//        Game game90 = new Game("g90", "Spore", "Evolve a creature from a single cell to a galactic traveler.", 9000.0, 2008, "cat9", "dev5", "pub5", "spore_poster.jpg", List.of("spore_1.jpg"), 12, true, 4.0f);
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
//                game81, game82, game83, game84, game85, game86, game87, game88, game89, game90
//        );
//
//        WriteBatch batch = db.batch();
//
//        for (Game g : gameList) {
//            DocumentReference ref = db.collection("games").document();
//            batch.set(ref, g);
//        }
//
//        batch.commit().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Toast.makeText(getContext(), "Games added with ratings", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getContext(), "Failed to add games", Toast.LENGTH_SHORT).show();
//            }
//        });


//        db.collection("games").get().addOnSuccessListener(queryDocumentSnapshots -> {
//            WriteBatch batch = db.batch();
//
//            // Create the attributes array
//            List<Map<String, Object>> attributes = new ArrayList<>();
//            Map<String, Object> platform = new HashMap<>();
//            platform.put("name", "Platform");
//            platform.put("type", "text");
//            platform.put("values", Arrays.asList("PC", "PS5", "Xbox"));
//            attributes.add(platform);
//
//            // Add the update operation for every game to the batch
//            for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                batch.update(doc.getReference(), "attributes", attributes);
//            }
//
//            // Commit the batch
//            batch.commit().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Toast.makeText(getContext(), "All 90 games updated!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
//                }
//            });
//        });


        //Repeated games cleaner
        db.collection("games").get().addOnSuccessListener(queryDocumentSnapshots -> {

            if (!isAdded() || binding == null) return;

            WriteBatch batch = db.batch();
            Set<String> seenTitles = new HashSet<>();
            int deleteCount = 0;

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                String title = doc.getString("title");

                if (title != null) {
                    if (seenTitles.contains(title)) {
                        // It's a duplicate, mark it for deletion
                        batch.delete(doc.getReference());
                        deleteCount++;
                    } else {
                        // First time seeing this title, add it to our tracking set
                        seenTitles.add(title);
                    }
                }
            }

            if (deleteCount > 0) {
                final int finalCount = deleteCount;
                batch.commit().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i("Games Fragment", "Cleaned up " + finalCount + " duplicates!");
                    } else {
                        Log.i("Games Fragment", "Failed to delete duplicates");
                    }
                });
            } else {
                Log.i("Games Fragment", "No duplicates found!");
            }
        });


        if (catId != null) {
            db.collection("games")
                    .whereEqualTo("categoryId", catId)
                    .orderBy("title", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(ds -> {
                        if (!ds.isEmpty()) {
                            List<Game> games = ds.toObjects(Game.class);

                            adapter = new ListingAdapter(games, game -> {

                                Bundle bundle = new Bundle();
                                bundle.putSerializable("gameId", game.getGameId());
                                bundle.putSerializable("catId", game.getCategoryId());

                                GameDetailFragment gameDetailFragment = new GameDetailFragment();
                                gameDetailFragment.setArguments(bundle);

                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainer, gameDetailFragment)
                                        .addToBackStack(null)
                                        .commit();

                            });

                            binding.recyclerGameView.setAdapter(adapter);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "Error" + e.getMessage());
                            Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            db.collection("games")
                    .orderBy("title", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(ds -> {
                        if (!ds.isEmpty()) {
                            List<Game> games = ds.toObjects(Game.class);

                            adapter = new ListingAdapter(games, game -> {

                                Bundle bundle = new Bundle();
                                bundle.putSerializable("gameId", game.getGameId());
                                bundle.putString("catId", game.getCategoryId());

                                GameDetailFragment gameDetailFragment = new GameDetailFragment();
                                gameDetailFragment.setArguments(bundle);

                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainer, gameDetailFragment)
                                        .addToBackStack(null)
                                        .commit();

                            });

                            binding.recyclerGameView.setAdapter(adapter);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "Error" + e.getMessage());
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

        requireActivity().findViewById(R.id.toolBar).setVisibility(View.GONE);

        binding.recyclerGameView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        binding.btnGamesBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        binding.btnSortGames.setOnClickListener(v -> {
            String[] options = {"Price: Low to High", "Price: High to Low", "Title: A-Z"};
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle("Sort By")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            displayedGameList.sort((g1, g2) -> Double.compare(g1.getPrice(), g2.getPrice()));
                        } else if (which == 1) {
                            displayedGameList.sort((g1, g2) -> Double.compare(g2.getPrice(), g1.getPrice()));
                        } else if (which == 2) {
                            displayedGameList.sort((g1, g2) -> g1.getTitle().compareToIgnoreCase(g2.getTitle()));
                        }
                        adapter.notifyDataSetChanged();
                    }).show();
        });

        // 4. Setup Local Search Filter
        binding.gamesLocalSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String query = s.toString().toLowerCase().trim();
                displayedGameList.clear();

                if (query.isEmpty()) {
                    displayedGameList.addAll(fullGameList);
                } else {
                    for (Game game : fullGameList) {
                        if (game.getTitle().toLowerCase().contains(query)) {
                            displayedGameList.add(game);
                        }
                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // ... Keep your existing Duplicate Cleaner code here ...

        // 5. Update your Firestore fetch calls to populate the lists
        Query query = (catId != null)
                ? db.collection("games").whereEqualTo("categoryId", catId).orderBy("title", Query.Direction.ASCENDING)
                : db.collection("games").orderBy("title", Query.Direction.ASCENDING);

        query.get().addOnSuccessListener(ds -> {
            if (!isAdded() || binding == null) return;
            if (!ds.isEmpty()) {
                fullGameList = ds.toObjects(Game.class);
                displayedGameList.clear();
                displayedGameList.addAll(fullGameList);

                adapter = new ListingAdapter(displayedGameList, game -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("gameId", game.getGameId());
                    bundle.putString("catId", game.getCategoryId());

                    GameDetailFragment gameDetailFragment = new GameDetailFragment();
                    gameDetailFragment.setArguments(bundle);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, gameDetailFragment)
                            .addToBackStack(null)
                            .commit();
                });
                binding.recyclerGameView.setAdapter(adapter);
            }
        }).addOnFailureListener(e -> {
            if (isAdded() && getContext() != null) {
                Log.e("Firestore", "Error" + e.getMessage());
                Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        // 6. Restore Toolbar when leaving
        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().findViewById(R.id.toolBar).setVisibility(View.VISIBLE);
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            getActivity().findViewById(R.id.toolBar).setVisibility(View.VISIBLE);
        }
        binding = null;}
}