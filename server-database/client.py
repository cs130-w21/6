import socket
import json
import cv2
import base64
import PIL.Image as Image
import io
class Client:
    def __init__(self,host='localhost',port=6000):
        self.host = host  # The server's hostname or IP address
        self.port = port  # The port used by the server

    # the client send operation and data dictionary to the server
    # the server returns a dictionary
    def send_message(self, data):
        # build a connection to python server
        local_ip = socket.gethostbyname(socket.gethostname())
        # create a json object of the data
        json_data = json.dumps(data, sort_keys=False, indent=2)
        encoded_data = (json_data + ';').encode()
        # build a socket and encode the data
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        # send encoded data through socket with while loop
        self.socket_send(encoded_data,s)
        # we may not be able to receive all data at once
        # we use a while loop to continuously receive data from server
        msg = ''
        while True:
            packet = s.recv(int(1e7)).decode()
            msg += packet
            if (packet[-1] == ';'):
                break
        # decode json object to python dictionary
        msg = json.loads(msg[0:-1])
        # close the socket
        s.close()
        return msg
    
    def encode_img(self,img_path):
        img_bytes = open(img_path,'rb').read()
        img_base64 = base64.encodebytes(img_bytes)
        img_utf8 = img_base64.decode('utf-8')
        return img_utf8
        
    def decode_img(self,img):
        img_base64   = img.encode('utf-8')
        img_bytes    = base64.decodebytes(img_base64)
        image        = Image.open(io.BytesIO(img_bytes))
        #img_arr      = np.array(image)
        return image

    def socket_send(self,data,s):
        s.connect((self.host, self.port))
        s.sendall(data)
