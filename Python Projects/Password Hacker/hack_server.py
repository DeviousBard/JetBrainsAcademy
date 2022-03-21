import socket
import json

from time import sleep


def main():
    login = "new_admin"
    password = "r5m0QA"
    s = socket.socket()
    s.bind((host, port))
    s.listen(1)
    print(f'Server is listening on port {port}.')
    client, address = s.accept()
    while True:
        data = client.recv(1024)
        if not data:
            break
        credentials = json.loads(data.decode())
        client_login = credentials["login"]
        client_password = credentials["password"]
        print(f'Received credentials: "{client_login}", "{client_password}"')
        if client_login == login:
            if client_password == password:
                message = json.dumps({"result": "Connection success!"})
            elif password != "" and password.startswith(client_password):
                sleep(0.1)
                message = json.dumps({"result": "Wrong password!"})
            else:
                message = json.dumps({"result": "Wrong password!"})
        else:
            message = json.dumps({"result": "Wrong login!"})
        client.send(message.encode())
    client.close()


host = ""
port = 9090
main()
