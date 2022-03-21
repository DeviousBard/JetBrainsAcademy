import random


class Player:
    def __init__(self, player_number):
        self.player_number = player_number

    def make_move(self, board):
        pass


class HumanPlayer(Player):
    def __init__(self, player_number):
        super().__init__(player_number)

    @staticmethod
    def translate_coordinates(coords):
        return [3 - coords[1], coords[0] - 1]

    def make_move(self, tic_tac_toe_board):
        coords = []
        while len(coords) != 2:
            coords_string = input("Enter the coordinates: ").split()
            if not (coords_string[0].isdigit() and coords_string[1].isdigit()):
                print("You should enter numbers!")
            else:
                coords = HumanPlayer.translate_coordinates([int(x) for x in coords_string])
                if coords[0] < 0 or coords[0] > 2 or coords[1] < 0 or coords[1] > 2:
                    print("Coordinates should be from 1 to 3!")
                    coords = []
                elif tic_tac_toe_board.get_cell(coords) != 2:
                    print("This cell is occupied! Choose another one!")
                    coords = []
        tic_tac_toe_board.set_cell(coords, self.player_number)


class RobotPlayer(Player):
    def __init__(self, player_number, level):
        super().__init__(player_number)
        self.level = level

    def make_move(self, tic_tac_toe_board):
        super().make_move(tic_tac_toe_board)


class EasyRobotPlayer(RobotPlayer):
    def __init__(self, player_number, level="easy"):
        super().__init__(player_number, level)

    def make_move(self, tic_tac_toe_board):
        print(f'Making move level "{self.level}"')
        x = random.randint(0, 2)
        y = random.randint(0, 2)
        while tic_tac_toe_board.get_cell([x, y]) != 2:
            x = random.randint(0, 2)
            y = random.randint(0, 2)
        tic_tac_toe_board.set_cell([x, y], self.player_number)


class MediumRobotPlayer(EasyRobotPlayer):
    def __init__(self, player_number, level="medium"):
        super().__init__(player_number, level)

    def make_move(self, tic_tac_toe_board):
        print(f'Making move level "{self.level}"')
        coord = tic_tac_toe_board.has_winning_move(self.player_number)
        if not(coord is None):
            tic_tac_toe_board.set_cell(coord, self.player_number)
        else:
            coord = tic_tac_toe_board.has_winning_move((self.player_number + 1) % 2)
            if not(coord is None):
                tic_tac_toe_board.set_cell(coord, self.player_number)
            else:
                super().make_move(tic_tac_toe_board)


class HardRobotPlayer(MediumRobotPlayer):
    def __init__(self, player_number, level="hard"):
        super().__init__(player_number, level)

    def make_move(self, tic_tac_toe_board):
        # print(f'Making move level "{self.level}"')
        super().make_move(tic_tac_toe_board)


class TicTacToeBoard:
    pieces = ["X", "O", " "]
    total_moves = 0

    def __init__(self):
        self.board = [[2, 2, 2], [2, 2, 2], [2, 2, 2]]

    def print(self):
        print("---------")
        for i in range(3):
            row = "| "
            for j in range(3):
                row += self.pieces[self.board[i][j]] + " "
            row += "|"
            print(row)
        print("---------")

    def count_player_pieces(self, player_number):
        return sum([self.board[i].count(player_number) for i in range(3)])

    def game_over(self):
        ended = False
        x_count = self.count_player_pieces(0)
        o_count = self.count_player_pieces(1)
        x_wins = self.is_winner(0)
        o_wins = self.is_winner(1)
        if abs(x_count - o_count) > 1 or (x_wins and o_wins):
            print("Impossible")
            ended = True
        elif x_wins:
            print("X wins")
            ended = True
        elif o_wins:
            print("O wins")
            ended = True
        elif x_count + o_count == 9:
            print("Draw")
            ended = True
        return ended

    def is_winner(self, player_number):
        left_diagonal = [self.board[0][0], self.board[1][1], self.board[2][2]]
        right_diagonal = [self.board[0][2], self.board[1][1], self.board[2][0]]
        if right_diagonal.count(player_number) == 3 or left_diagonal.count(player_number) == 3:
            return True
        else:
            for i in range(3):
                col = [self.board[j][i] for j in range(3)]
                if self.board[i].count(player_number) == 3 or col.count(player_number) == 3:
                    return True
        return False

    def get_cell(self, coords):
        return self.board[coords[0]][coords[1]]

    def set_cell(self, coords, player_number):
        self.board[coords[0]][coords[1]] = player_number

    def has_winning_move(self, player):
        for x in range(3):
            for y in range(3):
                coord = [x, y]
                if self.is_winning_cell(player, coord):
                    return coord

    def is_winning_cell(self, player, coord):
        x = coord[0]
        y = coord[1]
        winning_cell = None
        if self.board[x][y] == 2:
            # check the row the cell is in
            row = self.board[x]
            if row.count(player) == 2 and row.count(2) == 1:
                return coord

            # check the column the cell is in
            col = [self.board[i][y] for i in range(3)]
            if col.count(player) == 2 and col.count(2) == 1:
                return coord

            if abs(x - y) == 0:
                if self.check_left_diagonal(player):
                    return coord
            elif abs(x - y) == 2 or x == y == 1:
                if self.check_right_diagonal(player):
                    return coord

        return winning_cell

    def check_left_diagonal(self, player):
        diag = [self.board[0][0], self.board[1][1], self.board[2][2]]
        if diag.count(player) == 2 and diag.count(2) == 1:
            return True
        return False

    def check_right_diagonal(self, player):
        diag = [self.board[0][2], self.board[1][1], self.board[2][0]]
        if diag.count(player) == 2 and diag.count(2) == 1:
            return True
        return False


def has_bad_parameters(game_params):
    valid_players = ["easy", "medium", "hard", "user"]
    if game_params.startswith("start"):
        players = game_params.split()
        if len(players) != 3:
            return True
        if not(players[1] in valid_players) and not(players[2] in valid_players):
            return True
    elif not (game_params.startswith("exit")):
        return True
    return False


def get_game_parameters():
    game_params = ""
    while has_bad_parameters(game_params):
        game_params = input("Input command: ")
    return game_params.split()


def get_players(game_param, player_number):
    if game_param == 'user':
        return HumanPlayer(player_number)
    if game_param == 'easy':
        return EasyRobotPlayer(player_number)
    if game_param == 'medium':
        return MediumRobotPlayer(player_number)
    if game_param == 'hard':
        return HardRobotPlayer(player_number)
    return None


def main():
    # random.seed(3)
    while True:
        game_params = get_game_parameters()

        if len(game_params) == 1:
            break

        players = [get_players(game_params[1], 0), get_players(game_params[2], 1)]
        tic_tac_toe_board = TicTacToeBoard()
        current_player = 0
        tic_tac_toe_board.print()
        while not(tic_tac_toe_board.game_over()):
            TicTacToeBoard.total_moves += 1
            players[current_player].make_move(tic_tac_toe_board)
            tic_tac_toe_board.print()
            current_player = (current_player + 1) % 2
        print()


main()
