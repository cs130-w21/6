#!/bin/sh
sudo docker run -p 8501:8501 \
       --mount type=bind,source="/home/ronny/MyStory/tensorflow/inception_server/",target=/models/inception_model -e MODEL_NAME=inception_model -t tensorflow/serving & \
sudo docker run -p 8502:8501 \
       --mount type=bind,source="/home/ronny/MyStory/tensorflow/autoencoder_server/",target=/models/autoencoder_model -e MODEL_NAME=autoencoder_model -t tensorflow/serving


