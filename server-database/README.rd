Backend of MyStory:
Methods:
* server.py: server code that implement python server middleware that
  connects UI with tensorflow server and database
   
* server_call_tensorflow.py: part of code that calls tensorflow server
  which generates captions given images

  - image_processing.py: pre-process the images. ex. resize to square

* run_server.sh: shell file to launch Tensorflow servers and mongoDB.
  create two dockers for Tensorflow servers and start mongoDB.
  

* mongo_db.py, confirm.py, delete.py, load.py, login.py, register.py, delete_user.py:
  code that manipulate the mongoDB:
    - mongo_db.py: basic initializations (create client, databases, etc.)
    - confirm.py: confirm a story to be added to the database
    - delete.py: remove a story from the database
    - load.py: load all stories belong to a user
    - login.py: check user login
    - register.py: check user registration
    - delete_user.py: remove a user from the database

* client.py: A Python client class for unit testing. Note all methods
  are mapped to Java Version in the real Mobile app

Singletons and Others:
* tokenizer.npy: a word tokenizer generated from Tensorflow training process
* help.py: a dummy file for special database manipulations (ex. force reset)
* autoencoder_server, inception_server: Tensorflow server models saved
  during training

Testing and Logging:
* server.log: log the activity of server access
* test_server.py: PyTest file that sanity checks all methods (with a
  Python client.py file)
