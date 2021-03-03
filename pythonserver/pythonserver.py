import socket
import _thread
import numpy as np
import json
import base64
import io
import PIL.Image as Image
import cv2
from server_call_tensorflow import *
class Socket:
    def __init__(self, host='127.0.0.1', port=9999, max_connect=5):
        self.s = socket.socket()
        self.hostname = host
        self.port = port
        self.s.bind((host, port))
        self.s.listen(max_connect)
        self.tokenizer = np.load('tokenizer.npy',allow_pickle='TRUE').item()
        self.BUFFERSIZE = 1000000
    def actions(self, clientsocket, address):
        #reply = 'successfully connected to python server!'
        #clientsocket.sendall(reply.encode())
        msg = ''
        while True:
            packet = clientsocket.recv(self.BUFFERSIZE)  
            packet = packet.decode()
            msg += packet
            if(packet[-1] == ';'):
                break
        msg = json.loads(msg[0:-1])
        if msg['op'] == 'get_quote':
            img = msg['data'].encode('utf-8')
            image = base64.decodebytes(img)
            image = Image.open(io.BytesIO(image))
            retcaptions = call_tensorflow(self.tokenizer, np.array(image))
            #print(retcaptions)
            data = {
                'status': '1',
                'data': retcaptions}
            json_data = json.dumps(data, sort_keys=False, indent=2)
            try:
                clientsocket.sendall((json_data + ';').encode())
            except (BrokenPipeError, IOError):
                pass
        clientsocket.close()
    def connect(self):
        while True:
            connection, address = self.s.accept()
            print("success!")
            _thread.start_new_thread(self.actions, (connection, address))
    def terminate(self):
        self.s.close()
server_socket = Socket(max_connect=10)
server_socket.connect()
server_socket.terminate()
