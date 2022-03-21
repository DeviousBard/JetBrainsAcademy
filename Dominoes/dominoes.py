import random
import math


class DominoGame:

    def __init__(self):
        try:
            self.domino_set = [[i, j] for i in range(0, 7) for j in range(i, 7)]
            self.stock_pieces = random.sample(self.domino_set, 14)
            self.computer_pieces = random.sample([i for i in self.domino_set if i not in self.stock_pieces], 7)
            self.max_computer_piece = max([piece for piece in self.computer_pieces if piece[0] == piece[1]])
            self.player_pieces = [i for i in self.domino_set if (i not in self.stock_pieces
                                                                 and i not in self.computer_pieces)]
            self.max_player_piece = max([piece for piece in self.player_pieces if piece[0] == piece[1]])
            self.status = "computer" if self.max_player_piece > self.max_computer_piece else "player"
            self.domino_snake = [max(self.max_player_piece, self.max_computer_piece)]
            self.computer_pieces.remove(self.domino_snake[0]) if self.status == "player" \
                else self.player_pieces.remove(self.domino_snake[0])
        except ValueError:
            self.__init__()

    def output(self):
        print()
        print(f"Stock pieces: {self.stock_pieces}")
        print(f"Computer pieces: {self.computer_pieces}")
        print(f"Player pieces: {self.player_pieces}")
        print(f"Domino snake: {self.domino_snake}")
        print(f"Status: {self.status}")

    def print_current_game(self):
        print(f"=" * 70)
        print(f"Stock size: {len(self.stock_pieces)}")
        print(f"Computer pieces: {len(self.computer_pieces)}")
        print()
        domino_snake_print = ""
        for i in range(0, len(self.domino_snake)):
            if i <= 2 or i >= len(self.domino_snake) - 3:
                domino_snake_print += str(self.domino_snake[i])
        print(domino_snake_print if len(self.domino_snake) < 7 else domino_snake_print[0:int(
            len(domino_snake_print) / 2)] + "..." + domino_snake_print[
                                                    int(len(domino_snake_print) / 2): len(domino_snake_print)])
        print()
        print(f"Your pieces:")
        for i in range(0, len(self.player_pieces)):
            print(f"{i + 1}:{self.player_pieces[i]}")
        print()

    def winner(self):
        winner = -1
        if len(self.player_pieces) == 0:
            winner = 0
        elif len(self.computer_pieces) == 0:
            winner = 1
        else:
            last_domino_index = len(self.domino_snake) - 1
            if self.domino_snake[0][0] == self.domino_snake[last_domino_index][1]:
                value = self.domino_snake[0][0]
                count = 0
                for i in range(0, last_domino_index + 1):
                    for j in range(0, 2):
                        count += 1 if self.domino_snake[i][j] == value else 0
                if count == 8:
                    winner = 2
        return winner

    @staticmethod
    def flip_domino(domino):
        temp = domino[0]
        domino[0] = domino[1]
        domino[1] = temp
        return domino

    def place_domino_on_domino_snake(self, domino_list, domino_index):
        abs_value_domino_index = int(math.fabs(domino_index))
        domino = domino_list[abs_value_domino_index - 1]
        if domino_index < 1:
            front_value = self.domino_snake[0][0]
            if front_value in domino:
                if domino[0] == front_value:
                    domino = self.flip_domino(domino)
                self.domino_snake.insert(0, domino)
                domino_list.pop(abs_value_domino_index - 1)
                return True
            else:
                return False
        else:
            back_value = self.domino_snake[len(self.domino_snake) - 1][1]
            if back_value in domino:
                if domino[1] == back_value:
                    domino = self.flip_domino(domino)
                self.domino_snake.append(domino)
                domino_list.pop(abs_value_domino_index - 1)
                return True
            else:
                return False

    def players_turn(self):
        invalid_move = True
        while invalid_move:
            selected_index = input()
            value = 100
            try:
                value = int(selected_index)
            except ValueError:
                pass
            abs_value = int(math.fabs(value))
            if not value == 100 and 0 <= abs_value <= len(self.player_pieces):
                if value == 0:
                    if len(self.stock_pieces) > 0:
                        self.player_pieces.append(self.stock_pieces.pop(random.randint(0, len(self.stock_pieces) - 1)))
                        invalid_move = False
                    else:
                        invalid_move = False
                else:
                    if self.place_domino_on_domino_snake(self.player_pieces, value):
                        invalid_move = False
                    else:
                        print("Illegal move. Please try again.")

            else:
                print("Invalid input. Please try again.")

    def rank_dominoes(self):
        counts = [0, 0, 0, 0, 0, 0, 0]
        ranks = {}
        for i in range(0, len(self.domino_snake)):
            for j in range(0, 2):
                counts[self.domino_snake[i][j]] += 1
        for i in range(0, len(self.computer_pieces)):
            for j in range(0, 2):
                counts[self.computer_pieces[i][j]] += 1
        for i in range(0, len(self.computer_pieces)):
            total = counts[self.computer_pieces[i][0]] + counts[self.computer_pieces[i][1]]
            ranks[i] = total
        sorted_ranks = sorted(ranks.items(), key=lambda item: item[1], reverse=True)
        return sorted_ranks

    def computers_turn(self):
        input()
        invalid_move = True
        ranks = self.rank_dominoes()
        ranks_index = 0
        while invalid_move:
            selected_index = ranks[ranks_index][0]
            if self.place_domino_on_domino_snake(self.computer_pieces, selected_index):
                invalid_move = False
            elif self.place_domino_on_domino_snake(self.computer_pieces, selected_index * -1):
                invalid_move = False
            else:
                ranks_index += 1
                if ranks_index > len(self.computer_pieces) - 1:
                    if len(self.stock_pieces) > 0:
                        self.computer_pieces.append(
                            self.stock_pieces.pop(random.randint(0, len(self.stock_pieces) - 1)))
                    invalid_move = False

    def play_game(self):
        winner = -1
        while winner == -1:
            self.print_current_game()
            if self.status == "player":
                print(f"Status: It's your turn to make a move. Enter your command.")
                self.players_turn()
                self.status = "computer"
            else:
                print(f"Status: Computer is about to make a move. Press Enter to continue...")
                self.computers_turn()
                self.status = "player"
            winner = self.winner()
        self.print_current_game()
        print("Status: The game is over.",
              "You won!" if winner == 0 else "The computer won!" if winner == 1 else "It's a draw!")


DominoGame().play_game()
