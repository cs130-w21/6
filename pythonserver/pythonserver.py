import socket
import thread

class Socket:
    def __init__(self, host=socket.gethostname(), port=5000, max_connect=5):
        self.s = socket.socket()
        self.hostname = host
        self.port = port
        self.s.bind((host, port))
        self.s.listen(max_connect)
    def actions(self, clientsocket, address):
        while True:
            #TODO
            msg = clientcsocket.recv(1024)
            break
        clientsocket.close()
    def connect(self):
        while True:
            connection, address = self.s.accept()
            thread.start_new_thread(self.actions, (connection, address))
    def terminate(self):
        self.s.close()
server_socket = Socket(max_connect=10)
server_socket.connect()
server_socket.terminate()
