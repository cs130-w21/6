#!/bin/sh
sudo docker run -p 8501:8501 \
       --mount type=bind,source="/home/gerry/Documents/cs130/6/tensorflow_server/inception_server/",target=/models/inception_model -e MODEL_NAME=inception_model -t tensorflow/serving & \
sudo docker run -p 8502:8501 \
       --mount type=bind,source="/home/gerry/Documents/cs130/6/tensorflow_server/autoencoder_server/",target=/models/autoencoder_model -e MODEL_NAME=autoencoder_model -t tensorflow/serving


