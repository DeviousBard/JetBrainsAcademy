from dataclasses import dataclass

__all__ = [
    "game_logo", "robot_image", "frame_chars", "main_menu", "status", "upgrade_store_menu", "status_menu",
    "game_over", "game_loaded", "game_saved", "default_random_seed", "default_min_animation_duration",
    "default_max_animation_duration", "default_locations_str", "saved_games_file_name", "high_scores_file_name"
]

game_logo = r"""
██████╗░██╗░░░██╗░██████╗██╗░░██╗███████╗██████╗░░██████╗
██╔══██╗██║░░░██║██╔════╝██║░██╔╝██╔════╝██╔══██╗██╔════╝
██║░░██║██║░░░██║╚█████╗░█████═╝░█████╗░░██████╔╝╚█████╗░
██║░░██║██║░░░██║░╚═══██╗██╔═██╗░██╔══╝░░██╔══██╗░╚═══██╗
██████╔╝╚██████╔╝██████╔╝██║░╚██╗███████╗██║░░██║██████╔╝
╚═════╝░░╚═════╝░╚═════╝░╚═╝░░╚═╝╚══════╝╚═╝░░╚═╝╚═════╝░
"""

# Must be 20 characters wide by 7 characters high (i.e. The array must have 7 strings with exactly 20 characters each)
robot_image = [
    r"      ,     ,       ",
    r"     (\____/)       ",
    r"      (_oo_)        ",
    r"        (O)         ",
    r"      __||__    \)  ",
    r"   []/______\[] /   ",
    r"   / \______/ \/    ",
]


# Characters for drawing menu frames
@dataclass
class FrameChars:
    TOP_LEFT_CORNER: str = "║"
    TOP_RIGHT_CORNER: str = "║"
    BOTTOM_LEFT_CORNER: str = "║"
    BOTTOM_RIGHT_CORNER: str = "║"
    TOP_EDGE: str = "═"
    RIGHT_EDGE: str = "║"
    BOTTOM_EDGE: str = "═"
    LEFT_EDGE: str = "║"


frame_chars = FrameChars()

# Main Menu
main_menu = r"""
[New]  Game
[Load] Game
[High] Scores
[Help]
[Exit]
"""

# Status
status = [
    r"                 [Ex]plore                          [Up]grade                  ",
    r"                 [Save]                             [M]enu                     "
]

# Upgrade Store Menu
upgrade_store_menu = [
    r"         UPGRADE STORE        ",
    r"                        Price ",
    r"[1] Titanium Scan         250 ",
    r"[2] Enemy Encounter Scan  500 ",
    r"[3] New Robot            1000 ",
    r"                              ",
    r"[Back]                        "
]

# Menu on the status screen
status_menu = [
    r"           MENU         ",
    r"                        ",
    r"[Back] to game          ",
    r" Return to [Main] Menu  ",
    r"[Save] and exit         ",
    r"[Exit] game             "
]

# Game over message
game_over = [
    r"          GAME OVER!          "
]

# Game loaded message
game_loaded = [
    r"    GAME LOADED SUCCESSFULLY  "
]

# Game saved message
game_saved = [
    r"    GAME SAVED SUCCESSFULLY   "
]

# Default program arguments
default_random_seed = 10
default_min_animation_duration = 0
default_max_animation_duration = 0
default_locations_str = "High,street/Green,park/Destroyed,Arch"

# File names
saved_games_file_name = "./save_file.txt"
high_scores_file_name = "./high_scores.txt"
