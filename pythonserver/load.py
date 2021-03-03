import pymongo

"""
TODO:
INPUTS:
   username: string
   tb: database that contains all stories
   valid: set to false if error occurs
OUTPUTS:
   stories: a list of [row_id, image_path, quote] of 
       the user. Note we only return entries that have
       'valid'=1 in the collection table.

entry keys in tb:
   'row_id', 'user_id', 'image_dir','quote','datetime','valid'
"""
def run_load(username,tb,valid):
    pass
