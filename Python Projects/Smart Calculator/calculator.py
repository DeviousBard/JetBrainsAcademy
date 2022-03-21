import re


class InvalidExpressionError(BaseException):
    pass


class UnknownVariableError(BaseException):
    pass


class UnknownCommandError(BaseException):
    pass


class InvalidIdentifierError(BaseException):
    pass


class Stack:

    def __init__(self):
        self.stack = []

    def push(self, item):
        self.stack.insert(0, item)

    def pop(self):
        return None if self.is_empty() else self.stack.pop(0)

    def is_empty(self):
        return len(self.stack) == 0

    def peek(self):
        return None if self.is_empty() else self.stack[0]

    def size(self):
        return len(self.stack)

    def __repr__(self):
        return str(self.stack)


class Infix:
    def __init__(self, infix_expr):
        self.__infix_expr = Infix.__clean_infix_expr(infix_expr)

    @staticmethod
    def __check_for_balanced_parentheses(expr):
        stack = Stack()
        for x in expr:
            if x == '(':
                stack.push(x)
            if x == ')':
                if stack.is_empty():
                    raise InvalidExpressionError
                if not stack.peek() == '(':
                    raise InvalidExpressionError
                stack.pop()

    @staticmethod
    def __clean_infix_expr(infix_expr):
        if re.search("[*/^][*/^]", infix_expr):
            raise InvalidExpressionError
        clean_expr = ''
        # Replace a sequence of two or more "+" operators with a single "+" operator and replace
        # a sequence of two or more "-" operators with either a single "-" operator for
        # an odd number of "-" operators in a row, or a single "+" operator for an even number
        # of "-" operators in a row.
        x = 0
        while x < len(infix_expr):
            if infix_expr[x] == '+':
                clean_expr += '+'
                while infix_expr[x] == '+':
                    x += 1
            if infix_expr[x] == '-':
                minus_count = 0
                while infix_expr[x] == '-':
                    minus_count += 1
                    x += 1
                if minus_count % 2 == 0:
                    clean_expr += '+'
                else:
                    clean_expr += '-'
            clean_expr += infix_expr[x]
            x += 1
        clean_expr = clean_expr.strip()
        Infix.__check_for_balanced_parentheses(clean_expr)
        return clean_expr

    def __repr__(self):
        return self.__infix_expr

    @staticmethod
    def __get_precedence(operator):
        if operator in ['+', '-']:
            return 1
        if operator in ['*', '/']:
            return 2
        if operator in ['^']:
            return 3
        return -1

    def to_postfix(self):
        operators = ['+', '-', '*', '/', '^']
        stack = Stack()
        postfix_expr_array = []
        number_flag = False
        for char in self.__infix_expr:
            if char == ' ':
                continue
            if char == '(':
                number_flag = False
                stack.push(char)
            elif char == ')':
                number_flag = False
                while not stack.is_empty() and not stack.peek() == '(':
                    postfix_expr_array.append(stack.pop())
                if not stack.is_empty() and not stack.peek() == '(':
                    raise InvalidExpressionError
                stack.pop()
            elif char in operators:
                number_flag = False
                if stack.is_empty():
                    stack.push(char)
                else:
                    while not stack.is_empty() and Infix.__get_precedence(stack.peek()) >= Infix.__get_precedence(char):
                        if stack.peek() == '(':
                            raise InvalidExpressionError
                        postfix_expr_array.append(stack.pop())
                    stack.push(char)
            else:
                if number_flag:
                    last_number = postfix_expr_array[len(postfix_expr_array) - 1]
                    last_number += char
                    postfix_expr_array[len(postfix_expr_array) - 1] = last_number
                else:
                    number_flag = True
                    postfix_expr_array.append(char)
        while not stack.is_empty():
            if stack.peek() == '(':
                raise InvalidExpressionError
            postfix_expr_array.append(stack.pop())
        return Postfix(" ".join(postfix_expr_array))

    def solve(self):
        return self.to_postfix().solve()

    def get_infix_expression(self):
        return self.__infix_expr


