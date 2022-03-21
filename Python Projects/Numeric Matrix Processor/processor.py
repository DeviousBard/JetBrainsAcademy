import math


class MatrixInitializationError(Exception):
    pass


class MatrixArithmeticError(Exception):
    pass


class Matrix:
    def __init__(self, matrix=None):
        if not matrix or type(matrix) is not list:
            self.matrix = []
        else:
            self.matrix = matrix

    @classmethod
    def from_input(cls, size_input_text="Enter size of matrix: ", matrix_input_text="Enter matrix: "):
        x, y = input(size_input_text).split()
        matrix = []
        print(matrix_input_text)
        for _ in range(0, int(x)):
            row = [float(num) for num in input().split()]
            if len(row) > int(y):
                raise MatrixInitializationError(f"Invalid number of columns entered: {len(row)}")
            matrix.append(row)
        return Matrix(matrix)

    def __str__(self):
        result = ""
        for row in self.matrix:
            result += " ".join(
                ('%.2f' % x).rstrip('0').rstrip('.') for x in row) + "\n"
        return result

    def get_element(self, x, y):
        return self.matrix[x][y]

    def set_element(self, x, y, value):
        self.matrix[x][y] = value

    @property
    def rows(self):
        return len(self.matrix)

    @property
    def columns(self):
        return len(self.matrix[0]) if self.rows > 0 else 0

    def __add__(self, other):
        if self.rows != other.rows or self.columns != other.columns:
            raise MatrixArithmeticError("Cannot add two matrices of differing dimensions.")
        new_matrix = []
        for x in range(0, self.rows):
            row = []
            for y in range(0, self.columns):
                row.append(self.get_element(x, y) + other.get_element(x, y))
            new_matrix.append(row)
        return Matrix(new_matrix)

    def __imul__(self, scalar):
        new_matrix = []
        for row in self.matrix:
            new_matrix.append(y * scalar for y in row)
        self.matrix = new_matrix
        return self

    def __get_column__(self, index):
        return [row[index] for row in self.matrix]

    def transpose(self):
        new_matrix = []
        for idx in range(0, self.columns):
            new_matrix.append(self.__get_column__(idx))
        return Matrix(new_matrix)

    def __mul__(self, other):
        if self.columns != other.rows:
            raise MatrixArithmeticError(
                f"Cannot multiply two matrices where the second "
                f"matrix's columns are not equal to the first matrix's rows")
        new_matrix = []
        t_other = other.transpose()
        for row in self.matrix:
            new_row = []
            for t_row in t_other.matrix:
                new_row.append(sum([row[i] * t_row[i] for i in range(0, t_other.columns)]))
            new_matrix.append(new_row)
        return Matrix(new_matrix)

    def reverse(self):
        new_matrix = []
        for row in self.matrix:
            new_row = [x for x in row]
            new_row.reverse()
            new_matrix.append(new_row)
        return Matrix(new_matrix)

    def flip(self):
        new_matrix = []
        for row in self.matrix:
            new_matrix.insert(0, [x for x in row])
        return Matrix(new_matrix)

    def reduced_row_echelon_format(self):
        m = Matrix(self.matrix)
        lead = 0
        for r in range(m.rows):
            if lead >= m.columns:
                return m
            i = r
            while m.get_element(i, lead) == 0:
                i += 1
                if i == m.rows:
                    i = r
                    lead += 1
                    if columnCount == lead:
                        return m
            m.matrix[i], m.matrix[r] = m.matrix[r], m.matrix[i]
            lv = m.get_element(r, lead)
            m.matrix[r] = [mrx / float(lv) for mrx in m.matrix[r]]
            for i in range(m.rows):
                if i != r:
                    lv = m.get_element(i, lead)
                    m.matrix[i] = [iv - lv * rv for rv, iv in zip(m.matrix[r], m.matrix[i])]
            lead += 1
        return m

    def determinant(self, multiplier=1):
        m = Matrix(self.matrix)
        if m.rows == 1:
            return multiplier * m.get_element(0, 0)
        else:
            sign = -1
            determinant = 0
            for i in range(m.rows):
                new_matrix = []
                for j in range(1, m.rows):
                    row = []
                    for k in range(m.rows):
                        if k != i:
                            row.append(m.get_element(j, k))
                    new_matrix.append(row)
                sign *= -1
                determinant += multiplier * Matrix(new_matrix).determinant(sign * m.get_element(0, i))
            return determinant

    def delete_row_column(self, x, y):
        m = Matrix(self.matrix)
        return Matrix([[m.matrix[row][col]
                        for col in range(m.columns) if col != y]
                       for row in range(m.rows) if row != x])

    def minor(self, x, y):
        m = Matrix(self.matrix)
        return m.delete_row_column(x, y).determinant()

    def inverse(self):
        m = Matrix(self.matrix)
        if m.rows != m.columns:
            raise MatrixArithmeticError("Can only calculate the inverse of square matrices")
        determinant = m.determinant()
        if determinant == 0:
            raise MatrixArithmeticError("Matrix has no inverse")
        cofactors = []
        for i in range(m.rows):
            cofactors_row = []
            for j in range(m.rows):
                coefficient = -1 if (i % 2) ^ (j % 2) else 1
                cofactors_row.append(coefficient * m.minor(i, j))
            cofactors.append(cofactors_row)
        adjugate = Matrix(cofactors).transpose().matrix
        inverse = [[math.trunc(element / determinant * 100) / 100 for element in row] for row in adjugate]
        return Matrix(inverse)


def main():
    while True:
        print()
        print("1. Add matrices")
        print("2. Multiply matrix by constant")
        print("3. Multiply matrices")
        print("4. Transpose matrix")
        print("5. Calculate a determinant")
        print("6. Inverse matrix")
        print("0. Exit")
        option = input()
        result = None
        if option == '0':
            break
        if option == '1':
            m1 = Matrix.from_input("Enter size of first matrix: ", "Enter first matrix: ")
            m2 = Matrix.from_input("Enter size of second matrix: ", "Enter second matrix: ")
            result = "Unable to add matrices."
            try:
                result = m1 + m2
            except MatrixArithmeticError:
                pass
        if option == '2':
            m = Matrix.from_input()
            constant = float(input("Enter constant: "))
            m *= constant
            result = m
        if option == '3':
            m1 = Matrix.from_input("Enter size of first matrix: ", "Enter first matrix: ")
            m2 = Matrix.from_input("Enter size of second matrix: ", "Enter second matrix: ")
            result = "Unable to multiply matrices"
            try:
                result = m1 * m2
            except MatrixArithmeticError:
                pass
        if option == '4':
            print()
            print("1. Main diagonal")
            print("2. Side diagonal")
            print("3. Vertical line")
            print("4. Horizontal line")
            transpose_type = input("Your choice: ")
            m = Matrix.from_input()
            if transpose_type == '1':
                result = m.transpose()
            if transpose_type == '2':
                result = m.reverse().transpose().reverse()
            if transpose_type == '3':
                result = m.reverse()
            if transpose_type == '4':
                result = m.flip()
        if option == '5':
            m = Matrix.from_input()
            result = m.determinant()
        if option == '6':
            m = Matrix.from_input()
            result = "This matrix doesn't have an inverse."
            try:
                result = m.inverse()
            except MatrixArithmeticError:
                pass
        print(f"The result is:\n{result}")


main()
