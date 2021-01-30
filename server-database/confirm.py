import pymongo

"""
TODO
INPUTS:
   username:string
   image: PIL Image type
   quote: string
   tb:    table in database that contains all stories
   valid: set to False if error occurs
OUTPUT:
   row_id in tb where the new entry is located

entry keys in tb:
   'row_id', 'user_id', 'image_dir','quote','datetime','valid'
"""

# save the image into a directory './images/'
# only save the directory of the image into database
def run_confirm(username,image,quote,tb,valid):
    pass