class Postfix:

    def __init__(self, postfix_expr):
        self.postfix_expr = Postfix.__clean_postfix_expr(postfix_expr)

    @staticmethod
    def __is_operator(term):
        return term in ['+', '-', '/', '*', '^', ')', '(']

    @staticmethod
    def __clean_postfix_expr(postfix_expr):
        clean_expr = postfix_expr
        re_search = re.search(r"(\s{2,})", clean_expr)
        while re_search:
            rep_char = re_search.group()[0]
            rep_string = clean_expr[re_search.span()[0]:re_search.span()[1]]
            clean_expr = clean_expr.replace(rep_string, rep_char)
            re_search = re.search(r"(\s{2,})", clean_expr)

        return clean_expr.strip()

    def __repr__(self):
        return self.postfix_expr

    def get_postfix_expression(self):
        return self.postfix_expr

    def to_infix(self):
        pass

    def solve(self):
        terms = self.get_postfix_expression().split()
        stack = Stack()
        for term in terms:
            is_numeric_term = None
            try:
                is_numeric_term = float(term)
            except ValueError:
                pass
            if is_numeric_term is not None:
                stack.push(float(term))
            elif not Postfix.__is_operator(term):
                stack.push(Variables.get_variable(term))
            else:
                if stack.size() >= 2:
                    result = Postfix.__apply_operator(term, stack.pop(), stack.pop())
                    if result is not None:
                        stack.push(result)
                    else:
                        raise InvalidExpressionError
        return stack.pop() if stack.size() == 1 else None

    @staticmethod
    def __apply_operator(operator, op1, op2):
        if operator == '+':
            return op2 + op1
        if operator == '-':
            return op2 - op1
        if operator == '*':
            return op2 * op1
        if operator == "/":
            return op2 / op1
        if operator == "^":
            return op2 ** op1
        return None


class Variables:
    __instance = None
    __variables = {}

    @staticmethod
    def __get_instance():
        if Variables.__instance is None:
            Variables()
        return Variables.__instance

    def __init__(self):
        if Variables.__instance is not None:
            raise Exception("This class is a singleton!")
        else:
            Variables.__instance = self

    @staticmethod
    def is_valid_variable_name(variable_name):
        if str(variable_name).isalpha():
            return True
        raise InvalidIdentifierError

    @staticmethod
    def has_variable(variable_name):
        if Variables.is_valid_variable_name(variable_name):
            if variable_name in Variables.__get_instance().__variables:
                return True
        raise UnknownVariableError

    @staticmethod
    def get_variable(variable_name):
        if Variables.has_variable(variable_name):
            return Variables.__get_instance().__variables[variable_name]

    @staticmethod
    def set_variable(variable_name, variable_value):
        if Variables.is_valid_variable_name(variable_name):
            Variables.__get_instance().__variables[variable_name] = variable_value


def main():
    while True:
        try:
            nums_str = input()
            commands = nums_str.split()
            if len(commands) != 0:
                if commands[0] == "/exit":
                    print("Bye!")
                    break
                elif commands[0] == "/help":
                    print("The program calculates the sum or difference of numbers")
                elif commands[0].startswith("/"):
                    raise UnknownCommandError
                elif '=' in nums_str:
                    assignment = nums_str.split("=")
                    if len(assignment) != 2:
                        raise InvalidExpressionError
                    else:
                        variable_name = assignment[0]
                        infix = Infix(assignment[1])
                        postfix = infix.to_postfix()
                        value = int(postfix.solve())
                        Variables.set_variable(variable_name.strip(), value)
                else:
                    infix = Infix(nums_str)
                    postfix = infix.to_postfix()
                    print(int(postfix.solve()))
        except InvalidExpressionError:
            print("Invalid Expression")
        except UnknownVariableError:
            print("Unknown variable")
        except UnknownCommandError:
            print("Unknown command")
        except InvalidIdentifierError:
            print("Invalid identifier")


main()
