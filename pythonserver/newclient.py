import socket
import json
import cv2
import base64
import sys

HOST = 'localhost'  # The server's hostname or IP address
PORT = 5000   # The port used by the server
def json_message(imgdata):
    #print(imgdata)
    local_ip = socket.gethostbyname(socket.gethostname())
    data = {
        'op': 'load',
        'uid': '1'
    }
    json_data = json.dumps(data, sort_keys=False, indent=2)
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    send_message(json_data,s)
    msg = ''
    while True:
        packet = s.recv(int(100))
        packet = packet.decode()
        msg += packet
        if (packet[-1] == ';'):
            break
    msg = json.loads(msg[0 : -1])
    print(msg)
    s.close()
    return json_data



def send_message(data,s):
    s.connect((HOST, PORT))
    try:
    	s.sendall((data + ';').encode())
    except (BrokenPipeError, IOError):
    	pass

json_message(open('p15.jpg','rb').read())
sys.stderr.close()
