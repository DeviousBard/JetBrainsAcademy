def print_board(board):
    print("---------")
    for i in range(3):
        row = "| "
        for j in range(3):
            row += board[i][j] + " "
        row += "|"
        print(row)
    print("---------")


def game_over(board):
    ended = False
    x_count = count_pieces(board, "X")
    o_count = count_pieces(board, "O")
    x_wins = is_winner(board, "X")
    o_wins = is_winner(board, "O")
    if abs(x_count - o_count) > 1 or (x_wins and o_wins):
        print("Impossible")
        ended = True
    elif x_wins:
        print("X wins")
        ended = True
    elif o_wins:
        print("O wins")
        ended = True
    elif (x_count == 4 and o_count == 5) or (x_count == 5 and o_count == 4):
        print("Draw")
        ended = True
    return ended


def count_pieces(board, piece):
    return sum([board[i].count(piece) for i in range(3)])


def is_winner(board, piece):
    left_diagonal = [board[0][0], board[1][1], board[2][2]]
    right_diagonal = [board[0][2], board[1][1], board[2][0]]
    if right_diagonal.count(piece) == 3 or left_diagonal.count(piece) == 3:
        return True
    else:
        for i in range(3):
            col = [board[j][i] for j in range(3)]
            if board[i].count(piece) == 3 or col.count(piece) == 3:
                return True
    return False


def translate_coordinates(coords):
    return [3 - coords[1], coords[0] - 1]


def get_user_move(board):
    coords = []
    while len(coords) != 2:
        coords_string = input("Enter the coordinates: ").split()
        if not(coords_string[0].isdigit() and coords_string[1].isdigit()):
            print("You should enter numbers!")
        else:
            coords = translate_coordinates([int(x) for x in coords_string])
            if coords[0] < 0 or coords[0] > 2 or coords[1] < 0 or coords[1] > 2:
                print("Coordinates should be from 1 to 3!")
                coords = []
            elif board[coords[0]][coords[1]] != ' ':
                print("This cell is occupied! Choose another one!")
                coords = []
    return coords


def main():
    board = [[" ", " ", " "], [" ", " ", " "], [" ", " ", " "]]
    pieces = ["X", "O"]
    current_player = 0
    print_board(board)
    while not(game_over(board)):
        coords = get_user_move(board)
        board[coords[0]][coords[1]] = pieces[current_player]
        current_player = not current_player
        print_board(board)


main()
