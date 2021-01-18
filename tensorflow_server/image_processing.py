import numpy as np
from PIL import Image
import cv2

def resize_image(in_img,shape=(299,299)):
    img = cv2.resize(in_img, shape)
    return img

