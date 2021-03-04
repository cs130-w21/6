import socket
import _thread
import numpy as np
import json
import base64
import io
import PIL.Image as Image
import cv2
from server_call_tensorflow import *
from mongo_db import *
from register import run_register
from login import run_login
from load import run_load
from confirm import run_confirm
from delete import run_delete
from delete_user import run_delete_user
from pprint import pprint
import logging
import time

class Socket:
    def __init__(self, host='localhost', port=6000, max_connect=10):
        self.s = socket.socket()
        self.hostname = host
        self.port = port
        self.s.bind((host, port))
        self.s.listen(max_connect)
        self.tokenizer = np.load('tokenizer.npy',allow_pickle='TRUE').item()
        self.BUFFERSIZE = 1000000
        # set up logging
        logging.basicConfig(
            filename='./server.log',
            format='%(asctime)s %(levelname)-8s %(message)s',
            level=logging.INFO,
            datefmt='%Y-%m-%d %H:%M:%S')

        # initialize database
        # create a client
        self.client = create_client()
        logging.info(print_all_db(self.client))
        # get a database called mystory
        self.db      = get_database(self.client,'mystory')
        # create a collection called user
        self.tb_user  = get_collection(self.db, 'user')
        # create a collection called story
        self.tb_story = get_collection(self.db, 'story')
        #stories  = run_load('xintayang',self.tb_story,valid=1)
        #print(stories)
    def actions(self, clientsocket, address):
        localtime = time.asctime(time.localtime(time.time()))
        msg = ''
        while True:
            packet = clientsocket.recv(self.BUFFERSIZE).decode()
            msg += packet
            if(packet[-1] == ';'):
                break
        msg = json.loads(msg[0:-1])
        
        valid = True # check if the operation succeed
        # UPLOAD an image
        if msg['op'] == 'upload':
            # decode image to PIL Image type
            image = self.decode_img(msg['image'])
            # call tensorflow to generate captions
            retcaptions = call_tensorflow(self.tokenizer, np.array(image))
            # create a data to be sent back
            data = {
                'op': 'upload',
                'quote': retcaptions
            }
            # log to info
            logging.info('upload success')
            
        # REGISTER a new user            
        elif msg['op'] == 'register':
            # {'uid', 'password'}
            username = msg['uid']
            password = msg['password']
            # success=1 if success (create a new user)
            # success=0 if usrname exists
            success = run_register(username,password,self.tb_user,valid)
            if valid:
                data = {
                    'op'      : 'register',
                    'success' : success,
                    'uid': username
                }
                logging.info('register success')
            else:
                data = {
                    'op'      : 'fail'
                }
                logging.info('register failed')
        # LOGIN a user
        elif msg['op'] == 'login':
            # {'uid', 'password'}
            username = msg['uid']
            password = msg['password']
            # success = 1 if success (correct pair)
            # success = 0 otherwise
            success = run_login(username,password,self.tb_user,valid)
            if valid:
                data = {
                    'op'      : 'login',
                    'success' : success,
                    'uid': username
                }
                logging.info('login success')
            else:
                data = {
                    'op' : 'fail'
                }
                logging.info('login failed')
        # DELETE a user
        elif msg['op'] == 'delete_user':
            username = msg['uid']
            run_delete_user(username,self.tb_user,valid)
            if valid:
                data = {
                    'op' : 'delete_user',
                    'uid': username
                }
                logging.info('delete user success')
            else:
                data = {
                    'op' : 'fail'
                }
                logging.info('delete user failed')
    
    # LOAD images for a user
        elif msg['op'] == 'load':
            # {'uid'}
            username = msg['uid']
            # stories are returned as a list of
            # [[row_id,image_path,capiton]...]
            stories  = run_load(username,self.tb_story,valid)
            if valid:
                # process stories
                nstories = []
                for story in stories:
                    row_id     = story[0]
                    image_path = story[1]
                    caption    = story[2]
                    enc_img    = self.encode_img(image_path)
                    nstories.append({'row_id':row_id,'image':enc_img,'quote':caption})
                # need to check here if the format is fine for json
                data = {
                    'op'   : 'load',
                    'data' : nstories
                    #json.dumps(nstories,sort_keys=False,indent=2)
                }
                logging.info('load success')
            else:
                data = {
                    'op' : 'fail'
                }
                logging.info('load failed')

        # CONFIRM a new story
        elif msg['op'] == 'confirm':
            # {'uid','image','quotes'}
            username = msg['uid']
            image    = msg['image']
            quote    = msg['quote']

            image    = self.decode_img(image)
            row_id   = run_confirm(username,image,quote,self.tb_story,valid)
            if valid:
                data = {
                    'op'     : 'confirm',
                    'row_id' :  row_id
                }
                logging.info('confirm success')
            else:
                data = {
                    'op' : 'fail'
                }
                logging.info('confirm failed')
        # DELETE stories
        elif msg['op'] == 'delete':
            # {'row_id'}
            row_id  = msg['row_id']
            username = msg['uid']
            #print(row_id)
            run_delete(row_id,self.tb_story,valid)
            if valid:
                stories  = run_load(username,self.tb_story,valid)
                if valid:
                    # process stories
                    nstories = []
                    for story in stories:
                      row_id     = story[0]
                      image_path = story[1]
                      caption    = story[2]
                      enc_img    = self.encode_img(image_path)
                      nstories.append({'row_id':row_id,'image':enc_img,'quote':caption})
                # need to check here if the format is fine for json
                    data = {
                        'op'   : 'delete',
                        'data' : nstories
                        #json.dumps(nstories,sort_keys=False,indent=2)
                    }
                    logging.info('delete success')
                else:
                    data = {
                        'op' : 'fail'
                    }
                    logging.info('delete failed')
            else:
                data = {
                    'op'     : 'fail'
                }
                logging.info('delete failed')
        # invalid operation
        else:
            #print("Invalid Operation!")
            logging.info('invalid operation')

        # dump data as json and send back
        json_data = json.dumps(data, sort_keys=False, indent=2)
        clientsocket.sendall((json_data + ';').encode())
        clientsocket.close()
        
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
        
    def connect(self):
        while True:
            connection, address = self.s.accept()
            _thread.start_new_thread(self.actions, (connection, address))
    def terminate(self):
        self.s.close()
server_socket = Socket(max_connect=10)
server_socket.connect()
server_socket.terminate()
