import math
import random


class KnightsTour:

    def __init__(self):
        self.board = {}
        self.board_dimensions = None
        self.cell_size = None
        self.max_number_width = None
        self.starting_position = None
        self.current_position = None
        self.game_over = 0

    def ask_for_board_dimensions(self):
        while True:
            dimensions_yx = input("Enter your board dimensions:").split()
            if len(dimensions_yx) == 2 and dimensions_yx[0].isdigit() and dimensions_yx[1].isdigit() \
                    and int(dimensions_yx[0]) > 0 and int(dimensions_yx[1]) > 0:
                self.board_dimensions = (int(dimensions_yx[0]), int(dimensions_yx[1]))
                self.cell_size = len(str(self.board_dimensions[0] * self.board_dimensions[1]))
                self.max_number_width = max(len(dimensions_yx[0]), len(dimensions_yx[1]))
                break
            else:
                print('Invalid dimensions!')

    def setup_board(self):
        for x in range(1, self.board_dimensions[1] + 1):
            for y in range(1, self.board_dimensions[0] + 1):
                self.board[(y, x)] = '_'

    def ask_for_starting_position(self):
        while True:
            starting_yx = input("Enter the knight's starting position:").split()
            if len(starting_yx) == 2 and starting_yx[0].isdigit() and starting_yx[1].isdigit() \
                    and 1 <= int(starting_yx[0]) <= self.board_dimensions[0] \
                    and 1 <= int(starting_yx[1]) <= self.board_dimensions[1]:
                self.board[(int(starting_yx[0]), int(starting_yx[1]))] = 'X'
                self.starting_position = (int(starting_yx[0]), int(starting_yx[1]))
                break
            else:
                print('Invalid dimensions!')

    def print_board(self):
        row_len = self.board_dimensions[0] * (self.cell_size + 1) + 3
        print(f"{' ' * self.max_number_width}{'-' * row_len}")
        for x in range(self.board_dimensions[1], 0, -1):
            row = ""
            row += f'{" " * (self.max_number_width - len(str(x))) + str(x)}| '
            for y in range(1, self.board_dimensions[0] + 1):
                cell_value = ("_" * self.cell_size)
                if self.board[(y, x)] != '_':
                    cell_value = (" " * (self.cell_size - 1) + self.board[(y, x)])
                row += cell_value
                row += " "
            row += "|"
            print(row)

        print(f"{' ' * self.max_number_width}{'-' * row_len}")
        numbers = f"{' ' * self.max_number_width} "
        for i in range(1, self.board_dimensions[0] + 1):
            numbers += f"{' '}{(' ' * (self.cell_size - len(str(i))))}{i}"
        print(numbers)

    def count_possible_moves(self, position):
        count = 0
        possible_moves = [(2, 1), (2, -1), (-2, 1), (-2, -1), (1, 2), (1, -2), (-1, 2), (-1, -2)]
        for possible_move in possible_moves:
            y = position[0] + possible_move[0]
            x = position[1] + possible_move[1]
            if 1 <= y <= self.board_dimensions[0] and 1 <= x <= self.board_dimensions[1] and self.board[(y, x)] != 'X' \
                    and self.board[(y, x)] != '*':
                count += 1
        return count

    def reset_counts(self, position):
        possible_moves = [(2, 1), (2, -1), (-2, 1), (-2, -1), (1, 2), (1, -2), (-1, 2), (-1, -2)]
        for possible_move in possible_moves:
            y = position[0] + possible_move[0]
            x = position[1] + possible_move[1]
            if 1 <= y <= self.board_dimensions[0] and 1 <= x <= self.board_dimensions[1] and self.board[(y, x)].isdigit():
                self.board[(y, x)] = '_'

    def get_next_move(self, position):
        invalid_text = ""
        while True:
            move_yx = input(f"\n{invalid_text}Enter your next move:").split()
            invalid_text = "Invalid move! "
            if len(move_yx) == 2 and move_yx[0].isdigit() and move_yx[1].isdigit() \
                    and 1 <= int(move_yx[0]) <= self.board_dimensions[0] \
                    and 1 <= int(move_yx[1]) <= self.board_dimensions[1]:
                y = int(move_yx[0])
                x = int(move_yx[1])
                if self.board[(y, x)].isdigit():
                    self.reset_counts(position)
                    self.board[position] = "*"
                    self.board[(y, x)] = "X"
                    break
        return y, x

    def find_possible_moves(self, position):
        possible_moves = [(2, 1), (2, -1), (-2, 1), (-2, -1), (1, 2), (1, -2), (-1, 2), (-1, -2)]
        no_moves = True
        for possible_move in possible_moves:
            y = position[0] + possible_move[0]
            x = position[1] + possible_move[1]
            if 1 <= y <= self.board_dimensions[0] and 1 <= x <= self.board_dimensions[1] and self.board[(y, x)] != 'X' and self.board[(y, x)] != '*':
                self.board[(y, x)] = str(self.count_possible_moves((y, x)))
                no_moves = False
        if no_moves:
            self.game_over = 2
            for x in range(1, self.board_dimensions[1] + 1):
                for y in range(1, self.board_dimensions[0] + 1):
                    if self.board[(y, x)] != 'X' and self.board[(y, x)] != '*':
                        self.game_over = 1
                        break

    @staticmethod
    def playing_puzzle():
        while (answer := input('Do you want to try the puzzle? (y/n):').lower()) not in ('y', 'n'):
            print("Invalid input!")
        return answer == 'y'

    def solve(self):
        self.ask_for_board_dimensions()
        self.setup_board()
        self.ask_for_starting_position()
        if self.playing_puzzle():
            solution_found = WarnsdorffsAlgorithm(self.board_dimensions, self.starting_position).solve(print_board=False)
            if solution_found:
                self.current_position = self.starting_position
                move_count = 0
                while True:
                    move_count += 1
                    self.find_possible_moves(self.current_position)
                    self.print_board()
                    if self.game_over != 0:
                        break
                    self.current_position = self.get_next_move(self.current_position)
                if self.game_over == 1:
                    print("\nNo more possible moves!")
                    print(f"Your knight visited {move_count} squares!")
                else:
                    print("\nWhat a great tour! Congratulations!")
        else:
            WarnsdorffsAlgorithm(self.board_dimensions, self.starting_position).solve(print_board=True)


