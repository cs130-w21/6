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

client = create_client()
client.drop_database('mystory')
