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

def pairup(annotation_file,image_dire):
    with open(annotation_file, 'r') as f:
        annotations = json.load(f)
    # Group all captions together having the same image ID.
    image_path_to_caption = collections.defaultdict(list)
    for val in annotations['annotations']:
        caption = f"<start> {val['caption']} <end>"
        image_path = image_dire + 'COCO_train2014_' + '%012d.jpg' % (val['image_id'])
        image_path_to_caption[image_path].append(caption)
    return image_path_to_caption

def select(num_images,image_path_to_caption):
    image_paths = list(image_path_to_caption.keys())
    random.shuffle(image_paths)
    train_image_paths = image_paths[:num_images]

    train_captions  = []
    img_name_vector = []
    for img_path in train_image_paths:
        caption_list = image_path_to_caption[img_path]
        train_captions.extend(caption_list)
        img_name_vector.extend([img_path] * len(caption_list))
    return train_captions, img_name_vector

def resize_imgs(img_name_vector):
    resize_img_name_vector = []
    exists_paths           = {}
    # we resize and save images
    img_id = 0
    for img_path in img_name_vector:
        if img_path not in exists_paths:
            input_img = cv2.imread(img_path)
            resized_img = resize_image(input_img)
            cv2.imwrite(f'./resize_images/{img_id}.jpg',resized_img)
            link_name = f'./resize_images/{img_id}.jpg'
            exists_paths[img_path] = img_id
            img_id += 1
        else:
            link_name = f'./resize_images/{exists_paths[img_path]}.jpg'
        resize_img_name_vector.append(link_name)
    
    return resize_img_name_vector