class WarnsdorffsAlgorithm:

    def __init__(self, board_dimensions, starting_position):
        self.board_dimensions = board_dimensions
        self.starting_position = starting_position
        self.board = {self.starting_position: 1}
        self.deltas = [(1, 2), (1, -2), (2, 1), (2, -1), (-1, 2), (-1, -2), (-2, 1), (-2, -1)]
        self.cell_size = len(str(self.board_dimensions[0] * self.board_dimensions[1]))
        self.max_number_width = max(len(str(self.board_dimensions[0])), len(str(self.board_dimensions[1])))
        pass

    def print_board(self):
        row_len = self.board_dimensions[0] * (self.cell_size + 1) + 3
        print(f"{' ' * self.max_number_width}{'-' * row_len}")
        for x in range(self.board_dimensions[1], 0, -1):
            row = ""
            row += f'{" " * (self.max_number_width - len(str(x))) + str(x)}| '
            for y in range(1, self.board_dimensions[0] + 1):
                row += str(self.board[(y, x)]).rjust(self.cell_size)
                row += " "
            row += "|"
            print(row)

        print(f"{' ' * self.max_number_width}{'-' * row_len}")
        numbers = f"{' ' * self.max_number_width} "
        for i in range(1, self.board_dimensions[0] + 1):
            numbers += f"{' '}{(' ' * (self.cell_size - len(str(i))))}{i}"
        print(numbers)

    def is_within_bounds(self, position):
        return 1 <= position[1] <= self.board_dimensions[1] and 1 <= position[0] <= self.board_dimensions[0]

    def is_empty_square(self, position):
        return self.is_within_bounds(position) and position not in self.board

    def valid_moves_count(self, position):
        count = 0
        for delta in self.deltas:
            if self.is_empty_square((position[0] + delta[0], position[1] + delta[1])):
                count += 1
        return count

    def find_next_move(self, position):
        deltas_length = len(self.deltas)
        min_valid_move = None
        min_count = math.inf
        starting_delta_index = random.randint(0, deltas_length)
        for delta_index in range(0, deltas_length):
            index = (starting_delta_index + delta_index) % deltas_length
            delta = self.deltas[index]
            next_move = (position[0] + delta[0], position[1] + delta[1])
            if self.is_empty_square(next_move) and (count := self.valid_moves_count(next_move)) < min_count:
                min_valid_move = next_move
                min_count = count
        if min_valid_move is None:
            return None
        self.board[min_valid_move] = self.board[position] + 1
        return min_valid_move

    def is_tour_complete(self, next_position):
        for delta in self.deltas:
            if next_position[0] + delta[0] == self.starting_position[0] \
                    and next_position[1] + delta[1] == self.starting_position[1]:
                return True
        return False

    def tour_is_closed(self):
        next_move = self.starting_position
        for i in range(0, self.board_dimensions[0] * self.board_dimensions[1] - 1):
            next_move = self.find_next_move(next_move)
            if next_move is None:
                return False
        return True

    def solve(self, print_board=False):
        if self.tour_is_closed():
            if print_board:
                print("\nHere's the solution!")
                self.print_board()
            return True
        else:
            print("No solution exists!")
            return False


KnightsTour().solve()
