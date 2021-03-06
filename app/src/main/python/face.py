# -*- coding: utf-8 -*-
"""
Created on Mon Nov  2 12:15:05 2020

@author: Abhishek
"""

import numpy as np
import cv2 
from PIL import Image
import base64
import io
import face_recognition

def main(data):
    decoded_data = base64.b64encode(data)
    np_data = np.fromstring(decoded_data, np.uint8)
    img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)
    
    img_rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    img_gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    
    face_locations = face_recognition.face_locations(img_gray)
    for (top,right,bottom,left) in face_locations:
        cv2.rectangle(img_rgb, (left,top), (right,bottom), (0,0,255), 8)
    
    pil_im = Image.fromarray(img_rgb)

    global i;  i = len(face_locations)

    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64decode(buff.getvalue())
    return ""+str(img_str,'utf-8')

def num():
    return i
