import tensorflow as tf
import numpy as np
import os
import collections
import random
import json
from PIL import Image
from sklearn.model_selection import train_test_split
from image_processing import resize_image
import cv2
from dataset_model import *

def create_dataset(annotation_file,image_dire,num_select=20000,ratio=0.8):
    img_path_to_caption = pairup(annotation_file,image_dire)
    train_captions,img_name_vector = select(num_select,img_path_to_caption)
    # resized images are saved in ./resize_images/
    resize_img_name_vector = resize_imgs(img_name_vector)
    # we save the train_captions and resize_img_name_vector
    np.save('./captions',np.array(train_captions))
    np.save('./img_links',np.array(resize_img_name_vector))
    return train_captions,resize_img_name_vector
    
